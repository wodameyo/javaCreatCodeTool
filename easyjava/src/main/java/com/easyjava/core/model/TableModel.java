package com.easyjava.core.model;

import com.easyjava.core.naming.NamingStrategy;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;

/**
 * 单表的不可变模型，模板只依赖该模型与配置。
 */
public final class TableModel {

    private final String tableName;
    private final String comment;
    private final String beanName;
    private final List<ColumnModel> columns;
    private final List<IndexModel> indexes;
    private final boolean legacyDate;

    public TableModel(String tableName, String comment, String beanName,
                      List<ColumnModel> columns, List<IndexModel> indexes, boolean legacyDate) {
        this.tableName = tableName;
        this.comment = comment == null ? "" : comment;
        this.beanName = beanName;
        this.columns = List.copyOf(columns);
        this.indexes = List.copyOf(indexes);
        this.legacyDate = legacyDate;
    }

    public String getTableName() {
        return tableName;
    }

    public String getComment() {
        return comment;
    }

    public String getBeanName() {
        return beanName;
    }

    public List<ColumnModel> getColumns() {
        return columns;
    }

    public List<IndexModel> getIndexes() {
        return indexes;
    }

    public boolean isLegacyDate() {
        return legacyDate;
    }

    public String getQueryName() {
        return beanName + NamingStrategy.QUERY_SUFFIX;
    }

    public String getMapperName() {
        return beanName + NamingStrategy.MAPPER_SUFFIX;
    }

    public String getMapperBeanName() {
        return NamingStrategy.lowerFirst(getMapperName());
    }

    public String getServiceName() {
        return beanName + NamingStrategy.SERVICE_SUFFIX;
    }

    public String getServiceBeanName() {
        return NamingStrategy.lowerFirst(getServiceName());
    }

    public String getServiceImplName() {
        return beanName + NamingStrategy.SERVICE_IMPL_SUFFIX;
    }

    public String getControllerName() {
        return beanName + NamingStrategy.CONTROLLER_SUFFIX;
    }

    public String getRequestMapping() {
        return NamingStrategy.lowerFirst(beanName);
    }

    public IndexModel getPrimaryIndex() {
        for (IndexModel index : indexes) {
            if (index.isPrimary()) {
                return index;
            }
        }
        return null;
    }

    public ColumnModel getAutoIncrementColumn() {
        for (ColumnModel column : columns) {
            if (column.isAutoIncrement()) {
                return column;
            }
        }
        return null;
    }

    /**
     * 主键索引的第一列；无主键时返回 null。
     */
    public ColumnModel getPrimaryColumn() {
        IndexModel primary = getPrimaryIndex();
        return primary == null ? null : primary.getFirstColumn();
    }

    /**
     * 前端目录 / 路由使用的短横线模块名，如 user-info。
     */
    public String getModuleName() {
        return beanName.replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase(Locale.ROOT);
    }

    public String getRoutePath() {
        return "/" + getModuleName();
    }

    /**
     * 菜单标题，优先使用表注释。
     */
    public String getMenuTitle() {
        String title = comment.isBlank() ? getModuleName() : comment;
        return title.replace("\r", " ").replace("\n", " ").trim();
    }

    /**
     * 列表页展示字段：隐藏 @JsonIgnore 字段与二进制列。
     */
    public List<ColumnModel> getFrontendColumns() {
        List<ColumnModel> result = new ArrayList<>();
        for (ColumnModel column : columns) {
            if (!column.isJsonIgnore() && !column.isBinaryType()) {
                result.add(column);
            }
        }
        return result;
    }

    /**
     * 新增 / 编辑表单字段：隐藏自增主键、@JsonIgnore 与二进制列。
     */
    public List<ColumnModel> getFormColumns() {
        List<ColumnModel> result = new ArrayList<>();
        for (ColumnModel column : getFrontendColumns()) {
            if (!column.isAutoIncrement()) {
                result.add(column);
            }
        }
        return result;
    }

    /**
     * 搜索区字段：隐藏 @JsonIgnore 与二进制列。
     */
    public List<ColumnModel> getSearchColumns() {
        return getFrontendColumns();
    }

    public List<ColumnModel> getInsertableColumns() {
        List<ColumnModel> result = new ArrayList<>();
        for (ColumnModel column : columns) {
            if (!column.isAutoIncrement()) {
                result.add(column);
            }
        }
        return result;
    }

    public boolean isHaveDate() {
        return columns.stream().anyMatch(ColumnModel::isDateType);
    }

    public boolean isHaveDateTime() {
        return columns.stream().anyMatch(ColumnModel::isDateTimeType);
    }

    public boolean isHaveTemporal() {
        return columns.stream().anyMatch(ColumnModel::isTemporal);
    }

    public boolean isHaveBigDecimal() {
        return columns.stream().anyMatch(column -> "BigDecimal".equals(column.getJavaType()));
    }

    public boolean isHaveBigInteger() {
        return columns.stream().anyMatch(column -> "BigInteger".equals(column.getJavaType()));
    }

    public boolean isHaveJsonIgnore() {
        return columns.stream().anyMatch(ColumnModel::isJsonIgnore);
    }

    /**
     * 生成 Java 源码时需要 import 的完整类名（去重）。
     */
    public List<String> getJavaImports() {
        Set<String> imports = new LinkedHashSet<>();
        for (ColumnModel column : columns) {
            if (column.getJavaImport() != null) {
                imports.add(column.getJavaImport());
            }
        }
        return new ArrayList<>(imports);
    }

    public String getColumnsCsv() {
        StringJoiner joiner = new StringJoiner(",");
        for (ColumnModel column : columns) {
            joiner.add(column.getColumnName());
        }
        return joiner.toString();
    }

    public String getDocComment() {
        return comment.replace("\r", " ").replace("\n", " ").replace("*/", "* /");
    }

    public String getXmlComment() {
        return comment.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("--", "- -")
                .replace("\r", "")
                .replace("\n", " ");
    }
}
