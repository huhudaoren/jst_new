package com.ruoyi.jst.user.vo;

import java.util.Date;

/**
 * 管理端参赛档案列表响应 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ParticipantAdminListResVO {

    private Long participantId;
    private String name;
    private Integer age;
    private String school;
    private String claimStatus;
    private Long claimedUserId;
    private String userName;
    private String guardianMobile;
    private Long createdByChannelId;
    private String createdByChannelName;
    private Date claimedTime;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public Long getClaimedUserId() {
        return claimedUserId;
    }

    public void setClaimedUserId(Long claimedUserId) {
        this.claimedUserId = claimedUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGuardianMobile() {
        return guardianMobile;
    }

    public void setGuardianMobile(String guardianMobile) {
        this.guardianMobile = guardianMobile;
    }

    public Long getCreatedByChannelId() {
        return createdByChannelId;
    }

    public void setCreatedByChannelId(Long createdByChannelId) {
        this.createdByChannelId = createdByChannelId;
    }

    public String getCreatedByChannelName() {
        return createdByChannelName;
    }

    public void setCreatedByChannelName(String createdByChannelName) {
        this.createdByChannelName = createdByChannelName;
    }

    public Date getClaimedTime() {
        return claimedTime;
    }

    public void setClaimedTime(Date claimedTime) {
        this.claimedTime = claimedTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
