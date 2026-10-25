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
import java.util.ArrayList;
import java.util.List;

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
                writeImport(bw, Constans.BEAN_DATE_PARSE_CLASS);
                writeImport(bw, Constans.BEAN_DATE_FORMAT_CLASS);
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
            boolean haveIgnoreBean = false;
            String[] ignoreFields = Constans.IGNORE_BEAN_TOJSON_FIELD.split(",");
            for(FieldInfo field : tableInfo.getFieldList()) {
                if(ArrayUtils.contains(ignoreFields, field.getPropertyName())){
                    haveIgnoreBean = true;
                    break;
                }
            }
            if (haveIgnoreBean){
                writeImport(bw, Constans.IGNORE_BEAN_TOJSON_CLASS);
            }

            if(tableInfo.isHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            boolean haveBigInteger = tableInfo.getFieldList().stream()
                    .anyMatch(field -> "BigInteger".equals(field.getJavaType()));
            if (haveBigInteger) {
                bw.write("import java.math.BigInteger;");
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
            List<String> toStringParts = new ArrayList<>();
            for(FieldInfo field : tableInfo.getFieldList()) {
                String properName = field.getPropertyName();
                if(ArrayUtils.contains(Constans.SQL_DATE_TIME_TYPES, field.getSqlType())) {
                    properName = "DateUtils.format(" + properName + ", DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())";
                } else if(ArrayUtils.contains(Constans.SQL_DATE_TYPES, field.getSqlType())) {
                    properName = "DateUtils.format(" + properName + ", DateTimePatternEnum.YYYY_MM_DD.getPattern())";
                }
                String comment = field.getComment() == null ? "" : field.getComment();
                toStringParts.add("(\"" + escapeJavaString(comment) + ":\" + (" + field.getPropertyName()
                        + " == null ? \"空\" : " + properName + "))");
            }

            String toStringExpression = toStringParts.isEmpty()
                    ? "\"" + escapeJavaString(tableInfo.getBeanName()) + "\""
                    : String.join(" + \", \" + ", toStringParts);

            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            bw.write("\t\treturn "+ toStringExpression +";");
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

    private static void writeImport(BufferedWriter bw, String importLine) throws IOException {
        if (importLine == null || importLine.isBlank()) {
            return;
        }
        String line = importLine.trim();
        while (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1).trim();
        }
        if (!line.startsWith("import ")) {
            line = "import " + line;
        }
        bw.write(line + ";");
        bw.newLine();
    }

    private static String escapeJavaString(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
