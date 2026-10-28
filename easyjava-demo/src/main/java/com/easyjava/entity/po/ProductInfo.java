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
 * @Description:商品信息表
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
public class ProductInfo implements Serializable {
    /**
     * 主键
     */
	private BigInteger id;

    /**
     * 商品名称
     */
	private String productName;

    /**
     * 商品编码
     */
	private String productCode;

    /**
     * 商品分类
     */
	private String category;

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

	@Override
	public String toString() {
		return ("主键:" + (id == null ? "空" : id)) + ", " + ("商品名称:" + (productName == null ? "空" : productName)) + ", " + ("商品编码:" + (productCode == null ? "空" : productCode)) + ", " + ("商品分类:" + (category == null ? "空" : category)) + ", " + ("售价:" + (price == null ? "空" : price)) + ", " + ("库存:" + (stock == null ? "空" : stock)) + ", " + ("是否上架:" + (onSale == null ? "空" : onSale)) + ", " + ("商品描述:" + (description == null ? "空" : description)) + ", " + ("创建时间:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())));
	}
}
