package com.ruoyi.jst.order.mapper.lookup;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

/**
 * 优惠券轻量查询 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface CouponLookupMapper {

    Map<String, Object> selectUserCoupon(@Param("userCouponId") Long userCouponId);

    int lockCoupon(@Param("userCouponId") Long userCouponId,
                   @Param("orderId") Long orderId,
                   @Param("userId") Long userId,
                   @Param("updateBy") String updateBy,
                   @Param("updateTime") Date updateTime);

    int unlockCoupon(@Param("userCouponId") Long userCouponId,
                     @Param("orderId") Long orderId,
                     @Param("updateBy") String updateBy,
                     @Param("updateTime") Date updateTime);

    int useCoupon(@Param("userCouponId") Long userCouponId,
                  @Param("orderId") Long orderId,
                  @Param("updateBy") String updateBy,
                  @Param("updateTime") Date updateTime);
}
