package com.easyjava.core.engine;

import com.easyjava.core.config.GeneratorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 按覆盖策略把生成计划写入磁盘，支持 dry-run。
 */
public final class OutputWriter {

    private static final Logger logger = LoggerFactory.getLogger(OutputWriter.class);

    private static final DateTimeFormatter BACKUP_SUFFIX = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public GenerationSummary write(GenerationPlan plan, boolean dryRun) {
        GenerationSummary summary = new GenerationSummary(dryRun);
        GeneratorConfig.OverwritePolicy policy = plan.getConfig().getOutput().getOverwritePolicy();
        String timestamp = BACKUP_SUFFIX.format(LocalDateTime.now());

        for (GeneratedFile file : plan.getFiles()) {
            Path target = file.getTargetPath();
            boolean exists = Files.exists(target);

            if (exists && policy == GeneratorConfig.OverwritePolicy.SKIP) {
                summary.getSkipped().add(target.toString());
                logger.info("跳过已存在文件: {}", target);
                continue;
            }

            if (exists && policy == GeneratorConfig.OverwritePolicy.BACKUP) {
                Path backup = target.resolveSibling(target.getFileName() + ".bak." + timestamp);
                if (!dryRun) {
                    try {
                        Files.copy(target, backup, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new IllegalStateException("备份文件失败: " + target, e);
                    }
                }
                summary.getBackedUp().add(backup.toString());
                logger.info("{}: {} -> {}", dryRun ? "将备份" : "已备份", target, backup.getFileName());
            }

            if (!dryRun) {
                try {
                    Files.createDirectories(target.getParent());
                    Files.writeString(target, file.getContent(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new IllegalStateException("写入文件失败: " + target, e);
                }
            }

            if (exists) {
                summary.getOverwritten().add(target.toString());
            } else {
                summary.getCreated().add(target.toString());
            }
            logger.info("{}: {}", exists ? "覆盖" : "生成", target);
        }
        return summary;
    }
}
