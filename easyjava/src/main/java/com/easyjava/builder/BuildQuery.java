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
import java.util.ArrayList;
import java.util.List;

public class BuildQuery {
    private  static  final Logger logger = LoggerFactory.getLogger(BuildQuery.class);
    public static void excute(TableInfo tableInfo) {
        File folder = new File(Constans.PATH_QUERY);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String className = tableInfo.getBeanName() + Constans.SUFFIX_BAEN_QUERY;

        File poFile = new File(folder, className + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out,"utf8");
            bw = new BufferedWriter(outw);

            bw.write("package "+Constans.PACKAGE_QUERY+";");
            bw.newLine();
            bw.newLine();


            if(tableInfo.isHaveDate() || tableInfo.isHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
            }

            if(tableInfo.isHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            bw.newLine();
            bw.newLine();
            //生成注释
            BuildComment.createClassComment(bw, tableInfo.getComment() + "查询对象");
            bw.write("public class "+ className +" extends BaseQuery {");
            bw.newLine();

            List<FieldInfo> extendList = new ArrayList<>();
            for (FieldInfo field : tableInfo.getFieldList()) {
                BuildComment.createFieldComment(bw, field.getComment());
                bw.write("\tprivate "+field.getJavaType()+" "+field.getPropertyName()+";");
                bw.newLine();
                bw.newLine();

                //String类型的参数
                if(ArrayUtils.contains(Constans.SQL_STRING_TYPES, field.getSqlType())) {
                    String propertyName = field.getPropertyName()+ Constans.SUFFIX_BAEN_QUERY_FUZZY;
                    bw.write("\tprivate " + field.getJavaType() + " " + propertyName +";");
                    bw.newLine();
                    bw.newLine();

                }

                if(ArrayUtils.contains(Constans.SQL_DATE_TIME_TYPES, field.getSqlType())|| ArrayUtils.contains(Constans.SQL_DATE_TYPES, field.getSqlType())) {
                    bw.write("\tprivate String " +field.getPropertyName() + Constans.SUFFIX_BAEN_QUERY_TIME_START+";");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tprivate String " +field.getPropertyName() + Constans.SUFFIX_BAEN_QUERY_TIME_END+";");
                    bw.newLine();
                    bw.newLine();

                }
            }
            buildGetSet(tableInfo.getFieldList(),bw);
            buildGetSet(tableInfo.getExtendFieldList(),bw);

            bw.write("}");
            bw.flush();
        }catch (Exception e) {
            logger.error("生成PO失败",e);
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

    private static void buildGetSet(List<FieldInfo> fieldInfoList, BufferedWriter bw) throws IOException{
        for(FieldInfo field : fieldInfoList) {
            //set方法
            String tempField = StringUtils.uperCaseFirstLetter(field.getPropertyName());
            bw.write("\tpublic void set"+ tempField +"(" +field.getJavaType()+" "+field.getPropertyName()+") {");
            bw.newLine();
            bw.write("\t\tthis."+field.getPropertyName()+" = "+field.getPropertyName()+";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
            //get方法
            bw.write("\tpublic "+field.getJavaType()+" get"+ tempField +"() {");
            bw.newLine();
            bw.write("\t\treturn this."+field.getPropertyName()+";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
        }
    }
}
