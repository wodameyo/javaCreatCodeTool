package com.easyjava.core.template;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * FreeMarker 封装。关闭 #{...} 插值，保证 MyBatis 占位符可直接书写。
 */
public final class TemplateEngine {

    private final Configuration configuration;

    public TemplateEngine() {
        this.configuration = new Configuration(Configuration.VERSION_2_3_33);
        this.configuration.setClassLoaderForTemplateLoading(
                TemplateEngine.class.getClassLoader(), "templates");
        this.configuration.setDefaultEncoding("UTF-8");
        this.configuration.setNumberFormat("computer");
        this.configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.configuration.setLogTemplateExceptions(false);
        this.configuration.setWrapUncheckedExceptions(true);
        this.configuration.setFallbackOnNullLoopVariable(false);
        this.configuration.setInterpolationSyntax(Configuration.DOLLAR_INTERPOLATION_SYNTAX);
    }

    public String render(String templateName, Map<String, Object> dataModel) {
        try (StringWriter writer = new StringWriter()) {
            configuration.getTemplate(templateName).process(dataModel, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new IllegalStateException("模板渲染失败: " + templateName, e);
        }
    }
}
