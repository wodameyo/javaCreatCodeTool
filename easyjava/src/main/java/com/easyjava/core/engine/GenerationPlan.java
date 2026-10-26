package com.easyjava.core.engine;

import com.easyjava.core.config.GeneratorConfig;

import java.util.List;

/**
 * 一次生成的完整结果，先构建计划再决定如何落盘。
 */
public final class GenerationPlan {

    private final List<GeneratedFile> files;
    private final GeneratorConfig config;

    public GenerationPlan(List<GeneratedFile> files, GeneratorConfig config) {
        this.files = List.copyOf(files);
        this.config = config;
    }

    public List<GeneratedFile> getFiles() {
        return files;
    }

    public GeneratorConfig getConfig() {
        return config;
    }

    public int getFileCount() {
        return files.size();
    }
}
