package com.ruoyi.jst.points.vo;

/**
 * 商城商品详情出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class GoodsDetailVO extends GoodsListVO {

    private Long couponTemplateId;

    private Long rightsTemplateId;

    public Long getCouponTemplateId() {
        return couponTemplateId;
    }

    public void setCouponTemplateId(Long couponTemplateId) {
        this.couponTemplateId = couponTemplateId;
    }

    public Long getRightsTemplateId() {
        return rightsTemplateId;
    }

    public void setRightsTemplateId(Long rightsTemplateId) {
        this.rightsTemplateId = rightsTemplateId;
    }
}
