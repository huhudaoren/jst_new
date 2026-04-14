package com.ruoyi.jst.common.mapper;

import com.ruoyi.jst.common.domain.JstBizNoRule;
import com.ruoyi.jst.common.dto.BizNoRuleQueryReqDTO;

import java.util.List;

/**
 * 业务编号规则 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstBizNoRuleMapper {

    /**
     * 按主键查询编号规则。
     *
     * @param ruleId 规则ID
     * @return 规则实体
     */
    JstBizNoRule selectJstBizNoRuleByRuleId(Long ruleId);

    /**
     * 按规则编码查询。
     *
     * @param ruleCode 规则编码
     * @return 规则实体
     */
    JstBizNoRule selectByRuleCode(String ruleCode);

    /**
     * 查询规则列表。
     *
     * @param query 查询条件
     * @return 规则列表
     */
    List<JstBizNoRule> selectJstBizNoRuleList(BizNoRuleQueryReqDTO query);

    /**
     * 新增规则。
     *
     * @param rule 规则实体
     * @return 影响行数
     */
    int insertJstBizNoRule(JstBizNoRule rule);

    /**
     * 修改规则。
     *
     * @param rule 规则实体
     * @return 影响行数
     */
    int updateJstBizNoRule(JstBizNoRule rule);

    /**
     * 批量删除规则。
     *
     * @param ruleIds 规则ID数组
     * @return 影响行数
     */
    int deleteJstBizNoRuleByRuleIds(Long[] ruleIds);
}
