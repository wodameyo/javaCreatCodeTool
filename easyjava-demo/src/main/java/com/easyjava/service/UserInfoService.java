package com.easyjava.service;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.easyjava.enums.DateTimePatternEnum;
import com.easyjava.utils.DateUtils;
import java.util.Date;
import java.io.Serializable;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import java.util.List;
/**
 * @Description:这是一个用户表Service
 * @Author:哈基米基哈
 * @Date:2025-04-13 20:23:33
 */
public interface UserInfoService {
    /**
     * 根据条件查询列表
     */
	List<UserInfo> findListByParam(UserInfoQuery query);

    /**
     * 根据条件查询数量
     */
	Integer findCountByParam(UserInfoQuery query);

    /**
     * 分页查询
     */
	PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query);

    /**
     * 新增
     */
	Integer add(UserInfo bean);

    /**
     * 批量新增
     */
	Integer addBatch(List<UserInfo> listbean);

    /**
     * 批量新增或修改
     */
	Integer addOrUpdateBatch(List<UserInfo> listbean);

    /**
     * 根据Id查询
     */
	UserInfo getUserInfoById(Integer id);


    /**
     * 根据Id更新
     */
	Integer updateUserInfoById(UserInfo bean, Integer id);


    /**
     * 根据Id删除
     */
	Integer deleteUserInfoById(Integer id);


    /**
     * 根据UserName查询
     */
	UserInfo getUserInfoByUserName(String userName);


    /**
     * 根据UserName更新
     */
	Integer updateUserInfoByUserName(UserInfo bean, String userName);


    /**
     * 根据UserName删除
     */
	Integer deleteUserInfoByUserName(String userName);



}