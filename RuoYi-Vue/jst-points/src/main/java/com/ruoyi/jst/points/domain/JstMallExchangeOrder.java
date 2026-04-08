package com.ruoyi.jst.points.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 积分商城兑换订单对象 jst_mall_exchange_order
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstMallExchangeOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 兑换订单ID */
    private Long exchangeId;

    /** 单号 */
    @Excel(name = "单号")
    private String exchangeNo;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long goodsId;

    /** 数量 */
    @Excel(name = "数量")
    private Long quantity;

    /** 消耗积分 */
    @Excel(name = "消耗积分")
    private Long pointsUsed;

    /** 现金补差金额 */
    @Excel(name = "现金补差金额")
    private BigDecimal cashAmount;

    /** 关联现金支付订单ID */
    @Excel(name = "关联现金支付订单ID")
    private Long orderId;

    /** 收货地址快照JSON */
    @Excel(name = "收货地址快照JSON")
    private String addressSnapshotJson;

    /** 订单状态：pending_pay/paid/pending_ship/shipped/completed/aftersale/closed */
    @Excel(name = "订单状态：pending_pay/paid/pending_ship/shipped/completed/aftersale/closed")
    private String status;

    /** 物流公司 */
    @Excel(name = "物流公司")
    private String logisticsCompany;

    /** 物流单号 */
    @Excel(name = "物流单号")
    private String logisticsNo;

    /** 发货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发货时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date shipTime;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date completeTime;

    /** 售后状态：none/applying/refunding/refunded */
    @Excel(name = "售后状态：none/applying/refunding/refunded")
    private String aftersaleStatus;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setExchangeId(Long exchangeId) 
    {
        this.exchangeId = exchangeId;
    }

    public Long getExchangeId() 
    {
        return exchangeId;
    }

    public void setExchangeNo(String exchangeNo) 
    {
        this.exchangeNo = exchangeNo;
    }

    public String getExchangeNo() 
    {
        return exchangeNo;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setGoodsId(Long goodsId) 
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId() 
    {
        return goodsId;
    }

    public void setQuantity(Long quantity) 
    {
        this.quantity = quantity;
    }

    public Long getQuantity() 
    {
        return quantity;
    }

    public void setPointsUsed(Long pointsUsed) 
    {
        this.pointsUsed = pointsUsed;
    }

    public Long getPointsUsed() 
    {
        return pointsUsed;
    }

    public void setCashAmount(BigDecimal cashAmount) 
    {
        this.cashAmount = cashAmount;
    }

    public BigDecimal getCashAmount() 
    {
        return cashAmount;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setAddressSnapshotJson(String addressSnapshotJson) 
    {
        this.addressSnapshotJson = addressSnapshotJson;
    }

    public String getAddressSnapshotJson() 
    {
        return addressSnapshotJson;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setLogisticsCompany(String logisticsCompany) 
    {
        this.logisticsCompany = logisticsCompany;
    }

    public String getLogisticsCompany() 
    {
        return logisticsCompany;
    }

    public void setLogisticsNo(String logisticsNo) 
    {
        this.logisticsNo = logisticsNo;
    }

    public String getLogisticsNo() 
    {
        return logisticsNo;
    }

    public void setShipTime(Date shipTime) 
    {
        this.shipTime = shipTime;
    }

    public Date getShipTime() 
    {
        return shipTime;
    }

    public void setCompleteTime(Date completeTime) 
    {
        this.completeTime = completeTime;
    }

    public Date getCompleteTime() 
    {
        return completeTime;
    }

    public void setAftersaleStatus(String aftersaleStatus) 
    {
        this.aftersaleStatus = aftersaleStatus;
    }

    public String getAftersaleStatus() 
    {
        return aftersaleStatus;
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
            .append("exchangeId", getExchangeId())
            .append("exchangeNo", getExchangeNo())
            .append("userId", getUserId())
            .append("goodsId", getGoodsId())
            .append("quantity", getQuantity())
            .append("pointsUsed", getPointsUsed())
            .append("cashAmount", getCashAmount())
            .append("orderId", getOrderId())
            .append("addressSnapshotJson", getAddressSnapshotJson())
            .append("status", getStatus())
            .append("logisticsCompany", getLogisticsCompany())
            .append("logisticsNo", getLogisticsNo())
            .append("shipTime", getShipTime())
            .append("completeTime", getCompleteTime())
            .append("aftersaleStatus", getAftersaleStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
