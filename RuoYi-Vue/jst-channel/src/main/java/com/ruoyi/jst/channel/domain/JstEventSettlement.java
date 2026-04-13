package com.ruoyi.jst.channel.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事方结算单对象 jst_event_settlement
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstEventSettlement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 结算单ID */
    private Long eventSettlementId;

    /** 结算单号 */
    @Excel(name = "结算单号")
    private String settlementNo;

    /** 赛事方ID */
    @Excel(name = "赛事方ID")
    private Long partnerId;

    /** 赛事ID */
    @Excel(name = "赛事ID")
    private Long contestId;

    /** 标价合计 */
    @Excel(name = "标价合计")
    private BigDecimal totalListAmount;

    /** 优惠券合计 */
    @Excel(name = "优惠券合计")
    private BigDecimal totalCouponAmount;

    /** 积分抵扣合计 */
    @Excel(name = "积分抵扣合计")
    private BigDecimal totalPointsAmount;

    /** 平台承担合计 */
    @Excel(name = "平台承担合计")
    private BigDecimal platformBearAmount;

    /** 用户净实付合计 */
    @Excel(name = "用户净实付合计")
    private BigDecimal totalNetPay;

    /** 退款合计 */
    @Excel(name = "退款合计")
    private BigDecimal totalRefund;

    /** 渠道返点合计 */
    @Excel(name = "渠道返点合计")
    private BigDecimal totalRebate;

    /** 服务费合计 */
    @Excel(name = "服务费合计")
    private BigDecimal totalServiceFee;

    /** 合同约定扣项 */
    @Excel(name = "合同约定扣项")
    private BigDecimal contractDeduction;

    /** 最终结算金额 */
    @Excel(name = "最终结算金额")
    private BigDecimal finalAmount;

    /** 状态：pending_confirm/reviewing/rejected/pending_pay/paid */
    @Excel(name = "状态：pending_confirm/reviewing/rejected/pending_pay/paid")
    private String status;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String auditRemark;

    /** 打款凭证URL */
    @Excel(name = "打款凭证URL")
    private String payVoucherUrl;

    /** 打款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "打款时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    /** 赛事方名称（JOIN查出，非持久化） */
    private String partnerName;

    /** 赛事名称（JOIN查出，非持久化） */
    private String contestName;

    public void setEventSettlementId(Long eventSettlementId) 
    {
        this.eventSettlementId = eventSettlementId;
    }

    public Long getEventSettlementId() 
    {
        return eventSettlementId;
    }

    public void setSettlementNo(String settlementNo) 
    {
        this.settlementNo = settlementNo;
    }

    public String getSettlementNo() 
    {
        return settlementNo;
    }

    public void setPartnerId(Long partnerId) 
    {
        this.partnerId = partnerId;
    }

    public Long getPartnerId() 
    {
        return partnerId;
    }

    public void setContestId(Long contestId) 
    {
        this.contestId = contestId;
    }

    public Long getContestId() 
    {
        return contestId;
    }

    public void setTotalListAmount(BigDecimal totalListAmount) 
    {
        this.totalListAmount = totalListAmount;
    }

    public BigDecimal getTotalListAmount() 
    {
        return totalListAmount;
    }

    public void setTotalCouponAmount(BigDecimal totalCouponAmount) 
    {
        this.totalCouponAmount = totalCouponAmount;
    }

    public BigDecimal getTotalCouponAmount() 
    {
        return totalCouponAmount;
    }

    public void setTotalPointsAmount(BigDecimal totalPointsAmount) 
    {
        this.totalPointsAmount = totalPointsAmount;
    }

    public BigDecimal getTotalPointsAmount() 
    {
        return totalPointsAmount;
    }

    public void setPlatformBearAmount(BigDecimal platformBearAmount) 
    {
        this.platformBearAmount = platformBearAmount;
    }

    public BigDecimal getPlatformBearAmount() 
    {
        return platformBearAmount;
    }

    public void setTotalNetPay(BigDecimal totalNetPay) 
    {
        this.totalNetPay = totalNetPay;
    }

    public BigDecimal getTotalNetPay() 
    {
        return totalNetPay;
    }

    public void setTotalRefund(BigDecimal totalRefund) 
    {
        this.totalRefund = totalRefund;
    }

    public BigDecimal getTotalRefund() 
    {
        return totalRefund;
    }

    public void setTotalRebate(BigDecimal totalRebate) 
    {
        this.totalRebate = totalRebate;
    }

    public BigDecimal getTotalRebate() 
    {
        return totalRebate;
    }

    public void setTotalServiceFee(BigDecimal totalServiceFee) 
    {
        this.totalServiceFee = totalServiceFee;
    }

    public BigDecimal getTotalServiceFee() 
    {
        return totalServiceFee;
    }

    public void setContractDeduction(BigDecimal contractDeduction) 
    {
        this.contractDeduction = contractDeduction;
    }

    public BigDecimal getContractDeduction() 
    {
        return contractDeduction;
    }

    public void setFinalAmount(BigDecimal finalAmount) 
    {
        this.finalAmount = finalAmount;
    }

    public BigDecimal getFinalAmount() 
    {
        return finalAmount;
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

    public void setPartnerName(String partnerName)
    {
        this.partnerName = partnerName;
    }

    public String getPartnerName()
    {
        return partnerName;
    }

    public void setContestName(String contestName)
    {
        this.contestName = contestName;
    }

    public String getContestName()
    {
        return contestName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("eventSettlementId", getEventSettlementId())
            .append("settlementNo", getSettlementNo())
            .append("partnerId", getPartnerId())
            .append("contestId", getContestId())
            .append("totalListAmount", getTotalListAmount())
            .append("totalCouponAmount", getTotalCouponAmount())
            .append("totalPointsAmount", getTotalPointsAmount())
            .append("platformBearAmount", getPlatformBearAmount())
            .append("totalNetPay", getTotalNetPay())
            .append("totalRefund", getTotalRefund())
            .append("totalRebate", getTotalRebate())
            .append("totalServiceFee", getTotalServiceFee())
            .append("contractDeduction", getContractDeduction())
            .append("finalAmount", getFinalAmount())
            .append("status", getStatus())
            .append("auditRemark", getAuditRemark())
            .append("payVoucherUrl", getPayVoucherUrl())
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
