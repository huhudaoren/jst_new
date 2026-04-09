package com.ruoyi.jst.points.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商城商品列表出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class GoodsListVO {

    private Long goodsId;

    private String goodsName;

    private String goodsType;

    private String coverImage;

    private String description;

    private Long pointsPrice;

    private BigDecimal cashPrice;

    private Integer stock;

    private Integer stockWarning;

    private String roleLimit;

    private String status;

    private String virtualTargetType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVirtualTargetType() {
        return virtualTargetType;
    }

    public void setVirtualTargetType(String virtualTargetType) {
        this.virtualTargetType = virtualTargetType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
