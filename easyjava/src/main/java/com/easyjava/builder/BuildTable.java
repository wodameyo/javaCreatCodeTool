package com.easyjava.builder;

import com.easyjava.bean.Constans;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.PropertiesUtils;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class BuildTable {

    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);

    private static final String SQL_SHOW_TABLE_STATUS = "show table status";

    private static final String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";

    private static final String SQL_SHOW_TABLE_INDEX = "show index from %s";

    private BuildTable() {
    }

    public static List<TableInfo> getTable() {
        List<TableInfo> tableInfoList = new ArrayList<>();
        List<String[]> tables = new ArrayList<>();

        try (Connection conn = openConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
                 ResultSet tableResult = ps.executeQuery()) {
                while (tableResult.next()) {
                    tables.add(new String[]{tableResult.getString("name"), tableResult.getString("comment")});
                }
            }

            for (String[] table : tables) {
                String tableName = table[0];
                String comment = table[1];
                try {
                    tableInfoList.add(buildTableInfo(conn, tableName, comment));
                } catch (Exception e) {
                    // 单表解析失败不阻断其它表
                    logger.error("解析表失败，已跳过: {}", tableName, e);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("获取表信息失败，请检查数据库连接配置", e);
        }
        return tableInfoList;
    }

    private static Connection openConnection() throws SQLException {
        String driverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String username = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("数据库驱动不存在: " + driverName, e);
        }
        return DriverManager.getConnection(url, username, password);
    }

    private static TableInfo buildTableInfo(Connection conn, String tableName, String comment) throws SQLException {
        String beanName = tableName;
        if (Constans.IGNORE_TABLE_PREFIX) {
            beanName = tableName.substring(beanName.indexOf('_') + 1);
        }
        beanName = processField(beanName, true);

        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        tableInfo.setBeanName(beanName);
        tableInfo.setComment(comment);
        tableInfo.setBeanParamName(beanName + Constans.SUFFIX_BAEN_QUERY);

        readFieldInfo(conn, tableInfo);
        getKeyIndexInfo(conn, tableInfo);
        return tableInfo;
    }

    private static void readFieldInfo(Connection conn, TableInfo tableInfo) throws SQLException {
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        List<FieldInfo> extendFieldList = new ArrayList<>();

        boolean haveDateTime = false;
        boolean haveDate = false;
        boolean haveBigDecimal = false;

        String sql = String.format(SQL_SHOW_TABLE_FIELDS, quoteIdentifier(tableInfo.getTableName()));
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet fieldResult = ps.executeQuery()) {
            while (fieldResult.next()) {
                String field = fieldResult.getString("field");
                String rawType = fieldResult.getString("type");
                String type = normalizeSqlType(rawType);
                String comment = fieldResult.getString("comment");
                String extra = fieldResult.getString("extra");
                String propertyName = processField(field, false);

                FieldInfo fieldInfo = new FieldInfo();
                fieldInfoList.add(fieldInfo);

                fieldInfo.setFieldName(field);
                fieldInfo.setComment(comment);
                fieldInfo.setSqlType(type);
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra));
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(processJavaType(rawType));

                if (ArrayUtils.contains(Constans.SQL_DATE_TIME_TYPES, type)) {
                    haveDateTime = true;
                }
                if (ArrayUtils.contains(Constans.SQL_DATE_TYPES, type)) {
                    haveDate = true;
                }
                if (ArrayUtils.contains(Constans.SQL_DECIMAL_TYPES, type)) {
                    haveBigDecimal = true;
                }

                if (ArrayUtils.contains(Constans.SQL_STRING_TYPES, type)) {
                    FieldInfo fuzzyField = new FieldInfo();
                    fuzzyField.setJavaType(fieldInfo.getJavaType());
                    fuzzyField.setPropertyName(propertyName + Constans.SUFFIX_BAEN_QUERY_FUZZY);
                    fuzzyField.setFieldName(fieldInfo.getFieldName());
                    fuzzyField.setSqlType(type);
                    extendFieldList.add(fuzzyField);
                }

                if (ArrayUtils.contains(Constans.SQL_DATE_TIME_TYPES, type)
                        || ArrayUtils.contains(Constans.SQL_DATE_TYPES, type)) {
                    FieldInfo timeStartField = new FieldInfo();
                    timeStartField.setJavaType("String");
                    timeStartField.setPropertyName(propertyName + Constans.SUFFIX_BAEN_QUERY_TIME_START);
                    timeStartField.setFieldName(fieldInfo.getFieldName());
                    timeStartField.setSqlType(type);
                    extendFieldList.add(timeStartField);

                    FieldInfo timeEndField = new FieldInfo();
                    timeEndField.setJavaType("String");
                    timeEndField.setPropertyName(propertyName + Constans.SUFFIX_BAEN_QUERY_TIME_END);
                    timeEndField.setFieldName(fieldInfo.getFieldName());
                    timeEndField.setSqlType(type);
                    extendFieldList.add(timeEndField);
                }
            }
        }

        tableInfo.setHaveDateTime(haveDateTime);
        tableInfo.setHaveDate(haveDate);
        tableInfo.setHaveBigDecimal(haveBigDecimal);
        tableInfo.setFieldList(fieldInfoList);
        tableInfo.setExtendFieldList(extendFieldList);
    }

    private static void getKeyIndexInfo(Connection conn, TableInfo tableInfo) throws SQLException {
        Map<String, FieldInfo> fieldMap = new LinkedHashMap<>();
        for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
            fieldMap.put(fieldInfo.getFieldName(), fieldInfo);
        }

        // keyName -> (seqInIndex -> field)，保证复合索引列顺序正确
        Map<String, TreeMap<Integer, FieldInfo>> indexMap = new LinkedHashMap<>();
        String sql = String.format(SQL_SHOW_TABLE_INDEX, quoteIdentifier(tableInfo.getTableName()));
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet indexResult = ps.executeQuery()) {
            while (indexResult.next()) {
                if (indexResult.getInt("non_unique") == 1) {
                    continue;
                }
                String keyName = indexResult.getString("key_name");
                int seqInIndex = indexResult.getInt("seq_in_index");
                FieldInfo fieldInfo = fieldMap.get(indexResult.getString("column_name"));
                if (fieldInfo == null) {
                    continue;
                }
                indexMap.computeIfAbsent(keyName, k -> new TreeMap<>()).put(seqInIndex, fieldInfo);
            }
        }

        for (Map.Entry<String, TreeMap<Integer, FieldInfo>> entry : indexMap.entrySet()) {
            tableInfo.getKeyIndexMap().put(entry.getKey(), new ArrayList<>(entry.getValue().values()));
        }
    }

    private static String quoteIdentifier(String name) {
        return "`" + name.replace("`", "``") + "`";
    }

    /**
     * 去掉 unsigned/zerofill 与括号中的长度信息，得到基础类型名
     */
    static String normalizeSqlType(String rawType) {
        if (rawType == null) {
            return "";
        }
        String type = rawType.toLowerCase(Locale.ROOT).trim();
        int bracketIndex = type.indexOf('(');
        if (bracketIndex > 0) {
            type = type.substring(0, bracketIndex);
        }
        type = type.replace(" unsigned", "").replace(" zerofill", "").trim();
        return type;
    }

    static String processField(String field, boolean upperCaseFirstLetter) {
        StringBuilder sb = new StringBuilder();
        String[] fields = field.split("_");
        sb.append(upperCaseFirstLetter ? StringUtils.uperCaseFirstLetter(fields[0]) : fields[0]);
        for (int i = 1, len = fields.length; i < len; i++) {
            sb.append(StringUtils.uperCaseFirstLetter(fields[i]));
        }
        return sb.toString();
    }

    /**
     * 处理 Java 类型；无法识别的类型回退为 String，不中断整表解析
     */
    static String processJavaType(String rawSqlType) {
        String type = normalizeSqlType(rawSqlType);
        String normalizedRawType = rawSqlType == null ? "" : rawSqlType.toLowerCase(Locale.ROOT).replace(" ", "");

        if ("tinyint".equals(type) && normalizedRawType.startsWith("tinyint(1)")) {
            return "Boolean";
        }
        if ("bigint".equals(type) && normalizedRawType.contains("unsigned")) {
            return "BigInteger";
        }
        if (ArrayUtils.contains(Constans.SQL_BOOLEAN_TYPES, type)) {
            return "Boolean";
        }
        if (ArrayUtils.contains(Constans.SQL_INTEGER_TYPES, type)
                || ArrayUtils.contains(Constans.SQL_YEAR_TYPES, type)) {
            return "Integer";
        }
        if (ArrayUtils.contains(Constans.SQL_LONG_TYPES, type)) {
            return "Long";
        }
        if (ArrayUtils.contains(Constans.SQL_STRING_TYPES, type)) {
            return "String";
        }
        if (ArrayUtils.contains(Constans.SQL_DECIMAL_TYPES, type)) {
            return "BigDecimal";
        }
        if (ArrayUtils.contains(Constans.SQL_DATE_TYPES, type)
                || ArrayUtils.contains(Constans.SQL_DATE_TIME_TYPES, type)
                || ArrayUtils.contains(Constans.SQL_TIME_TYPES, type)) {
            return "Date";
        }
        if (ArrayUtils.contains(Constans.SQL_BINARY_TYPES, type)) {
            return "byte[]";
        }
        logger.warn("未识别的字段类型: {}，已回退为 String", rawSqlType);
        return "String";
    }
}
