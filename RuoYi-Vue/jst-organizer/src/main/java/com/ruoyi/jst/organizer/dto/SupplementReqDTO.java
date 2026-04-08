package com.ruoyi.jst.organizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 赛事方入驻申请-补件请求 DTO
 *
 * @author jst
 * @since 1.0.0
 */
public class SupplementReqDTO {

    @NotBlank(message = "supplementRemark 不能为空")
    @Size(max = 255, message = "supplementRemark 长度不能超过 255")
    private String supplementRemark;

    public String getSupplementRemark() {
        return supplementRemark;
    }

    public void setSupplementRemark(String supplementRemark) {
        this.supplementRemark = supplementRemark;
    }
}
