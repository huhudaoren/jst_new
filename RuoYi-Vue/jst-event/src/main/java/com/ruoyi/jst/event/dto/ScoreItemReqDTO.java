package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 单项成绩打分请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ScoreItemReqDTO {

    /** 成绩项ID（FK jst_score_item.item_id） */
    @NotNull(message = "itemId 不能为空")
    private Long itemId;

    /** 打分值 */
    @NotNull(message = "score 不能为空")
    @DecimalMin(value = "0", message = "score 不能为负数")
    private BigDecimal score;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }
}
