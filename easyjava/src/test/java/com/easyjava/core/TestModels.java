package com.easyjava.core;

import com.easyjava.core.config.GeneratorConfig;
import com.easyjava.core.model.ColumnModel;
import com.easyjava.core.model.IndexModel;
import com.easyjava.core.model.TableModel;
import com.easyjava.core.naming.NamingStrategy;
import com.easyjava.core.type.TypeMapper;

import java.nio.file.Path;
import java.util.List;

/**
 * 测试用的固定表模型与配置。
 */
public final class TestModels {

    private TestModels() {
    }

    public static GeneratorConfig config(Path backendDir, Path resourcesDir) {
        return new GeneratorConfig(
                new GeneratorConfig.DataSource("com.mysql.cj.jdbc.Driver",
                        "jdbc:mysql://localhost:3306/test", "root", ""),
                new GeneratorConfig.Output(backendDir, resourcesDir, null,
                        GeneratorConfig.OverwritePolicy.BACKUP),
                new GeneratorConfig.Table(List.of(), List.of(), List.of("ts_")),
                new GeneratorConfig.Packages("com.easyjava",
                        "entity.po", "entity.query", "entity.vo", "utils", "enums",
                        "exception", "mappers", "service", "service.impl", "controller"),
                new GeneratorConfig.Backend(false, GeneratorConfig.DateType.LEGACY, true,
                        List.of("status", "taotle")),
                new GeneratorConfig.Frontend(false),
                "tester");
    }

    public static TableModel userInfo() {
        ColumnModel id = column("id", "int", "这是id", true, true, true, false);
        ColumnModel userName = column("user_name", "varchar(32)", "用户昵称", false, false, true, false);
        ColumnModel productName = column("product_name", "varchar(64)", "商品名称", false, false, false, false);
        ColumnModel createTime = column("create_time", "datetime", "创建时间", false, false, false, false);
        ColumnModel createDate = column("create_date", "date", "创建日期", false, false, false, false);
        ColumnModel status = column("status", "int", "身份状态", false, false, true, true);
        ColumnModel taotle = column("taotle", "int", "硬币数量", false, false, false, true);
        ColumnModel isDel = column("is_del", "tinyint(1)", "是否删除", false, false, false, false);

        List<ColumnModel> columns = List.of(id, userName, productName, createTime, createDate,
                status, taotle, isDel);
        List<IndexModel> indexes = List.of(
                new IndexModel("PRIMARY", true, List.of(id)),
                new IndexModel("uk_user_name", false, List.of(userName)),
                new IndexModel("uk_name_status", false, List.of(userName, status))
        );
        return new TableModel("ts_user_info", "这是一个用户表", "UserInfo", columns, indexes, true);
    }

    private static ColumnModel column(String name, String sqlType, String comment,
                                      boolean autoIncrement, boolean primaryKey, boolean keyColumn,
                                      boolean jsonIgnore) {
        return new ColumnModel(
                name,
                NamingStrategy.toPropertyName(name),
                TypeMapper.javaType(sqlType, GeneratorConfig.DateType.LEGACY),
                TypeMapper.javaImport(sqlType, GeneratorConfig.DateType.LEGACY),
                TypeMapper.normalize(sqlType),
                sqlType,
                comment,
                autoIncrement,
                primaryKey,
                keyColumn,
                true,
                jsonIgnore,
                TypeMapper.categorize(sqlType),
                true
        );
    }
}
