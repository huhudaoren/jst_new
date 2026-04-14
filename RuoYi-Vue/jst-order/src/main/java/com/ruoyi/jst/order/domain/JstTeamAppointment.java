package com.ruoyi.jst.order.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 团队预约主记录对象 jst_team_appointment
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstTeamAppointment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 团队预约ID */
    private Long teamAppointmentId;

    /** 团队预约号 */
    @Excel(name = "团队预约号")
    private String teamNo;

    /** 关联活动ID */
    @Excel(name = "关联活动ID")
    private Long contestId;

    /** 渠道方ID */
    @Excel(name = "渠道方ID")
    private Long channelId;

    /** 团队/批次名称 */
    @Excel(name = "团队/批次名称")
    private String teamName;

    /** 预约日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预约日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date appointmentDate;

    /** 场次/时段编码 */
    @Excel(name = "场次/时段编码")
    private String sessionCode;

    /** 预约总人数 */
    @Excel(name = "预约总人数")
    private Long totalPersons;

    /** 正式成员人数 */
    @Excel(name = "正式成员人数")
    private Long memberPersons;

    /** 额外人数（家长/工作人员） */
    @Excel(name = "额外人数", readConverterExp = "家=长/工作人员")
    private Long extraPersons;

    /** 额外名单JSON */
    @Excel(name = "额外名单JSON")
    private String extraListJson;

    /** 已核销人数 */
    @Excel(name = "已核销人数")
    private Long writeoffPersons;

    /** 团队预约状态：booked/partial_writeoff/fully_writeoff/partial_writeoff_ended/no_show/cancelled/expired */
    @Excel(name = "团队预约状态：booked/partial_writeoff/fully_writeoff/partial_writeoff_ended/no_show/cancelled/expired")
    private String status;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    /** 赛事名称（JOIN查出，非持久化） */
    private String contestName;

    /** 渠道名称（JOIN查出，非持久化） */
    private String channelName;

    public void setTeamAppointmentId(Long teamAppointmentId) 
    {
        this.teamAppointmentId = teamAppointmentId;
    }

    public Long getTeamAppointmentId() 
    {
        return teamAppointmentId;
    }

    public void setTeamNo(String teamNo) 
    {
        this.teamNo = teamNo;
    }

    public String getTeamNo() 
    {
        return teamNo;
    }

    public void setContestId(Long contestId) 
    {
        this.contestId = contestId;
    }

    public Long getContestId() 
    {
        return contestId;
    }

    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }

    public void setTeamName(String teamName) 
    {
        this.teamName = teamName;
    }

    public String getTeamName() 
    {
        return teamName;
    }

    public void setAppointmentDate(Date appointmentDate) 
    {
        this.appointmentDate = appointmentDate;
    }

    public Date getAppointmentDate() 
    {
        return appointmentDate;
    }

    public void setSessionCode(String sessionCode) 
    {
        this.sessionCode = sessionCode;
    }

    public String getSessionCode() 
    {
        return sessionCode;
    }

    public void setTotalPersons(Long totalPersons) 
    {
        this.totalPersons = totalPersons;
    }

    public Long getTotalPersons() 
    {
        return totalPersons;
    }

    public void setMemberPersons(Long memberPersons) 
    {
        this.memberPersons = memberPersons;
    }

    public Long getMemberPersons() 
    {
        return memberPersons;
    }

    public void setExtraPersons(Long extraPersons) 
    {
        this.extraPersons = extraPersons;
    }

    public Long getExtraPersons() 
    {
        return extraPersons;
    }

    public void setExtraListJson(String extraListJson) 
    {
        this.extraListJson = extraListJson;
    }

    public String getExtraListJson() 
    {
        return extraListJson;
    }

    public void setWriteoffPersons(Long writeoffPersons) 
    {
        this.writeoffPersons = writeoffPersons;
    }

    public Long getWriteoffPersons() 
    {
        return writeoffPersons;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
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

    public void setChannelName(String channelName)
    {
        this.channelName = channelName;
    }

    public String getChannelName()
    {
        return channelName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("teamAppointmentId", getTeamAppointmentId())
            .append("teamNo", getTeamNo())
            .append("contestId", getContestId())
            .append("channelId", getChannelId())
            .append("teamName", getTeamName())
            .append("appointmentDate", getAppointmentDate())
            .append("sessionCode", getSessionCode())
            .append("totalPersons", getTotalPersons())
            .append("memberPersons", getMemberPersons())
            .append("extraPersons", getExtraPersons())
            .append("extraListJson", getExtraListJson())
            .append("writeoffPersons", getWriteoffPersons())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
