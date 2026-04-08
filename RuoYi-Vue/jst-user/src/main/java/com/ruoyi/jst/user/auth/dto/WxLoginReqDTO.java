package com.ruoyi.jst.user.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 微信小程序登录-请求 DTO
 * <p>
 * 入参 code 来自小程序 wx.login() 调用结果
 *
 * @author jst
 * @since 1.0.0
 */
public class WxLoginReqDTO {

    /** wx.login() 返回的临时 code (mock 模式下可传 MOCK_xxx 形式) */
    @NotBlank(message = "code 不能为空")
    private String code;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
