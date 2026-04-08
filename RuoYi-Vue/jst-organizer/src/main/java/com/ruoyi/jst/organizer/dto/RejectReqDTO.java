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

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }
}
