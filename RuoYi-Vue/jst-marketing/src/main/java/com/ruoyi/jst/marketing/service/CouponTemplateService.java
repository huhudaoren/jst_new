package com.ruoyi.jst.marketing.service;

import com.ruoyi.jst.marketing.dto.CouponIssueDTO;
import com.ruoyi.jst.marketing.dto.CouponTemplateSaveDTO;
import com.ruoyi.jst.marketing.vo.ClaimableCouponVO;
import com.ruoyi.jst.marketing.vo.CouponTemplateVO;

import java.util.List;
import java.util.Map;

/**
 * Coupon template service.
 */
public interface CouponTemplateService {

    List<CouponTemplateVO> selectAdminList(String couponName, String couponType, Integer status);

    CouponTemplateVO getAdminDetail(Long couponTemplateId);

    Long create(CouponTemplateSaveDTO req);

    void update(CouponTemplateSaveDTO req);

    void delete(Long couponTemplateId);

    void publish(Long couponTemplateId);

    void offline(Long couponTemplateId);

    Map<String, Object> issue(Long couponTemplateId, CouponIssueDTO req);

    List<ClaimableCouponVO> selectClaimableTemplates(Long userId);

    List<ClaimableCouponVO> selectClaimableTemplatesByIds(Long userId, List<Long> templateIds);
}
