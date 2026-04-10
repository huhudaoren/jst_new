package com.ruoyi.jst.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 赛事方结算争议提交参数。
 */
public class SettlementDisputeReqDTO {

    @NotBlank(message = "争议说明不能为空")
    @Size(max = 255, message = "争议说明不能超过255个字符")
    private String disputeReason;

    public String getDisputeReason() {
        return disputeReason;
    }

    public void setDisputeReason(String disputeReason) {
        this.disputeReason = disputeReason;
    }
}
