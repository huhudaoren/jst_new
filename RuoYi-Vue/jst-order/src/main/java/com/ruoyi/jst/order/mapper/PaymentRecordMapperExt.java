package com.ruoyi.jst.order.mapper;

import com.ruoyi.jst.order.domain.JstPaymentRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 支付记录扩展 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface PaymentRecordMapperExt {

    JstPaymentRecord selectLatestByOrderId(@Param("orderId") Long orderId);
}
