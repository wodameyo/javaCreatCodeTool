package com.easyjava.entity.query;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:订单信息表查询对象
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
public class OrderInfoQuery extends BaseQuery {
    /**
     * 主键
     */
	private BigInteger id;

    /**
     * 订单号
     */
	private String orderNo;

	private String orderNoFuzzy;

    /**
     * 用户ID
     */
	private BigInteger userId;

    /**
     * 商品ID
     */
	private BigInteger productId;

    /**
     * 数量
     */
	private Integer quantity;

    /**
     * 订单金额
     */
	private BigDecimal amount;

    /**
     * 订单状态
     */
	private Integer orderStatus;

    /**
     * 备注
     */
	private String remark;

	private String remarkFuzzy;

    /**
     * 创建时间
     */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	public void setId(BigInteger id) {
		this.id = id;
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderNo() {
		return this.orderNo;
	}

	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}

	public BigInteger getUserId() {
		return this.userId;
	}

	public void setProductId(BigInteger productId) {
		this.productId = productId;
	}

	public BigInteger getProductId() {
		return this.productId;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getQuantity() {
		return this.quantity;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getOrderStatus() {
		return this.orderStatus;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setOrderNoFuzzy(String orderNoFuzzy) {
		this.orderNoFuzzy = orderNoFuzzy;
	}

	public String getOrderNoFuzzy() {
		return this.orderNoFuzzy;
	}

	public void setRemarkFuzzy(String remarkFuzzy) {
		this.remarkFuzzy = remarkFuzzy;
	}

	public String getRemarkFuzzy() {
		return this.remarkFuzzy;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeStart() {
		return this.createTimeStart;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getCreateTimeEnd() {
		return this.createTimeEnd;
	}

}
