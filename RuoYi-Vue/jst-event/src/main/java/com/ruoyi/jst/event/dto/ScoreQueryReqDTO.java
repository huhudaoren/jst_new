package com.ruoyi.jst.event.dto;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事方成绩列表查询请求。
 *
 * @author jst
 * @since 1.0.0
 */
public class ScoreQueryReqDTO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 赛事ID。 */
    private Long contestId;

    /** 学生姓名关键字。 */
    private String participantName;

    /** 审核状态。 */
    private String auditStatus;

    /** 发布状态。 */
    private String publishStatus;

    /** 前端聚合展示状态。 */
    private String displayStatus;

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }
}
