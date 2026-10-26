package com.easyjava.core.engine;

import com.easyjava.core.TestModels;
import com.easyjava.core.config.GeneratorConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OutputWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void overwritesExistingFile() throws IOException {
        Path target = existingFile();
        GenerationSummary summary = new OutputWriter()
                .write(plan(GeneratorConfig.OverwritePolicy.OVERWRITE), false);

        assertEquals(1, summary.getOverwritten().size());
        assertEquals("new-content", Files.readString(target));
    }

    @Test
    void skipsExistingFile() throws IOException {
        Path target = existingFile();
        GenerationSummary summary = new OutputWriter()
                .write(plan(GeneratorConfig.OverwritePolicy.SKIP), false);

        assertEquals(1, summary.getSkipped().size());
        assertEquals("old-content", Files.readString(target));
    }

    @Test
    void backsUpExistingFile() throws IOException {
        Path target = existingFile();
        GenerationSummary summary = new OutputWriter()
                .write(plan(GeneratorConfig.OverwritePolicy.BACKUP), false);

        assertEquals(1, summary.getBackedUp().size());
        assertEquals("new-content", Files.readString(target));
        Path backup = Path.of(summary.getBackedUp().get(0));
        assertTrue(Files.exists(backup));
        assertEquals("old-content", Files.readString(backup));
    }

    @Test
    void dryRunDoesNotTouchDisk() {
        Path target = tempDir.resolve("out").resolve("A.java");
        GeneratorConfig config = TestModels.config(tempDir.resolve("java"), tempDir.resolve("resources"));
        GenerationPlan plan = new GenerationPlan(List.of(new GeneratedFile(
                "java/A.java", target, "new-content", GeneratedFile.Kind.JAVA)),
                withPolicy(config, GeneratorConfig.OverwritePolicy.BACKUP));

        GenerationSummary summary = new OutputWriter().write(plan, true);

        assertEquals(1, summary.getCreated().size());
        assertTrue(summary.isDryRun());
        assertFalse(Files.exists(target));
    }

    @Test
    void exportsZipWithLogicalPaths() throws IOException {
        Path zipPath = tempDir.resolve("out.zip");
        GeneratorConfig config = TestModels.config(tempDir.resolve("java"), tempDir.resolve("resources"));
        GenerationPlan plan = new GenerationPlan(List.of(
                new GeneratedFile("java/A.java", tempDir.resolve("A.java"), "a", GeneratedFile.Kind.JAVA),
                new GeneratedFile("resources/m/B.xml", tempDir.resolve("B.xml"), "b", GeneratedFile.Kind.RESOURCES)
        ), config);

        new ZipExporter().export(plan, zipPath);

        try (ZipFile zip = new ZipFile(zipPath.toFile())) {
            assertTrue(zip.getEntry("java/A.java") != null);
            assertTrue(zip.getEntry("resources/m/B.xml") != null);
            assertEquals("b", new String(zip.getInputStream(zip.getEntry("resources/m/B.xml"))
                    .readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    private Path existingFile() throws IOException {
        Path target = tempDir.resolve("out").resolve("A.java");
        Files.createDirectories(target.getParent());
        Files.writeString(target, "old-content", StandardCharsets.UTF_8);
        return target;
    }

    private GenerationPlan plan(GeneratorConfig.OverwritePolicy policy) {
        Path target = tempDir.resolve("out").resolve("A.java");
        GeneratorConfig config = withPolicy(
                TestModels.config(tempDir.resolve("java"), tempDir.resolve("resources")), policy);
        return new GenerationPlan(List.of(new GeneratedFile(
                "java/A.java", target, "new-content", GeneratedFile.Kind.JAVA)), config);
    }

    private static GeneratorConfig withPolicy(GeneratorConfig config,
                                              GeneratorConfig.OverwritePolicy policy) {
        GeneratorConfig.Output output = config.getOutput();
        return new GeneratorConfig(config.getDatasource(),
                new GeneratorConfig.Output(output.getBackendDir(), output.getResourcesDir(),
                        output.getFrontendDir(), policy),
                config.getTable(), config.getPackages(), config.getBackend(),
                config.getFrontend(), config.getAuthor());
    }
}
