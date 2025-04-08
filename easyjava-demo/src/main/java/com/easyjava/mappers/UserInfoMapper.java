package com.easyjava.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:这是一个用户表Mapper
 * @Author:哈基米基哈
 * @Date:2025-04-05 18:32:09
 */
public interface UserInfoMapper<T, P> extends BaseMapper {
    /**
     * 根据Id查询
     */
	T selectById(@Param("id") Integer id);


    /**
     * 根据Id更新
     */
	Integer updateById(@Param("bean") T t, @Param("id") Integer id);


    /**
     * 根据Id删除
     */
	Integer deleteById(@Param("id") Integer id);


}