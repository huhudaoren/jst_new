package com.ruoyi.jst.organizer.dto;

import jakarta.validation.constraints.Size;

/**
 * 渠道认证申请-审核通过入参
 * <p>
 * 关联表：jst_channel_auth_apply、jst_channel、sys_user_role
 * 关联状态机：SM-3 pending → approved
 * 关联权限：jst:organizer:channelApply:approve
 *
 * @author jst
 * @since 1.0.0
 */
public class ApproveChannelReqDTO {

    @Size(max = 255, message = "审核备注长度不能超过255个字符")
    private String auditRemark;

    @Size(max = 255, message = "标签长度不能超过255个字符")
    private String tags;

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
