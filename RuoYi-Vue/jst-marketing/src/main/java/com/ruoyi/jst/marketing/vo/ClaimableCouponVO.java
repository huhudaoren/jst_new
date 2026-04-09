package com.ruoyi.jst.marketing.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Claimable coupon template view.
 */
public class ClaimableCouponVO {

    private Long couponTemplateId;
    private String couponName;
    private String couponType;
    private BigDecimal faceValue;
    private BigDecimal discountRate;
    private BigDecimal thresholdAmount;
    private String applicableRole;
    private List<Long> applicableContestIds;
    private Integer validDays;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validEnd;

    private String stackRule;
    private Integer status;

    public Long getCouponTemplateId() {
        return couponTemplateId;
    }

    public void setCouponTemplateId(Long couponTemplateId) {
        this.couponTemplateId = couponTemplateId;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getThresholdAmount() {
        return thresholdAmount;
    }

    public void setThresholdAmount(BigDecimal thresholdAmount) {
        this.thresholdAmount = thresholdAmount;
    }

    public String getApplicableRole() {
        return applicableRole;
    }

    public void setApplicableRole(String applicableRole) {
        this.applicableRole = applicableRole;
    }

    public List<Long> getApplicableContestIds() {
        return applicableContestIds;
    }

    public void setApplicableContestIds(List<Long> applicableContestIds) {
        this.applicableContestIds = applicableContestIds;
    }

    public Integer getValidDays() {
        return validDays;
    }

    public void setValidDays(Integer validDays) {
        this.validDays = validDays;
    }

    public Date getValidStart() {
        return validStart;
    }

    public void setValidStart(Date validStart) {
        this.validStart = validStart;
    }

    public Date getValidEnd() {
        return validEnd;
    }

    public void setValidEnd(Date validEnd) {
        this.validEnd = validEnd;
    }

    public String getStackRule() {
        return stackRule;
    }

    public void setStackRule(String stackRule) {
        this.stackRule = stackRule;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
