package com.ruoyi.jst.common.vo;

import java.util.Date;

/**
 * 编号规则响应 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class BizNoRuleResVO {

    /** 规则ID。 */
    private Long ruleId;

    /** 规则编码。 */
    private String ruleCode;

    /** 规则名称。 */
    private String ruleName;

    /** 前缀。 */
    private String prefix;

    /** 日期格式。 */
    private String dateFormat;

    /** 序号位数。 */
    private Integer seqLength;

    /** 起始序号。 */
    private Long seqStart;

    /** 规则描述。 */
    private String description;

    /** 规则状态。 */
    private Integer status;

    /** 创建时间。 */
    private Date createTime;

    /** 更新时间。 */
    private Date updateTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
