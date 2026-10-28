package com.easyjava.mappers;

import com.easyjava.entity.po.OrderInfo;
import com.easyjava.entity.query.OrderInfoQuery;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:订单信息表Mapper
 * @Author:zcs
 * @Date:2026-10-29 00:24:57
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo, OrderInfoQuery> {

    /**
     * 根据Id查询
     */
	OrderInfo selectById(@Param("id") BigInteger id);

    /**
     * 根据Id更新
     */
	Integer updateById(@Param("bean") OrderInfo bean, @Param("id") BigInteger id);

    /**
     * 根据Id删除
     */
	Integer deleteById(@Param("id") BigInteger id);

    /**
     * 根据OrderNo查询
     */
	OrderInfo selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据OrderNo更新
     */
	Integer updateByOrderNo(@Param("bean") OrderInfo bean, @Param("orderNo") String orderNo);

    /**
     * 根据OrderNo删除
     */
	Integer deleteByOrderNo(@Param("orderNo") String orderNo);
}
