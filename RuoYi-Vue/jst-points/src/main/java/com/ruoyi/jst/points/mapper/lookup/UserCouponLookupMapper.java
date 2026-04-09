package com.ruoyi.jst.points.mapper.lookup;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

/**
 * 用户券发放 lookup Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface UserCouponLookupMapper {

    Map<String, Object> selectCouponTemplate(@Param("couponTemplateId") Long couponTemplateId);

    int insertUserCoupon(@Param("couponTemplateId") Long couponTemplateId,
                         @Param("userId") Long userId,
                         @Param("issueBatchNo") String issueBatchNo,
                         @Param("issueSource") String issueSource,
                         @Param("status") String status,
                         @Param("validStart") Date validStart,
                         @Param("validEnd") Date validEnd,
                         @Param("orderId") Long orderId,
                         @Param("createBy") String createBy,
                         @Param("createTime") Date createTime,
                         @Param("remark") String remark);

    int countUsedCouponsByExchangeId(@Param("exchangeId") Long exchangeId);

    int refundCouponsByExchangeId(@Param("exchangeId") Long exchangeId,
                                  @Param("updateBy") String updateBy,
                                  @Param("updateTime") Date updateTime);
}
