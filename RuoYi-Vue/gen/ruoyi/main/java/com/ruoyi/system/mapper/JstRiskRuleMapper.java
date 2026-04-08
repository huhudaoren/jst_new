package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.JstRiskRule;

/**
 * 风控规则Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstRiskRuleMapper 
{
    /**
     * 查询风控规则
     * 
     * @param riskRuleId 风控规则主键
     * @return 风控规则
     */
    public JstRiskRule selectJstRiskRuleByRiskRuleId(Long riskRuleId);

    /**
     * 查询风控规则列表
     * 
     * @param jstRiskRule 风控规则
     * @return 风控规则集合
     */
    public List<JstRiskRule> selectJstRiskRuleList(JstRiskRule jstRiskRule);

    /**
     * 新增风控规则
     * 
     * @param jstRiskRule 风控规则
     * @return 结果
     */
    public int insertJstRiskRule(JstRiskRule jstRiskRule);

    /**
     * 修改风控规则
     * 
     * @param jstRiskRule 风控规则
     * @return 结果
     */
    public int updateJstRiskRule(JstRiskRule jstRiskRule);

    /**
     * 删除风控规则
     * 
     * @param riskRuleId 风控规则主键
     * @return 结果
     */
    public int deleteJstRiskRuleByRiskRuleId(Long riskRuleId);

    /**
     * 批量删除风控规则
     * 
     * @param riskRuleIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstRiskRuleByRiskRuleIds(Long[] riskRuleIds);
}
