package com.ruoyi.web.controller.jst.vo;

import java.math.BigDecimal;

/**
 * 平台运营渠道排行出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class ChannelRankVO {

    private Long channelId;
    private String channelName;
    private Integer studentCount;
    private BigDecimal totalRebate;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public BigDecimal getTotalRebate() {
        return totalRebate;
    }

    public void setTotalRebate(BigDecimal totalRebate) {
        this.totalRebate = totalRebate;
    }
}
