package com.easyjava.core.engine;

import com.easyjava.core.TestModels;
import com.easyjava.core.config.GeneratorConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodeGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    void generatesAllFiles() {
        GeneratorConfig config = TestModels.config(tempDir.resolve("java"), tempDir.resolve("resources"));
        GenerationPlan plan = new CodeGenerator(config).generate(List.of(TestModels.userInfo()));

        assertEquals(19, plan.getFileCount());
        assertTrue(content(plan, "java/com/easyjava/entity/po/UserInfo.java").contains("public class UserInfo"));
        assertTrue(content(plan, "java/com/easyjava/entity/query/BaseQuery.java")
                .contains("public class BaseQuery"));
        assertTrue(content(plan, "java/com/easyjava/controller/AGlobalExceptionHandlerController.java")
                .contains("jakarta.servlet.http.HttpServletRequest"));
    }

    @Test
    void rendersPoWithDateAnnotationsAndIgnoreFields() {
        String po = content(plan(), "java/com/easyjava/entity/po/UserInfo.java");

        assertTrue(po.contains("package com.easyjava.entity.po;"));
        assertTrue(po.contains("import org.springframework.format.annotation.DateTimeFormat;"));
        assertTrue(po.contains("import com.fasterxml.jackson.annotation.JsonFormat;"));
        assertTrue(po.contains("import com.easyjava.utils.DateUtils;"));
        assertTrue(po.contains("@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")"));
        assertTrue(po.contains("@JsonIgnore"));
        assertTrue(po.contains("private Date createTime;"));
        assertTrue(po.contains("DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())"));
        assertTrue(po.contains("public Date getCreateTime()"));
    }

    @Test
    void rendersQueryWithFuzzyAndRangeFields() {
        String query = content(plan(), "java/com/easyjava/entity/query/UserInfoQuery.java");

        assertTrue(query.contains("public class UserInfoQuery extends BaseQuery"));
        assertTrue(query.contains("private String userNameFuzzy;"));
        assertTrue(query.contains("private String createTimeStart;"));
        assertTrue(query.contains("private String createTimeEnd;"));
        assertTrue(query.contains("public void setUserNameFuzzy(String userNameFuzzy)"));
    }

    @Test
    void rendersMapperWithConcreteGenericsAndIndexMethods() {
        String mapper = content(plan(), "java/com/easyjava/mappers/UserInfoMapper.java");

        assertTrue(mapper.contains("public interface UserInfoMapper extends BaseMapper<UserInfo, UserInfoQuery>"));
        assertTrue(mapper.contains("import com.easyjava.entity.po.UserInfo;"));
        assertTrue(mapper.contains("import com.easyjava.entity.query.UserInfoQuery;"));
        assertTrue(mapper.contains("Integer updateByUserNameAndStatus(@Param(\"bean\") UserInfo bean, "
                + "@Param(\"userName\") String userName, @Param(\"status\") Integer status);"));
        assertFalse(mapper.contains(";;"));
    }

    @Test
    void rendersMapperXmlWithAliasPaginationAndCompositeKey() {
        String xml = content(plan(), "resources/com/easyjava/mappers/UserInfoMapper.xml");

        assertTrue(xml.contains("namespace=\"com.easyjava.mappers.UserInfoMapper\""));
        assertTrue(xml.contains("FROM ts_user_info t"));
        assertTrue(xml.contains("limit #{query.simplePage.start},#{query.simplePage.pageSize}"));
        assertTrue(xml.contains("order by ${query.orderBy}"));
        assertTrue(xml.contains("useGeneratedKeys=\"true\" keyProperty=\"bean.id\""));
        assertTrue(xml.contains("user_name=#{userName} and status=#{status}"));
        assertTrue(xml.contains("and t.user_name = #{query.userName}"));
        assertTrue(xml.contains("and t.user_name like concat('%', #{query.userNameFuzzy}, '%')"));
        assertTrue(xml.contains("<id column=\"id\" property=\"id\" />"));
    }

    @Test
    void rendersServiceControllerAndBaseClasses() {
        GenerationPlan plan = plan();

        String service = content(plan, "java/com/easyjava/service/UserInfoService.java");
        assertTrue(service.contains("PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query);"));

        String serviceImpl = content(plan, "java/com/easyjava/service/impl/UserInfoServiceImpl.java");
        assertTrue(serviceImpl.contains("@Service(\"userInfoService\")"));
        assertTrue(serviceImpl.contains("new SimplePage(query.getPageNo(), count, pageSize)"));
        assertTrue(serviceImpl.contains("@Transactional(rollbackFor = Exception.class)"));

        String controller = content(plan, "java/com/easyjava/controller/UserInfoController.java");
        assertTrue(controller.contains("@RequestMapping(\"/userInfo\")"));
        assertTrue(controller.contains("@RequestBody UserInfo bean"));

        String baseQuery = content(plan, "java/com/easyjava/entity/query/BaseQuery.java");
        assertTrue(baseQuery.contains("非法的排序参数"));
    }

    private GenerationPlan plan() {
        GeneratorConfig config = TestModels.config(tempDir.resolve("java"), tempDir.resolve("resources"));
        return new CodeGenerator(config).generate(List.of(TestModels.userInfo()));
    }

    private static String content(GenerationPlan plan, String logicalPath) {
        return plan.getFiles().stream()
                .filter(file -> file.getLogicalPath().equals(logicalPath))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未生成文件: " + logicalPath))
                .getContent();
    }
}
