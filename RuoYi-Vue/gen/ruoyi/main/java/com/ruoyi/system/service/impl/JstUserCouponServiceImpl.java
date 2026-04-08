package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstUserCouponMapper;
import com.ruoyi.system.domain.JstUserCoupon;
import com.ruoyi.system.service.IJstUserCouponService;

/**
 * 用户持有优惠券Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstUserCouponServiceImpl implements IJstUserCouponService 
{
    @Autowired
    private JstUserCouponMapper jstUserCouponMapper;

    /**
     * 查询用户持有优惠券
     * 
     * @param userCouponId 用户持有优惠券主键
     * @return 用户持有优惠券
     */
    @Override
    public JstUserCoupon selectJstUserCouponByUserCouponId(Long userCouponId)
    {
        return jstUserCouponMapper.selectJstUserCouponByUserCouponId(userCouponId);
    }

    /**
     * 查询用户持有优惠券列表
     * 
     * @param jstUserCoupon 用户持有优惠券
     * @return 用户持有优惠券
     */
    @Override
    public List<JstUserCoupon> selectJstUserCouponList(JstUserCoupon jstUserCoupon)
    {
        return jstUserCouponMapper.selectJstUserCouponList(jstUserCoupon);
    }

    /**
     * 新增用户持有优惠券
     * 
     * @param jstUserCoupon 用户持有优惠券
     * @return 结果
     */
    @Override
    public int insertJstUserCoupon(JstUserCoupon jstUserCoupon)
    {
        jstUserCoupon.setCreateTime(DateUtils.getNowDate());
        return jstUserCouponMapper.insertJstUserCoupon(jstUserCoupon);
    }

    /**
     * 修改用户持有优惠券
     * 
     * @param jstUserCoupon 用户持有优惠券
     * @return 结果
     */
    @Override
    public int updateJstUserCoupon(JstUserCoupon jstUserCoupon)
    {
        jstUserCoupon.setUpdateTime(DateUtils.getNowDate());
        return jstUserCouponMapper.updateJstUserCoupon(jstUserCoupon);
    }

    /**
     * 批量删除用户持有优惠券
     * 
     * @param userCouponIds 需要删除的用户持有优惠券主键
     * @return 结果
     */
    @Override
    public int deleteJstUserCouponByUserCouponIds(Long[] userCouponIds)
    {
        return jstUserCouponMapper.deleteJstUserCouponByUserCouponIds(userCouponIds);
    }

    /**
     * 删除用户持有优惠券信息
     * 
     * @param userCouponId 用户持有优惠券主键
     * @return 结果
     */
    @Override
    public int deleteJstUserCouponByUserCouponId(Long userCouponId)
    {
        return jstUserCouponMapper.deleteJstUserCouponByUserCouponId(userCouponId);
    }
}
