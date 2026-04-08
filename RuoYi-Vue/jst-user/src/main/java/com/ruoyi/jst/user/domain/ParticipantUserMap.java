package com.ruoyi.jst.user.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 临时档案↔正式用户 认领映射 Entity
 * <p>
 * 对应表 jst_participant_user_map
 * <p>
 * 重要：底层订单/成绩/证书等流水继续保留 participant_id 快照，
 * 通过本表归集到 user_id，<b>不</b>覆盖原始流水主键(防孤儿规则)
 *
 * @author jst
 * @since 1.0.0
 */
public class ParticipantUserMap extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 映射ID */
    private Long mapId;

    /** 临时档案ID */
    private Long participantId;

    /** 正式用户ID */
    private Long userId;

    /** 认领方式: auto_phone_name / manual_admin / manual_user */
    private String claimMethod;

    /** 认领生效时间 */
    private Date claimTime;

    /** 操作管理员ID(manual_admin 时必填) */
    private Long operatorId;

    /** 映射状态: active / revoked */
    private String status;

    /** 撤销原因 */
    private String revokeReason;

    public Long getMapId() { return mapId; }
    public void setMapId(Long mapId) { this.mapId = mapId; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getClaimMethod() { return claimMethod; }
    public void setClaimMethod(String claimMethod) { this.claimMethod = claimMethod; }
    public Date getClaimTime() { return claimTime; }
    public void setClaimTime(Date claimTime) { this.claimTime = claimTime; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRevokeReason() { return revokeReason; }
    public void setRevokeReason(String revokeReason) { this.revokeReason = revokeReason; }
}
