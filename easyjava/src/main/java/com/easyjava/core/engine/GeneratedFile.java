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
    private final boolean scaffold;
    private final boolean mergeable;

    public GeneratedFile(String logicalPath, Path targetPath, String content, Kind kind) {
        this(logicalPath, targetPath, content, kind, false, false);
    }

    /**
     * @param scaffold  脚手架文件，已存在时默认跳过，避免覆盖用户定制
     * @param mergeable 需要按标记块与现有文件合并的聚合文件（如前端路由表）
     */
    public GeneratedFile(String logicalPath, Path targetPath, String content, Kind kind,
                         boolean scaffold, boolean mergeable) {
        this.logicalPath = logicalPath;
        this.targetPath = targetPath;
        this.content = content;
        this.kind = kind;
        this.scaffold = scaffold;
        this.mergeable = mergeable;
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

    public boolean isScaffold() {
        return scaffold;
    }

    public boolean isMergeable() {
        return mergeable;
    }
}
