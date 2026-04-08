package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 风控黑白名单对象 jst_risk_blacklist
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstRiskBlacklist extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long listId;

    /** 名单类型：black/white */
    @Excel(name = "名单类型：black/white")
    private String listType;

    /** 对象类型：user/channel/device/mobile/address */
    @Excel(name = "对象类型：user/channel/device/mobile/address")
    private String targetType;

    /** 目标值 */
    @Excel(name = "目标值")
    private String targetValue;

    /** 入名单原因 */
    @Excel(name = "入名单原因")
    private String reason;

    /** 生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date effectiveTime;

    /** 失效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "失效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expireTime;

    /** 启停：0停 1启 */
    @Excel(name = "启停：0停 1启")
    private Integer status;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setListId(Long listId) 
    {
        this.listId = listId;
    }

    public Long getListId() 
    {
        return listId;
    }

    public void setListType(String listType) 
    {
        this.listType = listType;
    }

    public String getListType() 
    {
        return listType;
    }

    public void setTargetType(String targetType) 
    {
        this.targetType = targetType;
    }

    public String getTargetType() 
    {
        return targetType;
    }

    public void setTargetValue(String targetValue) 
    {
        this.targetValue = targetValue;
    }

    public String getTargetValue() 
    {
        return targetValue;
    }

    public void setReason(String reason) 
    {
        this.reason = reason;
    }

    public String getReason() 
    {
        return reason;
    }

    public void setEffectiveTime(Date effectiveTime) 
    {
        this.effectiveTime = effectiveTime;
    }

    public Date getEffectiveTime() 
    {
        return effectiveTime;
    }

    public void setExpireTime(Date expireTime) 
    {
        this.expireTime = expireTime;
    }

    public Date getExpireTime() 
    {
        return expireTime;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("listId", getListId())
            .append("listType", getListType())
            .append("targetType", getTargetType())
            .append("targetValue", getTargetValue())
            .append("reason", getReason())
            .append("effectiveTime", getEffectiveTime())
            .append("expireTime", getExpireTime())
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
