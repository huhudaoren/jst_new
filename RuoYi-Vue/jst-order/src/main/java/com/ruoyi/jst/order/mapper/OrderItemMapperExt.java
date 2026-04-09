package com.ruoyi.jst.order.mapper;

import com.ruoyi.jst.order.domain.JstOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单明细扩展 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface OrderItemMapperExt {

    List<JstOrderItem> selectByOrderId(@Param("orderId") Long orderId);

    int accumulateRefundByExpected(@Param("itemId") Long itemId,
                                   @Param("expectedRefundAmount") java.math.BigDecimal expectedRefundAmount,
                                   @Param("expectedRefundPoints") Long expectedRefundPoints,
                                   @Param("refundAmount") java.math.BigDecimal refundAmount,
                                   @Param("refundPoints") Long refundPoints,
                                   @Param("updateBy") String updateBy,
                                   @Param("updateTime") java.util.Date updateTime);
}
