package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SalesUpdateRateReqDTO {
    @NotNull
    @DecimalMin("0.0000")
    @DecimalMax("1.0000")
    private BigDecimal rate;

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal v) { this.rate = v; }
}
