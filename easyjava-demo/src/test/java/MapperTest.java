import com.easyjava.RunDemoApplication;
import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import com.easyjava.mappers.UserInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = RunDemoApplication.class)
public class MapperTest {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Test
    public void selectList() {
        UserInfoQuery query = new UserInfoQuery();
        List<UserInfo> userInfoList = userInfoMapper.selectList(query);
        assertNotNull(userInfoList);
    }

    @Test
    public void crudRoundTrip() {
        String userName = "smoke_" + UUID.randomUUID().toString().substring(0, 8);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(userName);
        userInfo.setProductName("smoke");
        userInfo.setCreateTime(new Date());
        userInfo.setCreateDate(new Date());
        userInfo.setIsDel(0);

        assertEquals(1, userInfoMapper.insert(userInfo));
        assertNotNull(userInfo.getId());

        UserInfo loaded = userInfoMapper.selectByUserName(userName);
        assertNotNull(loaded);
        assertEquals(userName, loaded.getUserName());

        UserInfo update = userInfoMapper.selectById(loaded.getId());
        update.setProductName("smoke-updated");
        assertEquals(1, userInfoMapper.updateById(update, update.getId()));

        UserInfo updated = userInfoMapper.selectById(loaded.getId());
        assertEquals("smoke-updated", updated.getProductName());

        assertEquals(1, userInfoMapper.deleteById(update.getId()));
        assertNull(userInfoMapper.selectById(update.getId()));
    }
}
