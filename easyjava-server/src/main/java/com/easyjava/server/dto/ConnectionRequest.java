package com.easyjava.server.dto;

/**
 * 控制台提交的连接参数，密码只存在于当前请求与会话内存中。
 */
public record ConnectionRequest(String host, Integer port, String database, String username,
                                String password, String driverClassName, String url) {
}
