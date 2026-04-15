package com.ruoyi.jst.user.vo;

import java.util.Date;

/**
 * 管理端参赛档案认领映射响应 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ParticipantUserMapAdminResVO {

    private Long mapId;
    private Long participantId;
    private String participantName;
    private Long userId;
    private String userName;
    private String claimMethod;
    private Date claimTime;
    private String status;
    private String revokeReason;

    public Long getMapId() {
        return mapId;
    }

    public void setMapId(Long mapId) {
        this.mapId = mapId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClaimMethod() {
        return claimMethod;
    }

    public void setClaimMethod(String claimMethod) {
        this.claimMethod = claimMethod;
    }

    public Date getClaimTime() {
        return claimTime;
    }

    public void setClaimTime(Date claimTime) {
        this.claimTime = claimTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRevokeReason() {
        return revokeReason;
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }
}
