package com.easyjava.builder;

import com.easyjava.bean.Constans;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.DateUtils;

import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BuildComment {
    public static void createClassComment(BufferedWriter bw,String classComment) throws Exception{
        bw.write("/**\n");
        bw.write(" * @Description:"+classComment+"\n");
        bw.write(" * @Author:"+ Constans.AUTHER_COMMENT +"\n");
        bw.write(" * @Date:"+ DateUtils.format(new Date(), DateUtils.YYYY_MM_DD_HH_MM_SS) +"\n");
        bw.write(" */\n");
    }

    public static void createFieldComment(BufferedWriter bw,String fieldComment) throws Exception{
        bw.write("    /**\n");
        bw.write("     * "+(fieldComment==null?"":fieldComment)+"\n");
        bw.write("     */\n");
    }

    public static void createMethodComment(TableInfo tableInfo) {
    }
}
