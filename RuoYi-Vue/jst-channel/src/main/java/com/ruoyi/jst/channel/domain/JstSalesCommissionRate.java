package com.ruoyi.jst.channel.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

public class JstSalesCommissionRate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long salesId;
    private String businessType;
    private BigDecimal rate;
    private Date effectiveFrom;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSalesId() { return salesId; }
    public void setSalesId(Long salesId) { this.salesId = salesId; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public Date getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(Date effectiveFrom) { this.effectiveFrom = effectiveFrom; }
}
