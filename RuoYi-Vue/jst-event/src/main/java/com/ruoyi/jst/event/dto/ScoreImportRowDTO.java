package com.ruoyi.jst.event.dto;

import com.ruoyi.common.annotation.Excel;

import java.math.BigDecimal;

/**
 * 成绩导入 Excel 行。
 *
 * @author jst
 * @since 1.0.0
 */
public class ScoreImportRowDTO {

    /** 报名ID。 */
    @Excel(name = "报名ID")
    private Long enrollId;

    /** 分数。 */
    @Excel(name = "分数")
    private BigDecimal scoreValue;

    /** 奖项等级。 */
    @Excel(name = "奖项等级")
    private String awardLevel;

    /** 名次。 */
    @Excel(name = "名次")
    private Long rankNo;

    /** 评语。 */
    @Excel(name = "评语")
    private String remark;

    public Long getEnrollId() {
        return enrollId;
    }

    public void setEnrollId(Long enrollId) {
        this.enrollId = enrollId;
    }

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    public String getAwardLevel() {
        return awardLevel;
    }

    public void setAwardLevel(String awardLevel) {
        this.awardLevel = awardLevel;
    }

    public Long getRankNo() {
        return rankNo;
    }

    public void setRankNo(Long rankNo) {
        this.rankNo = rankNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
