package com.easyjava.core.engine;

import java.nio.file.Path;

/**
 * 一个待写出的生成文件。
 */
public final class GeneratedFile {

    public enum Kind {
        JAVA, RESOURCES, FRONTEND
    }

    private final String logicalPath;
    private final Path targetPath;
    private final String content;
    private final Kind kind;

    public GeneratedFile(String logicalPath, Path targetPath, String content, Kind kind) {
        this.logicalPath = logicalPath;
        this.targetPath = targetPath;
        this.content = content;
        this.kind = kind;
    }

    public String getLogicalPath() {
        return logicalPath;
    }

    public Path getTargetPath() {
        return targetPath;
    }

    public String getContent() {
        return content;
    }

    public Kind getKind() {
        return kind;
    }
}
