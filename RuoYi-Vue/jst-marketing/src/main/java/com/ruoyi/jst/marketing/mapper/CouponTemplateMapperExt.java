package com.ruoyi.jst.marketing.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Coupon template custom mapper.
 */
@Mapper
public interface CouponTemplateMapperExt {

    List<Map<String, Object>> selectAdminList(@Param("couponName") String couponName,
                                              @Param("couponType") String couponType,
                                              @Param("status") Integer status);

    List<Map<String, Object>> selectClaimableTemplates(@Param("applicableRole") String applicableRole,
                                                       @Param("now") Date now,
                                                       @Param("excludeTemplateIds") List<Long> excludeTemplateIds);

    List<Map<String, Object>> selectClaimableTemplatesByIds(@Param("templateIds") List<Long> templateIds,
                                                            @Param("applicableRole") String applicableRole,
                                                            @Param("now") Date now,
                                                            @Param("excludeTemplateIds") List<Long> excludeTemplateIds);

    int updateStatus(@Param("couponTemplateId") Long couponTemplateId,
                     @Param("status") Integer status,
                     @Param("updateBy") String updateBy,
                     @Param("updateTime") Date updateTime);

    int markDeleted(@Param("couponTemplateId") Long couponTemplateId,
                    @Param("updateBy") String updateBy,
                    @Param("updateTime") Date updateTime);
}
