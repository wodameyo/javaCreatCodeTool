package com.easyjava.core.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * 落盘结果统计。
 */
public final class GenerationSummary {

    private final boolean dryRun;
    private final List<String> created = new ArrayList<>();
    private final List<String> overwritten = new ArrayList<>();
    private final List<String> backedUp = new ArrayList<>();
    private final List<String> skipped = new ArrayList<>();

    public GenerationSummary(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public List<String> getCreated() {
        return created;
    }

    public List<String> getOverwritten() {
        return overwritten;
    }

    public List<String> getBackedUp() {
        return backedUp;
    }

    public List<String> getSkipped() {
        return skipped;
    }

    public int getTotal() {
        return created.size() + overwritten.size() + skipped.size();
    }

    public String describe() {
        String prefix = dryRun ? "[dry-run] " : "";
        return prefix + "新增 " + created.size()
                + " 个，覆盖 " + overwritten.size()
                + " 个，跳过 " + skipped.size()
                + " 个，备份 " + backedUp.size() + " 个";
    }
}
