package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstRiskAlert;

/**
 * 风险预警Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstRiskAlertService 
{
    /**
     * 查询风险预警
     * 
     * @param alertId 风险预警主键
     * @return 风险预警
     */
    public JstRiskAlert selectJstRiskAlertByAlertId(Long alertId);

    /**
     * 查询风险预警列表
     * 
     * @param jstRiskAlert 风险预警
     * @return 风险预警集合
     */
    public List<JstRiskAlert> selectJstRiskAlertList(JstRiskAlert jstRiskAlert);

    /**
     * 新增风险预警
     * 
     * @param jstRiskAlert 风险预警
     * @return 结果
     */
    public int insertJstRiskAlert(JstRiskAlert jstRiskAlert);

    /**
     * 修改风险预警
     * 
     * @param jstRiskAlert 风险预警
     * @return 结果
     */
    public int updateJstRiskAlert(JstRiskAlert jstRiskAlert);

    /**
     * 批量删除风险预警
     * 
     * @param alertIds 需要删除的风险预警主键集合
     * @return 结果
     */
    public int deleteJstRiskAlertByAlertIds(Long[] alertIds);

    /**
     * 删除风险预警信息
     * 
     * @param alertId 风险预警主键
     * @return 结果
     */
    public int deleteJstRiskAlertByAlertId(Long alertId);
}
