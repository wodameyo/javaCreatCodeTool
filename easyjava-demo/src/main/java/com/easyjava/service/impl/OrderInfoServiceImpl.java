package com.easyjava.service.impl;

import com.easyjava.entity.po.OrderInfo;
import com.easyjava.entity.query.OrderInfoQuery;
import com.easyjava.entity.query.SimplePage;
import com.easyjava.mappers.OrderInfoMapper;
import com.easyjava.service.OrderInfoService;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.enums.PageSize;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description:订单信息表ServiceImpl
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
@Service("orderInfoService")
@Transactional(rollbackFor = Exception.class)
public class OrderInfoServiceImpl implements OrderInfoService {

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Override
	public List<OrderInfo> findListByParam(OrderInfoQuery query) {
		return this.orderInfoMapper.selectList(query);
	}

	@Override
	public Integer findCountByParam(OrderInfoQuery query) {
		return this.orderInfoMapper.selectCount(query);
	}

	@Override
	public PaginationResultVO<OrderInfo> findListByPage(OrderInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<OrderInfo> list = this.findListByParam(query);
		return new PaginationResultVO<OrderInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
	}

	@Override
	public Integer add(OrderInfo bean) {
		return this.orderInfoMapper.insert(bean);
	}

	@Override
	public Integer addBatch(List<OrderInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.orderInfoMapper.insertBatch(listBean);
	}

	@Override
	public Integer addOrUpdateBatch(List<OrderInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.orderInfoMapper.insertOrUpdateBatch(listBean);
	}

	@Override
	public OrderInfo getOrderInfoById(BigInteger id) {
		return this.orderInfoMapper.selectById(id);
	}

	@Override
	public Integer updateOrderInfoById(OrderInfo bean, BigInteger id) {
		return this.orderInfoMapper.updateById(bean, id);
	}

	@Override
	public Integer deleteOrderInfoById(BigInteger id) {
		return this.orderInfoMapper.deleteById(id);
	}

	@Override
	public OrderInfo getOrderInfoByOrderNo(String orderNo) {
		return this.orderInfoMapper.selectByOrderNo(orderNo);
	}

	@Override
	public Integer updateOrderInfoByOrderNo(OrderInfo bean, String orderNo) {
		return this.orderInfoMapper.updateByOrderNo(bean, orderNo);
	}

	@Override
	public Integer deleteOrderInfoByOrderNo(String orderNo) {
		return this.orderInfoMapper.deleteByOrderNo(orderNo);
	}
}
