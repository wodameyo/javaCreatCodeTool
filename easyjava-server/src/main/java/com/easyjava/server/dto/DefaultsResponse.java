package com.easyjava.server.dto;

import java.util.List;

/**
 * 控制台默认配置与安全边界。
 */
public record DefaultsResponse(GeneratorConfigRequest config, List<String> allowedOutputRoots,
                               int sessionTtlMinutes, String version, String phase) {
}
