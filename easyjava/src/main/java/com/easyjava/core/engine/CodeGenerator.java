package com.easyjava.core.engine;

import com.easyjava.core.config.GeneratorConfig;
import com.easyjava.core.model.ColumnModel;
import com.easyjava.core.model.TableModel;
import com.easyjava.core.template.TemplateEngine;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将表模型渲染为待写入文件列表。
 */
public final class CodeGenerator {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final List<FrontendTemplate> FRONTEND_SCAFFOLD = List.of(
            new FrontendTemplate("frontend/package.json.ftl", "package.json"),
            new FrontendTemplate("frontend/vite.config.ts.ftl", "vite.config.ts"),
            new FrontendTemplate("frontend/tsconfig.json.ftl", "tsconfig.json"),
            new FrontendTemplate("frontend/tsconfig.node.json.ftl", "tsconfig.node.json"),
            new FrontendTemplate("frontend/index.html.ftl", "index.html"),
            new FrontendTemplate("frontend/env.development.ftl", ".env.development"),
            new FrontendTemplate("frontend/env.d.ts.ftl", "src/env.d.ts"),
            new FrontendTemplate("frontend/main.ts.ftl", "src/main.ts"),
            new FrontendTemplate("frontend/App.vue.ftl", "src/App.vue"),
            new FrontendTemplate("frontend/router-index.ts.ftl", "src/router/index.ts"),
            new FrontendTemplate("frontend/request.ts.ftl", "src/utils/request.ts"),
            new FrontendTemplate("frontend/common-types.ts.ftl", "src/types/common.ts"),
            new FrontendTemplate("frontend/welcome.vue.ftl", "src/views/WelcomeView.vue")
    );

    private static final List<BaseTemplate> BASE_TEMPLATES = List.of(
            new BaseTemplate("base/DateTimePatternEnum.java.ftl", BaseTemplate.Kind.ENUMS, "DateTimePatternEnum"),
            new BaseTemplate("base/DateUtils.java.ftl", BaseTemplate.Kind.UTILS, "DateUtils"),
            new BaseTemplate("base/PageSize.java.ftl", BaseTemplate.Kind.ENUMS, "PageSize"),
            new BaseTemplate("base/SimplePage.java.ftl", BaseTemplate.Kind.QUERY, "SimplePage"),
            new BaseTemplate("base/BaseQuery.java.ftl", BaseTemplate.Kind.QUERY, "BaseQuery"),
            new BaseTemplate("base/BaseMapper.java.ftl", BaseTemplate.Kind.MAPPERS, "BaseMapper"),
            new BaseTemplate("base/PaginationResultVO.java.ftl", BaseTemplate.Kind.VO, "PaginationResultVO"),
            new BaseTemplate("base/ResponseVO.java.ftl", BaseTemplate.Kind.VO, "ResponseVO"),
            new BaseTemplate("base/BusinessException.java.ftl", BaseTemplate.Kind.EXCEPTION, "BusinessException"),
            new BaseTemplate("base/ResponseCodeEnum.java.ftl", BaseTemplate.Kind.ENUMS, "ResponseCodeEnum"),
            new BaseTemplate("base/ABaseController.java.ftl", BaseTemplate.Kind.CONTROLLER, "ABaseController"),
            new BaseTemplate("base/AGlobalExceptionHandlerController.java.ftl",
                    BaseTemplate.Kind.CONTROLLER, "AGlobalExceptionHandlerController")
    );

    private final GeneratorConfig config;
    private final TemplateEngine templateEngine;
    private final String timestamp;

    public CodeGenerator(GeneratorConfig config) {
        this(config, new TemplateEngine(), TIMESTAMP.format(LocalDateTime.now()));
    }

    public CodeGenerator(GeneratorConfig config, TemplateEngine templateEngine) {
        this(config, templateEngine, TIMESTAMP.format(LocalDateTime.now()));
    }

    public CodeGenerator(GeneratorConfig config, TemplateEngine templateEngine, String timestamp) {
        this.config = config;
        this.templateEngine = templateEngine;
        this.timestamp = timestamp;
    }

    public GenerationPlan generate(List<TableModel> tables) {
        List<GeneratedFile> files = new ArrayList<>();
        Map<String, Object> baseData = baseData();

        for (BaseTemplate template : BASE_TEMPLATES) {
            files.add(renderJava(template.template(), resolvePackage(template.kind()),
                    template.fileName() + ".java", baseData));
        }

        for (TableModel table : tables) {
            Map<String, Object> data = new HashMap<>(baseData);
            data.put("table", table);
            ColumnModel autoIncrement = table.getAutoIncrementColumn();
            if (autoIncrement != null) {
                data.put("autoIncrementColumn", autoIncrement);
            }

            files.add(renderJava("po.java.ftl", packages().getPo(), table.getBeanName() + ".java", data));
            files.add(renderJava("query.java.ftl", packages().getQuery(), table.getQueryName() + ".java", data));
            files.add(renderJava("mapper.java.ftl", packages().getMappers(), table.getMapperName() + ".java", data));
            files.add(renderJava("service.java.ftl", packages().getService(), table.getServiceName() + ".java", data));
            files.add(renderJava("serviceImpl.java.ftl", packages().getServiceImpl(),
                    table.getServiceImplName() + ".java", data));
            files.add(renderJava("controller.java.ftl", packages().getController(),
                    table.getControllerName() + ".java", data));
            files.add(renderMapperXml(table, data));
        }

        addFrontendFiles(files, tables, baseData);

        return new GenerationPlan(files, config);
    }

    private void addFrontendFiles(List<GeneratedFile> files, List<TableModel> tables,
                                  Map<String, Object> baseData) {
        GeneratorConfig.Frontend frontend = config.getFrontend();
        if (frontend == null || !frontend.isEnabled()) {
            return;
        }
        Path frontendDir = config.getOutput().getFrontendDir();
        if (frontendDir == null) {
            throw new IllegalStateException("frontend.enabled=true 时需要配置 output.frontend-dir");
        }

        Map<String, Object> data = new HashMap<>(baseData);
        data.put("frontend", frontend);
        data.put("tables", tables);
        data.put("projectName", projectName(frontendDir));
        data.put("business", !frontend.isScaffoldOnly());

        for (FrontendTemplate template : FRONTEND_SCAFFOLD) {
            files.add(renderFrontend(template.template(), template.path(), data, true, false));
        }
        files.add(renderFrontend("frontend/router-tables.ts.ftl", "src/router/tables.ts",
                data, false, true));

        if (frontend.isScaffoldOnly()) {
            return;
        }
        for (TableModel table : tables) {
            Map<String, Object> tableData = new HashMap<>(data);
            tableData.put("table", table);
            String module = table.getModuleName();
            files.add(renderFrontend("frontend/entity-types.ts.ftl",
                    "src/types/" + module + ".ts", tableData, false, false));
            files.add(renderFrontend("frontend/api.ts.ftl",
                    "src/api/" + module + ".ts", tableData, false, false));
            files.add(renderFrontend("frontend/list.vue.ftl",
                    "src/views/" + module + "/index.vue", tableData, false, false));
            files.add(renderFrontend("frontend/form.vue.ftl",
                    "src/views/" + module + "/components/" + table.getBeanName() + "Form.vue",
                    tableData, false, false));
        }
    }

    private GeneratedFile renderFrontend(String template, String relativePath,
                                         Map<String, Object> data, boolean scaffold, boolean mergeable) {
        String content = templateEngine.render(template, data);
        Path target = config.getOutput().getFrontendDir().resolve(relativePath);
        return new GeneratedFile("frontend/" + relativePath, target, content,
                GeneratedFile.Kind.FRONTEND, scaffold, mergeable);
    }

    private static String projectName(Path frontendDir) {
        Path fileName = frontendDir.getFileName();
        String raw = fileName == null ? "easyjava-frontend" : fileName.toString();
        String normalized = raw.toLowerCase()
                .replaceAll("[^a-z0-9._-]", "-")
                .replaceAll("^-+", "");
        return normalized.isBlank() ? "easyjava-frontend" : normalized;
    }

    private GeneratedFile renderJava(String template, String packageName, String fileName,
                                     Map<String, Object> data) {
        String content = templateEngine.render(template, data);
        String packagePath = GeneratorConfig.Packages.packagePath(packageName);
        Path target = config.getOutput().getBackendDir().resolve(packagePath).resolve(fileName);
        String logical = "java/" + packagePath + "/" + fileName;
        return new GeneratedFile(logical, target, content, GeneratedFile.Kind.JAVA);
    }

    private GeneratedFile renderMapperXml(TableModel table, Map<String, Object> data) {
        String content = templateEngine.render("mapperXml.xml.ftl", data);
        String packagePath = GeneratorConfig.Packages.packagePath(packages().getMappers());
        String fileName = table.getMapperName() + ".xml";
        Path target = config.getOutput().getResourcesDir().resolve(packagePath).resolve(fileName);
        String logical = "resources/" + packagePath + "/" + fileName;
        return new GeneratedFile(logical, target, content, GeneratedFile.Kind.RESOURCES);
    }

    private Map<String, Object> baseData() {
        Map<String, Object> data = new HashMap<>();
        data.put("packages", packages());
        data.put("author", config.getAuthor());
        data.put("timestamp", timestamp);
        data.put("lombok", config.getBackend().isLombok());
        data.put("transaction", config.getBackend().isTransaction());
        data.put("legacyDate", config.getBackend().getDateType() == GeneratorConfig.DateType.LEGACY);
        return data;
    }

    private GeneratorConfig.Packages packages() {
        return config.getPackages();
    }

    private String resolvePackage(BaseTemplate.Kind kind) {
        return switch (kind) {
            case ENUMS -> packages().getEnums();
            case UTILS -> packages().getUtils();
            case QUERY -> packages().getQuery();
            case MAPPERS -> packages().getMappers();
            case VO -> packages().getVo();
            case EXCEPTION -> packages().getException();
            case CONTROLLER -> packages().getController();
        };
    }

    private record BaseTemplate(String template, Kind kind, String fileName) {
        enum Kind {
            ENUMS, UTILS, QUERY, MAPPERS, VO, EXCEPTION, CONTROLLER
        }
    }

    private record FrontendTemplate(String template, String path) {
    }
}
