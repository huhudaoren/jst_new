package com.ruoyi.jst.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 编号规则新增请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class BizNoRuleSaveReqDTO {

    /** 规则编码。 */
    @NotBlank(message = "ruleCode 不能为空")
    @Size(max = 50, message = "ruleCode 长度不能超过 50")
    private String ruleCode;

    /** 规则名称。 */
    @NotBlank(message = "ruleName 不能为空")
    @Size(max = 100, message = "ruleName 长度不能超过 100")
    private String ruleName;

    /** 前缀。 */
    @Size(max = 20, message = "prefix 长度不能超过 20")
    private String prefix;

    /** 日期格式。 */
    @NotBlank(message = "dateFormat 不能为空")
    @Size(max = 20, message = "dateFormat 长度不能超过 20")
    private String dateFormat;

    /** 序号位数。 */
    @NotNull(message = "seqLength 不能为空")
    @Min(value = 1, message = "seqLength 最小为 1")
    @Max(value = 12, message = "seqLength 最大为 12")
    private Integer seqLength;

    /** 起始序号。 */
    @NotNull(message = "seqStart 不能为空")
    @Min(value = 1, message = "seqStart 最小为 1")
    private Long seqStart;

    /** 规则描述。 */
    @Size(max = 500, message = "description 长度不能超过 500")
    private String description;

    /** 规则状态：1启用 0停用。 */
    @NotNull(message = "status 不能为空")
    @Min(value = 0, message = "status 仅支持 0 或 1")
    @Max(value = 1, message = "status 仅支持 0 或 1")
    private Integer status;

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
}
