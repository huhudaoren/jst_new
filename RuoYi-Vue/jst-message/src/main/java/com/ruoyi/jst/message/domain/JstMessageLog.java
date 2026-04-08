package com.ruoyi.jst.message.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 消息发送日志对象 jst_message_log
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstMessageLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Long logId;

    /** 模板编码 */
    @Excel(name = "模板编码")
    private String templateCode;

    /** 通道：inner/sms/wechat */
    @Excel(name = "通道：inner/sms/wechat")
    private String channel;

    /** 接收用户ID */
    @Excel(name = "接收用户ID")
    private Long targetUserId;

    /** 接收手机 */
    @Excel(name = "接收手机")
    private String targetMobile;

    /** 触发来源（业务场景） */
    @Excel(name = "触发来源", readConverterExp = "业=务场景")
    private String triggerSource;

    /** 渲染后的实际内容 */
    @Excel(name = "渲染后的实际内容")
    private String content;

    /** 发送状态：pending/success/failed */
    @Excel(name = "发送状态：pending/success/failed")
    private String sendStatus;

    /** 失败原因 */
    @Excel(name = "失败原因")
    private String failReason;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date sendTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setLogId(Long logId) 
    {
        this.logId = logId;
    }

    public Long getLogId() 
    {
        return logId;
    }

    public void setTemplateCode(String templateCode) 
    {
        this.templateCode = templateCode;
    }

    public String getTemplateCode() 
    {
        return templateCode;
    }

    public void setChannel(String channel) 
    {
        this.channel = channel;
    }

    public String getChannel() 
    {
        return channel;
    }

    public void setTargetUserId(Long targetUserId) 
    {
        this.targetUserId = targetUserId;
    }

    public Long getTargetUserId() 
    {
        return targetUserId;
    }

    public void setTargetMobile(String targetMobile) 
    {
        this.targetMobile = targetMobile;
    }

    public String getTargetMobile() 
    {
        return targetMobile;
    }

    public void setTriggerSource(String triggerSource) 
    {
        this.triggerSource = triggerSource;
    }

    public String getTriggerSource() 
    {
        return triggerSource;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setSendStatus(String sendStatus) 
    {
        this.sendStatus = sendStatus;
    }

    public String getSendStatus() 
    {
        return sendStatus;
    }

    public void setFailReason(String failReason) 
    {
        this.failReason = failReason;
    }

    public String getFailReason() 
    {
        return failReason;
    }

    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("logId", getLogId())
            .append("templateCode", getTemplateCode())
            .append("channel", getChannel())
            .append("targetUserId", getTargetUserId())
            .append("targetMobile", getTargetMobile())
            .append("triggerSource", getTriggerSource())
            .append("content", getContent())
            .append("sendStatus", getSendStatus())
            .append("failReason", getFailReason())
            .append("sendTime", getSendTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
