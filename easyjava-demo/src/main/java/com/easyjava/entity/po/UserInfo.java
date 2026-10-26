package com.easyjava.entity.po;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.easyjava.enums.DateTimePatternEnum;
import com.easyjava.utils.DateUtils;
import java.util.Date;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Description:这是一个用户表
 * @Author:zcs
 * @Date:2026-10-26 23:51:44
 */
public class UserInfo implements Serializable {
    /**
     * 这是id
     */
	private Integer id;

    /**
     * 用户昵称
     */
	private String userName;

    /**
     * 商品名称
     */
	private String productName;

    /**
     * 创建时间
     */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

    /**
     * 硬币数量
     */
	@JsonIgnore
	private Integer taotle;

    /**
     * 身份状态
     */
	@JsonIgnore
	private Integer status;

    /**
     * 创建日期
     */
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createDate;

    /**
     * 用户是否存在，0：删除 1：正常
     */
	private Integer isDel;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductName() {
		return this.productName;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setTaotle(Integer taotle) {
		this.taotle = taotle;
	}

	public Integer getTaotle() {
		return this.taotle;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public Integer getIsDel() {
		return this.isDel;
	}

	@Override
	public String toString() {
		return ("这是id:" + (id == null ? "空" : id)) + ", " + ("用户昵称:" + (userName == null ? "空" : userName)) + ", " + ("商品名称:" + (productName == null ? "空" : productName)) + ", " + ("创建时间:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))) + ", " + ("硬币数量:" + (taotle == null ? "空" : taotle)) + ", " + ("身份状态:" + (status == null ? "空" : status)) + ", " + ("创建日期:" + (createDate == null ? "空" : DateUtils.format(createDate, DateTimePatternEnum.YYYY_MM_DD.getPattern()))) + ", " + ("用户是否存在，0：删除 1：正常:" + (isDel == null ? "空" : isDel));
	}
}
