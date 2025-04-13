package com.easyjava.builder;

import com.easyjava.bean.Constans;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.DateUtils;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BuildService {
    private  static  final Logger logger = LoggerFactory.getLogger(BuildService.class);
    public static void excute(TableInfo tableInfo) {
        File folder = new File(Constans.PATH_SERVICE);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String classname = tableInfo.getBeanName()+"Service";
        File poFile = new File(folder, classname + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out,"utf8");
            bw = new BufferedWriter(outw);

            bw.write("package "+Constans.PACKAGE_SERVICE+";");
            bw.newLine();
            bw.newLine();


            if(tableInfo.isHaveDate() || tableInfo.isHaveDateTime()) {
                bw.write(Constans.BEAN_DATE_PARSE_CLASS);
                bw.newLine();
                bw.write(Constans.BEAN_DATE_FORMAT_CLASS);
                bw.newLine();
                bw.write("import " + Constans.PACKAGE_ENUMS+ ".DateTimePatternEnum;");
                bw.newLine();
                bw.write("import "+ Constans.PACKAGE_UTILS + ".DateUtils;");
                bw.newLine();
                bw.write("import java.util.Date;");
                bw.newLine();
            }
            bw.write("import java.io.Serializable;");
            bw.newLine();

            //引入PaginationResultVO
            bw.write("import "+Constans.PACKAGE_VO+"."+"PaginationResultVO;");
            bw.newLine();
            //引入PO
            bw.write("import "+Constans.PACKAGE_PO+"."+tableInfo.getBeanName()+";");
            bw.newLine();
            //引入Query
            bw.write("import "+Constans.PACKAGE_QUERY+"."+tableInfo.getBeanParamName()+";");
            bw.newLine();
            //引入集合
            bw.write("import java.util.List;");
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment()+"Service");
            bw.write("public interface "+classname+" {");
            bw.newLine();

            //开始生成内容

            BuildComment.createFieldComment(bw, "根据条件查询列表");
            bw.write("\tList<"+tableInfo.getBeanName()+"> findListByParam("+tableInfo.getBeanParamName()+" query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "根据条件查询数量");
            bw.write("\tInteger findCountByParam("+tableInfo.getBeanParamName()+" query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "分页查询");
            bw.write("\tPaginationResultVO<"+tableInfo.getBeanName()+"> findListByPage("+tableInfo.getBeanParamName()+" query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "新增");
            bw.write("\tInteger add("+tableInfo.getBeanName()+" bean);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\tInteger addBatch(List<"+tableInfo.getBeanName()+"> listbean);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增或修改");
            bw.write("\tInteger addOrUpdateBatch(List<"+tableInfo.getBeanName()+"> listbean);");
            bw.newLine();
            bw.newLine();

            for(Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
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

                    methodParam.append(fieldInfo.getJavaType()+" " + fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodParam.append(", ");
                    }
                }
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\t"+tableInfo.getBeanName()+" get"+tableInfo.getBeanName()+"By" + methodName + "(" + methodParam + ");");
                bw.newLine();
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tInteger update"+tableInfo.getBeanName()+"By" + methodName + "("+tableInfo.getBeanName()+" bean, " + methodParam + ");");
                bw.newLine();
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger delete"+tableInfo.getBeanName()+"By" + methodName + "(" + methodParam + ");");
                bw.newLine();
                bw.newLine();
                bw.newLine();
            }

            bw.newLine();
            bw.write("}");
            bw.flush();
        }catch (Exception e) {
            logger.error("生成SERVICE失败",e);
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
