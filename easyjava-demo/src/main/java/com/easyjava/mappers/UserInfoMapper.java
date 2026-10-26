package com.easyjava.mappers;

import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:这是一个用户表Mapper
 * @Author:zcs
 * @Date:2026-10-26 23:51:44
 */
public interface UserInfoMapper extends BaseMapper<UserInfo, UserInfoQuery> {

    /**
     * 根据Id查询
     */
	UserInfo selectById(@Param("id") Integer id);

    /**
     * 根据Id更新
     */
	Integer updateById(@Param("bean") UserInfo bean, @Param("id") Integer id);

    /**
     * 根据Id删除
     */
	Integer deleteById(@Param("id") Integer id);

    /**
     * 根据UserName查询
     */
	UserInfo selectByUserName(@Param("userName") String userName);

    /**
     * 根据UserName更新
     */
	Integer updateByUserName(@Param("bean") UserInfo bean, @Param("userName") String userName);

    /**
     * 根据UserName删除
     */
	Integer deleteByUserName(@Param("userName") String userName);
}
