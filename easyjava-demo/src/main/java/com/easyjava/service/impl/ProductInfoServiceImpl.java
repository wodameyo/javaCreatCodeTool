package com.easyjava.service.impl;

import com.easyjava.entity.po.ProductInfo;
import com.easyjava.entity.query.ProductInfoQuery;
import com.easyjava.entity.query.SimplePage;
import com.easyjava.mappers.ProductInfoMapper;
import com.easyjava.service.ProductInfoService;
import com.easyjava.entity.vo.PaginationResultVO;
import com.easyjava.enums.PageSize;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description:商品信息表ServiceImpl
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
@Service("productInfoService")
@Transactional(rollbackFor = Exception.class)
public class ProductInfoServiceImpl implements ProductInfoService {

	@Autowired
	private ProductInfoMapper productInfoMapper;

	@Override
	public List<ProductInfo> findListByParam(ProductInfoQuery query) {
		return this.productInfoMapper.selectList(query);
	}

	@Override
	public Integer findCountByParam(ProductInfoQuery query) {
		return this.productInfoMapper.selectCount(query);
	}

	@Override
	public PaginationResultVO<ProductInfo> findListByPage(ProductInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<ProductInfo> list = this.findListByParam(query);
		return new PaginationResultVO<ProductInfo>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
	}

	@Override
	public Integer add(ProductInfo bean) {
		return this.productInfoMapper.insert(bean);
	}

	@Override
	public Integer addBatch(List<ProductInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.productInfoMapper.insertBatch(listBean);
	}

	@Override
	public Integer addOrUpdateBatch(List<ProductInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.productInfoMapper.insertOrUpdateBatch(listBean);
	}

	@Override
	public ProductInfo getProductInfoById(BigInteger id) {
		return this.productInfoMapper.selectById(id);
	}

	@Override
	public Integer updateProductInfoById(ProductInfo bean, BigInteger id) {
		return this.productInfoMapper.updateById(bean, id);
	}

	@Override
	public Integer deleteProductInfoById(BigInteger id) {
		return this.productInfoMapper.deleteById(id);
	}

	@Override
	public ProductInfo getProductInfoByProductCode(String productCode) {
		return this.productInfoMapper.selectByProductCode(productCode);
	}

	@Override
	public Integer updateProductInfoByProductCode(ProductInfo bean, String productCode) {
		return this.productInfoMapper.updateByProductCode(bean, productCode);
	}

	@Override
	public Integer deleteProductInfoByProductCode(String productCode) {
		return this.productInfoMapper.deleteByProductCode(productCode);
	}
}
