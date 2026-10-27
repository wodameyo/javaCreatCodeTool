package com.easyjava.server.dto;

import java.util.List;

/**
 * 控制台表单对应的生成配置，字段与 easyjava.yml 一一对应。
 */
public record GeneratorConfigRequest(String packageBase, Packages packages, String author,
                                     Output output, Backend backend, Table table) {

    public record Packages(String po, String query, String vo, String utils, String enums,
                           String exception, String mappers, String service, String serviceImpl,
                           String controller) {
    }

    public record Output(String backendDir, String resourcesDir, String frontendDir,
                         String overwritePolicy) {
    }

    public record Backend(Boolean lombok, String dateType, Boolean transaction,
                          List<String> ignoreJsonFields) {
    }

    public record Table(List<String> include, List<String> exclude, List<String> ignorePrefix) {
    }
}
