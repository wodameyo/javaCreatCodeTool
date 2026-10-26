package com.easyjava.core.engine;

import com.easyjava.core.TestModels;
import com.easyjava.core.config.GeneratorConfig;
import com.easyjava.core.template.TemplateEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * 全量模板黄金文件测试。首次生成或确认模板变更后，可用 -Dgolden.update=true 刷新基线。
 */
class GoldenFileTest {

    private static final String FIXED_TIMESTAMP = "2026-01-01 00:00:00";

    @TempDir
    Path tempDir;

    @Test
    void matchesAllGoldenFiles() throws IOException {
        GeneratorConfig config = TestModels.config(tempDir.resolve("java"), tempDir.resolve("resources"));
        GenerationPlan plan = new CodeGenerator(config, new TemplateEngine(), FIXED_TIMESTAMP)
                .generate(List.of(TestModels.userInfo()));

        Path goldenRoot = Paths.get("src/test/resources/golden");
        boolean update = Boolean.getBoolean("golden.update");

        for (GeneratedFile file : plan.getFiles()) {
            Path golden = goldenRoot.resolve(file.getLogicalPath());
            if (!Files.exists(golden)) {
                if (update) {
                    Files.createDirectories(golden.getParent());
                    Files.writeString(golden, file.getContent(), StandardCharsets.UTF_8);
                    continue;
                }
                fail("缺少黄金文件: " + golden);
            }
            assertEquals(Files.readString(golden, StandardCharsets.UTF_8), file.getContent(),
                    "模板输出与黄金文件不一致: " + file.getLogicalPath());
        }
    }
}
