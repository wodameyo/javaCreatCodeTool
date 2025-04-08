package com.easyjava.builder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.easyjava.bean.Constans;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class BuildBase {

    private static final Logger logger = LoggerFactory.getLogger(BuildBase.class);


    public static void execute() {
        List<String> headrInfoList = new ArrayList<>();

        //生成日期枚举
        headrInfoList.add("package "+Constans.PACKAGE_ENUMS+";");
        build(headrInfoList,"DateTimePatternEnum", Constans.PATH_ENUMS);

        headrInfoList.clear();
        headrInfoList.add("package "+Constans.PACKAGE_UTILS+";");
        build(headrInfoList ,"DateUtils", Constans.PATH_UTILS);

        //生成baseMapper
        headrInfoList.clear();
        headrInfoList.add("package "+Constans.PACKAGE_MAPPERS+";");
        build(headrInfoList,"BaseMapper", Constans.PATH_MAPPERS);

        //生成PageSize枚举
        headrInfoList.clear();
        headrInfoList.add("package "+Constans.PACKAGE_ENUMS+";");
        build(headrInfoList,"PageSize", Constans.PATH_ENUMS);

        //生成SimplePage
        headrInfoList.clear();
        headrInfoList.add("package "+Constans.PACKAGE_QUERY+";");
        //导入PageSize
        headrInfoList.add("import "+Constans.PACKAGE_ENUMS+".PageSize;");
        build(headrInfoList,"SimplePage", Constans.PATH_QUERY);

        //生成BaseQuery
        headrInfoList.clear();
        headrInfoList.add("package "+Constans.PACKAGE_QUERY+";");
        build(headrInfoList,"BaseQuery", Constans.PATH_QUERY);
    }

    private static void build(List<String> headrInfoList, String fileName, String outputPath) {
        File folder = new File(outputPath);
        if(!folder.exists()) {
            folder.mkdirs();
        }

        File javaFile = new File(folder, fileName + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;

        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader br = null;

        try {
            out = new FileOutputStream(javaFile);
            outw = new OutputStreamWriter(out,"utf8");
            bw = new BufferedWriter(outw);

            String templatePath = BuildBase.class.getClassLoader().getResource("template/"+fileName+".txt").getPath();
            in = new FileInputStream(templatePath);
            inr = new InputStreamReader(in,"utf8");
            br = new BufferedReader(inr);

            for(String headrInfo : headrInfoList) {
                bw.write(headrInfo);
                bw.newLine();
                if(headrInfo.contains("package ")) {
                    bw.newLine();
                }
            }

            String lineInfo = null;
            while((lineInfo=br.readLine())!=null) {
                bw.write(lineInfo);
                bw.newLine();
            }
            bw.flush();

        } catch (Exception e) {
            logger.error("生成基础类失败: 文件名={}，输出路径={}",fileName,e);
        } finally {
            if (bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outw != null){
                try {
                    outw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inr != null){
                try {
                    inr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
