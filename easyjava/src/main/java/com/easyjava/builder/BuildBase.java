package com.easyjava.builder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

        //生成PaginationResultVO
        headrInfoList.clear();
        headrInfoList.add("package "+Constans.PACKAGE_VO+";");
        build(headrInfoList,"PaginationResultVO", Constans.PATH_VO);

        //生成Exception
        headrInfoList.clear();
        headrInfoList.add("package "+Constans.PACKAGE_EXCEPTION+";");
        headrInfoList.add("import "+Constans.PACKAGE_ENUMS+".ResponseCodeEnum;");
        build(headrInfoList,"BusinessException", Constans.PATH_EXCEPTION);

        //生成ResponseCodeEnum
        headrInfoList.clear();
        headrInfoList.add("package "+Constans.PACKAGE_ENUMS+";");
        build(headrInfoList,"ResponseCodeEnum", Constans.PATH_ENUMS);

        //生成ResponseVO
        headrInfoList.clear();
        headrInfoList.add("package "+Constans.PACKAGE_VO+";");
        build(headrInfoList,"ResponseVO", Constans.PATH_VO);

        //生成BaseConontroller
        headrInfoList.clear();
        headrInfoList.add("package "+Constans.PACKAGE_CONTROLLER+";");
        headrInfoList.add("import "+Constans.PACKAGE_ENUMS+".ResponseCodeEnum;");
        headrInfoList.add("import "+Constans.PACKAGE_VO+".ResponseVO;");
        build(headrInfoList,"ABaseController", Constans.PATH_CONTROLLER);

        //生成GlobalExceptionHandlerController
        headrInfoList.clear();
        headrInfoList.add("package "+Constans.PACKAGE_CONTROLLER+";");
        headrInfoList.add("import "+Constans.PACKAGE_EXCEPTION+".BusinessException;");
        headrInfoList.add("import "+Constans.PACKAGE_VO+".ResponseVO;");
        headrInfoList.add("import "+Constans.PACKAGE_ENUMS+".ResponseCodeEnum;");
        build(headrInfoList,"AGlobalExceptionHandlerController", Constans.PATH_CONTROLLER);
    }

    private static void build(List<String> headrInfoList, String fileName, String outputPath) {
        File folder = new File(outputPath);
        if(!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("无法创建输出目录: " + folder.getAbsolutePath());
        }

        File javaFile = new File(folder, fileName + ".java");
        String resourceName = "template/" + fileName + ".txt";

        try (InputStream in = BuildBase.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalStateException("找不到模板文件: " + resourceName);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                 BufferedWriter bw = Files.newBufferedWriter(javaFile.toPath(), StandardCharsets.UTF_8)) {

                for (String headrInfo : headrInfoList) {
                    bw.write(headrInfo);
                    bw.newLine();
                    if (headrInfo.contains("package ")) {
                        bw.newLine();
                    }
                }

                String lineInfo;
                while ((lineInfo = br.readLine()) != null) {
                    bw.write(lineInfo);
                    bw.newLine();
                }
                bw.flush();
            }
        } catch (Exception e) {
            throw new IllegalStateException("生成基础类失败: 文件名=" + fileName + "，输出路径=" + outputPath, e);
        }
    }
}
