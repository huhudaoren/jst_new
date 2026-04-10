package com.ruoyi.jst.event.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量生成证书响应。
 *
 * @author jst
 * @since 1.0.0
 */
public class CertBatchGrantResVO {

    private Integer createdCount;
    private Integer skippedCount;
    private List<Long> certIds = new ArrayList<>();

    public Integer getCreatedCount() { return createdCount; }
    public void setCreatedCount(Integer createdCount) { this.createdCount = createdCount; }
    public Integer getSkippedCount() { return skippedCount; }
    public void setSkippedCount(Integer skippedCount) { this.skippedCount = skippedCount; }
    public List<Long> getCertIds() { return certIds; }
    public void setCertIds(List<Long> certIds) { this.certIds = certIds; }
}
