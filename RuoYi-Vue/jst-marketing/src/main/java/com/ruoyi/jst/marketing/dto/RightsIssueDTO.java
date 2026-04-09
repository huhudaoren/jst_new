package com.ruoyi.jst.marketing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Rights issue DTO.
 */
public class RightsIssueDTO {

    @NotEmpty(message = "userIds不能为空")
    private List<Long> userIds;

    @DecimalMin(value = "0.00", message = "remainQuotaOverride不能为负数")
    private BigDecimal remainQuotaOverride;

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public BigDecimal getRemainQuotaOverride() {
        return remainQuotaOverride;
    }

    public void setRemainQuotaOverride(BigDecimal remainQuotaOverride) {
        this.remainQuotaOverride = remainQuotaOverride;
    }
}
