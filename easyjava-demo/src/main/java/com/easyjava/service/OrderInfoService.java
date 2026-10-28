package com.easyjava.service;

import com.easyjava.entity.po.OrderInfo;
import com.easyjava.entity.query.OrderInfoQuery;
import com.easyjava.entity.vo.PaginationResultVO;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:订单信息表Service
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
public interface OrderInfoService {

    /**
     * 根据条件查询列表
     */
	List<OrderInfo> findListByParam(OrderInfoQuery query);

    /**
     * 根据条件查询数量
     */
	Integer findCountByParam(OrderInfoQuery query);

    /**
     * 分页查询
     */
	PaginationResultVO<OrderInfo> findListByPage(OrderInfoQuery query);

    /**
     * 新增
     */
	Integer add(OrderInfo bean);

    /**
     * 批量新增
     */
	Integer addBatch(List<OrderInfo> listBean);

    /**
     * 批量新增或修改
     */
	Integer addOrUpdateBatch(List<OrderInfo> listBean);

    /**
     * 根据Id查询
     */
	OrderInfo getOrderInfoById(BigInteger id);

    /**
     * 根据Id更新
     */
	Integer updateOrderInfoById(OrderInfo bean, BigInteger id);

    /**
     * 根据Id删除
     */
	Integer deleteOrderInfoById(BigInteger id);

    /**
     * 根据OrderNo查询
     */
	OrderInfo getOrderInfoByOrderNo(String orderNo);

    /**
     * 根据OrderNo更新
     */
	Integer updateOrderInfoByOrderNo(OrderInfo bean, String orderNo);

    /**
     * 根据OrderNo删除
     */
	Integer deleteOrderInfoByOrderNo(String orderNo);
}
