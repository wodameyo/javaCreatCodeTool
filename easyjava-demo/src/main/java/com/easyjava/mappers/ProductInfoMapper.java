package com.easyjava.mappers;

import com.easyjava.entity.po.ProductInfo;
import com.easyjava.entity.query.ProductInfoQuery;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:商品信息表Mapper
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
public interface ProductInfoMapper extends BaseMapper<ProductInfo, ProductInfoQuery> {

    /**
     * 根据Id查询
     */
	ProductInfo selectById(@Param("id") BigInteger id);

    /**
     * 根据Id更新
     */
	Integer updateById(@Param("bean") ProductInfo bean, @Param("id") BigInteger id);

    /**
     * 根据Id删除
     */
	Integer deleteById(@Param("id") BigInteger id);

    /**
     * 根据ProductCode查询
     */
	ProductInfo selectByProductCode(@Param("productCode") String productCode);

    /**
     * 根据ProductCode更新
     */
	Integer updateByProductCode(@Param("bean") ProductInfo bean, @Param("productCode") String productCode);

    /**
     * 根据ProductCode删除
     */
	Integer deleteByProductCode(@Param("productCode") String productCode);
}
