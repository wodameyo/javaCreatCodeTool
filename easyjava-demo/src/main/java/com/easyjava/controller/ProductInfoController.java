package com.easyjava.controller;

import com.easyjava.entity.po.ProductInfo;
import com.easyjava.entity.query.ProductInfoQuery;
import com.easyjava.service.ProductInfoService;
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
 * @Description:商品信息表Controller
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
@RestController
@RequestMapping("/productInfo")
public class ProductInfoController extends ABaseController {

	@Autowired
	private ProductInfoService productInfoService;

	/**
     * 根据条件分页查询
     */
	@RequestMapping("/loadDataList")
	public ResponseVO<?> loadDataList(ProductInfoQuery query) {
		return getSuccessResponseVO(this.productInfoService.findListByPage(query));
	}

	/**
     * 新增
     */
	@RequestMapping("/add")
	public ResponseVO<?> add(@RequestBody ProductInfo bean) {
		this.productInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
     * 批量新增
     */
	@RequestMapping("/addBatch")
	public ResponseVO<?> addBatch(@RequestBody List<ProductInfo> listBean) {
		this.productInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
     * 批量新增或修改
     */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO<?> addOrUpdateBatch(@RequestBody List<ProductInfo> listBean) {
		this.productInfoService.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据Id查询
     */
	@RequestMapping("/getProductInfoById")
	public ResponseVO<?> getProductInfoById(@RequestParam("id") BigInteger id) {
		return getSuccessResponseVO(this.productInfoService.getProductInfoById(id));
	}

	/**
     * 根据Id更新
     */
	@RequestMapping("/updateProductInfoById")
	public ResponseVO<?> updateProductInfoById(@RequestBody ProductInfo bean, @RequestParam("id") BigInteger id) {
		this.productInfoService.updateProductInfoById(bean, id);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据Id删除
     */
	@RequestMapping("/deleteProductInfoById")
	public ResponseVO<?> deleteProductInfoById(@RequestParam("id") BigInteger id) {
		this.productInfoService.deleteProductInfoById(id);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据ProductCode查询
     */
	@RequestMapping("/getProductInfoByProductCode")
	public ResponseVO<?> getProductInfoByProductCode(@RequestParam("productCode") String productCode) {
		return getSuccessResponseVO(this.productInfoService.getProductInfoByProductCode(productCode));
	}

	/**
     * 根据ProductCode更新
     */
	@RequestMapping("/updateProductInfoByProductCode")
	public ResponseVO<?> updateProductInfoByProductCode(@RequestBody ProductInfo bean, @RequestParam("productCode") String productCode) {
		this.productInfoService.updateProductInfoByProductCode(bean, productCode);
		return getSuccessResponseVO(null);
	}

	/**
     * 根据ProductCode删除
     */
	@RequestMapping("/deleteProductInfoByProductCode")
	public ResponseVO<?> deleteProductInfoByProductCode(@RequestParam("productCode") String productCode) {
		this.productInfoService.deleteProductInfoByProductCode(productCode);
		return getSuccessResponseVO(null);
	}
}
