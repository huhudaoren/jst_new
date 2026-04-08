package com.ruoyi.jst.marketing.service;

import java.util.List;
import com.ruoyi.jst.marketing.domain.JstCouponTemplate;

/**
 * 优惠券模板Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstCouponTemplateService 
{
    /**
     * 查询优惠券模板
     * 
     * @param couponTemplateId 优惠券模板主键
     * @return 优惠券模板
     */
    public JstCouponTemplate selectJstCouponTemplateByCouponTemplateId(Long couponTemplateId);

    /**
     * 查询优惠券模板列表
     * 
     * @param jstCouponTemplate 优惠券模板
     * @return 优惠券模板集合
     */
    public List<JstCouponTemplate> selectJstCouponTemplateList(JstCouponTemplate jstCouponTemplate);

    /**
     * 新增优惠券模板
     * 
     * @param jstCouponTemplate 优惠券模板
     * @return 结果
     */
    public int insertJstCouponTemplate(JstCouponTemplate jstCouponTemplate);

    /**
     * 修改优惠券模板
     * 
     * @param jstCouponTemplate 优惠券模板
     * @return 结果
     */
    public int updateJstCouponTemplate(JstCouponTemplate jstCouponTemplate);

    /**
     * 批量删除优惠券模板
     * 
     * @param couponTemplateIds 需要删除的优惠券模板主键集合
     * @return 结果
     */
    public int deleteJstCouponTemplateByCouponTemplateIds(Long[] couponTemplateIds);

    /**
     * 删除优惠券模板信息
     * 
     * @param couponTemplateId 优惠券模板主键
     * @return 结果
     */
    public int deleteJstCouponTemplateByCouponTemplateId(Long couponTemplateId);
}
