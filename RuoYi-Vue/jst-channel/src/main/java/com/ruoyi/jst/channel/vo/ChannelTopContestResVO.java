package com.ruoyi.jst.channel.vo;

/**
 * 渠道工作台热门赛事返回对象。
 *
 * @author jst
 * @since 1.0.0
 */
public class ChannelTopContestResVO {

    private Long contestId;
    private String contestName;
    private String category;
    private Integer enrollCount;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getEnrollCount() {
        return enrollCount;
    }

    public void setEnrollCount(Integer enrollCount) {
        this.enrollCount = enrollCount;
    }
}
