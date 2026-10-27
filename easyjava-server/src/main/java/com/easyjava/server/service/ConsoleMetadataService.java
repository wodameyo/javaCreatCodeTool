package com.easyjava.server.service;

import com.easyjava.core.config.GeneratorConfig;
import com.easyjava.core.naming.NamingStrategy;
import com.easyjava.core.type.SqlTypeCategory;
import com.easyjava.core.type.TypeMapper;
import com.easyjava.server.dto.ColumnInfo;
import com.easyjava.server.dto.IndexInfo;
import com.easyjava.server.dto.TableColumns;
import com.easyjava.server.dto.TableInfo;
import com.easyjava.server.web.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 控制台的表/列浏览查询。MySQL 走 information_schema，其他库回退到 DatabaseMetaData。
 */
@Service
public class ConsoleMetadataService {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleMetadataService.class);

    public List<TableInfo> listTables(DataSourceSession session) {
        try (Connection connection = open(session)) {
            List<TableInfo> tables = isMysql(connection)
                    ? mysqlTables(connection) : jdbcTables(connection);
            tables.sort(Comparator.comparing(TableInfo::name, String.CASE_INSENSITIVE_ORDER));
            return tables;
        } catch (SQLException e) {
            logger.error("读取表清单失败", e);
            throw new ApiException(HttpStatus.BAD_REQUEST, "读取表清单失败: " + e.getMessage());
        }
    }

    public List<TableColumns> listColumns(DataSourceSession session, List<String> tables,
                                          GeneratorConfig.DateType dateType) {
        List<String> names = cleanNames(tables);
        if (names.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "请至少选择一张表");
        }
        try (Connection connection = open(session)) {
            return isMysql(connection)
                    ? mysqlColumns(connection, names, dateType)
                    : jdbcColumns(connection, names, dateType);
        } catch (SQLException e) {
            logger.error("读取列信息失败", e);
            throw new ApiException(HttpStatus.BAD_REQUEST, "读取列信息失败: " + e.getMessage());
        }
    }

    private List<TableInfo> mysqlTables(Connection connection) throws SQLException {
        String sql = """
                SELECT t.TABLE_NAME AS table_name,
                       COALESCE(t.TABLE_COMMENT, '') AS table_comment,
                       COALESCE(t.TABLE_ROWS, 0) AS table_rows,
                       (SELECT COUNT(*) FROM information_schema.COLUMNS c
                         WHERE c.TABLE_SCHEMA = t.TABLE_SCHEMA
                           AND c.TABLE_NAME = t.TABLE_NAME) AS column_count
                  FROM information_schema.TABLES t
                 WHERE t.TABLE_SCHEMA = ? AND t.TABLE_TYPE = 'BASE TABLE'
                 ORDER BY t.TABLE_NAME
                """;
        List<TableInfo> tables = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, connection.getCatalog());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tables.add(new TableInfo(
                            rs.getString("table_name"),
                            rs.getString("table_comment"),
                            rs.getLong("table_rows"),
                            rs.getInt("column_count")));
                }
            }
        }
        return tables;
    }

    private List<TableInfo> jdbcTables(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        List<TableInfo> tables = new ArrayList<>();
        try (ResultSet rs = metaData.getTables(connection.getCatalog(), null, "%",
                new String[]{"TABLE"})) {
            while (rs.next()) {
                String name = rs.getString("TABLE_NAME");
                String comment = rs.getString("REMARKS");
                tables.add(new TableInfo(name, comment == null ? "" : comment, 0,
                        countColumns(metaData, connection.getCatalog(), name)));
            }
        }
        return tables;
    }

    private List<TableColumns> mysqlColumns(Connection connection, List<String> names,
                                            GeneratorConfig.DateType dateType) throws SQLException {
        String catalog = connection.getCatalog();
        Map<String, String> comments = mysqlTableComments(connection, catalog, names);

        Map<String, List<ColumnInfo>> columnsByTable = new LinkedHashMap<>();
        for (String name : names) {
            columnsByTable.put(name, new ArrayList<>());
        }

        String placeholders = placeholders(names.size());
        String sql = "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT, IS_NULLABLE,"
                + " COLUMN_KEY, EXTRA FROM information_schema.COLUMNS"
                + " WHERE TABLE_SCHEMA = ? AND TABLE_NAME IN (" + placeholders + ")"
                + " ORDER BY TABLE_NAME, ORDINAL_POSITION";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindNames(ps, catalog, names);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String table = rs.getString("TABLE_NAME");
                    String extra = rs.getString("EXTRA");
                    boolean autoIncrement = extra != null
                            && extra.toLowerCase(Locale.ROOT).contains("auto_increment");
                    ColumnInfo column = columnInfo(
                            rs.getString("COLUMN_NAME"),
                            rs.getString("COLUMN_TYPE"),
                            rs.getString("COLUMN_COMMENT"),
                            "YES".equalsIgnoreCase(rs.getString("IS_NULLABLE")),
                            "PRI".equalsIgnoreCase(rs.getString("COLUMN_KEY")),
                            autoIncrement,
                            dateType);
                    columnsByTable.computeIfAbsent(table, key -> new ArrayList<>()).add(column);
                }
            }
        }

        Map<String, List<IndexInfo>> indexesByTable = mysqlIndexes(connection, catalog, names);
        List<TableColumns> result = new ArrayList<>();
        for (String name : names) {
            result.add(new TableColumns(name, comments.getOrDefault(name, ""),
                    columnsByTable.getOrDefault(name, List.of()),
                    indexesByTable.getOrDefault(name, List.of())));
        }
        return result;
    }

    private Map<String, String> mysqlTableComments(Connection connection, String catalog,
                                                   List<String> names) throws SQLException {
        Map<String, String> comments = new LinkedHashMap<>();
        String sql = "SELECT TABLE_NAME, COALESCE(TABLE_COMMENT, '') FROM information_schema.TABLES"
                + " WHERE TABLE_SCHEMA = ? AND TABLE_NAME IN (" + placeholders(names.size()) + ")";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindNames(ps, catalog, names);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.put(rs.getString(1), rs.getString(2));
                }
            }
        }
        return comments;
    }

    private Map<String, List<IndexInfo>> mysqlIndexes(Connection connection, String catalog,
                                                      List<String> names) throws SQLException {
        Map<String, Map<String, TreeMap<Integer, String>>> raw = new LinkedHashMap<>();
        String sql = "SELECT TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX, COLUMN_NAME"
                + " FROM information_schema.STATISTICS"
                + " WHERE TABLE_SCHEMA = ? AND TABLE_NAME IN (" + placeholders(names.size()) + ")"
                + " ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindNames(ps, catalog, names);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String table = rs.getString("TABLE_NAME");
                    String indexName = rs.getString("INDEX_NAME");
                    String columnName = rs.getString("COLUMN_NAME");
                    if (indexName == null || columnName == null) {
                        continue;
                    }
                    raw.computeIfAbsent(table, key -> new LinkedHashMap<>())
                            .computeIfAbsent(indexName, key -> new TreeMap<>())
                            .put(rs.getInt("SEQ_IN_INDEX"), columnName);
                }
            }
        }
        return convertIndexes(raw, names);
    }

    private List<TableColumns> jdbcColumns(Connection connection, List<String> names,
                                           GeneratorConfig.DateType dateType) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String catalog = connection.getCatalog();
        Map<String, String> comments = new LinkedHashMap<>();
        Map<String, Map<String, TreeMap<Integer, String>>> rawIndexes = new LinkedHashMap<>();
        List<TableColumns> result = new ArrayList<>();

        for (String table : names) {
            Set<String> primaryKeys = new HashSet<>();
            try (ResultSet rs = metaData.getPrimaryKeys(catalog, null, table)) {
                while (rs.next()) {
                    primaryKeys.add(rs.getString("COLUMN_NAME"));
                }
            }

            List<ColumnInfo> columns = new ArrayList<>();
            try (ResultSet rs = metaData.getColumns(catalog, null, table, "%")) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    columns.add(columnInfo(
                            columnName,
                            rs.getString("TYPE_NAME"),
                            rs.getString("REMARKS"),
                            rs.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls,
                            primaryKeys.contains(columnName),
                            "YES".equalsIgnoreCase(rs.getString("IS_AUTOINCREMENT")),
                            dateType));
                }
            }

            try (ResultSet rs = metaData.getIndexInfo(catalog, null, table, true, false)) {
                while (rs.next()) {
                    String indexName = rs.getString("INDEX_NAME");
                    String columnName = rs.getString("COLUMN_NAME");
                    if (indexName == null || columnName == null) {
                        continue;
                    }
                    rawIndexes.computeIfAbsent(table, key -> new LinkedHashMap<>())
                            .computeIfAbsent(indexName, key -> new TreeMap<>())
                            .put((int) rs.getShort("ORDINAL_POSITION"), columnName);
                }
            }

            List<IndexInfo> indexes = convertIndexes(rawIndexes, List.of(table))
                    .getOrDefault(table, List.of());
            result.add(new TableColumns(table, comments.getOrDefault(table, ""), columns, indexes));
        }
        return result;
    }

    private Map<String, List<IndexInfo>> convertIndexes(
            Map<String, Map<String, TreeMap<Integer, String>>> raw, List<String> names) {
        Map<String, List<IndexInfo>> result = new LinkedHashMap<>();
        for (String table : names) {
            List<IndexInfo> indexes = new ArrayList<>();
            for (Map.Entry<String, TreeMap<Integer, String>> entry : raw
                    .getOrDefault(table, Map.of()).entrySet()) {
                indexes.add(new IndexInfo(entry.getKey(),
                        "PRIMARY".equalsIgnoreCase(entry.getKey()),
                        new ArrayList<>(entry.getValue().values())));
            }
            indexes.sort(Comparator.comparing(IndexInfo::primary).reversed()
                    .thenComparing(IndexInfo::name, String.CASE_INSENSITIVE_ORDER));
            result.put(table, indexes);
        }
        return result;
    }

    private ColumnInfo columnInfo(String columnName, String rawType, String comment, boolean nullable,
                                  boolean primaryKey, boolean autoIncrement,
                                  GeneratorConfig.DateType dateType) {
        String propertyName = NamingStrategy.toPropertyName(columnName);
        String fuzzy = TypeMapper.categorize(rawType) == SqlTypeCategory.STRING
                ? propertyName + NamingStrategy.FUZZY_SUFFIX : null;
        return new ColumnInfo(columnName, propertyName, rawType,
                TypeMapper.javaType(rawType, dateType), comment == null ? "" : comment,
                nullable, primaryKey, autoIncrement, fuzzy);
    }

    private int countColumns(DatabaseMetaData metaData, String catalog, String table)
            throws SQLException {
        int count = 0;
        try (ResultSet rs = metaData.getColumns(catalog, null, table, "%")) {
            while (rs.next()) {
                count++;
            }
        }
        return count;
    }

    private Connection open(DataSourceSession session) {
        GeneratorConfig.DataSource datasource = session.getDatasource();
        try {
            Class.forName(datasource.getDriverClassName());
            DriverManager.setLoginTimeout(5);
            return DriverManager.getConnection(datasource.getUrl(), datasource.getUsername(),
                    datasource.getPassword());
        } catch (ClassNotFoundException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "数据库驱动不存在: " + datasource.getDriverClassName());
        } catch (SQLException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "连接数据库失败: " + e.getMessage());
        }
    }

    private static boolean isMysql(Connection connection) throws SQLException {
        String product = connection.getMetaData().getDatabaseProductName();
        return product != null && product.toLowerCase(Locale.ROOT).contains("mysql");
    }

    private static String placeholders(int count) {
        return String.join(",", Collections.nCopies(count, "?"));
    }

    private static void bindNames(PreparedStatement ps, String catalog, List<String> names)
            throws SQLException {
        ps.setString(1, catalog);
        for (int i = 0; i < names.size(); i++) {
            ps.setString(i + 2, names.get(i));
        }
    }

    private static List<String> cleanNames(List<String> names) {
        List<String> result = new ArrayList<>();
        if (names != null) {
            for (String name : names) {
                if (name != null && !name.isBlank() && !result.contains(name)) {
                    result.add(name.trim());
                }
            }
        }
        return result;
    }
}
