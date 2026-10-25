package com.easyjava.builder;

import com.easyjava.bean.Constans;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class BuildController {

    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    public static void excute(TableInfo tableInfo) {
        File folder = new File(Constans.PATH_CONTROLLER);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("无法创建目录: " + folder.getAbsolutePath());
        }

        String beanName = tableInfo.getBeanName();
        String classname = beanName + "Controller";
        String serviceName = beanName + "Service";
        String serviceBeanName = StringUtils.lowerCaseFirstLetter(serviceName);
        File poFile = new File(folder, classname + ".java");

        try (BufferedWriter bw = Files.newBufferedWriter(poFile.toPath(), StandardCharsets.UTF_8)) {
            bw.write("package " + Constans.PACKAGE_CONTROLLER + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import " + Constans.PACKAGE_PO + "." + beanName + ";");
            bw.newLine();
            bw.write("import " + Constans.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constans.PACKAGE_SERVICE + "." + serviceName + ";");
            bw.newLine();
            bw.write("import " + Constans.PACKAGE_VO + ".ResponseVO;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RestController;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestBody;");
            bw.newLine();
            bw.write("import org.springframework.beans.factory.annotation.Autowired;");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment() + "Controller");
            bw.write("@RestController");
            bw.newLine();
            bw.write("@RequestMapping(\"/" + StringUtils.lowerCaseFirstLetter(beanName) + "\")");
            bw.newLine();
            bw.write("public class " + classname + " extends ABaseController {");
            bw.newLine();
            bw.newLine();

            bw.write("\t@Autowired");
            bw.newLine();
            bw.write("\tprivate " + serviceName + " " + serviceBeanName + ";");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "根据条件分页查询");
            bw.write("\t@RequestMapping(\"/loadDataList\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO loadDataList(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".findListByPage(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "新增");
            bw.write("\t@RequestMapping(\"/add\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO add(@RequestBody " + beanName + " bean) {");
            bw.newLine();
            bw.write("\t\tthis." + serviceBeanName + ".add(bean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\t@RequestMapping(\"/addBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addBatch(@RequestBody List<" + beanName + "> listBean) {");
            bw.newLine();
            bw.write("\t\tthis." + serviceBeanName + ".addBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增或修改");
            bw.write("\t@RequestMapping(\"/addOrUpdateBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addOrUpdateBatch(@RequestBody List<" + beanName + "> listBean) {");
            bw.newLine();
            bw.write("\t\tthis." + serviceBeanName + ".addOrUpdateBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();

                int index = 0;
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParam = new StringBuilder();
                StringBuilder paramNames = new StringBuilder();

                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodName.append(StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if (index < keyFieldInfoList.size()) {
                        methodName.append("And");
                    }

                    methodParam.append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName());
                    paramNames.append(fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodParam.append(", ");
                        paramNames.append(", ");
                    }
                }

                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\t@RequestMapping(\"/get" + beanName + "By" + methodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO get" + beanName + "By" + methodName + "(" + methodParam + ") {");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".get" + beanName
                        + "By" + methodName + "(" + paramNames + "));");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\t@RequestMapping(\"/update" + beanName + "By" + methodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO update" + beanName + "By" + methodName + "(@RequestBody " + beanName
                        + " bean, " + methodParam + ") {");
                bw.newLine();
                bw.write("\t\tthis." + serviceBeanName + ".update" + beanName + "By" + methodName
                        + "(bean, " + paramNames + ");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\t@RequestMapping(\"/delete" + beanName + "By" + methodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO delete" + beanName + "By" + methodName + "(" + methodParam + ") {");
                bw.newLine();
                bw.write("\t\tthis." + serviceBeanName + ".delete" + beanName + "By" + methodName + "(" + paramNames + ");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
            }

            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("生成Controller失败", e);
            throw new IllegalStateException("生成Controller失败: " + classname, e);
        }
    }
}
