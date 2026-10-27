package com.easyjava.server.dto;

/**
 * 生成计划中的单个文件预览。
 *
 * @param existingContent 目标文件已存在时的原内容，可能为 null
 */
public record FilePreview(String logicalPath, String kind, String targetPath, boolean exists,
                          boolean changed, String content, String existingContent) {
}
