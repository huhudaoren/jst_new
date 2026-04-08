package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 参赛档案-正式用户认领映射对象 jst_participant_user_map
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstParticipantUserMap extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 映射ID */
    private Long mapId;

    /** 临时档案ID，FK→jst_participant */
    @Excel(name = "临时档案ID，FK→jst_participant")
    private Long participantId;

    /** 正式用户ID，FK→jst_user */
    @Excel(name = "正式用户ID，FK→jst_user")
    private Long userId;

    /** 认领方式：auto_phone_name手机姓名自动/manual_admin管理员手动/manual_user用户手动 */
    @Excel(name = "认领方式：auto_phone_name手机姓名自动/manual_admin管理员手动/manual_user用户手动")
    private String claimMethod;

    /** 认领生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "认领生效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date claimTime;

    /** 操作管理员ID（manual_admin 时必填） */
    @Excel(name = "操作管理员ID", readConverterExp = "m=anual_admin,时=必填")
    private Long operatorId;

    /** 映射状态：active生效/revoked已撤销 */
    @Excel(name = "映射状态：active生效/revoked已撤销")
    private String status;

    /** 撤销原因 */
    @Excel(name = "撤销原因")
    private String revokeReason;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setMapId(Long mapId) 
    {
        this.mapId = mapId;
    }

    public Long getMapId() 
    {
        return mapId;
    }

    public void setParticipantId(Long participantId) 
    {
        this.participantId = participantId;
    }

    public Long getParticipantId() 
    {
        return participantId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setClaimMethod(String claimMethod) 
    {
        this.claimMethod = claimMethod;
    }

    public String getClaimMethod() 
    {
        return claimMethod;
    }

    public void setClaimTime(Date claimTime) 
    {
        this.claimTime = claimTime;
    }

    public Date getClaimTime() 
    {
        return claimTime;
    }

    public void setOperatorId(Long operatorId) 
    {
        this.operatorId = operatorId;
    }

    public Long getOperatorId() 
    {
        return operatorId;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setRevokeReason(String revokeReason) 
    {
        this.revokeReason = revokeReason;
    }

    public String getRevokeReason() 
    {
        return revokeReason;
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
            .append("mapId", getMapId())
            .append("participantId", getParticipantId())
            .append("userId", getUserId())
            .append("claimMethod", getClaimMethod())
            .append("claimTime", getClaimTime())
            .append("operatorId", getOperatorId())
            .append("status", getStatus())
            .append("revokeReason", getRevokeReason())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
