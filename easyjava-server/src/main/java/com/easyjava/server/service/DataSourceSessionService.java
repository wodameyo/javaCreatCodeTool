package com.easyjava.server.service;

import com.easyjava.core.config.GeneratorConfig;
import com.easyjava.server.config.ConsoleProperties;
import com.easyjava.server.dto.ConnectionRequest;
import com.easyjava.server.dto.ConnectionTestResponse;
import com.easyjava.server.web.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 测试连接并维护会话级数据源。密码仅保存在内存，响应与日志均脱敏。
 */
@Service
public class DataSourceSessionService {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceSessionService.class);

    private static final Pattern HOST_PATTERN = Pattern.compile("[A-Za-z0-9._-]+");

    private static final Pattern DATABASE_PATTERN = Pattern.compile("[A-Za-z0-9_$-]+");

    private static final Pattern PASSWORD_PARAM = Pattern.compile("(?i)(password=)[^&;]*");

    private final Map<String, DataSourceSession> sessions = new ConcurrentHashMap<>();

    private final ConsoleProperties properties;

    public DataSourceSessionService(ConsoleProperties properties) {
        this.properties = properties;
    }

    public ConnectionTestResponse test(ConnectionRequest request) {
        if (request == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "连接参数不能为空");
        }
        String url = buildUrl(request);
        String username = require(request.username(), "用户名");
        String password = request.password() == null ? "" : request.password();
        String driver = blank(request.driverClassName()) ? "com.mysql.cj.jdbc.Driver"
                : request.driverClassName().trim();

        try {
            Class.forName(driver);
            DriverManager.setLoginTimeout(5);
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                DatabaseMetaData metaData = connection.getMetaData();
                DataSourceSession session = new DataSourceSession(
                        UUID.randomUUID().toString(),
                        new GeneratorConfig.DataSource(driver, url, username, password),
                        maskPassword(url),
                        metaData.getDatabaseProductName(),
                        metaData.getDatabaseProductVersion());
                sessions.put(session.getId(), session);
                prune();
                logger.info("数据源会话已建立: session={}, database={}, user={}",
                        session.getId(), request.database(), username);
                return new ConnectionTestResponse(session.getId(), session.getJdbcUrlDisplay(),
                        session.getProductName(), session.getProductVersion(), ttl().getSeconds());
            }
        } catch (ClassNotFoundException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "数据库驱动不存在: " + driver);
        } catch (SQLException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "连接数据库失败: " + e.getMessage());
        }
    }

    public DataSourceSession require(String sessionId) {
        if (blank(sessionId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "sessionId 不能为空");
        }
        prune();
        DataSourceSession session = sessions.get(sessionId);
        if (session == null || session.isExpired(ttl())) {
            sessions.remove(sessionId);
            throw new ApiException(HttpStatus.NOT_FOUND, "会话不存在或已过期，请重新连接数据库");
        }
        session.touch();
        return session;
    }

    public Duration ttl() {
        return Duration.ofMinutes(Math.max(1, properties.getSessionTtlMinutes()));
    }

    private void prune() {
        sessions.values().removeIf(session -> session.isExpired(ttl()));
    }

    private String buildUrl(ConnectionRequest request) {
        if (!blank(request.url())) {
            return request.url().trim();
        }
        String host = blank(request.host()) ? "localhost" : request.host().trim();
        if (!HOST_PATTERN.matcher(host).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "主机名格式不正确: " + host);
        }
        int port = request.port() == null ? 3306 : request.port();
        if (port < 1 || port > 65535) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "端口号不合法: " + port);
        }
        String database = require(request.database(), "数据库名");
        if (!DATABASE_PATTERN.matcher(database).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "数据库名格式不正确: " + database);
        }
        return "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false"
                + "&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    }

    private static String maskPassword(String url) {
        return PASSWORD_PARAM.matcher(url).replaceAll("$1******");
    }

    private static String require(String value, String field) {
        if (blank(value)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, field + " 不能为空");
        }
        return value.trim();
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
