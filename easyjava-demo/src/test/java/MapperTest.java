import com.easyjava.RunDemoApplication;
import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import com.easyjava.mappers.UserInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;

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
    @Test
    public void insertBatch() {
        List<UserInfo> list = new ArrayList<>();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("哈密3");
        userInfo.setProductName("米呀米基哈2");
        userInfo.setCreateTime(new java.util.Date());
        userInfo.setCreateDate(new java.util.Date());
        list.add(userInfo);
        userInfo = new UserInfo();
        userInfo.setUserName("哈密4");
        userInfo.setProductName("米呀密哈3");
        userInfo.setCreateTime(new java.util.Date());
        userInfo.setCreateDate(new java.util.Date());
        list.add(userInfo);
        userInfoMapper.insertBatch(list);
    }

    @Test
    public void insertOrUpdateBatch() {
        List<UserInfo> list = new ArrayList<>();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("哈密3");
        userInfo.setProductName("曼波111");
        userInfo.setCreateTime(new java.util.Date());
        userInfo.setCreateDate(new java.util.Date());
        list.add(userInfo);
        userInfo = new UserInfo();
        userInfo.setUserName("哈密4");
        userInfo.setProductName("曼波222");
        userInfo.setCreateTime(new java.util.Date());
        userInfo.setCreateDate(new java.util.Date());
        list.add(userInfo);
        userInfoMapper.insertOrUpdateBatch(list);
    }
    @Test
    public void selectbykey() {
        UserInfo userInfo = userInfoMapper.selectById(1);
        System.out.println(userInfo);
    }
    @Test
    public void updatebykey() {
        UserInfo userInfo = new UserInfo();
        userInfo.setProductName("哈基哈基哈基米");
        userInfo.setCreateTime(new java.util.Date());
        userInfo.setCreateDate(new java.util.Date());
        Integer count = userInfoMapper.updateById(userInfo,10);
        System.out.println("-----"+count+"-----");
    }
    @Test
    public void deletebykey() {
        userInfoMapper.deleteById(12);
    }
}
