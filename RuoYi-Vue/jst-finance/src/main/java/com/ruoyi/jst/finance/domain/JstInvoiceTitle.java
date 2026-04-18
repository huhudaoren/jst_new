package com.ruoyi.jst.finance.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 发票抬头 jst_invoice_title。
 * <p>
 * 对应 DDL：架构设计/ddl/99-migration-invoice-title.sql
 * 来源任务：BACKEND-UX-POLISH-TODO-ROUND-2.md · B3
 *
 * @author jst
 * @since 1.0.0
 */
public class JstInvoiceTitle extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 抬头ID */
    private Long titleId;

    /** 所属用户ID */
    @Excel(name = "所属用户ID")
    private Long userId;

    /** 抬头类型: personal个人 / company企业 */
    @Excel(name = "抬头类型", readConverterExp = "personal=个人,company=企业")
    private String titleType;

    /** 抬头名称 */
    @Excel(name = "抬头名称")
    private String titleName;

    /** 税号 (company 必填) */
    @Excel(name = "税号")
    private String taxNo;

    /** 是否默认: 0否 1是 */
    @Excel(name = "是否默认")
    private Integer isDefault;

    /** 逻辑删除: 0存在 2删除 */
    private String delFlag;

    public Long getTitleId() {
        return titleId;
    }

    public void setTitleId(Long titleId) {
        this.titleId = titleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("titleId", getTitleId())
                .append("userId", getUserId())
                .append("titleType", getTitleType())
                .append("titleName", getTitleName())
                .append("taxNo", getTaxNo())
                .append("isDefault", getIsDefault())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .toString();
    }
}
