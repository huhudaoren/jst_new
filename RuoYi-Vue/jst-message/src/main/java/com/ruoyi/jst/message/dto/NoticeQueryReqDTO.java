package com.ruoyi.jst.message.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 管理后台公告列表查询请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class NoticeQueryReqDTO {

    /** 标题关键字。 */
    @Size(max = 255, message = "title 长度不能超过 255")
    private String title;

    /** 公告分类。 */
    @Pattern(regexp = "contest|platform|points|mall", message = "category 仅支持 contest/platform/points/mall")
    private String category;

    /** 状态。 */
    @Pattern(regexp = "draft|published|offline", message = "status 仅支持 draft/published/offline")
    private String status;

    /** 是否置顶。 */
    @Min(value = 0, message = "topFlag 仅支持 0/1")
    @Max(value = 1, message = "topFlag 仅支持 0/1")
    private Integer topFlag;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTopFlag() {
        return topFlag;
    }

    public void setTopFlag(Integer topFlag) {
        this.topFlag = topFlag;
    }
}
