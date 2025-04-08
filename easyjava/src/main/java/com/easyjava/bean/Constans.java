package com.easyjava.bean;

import com.easyjava.utils.PropertiesUtils;

import java.util.Properties;

public class Constans {
    public static String AUTHER_COMMENT ;

    public static Boolean IGNORE_TABLE_PREFIX ;

    public static String SUFFIX_BAEN_QUERY;
    //查询对象后缀
    public static String SUFFIX_BAEN_QUERY_FUZZY;
    //日期起止
    public static String SUFFIX_BAEN_QUERY_TIME_START;
    public static String SUFFIX_BAEN_QUERY_TIME_END;
    //mappers后缀
    public static String SUFFIX_MAPPERS;

    //需要忽略的属性
    public static String IGNORE_BEAN_TOJSON_FIELD;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;

    //日期序列化和反序列化
    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;

    public static String BEAN_DATE_PARSE_EXPRESSION;
    public static String BEAN_DATE_PARSE_CLASS;

    public static String PATH_JAVA = "java";

    public static String PATH_RESOURCE = "resources";

    public static String PACKAGE_BASE;

    public static String PACKAGE_PO;

    public static String PACKAGE_UTILS;

    public static String PACKAGE_ENUMS;

    public static String PACKAGE_QUERY;

    public static String PACKAGE_MAPPERS;

    public static String PATH_BASE;

    public static String PATH_PO;

    public static String PATH_UTILS;

    public static String PATH_ENUMS;

    public static String PATH_QUERY;

    public static String PATH_MAPPERS;

    public static String PATH_MAPPER_XML;

    static {
        AUTHER_COMMENT = PropertiesUtils.getString("auther.comment");

        IGNORE_BEAN_TOJSON_FIELD = PropertiesUtils.getString("ignore.bean.tojson.field");
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getString("ignore.bean.tojson.expression");
        IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getString("ignore.bean.tojson.class");

        //日期序列化，反序列化
        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");
        BEAN_DATE_PARSE_EXPRESSION = PropertiesUtils.getString("bean.date.parse.expression");
        BEAN_DATE_PARSE_CLASS = PropertiesUtils.getString("bean.date.parse.class");

        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BAEN_QUERY = PropertiesUtils.getString("suffix.bean.query");

        SUFFIX_BAEN_QUERY_FUZZY = PropertiesUtils.getString("suffix.bean.query.fuzzy");

        SUFFIX_BAEN_QUERY_TIME_START = PropertiesUtils.getString("suffix.bean.query.time.start");
        SUFFIX_BAEN_QUERY_TIME_END = PropertiesUtils.getString("suffix.bean.query.time.end");

        SUFFIX_MAPPERS = PropertiesUtils.getString("suffix.mappers");

        PACKAGE_BASE = PropertiesUtils.getString("package.base");
        //PO
        PACKAGE_PO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.po");
        PACKAGE_UTILS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.utils");
        PACKAGE_ENUMS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.enums");
        PACKAGE_QUERY = PACKAGE_BASE + "." + PropertiesUtils.getString("package.query");
        PACKAGE_MAPPERS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.mappers");

        PATH_BASE = PropertiesUtils.getString("path.base");
        PATH_BASE = PATH_BASE + PATH_JAVA;

        PATH_PO = PATH_BASE + "/" + PACKAGE_PO.replace(".", "/");
        PATH_UTILS = PATH_BASE + "/" + PACKAGE_UTILS.replace(".", "/");
        PATH_ENUMS = PATH_BASE + "/" + PACKAGE_ENUMS.replace(".", "/");
        PATH_QUERY = PATH_BASE + "/" + PACKAGE_QUERY.replace(".", "/");
        PATH_MAPPERS = PATH_BASE + "/" + PACKAGE_MAPPERS.replace(".", "/");

        PATH_MAPPER_XML = PropertiesUtils.getString("path.base") + PATH_RESOURCE +"/"+ PACKAGE_MAPPERS.replace(".", "/");
    }

    public static String[] SQL_DATE_TIME_TYPES = new String[]{"datetime","timestamp"};

    public static String[] SQL_DATE_TYPES = new String[]{"date"};

    public static String[] SQL_DECIMAL_TYPES = new String[]{"decimal","double","float"};

    public static String[] SQL_STRING_TYPES = new String[]{"char","varchar","text","mediumtext","longtext"};

    public static String[] SQL_INTEGER_TYPES = new String[]{"int","tinyint"};

    public static String[] SQL_LONG_TYPES = new String[]{"bigint"};

    public static void main(String[] args) {
        System.out.println(PACKAGE_PO);
        System.out.println(PATH_PO);
        System.out.println(PATH_MAPPER_XML);
    }
}
