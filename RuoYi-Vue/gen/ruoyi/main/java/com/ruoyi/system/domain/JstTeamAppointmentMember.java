package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 团队预约成员明细对象 jst_team_appointment_member
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstTeamAppointmentMember extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 成员明细ID */
    private Long memberId;

    /** 团队预约主ID */
    @Excel(name = "团队预约主ID")
    private Long teamAppointmentId;

    /** 正式用户ID */
    @Excel(name = "正式用户ID")
    private Long userId;

    /** 参赛人ID（含临时档案） */
    @Excel(name = "参赛人ID", readConverterExp = "含=临时档案")
    private Long participantId;

    /** 成员编号 */
    @Excel(name = "成员编号")
    private String memberNo;

    /** 姓名快照 */
    @Excel(name = "姓名快照")
    private String nameSnapshot;

    /** 手机快照 */
    @Excel(name = "手机快照")
    private String mobileSnapshot;

    /** 个人子订单ID */
    @Excel(name = "个人子订单ID")
    private Long subOrderId;

    /** 核销状态：unused/used/expired/voided */
    @Excel(name = "核销状态：unused/used/expired/voided")
    private String writeoffStatus;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setMemberId(Long memberId) 
    {
        this.memberId = memberId;
    }

    public Long getMemberId() 
    {
        return memberId;
    }

    public void setTeamAppointmentId(Long teamAppointmentId) 
    {
        this.teamAppointmentId = teamAppointmentId;
    }

    public Long getTeamAppointmentId() 
    {
        return teamAppointmentId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setParticipantId(Long participantId) 
    {
        this.participantId = participantId;
    }

    public Long getParticipantId() 
    {
        return participantId;
    }

    public void setMemberNo(String memberNo) 
    {
        this.memberNo = memberNo;
    }

    public String getMemberNo() 
    {
        return memberNo;
    }

    public void setNameSnapshot(String nameSnapshot) 
    {
        this.nameSnapshot = nameSnapshot;
    }

    public String getNameSnapshot() 
    {
        return nameSnapshot;
    }

    public void setMobileSnapshot(String mobileSnapshot) 
    {
        this.mobileSnapshot = mobileSnapshot;
    }

    public String getMobileSnapshot() 
    {
        return mobileSnapshot;
    }

    public void setSubOrderId(Long subOrderId) 
    {
        this.subOrderId = subOrderId;
    }

    public Long getSubOrderId() 
    {
        return subOrderId;
    }

    public void setWriteoffStatus(String writeoffStatus) 
    {
        this.writeoffStatus = writeoffStatus;
    }

    public String getWriteoffStatus() 
    {
        return writeoffStatus;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("memberId", getMemberId())
            .append("teamAppointmentId", getTeamAppointmentId())
            .append("userId", getUserId())
            .append("participantId", getParticipantId())
            .append("memberNo", getMemberNo())
            .append("nameSnapshot", getNameSnapshot())
            .append("mobileSnapshot", getMobileSnapshot())
            .append("subOrderId", getSubOrderId())
            .append("writeoffStatus", getWriteoffStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
