import com.easyjava.RunDemoApplication;
import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import com.easyjava.mappers.UserInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootTest(classes = RunDemoApplication.class)
public class MapperTest {
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Test
    public  void selectList(){

        UserInfoQuery query = new UserInfoQuery();
        query.setUserNameFuzzy("哈");
        List<UserInfo> userInfoList = userInfoMapper.selectList(query);
        for(UserInfo userInfo : userInfoList) {
            System.out.println(userInfo);
        }
    }
    @Test
    public void insert() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("哈呀哈机密");
        userInfo.setProductName("哈密");
        userInfo.setTaotle(514);
        userInfo.setStatus(1);
        userInfo.setCreateDate(new java.util.Date());
        userInfo.setCreateTime(new java.util.Date());
        userInfo.setIsDel(0);
        userInfoMapper.insert(userInfo);
        System.out.println(userInfo.getId());
    }
    @Test
    public void insertOrUpdate() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(3);
        userInfo.setUserName("米呀密集哈");
        userInfo.setProductName("测试01");
        userInfo.setTaotle(514);
        this.userInfoMapper.insertOrUpdate(userInfo);
        System.out.println(userInfo.getId());
    }
}
