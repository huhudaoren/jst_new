package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 赛事方成绩保存请求。
 *
 * @author jst
 * @since 1.0.0
 */
public class ScoreSaveReqDTO {

    /** 赛事ID。 */
    @NotNull(message = "赛事ID不能为空")
    private Long contestId;

    /** 报名ID。 */
    @NotNull(message = "报名ID不能为空")
    private Long enrollId;

    /** 分数。 */
    @DecimalMin(value = "0.00", message = "分数不能小于0")
    private BigDecimal scoreValue;

    /** 奖项等级。 */
    @Size(max = 32, message = "奖项等级长度不能超过32个字符")
    private String awardLevel;

    /** 名次。 */
    @Min(value = 1, message = "名次必须大于0")
    private Long rankNo;

    /** 评语。 */
    @Size(max = 500, message = "评语长度不能超过500个字符")
    private String remark;

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

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
