package com.ruoyi.jst.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

/**
 * 小程序端发票申请入参。
 *
 * @author jst
 * @since 1.0.0
 */
public class InvoiceApplyForm {

    /** 关联结算/提现单号 */
    @NotBlank(message = "关联结算单号不能为空")
    private String refSettlementNo;

    /** 发票抬头 */
    @NotBlank(message = "发票抬头不能为空")
    private String invoiceTitle;

    /** 税号 */
    @NotBlank(message = "税号不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{15,20}$", message = "税号格式不合法")
    private String taxNo;

    /** 开票金额 */
    @NotNull(message = "开票金额不能为空")
    @DecimalMin(value = "0.01", message = "开票金额必须大于0")
    private BigDecimal amount;

    /** 备注 */
    private String remark;

    public String getRefSettlementNo() {
        return refSettlementNo;
    }

    public void setRefSettlementNo(String refSettlementNo) {
        this.refSettlementNo = refSettlementNo;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
