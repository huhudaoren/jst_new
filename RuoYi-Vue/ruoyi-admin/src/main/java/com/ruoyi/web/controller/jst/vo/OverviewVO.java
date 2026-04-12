package com.ruoyi.web.controller.jst.vo;

import java.math.BigDecimal;

/**
 * 平台运营总览指标出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class OverviewVO {

    private Integer todayOrderCount;
    private BigDecimal todayRevenue;
    private BigDecimal monthRevenue;
    private Integer totalUserCount;
    private Integer totalContestCount;
    private Integer totalEnrollCount;

    public Integer getTodayOrderCount() {
        return todayOrderCount;
    }

    public void setTodayOrderCount(Integer todayOrderCount) {
        this.todayOrderCount = todayOrderCount;
    }

    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public BigDecimal getMonthRevenue() {
        return monthRevenue;
    }

    public void setMonthRevenue(BigDecimal monthRevenue) {
        this.monthRevenue = monthRevenue;
    }

    public Integer getTotalUserCount() {
        return totalUserCount;
    }

    public void setTotalUserCount(Integer totalUserCount) {
        this.totalUserCount = totalUserCount;
    }

    public Integer getTotalContestCount() {
        return totalContestCount;
    }

    public void setTotalContestCount(Integer totalContestCount) {
        this.totalContestCount = totalContestCount;
    }

    public Integer getTotalEnrollCount() {
        return totalEnrollCount;
    }

    public void setTotalEnrollCount(Integer totalEnrollCount) {
        this.totalEnrollCount = totalEnrollCount;
    }
}
