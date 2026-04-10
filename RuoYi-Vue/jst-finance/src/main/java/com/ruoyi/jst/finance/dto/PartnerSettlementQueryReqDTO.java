package com.ruoyi.jst.finance.dto;

import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.Pattern;

/**
 * 赛事方结算单查询参数。
 */
public class PartnerSettlementQueryReqDTO extends BaseEntity {

    @Pattern(regexp = "pending_confirm|reviewing|rejected|pending_pay|paid", message = "结算状态不合法")
    private String status;

    private Long contestId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }
}
