package com.ruoyi.jst.message.vo;

/**
 * 首页 banner VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class BannerVO {

    /** 业务ID。 */
    private Long id;

    /** 业务类型。 */
    private String type;

    /** 标题。 */
    private String title;

    /** 图片地址。 */
    private String image;

    /** 跳转地址。 */
    private String linkUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}
