package com.ruoyi.jst.channel.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

public class JstSales extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long salesId;
    private Long sysUserId;
    private String salesNo;
    private String salesName;
    private String phone;
    private Long managerId;
    private Integer isManager;
    private String status;
    private Date resignApplyDate;
    private Date expectedResignDate;
    private Date actualResignDate;
    private Date transitionEndDate;
    private BigDecimal commissionRateDefault;
    private BigDecimal managerCommissionRate;

    public Long getSalesId() { return salesId; }
    public void setSalesId(Long salesId) { this.salesId = salesId; }
    public Long getSysUserId() { return sysUserId; }
    public void setSysUserId(Long sysUserId) { this.sysUserId = sysUserId; }
    public String getSalesNo() { return salesNo; }
    public void setSalesNo(String salesNo) { this.salesNo = salesNo; }
    public String getSalesName() { return salesName; }
    public void setSalesName(String salesName) { this.salesName = salesName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public Integer getIsManager() { return isManager; }
    public void setIsManager(Integer isManager) { this.isManager = isManager; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getResignApplyDate() { return resignApplyDate; }
    public void setResignApplyDate(Date resignApplyDate) { this.resignApplyDate = resignApplyDate; }
    public Date getExpectedResignDate() { return expectedResignDate; }
    public void setExpectedResignDate(Date expectedResignDate) { this.expectedResignDate = expectedResignDate; }
    public Date getActualResignDate() { return actualResignDate; }
    public void setActualResignDate(Date actualResignDate) { this.actualResignDate = actualResignDate; }
    public Date getTransitionEndDate() { return transitionEndDate; }
    public void setTransitionEndDate(Date transitionEndDate) { this.transitionEndDate = transitionEndDate; }
    public BigDecimal getCommissionRateDefault() { return commissionRateDefault; }
    public void setCommissionRateDefault(BigDecimal commissionRateDefault) { this.commissionRateDefault = commissionRateDefault; }
    public BigDecimal getManagerCommissionRate() { return managerCommissionRate; }
    public void setManagerCommissionRate(BigDecimal managerCommissionRate) { this.managerCommissionRate = managerCommissionRate; }
}
