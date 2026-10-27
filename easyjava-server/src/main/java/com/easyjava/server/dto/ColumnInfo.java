package com.easyjava.server.dto;

/**
 * 列的展示信息。
 */
public record ColumnInfo(String name, String propertyName, String sqlType, String javaType,
                         String comment, boolean nullable, boolean primaryKey,
                         boolean autoIncrement, String fuzzyPropertyName) {
}
