package com.easyjava.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PropertiesUtils {

    private static final String CONFIG_FILE = "application.properties";

    /**
     * 支持 ${ENV_NAME:defaultValue} 形式的环境变量占位符
     */
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}:]+)(?::([^}]*))?}");

    private static final Map<String, String> PROPER_MAP = new ConcurrentHashMap<>();

    static {
        Properties props = new Properties();
        try (InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new IllegalStateException("找不到配置文件: " + CONFIG_FILE);
            }
            props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("读取配置文件失败: " + CONFIG_FILE, e);
        }
        for (String name : props.stringPropertyNames()) {
            PROPER_MAP.put(name, resolvePlaceholders(props.getProperty(name)));
        }
    }

    private PropertiesUtils() {
    }

    public static String getString(String key) {
        String value = PROPER_MAP.get(key);
        if (value == null) {
            throw new IllegalStateException("缺少配置项: " + key);
        }
        return value;
    }

    public static String getString(String key, String defaultValue) {
        String value = PROPER_MAP.get(key);
        return value == null ? defaultValue : value;
    }

    private static String resolvePlaceholders(String value) {
        if (value == null || !value.contains("${")) {
            return value;
        }
        Matcher matcher = PLACEHOLDER.matcher(value);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String envName = matcher.group(1).trim();
            String defaultValue = matcher.group(2);
            String replacement = System.getenv(envName);
            if (replacement == null) {
                replacement = defaultValue == null ? "" : defaultValue;
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
