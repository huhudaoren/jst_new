package com.ruoyi.jst.channel.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

public class JstSalesCommissionLedger extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long ledgerId;
    private Long salesId;
    private Long orderId;
    private String orderNo;
    private Long channelId;
    private String bindingType;
    private String businessType;
    private BigDecimal baseAmount;
    private BigDecimal appliedRate;
    private BigDecimal rawAmount;
    private BigDecimal compressRatio;
    private BigDecimal amount;
    private String status;
    private Date accrueAt;
    private Date accruedAt;
    private Long settlementId;

    public Long getLedgerId() { return ledgerId; }
    public void setLedgerId(Long ledgerId) { this.ledgerId = ledgerId; }
    public Long getSalesId() { return salesId; }
    public void setSalesId(Long salesId) { this.salesId = salesId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public String getBindingType() { return bindingType; }
    public void setBindingType(String bindingType) { this.bindingType = bindingType; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }
    public BigDecimal getAppliedRate() { return appliedRate; }
    public void setAppliedRate(BigDecimal appliedRate) { this.appliedRate = appliedRate; }
    public BigDecimal getRawAmount() { return rawAmount; }
    public void setRawAmount(BigDecimal rawAmount) { this.rawAmount = rawAmount; }
    public BigDecimal getCompressRatio() { return compressRatio; }
    public void setCompressRatio(BigDecimal compressRatio) { this.compressRatio = compressRatio; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getAccrueAt() { return accrueAt; }
    public void setAccrueAt(Date accrueAt) { this.accrueAt = accrueAt; }
    public Date getAccruedAt() { return accruedAt; }
    public void setAccruedAt(Date accruedAt) { this.accruedAt = accruedAt; }
    public Long getSettlementId() { return settlementId; }
    public void setSettlementId(Long settlementId) { this.settlementId = settlementId; }
}
