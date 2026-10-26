package com.easyjava.core.model;

import com.easyjava.core.naming.NamingStrategy;
import com.easyjava.core.type.SqlTypeCategory;

/**
 * 数据库列的不可变模型。
 */
public final class ColumnModel {

    private final String columnName;
    private final String propertyName;
    private final String javaType;
    private final String javaImport;
    private final String sqlType;
    private final String rawSqlType;
    private final String comment;
    private final boolean autoIncrement;
    private final boolean primaryKey;
    private final boolean keyColumn;
    private final boolean nullable;
    private final boolean jsonIgnore;
    private final SqlTypeCategory category;
    private final boolean legacyDate;

    public ColumnModel(String columnName, String propertyName, String javaType, String javaImport,
                       String sqlType, String rawSqlType, String comment,
                       boolean autoIncrement, boolean primaryKey, boolean keyColumn, boolean nullable,
                       boolean jsonIgnore, SqlTypeCategory category, boolean legacyDate) {
        this.columnName = columnName;
        this.propertyName = propertyName;
        this.javaType = javaType;
        this.javaImport = javaImport;
        this.sqlType = sqlType;
        this.rawSqlType = rawSqlType;
        this.comment = comment == null ? "" : comment;
        this.autoIncrement = autoIncrement;
        this.primaryKey = primaryKey;
        this.keyColumn = keyColumn;
        this.nullable = nullable;
        this.jsonIgnore = jsonIgnore;
        this.category = category;
        this.legacyDate = legacyDate;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getUpperPropertyName() {
        return NamingStrategy.capitalize(propertyName);
    }

    public String getJavaType() {
        return javaType;
    }

    public String getJavaImport() {
        return javaImport;
    }

    public String getSqlType() {
        return sqlType;
    }

    public String getRawSqlType() {
        return rawSqlType;
    }

    public String getComment() {
        return comment;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isKeyColumn() {
        return keyColumn;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isJsonIgnore() {
        return jsonIgnore;
    }

    public SqlTypeCategory getCategory() {
        return category;
    }

    public boolean isStringType() {
        return category == SqlTypeCategory.STRING;
    }

    public boolean isDateType() {
        return category == SqlTypeCategory.DATE;
    }

    public boolean isDateTimeType() {
        return category == SqlTypeCategory.DATE_TIME;
    }

    public boolean isTimeType() {
        return category == SqlTypeCategory.TIME;
    }

    public boolean isTemporal() {
        return isDateType() || isDateTimeType() || isTimeType();
    }

    public boolean isDecimalType() {
        return category == SqlTypeCategory.DECIMAL;
    }

    public boolean isBinaryType() {
        return category == SqlTypeCategory.BINARY;
    }

    public boolean isBooleanType() {
        return category == SqlTypeCategory.BOOLEAN;
    }

    /**
     * 模糊查询字段名，仅字符串列有值。
     */
    public String getFuzzyPropertyName() {
        return isStringType() ? propertyName + NamingStrategy.FUZZY_SUFFIX : null;
    }

    public String getTimeStartPropertyName() {
        return isTemporal() ? propertyName + NamingStrategy.TIME_START_SUFFIX : null;
    }

    public String getTimeEndPropertyName() {
        return isTemporal() ? propertyName + NamingStrategy.TIME_END_SUFFIX : null;
    }

    /**
     * 适用于 Javadoc 的注释。
     */
    public String getDocComment() {
        return comment.replace("\r", " ").replace("\n", " ").replace("*/", "* /");
    }

    /**
     * 适用于 XML 注释的注释。
     */
    public String getXmlComment() {
        return comment.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("--", "- -")
                .replace("\r", "")
                .replace("\n", " ");
    }

    /**
     * PO#toString 中对应该列的表达式片段。
     */
    public String getToStringExpression() {
        String display = propertyName;
        if (legacyDate && isDateTimeType()) {
            display = "DateUtils.format(" + propertyName
                    + ", DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())";
        } else if (legacyDate && isDateType()) {
            display = "DateUtils.format(" + propertyName
                    + ", DateTimePatternEnum.YYYY_MM_DD.getPattern())";
        }
        return "(\"" + escapeJava(comment) + ":\" + (" + propertyName
                + " == null ? \"空\" : " + display + "))";
    }

    private static String escapeJava(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
