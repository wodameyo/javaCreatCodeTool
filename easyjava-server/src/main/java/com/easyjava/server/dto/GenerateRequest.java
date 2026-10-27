package com.easyjava.server.dto;

import java.util.List;

/**
 * 生成请求。
 *
 * @param writeToDisk true 时按覆盖策略写入磁盘；false 时仅打包 ZIP
 */
public record GenerateRequest(String sessionId, List<String> tables,
                              GeneratorConfigRequest config, Boolean writeToDisk) {
}
