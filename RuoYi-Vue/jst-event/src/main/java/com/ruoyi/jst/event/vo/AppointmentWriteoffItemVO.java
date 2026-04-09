package com.ruoyi.jst.event.vo;

import java.util.Date;

/**
 * 预约详情中的核销子项。
 */
public class AppointmentWriteoffItemVO {

    private Long writeoffItemId;
    private String itemType;
    private String itemName;
    private String qrCode;
    private String status;
    private Date writeoffTime;

    public Long getWriteoffItemId() { return writeoffItemId; }
    public void setWriteoffItemId(Long writeoffItemId) { this.writeoffItemId = writeoffItemId; }
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
}
