package com.ruoyi.jst.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * 编号规则列表查询请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class BizNoRuleQueryReqDTO {

    /** 规则编码。 */
    @Size(max = 50, message = "ruleCode 长度不能超过 50")
    private String ruleCode;

    /** 规则名称。 */
    @Size(max = 100, message = "ruleName 长度不能超过 100")
    private String ruleName;

    /** 规则状态：1启用 0停用。 */
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
