package com.ruoyi.jst.message.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 公告新增/编辑请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class NoticeSaveReqDTO {

    /** 公告ID，编辑时必填。 */
    private Long noticeId;

    /** 标题。 */
    @NotBlank(message = "title 不能为空")
    @Size(max = 255, message = "title 长度不能超过 255")
    private String title;

    /** 分类。 */
    @NotBlank(message = "category 不能为空")
    @Pattern(regexp = "contest|platform|points|mall", message = "category 仅支持 contest/platform/points/mall")
    private String category;

    /** 富文本内容。 */
    @NotBlank(message = "content 不能为空")
    private String content;

    /** 封面图。 */
    @Size(max = 255, message = "coverImage 长度不能超过 255")
    private String coverImage;

    /** 是否置顶。 */
    @Min(value = 0, message = "topFlag 仅支持 0/1")
    @Max(value = 1, message = "topFlag 仅支持 0/1")
    private Integer topFlag;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Integer getTopFlag() {
        return topFlag;
    }

    public void setTopFlag(Integer topFlag) {
        this.topFlag = topFlag;
    }
}
