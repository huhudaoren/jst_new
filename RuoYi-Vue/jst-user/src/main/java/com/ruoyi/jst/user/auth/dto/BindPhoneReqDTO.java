package com.ruoyi.jst.user.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 绑定手机号-请求 DTO (微信 getPhoneNumber 加密数据)
 *
 * @author jst
 * @since 1.0.0
 */
public class BindPhoneReqDTO {

    /** 微信加密数据 */
    @NotBlank(message = "encryptedData 不能为空")
    private String encryptedData;

    /** 加密算法 IV */
    @NotBlank(message = "iv 不能为空")
    private String iv;

    public String getEncryptedData() { return encryptedData; }
    public void setEncryptedData(String encryptedData) { this.encryptedData = encryptedData; }
    public String getIv() { return iv; }
    public void setIv(String iv) { this.iv = iv; }
}
