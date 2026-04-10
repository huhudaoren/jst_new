package com.ruoyi.jst.event.dto;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事方证书列表查询请求。
 *
 * @author jst
 * @since 1.0.0
 */
public class CertQueryReqDTO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 赛事ID。 */
    private Long contestId;

    /** 学生姓名关键字。 */
    private String participantName;

    /** 证书编号。 */
    private String certNo;

    /** 发放状态。 */
    private String issueStatus;

    /** 前端展示状态。 */
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

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }
}
