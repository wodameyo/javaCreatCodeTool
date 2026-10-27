package com.easyjava.server.dto;

import java.util.List;

/**
 * 预览响应，包含全部待生成文件。
 */
public record PreviewResponse(List<FilePreview> files, int total, int existingCount,
                              String overwritePolicy) {
}
