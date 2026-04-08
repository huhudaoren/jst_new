package com.ruoyi.jst.risk.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.risk.mapper.JstRiskCaseMapper;
import com.ruoyi.jst.risk.domain.JstRiskCase;
import com.ruoyi.jst.risk.service.IJstRiskCaseService;

/**
 * 风险工单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstRiskCaseServiceImpl implements IJstRiskCaseService 
{
    @Autowired
    private JstRiskCaseMapper jstRiskCaseMapper;

    /**
     * 查询风险工单
     * 
     * @param caseId 风险工单主键
     * @return 风险工单
     */
    @Override
    public JstRiskCase selectJstRiskCaseByCaseId(Long caseId)
    {
        return jstRiskCaseMapper.selectJstRiskCaseByCaseId(caseId);
    }

    /**
     * 查询风险工单列表
     * 
     * @param jstRiskCase 风险工单
     * @return 风险工单
     */
    @Override
    public List<JstRiskCase> selectJstRiskCaseList(JstRiskCase jstRiskCase)
    {
        return jstRiskCaseMapper.selectJstRiskCaseList(jstRiskCase);
    }

    /**
     * 新增风险工单
     * 
     * @param jstRiskCase 风险工单
     * @return 结果
     */
    @Override
    public int insertJstRiskCase(JstRiskCase jstRiskCase)
    {
        jstRiskCase.setCreateTime(DateUtils.getNowDate());
        return jstRiskCaseMapper.insertJstRiskCase(jstRiskCase);
    }

    /**
     * 修改风险工单
     * 
     * @param jstRiskCase 风险工单
     * @return 结果
     */
    @Override
    public int updateJstRiskCase(JstRiskCase jstRiskCase)
    {
        jstRiskCase.setUpdateTime(DateUtils.getNowDate());
        return jstRiskCaseMapper.updateJstRiskCase(jstRiskCase);
    }

    /**
     * 批量删除风险工单
     * 
     * @param caseIds 需要删除的风险工单主键
     * @return 结果
     */
    @Override
    public int deleteJstRiskCaseByCaseIds(Long[] caseIds)
    {
        return jstRiskCaseMapper.deleteJstRiskCaseByCaseIds(caseIds);
    }

    /**
     * 删除风险工单信息
     * 
     * @param caseId 风险工单主键
     * @return 结果
     */
    @Override
    public int deleteJstRiskCaseByCaseId(Long caseId)
    {
        return jstRiskCaseMapper.deleteJstRiskCaseByCaseId(caseId);
    }
}
