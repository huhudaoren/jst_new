package com.ruoyi.jst.user.dto;

import jakarta.validation.constraints.Pattern;

public class ChannelParticipantQueryReqDTO {

    @Pattern(regexp = "^(unclaimed|claimed|conflict)?$", message = "status only supports unclaimed/claimed/conflict")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
