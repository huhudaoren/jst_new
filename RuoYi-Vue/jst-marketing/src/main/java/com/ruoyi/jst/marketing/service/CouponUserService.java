package com.ruoyi.jst.marketing.service;

import com.ruoyi.jst.marketing.dto.CouponSelectDTO;
import com.ruoyi.jst.marketing.vo.CouponSelectResVO;
import com.ruoyi.jst.marketing.vo.MyCouponVO;

import java.util.List;

/**
 * Coupon user service.
 */
public interface CouponUserService {

    Long claim(Long userId, Long templateId);

    List<MyCouponVO> selectMyCoupons(Long userId, String status);

    CouponSelectResVO selectBestCoupon(Long userId, CouponSelectDTO req);
}
