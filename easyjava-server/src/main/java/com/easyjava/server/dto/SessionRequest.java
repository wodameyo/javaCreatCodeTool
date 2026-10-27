package com.easyjava.server.dto;

/**
 * 仅携带会话标识的请求。
 */
public record SessionRequest(String sessionId) {
}
