package com.easyjava.utils;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class DateUtils {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static String format(Date date,String pattern){
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date parse(String date,String pattern) {
        try{
            new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
