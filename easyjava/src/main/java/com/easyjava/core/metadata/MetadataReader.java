package com.easyjava.core.metadata;

import com.easyjava.core.config.GeneratorConfig;
import com.easyjava.core.model.ColumnModel;
import com.easyjava.core.model.IndexModel;
import com.easyjava.core.model.TableModel;
import com.easyjava.core.naming.NamingStrategy;
import com.easyjava.core.type.SqlTypeCategory;
import com.easyjava.core.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 基于 JDBC DatabaseMetaData 的表结构读取器。
 * MySQL 下额外读取 information_schema，补齐列类型（含 unsigned / tinyint(1)）与注释。
 */
public final class MetadataReader {

    private static final Logger logger = LoggerFactory.getLogger(MetadataReader.class);

    private static final String PRIMARY_INDEX = "PRIMARY";

    private final GeneratorConfig config;

    public MetadataReader(GeneratorConfig config) {
        this.config = config;
    }

    public List<TableModel> read() {
        try (Connection connection = openConnection()) {
            String catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            boolean mysql = isMysql(metaData.getDatabaseProductName());

            Map<String, String> tableComments = mysql ? readTableComments(connection, catalog) : Map.of();
            Map<String, Map<String, ColumnExtra>> columnExtras = mysql
                    ? readColumnExtras(connection, catalog) : Map.of();

            TableFilter filter = new TableFilter(config.getTable().getInclude(), config.getTable().getExclude());
            List<TableModel> tables = new ArrayList<>();

            try (ResultSet rs = metaData.getTables(catalog, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if (!filter.accept(tableName)) {
                        logger.info("跳过表: {}", tableName);
                        continue;
                    }
                    String remarks = rs.getString("REMARKS");
                    String comment = tableComments.getOrDefault(tableName, remarks);
                    try {
                        tables.add(readTable(metaData, catalog, tableName, comment, columnExtras.get(tableName)));
                        logger.info("已解析表: {}", tableName);
                    } catch (SQLException e) {
                        logger.error("解析表失败，已跳过: {}", tableName, e);
                    }
                }
            }

            tables.sort(Comparator.comparing(TableModel::getTableName));
            return tables;
        } catch (SQLException e) {
            throw new IllegalStateException("读取数据库元数据失败，请检查 datasource 配置", e);
        }
    }

    private Connection openConnection() throws SQLException {
        GeneratorConfig.DataSource ds = config.getDatasource();
        if (ds.getDriverClassName() != null && !ds.getDriverClassName().isBlank()) {
            try {
                Class.forName(ds.getDriverClassName());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("数据库驱动不存在: " + ds.getDriverClassName(), e);
            }
        }
        return DriverManager.getConnection(ds.getUrl(), ds.getUsername(), ds.getPassword());
    }

    private TableModel readTable(DatabaseMetaData metaData, String catalog, String tableName,
                                 String tableComment, Map<String, ColumnExtra> extras) throws SQLException {
        List<RawColumn> rawColumns = readColumns(metaData, catalog, tableName, extras);

        Map<String, TreeMap<Integer, String>> indexColumns = readIndexColumns(metaData, catalog, tableName);
        Set<String> primaryColumns = new LinkedHashSet<>();
        Set<String> keyColumns = new LinkedHashSet<>();
        for (Map.Entry<String, TreeMap<Integer, String>> entry : indexColumns.entrySet()) {
            List<String> columns = new ArrayList<>(entry.getValue().values());
            keyColumns.addAll(columns);
            if (PRIMARY_INDEX.equalsIgnoreCase(entry.getKey())) {
                primaryColumns.addAll(columns);
            }
        }

        String beanName = NamingStrategy.toBeanName(tableName, config.getTable().getIgnorePrefix());
        GeneratorConfig.DateType dateType = config.getBackend().getDateType();
        boolean legacyDate = dateType == GeneratorConfig.DateType.LEGACY;

        Map<String, ColumnModel> columnByName = new LinkedHashMap<>();
        List<ColumnModel> columns = new ArrayList<>();
        for (RawColumn raw : rawColumns) {
            String sqlType = TypeMapper.normalize(raw.typeName);
            SqlTypeCategory category = TypeMapper.categorize(raw.typeName);
            String propertyName = NamingStrategy.toPropertyName(raw.name);
            boolean jsonIgnore = isJsonIgnored(raw.name, propertyName);
            ColumnModel column = new ColumnModel(
                    raw.name,
                    propertyName,
                    TypeMapper.javaType(raw.typeName, dateType),
                    TypeMapper.javaImport(raw.typeName, dateType),
                    sqlType,
                    raw.typeName,
                    raw.comment,
                    raw.autoIncrement,
                    primaryColumns.contains(raw.name),
                    keyColumns.contains(raw.name),
                    raw.nullable,
                    jsonIgnore,
                    category,
                    legacyDate
            );
            columns.add(column);
            columnByName.put(raw.name, column);
        }

        List<IndexModel> indexes = new ArrayList<>();
        for (Map.Entry<String, TreeMap<Integer, String>> entry : indexColumns.entrySet()) {
            List<ColumnModel> indexColumnModels = new ArrayList<>();
            for (String columnName : entry.getValue().values()) {
                ColumnModel column = columnByName.get(columnName);
                if (column != null) {
                    indexColumnModels.add(column);
                }
            }
            if (!indexColumnModels.isEmpty()) {
                indexes.add(new IndexModel(entry.getKey(),
                        PRIMARY_INDEX.equalsIgnoreCase(entry.getKey()), indexColumnModels));
            }
        }
        indexes.sort(Comparator.comparing(IndexModel::isPrimary).reversed()
                .thenComparing(IndexModel::getName, String.CASE_INSENSITIVE_ORDER));

        String comment = tableComment == null || tableComment.isBlank()
                ? "" : tableComment;
        return new TableModel(tableName, comment, beanName, columns, indexes, legacyDate);
    }

    private List<RawColumn> readColumns(DatabaseMetaData metaData, String catalog, String tableName,
                                        Map<String, ColumnExtra> extras) throws SQLException {
        Map<Integer, RawColumn> ordered = new TreeMap<>();
        try (ResultSet rs = metaData.getColumns(catalog, null, tableName, "%")) {
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String typeName = rs.getString("TYPE_NAME");
                String comment = rs.getString("REMARKS");

                ColumnExtra extra = extras == null ? null : extras.get(columnName);
                if (extra != null) {
                    if (extra.columnType != null && !extra.columnType.isBlank()) {
                        typeName = extra.columnType;
                    }
                    if (extra.comment != null && !extra.comment.isBlank()) {
                        comment = extra.comment;
                    }
                }

                RawColumn raw = new RawColumn();
                raw.name = columnName;
                raw.typeName = typeName;
                raw.comment = comment == null ? "" : comment;
                raw.nullable = rs.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls;
                raw.autoIncrement = "YES".equalsIgnoreCase(rs.getString("IS_AUTOINCREMENT"));
                int ordinal = rs.getInt("ORDINAL_POSITION");
                ordered.put(ordinal <= 0 ? ordered.size() + 1 : ordinal, raw);
            }
        }
        return new ArrayList<>(ordered.values());
    }

    private Map<String, TreeMap<Integer, String>> readIndexColumns(DatabaseMetaData metaData,
                                                                   String catalog, String tableName) throws SQLException {
        Map<String, TreeMap<Integer, String>> result = new LinkedHashMap<>();
        try (ResultSet rs = metaData.getIndexInfo(catalog, null, tableName, true, false)) {
            while (rs.next()) {
                String indexName = rs.getString("INDEX_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                if (indexName == null || columnName == null) {
                    continue;
                }
                short position = rs.getShort("ORDINAL_POSITION");
                result.computeIfAbsent(indexName, k -> new TreeMap<>()).put((int) position, columnName);
            }
        }
        return result;
    }

    private boolean isJsonIgnored(String columnName, String propertyName) {
        for (String field : config.getBackend().getIgnoreJsonFields()) {
            if (field.equalsIgnoreCase(columnName) || field.equalsIgnoreCase(propertyName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMysql(String productName) {
        if (productName == null) {
            return false;
        }
        String lower = productName.toLowerCase(Locale.ROOT);
        return lower.contains("mysql") || lower.contains("mariadb");
    }

    private Map<String, String> readTableComments(Connection connection, String catalog) {
        Map<String, String> result = new HashMap<>();
        String sql = "SELECT TABLE_NAME, TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, catalog);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("TABLE_NAME"), rs.getString("TABLE_COMMENT"));
                }
            }
        } catch (SQLException e) {
            logger.warn("读取表注释失败，将使用驱动返回值: {}", e.getMessage());
        }
        return result;
    }

    private Map<String, Map<String, ColumnExtra>> readColumnExtras(Connection connection, String catalog) {
        Map<String, Map<String, ColumnExtra>> result = new HashMap<>();
        String sql = "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT "
                + "FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, catalog);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ColumnExtra extra = new ColumnExtra();
                    extra.columnType = rs.getString("COLUMN_TYPE");
                    extra.comment = rs.getString("COLUMN_COMMENT");
                    result.computeIfAbsent(rs.getString("TABLE_NAME"), k -> new HashMap<>())
                            .put(rs.getString("COLUMN_NAME"), extra);
                }
            }
        } catch (SQLException e) {
            logger.warn("读取列类型/注释失败，将使用驱动返回值: {}", e.getMessage());
        }
        return result;
    }

    private static final class RawColumn {
        private String name;
        private String typeName;
        private String comment;
        private boolean nullable;
        private boolean autoIncrement;
    }

    private static final class ColumnExtra {
        private String columnType;
        private String comment;
    }
}
