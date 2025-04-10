package com.easyjava.builder;

import com.easyjava.bean.Constans;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BuildMapper {

    private static final Logger logger = LoggerFactory.getLogger(BuildMapper.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constans.PATH_MAPPERS);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + Constans.SUFFIX_MAPPERS;
        File poFile = new File(folder, className+ ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outw);

            bw.write("package " + Constans.PACKAGE_MAPPERS + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            bw.newLine();

            //生成类注释
            BuildComment.createClassComment(bw, tableInfo.getComment()+"Mapper");
            bw.write("public interface " + className + "<T, P> extends BaseMapper {");
            bw.newLine();

            Map<String, List<FieldInfo>>  keyIndexMap = tableInfo.getKeyIndexMap();


            for(Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();

                int index = 0;
                StringBuilder methodName = new StringBuilder();

                StringBuilder methodParam = new StringBuilder();
                for(FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodName.append(StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if(index < keyFieldInfoList.size()) {
                        methodName.append("And");
                    }

                    methodParam.append("@Param(\""+fieldInfo.getPropertyName()+"\") "+fieldInfo.getJavaType()+" " + fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodParam.append(", ");
                    }
                }
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\tT selectBy" + methodName + "(" + methodParam + ");");
                bw.newLine();
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tLong updateBy" + methodName + "(@Param(\"bean\") T t, " + methodParam + ");");
                bw.newLine();
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tLong deleteBy" + methodName + "(" + methodParam + ");");
                bw.newLine();
                bw.newLine();
                bw.newLine();
            }

            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("生成MAPPERS失败", e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (outw != null) {
                    outw.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
