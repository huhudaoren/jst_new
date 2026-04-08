package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 统一打款记录对象 jst_payment_pay_record
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstPaymentPayRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 打款记录ID */
    private Long payRecordId;

    /** 打款单号 */
    @Excel(name = "打款单号")
    private String payNo;

    /** 业务类型：rebate_withdraw渠道提现/event_settlement赛事方结算 */
    @Excel(name = "业务类型：rebate_withdraw渠道提现/event_settlement赛事方结算")
    private String businessType;

    /** 业务单据ID */
    @Excel(name = "业务单据ID")
    private Long businessId;

    /** 收款方类型：channel/partner */
    @Excel(name = "收款方类型：channel/partner")
    private String targetType;

    /** 收款方ID */
    @Excel(name = "收款方ID")
    private Long targetId;

    /** 打款金额 */
    @Excel(name = "打款金额")
    private BigDecimal amount;

    /** 打款账户快照 */
    @Excel(name = "打款账户快照")
    private String payAccount;

    /** 打款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "打款时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    /** 打款凭证URL */
    @Excel(name = "打款凭证URL")
    private String voucherUrl;

    /** 操作人 */
    @Excel(name = "操作人")
    private Long operatorId;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setPayRecordId(Long payRecordId) 
    {
        this.payRecordId = payRecordId;
    }

    public Long getPayRecordId() 
    {
        return payRecordId;
    }

    public void setPayNo(String payNo) 
    {
        this.payNo = payNo;
    }

    public String getPayNo() 
    {
        return payNo;
    }

    public void setBusinessType(String businessType) 
    {
        this.businessType = businessType;
    }

    public String getBusinessType() 
    {
        return businessType;
    }

    public void setBusinessId(Long businessId) 
    {
        this.businessId = businessId;
    }

    public Long getBusinessId() 
    {
        return businessId;
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

    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }

    public void setPayAccount(String payAccount) 
    {
        this.payAccount = payAccount;
    }

    public String getPayAccount() 
    {
        return payAccount;
    }

    public void setPayTime(Date payTime) 
    {
        this.payTime = payTime;
    }

    public Date getPayTime() 
    {
        return payTime;
    }

    public void setVoucherUrl(String voucherUrl) 
    {
        this.voucherUrl = voucherUrl;
    }

    public String getVoucherUrl() 
    {
        return voucherUrl;
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
            .append("payRecordId", getPayRecordId())
            .append("payNo", getPayNo())
            .append("businessType", getBusinessType())
            .append("businessId", getBusinessId())
            .append("targetType", getTargetType())
            .append("targetId", getTargetId())
            .append("amount", getAmount())
            .append("payAccount", getPayAccount())
            .append("payTime", getPayTime())
            .append("voucherUrl", getVoucherUrl())
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
