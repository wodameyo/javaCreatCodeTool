package com.easyjava.server.dto;

import java.util.List;

/**
 * 落盘/预览统计。
 */
public record GenerationSummaryDto(boolean dryRun, List<String> created, List<String> overwritten,
                                   List<String> backedUp, List<String> skipped, int total) {
}
