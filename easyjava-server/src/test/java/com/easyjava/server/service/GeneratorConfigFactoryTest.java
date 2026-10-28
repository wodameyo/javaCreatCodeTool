package com.easyjava.server.service;

import com.easyjava.core.config.GeneratorConfig;
import com.easyjava.server.config.ConsoleProperties;
import com.easyjava.server.dto.GeneratorConfigRequest;
import com.easyjava.server.web.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorConfigFactoryTest {

    @TempDir
    Path tempDir;

    private GeneratorConfigFactory factory;

    private GeneratorConfig.DataSource datasource;

    @BeforeEach
    void setUp() {
        ConsoleProperties properties = new ConsoleProperties();
        properties.setAllowedOutputRoots(List.of(tempDir.toString()));
        factory = new GeneratorConfigFactory(new PathGuard(properties));
        datasource = new GeneratorConfig.DataSource("com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://localhost:3306/test", "root", "secret");
    }

    @Test
    void mapsFormValuesToGeneratorConfig() {
        GeneratorConfigRequest defaults = factory.defaults();
        GeneratorConfigRequest request = new GeneratorConfigRequest(
                "com.example.demo",
                defaults.packages(),
                "tester",
                new GeneratorConfigRequest.Output(tempDir.resolve("java").toString(),
                        tempDir.resolve("resources").toString(),
                        tempDir.resolve("frontend").toString(), "SKIP"),
                new GeneratorConfigRequest.Backend(true, "LOCAL", false, List.of("password")),
                new GeneratorConfigRequest.Table(List.of(), List.of("sys_*"), List.of("t_", "tb_")),
                new GeneratorConfigRequest.Frontend(true, true, false, "pnpm", "/backend", "OVERWRITE"));

        GeneratorConfig config = factory.create(request, datasource, List.of("user_info", "order"));

        assertEquals("com.example.demo.entity.po", config.getPackages().getPo());
        assertEquals(GeneratorConfig.OverwritePolicy.SKIP,
                config.getOutput().getOverwritePolicy());
        assertTrue(config.getBackend().isLombok());
        assertEquals(GeneratorConfig.DateType.LOCAL, config.getBackend().getDateType());
        assertFalse(config.getBackend().isTransaction());
        assertEquals(List.of("user_info", "order"), config.getTable().getInclude());
        assertEquals(List.of("t_", "tb_"), config.getTable().getIgnorePrefix());
        assertEquals("tester", config.getAuthor());
        assertTrue(config.getFrontend().isEnabled());
        assertEquals(tempDir.resolve("frontend"), config.getOutput().getFrontendDir());
        assertEquals("pnpm", config.getFrontend().getPackageManager());
        assertEquals("/backend", config.getFrontend().getApiBasePath());
        assertEquals(GeneratorConfig.OverwritePolicy.OVERWRITE,
                config.getFrontend().getOverwritePolicy());
    }

    @Test
    void rejectsOutputOutsideAllowedRoot() {
        GeneratorConfigRequest defaults = factory.defaults();
        GeneratorConfigRequest request = new GeneratorConfigRequest(
                defaults.packageBase(), defaults.packages(), defaults.author(),
                new GeneratorConfigRequest.Output(tempDir.resolveSibling("outside").toString(),
                        tempDir.resolve("resources").toString(), null, "BACKUP"),
                defaults.backend(), defaults.table(),
                new GeneratorConfigRequest.Frontend(false, true, false, "npm", "/api", null));

        assertThrows(ApiException.class,
                () -> factory.create(request, datasource, List.of("user_info")));
    }
}
