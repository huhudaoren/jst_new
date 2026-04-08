package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstTeamAppointmentMember;

/**
 * 团队预约成员明细Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstTeamAppointmentMemberService 
{
    /**
     * 查询团队预约成员明细
     * 
     * @param memberId 团队预约成员明细主键
     * @return 团队预约成员明细
     */
    public JstTeamAppointmentMember selectJstTeamAppointmentMemberByMemberId(Long memberId);

    /**
     * 查询团队预约成员明细列表
     * 
     * @param jstTeamAppointmentMember 团队预约成员明细
     * @return 团队预约成员明细集合
     */
    public List<JstTeamAppointmentMember> selectJstTeamAppointmentMemberList(JstTeamAppointmentMember jstTeamAppointmentMember);

    /**
     * 新增团队预约成员明细
     * 
     * @param jstTeamAppointmentMember 团队预约成员明细
     * @return 结果
     */
    public int insertJstTeamAppointmentMember(JstTeamAppointmentMember jstTeamAppointmentMember);

    /**
     * 修改团队预约成员明细
     * 
     * @param jstTeamAppointmentMember 团队预约成员明细
     * @return 结果
     */
    public int updateJstTeamAppointmentMember(JstTeamAppointmentMember jstTeamAppointmentMember);

    /**
     * 批量删除团队预约成员明细
     * 
     * @param memberIds 需要删除的团队预约成员明细主键集合
     * @return 结果
     */
    public int deleteJstTeamAppointmentMemberByMemberIds(Long[] memberIds);

    /**
     * 删除团队预约成员明细信息
     * 
     * @param memberId 团队预约成员明细主键
     * @return 结果
     */
    public int deleteJstTeamAppointmentMemberByMemberId(Long memberId);
}
