package com.ruoyi.web.controller.jst.vo;

import java.math.BigDecimal;

/**
 * 平台运营赛事排行出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class ContestRankVO {

    private Long contestId;
    private String contestName;
    private Integer enrollCount;
    private BigDecimal totalRevenue;

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public String getContestName() {
        return contestName;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public Integer getEnrollCount() {
        return enrollCount;
    }

    public void setEnrollCount(Integer enrollCount) {
        this.enrollCount = enrollCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
