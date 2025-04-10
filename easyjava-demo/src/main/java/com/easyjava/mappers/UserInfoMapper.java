package com.easyjava.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:这是一个用户表Mapper
 * @Author:哈基米基哈
 * @Date:2025-04-10 20:28:49
 */
public interface UserInfoMapper<T, P> extends BaseMapper {
    /**
     * 根据Id查询
     */
	T selectById(@Param("id") Integer id);


    /**
     * 根据Id更新
     */
	Long updateById(@Param("bean") T t, @Param("id") Integer id);


    /**
     * 根据Id删除
     */
	Long deleteById(@Param("id") Integer id);


    /**
     * 根据UserName查询
     */
	T selectByUserName(@Param("userName") String userName);


    /**
     * 根据UserName更新
     */
	Long updateByUserName(@Param("bean") T t, @Param("userName") String userName);


    /**
     * 根据UserName删除
     */
	Long deleteByUserName(@Param("userName") String userName);


}