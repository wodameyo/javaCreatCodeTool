package com.easyjava.service;

import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import java.util.List;

/**
 * @Description:这是一个用户表Service
 * @Author:zcs
 * @Date:2026-10-25 23:56:38
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
	Integer addBatch(List<UserInfo> listBean);

    /**
     * 批量新增或修改
     */
	Integer addOrUpdateBatch(List<UserInfo> listBean);

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