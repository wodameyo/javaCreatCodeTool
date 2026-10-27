package com.easyjava.server.service;

import com.easyjava.server.config.ConsoleProperties;
import com.easyjava.server.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ZIP 下载令牌：短时有效、一次性使用，过期文件会被清理。
 */
@Service
public class DownloadTokenService {

    public record DownloadEntry(Path path, String fileName) {
    }

    private record TokenEntry(Path path, String fileName, Instant expiresAt) {
    }

    private final Map<String, TokenEntry> tokens = new ConcurrentHashMap<>();

    private final ConsoleProperties properties;

    public DownloadTokenService(ConsoleProperties properties) {
        this.properties = properties;
    }

    public String issue(Path path, String fileName) {
        prune();
        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenEntry(path, fileName,
                Instant.now().plus(ttl())));
        return token;
    }

    public DownloadEntry consume(String token) {
        if (token == null || token.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "下载 token 不能为空");
        }
        TokenEntry entry = tokens.remove(token);
        if (entry == null || entry.expiresAt().isBefore(Instant.now())) {
            if (entry != null) {
                deleteQuietly(entry.path());
            }
            throw new ApiException(HttpStatus.NOT_FOUND, "下载链接不存在或已失效，请重新生成");
        }
        if (!Files.isRegularFile(entry.path())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "ZIP 文件已被清理，请重新生成");
        }
        return new DownloadEntry(entry.path(), entry.fileName());
    }

    private Duration ttl() {
        return Duration.ofMinutes(Math.max(1, properties.getDownloadTtlMinutes()));
    }

    private void prune() {
        Instant now = Instant.now();
        tokens.entrySet().removeIf(entry -> {
            if (entry.getValue().expiresAt().isBefore(now)) {
                deleteQuietly(entry.getValue().path());
                return true;
            }
            return false;
        });
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // 临时文件清理失败不影响主流程
        }
    }
}
