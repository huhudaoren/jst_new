package com.ruoyi.jst.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ChannelParticipantResVO {

    private Long participantId;
    private String name;
    private String guardianMobile;
    private String guardianMobileMasked;
    private String school;
    private String className;
    private String claimStatus;
    private String claimedUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuardianMobile() {
        return guardianMobile;
    }

    public void setGuardianMobile(String guardianMobile) {
        this.guardianMobile = guardianMobile;
    }

    public String getGuardianMobileMasked() {
        return guardianMobileMasked;
    }

    public void setGuardianMobileMasked(String guardianMobileMasked) {
        this.guardianMobileMasked = guardianMobileMasked;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public String getClaimedUserName() {
        return claimedUserName;
    }

    public void setClaimedUserName(String claimedUserName) {
        this.claimedUserName = claimedUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
