package com.ruoyi.jst.channel.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

public class JstSalesChannelBinding extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long bindingId;
    private Long channelId;
    private Long salesId;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String bindSource;
    private String bindRemark;
    private Long operatorId;
    private String salesName;
    private String channelName;

    public Long getBindingId() { return bindingId; }
    public void setBindingId(Long bindingId) { this.bindingId = bindingId; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public Long getSalesId() { return salesId; }
    public void setSalesId(Long salesId) { this.salesId = salesId; }
    public Date getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(Date effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public Date getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(Date effectiveTo) { this.effectiveTo = effectiveTo; }
    public String getBindSource() { return bindSource; }
    public void setBindSource(String bindSource) { this.bindSource = bindSource; }
    public String getBindRemark() { return bindRemark; }
    public void setBindRemark(String bindRemark) { this.bindRemark = bindRemark; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getSalesName() { return salesName; }
    public void setSalesName(String salesName) { this.salesName = salesName; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
}
