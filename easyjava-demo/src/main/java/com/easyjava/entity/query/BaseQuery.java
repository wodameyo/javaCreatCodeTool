package com.easyjava.entity.query;

public class BaseQuery {
    private SimplePage simplePage;
    private Integer pageNo;
    private Integer pageSize;
    private String orderBy;

    public SimplePage getSimplePage() {
        return simplePage;
    }

    public void setSimplePage(SimplePage simplePage) {
        this.simplePage = simplePage;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    /**
     * 仅允许 "column"、"column asc|desc" 及逗号分隔的组合，避免 SQL 注入
     */
    public void setOrderBy(String orderBy) {
        if (orderBy == null || orderBy.isBlank()) {
            this.orderBy = null;
            return;
        }
        String normalized = orderBy.trim();
        if (!normalized.matches("(?i)^[a-z0-9_]+(\\s+(asc|desc))?(\\s*,\\s*[a-z0-9_]+(\\s+(asc|desc))?)*$")) {
            throw new IllegalArgumentException("非法的排序参数: " + orderBy);
        }
        this.orderBy = normalized;
    }
}
