package com.ruoyi.jst.message.vo;

import java.util.Date;

/**
 * 小程序公告详情 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class WxNoticeDetailVO {

    /** 公告ID。 */
    private Long noticeId;

    /** 标题。 */
    private String title;

    /** 分类。 */
    private String category;

    /** 封面图。 */
    private String coverImage;

    /** 是否置顶。 */
    private Boolean topFlag;

    /** 发布时间。 */
    private Date publishTime;

    /** 富文本正文。 */
    private String content;

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Boolean getTopFlag() {
        return topFlag;
    }

    public void setTopFlag(Boolean topFlag) {
        this.topFlag = topFlag;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
