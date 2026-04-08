package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 小程序赛事列表查询 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class WxContestQueryDTO {

    private String category;

    private String groupLevel;

    private String keyword;

    @Min(value = 1, message = "pageNum 不能小于1")
    private Integer pageNum;

    @Min(value = 1, message = "pageSize 不能小于1")
    @Max(value = 50, message = "pageSize 不能大于50")
    private Integer pageSize;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGroupLevel() {
        return groupLevel;
    }

    public void setGroupLevel(String groupLevel) {
        this.groupLevel = groupLevel;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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
