package com.easyjava.entity.po;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.easyjava.enums.DateTimePatternEnum;
import com.easyjava.utils.DateUtils;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

/**
 * @Description:订单信息表
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
public class OrderInfo implements Serializable {
    /**
     * 主键
     */
	private BigInteger id;

    /**
     * 订单号
     */
	private String orderNo;

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

    /**
     * 创建时间
     */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

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

	@Override
	public String toString() {
		return ("主键:" + (id == null ? "空" : id)) + ", " + ("订单号:" + (orderNo == null ? "空" : orderNo)) + ", " + ("用户ID:" + (userId == null ? "空" : userId)) + ", " + ("商品ID:" + (productId == null ? "空" : productId)) + ", " + ("数量:" + (quantity == null ? "空" : quantity)) + ", " + ("订单金额:" + (amount == null ? "空" : amount)) + ", " + ("订单状态:" + (orderStatus == null ? "空" : orderStatus)) + ", " + ("备注:" + (remark == null ? "空" : remark)) + ", " + ("创建时间:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())));
	}
}
