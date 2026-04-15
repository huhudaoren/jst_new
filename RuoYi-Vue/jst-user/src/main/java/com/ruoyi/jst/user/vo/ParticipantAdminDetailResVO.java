package com.ruoyi.jst.user.vo;

import java.util.Date;

/**
 * 管理端参赛档案详情响应 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ParticipantAdminDetailResVO {

    private Long participantId;
    private String name;
    private Integer gender;
    private Integer age;
    private String guardianName;
    private String guardianMobile;
    private String school;
    private String organization;
    private String className;
    private String claimStatus;
    private Long claimedUserId;
    private String userName;
    private Date claimedTime;
    private Long createdByChannelId;
    private String createdByChannelName;
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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianMobile() {
        return guardianMobile;
    }

    public void setGuardianMobile(String guardianMobile) {
        this.guardianMobile = guardianMobile;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
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

    public Date getClaimedTime() {
        return claimedTime;
    }

    public void setClaimedTime(Date claimedTime) {
        this.claimedTime = claimedTime;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
