package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstRiskAlertMapper;
import com.ruoyi.system.domain.JstRiskAlert;
import com.ruoyi.system.service.IJstRiskAlertService;

/**
 * 风险预警Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstRiskAlertServiceImpl implements IJstRiskAlertService 
{
    @Autowired
    private JstRiskAlertMapper jstRiskAlertMapper;

    /**
     * 查询风险预警
     * 
     * @param alertId 风险预警主键
     * @return 风险预警
     */
    @Override
    public JstRiskAlert selectJstRiskAlertByAlertId(Long alertId)
    {
        return jstRiskAlertMapper.selectJstRiskAlertByAlertId(alertId);
    }

    /**
     * 查询风险预警列表
     * 
     * @param jstRiskAlert 风险预警
     * @return 风险预警
     */
    @Override
    public List<JstRiskAlert> selectJstRiskAlertList(JstRiskAlert jstRiskAlert)
    {
        return jstRiskAlertMapper.selectJstRiskAlertList(jstRiskAlert);
    }

    /**
     * 新增风险预警
     * 
     * @param jstRiskAlert 风险预警
     * @return 结果
     */
    @Override
    public int insertJstRiskAlert(JstRiskAlert jstRiskAlert)
    {
        jstRiskAlert.setCreateTime(DateUtils.getNowDate());
        return jstRiskAlertMapper.insertJstRiskAlert(jstRiskAlert);
    }

    /**
     * 修改风险预警
     * 
     * @param jstRiskAlert 风险预警
     * @return 结果
     */
    @Override
    public int updateJstRiskAlert(JstRiskAlert jstRiskAlert)
    {
        jstRiskAlert.setUpdateTime(DateUtils.getNowDate());
        return jstRiskAlertMapper.updateJstRiskAlert(jstRiskAlert);
    }

    /**
     * 批量删除风险预警
     * 
     * @param alertIds 需要删除的风险预警主键
     * @return 结果
     */
    @Override
    public int deleteJstRiskAlertByAlertIds(Long[] alertIds)
    {
        return jstRiskAlertMapper.deleteJstRiskAlertByAlertIds(alertIds);
    }

    /**
     * 删除风险预警信息
     * 
     * @param alertId 风险预警主键
     * @return 结果
     */
    @Override
    public int deleteJstRiskAlertByAlertId(Long alertId)
    {
        return jstRiskAlertMapper.deleteJstRiskAlertByAlertId(alertId);
    }
}
