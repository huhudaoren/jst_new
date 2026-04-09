package com.ruoyi.jst.event.vo;

import java.util.Date;

/**
 * 团队预约列表项。
 */
public class TeamAppointmentListVO {

    private Long teamAppointmentId;
    private String teamNo;
    private String teamName;
    private Long contestId;
    private String contestName;
    private Date appointmentDate;
    private String sessionCode;
    private Long totalPersons;
    private Long writeoffPersons;
    private String status;

    public Long getTeamAppointmentId() { return teamAppointmentId; }
    public void setTeamAppointmentId(Long teamAppointmentId) { this.teamAppointmentId = teamAppointmentId; }
    public String getTeamNo() { return teamNo; }
    public void setTeamNo(String teamNo) { this.teamNo = teamNo; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public String getContestName() { return contestName; }
    public void setContestName(String contestName) { this.contestName = contestName; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }
    public Long getTotalPersons() { return totalPersons; }
    public void setTotalPersons(Long totalPersons) { this.totalPersons = totalPersons; }
    public Long getWriteoffPersons() { return writeoffPersons; }
    public void setWriteoffPersons(Long writeoffPersons) { this.writeoffPersons = writeoffPersons; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
