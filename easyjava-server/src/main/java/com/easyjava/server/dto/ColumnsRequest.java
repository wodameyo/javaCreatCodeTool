package com.easyjava.server.dto;

import java.util.List;

/**
 * 查询指定表的列与索引详情。
 */
public record ColumnsRequest(String sessionId, List<String> tables, List<String> ignorePrefix,
                             String dateType) {
}
