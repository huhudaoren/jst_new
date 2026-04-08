package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 报名记录列表查询请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class EnrollQueryReqDTO {

    private Long contestId;

    @Pattern(regexp = "pending|approved|rejected|supplement", message = "auditStatus 仅支持 pending/approved/rejected/supplement")
    private String auditStatus;

    @Pattern(regexp = "draft|submitted|supplement", message = "materialStatus 仅支持 draft/submitted/supplement")
    private String materialStatus;

    @Size(max = 32, message = "enrollNo 长度不能超过 32")
    private String enrollNo;

    @Size(max = 64, message = "participantName 长度不能超过 64")
    private String participantName;

    @Size(max = 20, message = "guardianMobile 长度不能超过 20")
    private String guardianMobile;

    private Long partnerId;

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getMaterialStatus() {
        return materialStatus;
    }

    public void setMaterialStatus(String materialStatus) {
        this.materialStatus = materialStatus;
    }

    public String getEnrollNo() {
        return enrollNo;
    }

    public void setEnrollNo(String enrollNo) {
        this.enrollNo = enrollNo;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getGuardianMobile() {
        return guardianMobile;
    }

    public void setGuardianMobile(String guardianMobile) {
        this.guardianMobile = guardianMobile;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }
}
