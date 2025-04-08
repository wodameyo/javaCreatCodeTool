package com.easyjava.builder;

import com.easyjava.bean.Constans;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.PropertiesUtils;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BuildTable {
    private static Connection conn = null;
    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);

    private static String SQL_SHOW_TABLE_STATUS = "show table status";

    private static String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";

    private static String SQL_SHOW_TABLE_INDEX = "show index from %s";

    static {
        String diverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String username = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            Class.forName(diverName);
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            logger.error("数据库连接失败",e);
        }

    }
    public static List<TableInfo> getTable(){
        PreparedStatement ps = null;
        ResultSet tableResult = null;

        List<TableInfo> tableInfoList = new ArrayList();
        try {
            ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            tableResult = ps.executeQuery();
            while (tableResult.next()){
                String tableName = tableResult.getString("name");
                String comment = tableResult.getString("comment");
                //logger.info("表名：{}，表注释：{}",tableName,comment);



                String beanName = tableName;
                if(Constans.IGNORE_TABLE_PREFIX) {
                    beanName = tableName.substring(beanName.indexOf('_')+1);
                }
                beanName = processField(beanName,true);

                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName+Constans.SUFFIX_BAEN_QUERY);

                readFieldInfo(tableInfo);

                getKeyIndexInfo(tableInfo);
                // logger.info("tableInfo{}",JsonUtils.convertoObj2Json(tableInfo));
                tableInfoList.add(tableInfo);
            }
        } catch (Exception e) {
            logger.error("获取表失败",e);
        } finally {
            if(tableResult!=null){
                try {
                    tableResult.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        return tableInfoList;
    }

    private static void readFieldInfo(TableInfo tableInfo){
        PreparedStatement ps = null;
        ResultSet fieldResult = null;

        List<FieldInfo> fieldInfoList = new ArrayList();

        List<FieldInfo> extendFieldList = new ArrayList();
        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS,tableInfo.getTableName()));
            fieldResult = ps.executeQuery();

            Boolean haveDateTime = false;
            Boolean haveDate = false;
            Boolean haveBigDecimal = false;

            while (fieldResult.next()) {
                String field = fieldResult.getString("field");
                String type = fieldResult.getString("type");
                String comment = fieldResult.getString("comment");
                String extra = fieldResult.getString("extra");

                if(type.indexOf("(")>0){
                    type = type.substring(0,type.indexOf("("));
                }
                String propertyName = processField(field,false);

                FieldInfo fieldInfo = new FieldInfo();
                fieldInfoList.add(fieldInfo);


                fieldInfo.setFieldName(field);
                fieldInfo.setComment(comment);
                fieldInfo.setSqlType(type);
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra) ? true : false);
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(processJavaType(type));
                //判断是否包含日期时间类型
                if(ArrayUtils.contains(Constans.SQL_DATE_TIME_TYPES,type)){
                    haveDateTime = true;
                }
                //判断是否包含日期类型
                if(ArrayUtils.contains(Constans.SQL_DATE_TYPES,type)) {
                    haveDate = true;
                }
                //判断是否包含BigDecimal类型
                if(ArrayUtils.contains(Constans.SQL_DECIMAL_TYPES,type)) {
                    haveBigDecimal = true;
                }

                if(ArrayUtils.contains(Constans.SQL_STRING_TYPES,type)) {
                    FieldInfo fuzzyField = new FieldInfo();
                    fuzzyField.setJavaType(fieldInfo.getJavaType());
                    fuzzyField.setPropertyName(propertyName + Constans.SUFFIX_BAEN_QUERY_FUZZY);
                    fuzzyField.setFieldName(fieldInfo.getFieldName());
                    fuzzyField.setSqlType(type);
                    extendFieldList.add(fuzzyField);
                }

                if(ArrayUtils.contains(Constans.SQL_DATE_TIME_TYPES,type) || ArrayUtils.contains(Constans.SQL_DATE_TYPES,type)) {
                    FieldInfo timeStartField= new FieldInfo();
                    timeStartField.setJavaType("String");
                    timeStartField.setPropertyName(propertyName+Constans.SUFFIX_BAEN_QUERY_TIME_START);
                    timeStartField.setFieldName(fieldInfo.getFieldName());
                    timeStartField.setSqlType(type);
                    extendFieldList.add(timeStartField);

                    FieldInfo timeEndField= new FieldInfo();
                    timeEndField.setJavaType("String");
                    timeEndField.setPropertyName(propertyName+Constans.SUFFIX_BAEN_QUERY_TIME_END);
                    timeEndField.setFieldName(fieldInfo.getFieldName());
                    timeEndField.setSqlType(type);
                    extendFieldList.add(timeEndField);
                }
            }
            tableInfo.setHaveDateTime(haveDateTime);
            tableInfo.setHaveDate(haveDate);
            tableInfo.setHaveBigDecimal(haveBigDecimal);
            tableInfo.setFieldList(fieldInfoList);
            tableInfo.setExtendFieldList(extendFieldList);
        } catch (Exception e) {
            logger.error("获取表失败",e);
        } finally {
            if(fieldResult !=null){
                try {
                    fieldResult.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }

        }
    }

    private static List<FieldInfo> getKeyIndexInfo(TableInfo tableInfo){
        PreparedStatement ps = null;
        ResultSet fieldResult = null;

        List<FieldInfo> fieldInfoList = new ArrayList();
        try {

            Map<String,FieldInfo> tempMap = new HashMap();
            for(FieldInfo fieldInfo:tableInfo.getFieldList()){
                tempMap.put(fieldInfo.getFieldName(),fieldInfo);
            }

            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_INDEX,tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            while (fieldResult.next()) {
                String keyName = fieldResult.getString("key_name");
                int nonUnique = fieldResult.getInt("non_unique");
                String columnName = fieldResult.getString("column_name");
                if(nonUnique == 1){
                    continue;
                }
                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().get(keyName);
                if(keyFieldList == null){
                    keyFieldList = new ArrayList();
                    tableInfo.getKeyIndexMap().put(keyName,keyFieldList);
                }
                keyFieldList.add(tempMap.get(columnName));
            }
        } catch (Exception e) {
            logger.error("获取索引失败",e);
        } finally {
            if(fieldResult !=null){
                try {
                    fieldResult.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }

        }
        return fieldInfoList;
    }

    private static String processField(String field,Boolean uperCaseFirstLetter){
        StringBuffer sb = new StringBuffer();
        String[] fields = field.split("_");
        sb.append(uperCaseFirstLetter? StringUtils.uperCaseFirstLetter(fields[0]) :fields[0]);
        for(int i =1,len = fields.length;i<len;i++){
            sb.append(StringUtils.uperCaseFirstLetter(fields[i]));
        }
        return sb.toString();
    }

    // 处理java类型
    private static String processJavaType(String type) {
        if(ArrayUtils.contains(Constans.SQL_INTEGER_TYPES, type)) {
            return "Integer";
        } else if(ArrayUtils.contains(Constans.SQL_LONG_TYPES, type)) {
            return "Long";
        } else if(ArrayUtils.contains(Constans.SQL_STRING_TYPES, type)) {
            return "String";
        } else if(ArrayUtils.contains(Constans.SQL_DECIMAL_TYPES, type)) {
            return "BigDecimal";
        } else if(ArrayUtils.contains(Constans.SQL_DATE_TYPES, type) || ArrayUtils.contains(Constans.SQL_DATE_TIME_TYPES, type)) {
            return "Date";
        } else {
            throw new RuntimeException("无法识别的类型");
        }
    }
}
