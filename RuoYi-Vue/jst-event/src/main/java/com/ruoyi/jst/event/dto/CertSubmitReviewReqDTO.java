package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 证书批量提交审核请求。
 *
 * @author jst
 * @since 1.0.0
 */
public class CertSubmitReviewReqDTO {

    /** 证书ID列表。 */
    @NotEmpty(message = "证书ID列表不能为空")
    private List<Long> certIds;

    public List<Long> getCertIds() {
        return certIds;
    }

    public void setCertIds(List<Long> certIds) {
        this.certIds = certIds;
    }
}
