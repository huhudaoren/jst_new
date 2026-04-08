package com.ruoyi.jst.user.dto;

import jakarta.validation.constraints.Size;

/**
 * 主动解绑请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class UnbindReqDTO {

    /** 解绑原因。 */
    @Size(max = 255, message = "reason 长度不能超过 255")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
