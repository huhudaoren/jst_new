package com.ruoyi.jst.points.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 积分商城商品对象 jst_mall_goods
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstMallGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long goodsId;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String goodsName;

    /** 类型：virtual虚拟 / physical实物 */
    @Excel(name = "类型：virtual虚拟 / physical实物")
    private String goodsType;

    /** 主图 */
    @Excel(name = "主图")
    private String coverImage;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 所需积分 */
    @Excel(name = "所需积分")
    private Long pointsPrice;

    /** 现金补差金额 */
    @Excel(name = "现金补差金额")
    private BigDecimal cashPrice;

    /** 库存 */
    @Excel(name = "库存")
    private Long stock;

    /** 预警阈值 */
    @Excel(name = "预警阈值")
    private Long stockWarning;

    /** 角色限制：student/channel/both */
    @Excel(name = "角色限制：student/channel/both")
    private String roleLimit;

    /** 状态：on/off/draft */
    @Excel(name = "状态：on/off/draft")
    private String status;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setGoodsId(Long goodsId) 
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId() 
    {
        return goodsId;
    }

    public void setGoodsName(String goodsName) 
    {
        this.goodsName = goodsName;
    }

    public String getGoodsName() 
    {
        return goodsName;
    }

    public void setGoodsType(String goodsType) 
    {
        this.goodsType = goodsType;
    }

    public String getGoodsType() 
    {
        return goodsType;
    }

    public void setCoverImage(String coverImage) 
    {
        this.coverImage = coverImage;
    }

    public String getCoverImage() 
    {
        return coverImage;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setPointsPrice(Long pointsPrice) 
    {
        this.pointsPrice = pointsPrice;
    }

    public Long getPointsPrice() 
    {
        return pointsPrice;
    }

    public void setCashPrice(BigDecimal cashPrice) 
    {
        this.cashPrice = cashPrice;
    }

    public BigDecimal getCashPrice() 
    {
        return cashPrice;
    }

    public void setStock(Long stock) 
    {
        this.stock = stock;
    }

    public Long getStock() 
    {
        return stock;
    }

    public void setStockWarning(Long stockWarning) 
    {
        this.stockWarning = stockWarning;
    }

    public Long getStockWarning() 
    {
        return stockWarning;
    }

    public void setRoleLimit(String roleLimit) 
    {
        this.roleLimit = roleLimit;
    }

    public String getRoleLimit() 
    {
        return roleLimit;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("goodsId", getGoodsId())
            .append("goodsName", getGoodsName())
            .append("goodsType", getGoodsType())
            .append("coverImage", getCoverImage())
            .append("description", getDescription())
            .append("pointsPrice", getPointsPrice())
            .append("cashPrice", getCashPrice())
            .append("stock", getStock())
            .append("stockWarning", getStockWarning())
            .append("roleLimit", getRoleLimit())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
