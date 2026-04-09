package com.ruoyi.jst.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 团队预约查询入参。
 */
public class TeamAppointmentQueryDTO {

    private Long contestId;
    private String status;
    private String sessionCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;

    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
}
