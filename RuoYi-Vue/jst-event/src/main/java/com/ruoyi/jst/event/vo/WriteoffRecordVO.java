package com.ruoyi.jst.event.vo;

import java.util.Date;

/**
 * 核销记录列表项。
 */
public class WriteoffRecordVO {

    private Long writeoffItemId;
    private Long contestId;
    private String contestName;
    private String teamName;
    private String memberName;
    private String itemType;
    private String itemName;
    private String status;
    private String sessionCode;
    private Date appointmentDate;
    private Date writeoffTime;
    private String writeoffTerminal;

    public Long getWriteoffItemId() { return writeoffItemId; }
    public void setWriteoffItemId(Long writeoffItemId) { this.writeoffItemId = writeoffItemId; }
    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public String getContestName() { return contestName; }
    public void setContestName(String contestName) { this.contestName = contestName; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public Date getWriteoffTime() { return writeoffTime; }
    public void setWriteoffTime(Date writeoffTime) { this.writeoffTime = writeoffTime; }
    public String getWriteoffTerminal() { return writeoffTerminal; }
    public void setWriteoffTerminal(String writeoffTerminal) { this.writeoffTerminal = writeoffTerminal; }
}
