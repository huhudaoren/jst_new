package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstRiskRuleMapper;
import com.ruoyi.system.domain.JstRiskRule;
import com.ruoyi.system.service.IJstRiskRuleService;

/**
 * 风控规则Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstRiskRuleServiceImpl implements IJstRiskRuleService 
{
    @Autowired
    private JstRiskRuleMapper jstRiskRuleMapper;

    /**
     * 查询风控规则
     * 
     * @param riskRuleId 风控规则主键
     * @return 风控规则
     */
    @Override
    public JstRiskRule selectJstRiskRuleByRiskRuleId(Long riskRuleId)
    {
        return jstRiskRuleMapper.selectJstRiskRuleByRiskRuleId(riskRuleId);
    }

    /**
     * 查询风控规则列表
     * 
     * @param jstRiskRule 风控规则
     * @return 风控规则
     */
    @Override
    public List<JstRiskRule> selectJstRiskRuleList(JstRiskRule jstRiskRule)
    {
        return jstRiskRuleMapper.selectJstRiskRuleList(jstRiskRule);
    }

    /**
     * 新增风控规则
     * 
     * @param jstRiskRule 风控规则
     * @return 结果
     */
    @Override
    public int insertJstRiskRule(JstRiskRule jstRiskRule)
    {
        jstRiskRule.setCreateTime(DateUtils.getNowDate());
        return jstRiskRuleMapper.insertJstRiskRule(jstRiskRule);
    }

    /**
     * 修改风控规则
     * 
     * @param jstRiskRule 风控规则
     * @return 结果
     */
    @Override
    public int updateJstRiskRule(JstRiskRule jstRiskRule)
    {
        jstRiskRule.setUpdateTime(DateUtils.getNowDate());
        return jstRiskRuleMapper.updateJstRiskRule(jstRiskRule);
    }

    /**
     * 批量删除风控规则
     * 
     * @param riskRuleIds 需要删除的风控规则主键
     * @return 结果
     */
    @Override
    public int deleteJstRiskRuleByRiskRuleIds(Long[] riskRuleIds)
    {
        return jstRiskRuleMapper.deleteJstRiskRuleByRiskRuleIds(riskRuleIds);
    }

    /**
     * 删除风控规则信息
     * 
     * @param riskRuleId 风控规则主键
     * @return 结果
     */
    @Override
    public int deleteJstRiskRuleByRiskRuleId(Long riskRuleId)
    {
        return jstRiskRuleMapper.deleteJstRiskRuleByRiskRuleId(riskRuleId);
    }
}
