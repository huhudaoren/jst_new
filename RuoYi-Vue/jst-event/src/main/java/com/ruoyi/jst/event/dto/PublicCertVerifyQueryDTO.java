package com.ruoyi.jst.event.dto;

/**
 * Public certificate verify query DTO.
 */
public class PublicCertVerifyQueryDTO {

    private String certNo;
    private String verifyCode;

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
