package com.ruoyi.jst.event.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 个人预约记录。
 */
public class EventAppointmentRecord extends BaseEntity {

    private Long appointmentId;
    private String appointmentNo;
    private Long contestId;
    private Long userId;
    private Long participantId;
    private Long channelId;
    private String appointmentType;
    private Long teamAppointmentId;
    private Date appointmentDate;
    private String sessionCode;
    private Long orderId;
    private String mainStatus;
    private String qrCode;
    private String delFlag;

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public String getAppointmentNo() { return appointmentNo; }
    public void setAppointmentNo(String appointmentNo) { this.appointmentNo = appointmentNo; }
    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public String getAppointmentType() { return appointmentType; }
    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }
    public Long getTeamAppointmentId() { return teamAppointmentId; }
    public void setTeamAppointmentId(Long teamAppointmentId) { this.teamAppointmentId = teamAppointmentId; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getMainStatus() { return mainStatus; }
    public void setMainStatus(String mainStatus) { this.mainStatus = mainStatus; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
