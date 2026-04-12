package com.ruoyi.web.controller.jst.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 平台运营数据排行榜查询入参。
 *
 * @author jst
 * @since 1.0.0
 */
public class AdminDashboardTopReqDTO {

    @Min(value = 1, message = "limit 最小为 1")
    @Max(value = 10, message = "limit 最大为 10")
    private Integer limit;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
