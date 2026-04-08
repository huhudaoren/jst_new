package com.ruoyi.jst.marketing.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.marketing.mapper.JstCouponTemplateMapper;
import com.ruoyi.jst.marketing.domain.JstCouponTemplate;
import com.ruoyi.jst.marketing.service.IJstCouponTemplateService;

/**
 * 优惠券模板Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstCouponTemplateServiceImpl implements IJstCouponTemplateService 
{
    @Autowired
    private JstCouponTemplateMapper jstCouponTemplateMapper;

    /**
     * 查询优惠券模板
     * 
     * @param couponTemplateId 优惠券模板主键
     * @return 优惠券模板
     */
    @Override
    public JstCouponTemplate selectJstCouponTemplateByCouponTemplateId(Long couponTemplateId)
    {
        return jstCouponTemplateMapper.selectJstCouponTemplateByCouponTemplateId(couponTemplateId);
    }

    /**
     * 查询优惠券模板列表
     * 
     * @param jstCouponTemplate 优惠券模板
     * @return 优惠券模板
     */
    @Override
    public List<JstCouponTemplate> selectJstCouponTemplateList(JstCouponTemplate jstCouponTemplate)
    {
        return jstCouponTemplateMapper.selectJstCouponTemplateList(jstCouponTemplate);
    }

    /**
     * 新增优惠券模板
     * 
     * @param jstCouponTemplate 优惠券模板
     * @return 结果
     */
    @Override
    public int insertJstCouponTemplate(JstCouponTemplate jstCouponTemplate)
    {
        jstCouponTemplate.setCreateTime(DateUtils.getNowDate());
        return jstCouponTemplateMapper.insertJstCouponTemplate(jstCouponTemplate);
    }

    /**
     * 修改优惠券模板
     * 
     * @param jstCouponTemplate 优惠券模板
     * @return 结果
     */
    @Override
    public int updateJstCouponTemplate(JstCouponTemplate jstCouponTemplate)
    {
        jstCouponTemplate.setUpdateTime(DateUtils.getNowDate());
        return jstCouponTemplateMapper.updateJstCouponTemplate(jstCouponTemplate);
    }

    /**
     * 批量删除优惠券模板
     * 
     * @param couponTemplateIds 需要删除的优惠券模板主键
     * @return 结果
     */
    @Override
    public int deleteJstCouponTemplateByCouponTemplateIds(Long[] couponTemplateIds)
    {
        return jstCouponTemplateMapper.deleteJstCouponTemplateByCouponTemplateIds(couponTemplateIds);
    }

    /**
     * 删除优惠券模板信息
     * 
     * @param couponTemplateId 优惠券模板主键
     * @return 结果
     */
    @Override
    public int deleteJstCouponTemplateByCouponTemplateId(Long couponTemplateId)
    {
        return jstCouponTemplateMapper.deleteJstCouponTemplateByCouponTemplateId(couponTemplateId);
    }
}
