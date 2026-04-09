package com.ruoyi.jst.marketing.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Coupon issue DTO.
 */
public class CouponIssueDTO {

    @NotEmpty(message = "userIds不能为空")
    private List<Long> userIds;

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
