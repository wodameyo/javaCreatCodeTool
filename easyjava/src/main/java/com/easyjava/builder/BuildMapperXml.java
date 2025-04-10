package com.easyjava.builder;

import com.easyjava.bean.Constans;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildMapperXml {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);

    private static final String BASE_COLUMN_LIST = "Base_Column_List";

    private static final String BASE_QUERY_CONDITION = "Base_Query_Condition";

    private static final String BASE_QUERY_CONDITION_EXTEND = "Base_Query_Condition_Extend";

    private static final String QUERY_CONDITION = "Query_Condition";

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constans.PATH_MAPPER_XML);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + Constans.SUFFIX_MAPPERS;
        File poFile = new File(folder, className+ ".xml");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outw);

            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            bw.newLine();
            bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >");
            bw.newLine();
            bw.write("<mapper namespace=\"" + Constans.PACKAGE_MAPPERS + "." + className + "\">");
            bw.newLine();

            bw.write("\t<!--实体映射-->");
            bw.newLine();
            String poClass = Constans.PACKAGE_PO + "." + tableInfo.getBeanName();
            bw.write("\t<resultMap id=\"base_result_map\" type=\"" + poClass + "\">");
            bw.newLine();

            FieldInfo idField = null;
            for(Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                if("PRIMARY".equals(entry.getKey())){
                    idField = entry.getValue().get(0);
                    break;
                }
            }

            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write(("\t\t<!--" + fieldInfo.getComment() + "-->"));
                bw.newLine();
                String key = "";
                if (idField != null && fieldInfo.getPropertyName().equals(idField.getPropertyName())) {
                    key = "id";
                } else {
                    key = "result";
                }
                bw.write("\t\t<" + key + " column=\"" + fieldInfo.getFieldName() + "\" property=\"" + fieldInfo.getPropertyName() + "\" javaType=\"" + fieldInfo.getJavaType() + "\" />");
                bw.newLine();
                bw.newLine();
            }
            bw.write("\t</resultMap>");
            bw.newLine();
            //通用查询列
            bw.newLine();
            bw.write("\t<!--通用查询结果列-->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_COLUMN_LIST + "\">");
            bw.newLine();
            StringBuilder columnBuilder = new StringBuilder();
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                columnBuilder.append(fieldInfo.getFieldName()).append(",");
            }
            String columnBuilderStr = columnBuilder.substring(0, columnBuilder.lastIndexOf(","));
            bw.write("\t" + columnBuilderStr);
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            //基础查询条件
            bw.newLine();
            bw.write("\t<!--基础查询条件-->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION + "\">");
            bw.newLine();
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String stringQuery = "";
                if(ArrayUtils.contains(Constans.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    stringQuery = " and query." +fieldInfo.getPropertyName() + "!=''";
                }

                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null" +stringQuery +"\">");
                bw.newLine();
                // bw.write("\t\t\tand t." + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
                bw.write("\t\t\tand id = #{query." + fieldInfo.getPropertyName() + "}");
                bw.newLine();
                bw.write(("\t\t</if>"));
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();

            bw.newLine();
            bw.write("\t<!--扩展的查询条件-->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION_EXTEND + "\">");
            bw.newLine();
            for(FieldInfo fieldInfo : tableInfo.getExtendFieldList()) {
                String andWhere = "";
                if(ArrayUtils.contains(Constans.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    andWhere = " and " + fieldInfo.getFieldName() + " like concat('%', #{query." +fieldInfo.getPropertyName() + "}, '%')";
                } else if(ArrayUtils.contains(Constans.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())|| ArrayUtils.contains(Constans.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
                    if(fieldInfo.getPropertyName().endsWith(Constans.SUFFIX_BAEN_QUERY_TIME_START)) {
                        andWhere = "and " + fieldInfo.getFieldName() + " >= str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d')";
                    }else if(fieldInfo.getPropertyName().endsWith(Constans.SUFFIX_BAEN_QUERY_TIME_END)) {
                        andWhere = "and " + fieldInfo.getFieldName() + " &lt; date_sub(str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d')," + "interval 1 day)";
                    }

                }
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null and query." +fieldInfo.getPropertyName() + " !=''"+"\">");
                bw.newLine();
                // bw.write("\t\t\tand t." + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
                // bw.write("\t\t\tand id = " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
                bw.write("\t\t\t" + andWhere);
                bw.newLine();
                bw.write(("\t\t</if>"));
                bw.newLine();
            }
            bw.write("\t</sql>");
            bw.newLine();


            //通用查询条件
            bw.newLine();
            bw.write("\t<!--扩展的查询结果列-->");
            bw.newLine();
            bw.write("\t<sql id=\"" + QUERY_CONDITION + "\">");
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION_EXTEND + "\"/>");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            //查询列表
            bw.newLine();
            bw.write("\t<!--查询列表-->");
            bw.newLine();
            bw.write("\t<select id=\"selectList\" resultMap=\"base_result_map\">");
            bw.newLine();
            bw.write("\t\tSELECT <include refid=\""+ BASE_COLUMN_LIST + "\"/> FROM " + tableInfo.getTableName() +" <include refid=\""
            +QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.orderBy!=null\">order by $(query.orderBy)</if>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.simplePage!=null\">limit #(query.simplePage.start),#(query.simplePage.end)</if>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();

            //查询数量
            bw.newLine();
            bw.write("\t<!--查询数量-->");
            bw.newLine();
            bw.write("\t<select id=\"selectCount\" resultType=\"java.lang.Long\">");
            bw.newLine();
            bw.write("\t\tSELECT count(1) FROM " + tableInfo.getTableName() +" <include refid=\"" +QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();

            //单条插入
            bw.newLine();
            bw.write("\t<!--单条插入(匹配有值的字段)-->");
            bw.newLine();
            bw.write("\t<insert id=\"insert\" parameterType=\"" + poClass + "\"" +
                    " useGeneratedKeys=\"true\" keyProperty=\"bean.id\">");
            bw.newLine();

            FieldInfo autoIncrementField = null;
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if( fieldInfo.isAutoIncrement()) {
                    autoIncrementField = fieldInfo;
                    break;
                }
            }
            if(autoIncrementField != null) {
                // bw.write("\t\t<selectKey keyProperty=\"bean." + autoIncrementField.getPropertyName() + "\" resultType=\""+autoIncrementField.getJavaType()+
                //         "\" order=\"AFTER\" >");
                bw.write("\t\tINSERT INTO "+ tableInfo.getTableName() +" ");
                // bw.newLine();
                // bw.write("\t\t\tSELECT LAST_INSERT_ID()");
                // bw.newLine();
                // bw.write("\t\t</selectKey>");
                bw.newLine();
            }
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

            //插入或更新
            bw.newLine();
            bw.write("\t<!--插入或更新(匹配有值的字段)-->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdate\" parameterType=\"" + poClass + "\"" +
                    " useGeneratedKeys=\"true\" keyProperty=\"bean.id\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO "+ tableInfo.getTableName() +" ");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\tVALUES");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\tON DUPLICATE KEY UPDATE");
            bw.newLine();

            Map<String,String> KeytempMap = new HashMap<>();
            for( Map.Entry<String, List<FieldInfo> > entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                for(FieldInfo item : fieldInfoList) {
                    KeytempMap.put(item.getPropertyName(), item.getFieldName());
                }
            }

            bw.write("\t\t<trim suffixOverrides=\",\">");
            bw.newLine();
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(KeytempMap.get(fieldInfo.getPropertyName()) != null) {
                    continue;
                }
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = VALUES("+fieldInfo.getFieldName() + "),");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

            //批量插入
            bw.newLine();
            bw.write("\t<!--批量插入-->");
            bw.newLine();
            bw.write("\t<insert id=\"insertBatch\" parameterType=\"" + "java.util.List" + "\"" +
                    " useGeneratedKeys=\"true\" keyProperty=\".id\">");
            bw.newLine();
            StringBuffer insertFieldBuffer = new StringBuffer();
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(fieldInfo.isAutoIncrement()) {
                    continue;
                }
                insertFieldBuffer.append( fieldInfo.getFieldName()).append(",");
            }
            String insertFieldBufferStr = insertFieldBuffer.substring(0,insertFieldBuffer.lastIndexOf(","));
            bw.write("\t\tINSERT INTO "+ tableInfo.getTableName() +"( " +insertFieldBufferStr+" )VALUES");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\" >");
            bw.newLine();

            StringBuffer insertPropertyBuffer = new StringBuffer();
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(fieldInfo.isAutoIncrement()) {
                    continue;
                }
                insertPropertyBuffer.append("#{item." + fieldInfo.getPropertyName() + "},");
            }
            String insertPropertyBufferStr = insertPropertyBuffer.substring(0,insertPropertyBuffer.lastIndexOf(","));
            bw.write("\t\t\t("+insertPropertyBufferStr+")");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

            //批量插入或更新
            bw.newLine();
            bw.write("\t<!--批量插入或更新-->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdateBatch\" parameterType=\"" + "java.util.List" + "\"" +
                    " useGeneratedKeys=\"true\" keyProperty=\".id\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO "+ tableInfo.getTableName() +" ("+insertFieldBufferStr+") VALUES");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\" >");
            bw.newLine();
            bw.write("\t\t\t("+insertPropertyBufferStr+")");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t\tON DUPLICATE KEY UPDATE");
            bw.newLine();
            StringBuffer insertBatchUpdateBuffer = new StringBuffer();
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(fieldInfo.isAutoIncrement()) {
                    continue;
                }
                insertBatchUpdateBuffer.append( fieldInfo.getFieldName()).append(" = VALUES("+fieldInfo.getFieldName() + "),");
            }
            String insertBatchUpdateBufferStr = insertBatchUpdateBuffer.substring(0,insertBatchUpdateBuffer.lastIndexOf(","));
            bw.write("\t\t"+insertBatchUpdateBufferStr);
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

            //根据主键更新
            bw.newLine();
            for(Map.Entry<String, List<FieldInfo> > entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();
                Integer index = 0;
                StringBuilder methodName = new StringBuilder();

                StringBuilder paramNames = new StringBuilder();
                for(FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodName.append(StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName()));
                    paramNames.append(fieldInfo.getPropertyName()+"=#{"+fieldInfo.getPropertyName()+"}");
                    if(index < keyFieldInfoList.size()) {
                        methodName.append("And");
                        paramNames.append("And");
                    }
                }
                //查询
                bw.write("\t<!--根据"+methodName+"查询-->");
                bw.newLine();
                bw.write("\t<select id=\"selectBy" + methodName + "\" resultMap=\"base_result_map\">");
                bw.newLine();
                bw.write("\t\tSELECT <include refid=\"" + BASE_COLUMN_LIST +"\"/> from " + tableInfo.getTableName() + " where " + paramNames);
                bw.newLine();
                bw.write("\t</select>");
                bw.newLine();
                bw.newLine();
                //更新
                bw.write("\t<!--根据"+methodName+"更新-->");
                bw.newLine();
                bw.write("\t<update id=\"updateBy" + methodName + "\" parameterType=\"" + poClass + "\">");
                bw.newLine();
                bw.write("\t\tUPDATE " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\t<set>");
                bw.newLine();
                for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                    if(fieldInfo.isAutoIncrement()) {
                        continue;
                    }
                    bw.write("\t\t\t<if test=\"bean."+fieldInfo.getPropertyName()+"!= null\">");
                    bw.newLine();
                    bw.write("\t\t\t\t"+fieldInfo.getFieldName()+" = #{bean."+fieldInfo.getPropertyName()+"},");
                    bw.newLine();
                    bw.write("\t\t\t</if>");
                    bw.newLine();
                }
                bw.write("\t\t</set>");
                bw.newLine();
                bw.write("\t\twhere "+paramNames);
                bw.newLine();
                bw.write("\t</update>");
                bw.newLine();
                bw.newLine();
                //删除
                bw.write("\t<!--根据"+methodName+"删除-->"  );
                bw.newLine();
                bw.write("\t<delete id=\"deleteBy" + methodName +"\">");
                bw.newLine();
                bw.write("\t\tDELETE FROM " + tableInfo.getTableName() + " where " + paramNames);
                bw.newLine();
                bw.write("\t</delete>");
                bw.newLine();
                bw.newLine();
            }

            bw.write("</mapper>");
            bw.newLine();
            bw.flush();
        } catch (Exception e) {
            logger.error("生成mapper.xml失败", e);
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
