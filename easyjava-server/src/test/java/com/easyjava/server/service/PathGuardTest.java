package com.easyjava.server.service;

import com.easyjava.server.config.ConsoleProperties;
import com.easyjava.server.web.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathGuardTest {

    @TempDir
    Path tempDir;

    @Test
    void allowsPathsUnderConfiguredRoot() {
        ConsoleProperties properties = new ConsoleProperties();
        properties.setAllowedOutputRoots(List.of(tempDir.toString()));
        PathGuard guard = new PathGuard(properties);

        assertDoesNotThrow(() -> guard.resolve(tempDir.resolve("out/java").toString(), "output"));
        assertEquals(1, guard.allowedRoots().size());
    }

    @Test
    void rejectsPathsOutsideConfiguredRoot() {
        ConsoleProperties properties = new ConsoleProperties();
        properties.setAllowedOutputRoots(List.of(tempDir.resolve("allowed").toString()));
        PathGuard guard = new PathGuard(properties);

        Path outside = tempDir.resolve("other");
        assertThrows(ApiException.class, () -> guard.resolve(outside.toString(), "output"));
        assertThrows(ApiException.class,
                () -> guard.resolve(tempDir.resolve("allowed/../other").toString(), "output"));
    }
}
