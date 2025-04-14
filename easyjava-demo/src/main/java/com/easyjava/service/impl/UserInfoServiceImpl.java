package com.easyjava.service.impl;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.easyjava.enums.DateTimePatternEnum;
import com.easyjava.utils.DateUtils;
import java.util.Date;
import java.io.Serializable;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import com.easyjava.service.UserInfoService;
import com.easyjava.mappers.UserInfoMapper;
import com.easyjava.entity.query.SimplePage;
import com.easyjava.enums.PageSize;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
/**
 * @Description:这是一个用户表ServiceImpl
 * @Author:哈基米基哈
 * @Date:2025-04-13 20:23:33
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Resource
	private UserInfoMapper<UserInfo,UserInfoQuery> userInfoMapper;    /**
     * 根据条件查询列表
     */
	public List<UserInfo> findListByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectList(query);
	}
    /**
     * 根据条件查询数量
     */
	public Integer findCountByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectCount(query);
	}
    /**
     * 分页查询
     */
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(query);
		PaginationResultVO<UserInfo> result = new PaginationResultVO<UserInfo>();
		return null;
	}
    /**
     * 新增
     */
	public Integer add(UserInfo bean){
		return this.userInfoMapper.insert(bean);
	}
    /**
     * 批量新增
     */
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}
    /**
     * 批量新增或修改
     */
	public Integer addOrUpdateBatch(List<UserInfo> listbean) {
		if (listbean == null || listbean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listbean);
	}
    /**
     * 根据Id查询
     */
	public UserInfo getUserInfoById(Integer id) {
		return this.userInfoMapper.selectById(id);
	}
    /**
     * 根据Id更新
     */
	public Integer updateUserInfoById(UserInfo bean, Integer id) {
		return this.userInfoMapper.updateById(bean,id);
	}
    /**
     * 根据Id删除
     */
	public Integer deleteUserInfoById(Integer id) {
		return this.userInfoMapper.deleteById(id);
	}
    /**
     * 根据UserName查询
     */
	public UserInfo getUserInfoByUserName(String userName) {
		return this.userInfoMapper.selectByUserName(userName);
	}
    /**
     * 根据UserName更新
     */
	public Integer updateUserInfoByUserName(UserInfo bean, String userName) {
		return this.userInfoMapper.updateByUserName(bean,userName);
	}
    /**
     * 根据UserName删除
     */
	public Integer deleteUserInfoByUserName(String userName) {
		return this.userInfoMapper.deleteByUserName(userName);
	}

}