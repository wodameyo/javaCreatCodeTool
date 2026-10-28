package com.easyjava.core.config;

import java.nio.file.Path;
import java.util.List;

/**
 * 生成器全部配置的不可变入口。
 */
public final class GeneratorConfig {

    public enum OverwritePolicy {
        OVERWRITE, SKIP, BACKUP
    }

    public enum DateType {
        LEGACY, LOCAL
    }

    public static final class DataSource {
        private final String driverClassName;
        private final String url;
        private final String username;
        private final String password;

        public DataSource(String driverClassName, String url, String username, String password) {
            this.driverClassName = driverClassName;
            this.url = url;
            this.username = username;
            this.password = password;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    public static final class Output {
        private final Path backendDir;
        private final Path resourcesDir;
        private final Path frontendDir;
        private final OverwritePolicy overwritePolicy;

        public Output(Path backendDir, Path resourcesDir, Path frontendDir, OverwritePolicy overwritePolicy) {
            this.backendDir = backendDir;
            this.resourcesDir = resourcesDir;
            this.frontendDir = frontendDir;
            this.overwritePolicy = overwritePolicy;
        }

        public Path getBackendDir() {
            return backendDir;
        }

        public Path getResourcesDir() {
            return resourcesDir;
        }

        public Path getFrontendDir() {
            return frontendDir;
        }

        public OverwritePolicy getOverwritePolicy() {
            return overwritePolicy;
        }
    }

    public static final class Table {
        private final List<String> include;
        private final List<String> exclude;
        private final List<String> ignorePrefix;

        public Table(List<String> include, List<String> exclude, List<String> ignorePrefix) {
            this.include = include;
            this.exclude = exclude;
            this.ignorePrefix = ignorePrefix;
        }

        public List<String> getInclude() {
            return include;
        }

        public List<String> getExclude() {
            return exclude;
        }

        public List<String> getIgnorePrefix() {
            return ignorePrefix;
        }
    }

    public static final class Packages {
        private final String base;
        private final String po;
        private final String query;
        private final String vo;
        private final String utils;
        private final String enums;
        private final String exception;
        private final String mappers;
        private final String service;
        private final String serviceImpl;
        private final String controller;

        public Packages(String base, String po, String query, String vo, String utils, String enums,
                        String exception, String mappers, String service, String serviceImpl, String controller) {
            this.base = base;
            this.po = po;
            this.query = query;
            this.vo = vo;
            this.utils = utils;
            this.enums = enums;
            this.exception = exception;
            this.mappers = mappers;
            this.service = service;
            this.serviceImpl = serviceImpl;
            this.controller = controller;
        }

        public String getBase() {
            return base;
        }

        public String getPo() {
            return join(po);
        }

        public String getQuery() {
            return join(query);
        }

        public String getVo() {
            return join(vo);
        }

        public String getUtils() {
            return join(utils);
        }

        public String getEnums() {
            return join(enums);
        }

        public String getException() {
            return join(exception);
        }

        public String getMappers() {
            return join(mappers);
        }

        public String getService() {
            return join(service);
        }

        public String getServiceImpl() {
            return join(serviceImpl);
        }

        public String getController() {
            return join(controller);
        }

        private String join(String subPackage) {
            if (subPackage == null || subPackage.isBlank()) {
                return base;
            }
            return base + "." + subPackage;
        }

        public static String packagePath(String packageName) {
            return packageName.replace('.', '/');
        }
    }

    public static final class Backend {
        private final boolean lombok;
        private final DateType dateType;
        private final boolean transaction;
        private final List<String> ignoreJsonFields;

        public Backend(boolean lombok, DateType dateType, boolean transaction, List<String> ignoreJsonFields) {
            this.lombok = lombok;
            this.dateType = dateType;
            this.transaction = transaction;
            this.ignoreJsonFields = ignoreJsonFields == null ? List.of() : List.copyOf(ignoreJsonFields);
        }

        public boolean isLombok() {
            return lombok;
        }

        public DateType getDateType() {
            return dateType;
        }

        public boolean isTransaction() {
            return transaction;
        }

        public List<String> getIgnoreJsonFields() {
            return ignoreJsonFields;
        }
    }

    public static final class Frontend {
        private final boolean enabled;
        private final boolean typescript;
        private final boolean scaffoldOnly;
        private final String packageManager;
        private final String apiBasePath;
        private final OverwritePolicy overwritePolicy;

        public Frontend(boolean enabled) {
            this(enabled, true, false, "npm", "/api", null);
        }

        public Frontend(boolean enabled, boolean typescript, boolean scaffoldOnly,
                        String packageManager, String apiBasePath, OverwritePolicy overwritePolicy) {
            this.enabled = enabled;
            this.typescript = typescript;
            this.scaffoldOnly = scaffoldOnly;
            this.packageManager = packageManager == null ? "npm" : packageManager;
            this.apiBasePath = apiBasePath == null || apiBasePath.isBlank() ? "/api" : apiBasePath;
            this.overwritePolicy = overwritePolicy;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public boolean isTypescript() {
            return typescript;
        }

        public boolean isScaffoldOnly() {
            return scaffoldOnly;
        }

        public String getPackageManager() {
            return packageManager;
        }

        public String getApiBasePath() {
            return apiBasePath;
        }

        /**
         * 前端业务文件的覆盖策略；为 null 时沿用 output.overwrite-policy。
         */
        public OverwritePolicy getOverwritePolicy() {
            return overwritePolicy;
        }
    }

    private final DataSource datasource;
    private final Output output;
    private final Table table;
    private final Packages packages;
    private final Backend backend;
    private final Frontend frontend;
    private final String author;

    public GeneratorConfig(DataSource datasource, Output output, Table table, Packages packages,
                           Backend backend, Frontend frontend, String author) {
        this.datasource = datasource;
        this.output = output;
        this.table = table;
        this.packages = packages;
        this.backend = backend;
        this.frontend = frontend;
        this.author = author;
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public Output getOutput() {
        return output;
    }

    public Table getTable() {
        return table;
    }

    public Packages getPackages() {
        return packages;
    }

    public Backend getBackend() {
        return backend;
    }

    public Frontend getFrontend() {
        return frontend;
    }

    public String getAuthor() {
        return author;
    }
}
