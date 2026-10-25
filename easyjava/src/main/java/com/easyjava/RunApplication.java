package com.easyjava;

import com.easyjava.bean.TableInfo;
import com.easyjava.builder.BuildBase;
import com.easyjava.builder.BuildController;
import com.easyjava.builder.BuildMapper;
import com.easyjava.builder.BuildMapperXml;
import com.easyjava.builder.BuildPo;
import com.easyjava.builder.BuildQuery;
import com.easyjava.builder.BuildService;
import com.easyjava.builder.BuildServiceImpl;
import com.easyjava.builder.BuildTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RunApplication {

    private static final Logger logger = LoggerFactory.getLogger(RunApplication.class);

    public static void main(String[] args) {
        logger.info("开始生成代码...");

        List<TableInfo> tableInfoList = BuildTable.getTable();
        if (tableInfoList.isEmpty()) {
            logger.warn("未读取到任何表，请检查数据库连接与库名配置");
            return;
        }

        BuildBase.execute();

        for (TableInfo tableInfo : tableInfoList) {
            logger.info("正在生成表: {}", tableInfo.getTableName());
            BuildPo.excute(tableInfo);
            BuildQuery.excute(tableInfo);
            BuildMapper.execute(tableInfo);
            BuildMapperXml.execute(tableInfo);
            BuildService.excute(tableInfo);
            BuildServiceImpl.excute(tableInfo);
            BuildController.excute(tableInfo);
        }
        logger.info("代码生成完毕，共处理 {} 张表", tableInfoList.size());
    }
}
