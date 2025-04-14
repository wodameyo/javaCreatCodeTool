package com.easyjava.controller;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.easyjava.enums.DateTimePatternEnum;
import com.easyjava.utils.DateUtils;
import java.util.Date;
import java.io.Serializable;
import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import com.easyjava.mappers.UserInfoMapper;
import com.easyjava.service.UserInfoService;
import com.easyjava.entity.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import javax.annotation.Resource;
import java.util.List;
/**
 * @Description:这是一个用户表Controller
 * @Author:哈基米基哈
 * @Date:2025-04-15 12:09:10
 */

@RestController
@RequestMapping("UserInfo")
public class UserInfoController extends ABaseController{

	@Resource
	private UserInfoService UserInfoService;
    /**
     * 根据条件分页查询
     */
	@RequestMapping("loadDataList")
	public ResponseVO loadDataList(UserInfoQuery query) {
		return getSuccessResponseVO(UserInfoService.findListByPage(query));
	}

    /**
     * 新增
     */
	@RequestMapping("add")
	public ResponseVO add(UserInfo bean){
		this.UserInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

    /**
     * 批量新增
     */
	@RequestMapping("addBatch")
	public ResponseVO addBatch(@RequestBody List<UserInfo> listBean) {
		this.UserInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

    /**
     * 批量新增或修改
     */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserInfo> listbean) {
		this.UserInfoService.addOrUpdateBatch(listbean);
		return getSuccessResponseVO(null);
	}

    /**
     * 根据Id查询
     */
	@RequestMapping("getUserInfoById")
	public ResponseVO getUserInfoById(Integer id) {
		return getSuccessResponseVO(this.UserInfoService.getUserInfoById(id));
	}

    /**
     * 根据Id更新
     */
	@RequestMapping("updateUserInfoById")
	public ResponseVO updateUserInfoById(UserInfo bean, Integer id) {
		this.UserInfoService.updateUserInfoById(bean,id);
		return getSuccessResponseVO(null);
	}

    /**
     * 根据Id删除
     */
	@RequestMapping("deleteUserInfoById")
	public ResponseVO deleteUserInfoById(Integer id) {
		this.UserInfoService.deleteUserInfoById(id);
		return getSuccessResponseVO(null);
	}

    /**
     * 根据UserName查询
     */
	@RequestMapping("getUserInfoByUserName")
	public ResponseVO getUserInfoByUserName(String userName) {
		return getSuccessResponseVO(this.UserInfoService.getUserInfoByUserName(userName));
	}

    /**
     * 根据UserName更新
     */
	@RequestMapping("updateUserInfoByUserName")
	public ResponseVO updateUserInfoByUserName(UserInfo bean, String userName) {
		this.UserInfoService.updateUserInfoByUserName(bean,userName);
		return getSuccessResponseVO(null);
	}

    /**
     * 根据UserName删除
     */
	@RequestMapping("deleteUserInfoByUserName")
	public ResponseVO deleteUserInfoByUserName(String userName) {
		this.UserInfoService.deleteUserInfoByUserName(userName);
		return getSuccessResponseVO(null);
	}


}