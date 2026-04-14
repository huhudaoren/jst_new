package com.ruoyi.jst.common.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 业务编号规则实体 jst_biz_no_rule。
 *
 * @author jst
 * @since 1.0.0
 */
public class JstBizNoRule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 规则ID。 */
    private Long ruleId;

    /** 规则编码。 */
    private String ruleCode;

    /** 规则名称。 */
    private String ruleName;

    /** 编号前缀。 */
    private String prefix;

    /** 日期格式。 */
    private String dateFormat;

    /** 序号位数。 */
    private Integer seqLength;

    /** 起始序号。 */
    private Long seqStart;

    /** 规则描述。 */
    private String description;

    /** 启用状态：1启用 0停用。 */
    private Integer status;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Integer getSeqLength() {
        return seqLength;
    }

    public void setSeqLength(Integer seqLength) {
        this.seqLength = seqLength;
    }

    public Long getSeqStart() {
        return seqStart;
    }

    public void setSeqStart(Long seqStart) {
        this.seqStart = seqStart;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("ruleId", getRuleId())
                .append("ruleCode", getRuleCode())
                .append("ruleName", getRuleName())
                .append("prefix", getPrefix())
                .append("dateFormat", getDateFormat())
                .append("seqLength", getSeqLength())
                .append("seqStart", getSeqStart())
                .append("description", getDescription())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
