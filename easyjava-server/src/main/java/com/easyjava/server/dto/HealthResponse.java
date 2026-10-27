package com.easyjava.server.dto;

import java.time.Instant;

/**
 * 健康检查响应。
 */
public record HealthResponse(String status, String version, String phase, Instant time) {
}
