package com.easyjava.service;

import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import com.easyjava.entity.vo.PaginationResultVO;
import java.util.List;

/**
 * @Description:这是一个用户表Service
 * @Author:tester
 * @Date:2026-01-01 00:00:00
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

    /**
     * 根据UserNameAndStatus查询
     */
	UserInfo getUserInfoByUserNameAndStatus(String userName, Integer status);

    /**
     * 根据UserNameAndStatus更新
     */
	Integer updateUserInfoByUserNameAndStatus(UserInfo bean, String userName, Integer status);

    /**
     * 根据UserNameAndStatus删除
     */
	Integer deleteUserInfoByUserNameAndStatus(String userName, Integer status);
}
