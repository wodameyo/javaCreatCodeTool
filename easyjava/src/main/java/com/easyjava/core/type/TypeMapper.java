package com.easyjava.core.type;

import com.easyjava.core.config.GeneratorConfig;

import java.util.Locale;
import java.util.Set;

/**
 * SQL 类型到 Java 类型的映射，支持 LEGACY(java.util.Date) 与 LOCAL(java.time) 两种日期策略。
 */
public final class TypeMapper {

    private static final Set<String> BOOLEAN_TYPES = Set.of("bit", "bool", "boolean");

    private static final Set<String> INTEGER_TYPES = Set.of("int", "tinyint", "smallint", "mediumint", "year");

    private static final Set<String> STRING_TYPES = Set.of(
            "char", "varchar", "tinytext", "text", "mediumtext", "longtext",
            "json", "enum", "set");

    private static final Set<String> DECIMAL_TYPES = Set.of("decimal", "numeric", "double", "float", "real");

    private static final Set<String> DATE_TIME_TYPES = Set.of("datetime", "timestamp");

    private static final Set<String> BINARY_TYPES = Set.of(
            "binary", "varbinary", "blob", "tinyblob", "mediumblob", "longblob");

    private TypeMapper() {
    }

    /**
     * 去掉 unsigned / zerofill 与括号中的长度信息，得到基础类型名。
     */
    public static String normalize(String rawSqlType) {
        if (rawSqlType == null) {
            return "";
        }
        String type = rawSqlType.toLowerCase(Locale.ROOT).trim();
        int bracketIndex = type.indexOf('(');
        if (bracketIndex > 0) {
            type = type.substring(0, bracketIndex);
        }
        type = type.replace(" unsigned", "").replace(" zerofill", "").trim();
        return type;
    }

    public static SqlTypeCategory categorize(String rawSqlType) {
        String type = normalize(rawSqlType);
        String compact = rawSqlType == null ? "" : rawSqlType.toLowerCase(Locale.ROOT).replace(" ", "");

        if ("tinyint".equals(type) && compact.startsWith("tinyint(1)")) {
            return SqlTypeCategory.BOOLEAN;
        }
        if ("bigint".equals(type) && compact.contains("unsigned")) {
            return SqlTypeCategory.BIG_INTEGER;
        }
        if (BOOLEAN_TYPES.contains(type)) {
            return SqlTypeCategory.BOOLEAN;
        }
        if (INTEGER_TYPES.contains(type)) {
            return SqlTypeCategory.INTEGER;
        }
        if ("bigint".equals(type)) {
            return SqlTypeCategory.LONG;
        }
        if (STRING_TYPES.contains(type)) {
            return SqlTypeCategory.STRING;
        }
        if (DECIMAL_TYPES.contains(type)) {
            return SqlTypeCategory.DECIMAL;
        }
        if (DATE_TIME_TYPES.contains(type)) {
            return SqlTypeCategory.DATE_TIME;
        }
        if ("date".equals(type)) {
            return SqlTypeCategory.DATE;
        }
        if ("time".equals(type)) {
            return SqlTypeCategory.TIME;
        }
        if (BINARY_TYPES.contains(type)) {
            return SqlTypeCategory.BINARY;
        }
        return SqlTypeCategory.OTHER;
    }

    public static String javaType(String rawSqlType, GeneratorConfig.DateType dateType) {
        return switch (categorize(rawSqlType)) {
            case BOOLEAN -> "Boolean";
            case INTEGER -> "Integer";
            case LONG -> "Long";
            case BIG_INTEGER -> "BigInteger";
            case DECIMAL -> "BigDecimal";
            case BINARY -> "byte[]";
            case DATE -> legacy(dateType) ? "Date" : "LocalDate";
            case DATE_TIME -> legacy(dateType) ? "Date" : "LocalDateTime";
            case TIME -> legacy(dateType) ? "Date" : "LocalTime";
            case STRING, OTHER -> "String";
        };
    }

    /**
     * 需要 import 的完整类名；java.lang 与 byte[] 返回 null。
     */
    public static String javaImport(String rawSqlType, GeneratorConfig.DateType dateType) {
        return switch (categorize(rawSqlType)) {
            case BIG_INTEGER -> "java.math.BigInteger";
            case DECIMAL -> "java.math.BigDecimal";
            case DATE -> legacy(dateType) ? "java.util.Date" : "java.time.LocalDate";
            case DATE_TIME -> legacy(dateType) ? "java.util.Date" : "java.time.LocalDateTime";
            case TIME -> legacy(dateType) ? "java.util.Date" : "java.time.LocalTime";
            default -> null;
        };
    }

    private static boolean legacy(GeneratorConfig.DateType dateType) {
        return dateType == null || dateType == GeneratorConfig.DateType.LEGACY;
    }
}
