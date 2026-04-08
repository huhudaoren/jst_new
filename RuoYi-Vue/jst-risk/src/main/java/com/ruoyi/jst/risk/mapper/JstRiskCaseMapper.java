package com.ruoyi.jst.risk.mapper;

import java.util.List;
import com.ruoyi.jst.risk.domain.JstRiskCase;

/**
 * 风险工单Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstRiskCaseMapper 
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
     * 删除风险工单
     * 
     * @param caseId 风险工单主键
     * @return 结果
     */
    public int deleteJstRiskCaseByCaseId(Long caseId);

    /**
     * 批量删除风险工单
     * 
     * @param caseIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstRiskCaseByCaseIds(Long[] caseIds);
}
