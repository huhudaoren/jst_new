package com.ruoyi.jst.marketing.service;

import java.util.List;
import com.ruoyi.jst.marketing.domain.JstUserCoupon;

/**
 * 用户持有优惠券Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstUserCouponService 
{
    /**
     * 查询用户持有优惠券
     * 
     * @param userCouponId 用户持有优惠券主键
     * @return 用户持有优惠券
     */
    public JstUserCoupon selectJstUserCouponByUserCouponId(Long userCouponId);

    /**
     * 查询用户持有优惠券列表
     * 
     * @param jstUserCoupon 用户持有优惠券
     * @return 用户持有优惠券集合
     */
    public List<JstUserCoupon> selectJstUserCouponList(JstUserCoupon jstUserCoupon);

    /**
     * 新增用户持有优惠券
     * 
     * @param jstUserCoupon 用户持有优惠券
     * @return 结果
     */
    public int insertJstUserCoupon(JstUserCoupon jstUserCoupon);

    /**
     * 修改用户持有优惠券
     * 
     * @param jstUserCoupon 用户持有优惠券
     * @return 结果
     */
    public int updateJstUserCoupon(JstUserCoupon jstUserCoupon);

    /**
     * 批量删除用户持有优惠券
     * 
     * @param userCouponIds 需要删除的用户持有优惠券主键集合
     * @return 结果
     */
    public int deleteJstUserCouponByUserCouponIds(Long[] userCouponIds);

    /**
     * 删除用户持有优惠券信息
     * 
     * @param userCouponId 用户持有优惠券主键
     * @return 结果
     */
    public int deleteJstUserCouponByUserCouponId(Long userCouponId);
}
