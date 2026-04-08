package com.ruoyi.jst.event.vo;

/**
 * 赛事分类统计 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class CategoryStatVO {

    private String code;
    private String name;
    private Long count;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
