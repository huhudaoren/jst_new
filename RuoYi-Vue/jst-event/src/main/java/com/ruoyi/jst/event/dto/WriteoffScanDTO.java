package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 扫码核销入参。
 */
public class WriteoffScanDTO {

    @NotBlank(message = "二维码不能为空")
    private String qrCode;

    private String scanType;

    private String terminal;

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public String getScanType() { return scanType; }
    public void setScanType(String scanType) { this.scanType = scanType; }
    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }
}
