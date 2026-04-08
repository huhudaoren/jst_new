package com.ruoyi.jst.order.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.order.mapper.JstTeamAppointmentMemberMapper;
import com.ruoyi.jst.order.domain.JstTeamAppointmentMember;
import com.ruoyi.jst.order.service.IJstTeamAppointmentMemberService;

/**
 * 团队预约成员明细Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstTeamAppointmentMemberServiceImpl implements IJstTeamAppointmentMemberService 
{
    @Autowired
    private JstTeamAppointmentMemberMapper jstTeamAppointmentMemberMapper;

    /**
     * 查询团队预约成员明细
     * 
     * @param memberId 团队预约成员明细主键
     * @return 团队预约成员明细
     */
    @Override
    public JstTeamAppointmentMember selectJstTeamAppointmentMemberByMemberId(Long memberId)
    {
        return jstTeamAppointmentMemberMapper.selectJstTeamAppointmentMemberByMemberId(memberId);
    }

    /**
     * 查询团队预约成员明细列表
     * 
     * @param jstTeamAppointmentMember 团队预约成员明细
     * @return 团队预约成员明细
     */
    @Override
    public List<JstTeamAppointmentMember> selectJstTeamAppointmentMemberList(JstTeamAppointmentMember jstTeamAppointmentMember)
    {
        return jstTeamAppointmentMemberMapper.selectJstTeamAppointmentMemberList(jstTeamAppointmentMember);
    }

    /**
     * 新增团队预约成员明细
     * 
     * @param jstTeamAppointmentMember 团队预约成员明细
     * @return 结果
     */
    @Override
    public int insertJstTeamAppointmentMember(JstTeamAppointmentMember jstTeamAppointmentMember)
    {
        jstTeamAppointmentMember.setCreateTime(DateUtils.getNowDate());
        return jstTeamAppointmentMemberMapper.insertJstTeamAppointmentMember(jstTeamAppointmentMember);
    }

    /**
     * 修改团队预约成员明细
     * 
     * @param jstTeamAppointmentMember 团队预约成员明细
     * @return 结果
     */
    @Override
    public int updateJstTeamAppointmentMember(JstTeamAppointmentMember jstTeamAppointmentMember)
    {
        jstTeamAppointmentMember.setUpdateTime(DateUtils.getNowDate());
        return jstTeamAppointmentMemberMapper.updateJstTeamAppointmentMember(jstTeamAppointmentMember);
    }

    /**
     * 批量删除团队预约成员明细
     * 
     * @param memberIds 需要删除的团队预约成员明细主键
     * @return 结果
     */
    @Override
    public int deleteJstTeamAppointmentMemberByMemberIds(Long[] memberIds)
    {
        return jstTeamAppointmentMemberMapper.deleteJstTeamAppointmentMemberByMemberIds(memberIds);
    }

    /**
     * 删除团队预约成员明细信息
     * 
     * @param memberId 团队预约成员明细主键
     * @return 结果
     */
    @Override
    public int deleteJstTeamAppointmentMemberByMemberId(Long memberId)
    {
        return jstTeamAppointmentMemberMapper.deleteJstTeamAppointmentMemberByMemberId(memberId);
    }
}
