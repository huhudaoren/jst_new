package com.ruoyi.jst.channel.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Trend data for the channel dashboard.
 */
public class DashboardStatsVO {

    private String period;
    private List<DashboardTrendPointVO> orderTrend;
    private List<DashboardTrendPointVO> rebateTrend;
    private List<TopContestVO> topContests;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<DashboardTrendPointVO> getOrderTrend() {
        return orderTrend;
    }

    public void setOrderTrend(List<DashboardTrendPointVO> orderTrend) {
        this.orderTrend = orderTrend;
    }

    public List<DashboardTrendPointVO> getRebateTrend() {
        return rebateTrend;
    }

    public void setRebateTrend(List<DashboardTrendPointVO> rebateTrend) {
        this.rebateTrend = rebateTrend;
    }

    public List<TopContestVO> getTopContests() {
        return topContests;
    }

    public void setTopContests(List<TopContestVO> topContests) {
        this.topContests = topContests;
    }

    /**
     * Common chart point.
     */
    public static class DashboardTrendPointVO {

        private String date;
        private Integer count;
        private BigDecimal amount;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    /**
     * Contest rank item.
     */
    public static class TopContestVO {

        private Long contestId;
        private String contestName;
        private Integer orderCount;

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

        public Integer getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Integer orderCount) {
            this.orderCount = orderCount;
        }
    }
}
