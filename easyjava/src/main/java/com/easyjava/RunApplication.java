package com.easyjava;

import com.easyjava.bean.TableInfo;
import com.easyjava.builder.*;

import java.util.List;

public class RunApplication {
    public static void main(String[] args) {

        List<TableInfo> tableInfoList = BuildTable.getTable();

        BuildBase.execute();

        for(TableInfo tableInfo : tableInfoList){
            // BuildPo.excute(tableInfo);

            BuildQuery.excute(tableInfo);

            BuildMapper.execute(tableInfo);

            BuildMapperXml.execute(tableInfo);

            BuildService.excute(tableInfo);

            BuildServiceImpl.excute(tableInfo);
        }

    }
}
