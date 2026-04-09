package com.ruoyi.jst.points.mapper.lookup;

import com.ruoyi.jst.points.dto.AftersaleQueryReqDTO;
import com.ruoyi.jst.points.vo.AftersaleDetailVO;
import com.ruoyi.jst.points.vo.AftersaleListVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商城售后跨模块退款单 lookup Mapper。
 */
public interface RefundRecordLookupMapper {

    List<AftersaleListVO> selectMyList(@Param("userId") Long userId,
                                       @Param("statuses") List<String> statuses);

    List<AftersaleListVO> selectAdminList(@Param("query") AftersaleQueryReqDTO query,
                                          @Param("statuses") List<String> statuses);

    AftersaleDetailVO selectDetail(@Param("refundId") Long refundId);

    Map<String, Object> selectBizDetail(@Param("refundId") Long refundId);

    Map<String, Object> selectByRefundNo(@Param("refundNo") String refundNo);

    int countBlockingByExchangeId(@Param("exchangeId") Long exchangeId);

    int insertRefundRecord(@Param("refundNo") String refundNo,
                           @Param("orderId") Long orderId,
                           @Param("itemId") Long itemId,
                           @Param("refundType") String refundType,
                           @Param("applySource") String applySource,
                           @Param("reason") String reason,
                           @Param("applyCash") BigDecimal applyCash,
                           @Param("applyPoints") Long applyPoints,
                           @Param("status") String status,
                           @Param("remark") String remark,
                           @Param("createBy") String createBy,
                           @Param("createTime") Date createTime);

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
                               @Param("updateTime") Date updateTime,
                               @Param("remark") String remark);
}
