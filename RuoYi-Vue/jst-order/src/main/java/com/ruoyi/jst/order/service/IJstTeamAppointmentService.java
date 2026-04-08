package com.ruoyi.jst.order.service;

import java.util.List;
import com.ruoyi.jst.order.domain.JstTeamAppointment;

/**
 * 团队预约主记录Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstTeamAppointmentService 
{
    /**
     * 查询团队预约主记录
     * 
     * @param teamAppointmentId 团队预约主记录主键
     * @return 团队预约主记录
     */
    public JstTeamAppointment selectJstTeamAppointmentByTeamAppointmentId(Long teamAppointmentId);

    /**
     * 查询团队预约主记录列表
     * 
     * @param jstTeamAppointment 团队预约主记录
     * @return 团队预约主记录集合
     */
    public List<JstTeamAppointment> selectJstTeamAppointmentList(JstTeamAppointment jstTeamAppointment);

    /**
     * 新增团队预约主记录
     * 
     * @param jstTeamAppointment 团队预约主记录
     * @return 结果
     */
    public int insertJstTeamAppointment(JstTeamAppointment jstTeamAppointment);

    /**
     * 修改团队预约主记录
     * 
     * @param jstTeamAppointment 团队预约主记录
     * @return 结果
     */
    public int updateJstTeamAppointment(JstTeamAppointment jstTeamAppointment);

    /**
     * 批量删除团队预约主记录
     * 
     * @param teamAppointmentIds 需要删除的团队预约主记录主键集合
     * @return 结果
     */
    public int deleteJstTeamAppointmentByTeamAppointmentIds(Long[] teamAppointmentIds);

    /**
     * 删除团队预约主记录信息
     * 
     * @param teamAppointmentId 团队预约主记录主键
     * @return 结果
     */
    public int deleteJstTeamAppointmentByTeamAppointmentId(Long teamAppointmentId);
}
