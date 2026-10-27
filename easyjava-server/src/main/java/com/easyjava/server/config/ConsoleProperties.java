package com.easyjava.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制台的安全与生命周期配置。
 */
@ConfigurationProperties(prefix = "easyjava.console")
public class ConsoleProperties {

    private List<String> allowedOutputRoots = new ArrayList<>(List.of("."));

    private int sessionTtlMinutes = 30;

    private int downloadTtlMinutes = 5;

    private String version = "1.0-SNAPSHOT";

    private String phase = "Phase 2";

    public List<String> getAllowedOutputRoots() {
        return allowedOutputRoots;
    }

    public void setAllowedOutputRoots(List<String> allowedOutputRoots) {
        this.allowedOutputRoots = allowedOutputRoots;
    }

    public int getSessionTtlMinutes() {
        return sessionTtlMinutes;
    }

    public void setSessionTtlMinutes(int sessionTtlMinutes) {
        this.sessionTtlMinutes = sessionTtlMinutes;
    }

    public int getDownloadTtlMinutes() {
        return downloadTtlMinutes;
    }

    public void setDownloadTtlMinutes(int downloadTtlMinutes) {
        this.downloadTtlMinutes = downloadTtlMinutes;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }
}
