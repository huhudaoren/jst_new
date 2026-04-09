package com.ruoyi.jst.event.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 预约核销子项。
 */
public class EventAppointmentWriteoffItem extends BaseEntity {

    private Long writeoffItemId;
    private Long appointmentId;
    private Long teamAppointmentId;
    private String itemType;
    private String itemName;
    private String qrCode;
    private String status;
    private Date writeoffTime;
    private Long writeoffUserId;
    private String writeoffTerminal;
    private Long writeoffQty;
    private Long totalQty;
    private String delFlag;

    public Long getWriteoffItemId() { return writeoffItemId; }
    public void setWriteoffItemId(Long writeoffItemId) { this.writeoffItemId = writeoffItemId; }
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public Long getTeamAppointmentId() { return teamAppointmentId; }
    public void setTeamAppointmentId(Long teamAppointmentId) { this.teamAppointmentId = teamAppointmentId; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getWriteoffTime() { return writeoffTime; }
    public void setWriteoffTime(Date writeoffTime) { this.writeoffTime = writeoffTime; }
    public Long getWriteoffUserId() { return writeoffUserId; }
    public void setWriteoffUserId(Long writeoffUserId) { this.writeoffUserId = writeoffUserId; }
    public String getWriteoffTerminal() { return writeoffTerminal; }
    public void setWriteoffTerminal(String writeoffTerminal) { this.writeoffTerminal = writeoffTerminal; }
    public Long getWriteoffQty() { return writeoffQty; }
    public void setWriteoffQty(Long writeoffQty) { this.writeoffQty = writeoffQty; }
    public Long getTotalQty() { return totalQty; }
    public void setTotalQty(Long totalQty) { this.totalQty = totalQty; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
