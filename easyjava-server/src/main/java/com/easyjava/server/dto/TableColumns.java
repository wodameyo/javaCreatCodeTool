package com.easyjava.server.dto;

import java.util.List;

/**
 * 单表的列与索引详情。
 */
public record TableColumns(String tableName, String comment, List<ColumnInfo> columns,
                           List<IndexInfo> indexes) {
}
