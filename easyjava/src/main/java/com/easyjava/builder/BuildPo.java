package com.easyjava.builder;

import com.easyjava.bean.Constans;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.DateUtils;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.easyjava.bean.FieldInfo;
import java.io.*;

public class BuildPo {
    private  static  final Logger logger = LoggerFactory.getLogger(BuildPo.class);
    public static void excute(TableInfo tableInfo) {
        File folder = new File(Constans.PATH_PO);
        if(!folder.exists()){
            folder.mkdirs();
        }

        File poFile = new File(folder, tableInfo.getBeanName() + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out,"utf8");
            bw = new BufferedWriter(outw);

            bw.write("package "+Constans.PACKAGE_PO+";");
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

            //是忽略属性
            Boolean haveIgnoreBean = false;
            for(FieldInfo field : tableInfo.getFieldList()) {
                if(ArrayUtils.contains(Constans.IGNORE_BEAN_TOJSON_FIELD.split(","), field.getPropertyName())){
                    haveIgnoreBean = true;
                    break;
                }
            }
            if (haveIgnoreBean){
                bw.write(Constans.IGNORE_BEAN_TOJSON_CLASS+";");
                bw.newLine();
            }

            if(tableInfo.isHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            bw.newLine();
            bw.newLine();
            //生成注释
            BuildComment.createClassComment(bw, tableInfo.getComment());
            bw.write("public class "+tableInfo.getBeanName()+" implements Serializable {");
            bw.newLine();
            for (FieldInfo field : tableInfo.getFieldList()) {
                BuildComment.createFieldComment(bw, field.getComment());

                if(ArrayUtils.contains(Constans.SQL_DATE_TIME_TYPES, field.getSqlType())) {
                    bw.write("\t"+String.format(Constans.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                    bw.write("\t"+String.format(Constans.BEAN_DATE_PARSE_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                }
                if(ArrayUtils.contains(Constans.SQL_DATE_TYPES, field.getSqlType())) {
                    bw.write("\t"+String.format(Constans.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();
                    bw.write("\t"+String.format(Constans.BEAN_DATE_PARSE_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();
                }

                if(ArrayUtils.contains(Constans.IGNORE_BEAN_TOJSON_FIELD.split(","), field.getPropertyName())){
                    bw.write("\t" + String.format(Constans.IGNORE_BEAN_TOJSON_EXPRESSION, field.getPropertyName()));
                    bw.newLine();
                }


                bw.write("\tprivate "+field.getJavaType()+" "+field.getPropertyName()+";");
                bw.newLine();
                bw.newLine();
            }

            for(FieldInfo field : tableInfo.getFieldList()) {
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

            //重写toString方法
            StringBuffer toString = new StringBuffer();
            for(FieldInfo field : tableInfo.getFieldList()) {
                String properName = field.getPropertyName();
                if(ArrayUtils.contains(Constans.SQL_DATE_TIME_TYPES, field.getSqlType())) {
                    properName = "DateUtils.format(" + properName + ", DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())";
                } else if(ArrayUtils.contains(Constans.SQL_DATE_TYPES, field.getSqlType())) {
                    properName = "DateUtils.format(" + properName + ", DateTimePatternEnum.YYYY_MM_DD.getPattern())";
                }
                toString.append("\"," + field.getComment() + ":\" + (" + field.getPropertyName() +
                        " == null ? \"空\" : "+ properName +") + ");
                if(field.getPropertyName().equals(tableInfo.getFieldList().get(0).getPropertyName())){
                    toString.deleteCharAt(1);
                }
                if(field.getPropertyName().equals(tableInfo.getFieldList().get(tableInfo.getFieldList().size()-1).getPropertyName())){
                    toString.deleteCharAt(toString.length()-1);
                    toString.deleteCharAt(toString.length()-1);
                }
            }

            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            bw.write("\t\treturn "+ toString +";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

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
}
