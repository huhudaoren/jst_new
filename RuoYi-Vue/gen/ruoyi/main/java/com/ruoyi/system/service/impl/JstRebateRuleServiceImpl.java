package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstRebateRuleMapper;
import com.ruoyi.system.domain.JstRebateRule;
import com.ruoyi.system.service.IJstRebateRuleService;

/**
 * 渠道返点规则Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstRebateRuleServiceImpl implements IJstRebateRuleService 
{
    @Autowired
    private JstRebateRuleMapper jstRebateRuleMapper;

    /**
     * 查询渠道返点规则
     * 
     * @param ruleId 渠道返点规则主键
     * @return 渠道返点规则
     */
    @Override
    public JstRebateRule selectJstRebateRuleByRuleId(Long ruleId)
    {
        return jstRebateRuleMapper.selectJstRebateRuleByRuleId(ruleId);
    }

    /**
     * 查询渠道返点规则列表
     * 
     * @param jstRebateRule 渠道返点规则
     * @return 渠道返点规则
     */
    @Override
    public List<JstRebateRule> selectJstRebateRuleList(JstRebateRule jstRebateRule)
    {
        return jstRebateRuleMapper.selectJstRebateRuleList(jstRebateRule);
    }

    /**
     * 新增渠道返点规则
     * 
     * @param jstRebateRule 渠道返点规则
     * @return 结果
     */
    @Override
    public int insertJstRebateRule(JstRebateRule jstRebateRule)
    {
        jstRebateRule.setCreateTime(DateUtils.getNowDate());
        return jstRebateRuleMapper.insertJstRebateRule(jstRebateRule);
    }

    /**
     * 修改渠道返点规则
     * 
     * @param jstRebateRule 渠道返点规则
     * @return 结果
     */
    @Override
    public int updateJstRebateRule(JstRebateRule jstRebateRule)
    {
        jstRebateRule.setUpdateTime(DateUtils.getNowDate());
        return jstRebateRuleMapper.updateJstRebateRule(jstRebateRule);
    }

    /**
     * 批量删除渠道返点规则
     * 
     * @param ruleIds 需要删除的渠道返点规则主键
     * @return 结果
     */
    @Override
    public int deleteJstRebateRuleByRuleIds(Long[] ruleIds)
    {
        return jstRebateRuleMapper.deleteJstRebateRuleByRuleIds(ruleIds);
    }

    /**
     * 删除渠道返点规则信息
     * 
     * @param ruleId 渠道返点规则主键
     * @return 结果
     */
    @Override
    public int deleteJstRebateRuleByRuleId(Long ruleId)
    {
        return jstRebateRuleMapper.deleteJstRebateRuleByRuleId(ruleId);
    }
}
