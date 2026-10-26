package com.easyjava.controller;

import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import com.easyjava.service.UserInfoService;
import com.easyjava.entity.vo.ResponseVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:这是一个用户表Controller
 * @Author:zcs
 * @Date:2026-10-26 23:51:44
 */
@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends ABaseController {

	@Autowired
	private UserInfoService userInfoService;

	/**
     * 根据条件分页查询
     */
	@RequestMapping("/loadDataList")
	public ResponseVO<?> loadDataList(UserInfoQuery query) {
		return getSuccessResponseVO(this.userInfoService.findListByPage(query));
	}

	/**
     * 新增
     */
	@RequestMapping("/add")
	public ResponseVO<?> add(@RequestBody UserInfo bean) {
		this.userInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
     * 批量新增
     */
	@RequestMapping("/addBatch")
	public ResponseVO<?> addBatch(@RequestBody List<UserInfo> listBean) {
		this.userInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
     * 批量新增或修改
     */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO<?> addOrUpdateBatch(@RequestBody List<UserInfo> listBean) {
		this.userInfoService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据Id查询
     */
	@RequestMapping("/getUserInfoById")
	public ResponseVO<?> getUserInfoById(Integer id) {
		return getSuccessResponseVO(this.userInfoService.getUserInfoById(id));
	}

	/**
     * 根据Id更新
     */
	@RequestMapping("/updateUserInfoById")
	public ResponseVO<?> updateUserInfoById(@RequestBody UserInfo bean, Integer id) {
		this.userInfoService.updateUserInfoById(bean, id);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据Id删除
     */
	@RequestMapping("/deleteUserInfoById")
	public ResponseVO<?> deleteUserInfoById(Integer id) {
		this.userInfoService.deleteUserInfoById(id);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据UserName查询
     */
	@RequestMapping("/getUserInfoByUserName")
	public ResponseVO<?> getUserInfoByUserName(String userName) {
		return getSuccessResponseVO(this.userInfoService.getUserInfoByUserName(userName));
	}

	/**
     * 根据UserName更新
     */
	@RequestMapping("/updateUserInfoByUserName")
	public ResponseVO<?> updateUserInfoByUserName(@RequestBody UserInfo bean, String userName) {
		this.userInfoService.updateUserInfoByUserName(bean, userName);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据UserName删除
     */
	@RequestMapping("/deleteUserInfoByUserName")
	public ResponseVO<?> deleteUserInfoByUserName(String userName) {
		this.userInfoService.deleteUserInfoByUserName(userName);
		return getSuccessResponseVO(null);
	}
}
