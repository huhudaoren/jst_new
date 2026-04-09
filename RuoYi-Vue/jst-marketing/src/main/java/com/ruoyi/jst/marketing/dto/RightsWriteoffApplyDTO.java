package com.ruoyi.jst.marketing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Rights writeoff apply DTO.
 */
public class RightsWriteoffApplyDTO {

    @DecimalMin(value = "0.00", inclusive = false, message = "writeoffAmount必须大于0")
    private BigDecimal writeoffAmount;

    @Size(max = 255, message = "remark长度不能超过255")
    private String remark;

    public BigDecimal getWriteoffAmount() {
        return writeoffAmount;
    }

    public void setWriteoffAmount(BigDecimal writeoffAmount) {
        this.writeoffAmount = writeoffAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
