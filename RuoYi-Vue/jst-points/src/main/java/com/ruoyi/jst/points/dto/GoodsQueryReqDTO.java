package com.ruoyi.jst.points.dto;

/**
 * 商城商品查询条件。
 *
 * @author jst
 * @since 1.0.0
 */
public class GoodsQueryReqDTO {

    private String goodsName;

    private String goodsType;

    private String status;

    private String roleLimit;

    private String category;

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoleLimit() {
        return roleLimit;
    }

    public void setRoleLimit(String roleLimit) {
        this.roleLimit = roleLimit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
