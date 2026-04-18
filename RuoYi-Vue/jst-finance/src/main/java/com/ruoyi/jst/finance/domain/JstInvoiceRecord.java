package com.ruoyi.jst.finance.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 发票记录对象 jst_invoice_record
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstInvoiceRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 发票ID */
    private Long invoiceId;

    /** 发票号码 */
    @Excel(name = "发票号码")
    private String invoiceNo;

    /** 对象类型：channel/partner */
    @Excel(name = "对象类型：channel/partner")
    private String targetType;

    /** 对象ID */
    @Excel(name = "对象ID")
    private Long targetId;

    /** 关联结算/提现单号 */
    @Excel(name = "关联结算/提现单号")
    private String refSettlementNo;

    /** 发票抬头 */
    @Excel(name = "发票抬头")
    private String invoiceTitle;

    /** 税号 */
    @Excel(name = "税号")
    private String taxNo;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 状态：pending_apply/reviewing/issuing/issued/voided/red_offset */
    @Excel(name = "状态：pending_apply/reviewing/issuing/issued/voided/red_offset")
    private String status;

    /** 票据附件URL */
    @Excel(name = "票据附件URL")
    private String fileUrl;

    /** 快递状态 */
    @Excel(name = "快递状态")
    private String expressStatus;

    /** 快递公司 */
    private String trackingCompany;

    /** 快递单号 */
    private String trackingNo;

    /** 投递邮箱 */
    private String deliveryEmail;

    /** 开票时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开票时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date issueTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setInvoiceId(Long invoiceId) 
    {
        this.invoiceId = invoiceId;
    }

    public Long getInvoiceId() 
    {
        return invoiceId;
    }

    public void setInvoiceNo(String invoiceNo) 
    {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceNo() 
    {
        return invoiceNo;
    }

    public void setTargetType(String targetType) 
    {
        this.targetType = targetType;
    }

    public String getTargetType() 
    {
        return targetType;
    }

    public void setTargetId(Long targetId) 
    {
        this.targetId = targetId;
    }

    public Long getTargetId() 
    {
        return targetId;
    }

    public void setRefSettlementNo(String refSettlementNo) 
    {
        this.refSettlementNo = refSettlementNo;
    }

    public String getRefSettlementNo() 
    {
        return refSettlementNo;
    }

    public void setInvoiceTitle(String invoiceTitle) 
    {
        this.invoiceTitle = invoiceTitle;
    }

    public String getInvoiceTitle() 
    {
        return invoiceTitle;
    }

    public void setTaxNo(String taxNo) 
    {
        this.taxNo = taxNo;
    }

    public String getTaxNo() 
    {
        return taxNo;
    }

    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setFileUrl(String fileUrl) 
    {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() 
    {
        return fileUrl;
    }

    public void setExpressStatus(String expressStatus)
    {
        this.expressStatus = expressStatus;
    }

    public String getExpressStatus()
    {
        return expressStatus;
    }

    public String getTrackingCompany() { return trackingCompany; }
    public void setTrackingCompany(String trackingCompany) { this.trackingCompany = trackingCompany; }
    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }
    public String getDeliveryEmail() { return deliveryEmail; }
    public void setDeliveryEmail(String deliveryEmail) { this.deliveryEmail = deliveryEmail; }

    public void setIssueTime(Date issueTime) 
    {
        this.issueTime = issueTime;
    }

    public Date getIssueTime() 
    {
        return issueTime;
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
            .append("invoiceId", getInvoiceId())
            .append("invoiceNo", getInvoiceNo())
            .append("targetType", getTargetType())
            .append("targetId", getTargetId())
            .append("refSettlementNo", getRefSettlementNo())
            .append("invoiceTitle", getInvoiceTitle())
            .append("taxNo", getTaxNo())
            .append("amount", getAmount())
            .append("status", getStatus())
            .append("fileUrl", getFileUrl())
            .append("expressStatus", getExpressStatus())
            .append("issueTime", getIssueTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
