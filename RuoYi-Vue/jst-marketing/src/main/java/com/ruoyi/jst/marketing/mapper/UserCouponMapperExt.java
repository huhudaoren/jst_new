package com.ruoyi.jst.marketing.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User coupon custom mapper.
 */
@Mapper
public interface UserCouponMapperExt {

    int countAnyByUserAndTemplate(@Param("userId") Long userId,
                                  @Param("couponTemplateId") Long couponTemplateId);

    List<Long> selectOwnedTemplateIds(@Param("userId") Long userId);

    List<Map<String, Object>> selectMyCoupons(@Param("userId") Long userId,
                                              @Param("status") String status,
                                              @Param("now") Date now);

    List<Map<String, Object>> selectSelectableCoupons(@Param("userId") Long userId,
                                                      @Param("candidateCouponIds") List<Long> candidateCouponIds,
                                                      @Param("now") Date now);
}
