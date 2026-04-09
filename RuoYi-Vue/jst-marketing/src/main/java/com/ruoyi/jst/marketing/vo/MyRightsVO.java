package com.ruoyi.jst.marketing.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User rights list view.
 */
public class MyRightsVO {

    private Long userRightsId;
    private Long rightsTemplateId;
    private String rightsName;
    private String rightsType;
    private String quotaMode;
    private BigDecimal quotaValue;
    private BigDecimal remainQuota;
    private String writeoffMode;
    private String status;
    private Boolean expired;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validEnd;

    public Long getUserRightsId() {
        return userRightsId;
    }

    public void setUserRightsId(Long userRightsId) {
        this.userRightsId = userRightsId;
    }

    public Long getRightsTemplateId() {
        return rightsTemplateId;
    }

    public void setRightsTemplateId(Long rightsTemplateId) {
        this.rightsTemplateId = rightsTemplateId;
    }

    public String getRightsName() {
        return rightsName;
    }

    public void setRightsName(String rightsName) {
        this.rightsName = rightsName;
    }

    public String getRightsType() {
        return rightsType;
    }

    public void setRightsType(String rightsType) {
        this.rightsType = rightsType;
    }

    public String getQuotaMode() {
        return quotaMode;
    }

    public void setQuotaMode(String quotaMode) {
        this.quotaMode = quotaMode;
    }

    public BigDecimal getQuotaValue() {
        return quotaValue;
    }

    public void setQuotaValue(BigDecimal quotaValue) {
        this.quotaValue = quotaValue;
    }

    public BigDecimal getRemainQuota() {
        return remainQuota;
    }

    public void setRemainQuota(BigDecimal remainQuota) {
        this.remainQuota = remainQuota;
    }

    public String getWriteoffMode() {
        return writeoffMode;
    }

    public void setWriteoffMode(String writeoffMode) {
        this.writeoffMode = writeoffMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public Date getValidStart() {
        return validStart;
    }

    public void setValidStart(Date validStart) {
        this.validStart = validStart;
    }

    public Date getValidEnd() {
        return validEnd;
    }

    public void setValidEnd(Date validEnd) {
        this.validEnd = validEnd;
    }
}
