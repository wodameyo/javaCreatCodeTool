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

public class BuildMapper {

    private static final Logger logger = LoggerFactory.getLogger(BuildMapper.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constans.PATH_MAPPERS);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("无法创建目录: " + folder.getAbsolutePath());
        }

        String className = tableInfo.getBeanName() + Constans.SUFFIX_MAPPERS;
        File mapperFile = new File(folder, className + ".java");
        String beanName = tableInfo.getBeanName();
        String queryName = tableInfo.getBeanParamName();

        try (BufferedWriter bw = Files.newBufferedWriter(mapperFile.toPath(), StandardCharsets.UTF_8)) {
            bw.write("package " + Constans.PACKAGE_MAPPERS + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import " + Constans.PACKAGE_PO + "." + beanName + ";");
            bw.newLine();
            bw.write("import " + Constans.PACKAGE_QUERY + "." + queryName + ";");
            bw.newLine();
            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment() + "Mapper");
            bw.write("public interface " + className + " extends BaseMapper<" + beanName + ", " + queryName + "> {");
            bw.newLine();

            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();

                int index = 0;
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParam = new StringBuilder();
                String paramNames = buildParamNames(keyFieldInfoList);

                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodName.append(StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if (index < keyFieldInfoList.size()) {
                        methodName.append("And");
                    }
                    methodParam.append("@Param(\"").append(fieldInfo.getPropertyName()).append("\") ")
                            .append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodParam.append(", ");
                    }
                }

                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\t" + beanName + " selectBy" + methodName + "(" + methodParam + ");");
                bw.newLine();
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tInteger updateBy" + methodName + "(@Param(\"bean\") " + beanName + " bean, "
                        + methodParam + ");");
                bw.newLine();
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger deleteBy" + methodName + "(" + methodParam + ");");
                bw.newLine();
                bw.newLine();
                bw.newLine();
            }

            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("生成Mapper失败", e);
            throw new IllegalStateException("生成Mapper失败: " + className, e);
        }
    }

    private static String buildParamNames(List<FieldInfo> keyFieldInfoList) {
        StringBuilder paramNames = new StringBuilder();
        for (int i = 0; i < keyFieldInfoList.size(); i++) {
            if (i > 0) {
                paramNames.append(",");
            }
            paramNames.append(keyFieldInfoList.get(i).getPropertyName());
        }
        return paramNames.toString();
    }
}
