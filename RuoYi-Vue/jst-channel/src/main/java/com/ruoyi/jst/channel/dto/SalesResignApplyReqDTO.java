package com.ruoyi.jst.channel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class SalesResignApplyReqDTO {
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expectedResignDate;

    public Date getExpectedResignDate() { return expectedResignDate; }
    public void setExpectedResignDate(Date v) { this.expectedResignDate = v; }
}
