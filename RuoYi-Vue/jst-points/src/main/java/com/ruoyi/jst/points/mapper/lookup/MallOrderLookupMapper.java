package com.ruoyi.jst.points.mapper.lookup;

import com.ruoyi.jst.order.domain.JstOrderMain;
import com.ruoyi.jst.order.domain.JstPaymentRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

/**
 * 商城兑换关联订单读写 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface MallOrderLookupMapper {

    int insertOrderMain(JstOrderMain order);

    JstOrderMain selectOrderById(@Param("orderId") Long orderId);

    Map<String, Object> selectOrderSnapshot(@Param("orderId") Long orderId);

    int updateOrderStatusByExpected(@Param("orderId") Long orderId,
                                    @Param("expectedStatus") String expectedStatus,
                                    @Param("targetStatus") String targetStatus,
                                    @Param("payTime") Date payTime,
                                    @Param("updateBy") String updateBy,
                                    @Param("updateTime") Date updateTime);

    JstPaymentRecord selectLatestPayment(@Param("orderId") Long orderId);

    Map<String, Object> selectLatestPaymentMap(@Param("orderId") Long orderId);

    int insertPaymentRecord(JstPaymentRecord record);

    int updateOrderRefundInfo(@Param("orderId") Long orderId,
                              @Param("expectedStatus") String expectedStatus,
                              @Param("targetStatus") String targetStatus,
                              @Param("refundStatus") String refundStatus,
                              @Param("updateBy") String updateBy,
                              @Param("updateTime") Date updateTime);
}
