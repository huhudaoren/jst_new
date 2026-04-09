package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.Pattern;

/**
 * My withdrawal list query.
 */
public class WithdrawQueryReqDTO {

    @Pattern(regexp = "pending|reviewing|rejected|approved|paid|closed",
            message = "status only supports pending/reviewing/rejected/approved/paid/closed")
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
