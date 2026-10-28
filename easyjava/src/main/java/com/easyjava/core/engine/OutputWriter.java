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
 * 按覆盖策略把生成计划写入磁盘，支持 dry-run、脚手架跳过与标记块增量合并。
 */
public final class OutputWriter {

    private static final Logger logger = LoggerFactory.getLogger(OutputWriter.class);

    private static final DateTimeFormatter BACKUP_SUFFIX = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public GenerationSummary write(GenerationPlan plan, boolean dryRun) {
        GenerationSummary summary = new GenerationSummary(dryRun);
        GeneratorConfig config = plan.getConfig();
        String timestamp = BACKUP_SUFFIX.format(LocalDateTime.now());

        for (GeneratedFile file : plan.getFiles()) {
            Path target = file.getTargetPath();
            boolean exists = Files.exists(target);
            String content = file.getContent();
            GeneratorConfig.OverwritePolicy policy = policyOf(config, file);

            if (exists && file.isMergeable()) {
                String existing = readExisting(target);
                String merged = MarkerMerger.merge(existing, content);
                if (merged.equals(existing)) {
                    summary.getSkipped().add(target.toString());
                    logger.info("合并后无变化，跳过: {}", target);
                    continue;
                }
                content = merged;
                // 合并结果包含新表，不能按 SKIP 丢弃
                if (policy == GeneratorConfig.OverwritePolicy.SKIP) {
                    policy = GeneratorConfig.OverwritePolicy.OVERWRITE;
                }
            }

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
                    Files.writeString(target, content, StandardCharsets.UTF_8);
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

    /**
     * 脚手架文件始终跳过已存在文件；前端业务文件可单独配置策略。
     */
    private static GeneratorConfig.OverwritePolicy policyOf(GeneratorConfig config, GeneratedFile file) {
        if (file.isScaffold()) {
            return GeneratorConfig.OverwritePolicy.SKIP;
        }
        if (file.getKind() == GeneratedFile.Kind.FRONTEND && config.getFrontend() != null
                && config.getFrontend().getOverwritePolicy() != null) {
            return config.getFrontend().getOverwritePolicy();
        }
        return config.getOutput().getOverwritePolicy();
    }

    private static String readExisting(Path target) {
        try {
            return Files.readString(target, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("读取现有文件失败: " + target, e);
        }
    }
}
