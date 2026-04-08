package com.ruoyi.jst.message.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 公告对象 jst_notice
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstNotice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    private Long noticeId;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 分类：contest赛事/platform平台/points积分/mall商城 */
    @Excel(name = "分类：contest赛事/platform平台/points积分/mall商城")
    private String category;

    /** 富文本内容 */
    @Excel(name = "富文本内容")
    private String content;

    /** 封面图URL */
    @Excel(name = "封面图URL")
    private String coverImage;

    /** 是否置顶：0否 1是 */
    @Excel(name = "是否置顶：0否 1是")
    private Integer topFlag;

    /** 状态：draft/published/offline */
    @Excel(name = "状态：draft/published/offline")
    private String status;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setNoticeId(Long noticeId) 
    {
        this.noticeId = noticeId;
    }

    public Long getNoticeId() 
    {
        return noticeId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setCoverImage(String coverImage) 
    {
        this.coverImage = coverImage;
    }

    public String getCoverImage() 
    {
        return coverImage;
    }

    public void setTopFlag(Integer topFlag) 
    {
        this.topFlag = topFlag;
    }

    public Integer getTopFlag() 
    {
        return topFlag;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setPublishTime(Date publishTime) 
    {
        this.publishTime = publishTime;
    }

    public Date getPublishTime() 
    {
        return publishTime;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("noticeId", getNoticeId())
            .append("title", getTitle())
            .append("category", getCategory())
            .append("content", getContent())
            .append("coverImage", getCoverImage())
            .append("topFlag", getTopFlag())
            .append("status", getStatus())
            .append("publishTime", getPublishTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
