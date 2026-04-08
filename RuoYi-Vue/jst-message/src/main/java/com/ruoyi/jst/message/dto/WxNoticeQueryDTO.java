package com.ruoyi.jst.message.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * 小程序公告列表查询请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class WxNoticeQueryDTO {

    /** 公告分类。 */
    @Pattern(regexp = "contest|platform|points|mall", message = "category 仅支持 contest/platform/points/mall")
    private String category;

    /** 页码。 */
    @Min(value = 1, message = "pageNum 不能小于 1")
    private Integer pageNum;

    /** 每页条数。 */
    @Min(value = 1, message = "pageSize 不能小于 1")
    private Integer pageSize;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
