package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 成绩更正申请请求。
 *
 * @author jst
 * @since 1.0.0
 */
public class ScoreCorrectionApplyReqDTO {

    /** 成绩ID。 */
    @NotNull(message = "成绩ID不能为空")
    private Long scoreId;

    /** 新分数。 */
    @DecimalMin(value = "0.00", message = "新分数不能小于0")
    private BigDecimal newScoreValue;

    /** 新奖项等级。 */
    @Size(max = 32, message = "新奖项等级长度不能超过32个字符")
    private String newAwardLevel;

    /** 新名次。 */
    @Min(value = 1, message = "新名次必须大于0")
    private Long newRankNo;

    /** 更正原因。 */
    @NotBlank(message = "更正原因不能为空")
    @Size(max = 255, message = "更正原因长度不能超过255个字符")
    private String reason;

    public Long getScoreId() {
        return scoreId;
    }

    public void setScoreId(Long scoreId) {
        this.scoreId = scoreId;
    }

    public BigDecimal getNewScoreValue() {
        return newScoreValue;
    }

    public void setNewScoreValue(BigDecimal newScoreValue) {
        this.newScoreValue = newScoreValue;
    }

    public String getNewAwardLevel() {
        return newAwardLevel;
    }

    public void setNewAwardLevel(String newAwardLevel) {
        this.newAwardLevel = newAwardLevel;
    }

    public Long getNewRankNo() {
        return newRankNo;
    }

    public void setNewRankNo(Long newRankNo) {
        this.newRankNo = newRankNo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
