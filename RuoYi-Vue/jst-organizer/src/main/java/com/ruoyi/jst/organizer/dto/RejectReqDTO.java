package com.ruoyi.jst.organizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 渠道认证申请-驳回入参
 * <p>
 * 关联表：jst_channel_auth_apply
 * 关联状态机：SM-3 pending → rejected
 * 关联权限：jst:organizer:channelApply:reject
 *
 * @author jst
 * @since 1.0.0
 */
public class RejectReqDTO {

    @NotBlank(message = "审核备注不能为空")
    @Size(max = 255, message = "审核备注长度不能超过255个字符")
    private String auditRemark;

    /**
     * Round 2A A2: 驳回原因（业务语义，前端优先展示）
     * <p>
     * 为空时 Service 会回退到 auditRemark，向后兼容旧调用方。
     */
    @Size(max = 500, message = "驳回原因长度不能超过500个字符")
    private String rejectReason;

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
