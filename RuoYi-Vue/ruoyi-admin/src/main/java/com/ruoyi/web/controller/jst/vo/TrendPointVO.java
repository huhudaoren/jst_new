package com.ruoyi.web.controller.jst.vo;

import java.math.BigDecimal;

/**
 * 平台运营趋势点出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class TrendPointVO {

    private String date;
    private Integer orderCount;
    private BigDecimal revenue;
    private Integer enrollCount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Integer getEnrollCount() {
        return enrollCount;
    }

    public void setEnrollCount(Integer enrollCount) {
        this.enrollCount = enrollCount;
    }
}
