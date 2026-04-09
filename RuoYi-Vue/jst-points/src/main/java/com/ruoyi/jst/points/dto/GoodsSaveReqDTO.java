package com.ruoyi.jst.points.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 商城商品保存请求。
 *
 * @author jst
 * @since 1.0.0
 */
public class GoodsSaveReqDTO {

    private Long goodsId;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 128, message = "商品名称长度不能超过128")
    private String goodsName;

    @NotBlank(message = "商品类型不能为空")
    private String goodsType;

    @Size(max = 255, message = "主图长度不能超过255")
    private String coverImage;

    @Size(max = 2000, message = "描述长度不能超过2000")
    private String description;

    @NotNull(message = "积分价格不能为空")
    @Min(value = 0, message = "积分价格不能小于0")
    private Long pointsPrice;

    @NotNull(message = "现金补差不能为空")
    @DecimalMin(value = "0.00", message = "现金补差不能小于0")
    private BigDecimal cashPrice;

    @NotNull(message = "库存不能为空")
    private Integer stock;

    @Min(value = 0, message = "库存预警不能小于0")
    private Integer stockWarning;

    @NotBlank(message = "角色限制不能为空")
    private String roleLimit;

    private String virtualTargetType;

    private Long couponTemplateId;

    private Long rightsTemplateId;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPointsPrice() {
        return pointsPrice;
    }

    public void setPointsPrice(Long pointsPrice) {
        this.pointsPrice = pointsPrice;
    }

    public BigDecimal getCashPrice() {
        return cashPrice;
    }

    public void setCashPrice(BigDecimal cashPrice) {
        this.cashPrice = cashPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockWarning() {
        return stockWarning;
    }

    public void setStockWarning(Integer stockWarning) {
        this.stockWarning = stockWarning;
    }

    public String getRoleLimit() {
        return roleLimit;
    }

    public void setRoleLimit(String roleLimit) {
        this.roleLimit = roleLimit;
    }

    public String getVirtualTargetType() {
        return virtualTargetType;
    }

    public void setVirtualTargetType(String virtualTargetType) {
        this.virtualTargetType = virtualTargetType;
    }

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
