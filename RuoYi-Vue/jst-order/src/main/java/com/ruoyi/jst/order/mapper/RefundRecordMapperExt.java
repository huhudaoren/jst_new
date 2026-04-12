package com.ruoyi.jst.order.mapper;

import com.ruoyi.jst.order.dto.RefundQueryReqDTO;
import com.ruoyi.jst.order.vo.RefundDetailVO;
import com.ruoyi.jst.order.vo.RefundListVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 退款单扩展 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface RefundRecordMapperExt {

    int countBlockingByOrderId(@Param("orderId") Long orderId);

    List<Long> selectTimeoutPendingRefundIds(@Param("deadlineTime") Date deadlineTime,
                                             @Param("lastRefundId") Long lastRefundId,
                                             @Param("limit") Integer limit);

    int updateStatusByExpected(@Param("refundId") Long refundId,
                               @Param("expectedStatus") String expectedStatus,
                               @Param("targetStatus") String targetStatus,
                               @Param("auditRemark") String auditRemark,
                               @Param("actualCash") BigDecimal actualCash,
                               @Param("actualPoints") Long actualPoints,
                               @Param("couponReturned") Integer couponReturned,
                               @Param("operatorId") Long operatorId,
                               @Param("completeTime") Date completeTime,
                               @Param("updateBy") String updateBy,
                               @Param("updateTime") Date updateTime);

    com.ruoyi.jst.order.domain.JstRefundRecord selectByRefundNo(@Param("refundNo") String refundNo);

    List<RefundListVO> selectMyList(RefundQueryReqDTO query);

    List<RefundListVO> selectAdminList(RefundQueryReqDTO query);

    RefundDetailVO selectDetail(@Param("refundId") Long refundId);
}
