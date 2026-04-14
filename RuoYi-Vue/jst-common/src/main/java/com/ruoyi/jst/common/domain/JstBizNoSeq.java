package com.ruoyi.jst.common.domain;

/**
 * 业务编号日序列实体 jst_biz_no_seq。
 *
 * @author jst
 * @since 1.0.0
 */
public class JstBizNoSeq {

    /** 规则编码。 */
    private String ruleCode;

    /** 日期键。 */
    private String dateKey;

    /** 当前序号。 */
    private Long currentSeq;

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getDateKey() {
        return dateKey;
    }

    public void setDateKey(String dateKey) {
        this.dateKey = dateKey;
    }

    public Long getCurrentSeq() {
        return currentSeq;
    }

    public void setCurrentSeq(Long currentSeq) {
        this.currentSeq = currentSeq;
    }
}
