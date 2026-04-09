package com.ruoyi.jst.event.vo;

import java.util.Date;

/**
 * 扫码核销结果。
 */
public class WriteoffScanResVO {

    private Long itemId;
    private String itemType;
    private String itemName;
    private String memberName;
    private String appointmentStatus;
    private String teamStatus;
    private Long teamWriteoffPersons;
    private Long teamTotalPersons;
    private Date writeoffTime;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getAppointmentStatus() { return appointmentStatus; }
    public void setAppointmentStatus(String appointmentStatus) { this.appointmentStatus = appointmentStatus; }
    public String getTeamStatus() { return teamStatus; }
    public void setTeamStatus(String teamStatus) { this.teamStatus = teamStatus; }
    public Long getTeamWriteoffPersons() { return teamWriteoffPersons; }
    public void setTeamWriteoffPersons(Long teamWriteoffPersons) { this.teamWriteoffPersons = teamWriteoffPersons; }
    public Long getTeamTotalPersons() { return teamTotalPersons; }
    public void setTeamTotalPersons(Long teamTotalPersons) { this.teamTotalPersons = teamTotalPersons; }
    public Date getWriteoffTime() { return writeoffTime; }
    public void setWriteoffTime(Date writeoffTime) { this.writeoffTime = writeoffTime; }
}
