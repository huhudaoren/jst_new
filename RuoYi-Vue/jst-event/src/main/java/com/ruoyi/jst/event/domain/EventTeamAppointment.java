package com.ruoyi.jst.event.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 团队预约主记录。
 */
public class EventTeamAppointment extends BaseEntity {

    private Long teamAppointmentId;
    private String teamNo;
    private Long contestId;
    private Long channelId;
    private String teamName;
    private Date appointmentDate;
    private String sessionCode;
    private Long totalPersons;
    private Long memberPersons;
    private Long extraPersons;
    private String extraListJson;
    private Long writeoffPersons;
    private String status;
    private String delFlag;

    public Long getTeamAppointmentId() { return teamAppointmentId; }
    public void setTeamAppointmentId(Long teamAppointmentId) { this.teamAppointmentId = teamAppointmentId; }
    public String getTeamNo() { return teamNo; }
    public void setTeamNo(String teamNo) { this.teamNo = teamNo; }
    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }
    public Long getTotalPersons() { return totalPersons; }
    public void setTotalPersons(Long totalPersons) { this.totalPersons = totalPersons; }
    public Long getMemberPersons() { return memberPersons; }
    public void setMemberPersons(Long memberPersons) { this.memberPersons = memberPersons; }
    public Long getExtraPersons() { return extraPersons; }
    public void setExtraPersons(Long extraPersons) { this.extraPersons = extraPersons; }
    public String getExtraListJson() { return extraListJson; }
    public void setExtraListJson(String extraListJson) { this.extraListJson = extraListJson; }
    public Long getWriteoffPersons() { return writeoffPersons; }
    public void setWriteoffPersons(Long writeoffPersons) { this.writeoffPersons = writeoffPersons; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
