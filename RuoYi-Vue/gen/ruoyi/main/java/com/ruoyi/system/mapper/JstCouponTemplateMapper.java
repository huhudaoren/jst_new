package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.JstCouponTemplate;

/**
 * 优惠券模板Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstCouponTemplateMapper 
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
     * 删除优惠券模板
     * 
     * @param couponTemplateId 优惠券模板主键
     * @return 结果
     */
    public int deleteJstCouponTemplateByCouponTemplateId(Long couponTemplateId);

    /**
     * 批量删除优惠券模板
     * 
     * @param couponTemplateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstCouponTemplateByCouponTemplateIds(Long[] couponTemplateIds);
}
