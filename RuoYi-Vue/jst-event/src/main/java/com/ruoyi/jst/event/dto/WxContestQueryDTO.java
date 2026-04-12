package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 小程序赛事列表查询 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class WxContestQueryDTO {

    @Pattern(regexp = "[a-z0-9_]*", message = "category 格式非法")
    private String category;

    @Pattern(regexp = "(default|hot|newest|deadline)?", message = "sortBy 仅支持 default/hot/newest/deadline")
    private String sortBy;

    @Pattern(regexp = "(online|offline|mixed)?", message = "eventType 仅支持 online/offline/mixed")
    private String eventType;

    @Size(max = 50, message = "tag 长度不能超过50")
    private String tag;

    private String groupLevel;

    @Size(max = 50, message = "keyword 长度不能超过50")
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

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
