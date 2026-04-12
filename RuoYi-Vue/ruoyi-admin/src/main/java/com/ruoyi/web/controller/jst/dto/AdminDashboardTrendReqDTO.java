package com.ruoyi.web.controller.jst.dto;

import jakarta.validation.constraints.Pattern;

/**
 * 平台运营数据趋势查询入参。
 *
 * @author jst
 * @since 1.0.0
 */
public class AdminDashboardTrendReqDTO {

    @Pattern(regexp = "^(7|30)?$", message = "days 仅支持 7 或 30")
    private String days;

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }
}
