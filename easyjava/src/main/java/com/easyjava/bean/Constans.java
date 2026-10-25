package com.easyjava.bean;

import com.easyjava.utils.PropertiesUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    public static String PACKAGE_BASE;

    public static String PACKAGE_PO;

    public static String PACKAGE_UTILS;

    public static String PACKAGE_EXCEPTION;

    public static String PACKAGE_ENUMS;

    public static String PACKAGE_QUERY;

    public static String PACKAGE_VO;

    public static String PACKAGE_MAPPERS;

    public static String PACKAGE_SERVICE;

    public static String PACKAGE_SERVICE_IMPL;

    public static String PACKAGE_CONTROLLER;

    public static String PATH_BASE;

    public static String PATH_PO;

    public static String PATH_UTILS;

    public static String PATH_EXCEPTION;

    public static String PATH_ENUMS;

    public static String PATH_QUERY;

    public static String PATH_VO;

    public static String PATH_MAPPERS;

    public static String PATH_MAPPER_XML;

    public static String PATH_SERVICE;

    public static String PATH_SERVICE_IMPL;

    public static String PATH_CONTROLLER;

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
        PACKAGE_EXCEPTION = PACKAGE_BASE + "." + PropertiesUtils.getString("package.exception");
        PACKAGE_ENUMS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.enums");
        PACKAGE_QUERY = PACKAGE_BASE + "." + PropertiesUtils.getString("package.query");
        PACKAGE_VO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.vo");
        PACKAGE_MAPPERS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.mappers");
        PACKAGE_SERVICE = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service");
        PACKAGE_SERVICE_IMPL = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service.impl");
        PACKAGE_CONTROLLER = PACKAGE_BASE + "." + PropertiesUtils.getString("package.controller");

        // path.base 为目标项目根目录，源码与资源统一从根目录推导
        Path projectRoot = Paths.get(PropertiesUtils.getString("path.base")).toAbsolutePath().normalize();
        PATH_BASE = projectRoot.resolve("src/main/java").toString();

        PATH_PO = Paths.get(PATH_BASE, PACKAGE_PO.replace(".", "/")).toString();
        PATH_UTILS = Paths.get(PATH_BASE, PACKAGE_UTILS.replace(".", "/")).toString();
        PATH_EXCEPTION = Paths.get(PATH_BASE, PACKAGE_EXCEPTION.replace(".", "/")).toString();
        PATH_ENUMS = Paths.get(PATH_BASE, PACKAGE_ENUMS.replace(".", "/")).toString();
        PATH_QUERY = Paths.get(PATH_BASE, PACKAGE_QUERY.replace(".", "/")).toString();
        PATH_VO = Paths.get(PATH_BASE, PACKAGE_VO.replace(".", "/")).toString();
        PATH_MAPPERS = Paths.get(PATH_BASE, PACKAGE_MAPPERS.replace(".", "/")).toString();
        PATH_SERVICE = Paths.get(PATH_BASE, PACKAGE_SERVICE.replace(".", "/")).toString();
        PATH_SERVICE_IMPL = Paths.get(PATH_BASE, PACKAGE_SERVICE_IMPL.replace(".", "/")).toString();
        PATH_CONTROLLER = Paths.get(PATH_BASE, PACKAGE_CONTROLLER.replace(".", "/")).toString();

        PATH_MAPPER_XML = projectRoot.resolve("src/main/resources")
                .resolve(PACKAGE_MAPPERS.replace(".", "/")).toString();
    }

    public static String[] SQL_DATE_TIME_TYPES = new String[]{"datetime","timestamp"};

    public static String[] SQL_DATE_TYPES = new String[]{"date"};

    public static String[] SQL_TIME_TYPES = new String[]{"time"};

    public static String[] SQL_YEAR_TYPES = new String[]{"year"};

    public static String[] SQL_DECIMAL_TYPES = new String[]{"decimal","numeric","double","float","real"};

    public static String[] SQL_STRING_TYPES = new String[]{"char","varchar","text","tinytext","mediumtext","longtext","json","enum","set"};

    public static String[] SQL_INTEGER_TYPES = new String[]{"int","tinyint","smallint","mediumint"};

    public static String[] SQL_LONG_TYPES = new String[]{"bigint"};

    public static String[] SQL_BOOLEAN_TYPES = new String[]{"bit","bool","boolean"};

    public static String[] SQL_BINARY_TYPES = new String[]{"binary","varbinary","blob","tinyblob","mediumblob","longblob"};

    public static void main(String[] args) {
        System.out.println(PACKAGE_PO);
        System.out.println(PATH_PO);
        System.out.println(PATH_MAPPER_XML);
    }
}
