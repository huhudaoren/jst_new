package com.ruoyi.jst.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 管理员手动认领临时档案-请求 DTO
 *
 * @author jst
 * @since 1.0.0
 */
public class ClaimAdminReqDTO {

    /** 临时档案ID */
    @NotNull(message = "participantId 不能为空")
    private Long participantId;

    /** 目标正式用户ID */
    @NotNull(message = "userId 不能为空")
    private Long userId;

    /** 认领原因(必填,记录到 audit_log) */
    @Size(max = 255, message = "原因长度不能超过 255")
    private String reason;

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
