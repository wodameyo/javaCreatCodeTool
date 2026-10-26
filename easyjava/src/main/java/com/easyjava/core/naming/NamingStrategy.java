package com.easyjava.core.naming;

import java.util.List;
import java.util.Locale;

/**
 * 表名 / 列名到 Java 命名的转换规则。
 */
public final class NamingStrategy {

    public static final String QUERY_SUFFIX = "Query";
    public static final String MAPPER_SUFFIX = "Mapper";
    public static final String SERVICE_SUFFIX = "Service";
    public static final String SERVICE_IMPL_SUFFIX = "ServiceImpl";
    public static final String CONTROLLER_SUFFIX = "Controller";

    public static final String FUZZY_SUFFIX = "Fuzzy";
    public static final String TIME_START_SUFFIX = "Start";
    public static final String TIME_END_SUFFIX = "End";

    /**
     * snake_case / kebab-case 转为小驼峰属性名。
     */
    public static String toPropertyName(String columnName) {
        return toCamel(columnName, false);
    }

    /**
     * 去掉匹配的表前缀后转为大驼峰 Bean 名。
     */
    public static String toBeanName(String tableName, List<String> ignorePrefixes) {
        String name = tableName;
        String matched = null;
        if (ignorePrefixes != null) {
            for (String prefix : ignorePrefixes) {
                if (prefix != null && !prefix.isBlank() && name.startsWith(prefix)
                        && (matched == null || prefix.length() > matched.length())) {
                    matched = prefix;
                }
            }
        }
        if (matched != null) {
            name = name.substring(matched.length());
        }
        return toCamel(name, true);
    }

    public static String toCamel(String raw, boolean upperFirst) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String[] parts = raw.split("[_\\-\\s]+");
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            String lower = part.toLowerCase(Locale.ROOT);
            if (first) {
                sb.append(upperFirst ? capitalize(lower) : lower);
                first = false;
            } else {
                sb.append(capitalize(lower));
            }
        }
        return sb.toString();
    }

    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public static String lowerFirst(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }
}
