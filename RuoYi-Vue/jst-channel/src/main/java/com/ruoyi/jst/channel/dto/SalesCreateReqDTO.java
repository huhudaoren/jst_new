package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class SalesCreateReqDTO {
    @NotNull(message = "sysUserId 不能为空")
    private Long sysUserId;

    @NotBlank
    @Size(max = 64)
    private String salesName;

    @NotBlank
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private Long managerId;

    @NotNull
    @DecimalMin("0.0000")
    @DecimalMax("1.0000")
    private BigDecimal commissionRateDefault;

    private Boolean asManager = false;

    public Long getSysUserId() { return sysUserId; }
    public void setSysUserId(Long v) { this.sysUserId = v; }
    public String getSalesName() { return salesName; }
    public void setSalesName(String v) { this.salesName = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long v) { this.managerId = v; }
    public BigDecimal getCommissionRateDefault() { return commissionRateDefault; }
    public void setCommissionRateDefault(BigDecimal v) { this.commissionRateDefault = v; }
    public Boolean getAsManager() { return asManager; }
    public void setAsManager(Boolean v) { this.asManager = v; }
}
