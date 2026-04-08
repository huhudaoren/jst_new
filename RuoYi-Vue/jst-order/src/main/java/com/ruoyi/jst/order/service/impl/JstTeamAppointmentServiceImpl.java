package com.ruoyi.jst.order.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.order.mapper.JstTeamAppointmentMapper;
import com.ruoyi.jst.order.domain.JstTeamAppointment;
import com.ruoyi.jst.order.service.IJstTeamAppointmentService;

/**
 * 团队预约主记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstTeamAppointmentServiceImpl implements IJstTeamAppointmentService 
{
    @Autowired
    private JstTeamAppointmentMapper jstTeamAppointmentMapper;

    /**
     * 查询团队预约主记录
     * 
     * @param teamAppointmentId 团队预约主记录主键
     * @return 团队预约主记录
     */
    @Override
    public JstTeamAppointment selectJstTeamAppointmentByTeamAppointmentId(Long teamAppointmentId)
    {
        return jstTeamAppointmentMapper.selectJstTeamAppointmentByTeamAppointmentId(teamAppointmentId);
    }

    /**
     * 查询团队预约主记录列表
     * 
     * @param jstTeamAppointment 团队预约主记录
     * @return 团队预约主记录
     */
    @Override
    public List<JstTeamAppointment> selectJstTeamAppointmentList(JstTeamAppointment jstTeamAppointment)
    {
        return jstTeamAppointmentMapper.selectJstTeamAppointmentList(jstTeamAppointment);
    }

    /**
     * 新增团队预约主记录
     * 
     * @param jstTeamAppointment 团队预约主记录
     * @return 结果
     */
    @Override
    public int insertJstTeamAppointment(JstTeamAppointment jstTeamAppointment)
    {
        jstTeamAppointment.setCreateTime(DateUtils.getNowDate());
        return jstTeamAppointmentMapper.insertJstTeamAppointment(jstTeamAppointment);
    }

    /**
     * 修改团队预约主记录
     * 
     * @param jstTeamAppointment 团队预约主记录
     * @return 结果
     */
    @Override
    public int updateJstTeamAppointment(JstTeamAppointment jstTeamAppointment)
    {
        jstTeamAppointment.setUpdateTime(DateUtils.getNowDate());
        return jstTeamAppointmentMapper.updateJstTeamAppointment(jstTeamAppointment);
    }

    /**
     * 批量删除团队预约主记录
     * 
     * @param teamAppointmentIds 需要删除的团队预约主记录主键
     * @return 结果
     */
    @Override
    public int deleteJstTeamAppointmentByTeamAppointmentIds(Long[] teamAppointmentIds)
    {
        return jstTeamAppointmentMapper.deleteJstTeamAppointmentByTeamAppointmentIds(teamAppointmentIds);
    }

    /**
     * 删除团队预约主记录信息
     * 
     * @param teamAppointmentId 团队预约主记录主键
     * @return 结果
     */
    @Override
    public int deleteJstTeamAppointmentByTeamAppointmentId(Long teamAppointmentId)
    {
        return jstTeamAppointmentMapper.deleteJstTeamAppointmentByTeamAppointmentId(teamAppointmentId);
    }
}
