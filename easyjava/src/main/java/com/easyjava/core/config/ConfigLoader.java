package com.easyjava.core.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 读取 easyjava.yml，解析环境变量占位符并做基础校验。
 */
public final class ConfigLoader {

    private static final String DEFAULT_CONFIG_NAME = "easyjava.yml";

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([A-Za-z0-9_.-]+)(?::([^}]*))?}");

    private static final Pattern PACKAGE_NAME = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)*$");

    private ConfigLoader() {
    }

    /**
     * 加载默认配置：优先当前目录下的 easyjava.yml，其次 classpath 根目录。
     */
    public static GeneratorConfig loadDefault() {
        Path local = Paths.get(DEFAULT_CONFIG_NAME).toAbsolutePath().normalize();
        if (Files.isRegularFile(local)) {
            return load(local);
        }
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_NAME)) {
            if (in == null) {
                throw new IllegalStateException("未找到配置文件 " + DEFAULT_CONFIG_NAME
                        + "，请通过 --config 指定路径");
            }
            return parse(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("读取默认配置失败", e);
        }
    }

    public static GeneratorConfig load(Path configPath) {
        if (configPath == null) {
            return loadDefault();
        }
        Path absolute = configPath.toAbsolutePath().normalize();
        if (!Files.isRegularFile(absolute)) {
            throw new IllegalArgumentException("配置文件不存在: " + absolute);
        }
        try (Reader reader = Files.newBufferedReader(absolute, StandardCharsets.UTF_8)) {
            return build(reader);
        } catch (IOException e) {
            throw new IllegalStateException("读取配置失败: " + absolute, e);
        }
    }

    private static GeneratorConfig parse(String content) {
        return build(new java.io.StringReader(content));
    }

    @SuppressWarnings("unchecked")
    private static GeneratorConfig build(Reader reader) {
        Object loaded = new Yaml().load(reader);
        if (loaded == null) {
            throw new IllegalArgumentException("配置文件内容为空");
        }
        if (!(loaded instanceof Map)) {
            throw new IllegalArgumentException("配置文件根节点必须是对象");
        }
        Map<String, Object> root = (Map<String, Object>) loaded;

        Map<String, Object> ds = section(root, "datasource");
        String driver = stringValue(ds, "driver-class-name", "com.mysql.cj.jdbc.Driver");
        String url = stringValue(ds, "url", null);
        String username = stringValue(ds, "username", null);
        String password = stringValue(ds, "password", "");

        if (isBlank(url)) {
            throw new IllegalArgumentException("datasource.url 不能为空");
        }
        if (isBlank(username)) {
            throw new IllegalArgumentException("datasource.username 不能为空");
        }

        Map<String, Object> out = section(root, "output");
        Path backendDir = resolvePath(stringValue(out, "backend-dir", null), "output.backend-dir");
        Path resourcesDir = resolvePath(stringValue(out, "resources-dir", null), "output.resources-dir");
        String frontendDirValue = stringValue(out, "frontend-dir", null);
        Path frontendDir = isBlank(frontendDirValue) ? null : resolvePath(frontendDirValue, "output.frontend-dir");
        GeneratorConfig.OverwritePolicy policy = parseEnum(GeneratorConfig.OverwritePolicy.class,
                stringValue(out, "overwrite-policy", "BACKUP"), "output.overwrite-policy");

        Map<String, Object> generator = section(root, "generator");
        String author = stringValue(generator, "author", "easyjava");
        String packageBase = stringValue(generator, "package-base", "com.easyjava");
        if (!PACKAGE_NAME.matcher(packageBase).matches()) {
            throw new IllegalArgumentException("generator.package-base 不是合法包名: " + packageBase);
        }

        Map<String, Object> packagesNode = section(generator, "packages");
        GeneratorConfig.Packages packages = new GeneratorConfig.Packages(
                packageBase,
                stringValue(packagesNode, "po", "entity.po"),
                stringValue(packagesNode, "query", "entity.query"),
                stringValue(packagesNode, "vo", "entity.vo"),
                stringValue(packagesNode, "utils", "utils"),
                stringValue(packagesNode, "enums", "enums"),
                stringValue(packagesNode, "exception", "exception"),
                stringValue(packagesNode, "mappers", "mappers"),
                stringValue(packagesNode, "service", "service"),
                stringValue(packagesNode, "service-impl", "service.impl"),
                stringValue(packagesNode, "controller", "controller")
        );

        Map<String, Object> tableNode = section(generator, "table");
        GeneratorConfig.Table table = new GeneratorConfig.Table(
                stringList(tableNode, "include"),
                stringList(tableNode, "exclude"),
                stringList(tableNode, "ignore-prefix")
        );

        Map<String, Object> backendNode = section(generator, "backend");
        GeneratorConfig.Backend backend = new GeneratorConfig.Backend(
                booleanValue(backendNode, "lombok", false),
                parseEnum(GeneratorConfig.DateType.class, stringValue(backendNode, "date-type", "LEGACY"),
                        "generator.backend.date-type"),
                booleanValue(backendNode, "transaction", true),
                stringList(backendNode, "ignore-json-fields")
        );

        Map<String, Object> frontendNode = section(root, "frontend");
        boolean frontendEnabled = booleanValue(frontendNode, "enabled", false);
        boolean frontendTypescript = booleanValue(frontendNode, "typescript", true);
        if (!frontendTypescript) {
            throw new IllegalArgumentException("frontend.typescript=false 暂未提供 JavaScript 模板");
        }
        if (frontendEnabled && frontendDir == null) {
            throw new IllegalArgumentException("frontend.enabled=true 时必须配置 output.frontend-dir");
        }
        String frontendPolicyValue = stringValue(frontendNode, "overwrite-policy", null);
        GeneratorConfig.OverwritePolicy frontendPolicy = isBlank(frontendPolicyValue) ? null
                : parseEnum(GeneratorConfig.OverwritePolicy.class, frontendPolicyValue,
                        "frontend.overwrite-policy");

        return new GeneratorConfig(
                new GeneratorConfig.DataSource(driver, url, username, password),
                new GeneratorConfig.Output(backendDir, resourcesDir, frontendDir, policy),
                table, packages, backend,
                new GeneratorConfig.Frontend(frontendEnabled, frontendTypescript,
                        booleanValue(frontendNode, "scaffold-only", false),
                        stringValue(frontendNode, "package-manager", "npm"),
                        stringValue(frontendNode, "api-base-path", "/api"),
                        frontendPolicy),
                author);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> section(Map<String, Object> parent, String key) {
        Object value = parent.get(key);
        if (value == null) {
            return Map.of();
        }
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("配置项 " + key + " 必须是对象");
        }
        return (Map<String, Object>) value;
    }

    private static String stringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }
        return resolvePlaceholders(String.valueOf(value));
    }

    private static boolean booleanValue(Map<String, Object> map, String key, boolean defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        return Boolean.parseBoolean(resolvePlaceholders(String.valueOf(value)));
    }

    private static List<String> stringList(Map<String, Object> map, String key) {
        Object value = map.get(key);
        List<String> result = new ArrayList<>();
        if (value == null) {
            return result;
        }
        if (value instanceof List<?> list) {
            for (Object item : list) {
                if (item != null) {
                    result.add(resolvePlaceholders(String.valueOf(item)));
                }
            }
            return result;
        }
        for (String item : String.valueOf(value).split(",")) {
            if (!item.isBlank()) {
                result.add(resolvePlaceholders(item.trim()));
            }
        }
        return result;
    }

    /**
     * 支持 ${ENV_NAME} 与 ${ENV_NAME:default} 两种写法。
     */
    static String resolvePlaceholders(String value) {
        if (value == null || !value.contains("${")) {
            return value;
        }
        Matcher matcher = PLACEHOLDER.matcher(value);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String envName = matcher.group(1);
            String defaultValue = matcher.group(2) == null ? "" : matcher.group(2);
            String replacement = System.getenv(envName);
            if (replacement == null) {
                replacement = System.getProperty(envName, defaultValue);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static Path resolvePath(String value, String configKey) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(configKey + " 不能为空");
        }
        Path path = Paths.get(value);
        if (!path.isAbsolute()) {
            path = Paths.get("").toAbsolutePath().resolve(path);
        }
        return path.normalize();
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> type, String value, String configKey) {
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(configKey + " 取值非法: " + value, e);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
