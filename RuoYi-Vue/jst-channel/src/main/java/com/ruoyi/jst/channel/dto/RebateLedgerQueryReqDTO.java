package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.Pattern;

/**
 * Rebate ledger list query.
 */
public class RebateLedgerQueryReqDTO {

    @Pattern(regexp = "pending_accrual|withdrawable|in_review|paid|rolled_back|negative",
            message = "status only supports pending_accrual/withdrawable/in_review/paid/rolled_back/negative")
    private String status;

    private Long channelId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
