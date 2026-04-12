package com.ruoyi.jst.event.vo;

/**
 * 首页热门标签 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class HomeHotTagVO {

    private String tag;
    private Integer count;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
