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

public class BuildService {

    private static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    public static void excute(TableInfo tableInfo) {
        File folder = new File(Constans.PATH_SERVICE);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("无法创建目录: " + folder.getAbsolutePath());
        }

        String beanName = tableInfo.getBeanName();
        String classname = beanName + "Service";
        File poFile = new File(folder, classname + ".java");

        try (BufferedWriter bw = Files.newBufferedWriter(poFile.toPath(), StandardCharsets.UTF_8)) {
            bw.write("package " + Constans.PACKAGE_SERVICE + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import " + Constans.PACKAGE_VO + ".PaginationResultVO;");
            bw.newLine();
            bw.write("import " + Constans.PACKAGE_PO + "." + beanName + ";");
            bw.newLine();
            bw.write("import " + Constans.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment() + "Service");
            bw.write("public interface " + classname + " {");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "根据条件查询列表");
            bw.write("\tList<" + beanName + "> findListByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "根据条件查询数量");
            bw.write("\tInteger findCountByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "分页查询");
            bw.write("\tPaginationResultVO<" + beanName + "> findListByPage(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "新增");
            bw.write("\tInteger add(" + beanName + " bean);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\tInteger addBatch(List<" + beanName + "> listBean);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增或修改");
            bw.write("\tInteger addOrUpdateBatch(List<" + beanName + "> listBean);");
            bw.newLine();
            bw.newLine();

            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();

                int index = 0;
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParam = new StringBuilder();

                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodName.append(StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if (index < keyFieldInfoList.size()) {
                        methodName.append("And");
                    }

                    methodParam.append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodParam.append(", ");
                    }
                }

                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\t" + beanName + " get" + beanName + "By" + methodName + "(" + methodParam + ");");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tInteger update" + beanName + "By" + methodName + "(" + beanName + " bean, " + methodParam + ");");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger delete" + beanName + "By" + methodName + "(" + methodParam + ");");
                bw.newLine();
                bw.newLine();
            }

            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("生成SERVICE失败", e);
            throw new IllegalStateException("生成Service失败: " + classname, e);
        }
    }
}
