package com.ruoyi.jst.event.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 场次剩余名额视图。
 */
public class AppointmentRemainingVO {

    private Long contestId;
    private String sessionCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;

    private Integer appointmentCapacity;
    private Integer individualBookedPersons;
    private Integer teamBookedPersons;
    private Integer bookedPersons;
    private Integer remainingCapacity;

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Integer getAppointmentCapacity() {
        return appointmentCapacity;
    }

    public void setAppointmentCapacity(Integer appointmentCapacity) {
        this.appointmentCapacity = appointmentCapacity;
    }

    public Integer getIndividualBookedPersons() {
        return individualBookedPersons;
    }

    public void setIndividualBookedPersons(Integer individualBookedPersons) {
        this.individualBookedPersons = individualBookedPersons;
    }

    public Integer getTeamBookedPersons() {
        return teamBookedPersons;
    }

    public void setTeamBookedPersons(Integer teamBookedPersons) {
        this.teamBookedPersons = teamBookedPersons;
    }

    public Integer getBookedPersons() {
        return bookedPersons;
    }

    public void setBookedPersons(Integer bookedPersons) {
        this.bookedPersons = bookedPersons;
    }

    public Integer getRemainingCapacity() {
        return remainingCapacity;
    }

    public void setRemainingCapacity(Integer remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }
}
