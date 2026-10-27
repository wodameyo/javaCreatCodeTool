package com.easyjava.server.dto;

/**
 * 生成结果，downloadToken 一次性且短时有效。
 */
public record GenerateResponse(String token, String zipFileName, GenerationSummaryDto summary,
                               int fileCount, long elapsedMs) {
}
