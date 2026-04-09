package com.ruoyi.jst.marketing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Rights template create/update DTO.
 */
public class RightsTemplateSaveDTO {

    private Long rightsTemplateId;

    @NotBlank(message = "rightsName不能为空")
    @Size(max = 128, message = "rightsName长度不能超过128")
    private String rightsName;

    @NotBlank(message = "rightsType不能为空")
    private String rightsType;

    @NotBlank(message = "quotaMode不能为空")
    private String quotaMode;

    @DecimalMin(value = "0.00", message = "quotaValue不能为负数")
    private BigDecimal quotaValue;

    @NotNull(message = "validDays不能为空")
    private Integer validDays;

    @NotBlank(message = "writeoffMode不能为空")
    private String writeoffMode;

    @NotBlank(message = "applicableRole不能为空")
    private String applicableRole;

    @Size(max = 500, message = "remark长度不能超过500")
    private String remark;

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

    public Integer getValidDays() {
        return validDays;
    }

    public void setValidDays(Integer validDays) {
        this.validDays = validDays;
    }

    public String getWriteoffMode() {
        return writeoffMode;
    }

    public void setWriteoffMode(String writeoffMode) {
        this.writeoffMode = writeoffMode;
    }

    public String getApplicableRole() {
        return applicableRole;
    }

    public void setApplicableRole(String applicableRole) {
        this.applicableRole = applicableRole;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
