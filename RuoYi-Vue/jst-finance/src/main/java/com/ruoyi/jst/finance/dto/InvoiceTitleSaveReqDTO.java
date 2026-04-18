package com.ruoyi.jst.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 发票抬头新增/更新请求 DTO。
 * <p>
 * - titleId 非空：更新（归属校验在 Service 层）
 * - titleId 为空：新增
 * - titleType=company 时 taxNo 非空（Service 层二次校验）
 *
 * @author jst
 * @since 1.0.0
 */
public class InvoiceTitleSaveReqDTO {

    /** 抬头ID (更新时传) */
    private Long titleId;

    /** 抬头类型: personal / company */
    @NotBlank(message = "抬头类型不能为空")
    @Pattern(regexp = "^(personal|company)$", message = "抬头类型仅支持 personal / company")
    private String titleType;

    /** 抬头名称 */
    @NotBlank(message = "抬头名称不能为空")
    @Size(max = 128, message = "抬头名称不能超过 128 字")
    private String titleName;

    /** 税号 (company 必填) */
    @Size(max = 64, message = "税号不能超过 64 字")
    private String taxNo;

    public Long getTitleId() {
        return titleId;
    }

    public void setTitleId(Long titleId) {
        this.titleId = titleId;
    }

    public String getTitleType() {
        return titleType;
    }

    public void setTitleType(String titleType) {
        this.titleType = titleType;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }
}
