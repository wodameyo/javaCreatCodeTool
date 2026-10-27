package com.easyjava.server.dto;

/**
 * 预览请求：使用会话中的连接参数 + 选中的表 + 表单配置。
 */
public record PreviewRequest(String sessionId, java.util.List<String> tables,
                             GeneratorConfigRequest config) {
}
