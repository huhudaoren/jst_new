package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 个人预约记录对象 jst_appointment_record
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstAppointmentRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 预约ID */
    private Long appointmentId;

    /** 预约号 */
    @Excel(name = "预约号")
    private String appointmentNo;

    /** 关联活动 */
    @Excel(name = "关联活动")
    private Long contestId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 参赛人ID */
    @Excel(name = "参赛人ID")
    private Long participantId;

    /** 锁定渠道方 */
    @Excel(name = "锁定渠道方")
    private Long channelId;

    /** 类型：individual/team */
    @Excel(name = "类型：individual/team")
    private String appointmentType;

    /** 关联团队主记录 */
    @Excel(name = "关联团队主记录")
    private Long teamAppointmentId;

    /** 预约日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预约日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date appointmentDate;

    /** 场次/时段 */
    @Excel(name = "场次/时段")
    private String sessionCode;

    /** 关联订单 */
    @Excel(name = "关联订单")
    private Long orderId;

    /** 主状态：unbooked/booked/partial_writeoff/fully_writeoff/partial_writeoff_ended/cancelled/expired/no_show */
    @Excel(name = "主状态：unbooked/booked/partial_writeoff/fully_writeoff/partial_writeoff_ended/cancelled/expired/no_show")
    private String mainStatus;

    /** 主二维码URL */
    @Excel(name = "主二维码URL")
    private String qrCode;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setAppointmentId(Long appointmentId) 
    {
        this.appointmentId = appointmentId;
    }

    public Long getAppointmentId() 
    {
        return appointmentId;
    }

    public void setAppointmentNo(String appointmentNo) 
    {
        this.appointmentNo = appointmentNo;
    }

    public String getAppointmentNo() 
    {
        return appointmentNo;
    }

    public void setContestId(Long contestId) 
    {
        this.contestId = contestId;
    }

    public Long getContestId() 
    {
        return contestId;
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

    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }

    public void setAppointmentType(String appointmentType) 
    {
        this.appointmentType = appointmentType;
    }

    public String getAppointmentType() 
    {
        return appointmentType;
    }

    public void setTeamAppointmentId(Long teamAppointmentId) 
    {
        this.teamAppointmentId = teamAppointmentId;
    }

    public Long getTeamAppointmentId() 
    {
        return teamAppointmentId;
    }

    public void setAppointmentDate(Date appointmentDate) 
    {
        this.appointmentDate = appointmentDate;
    }

    public Date getAppointmentDate() 
    {
        return appointmentDate;
    }

    public void setSessionCode(String sessionCode) 
    {
        this.sessionCode = sessionCode;
    }

    public String getSessionCode() 
    {
        return sessionCode;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setMainStatus(String mainStatus) 
    {
        this.mainStatus = mainStatus;
    }

    public String getMainStatus() 
    {
        return mainStatus;
    }

    public void setQrCode(String qrCode) 
    {
        this.qrCode = qrCode;
    }

    public String getQrCode() 
    {
        return qrCode;
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
            .append("appointmentId", getAppointmentId())
            .append("appointmentNo", getAppointmentNo())
            .append("contestId", getContestId())
            .append("userId", getUserId())
            .append("participantId", getParticipantId())
            .append("channelId", getChannelId())
            .append("appointmentType", getAppointmentType())
            .append("teamAppointmentId", getTeamAppointmentId())
            .append("appointmentDate", getAppointmentDate())
            .append("sessionCode", getSessionCode())
            .append("orderId", getOrderId())
            .append("mainStatus", getMainStatus())
            .append("qrCode", getQrCode())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
