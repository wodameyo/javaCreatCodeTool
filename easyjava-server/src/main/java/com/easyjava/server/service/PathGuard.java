package com.easyjava.server.service;

import com.easyjava.server.config.ConsoleProperties;
import com.easyjava.server.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 限定生成文件的读写范围，防止通过相对路径或 .. 写出允许目录。
 */
@Component
public class PathGuard {

    private final List<Path> allowedRoots;

    public PathGuard(ConsoleProperties properties) {
        List<Path> roots = new ArrayList<>();
        for (String value : properties.getAllowedOutputRoots()) {
            if (value != null && !value.isBlank()) {
                roots.add(resolveAgainstWorkingDir(value));
            }
        }
        if (roots.isEmpty()) {
            roots.add(Paths.get("").toAbsolutePath().normalize());
        }
        this.allowedRoots = List.copyOf(roots);
    }

    public Path resolve(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, field + " 不能为空");
        }
        Path path = resolveAgainstWorkingDir(value);
        ensureAllowed(path, field);
        return path;
    }

    public void ensureAllowed(Path path, String field) {
        Path normalized = path.toAbsolutePath().normalize();
        boolean allowed = allowedRoots.stream().anyMatch(normalized::startsWith);
        if (!allowed) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    field + " 不在允许的输出目录内: " + normalized);
        }
    }

    public List<String> allowedRoots() {
        return allowedRoots.stream().map(Path::toString).toList();
    }

    private static Path resolveAgainstWorkingDir(String value) {
        Path path = Paths.get(value);
        if (!path.isAbsolute()) {
            path = Paths.get("").toAbsolutePath().resolve(path);
        }
        return path.normalize();
    }
}
