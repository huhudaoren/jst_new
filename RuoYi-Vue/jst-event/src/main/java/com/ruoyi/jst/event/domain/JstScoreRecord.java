package com.ruoyi.jst.event.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 成绩记录对象 jst_score_record
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstScoreRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 成绩ID */
    private Long scoreId;

    /** 赛事ID */
    @Excel(name = "赛事ID")
    private Long contestId;

    /** 报名ID */
    @Excel(name = "报名ID")
    private Long enrollId;

    /** 用户ID（临时档案为空） */
    @Excel(name = "用户ID", readConverterExp = "临=时档案为空")
    private Long userId;

    /** 参赛人ID */
    @Excel(name = "参赛人ID")
    private Long participantId;

    /** 分数 */
    @Excel(name = "分数")
    private BigDecimal scoreValue;

    /** 奖项等级：国际金/国际银/全国金/全国银/全国铜/优秀/参与 */
    @Excel(name = "奖项等级：国际金/国际银/全国金/全国银/全国铜/优秀/参与")
    private String awardLevel;

    /** 名次 */
    @Excel(name = "名次")
    private Long rankNo;

    /** 导入批次号 */
    @Excel(name = "导入批次号")
    private String importBatchNo;

    /** 审核状态：pending/approved/rejected */
    @Excel(name = "审核状态：pending/approved/rejected")
    private String auditStatus;

    /** 发布状态：unpublished/published/withdrawn */
    @Excel(name = "发布状态：unpublished/published/withdrawn")
    private String publishStatus;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    /** 赛事名称（JOIN查出，非持久化） */
    private String contestName;

    /** 参赛人姓名（JOIN查出，非持久化） */
    private String participantName;

    /** 报名编号（JOIN查出，非持久化） */
    private String enrollNo;

    public void setScoreId(Long scoreId) 
    {
        this.scoreId = scoreId;
    }

    public Long getScoreId() 
    {
        return scoreId;
    }

    public void setContestId(Long contestId) 
    {
        this.contestId = contestId;
    }

    public Long getContestId() 
    {
        return contestId;
    }

    public void setEnrollId(Long enrollId) 
    {
        this.enrollId = enrollId;
    }

    public Long getEnrollId() 
    {
        return enrollId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setParticipantId(Long participantId) 
    {
        this.participantId = participantId;
    }

    public Long getParticipantId() 
    {
        return participantId;
    }

    public void setScoreValue(BigDecimal scoreValue) 
    {
        this.scoreValue = scoreValue;
    }

    public BigDecimal getScoreValue() 
    {
        return scoreValue;
    }

    public void setAwardLevel(String awardLevel) 
    {
        this.awardLevel = awardLevel;
    }

    public String getAwardLevel() 
    {
        return awardLevel;
    }

    public void setRankNo(Long rankNo) 
    {
        this.rankNo = rankNo;
    }

    public Long getRankNo() 
    {
        return rankNo;
    }

    public void setImportBatchNo(String importBatchNo) 
    {
        this.importBatchNo = importBatchNo;
    }

    public String getImportBatchNo() 
    {
        return importBatchNo;
    }

    public void setAuditStatus(String auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus() 
    {
        return auditStatus;
    }

    public void setPublishStatus(String publishStatus) 
    {
        this.publishStatus = publishStatus;
    }

    public String getPublishStatus() 
    {
        return publishStatus;
    }

    public void setPublishTime(Date publishTime) 
    {
        this.publishTime = publishTime;
    }

    public Date getPublishTime() 
    {
        return publishTime;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setContestName(String contestName)
    {
        this.contestName = contestName;
    }

    public String getContestName()
    {
        return contestName;
    }

    public void setParticipantName(String participantName)
    {
        this.participantName = participantName;
    }

    public String getParticipantName()
    {
        return participantName;
    }

    public void setEnrollNo(String enrollNo)
    {
        this.enrollNo = enrollNo;
    }

    public String getEnrollNo()
    {
        return enrollNo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("scoreId", getScoreId())
            .append("contestId", getContestId())
            .append("enrollId", getEnrollId())
            .append("userId", getUserId())
            .append("participantId", getParticipantId())
            .append("scoreValue", getScoreValue())
            .append("awardLevel", getAwardLevel())
            .append("rankNo", getRankNo())
            .append("importBatchNo", getImportBatchNo())
            .append("auditStatus", getAuditStatus())
            .append("publishStatus", getPublishStatus())
            .append("publishTime", getPublishTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
