package com.ruoyi.jst.event.vo;

import java.util.Date;
import java.util.List;

/**
 * 我的预约详情。
 */
public class AppointmentDetailVO {

    private Long appointmentId;
    private Long contestId;
    private String contestName;
    private Long participantId;
    private String participantName;
    private Date appointmentDate;
    private String sessionCode;
    private String mainStatus;
    private String appointmentType;
    private Long teamAppointmentId;
    private String teamNo;
    private String teamName;
    private String qrCode;
    private List<AppointmentWriteoffItemVO> writeoffItems;

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public String getContestName() { return contestName; }
    public void setContestName(String contestName) { this.contestName = contestName; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }
    public String getMainStatus() { return mainStatus; }
    public void setMainStatus(String mainStatus) { this.mainStatus = mainStatus; }
    public String getAppointmentType() { return appointmentType; }
    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }
    public Long getTeamAppointmentId() { return teamAppointmentId; }
    public void setTeamAppointmentId(Long teamAppointmentId) { this.teamAppointmentId = teamAppointmentId; }
    public String getTeamNo() { return teamNo; }
    public void setTeamNo(String teamNo) { this.teamNo = teamNo; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public List<AppointmentWriteoffItemVO> getWriteoffItems() { return writeoffItems; }
    public void setWriteoffItems(List<AppointmentWriteoffItemVO> writeoffItems) { this.writeoffItems = writeoffItems; }
}
