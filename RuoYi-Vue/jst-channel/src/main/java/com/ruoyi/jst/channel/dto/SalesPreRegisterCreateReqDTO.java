package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SalesPreRegisterCreateReqDTO {
    @NotBlank
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Size(max = 128)
    private String targetName;

    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getTargetName() { return targetName; }
    public void setTargetName(String v) { this.targetName = v; }
}
