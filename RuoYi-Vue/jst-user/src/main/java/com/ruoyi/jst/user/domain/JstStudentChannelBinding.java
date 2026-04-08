package com.ruoyi.jst.user.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学生-渠道方绑定关系（同一user同时仅1条active）对象 jst_student_channel_binding
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstStudentChannelBinding extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 绑定ID */
    private Long bindingId;

    /** 学生用户ID，FK→jst_user */
    @Excel(name = "学生用户ID，FK→jst_user")
    private Long userId;

    /** 渠道方ID，FK→jst_channel */
    @Excel(name = "渠道方ID，FK→jst_channel")
    private Long channelId;

    /** 绑定生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "绑定生效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date bindTime;

    /** 失效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "失效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date unbindTime;

    /** 状态：active生效/expired过期/replaced被覆盖/manual_unbound人工解绑 */
    @Excel(name = "状态：active生效/expired过期/replaced被覆盖/manual_unbound人工解绑")
    private String status;

    /** 解绑原因 */
    @Excel(name = "解绑原因")
    private String unbindReason;

    /** 解绑操作人ID */
    @Excel(name = "解绑操作人ID")
    private Long operatorId;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setBindingId(Long bindingId) 
    {
        this.bindingId = bindingId;
    }

    public Long getBindingId() 
    {
        return bindingId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }

    public void setBindTime(Date bindTime) 
    {
        this.bindTime = bindTime;
    }

    public Date getBindTime() 
    {
        return bindTime;
    }

    public void setUnbindTime(Date unbindTime) 
    {
        this.unbindTime = unbindTime;
    }

    public Date getUnbindTime() 
    {
        return unbindTime;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setUnbindReason(String unbindReason) 
    {
        this.unbindReason = unbindReason;
    }

    public String getUnbindReason() 
    {
        return unbindReason;
    }

    public void setOperatorId(Long operatorId) 
    {
        this.operatorId = operatorId;
    }

    public Long getOperatorId() 
    {
        return operatorId;
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
            .append("bindingId", getBindingId())
            .append("userId", getUserId())
            .append("channelId", getChannelId())
            .append("bindTime", getBindTime())
            .append("unbindTime", getUnbindTime())
            .append("status", getStatus())
            .append("unbindReason", getUnbindReason())
            .append("operatorId", getOperatorId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
