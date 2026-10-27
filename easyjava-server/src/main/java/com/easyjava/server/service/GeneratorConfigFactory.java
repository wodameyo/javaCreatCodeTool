package com.easyjava.server.service;

import com.easyjava.core.config.GeneratorConfig;
import com.easyjava.server.dto.GeneratorConfigRequest;
import com.easyjava.server.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 将控制台表单转换为生成器使用的不可变 {@link GeneratorConfig}。
 */
@Component
public class GeneratorConfigFactory {

    private static final Pattern PACKAGE_NAME =
            Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)*$");

    private final PathGuard pathGuard;

    public GeneratorConfigFactory(PathGuard pathGuard) {
        this.pathGuard = pathGuard;
    }

    public GeneratorConfigRequest defaults() {
        return new GeneratorConfigRequest(
                "com.easyjava",
                new GeneratorConfigRequest.Packages("entity.po", "entity.query", "entity.vo",
                        "utils", "enums", "exception", "mappers", "service", "service.impl",
                        "controller"),
                "easyjava",
                new GeneratorConfigRequest.Output("easyjava-demo/src/main/java",
                        "easyjava-demo/src/main/resources", "easyjava-demo-ui", "BACKUP"),
                new GeneratorConfigRequest.Backend(false, "LEGACY", true, List.of("status", "taotle")),
                new GeneratorConfigRequest.Table(List.of(), List.of(), List.of("ts_")));
    }

    public GeneratorConfig create(GeneratorConfigRequest request, GeneratorConfig.DataSource datasource,
                                  List<String> selectedTables) {
        GeneratorConfigRequest source = request == null ? defaults() : request;
        GeneratorConfigRequest fallback = defaults();

        GeneratorConfigRequest.Packages packages = source.packages() == null
                ? fallback.packages() : source.packages();
        String packageBase = validatePackage(nonBlank(source.packageBase(),
                fallback.packageBase()), "package-base");
        GeneratorConfig.Packages resolvedPackages = new GeneratorConfig.Packages(
                packageBase,
                sub(packages.po(), fallback.packages().po()),
                sub(packages.query(), fallback.packages().query()),
                sub(packages.vo(), fallback.packages().vo()),
                sub(packages.utils(), fallback.packages().utils()),
                sub(packages.enums(), fallback.packages().enums()),
                sub(packages.exception(), fallback.packages().exception()),
                sub(packages.mappers(), fallback.packages().mappers()),
                sub(packages.service(), fallback.packages().service()),
                sub(packages.serviceImpl(), fallback.packages().serviceImpl()),
                sub(packages.controller(), fallback.packages().controller()));

        GeneratorConfigRequest.Output output = source.output() == null
                ? fallback.output() : source.output();
        Path backendDir = pathGuard.resolve(nonBlank(output.backendDir(),
                fallback.output().backendDir()), "output.backendDir");
        Path resourcesDir = pathGuard.resolve(nonBlank(output.resourcesDir(),
                fallback.output().resourcesDir()), "output.resourcesDir");
        GeneratorConfig.OverwritePolicy policy = parsePolicy(nonBlank(output.overwritePolicy(),
                fallback.output().overwritePolicy()));
        GeneratorConfig.Output resolvedOutput = new GeneratorConfig.Output(
                backendDir, resourcesDir, null, policy);

        GeneratorConfigRequest.Table table = source.table() == null ? fallback.table() : source.table();
        List<String> include = selectedTables != null && !selectedTables.isEmpty()
                ? selectedTables : table.include();
        GeneratorConfig.Table resolvedTable = new GeneratorConfig.Table(
                cleanList(include), cleanList(table.exclude()), cleanList(table.ignorePrefix()));

        GeneratorConfigRequest.Backend backend = source.backend() == null
                ? fallback.backend() : source.backend();
        GeneratorConfig.Backend resolvedBackend = new GeneratorConfig.Backend(
                Boolean.TRUE.equals(backend.lombok()),
                parseDateType(nonBlank(backend.dateType(), fallback.backend().dateType())),
                backend.transaction() == null || backend.transaction(),
                cleanList(backend.ignoreJsonFields()));

        String author = nonBlank(source.author(), fallback.author());
        return new GeneratorConfig(datasource, resolvedOutput, resolvedTable, resolvedPackages,
                resolvedBackend, new GeneratorConfig.Frontend(false), author);
    }

    private static String validatePackage(String value, String field) {
        if (!PACKAGE_NAME.matcher(value).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, field + " 不是合法的包名: " + value);
        }
        return value;
    }

    private static GeneratorConfig.OverwritePolicy parsePolicy(String value) {
        try {
            return GeneratorConfig.OverwritePolicy.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "覆盖策略必须是 OVERWRITE / SKIP / BACKUP");
        }
    }

    private static GeneratorConfig.DateType parseDateType(String value) {
        try {
            return GeneratorConfig.DateType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "日期类型必须是 LEGACY / LOCAL");
        }
    }

    private static List<String> cleanList(List<String> values) {
        List<String> result = new ArrayList<>();
        if (values != null) {
            for (String value : values) {
                if (value != null && !value.isBlank()) {
                    result.add(value.trim());
                }
            }
        }
        return result;
    }

    private static String sub(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static String nonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
