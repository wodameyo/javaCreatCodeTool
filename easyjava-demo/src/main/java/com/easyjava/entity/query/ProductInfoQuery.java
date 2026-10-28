package com.easyjava.entity.query;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:商品信息表查询对象
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
public class ProductInfoQuery extends BaseQuery {
    /**
     * 主键
     */
	private BigInteger id;

    /**
     * 商品名称
     */
	private String productName;

	private String productNameFuzzy;

    /**
     * 商品编码
     */
	private String productCode;

	private String productCodeFuzzy;

    /**
     * 商品分类
     */
	private String category;

	private String categoryFuzzy;

    /**
     * 售价
     */
	private BigDecimal price;

    /**
     * 库存
     */
	private Integer stock;

    /**
     * 是否上架
     */
	private Boolean onSale;

    /**
     * 商品描述
     */
	private String description;

	private String descriptionFuzzy;

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

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductName() {
		return this.productName;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductCode() {
		return this.productCode;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return this.category;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Integer getStock() {
		return this.stock;
	}

	public void setOnSale(Boolean onSale) {
		this.onSale = onSale;
	}

	public Boolean getOnSale() {
		return this.onSale;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setProductNameFuzzy(String productNameFuzzy) {
		this.productNameFuzzy = productNameFuzzy;
	}

	public String getProductNameFuzzy() {
		return this.productNameFuzzy;
	}

	public void setProductCodeFuzzy(String productCodeFuzzy) {
		this.productCodeFuzzy = productCodeFuzzy;
	}

	public String getProductCodeFuzzy() {
		return this.productCodeFuzzy;
	}

	public void setCategoryFuzzy(String categoryFuzzy) {
		this.categoryFuzzy = categoryFuzzy;
	}

	public String getCategoryFuzzy() {
		return this.categoryFuzzy;
	}

	public void setDescriptionFuzzy(String descriptionFuzzy) {
		this.descriptionFuzzy = descriptionFuzzy;
	}

	public String getDescriptionFuzzy() {
		return this.descriptionFuzzy;
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
