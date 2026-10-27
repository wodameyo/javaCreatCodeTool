package com.easyjava.server.dto;

import java.util.List;

/**
 * 表清单响应。
 */
public record TableListResponse(String sessionId, List<TableInfo> tables) {
}
