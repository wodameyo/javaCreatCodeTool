package com.easyjava.core.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 把生成计划打包为 ZIP，便于在不落盘的情况下交付。
 */
public final class ZipExporter {

    private static final Logger logger = LoggerFactory.getLogger(ZipExporter.class);

    public Path export(GenerationPlan plan, Path zipPath) {
        try {
            Files.createDirectories(zipPath.toAbsolutePath().getParent());
            try (OutputStream out = Files.newOutputStream(zipPath);
                 ZipOutputStream zip = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
                for (GeneratedFile file : plan.getFiles()) {
                    ZipEntry entry = new ZipEntry(file.getLogicalPath());
                    zip.putNextEntry(entry);
                    zip.write(file.getContent().getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
            }
            logger.info("已导出 ZIP: {}", zipPath.toAbsolutePath());
            return zipPath.toAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException("导出 ZIP 失败: " + zipPath, e);
        }
    }
}
