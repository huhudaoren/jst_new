package com.ruoyi.jst.points.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.points.mapper.JstPointsRuleMapper;
import com.ruoyi.jst.points.domain.JstPointsRule;
import com.ruoyi.jst.points.service.IJstPointsRuleService;

/**
 * 积分/成长值规则Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstPointsRuleServiceImpl implements IJstPointsRuleService 
{
    @Autowired
    private JstPointsRuleMapper jstPointsRuleMapper;

    /**
     * 查询积分/成长值规则
     * 
     * @param ruleId 积分/成长值规则主键
     * @return 积分/成长值规则
     */
    @Override
    public JstPointsRule selectJstPointsRuleByRuleId(Long ruleId)
    {
        return jstPointsRuleMapper.selectJstPointsRuleByRuleId(ruleId);
    }

    /**
     * 查询积分/成长值规则列表
     * 
     * @param jstPointsRule 积分/成长值规则
     * @return 积分/成长值规则
     */
    @Override
    public List<JstPointsRule> selectJstPointsRuleList(JstPointsRule jstPointsRule)
    {
        return jstPointsRuleMapper.selectJstPointsRuleList(jstPointsRule);
    }

    /**
     * 新增积分/成长值规则
     * 
     * @param jstPointsRule 积分/成长值规则
     * @return 结果
     */
    @Override
    public int insertJstPointsRule(JstPointsRule jstPointsRule)
    {
        jstPointsRule.setCreateTime(DateUtils.getNowDate());
        return jstPointsRuleMapper.insertJstPointsRule(jstPointsRule);
    }

    /**
     * 修改积分/成长值规则
     * 
     * @param jstPointsRule 积分/成长值规则
     * @return 结果
     */
    @Override
    public int updateJstPointsRule(JstPointsRule jstPointsRule)
    {
        jstPointsRule.setUpdateTime(DateUtils.getNowDate());
        return jstPointsRuleMapper.updateJstPointsRule(jstPointsRule);
    }

    /**
     * 批量删除积分/成长值规则
     * 
     * @param ruleIds 需要删除的积分/成长值规则主键
     * @return 结果
     */
    @Override
    public int deleteJstPointsRuleByRuleIds(Long[] ruleIds)
    {
        return jstPointsRuleMapper.deleteJstPointsRuleByRuleIds(ruleIds);
    }

    /**
     * 删除积分/成长值规则信息
     * 
     * @param ruleId 积分/成长值规则主键
     * @return 结果
     */
    @Override
    public int deleteJstPointsRuleByRuleId(Long ruleId)
    {
        return jstPointsRuleMapper.deleteJstPointsRuleByRuleId(ruleId);
    }
}
