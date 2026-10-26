package com.easyjava.core.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 表名过滤：include 为空表示全部，exclude 优先级更高，支持 * 与 ? 通配。
 */
public final class TableFilter {

    private final List<Pattern> includePatterns;
    private final List<Pattern> excludePatterns;

    public TableFilter(List<String> include, List<String> exclude) {
        this.includePatterns = compile(include);
        this.excludePatterns = compile(exclude);
    }

    public boolean accept(String tableName) {
        String name = tableName.toUpperCase(Locale.ROOT);
        if (!includePatterns.isEmpty() && includePatterns.stream().noneMatch(p -> p.matcher(name).matches())) {
            return false;
        }
        return excludePatterns.stream().noneMatch(p -> p.matcher(name).matches());
    }

    private static List<Pattern> compile(List<String> wildcards) {
        List<Pattern> patterns = new ArrayList<>();
        if (wildcards == null) {
            return patterns;
        }
        for (String wildcard : wildcards) {
            if (wildcard != null && !wildcard.isBlank()) {
                patterns.add(Pattern.compile(toRegex(wildcard.trim()), Pattern.CASE_INSENSITIVE));
            }
        }
        return patterns;
    }

    static String toRegex(String wildcard) {
        StringBuilder sb = new StringBuilder();
        for (char c : wildcard.toCharArray()) {
            switch (c) {
                case '*' -> sb.append(".*");
                case '?' -> sb.append('.');
                default -> sb.append(Pattern.quote(String.valueOf(c)));
            }
        }
        return sb.toString();
    }
}
