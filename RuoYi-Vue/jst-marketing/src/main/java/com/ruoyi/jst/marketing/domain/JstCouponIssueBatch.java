package com.ruoyi.jst.marketing.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 发券批次对象 jst_coupon_issue_batch
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstCouponIssueBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 批次ID */
    private Long batchId;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNo;

    /** 模板ID */
    @Excel(name = "模板ID")
    private Long couponTemplateId;

    /** 目标类型：user/channel/campaign/tag */
    @Excel(name = "目标类型：user/channel/campaign/tag")
    private String targetType;

    /** 目标对象JSON */
    @Excel(name = "目标对象JSON")
    private String targetJson;

    /** 计划数量 */
    @Excel(name = "计划数量")
    private Long totalCount;

    /** 成功数 */
    @Excel(name = "成功数")
    private Long successCount;

    /** 失败数 */
    @Excel(name = "失败数")
    private Long failCount;

    /** 状态：pending/running/completed/failed */
    @Excel(name = "状态：pending/running/completed/failed")
    private String status;

    /** 操作人 */
    @Excel(name = "操作人")
    private Long operatorId;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setBatchId(Long batchId) 
    {
        this.batchId = batchId;
    }

    public Long getBatchId() 
    {
        return batchId;
    }

    public void setBatchNo(String batchNo) 
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo() 
    {
        return batchNo;
    }

    public void setCouponTemplateId(Long couponTemplateId) 
    {
        this.couponTemplateId = couponTemplateId;
    }

    public Long getCouponTemplateId() 
    {
        return couponTemplateId;
    }

    public void setTargetType(String targetType) 
    {
        this.targetType = targetType;
    }

    public String getTargetType() 
    {
        return targetType;
    }

    public void setTargetJson(String targetJson) 
    {
        this.targetJson = targetJson;
    }

    public String getTargetJson() 
    {
        return targetJson;
    }

    public void setTotalCount(Long totalCount) 
    {
        this.totalCount = totalCount;
    }

    public Long getTotalCount() 
    {
        return totalCount;
    }

    public void setSuccessCount(Long successCount) 
    {
        this.successCount = successCount;
    }

    public Long getSuccessCount() 
    {
        return successCount;
    }

    public void setFailCount(Long failCount) 
    {
        this.failCount = failCount;
    }

    public Long getFailCount() 
    {
        return failCount;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
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
            .append("batchId", getBatchId())
            .append("batchNo", getBatchNo())
            .append("couponTemplateId", getCouponTemplateId())
            .append("targetType", getTargetType())
            .append("targetJson", getTargetJson())
            .append("totalCount", getTotalCount())
            .append("successCount", getSuccessCount())
            .append("failCount", getFailCount())
            .append("status", getStatus())
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
