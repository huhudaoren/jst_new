package com.ruoyi.jst.event.vo;

/**
 * 团队预约成员进度。
 */
public class TeamAppointmentMemberVO {

    private Long appointmentId;
    private Long participantId;
    private Long userId;
    private String memberNo;
    private String memberName;
    private String mobileSnapshot;
    private Long subOrderId;
    private String writeoffStatus;
    private String appointmentStatus;
    private Integer usedItems;
    private Integer totalItems;

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMemberNo() { return memberNo; }
    public void setMemberNo(String memberNo) { this.memberNo = memberNo; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getMobileSnapshot() { return mobileSnapshot; }
    public void setMobileSnapshot(String mobileSnapshot) { this.mobileSnapshot = mobileSnapshot; }
    public Long getSubOrderId() { return subOrderId; }
    public void setSubOrderId(Long subOrderId) { this.subOrderId = subOrderId; }
    public String getWriteoffStatus() { return writeoffStatus; }
    public void setWriteoffStatus(String writeoffStatus) { this.writeoffStatus = writeoffStatus; }
    public String getAppointmentStatus() { return appointmentStatus; }
    public void setAppointmentStatus(String appointmentStatus) { this.appointmentStatus = appointmentStatus; }
    public Integer getUsedItems() { return usedItems; }
    public void setUsedItems(Integer usedItems) { this.usedItems = usedItems; }
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
}
