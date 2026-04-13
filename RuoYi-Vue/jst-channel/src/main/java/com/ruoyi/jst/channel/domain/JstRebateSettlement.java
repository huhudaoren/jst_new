package com.ruoyi.jst.channel.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 渠道提现/结算单对象 jst_rebate_settlement
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstRebateSettlement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 结算单ID */
    private Long settlementId;

    /** 结算单号 */
    @Excel(name = "结算单号")
    private String settlementNo;

    /** 渠道方ID */
    @Excel(name = "渠道方ID")
    private Long channelId;

    /** 申请金额 */
    @Excel(name = "申请金额")
    private BigDecimal applyAmount;

    /** 负向台账抵扣金额 */
    @Excel(name = "负向台账抵扣金额")
    private BigDecimal negativeOffsetAmount;

    /** 实际打款金额 */
    @Excel(name = "实际打款金额")
    private BigDecimal actualPayAmount;

    /** 发票状态：none/pending/issued/voided */
    @Excel(name = "发票状态：none/pending/issued/voided")
    private String invoiceStatus;

    /** 关联发票ID */
    @Excel(name = "关联发票ID")
    private Long invoiceId;

    /** 收款账户快照 */
    @Excel(name = "收款账户快照")
    private String bankAccountSnapshot;

    /** 状态：pending/reviewing/rejected/approved/paid */
    @Excel(name = "状态：pending/reviewing/rejected/approved/paid")
    private String status;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String auditRemark;

    /** 打款凭证URL */
    @Excel(name = "打款凭证URL")
    private String payVoucherUrl;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date applyTime;

    /** 打款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "打款时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    /** 渠道名称（JOIN查出，非持久化） */
    private String channelName;

    public void setSettlementId(Long settlementId) 
    {
        this.settlementId = settlementId;
    }

    public Long getSettlementId() 
    {
        return settlementId;
    }

    public void setSettlementNo(String settlementNo) 
    {
        this.settlementNo = settlementNo;
    }

    public String getSettlementNo() 
    {
        return settlementNo;
    }

    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }

    public void setApplyAmount(BigDecimal applyAmount) 
    {
        this.applyAmount = applyAmount;
    }

    public BigDecimal getApplyAmount() 
    {
        return applyAmount;
    }

    public void setNegativeOffsetAmount(BigDecimal negativeOffsetAmount) 
    {
        this.negativeOffsetAmount = negativeOffsetAmount;
    }

    public BigDecimal getNegativeOffsetAmount() 
    {
        return negativeOffsetAmount;
    }

    public void setActualPayAmount(BigDecimal actualPayAmount) 
    {
        this.actualPayAmount = actualPayAmount;
    }

    public BigDecimal getActualPayAmount() 
    {
        return actualPayAmount;
    }

    public void setInvoiceStatus(String invoiceStatus) 
    {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceStatus() 
    {
        return invoiceStatus;
    }

    public void setInvoiceId(Long invoiceId) 
    {
        this.invoiceId = invoiceId;
    }

    public Long getInvoiceId() 
    {
        return invoiceId;
    }

    public void setBankAccountSnapshot(String bankAccountSnapshot) 
    {
        this.bankAccountSnapshot = bankAccountSnapshot;
    }

    public String getBankAccountSnapshot() 
    {
        return bankAccountSnapshot;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setAuditRemark(String auditRemark) 
    {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark() 
    {
        return auditRemark;
    }

    public void setPayVoucherUrl(String payVoucherUrl) 
    {
        this.payVoucherUrl = payVoucherUrl;
    }

    public String getPayVoucherUrl() 
    {
        return payVoucherUrl;
    }

    public void setApplyTime(Date applyTime) 
    {
        this.applyTime = applyTime;
    }

    public Date getApplyTime() 
    {
        return applyTime;
    }

    public void setPayTime(Date payTime) 
    {
        this.payTime = payTime;
    }

    public Date getPayTime() 
    {
        return payTime;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
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
            .append("settlementId", getSettlementId())
            .append("settlementNo", getSettlementNo())
            .append("channelId", getChannelId())
            .append("applyAmount", getApplyAmount())
            .append("negativeOffsetAmount", getNegativeOffsetAmount())
            .append("actualPayAmount", getActualPayAmount())
            .append("invoiceStatus", getInvoiceStatus())
            .append("invoiceId", getInvoiceId())
            .append("bankAccountSnapshot", getBankAccountSnapshot())
            .append("status", getStatus())
            .append("auditRemark", getAuditRemark())
            .append("payVoucherUrl", getPayVoucherUrl())
            .append("applyTime", getApplyTime())
            .append("payTime", getPayTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
