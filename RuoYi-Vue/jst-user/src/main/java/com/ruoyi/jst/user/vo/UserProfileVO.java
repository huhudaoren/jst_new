package com.ruoyi.jst.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UserProfileVO {

    private Long userId;
    private String nickname;
    private String avatar;
    private String realName;
    private Integer gender;
    private Date birthday;
    private String mobileMasked;
    private String guardianName;
    private String guardianMobileMasked;
    private String userType;
    private Long currentLevelId;
    private Long totalPoints;
    private Long availablePoints;
    private Long growthValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;

    private Integer enrollCount;
    private Integer scoreCount;
    private Integer certCount;
    private Integer courseCount;
    private Long frozenPoints;
    private String levelName;
    private Integer couponCount;
    private Integer unreadMsgCount;
    private ChannelBindingVO channelBinding;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getMobileMasked() {
        return mobileMasked;
    }

    public void setMobileMasked(String mobileMasked) {
        this.mobileMasked = mobileMasked;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianMobileMasked() {
        return guardianMobileMasked;
    }

    public void setGuardianMobileMasked(String guardianMobileMasked) {
        this.guardianMobileMasked = guardianMobileMasked;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getCurrentLevelId() {
        return currentLevelId;
    }

    public void setCurrentLevelId(Long currentLevelId) {
        this.currentLevelId = currentLevelId;
    }

    public Long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Long getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(Long availablePoints) {
        this.availablePoints = availablePoints;
    }

    public Long getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(Long growthValue) {
        this.growthValue = growthValue;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Integer getEnrollCount() {
        return enrollCount;
    }

    public void setEnrollCount(Integer enrollCount) {
        this.enrollCount = enrollCount;
    }

    public Integer getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(Integer scoreCount) {
        this.scoreCount = scoreCount;
    }

    public Integer getCertCount() {
        return certCount;
    }

    public void setCertCount(Integer certCount) {
        this.certCount = certCount;
    }

    public Integer getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Integer courseCount) {
        this.courseCount = courseCount;
    }

    public Long getFrozenPoints() {
        return frozenPoints;
    }

    public void setFrozenPoints(Long frozenPoints) {
        this.frozenPoints = frozenPoints;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public Integer getCouponCount() {
        return couponCount;
    }

    public void setCouponCount(Integer couponCount) {
        this.couponCount = couponCount;
    }

    public Integer getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(Integer unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public ChannelBindingVO getChannelBinding() {
        return channelBinding;
    }

    public void setChannelBinding(ChannelBindingVO channelBinding) {
        this.channelBinding = channelBinding;
    }

    public static class ChannelBindingVO {

        private String name;
        private String school;
        private String remark;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
