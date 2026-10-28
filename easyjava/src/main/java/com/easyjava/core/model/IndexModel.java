package com.easyjava.core.model;

import com.easyjava.core.naming.NamingStrategy;

import java.util.List;
import java.util.StringJoiner;

/**
 * 唯一索引模型，用于生成 selectByXxx / updateByXxx / deleteByXxx。
 */
public final class IndexModel {

    private final String name;
    private final boolean primary;
    private final List<ColumnModel> columns;

    public IndexModel(String name, boolean primary, List<ColumnModel> columns) {
        this.name = name;
        this.primary = primary;
        this.columns = List.copyOf(columns);
    }

    public String getName() {
        return name;
    }

    public boolean isPrimary() {
        return primary;
    }

    public List<ColumnModel> getColumns() {
        return columns;
    }

    public boolean isSingleColumn() {
        return columns.size() == 1;
    }

    public ColumnModel getFirstColumn() {
        return columns.isEmpty() ? null : columns.get(0);
    }

    /**
     * 如 UserIdAndStatus。
     */
    public String getMethodSuffix() {
        StringJoiner joiner = new StringJoiner("And");
        for (ColumnModel column : columns) {
            joiner.add(NamingStrategy.capitalize(column.getPropertyName()));
        }
        return joiner.toString();
    }

    /**
     * 如 Integer id, String userName。
     */
    public String getParamDeclaration() {
        StringJoiner joiner = new StringJoiner(", ");
        for (ColumnModel column : columns) {
            joiner.add(column.getJavaType() + " " + column.getPropertyName());
        }
        return joiner.toString();
    }

    /**
     * Controller 方法声明，为每个参数补充 @RequestParam，避免依赖编译参数 -parameters。
     */
    public String getControllerParamDeclaration() {
        StringJoiner joiner = new StringJoiner(", ");
        for (ColumnModel column : columns) {
            joiner.add("@RequestParam(\"" + column.getPropertyName() + "\") "
                    + column.getJavaType() + " " + column.getPropertyName());
        }
        return joiner.toString();
    }

    /**
     * Mapper 方法声明，为每个参数补充 @Param，保证 XML 中按名称取值。
     */
    public String getMapperParamDeclaration() {
        StringJoiner joiner = new StringJoiner(", ");
        for (ColumnModel column : columns) {
            joiner.add("@Param(\"" + column.getPropertyName() + "\") "
                    + column.getJavaType() + " " + column.getPropertyName());
        }
        return joiner.toString();
    }

    /**
     * 如 id, userName。
     */
    public String getParamNames() {
        StringJoiner joiner = new StringJoiner(", ");
        for (ColumnModel column : columns) {
            joiner.add(column.getPropertyName());
        }
        return joiner.toString();
    }

    /**
     * 如 user_name=#{userName} and status=#{status}。
     */
    public String getWhereClause() {
        StringJoiner joiner = new StringJoiner(" and ");
        for (ColumnModel column : columns) {
            joiner.add(column.getColumnName() + "=#{" + column.getPropertyName() + "}");
        }
        return joiner.toString();
    }
}
