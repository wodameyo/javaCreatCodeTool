package com.easyjava.server.service;

import com.easyjava.server.config.ConsoleProperties;
import com.easyjava.server.web.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DownloadTokenServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void tokenIsOneTimeUse() throws Exception {
        Path zip = tempDir.resolve("demo.zip");
        Files.writeString(zip, "zip-content");
        DownloadTokenService service = new DownloadTokenService(new ConsoleProperties());

        String token = service.issue(zip, "demo.zip");
        DownloadTokenService.DownloadEntry entry = service.consume(token);

        assertEquals("demo.zip", entry.fileName());
        assertThrows(ApiException.class, () -> service.consume(token));
    }

    @Test
    void unknownTokenIsRejected() {
        DownloadTokenService service = new DownloadTokenService(new ConsoleProperties());
        assertThrows(ApiException.class, () -> service.consume("missing"));
        assertFalse(Files.exists(tempDir.resolve("demo.zip")));
    }
}
