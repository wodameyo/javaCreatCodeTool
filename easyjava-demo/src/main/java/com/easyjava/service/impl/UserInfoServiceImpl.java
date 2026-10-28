package com.easyjava.service.impl;

import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import com.easyjava.entity.query.SimplePage;
import com.easyjava.mappers.UserInfoMapper;
import com.easyjava.service.UserInfoService;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.enums.PageSize;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description:这是一个用户表ServiceImpl
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
@Service("userInfoService")
@Transactional(rollbackFor = Exception.class)
public class UserInfoServiceImpl implements UserInfoService {

	@Autowired
	private UserInfoMapper userInfoMapper;

	@Override
	public List<UserInfo> findListByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectList(query);
	}

	@Override
	public Integer findCountByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectCount(query);
	}

	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(query);
		return new PaginationResultVO<UserInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
	}

	@Override
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	@Override
	public UserInfo getUserInfoById(Integer id) {
		return this.userInfoMapper.selectById(id);
	}

	@Override
	public Integer updateUserInfoById(UserInfo bean, Integer id) {
		return this.userInfoMapper.updateById(bean, id);
	}

	@Override
	public Integer deleteUserInfoById(Integer id) {
		return this.userInfoMapper.deleteById(id);
	}

	@Override
	public UserInfo getUserInfoByUserName(String userName) {
		return this.userInfoMapper.selectByUserName(userName);
	}

	@Override
	public Integer updateUserInfoByUserName(UserInfo bean, String userName) {
		return this.userInfoMapper.updateByUserName(bean, userName);
	}

	@Override
	public Integer deleteUserInfoByUserName(String userName) {
		return this.userInfoMapper.deleteByUserName(userName);
	}
}
