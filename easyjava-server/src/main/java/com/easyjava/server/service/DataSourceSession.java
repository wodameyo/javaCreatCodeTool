package com.easyjava.server.service;

import com.easyjava.core.config.GeneratorConfig;

import java.time.Duration;
import java.time.Instant;

/**
 * 一次浏览器连接对应的数据源会话，连接参数只保留在内存中。
 */
public final class DataSourceSession {

    private final String id;
    private final GeneratorConfig.DataSource datasource;
    private final String jdbcUrlDisplay;
    private final String productName;
    private final String productVersion;
    private final Instant createdAt;
    private volatile Instant lastAccessAt;

    DataSourceSession(String id, GeneratorConfig.DataSource datasource, String jdbcUrlDisplay,
                      String productName, String productVersion) {
        this.id = id;
        this.datasource = datasource;
        this.jdbcUrlDisplay = jdbcUrlDisplay;
        this.productName = productName;
        this.productVersion = productVersion;
        this.createdAt = Instant.now();
        this.lastAccessAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public GeneratorConfig.DataSource getDatasource() {
        return datasource;
    }

    public String getJdbcUrlDisplay() {
        return jdbcUrlDisplay;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void touch() {
        this.lastAccessAt = Instant.now();
    }

    boolean isExpired(Duration ttl) {
        return lastAccessAt.plus(ttl).isBefore(Instant.now());
    }
}
