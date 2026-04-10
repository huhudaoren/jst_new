package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * 渠道工作台排行榜查询入参。
 *
 * @author jst
 * @since 1.0.0
 */
public class ChannelTopRankingReqDTO {

    @Pattern(regexp = "^(last7d|last30d|last90d)?$", message = "period 仅支持 last7d/last30d/last90d")
    private String period;

    @Min(value = 1, message = "limit 最小为 1")
    @Max(value = 20, message = "limit 最大为 20")
    private Integer limit;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
