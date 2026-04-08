package com.ruoyi.jst.channel.service;

import java.util.List;
import com.ruoyi.jst.channel.domain.JstRebateRule;

/**
 * 渠道返点规则Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstRebateRuleService 
{
    /**
     * 查询渠道返点规则
     * 
     * @param ruleId 渠道返点规则主键
     * @return 渠道返点规则
     */
    public JstRebateRule selectJstRebateRuleByRuleId(Long ruleId);

    /**
     * 查询渠道返点规则列表
     * 
     * @param jstRebateRule 渠道返点规则
     * @return 渠道返点规则集合
     */
    public List<JstRebateRule> selectJstRebateRuleList(JstRebateRule jstRebateRule);

    /**
     * 新增渠道返点规则
     * 
     * @param jstRebateRule 渠道返点规则
     * @return 结果
     */
    public int insertJstRebateRule(JstRebateRule jstRebateRule);

    /**
     * 修改渠道返点规则
     * 
     * @param jstRebateRule 渠道返点规则
     * @return 结果
     */
    public int updateJstRebateRule(JstRebateRule jstRebateRule);

    /**
     * 批量删除渠道返点规则
     * 
     * @param ruleIds 需要删除的渠道返点规则主键集合
     * @return 结果
     */
    public int deleteJstRebateRuleByRuleIds(Long[] ruleIds);

    /**
     * 删除渠道返点规则信息
     * 
     * @param ruleId 渠道返点规则主键
     * @return 结果
     */
    public int deleteJstRebateRuleByRuleId(Long ruleId);
}
