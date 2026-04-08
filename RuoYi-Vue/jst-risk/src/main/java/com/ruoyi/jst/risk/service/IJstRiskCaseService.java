package com.ruoyi.jst.risk.service;

import java.util.List;
import com.ruoyi.jst.risk.domain.JstRiskCase;

/**
 * 风险工单Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstRiskCaseService 
{
    /**
     * 查询风险工单
     * 
     * @param caseId 风险工单主键
     * @return 风险工单
     */
    public JstRiskCase selectJstRiskCaseByCaseId(Long caseId);

    /**
     * 查询风险工单列表
     * 
     * @param jstRiskCase 风险工单
     * @return 风险工单集合
     */
    public List<JstRiskCase> selectJstRiskCaseList(JstRiskCase jstRiskCase);

    /**
     * 新增风险工单
     * 
     * @param jstRiskCase 风险工单
     * @return 结果
     */
    public int insertJstRiskCase(JstRiskCase jstRiskCase);

    /**
     * 修改风险工单
     * 
     * @param jstRiskCase 风险工单
     * @return 结果
     */
    public int updateJstRiskCase(JstRiskCase jstRiskCase);

    /**
     * 批量删除风险工单
     * 
     * @param caseIds 需要删除的风险工单主键集合
     * @return 结果
     */
    public int deleteJstRiskCaseByCaseIds(Long[] caseIds);

    /**
     * 删除风险工单信息
     * 
     * @param caseId 风险工单主键
     * @return 结果
     */
    public int deleteJstRiskCaseByCaseId(Long caseId);
}
