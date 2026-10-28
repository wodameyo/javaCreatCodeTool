package com.easyjava.service;

import com.easyjava.entity.po.ProductInfo;
import com.easyjava.entity.query.ProductInfoQuery;
import com.easyjava.entity.vo.PaginationResultVO;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:商品信息表Service
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
public interface ProductInfoService {

    /**
     * 根据条件查询列表
     */
	List<ProductInfo> findListByParam(ProductInfoQuery query);

    /**
     * 根据条件查询数量
     */
	Integer findCountByParam(ProductInfoQuery query);

    /**
     * 分页查询
     */
	PaginationResultVO<ProductInfo> findListByPage(ProductInfoQuery query);

    /**
     * 新增
     */
	Integer add(ProductInfo bean);

    /**
     * 批量新增
     */
	Integer addBatch(List<ProductInfo> listBean);

    /**
     * 批量新增或修改
     */
	Integer addOrUpdateBatch(List<ProductInfo> listBean);

    /**
     * 根据Id查询
     */
	ProductInfo getProductInfoById(BigInteger id);

    /**
     * 根据Id更新
     */
	Integer updateProductInfoById(ProductInfo bean, BigInteger id);

    /**
     * 根据Id删除
     */
	Integer deleteProductInfoById(BigInteger id);

    /**
     * 根据ProductCode查询
     */
	ProductInfo getProductInfoByProductCode(String productCode);

    /**
     * 根据ProductCode更新
     */
	Integer updateProductInfoByProductCode(ProductInfo bean, String productCode);

    /**
     * 根据ProductCode删除
     */
	Integer deleteProductInfoByProductCode(String productCode);
}
