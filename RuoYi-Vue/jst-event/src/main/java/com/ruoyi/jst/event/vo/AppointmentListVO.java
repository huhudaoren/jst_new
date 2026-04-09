package com.ruoyi.jst.event.vo;

import java.util.Date;

/**
 * 我的预约列表项。
 */
public class AppointmentListVO {

    private Long appointmentId;
    private Long contestId;
    private String contestName;
    private Date appointmentDate;
    private String sessionCode;
    private String mainStatus;
    private String appointmentType;
    private Boolean individualAppointment;
    private Boolean teamAppointment;
    private Long teamAppointmentId;
    private String teamNo;
    private String teamName;
    private Integer writeoffDoneCount;
    private Integer writeoffTotalCount;

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public String getContestName() { return contestName; }
    public void setContestName(String contestName) { this.contestName = contestName; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }
    public String getMainStatus() { return mainStatus; }
    public void setMainStatus(String mainStatus) { this.mainStatus = mainStatus; }
    public String getAppointmentType() { return appointmentType; }
    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }
    public Boolean getIndividualAppointment() { return individualAppointment; }
    public void setIndividualAppointment(Boolean individualAppointment) { this.individualAppointment = individualAppointment; }
    public Boolean getTeamAppointment() { return teamAppointment; }
    public void setTeamAppointment(Boolean teamAppointment) { this.teamAppointment = teamAppointment; }
    public Long getTeamAppointmentId() { return teamAppointmentId; }
    public void setTeamAppointmentId(Long teamAppointmentId) { this.teamAppointmentId = teamAppointmentId; }
    public String getTeamNo() { return teamNo; }
    public void setTeamNo(String teamNo) { this.teamNo = teamNo; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public Integer getWriteoffDoneCount() { return writeoffDoneCount; }
    public void setWriteoffDoneCount(Integer writeoffDoneCount) { this.writeoffDoneCount = writeoffDoneCount; }
    public Integer getWriteoffTotalCount() { return writeoffTotalCount; }
    public void setWriteoffTotalCount(Integer writeoffTotalCount) { this.writeoffTotalCount = writeoffTotalCount; }
}
