package com.ruoyi.jst.event.vo;

/**
 * 报名按"用户所属渠道"聚合结果 VO。
 * <p>
 * 用于管理端报名列表"按渠道查看" Tab 的一级行渲染。
 * 关联任务：ADMIN-UX-B3 主线 B。
 *
 * @author jst
 * @since 1.0.0
 */
public class EnrollChannelGroupVO {

    private Long channelId;

    private String channelName;

    private Integer enrollCount;

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

    public Integer getEnrollCount() {
        return enrollCount;
    }

    public void setEnrollCount(Integer enrollCount) {
        this.enrollCount = enrollCount;
    }
}
