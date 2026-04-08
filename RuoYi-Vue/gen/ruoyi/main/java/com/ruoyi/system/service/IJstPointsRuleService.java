package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstPointsRule;

/**
 * 积分/成长值规则Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstPointsRuleService 
{
    /**
     * 查询积分/成长值规则
     * 
     * @param ruleId 积分/成长值规则主键
     * @return 积分/成长值规则
     */
    public JstPointsRule selectJstPointsRuleByRuleId(Long ruleId);

    /**
     * 查询积分/成长值规则列表
     * 
     * @param jstPointsRule 积分/成长值规则
     * @return 积分/成长值规则集合
     */
    public List<JstPointsRule> selectJstPointsRuleList(JstPointsRule jstPointsRule);

    /**
     * 新增积分/成长值规则
     * 
     * @param jstPointsRule 积分/成长值规则
     * @return 结果
     */
    public int insertJstPointsRule(JstPointsRule jstPointsRule);

    /**
     * 修改积分/成长值规则
     * 
     * @param jstPointsRule 积分/成长值规则
     * @return 结果
     */
    public int updateJstPointsRule(JstPointsRule jstPointsRule);

    /**
     * 批量删除积分/成长值规则
     * 
     * @param ruleIds 需要删除的积分/成长值规则主键集合
     * @return 结果
     */
    public int deleteJstPointsRuleByRuleIds(Long[] ruleIds);

    /**
     * 删除积分/成长值规则信息
     * 
     * @param ruleId 积分/成长值规则主键
     * @return 结果
     */
    public int deleteJstPointsRuleByRuleId(Long ruleId);
}
