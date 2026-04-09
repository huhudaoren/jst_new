package com.ruoyi.jst.marketing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Coupon template create/update DTO.
 */
public class CouponTemplateSaveDTO {

    private Long couponTemplateId;

    @NotBlank(message = "couponName不能为空")
    @Size(max = 128, message = "couponName长度不能超过128")
    private String couponName;

    @NotBlank(message = "couponType不能为空")
    private String couponType;

    @DecimalMin(value = "0.00", message = "faceValue不能为负数")
    private BigDecimal faceValue;

    @DecimalMin(value = "0.00", message = "discountRate不能为负数")
    private BigDecimal discountRate;

    @DecimalMin(value = "0.00", message = "thresholdAmount不能为负数")
    private BigDecimal thresholdAmount;

    @NotBlank(message = "applicableRole不能为空")
    private String applicableRole;

    private List<Long> applicableContestIds;

    @NotNull(message = "validDays不能为空")
    private Integer validDays;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validEnd;

    @NotBlank(message = "stackRule不能为空")
    private String stackRule;

    @Size(max = 500, message = "remark长度不能超过500")
    private String remark;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
