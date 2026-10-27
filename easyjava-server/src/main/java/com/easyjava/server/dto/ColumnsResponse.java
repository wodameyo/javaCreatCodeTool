package com.easyjava.server.dto;

import java.util.List;

/**
 * 多表列详情响应。
 */
public record ColumnsResponse(List<TableColumns> tables) {
}
