package com.easyjava.service.impl;

import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.entity.po.UserInfo;
import com.easyjava.entity.query.UserInfoQuery;
import com.easyjava.service.UserInfoService;
import com.easyjava.mappers.UserInfoMapper;
import com.easyjava.entity.query.SimplePage;
import com.easyjava.enums.PageSize;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * @Description:这是一个用户表ServiceImpl
 * @Author:zcs
 * @Date:2026-10-25 23:56:38
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Autowired
	private UserInfoMapper userInfoMapper;

    /**
     * 根据条件查询列表
     */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectList(query);
	}

    /**
     * 根据条件查询数量
     */
	@Override
	public Integer findCountByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectCount(query);
	}

    /**
     * 分页查询
     */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(query);
		return new PaginationResultVO<UserInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
	}

    /**
     * 新增
     */
	@Override
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

    /**
     * 批量新增
     */
	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

    /**
     * 批量新增或修改
     */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

    /**
     * 根据Id查询
     */
	@Override
	public UserInfo getUserInfoById(Integer id) {
		return this.userInfoMapper.selectById(id);
	}

    /**
     * 根据Id更新
     */
	@Override
	public Integer updateUserInfoById(UserInfo bean, Integer id) {
		return this.userInfoMapper.updateById(bean, id);
	}

    /**
     * 根据Id删除
     */
	@Override
	public Integer deleteUserInfoById(Integer id) {
		return this.userInfoMapper.deleteById(id);
	}

    /**
     * 根据UserName查询
     */
	@Override
	public UserInfo getUserInfoByUserName(String userName) {
		return this.userInfoMapper.selectByUserName(userName);
	}

    /**
     * 根据UserName更新
     */
	@Override
	public Integer updateUserInfoByUserName(UserInfo bean, String userName) {
		return this.userInfoMapper.updateByUserName(bean, userName);
	}

    /**
     * 根据UserName删除
     */
	@Override
	public Integer deleteUserInfoByUserName(String userName) {
		return this.userInfoMapper.deleteByUserName(userName);
	}

}