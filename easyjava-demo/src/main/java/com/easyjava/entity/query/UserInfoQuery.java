package com.easyjava.entity.query;

import java.util.Date;


/**
 * @Description:这是一个用户表查询对象
 * @Author:哈基米基哈
 * @Date:2025-04-13 20:23:33
 */
public class UserInfoQuery extends BaseQuery {
    /**
     * 这是id
     */
	private Integer id;

    /**
     * 用户昵称
     */
	private String userName;

	private String userNameFuzzy;

    /**
     * 商品名称
     */
	private String productName;

	private String productNameFuzzy;

    /**
     * 创建时间
     */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

    /**
     * 硬币数量
     */
	private Integer taotle;

    /**
     * 身份状态
     */
	private Integer status;

    /**
     * 创建日期
     */
	private Date createDate;

	private String createDateStart;

	private String createDateEnd;

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

	public void setUserNameFuzzy(String userNameFuzzy) {
		this.userNameFuzzy = userNameFuzzy;
	}

	public String getUserNameFuzzy() {
		return this.userNameFuzzy;
	}

	public void setProductNameFuzzy(String productNameFuzzy) {
		this.productNameFuzzy = productNameFuzzy;
	}

	public String getProductNameFuzzy() {
		return this.productNameFuzzy;
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

	public void setCreateDateStart(String createDateStart) {
		this.createDateStart = createDateStart;
	}

	public String getCreateDateStart() {
		return this.createDateStart;
	}

	public void setCreateDateEnd(String createDateEnd) {
		this.createDateEnd = createDateEnd;
	}

	public String getCreateDateEnd() {
		return this.createDateEnd;
	}

}