package com.ruoyi.jst.marketing.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Rights detail view.
 */
public class RightsDetailVO {

    private Long userRightsId;
    private Long rightsTemplateId;
    private String ownerType;
    private Long ownerId;
    private String sourceType;
    private Long sourceRefId;
    private String rightsName;
    private String rightsType;
    private String quotaMode;
    private BigDecimal quotaValue;
    private BigDecimal remainQuota;
    private String writeoffMode;
    private String status;
    private Boolean expired;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validEnd;

    private List<WriteoffRecordVO> writeoffHistory;

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

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getSourceRefId() {
        return sourceRefId;
    }

    public void setSourceRefId(Long sourceRefId) {
        this.sourceRefId = sourceRefId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public List<WriteoffRecordVO> getWriteoffHistory() {
        return writeoffHistory;
    }

    public void setWriteoffHistory(List<WriteoffRecordVO> writeoffHistory) {
        this.writeoffHistory = writeoffHistory;
    }

    public static class WriteoffRecordVO {
        private Long recordId;
        private String writeoffNo;
        private BigDecimal useAmount;
        private String applyRemark;
        private String status;
        private String auditRemark;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date writeoffTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createTime;

        public Long getRecordId() {
            return recordId;
        }

        public void setRecordId(Long recordId) {
            this.recordId = recordId;
        }

        public String getWriteoffNo() {
            return writeoffNo;
        }

        public void setWriteoffNo(String writeoffNo) {
            this.writeoffNo = writeoffNo;
        }

        public BigDecimal getUseAmount() {
            return useAmount;
        }

        public void setUseAmount(BigDecimal useAmount) {
            this.useAmount = useAmount;
        }

        public String getApplyRemark() {
            return applyRemark;
        }

        public void setApplyRemark(String applyRemark) {
            this.applyRemark = applyRemark;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAuditRemark() {
            return auditRemark;
        }

        public void setAuditRemark(String auditRemark) {
            this.auditRemark = auditRemark;
        }

        public Date getWriteoffTime() {
            return writeoffTime;
        }

        public void setWriteoffTime(Date writeoffTime) {
            this.writeoffTime = writeoffTime;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }
    }
}
