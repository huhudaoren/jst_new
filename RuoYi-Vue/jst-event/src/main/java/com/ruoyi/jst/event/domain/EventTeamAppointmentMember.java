package com.ruoyi.jst.event.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 团队预约成员明细。
 */
public class EventTeamAppointmentMember extends BaseEntity {

    private Long memberId;
    private Long teamAppointmentId;
    private Long userId;
    private Long participantId;
    private String memberNo;
    private String nameSnapshot;
    private String mobileSnapshot;
    private Long subOrderId;
    private String writeoffStatus;
    private String delFlag;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getTeamAppointmentId() { return teamAppointmentId; }
    public void setTeamAppointmentId(Long teamAppointmentId) { this.teamAppointmentId = teamAppointmentId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public String getMemberNo() { return memberNo; }
    public void setMemberNo(String memberNo) { this.memberNo = memberNo; }
    public String getNameSnapshot() { return nameSnapshot; }
    public void setNameSnapshot(String nameSnapshot) { this.nameSnapshot = nameSnapshot; }
    public String getMobileSnapshot() { return mobileSnapshot; }
    public void setMobileSnapshot(String mobileSnapshot) { this.mobileSnapshot = mobileSnapshot; }
    public Long getSubOrderId() { return subOrderId; }
    public void setSubOrderId(Long subOrderId) { this.subOrderId = subOrderId; }
    public String getWriteoffStatus() { return writeoffStatus; }
    public void setWriteoffStatus(String writeoffStatus) { this.writeoffStatus = writeoffStatus; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
