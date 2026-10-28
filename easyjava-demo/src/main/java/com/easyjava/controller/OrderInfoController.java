package com.easyjava.controller;

import com.easyjava.entity.po.OrderInfo;
import com.easyjava.entity.query.OrderInfoQuery;
import com.easyjava.service.OrderInfoService;
import com.easyjava.entity.vo.ResponseVO;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:订单信息表Controller
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
@RestController
@RequestMapping("/orderInfo")
public class OrderInfoController extends ABaseController {

	@Autowired
	private OrderInfoService orderInfoService;

	/**
     * 根据条件分页查询
     */
	@RequestMapping("/loadDataList")
	public ResponseVO<?> loadDataList(OrderInfoQuery query) {
		return getSuccessResponseVO(this.orderInfoService.findListByPage(query));
	}

	/**
     * 新增
     */
	@RequestMapping("/add")
	public ResponseVO<?> add(@RequestBody OrderInfo bean) {
		this.orderInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
     * 批量新增
     */
	@RequestMapping("/addBatch")
	public ResponseVO<?> addBatch(@RequestBody List<OrderInfo> listBean) {
		this.orderInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
     * 批量新增或修改
     */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO<?> addOrUpdateBatch(@RequestBody List<OrderInfo> listBean) {
		this.orderInfoService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据Id查询
     */
	@RequestMapping("/getOrderInfoById")
	public ResponseVO<?> getOrderInfoById(@RequestParam("id") BigInteger id) {
		return getSuccessResponseVO(this.orderInfoService.getOrderInfoById(id));
	}

	/**
     * 根据Id更新
     */
	@RequestMapping("/updateOrderInfoById")
	public ResponseVO<?> updateOrderInfoById(@RequestBody OrderInfo bean, @RequestParam("id") BigInteger id) {
		this.orderInfoService.updateOrderInfoById(bean, id);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据Id删除
     */
	@RequestMapping("/deleteOrderInfoById")
	public ResponseVO<?> deleteOrderInfoById(@RequestParam("id") BigInteger id) {
		this.orderInfoService.deleteOrderInfoById(id);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据OrderNo查询
     */
	@RequestMapping("/getOrderInfoByOrderNo")
	public ResponseVO<?> getOrderInfoByOrderNo(@RequestParam("orderNo") String orderNo) {
		return getSuccessResponseVO(this.orderInfoService.getOrderInfoByOrderNo(orderNo));
	}

	/**
     * 根据OrderNo更新
     */
	@RequestMapping("/updateOrderInfoByOrderNo")
	public ResponseVO<?> updateOrderInfoByOrderNo(@RequestBody OrderInfo bean, @RequestParam("orderNo") String orderNo) {
		this.orderInfoService.updateOrderInfoByOrderNo(bean, orderNo);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据OrderNo删除
     */
	@RequestMapping("/deleteOrderInfoByOrderNo")
	public ResponseVO<?> deleteOrderInfoByOrderNo(@RequestParam("orderNo") String orderNo) {
		this.orderInfoService.deleteOrderInfoByOrderNo(orderNo);
		return getSuccessResponseVO(null);
	}
}
