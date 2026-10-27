package com.easyjava.server.web;

import java.time.Instant;

/**
 * 统一错误响应。
 */
public record ApiError(Instant timestamp, int status, String error, String message, String path) {
}
