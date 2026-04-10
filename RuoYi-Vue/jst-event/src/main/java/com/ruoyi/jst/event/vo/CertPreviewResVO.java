package com.ruoyi.jst.event.vo;

/**
 * 证书预览响应。
 *
 * @author jst
 * @since 1.0.0
 */
public class CertPreviewResVO {

    private Long certId;
    private String certNo;
    private String previewImage;

    public Long getCertId() { return certId; }
    public void setCertId(Long certId) { this.certId = certId; }
    public String getCertNo() { return certNo; }
    public void setCertNo(String certNo) { this.certNo = certNo; }
    public String getPreviewImage() { return previewImage; }
    public void setPreviewImage(String previewImage) { this.previewImage = previewImage; }
}
