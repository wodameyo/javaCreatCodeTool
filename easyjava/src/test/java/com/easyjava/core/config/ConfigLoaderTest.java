package com.easyjava.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsYamlWithPlaceholdersAndDefaults() throws IOException {
        Path config = writeConfig("""
                datasource:
                  url: jdbc:mysql://localhost:3306/test
                  username: root
                  password: ${EASYJAVA_TEST_PASSWORD:default-pwd}
                output:
                  backend-dir: %s
                  resources-dir: %s
                generator:
                  author: tester
                  package-base: com.demo
                  table:
                    ignore-prefix: [ts_]
                """.formatted(tempDir.resolve("java"), tempDir.resolve("resources")));

        GeneratorConfig loaded = ConfigLoader.load(config);

        assertEquals("default-pwd", loaded.getDatasource().getPassword());
        assertEquals("com.mysql.cj.jdbc.Driver", loaded.getDatasource().getDriverClassName());
        assertEquals("tester", loaded.getAuthor());
        assertEquals("com.demo.entity.po", loaded.getPackages().getPo());
        assertEquals("com.demo.service.impl", loaded.getPackages().getServiceImpl());
        assertEquals(GeneratorConfig.OverwritePolicy.BACKUP, loaded.getOutput().getOverwritePolicy());
        assertEquals(GeneratorConfig.DateType.LEGACY, loaded.getBackend().getDateType());
        assertTrue(loaded.getBackend().isTransaction());
        assertFalse(loaded.getFrontend().isEnabled());
        assertEquals(1, loaded.getTable().getIgnorePrefix().size());
    }

    @Test
    void rejectsMissingDatasourceUrl() throws IOException {
        Path config = writeConfig("""
                datasource:
                  username: root
                output:
                  backend-dir: %s
                  resources-dir: %s
                """.formatted(tempDir.resolve("java"), tempDir.resolve("resources")));

        assertThrows(IllegalArgumentException.class, () -> ConfigLoader.load(config));
    }

    @Test
    void loadsEnabledFrontendOptions() throws IOException {
        Path config = writeConfig("""
                datasource:
                  url: jdbc:mysql://localhost:3306/test
                  username: root
                output:
                  backend-dir: %s
                  resources-dir: %s
                  frontend-dir: %s
                frontend:
                  enabled: true
                  typescript: true
                  scaffold-only: true
                  package-manager: pnpm
                  api-base-path: /backend
                  overwrite-policy: OVERWRITE
                """.formatted(tempDir.resolve("java"), tempDir.resolve("resources"),
                tempDir.resolve("frontend")));

        GeneratorConfig.Frontend frontend = ConfigLoader.load(config).getFrontend();

        assertTrue(frontend.isEnabled());
        assertTrue(frontend.isTypescript());
        assertTrue(frontend.isScaffoldOnly());
        assertEquals("pnpm", frontend.getPackageManager());
        assertEquals("/backend", frontend.getApiBasePath());
        assertEquals(GeneratorConfig.OverwritePolicy.OVERWRITE, frontend.getOverwritePolicy());
    }

    @Test
    void rejectsEnabledFrontendWithoutOutputDir() throws IOException {
        Path config = writeConfig("""
                datasource:
                  url: jdbc:mysql://localhost:3306/test
                  username: root
                output:
                  backend-dir: %s
                  resources-dir: %s
                frontend:
                  enabled: true
                """.formatted(tempDir.resolve("java"), tempDir.resolve("resources")));

        assertThrows(IllegalArgumentException.class, () -> ConfigLoader.load(config));
    }

    private Path writeConfig(String content) throws IOException {
        Path file = tempDir.resolve("easyjava.yml");
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }
}
