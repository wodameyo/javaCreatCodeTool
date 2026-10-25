package com.easyjava.builder;

import com.easyjava.bean.Constans;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratedCodeTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldGenerateBaseClassesForSpringBoot3() throws Exception {
        setBasePaths();

        BuildBase.execute();

        String exceptionHandler = Files.readString(
                Path.of(Constans.PATH_CONTROLLER, "AGlobalExceptionHandlerController.java"));
        assertTrue(exceptionHandler.contains("jakarta.servlet.http.HttpServletRequest"));
        assertTrue(exceptionHandler.contains("org.springframework.validation.BindException"));
        assertFalse(exceptionHandler.contains("javax.servlet"));
        assertFalse(exceptionHandler.contains("java.net.BindException"));

        String pageSize = Files.readString(Path.of(Constans.PATH_ENUMS, "PageSize.java"));
        assertTrue(pageSize.contains("SIZE40(40)"));

        String baseQuery = Files.readString(Path.of(Constans.PATH_QUERY, "BaseQuery.java"));
        assertTrue(baseQuery.contains("非法的排序参数"));

        String simplePage = Files.readString(Path.of(Constans.PATH_QUERY, "SimplePage.java"));
        assertTrue(simplePage.contains("this.pageSize == null"));
    }

    @Test
    void shouldGenerateCompilablePo() throws Exception {
        Constans.PATH_PO = tempDir.resolve("po").toString();
        TableInfo tableInfo = sampleTable();

        BuildPo.excute(tableInfo);

        String source = Files.readString(Path.of(Constans.PATH_PO, "UserInfo.java"));
        assertFalse(source.contains(";;"));
        assertTrue(source.contains("import com.fasterxml.jackson.annotation.JsonIgnore;"));
        assertTrue(source.contains("return ("));
        assertTrue(source.contains("\\\""));
        assertTrue(source.contains(", "));
    }

    @Test
    void shouldGenerateTypedMapper() throws Exception {
        Constans.PATH_MAPPERS = tempDir.resolve("mappers").toString();

        BuildMapper.execute(sampleTable());

        String source = Files.readString(Path.of(Constans.PATH_MAPPERS, "UserInfoMapper.java"));
        assertTrue(source.contains("import com.easyjava.entity.po.UserInfo;"));
        assertTrue(source.contains("import com.easyjava.entity.query.UserInfoQuery;"));
        assertTrue(source.contains("extends BaseMapper<UserInfo, UserInfoQuery>"));
        assertFalse(source.contains("<T, P>"));
        assertTrue(source.contains("UserInfo selectByUserId"));
        assertTrue(source.contains("UserInfo selectByUserName"));
        assertTrue(source.contains("@Param(\"bean\") UserInfo bean"));
    }

    @Test
    void shouldGenerateCorrectMapperXml() throws Exception {
        Constans.PATH_MAPPER_XML = tempDir.resolve("mappers").toString();

        BuildMapperXml.execute(sampleTable());

        String xml = Files.readString(Path.of(Constans.PATH_MAPPER_XML, "UserInfoMapper.xml"));
        assertTrue(xml.contains("FROM user_info t<include"));
        assertTrue(xml.contains("SELECT count(1) FROM user_info t<include"));
        assertTrue(xml.contains("limit #{query.simplePage.start},#{query.simplePage.pageSize}"));
        assertTrue(xml.contains("order by ${query.orderBy}"));
        assertTrue(xml.contains("keyProperty=\"bean.userId\""));
        assertTrue(xml.contains("keyProperty=\"userId\""));
        assertTrue(xml.contains("and t.user_name like"));
        assertTrue(xml.contains("and t.create_time >= str_to_date"));
        assertTrue(xml.contains("and t.create_time &lt; date_sub"));
        assertTrue(xml.contains("<id column=\"user_id\" property=\"userId\" />"));
        assertTrue(xml.contains("&quot;"));
        assertTrue(xml.contains("user_name=#{userName} and status=#{status}"));
        assertFalse(xml.contains("#("));
        assertFalse(xml.contains("$("));
    }

    @Test
    void shouldGenerateServiceImplWithPaginationResult() throws Exception {
        Constans.PATH_SERVICE = tempDir.resolve("service").toString();
        Constans.PATH_SERVICE_IMPL = tempDir.resolve("service/impl").toString();

        BuildService.excute(sampleTable());
        BuildServiceImpl.excute(sampleTable());

        String source = Files.readString(Path.of(Constans.PATH_SERVICE_IMPL, "UserInfoServiceImpl.java"));
        assertTrue(source.contains("private UserInfoMapper userInfoMapper;"));
        assertTrue(source.contains("return new PaginationResultVO<UserInfo>("));
        assertTrue(source.contains("query.setSimplePage(page);"));
        assertFalse(source.contains("return null;"));
        assertFalse(source.contains("PaginationResultVO result"));
    }

    @Test
    void shouldGenerateControllerWithRequestBody() throws Exception {
        Constans.PATH_CONTROLLER = tempDir.resolve("controller").toString();

        BuildController.excute(sampleTable());

        String source = Files.readString(Path.of(Constans.PATH_CONTROLLER, "UserInfoController.java"));
        assertTrue(source.contains("@RequestBody UserInfo bean"));
        assertTrue(source.contains("private UserInfoService userInfoService;"));
        assertTrue(source.contains("@RequestMapping(\"/getUserInfoByUserId\")"));
        assertFalse(source.contains("UserInfoService UserInfoService"));
    }

    private void setBasePaths() {
        Constans.PATH_ENUMS = tempDir.resolve("enums").toString();
        Constans.PATH_UTILS = tempDir.resolve("utils").toString();
        Constans.PATH_EXCEPTION = tempDir.resolve("exception").toString();
        Constans.PATH_VO = tempDir.resolve("vo").toString();
        Constans.PATH_QUERY = tempDir.resolve("query").toString();
        Constans.PATH_MAPPERS = tempDir.resolve("mappers").toString();
        Constans.PATH_CONTROLLER = tempDir.resolve("controller").toString();
    }

    private TableInfo sampleTable() {
        FieldInfo id = field("user_id", "userId", "int", "Integer", true, "用户ID");
        FieldInfo userName = field("user_name", "userName", "varchar", "String", false, "用户\"名");
        FieldInfo status = field("status", "status", "tinyint", "Integer", false, "状态");
        FieldInfo createTime = field("create_time", "createTime", "datetime", "Date", false, "创建时间");

        List<FieldInfo> fields = new ArrayList<>();
        fields.add(id);
        fields.add(userName);
        fields.add(status);
        fields.add(createTime);

        List<FieldInfo> extendFields = new ArrayList<>();
        FieldInfo fuzzy = field("user_name", "userNameFuzzy", "varchar", "String", false, null);
        extendFields.add(fuzzy);
        extendFields.add(field("create_time", "createTimeStart", "datetime", "String", false, null));
        extendFields.add(field("create_time", "createTimeEnd", "datetime", "String", false, null));

        Map<String, List<FieldInfo>> keyIndexMap = new LinkedHashMap<>();
        keyIndexMap.put("PRIMARY", List.of(id));
        keyIndexMap.put("uk_user_name", List.of(userName));
        keyIndexMap.put("uk_user_status", List.of(userName, status));

        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName("user_info");
        tableInfo.setBeanName("UserInfo");
        tableInfo.setBeanParamName("UserInfoQuery");
        tableInfo.setComment("用户信息");
        tableInfo.setFieldList(fields);
        tableInfo.setExtendFieldList(extendFields);
        tableInfo.setKeyIndexMap(keyIndexMap);
        tableInfo.setHaveDateTime(true);
        return tableInfo;
    }

    private FieldInfo field(String fieldName, String propertyName, String sqlType, String javaType,
                            boolean autoIncrement, String comment) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setFieldName(fieldName);
        fieldInfo.setPropertyName(propertyName);
        fieldInfo.setSqlType(sqlType);
        fieldInfo.setJavaType(javaType);
        fieldInfo.setAutoIncrement(autoIncrement);
        fieldInfo.setComment(comment);
        return fieldInfo;
    }
}
