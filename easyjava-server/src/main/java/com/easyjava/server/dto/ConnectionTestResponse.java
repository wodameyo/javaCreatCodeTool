package com.easyjava.server.dto;

/**
 * 测试连接结果，jdbcUrl 中的密码参数已脱敏。
 */
public record ConnectionTestResponse(String sessionId, String jdbcUrl, String productName,
                                     String productVersion, long expiresInSeconds) {
}
