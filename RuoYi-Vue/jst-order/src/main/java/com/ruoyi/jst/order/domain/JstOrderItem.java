package com.ruoyi.jst.order.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单明细（最小核算单元，承载分摊与回滚）对象 jst_order_item
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstOrderItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long itemId;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 类型：enroll/appointment_member/goods/course */
    @Excel(name = "类型：enroll/appointment_member/goods/course")
    private String skuType;

    /** 引用业务ID（报名/成员/商品/课程） */
    @Excel(name = "引用业务ID", readConverterExp = "报=名/成员/商品/课程")
    private Long refId;

    /** 商品/项目名称 */
    @Excel(name = "商品/项目名称")
    private String itemName;

    /** 数量 */
    @Excel(name = "数量")
    private Long quantity;

    /** 标价金额 */
    @Excel(name = "标价金额")
    private BigDecimal listAmount;

    /** 优惠券分摊金额 */
    @Excel(name = "优惠券分摊金额")
    private BigDecimal couponShare;

    /** 积分抵扣分摊金额 */
    @Excel(name = "积分抵扣分摊金额")
    private BigDecimal pointsShare;

    /** 净实付分摊金额 */
    @Excel(name = "净实付分摊金额")
    private BigDecimal netPayShare;

    /** 服务费分摊 */
    @Excel(name = "服务费分摊")
    private BigDecimal serviceFeeShare;

    /** 渠道返点分摊（计提阶段写入） */
    @Excel(name = "渠道返点分摊", readConverterExp = "计=提阶段写入")
    private BigDecimal rebateShare;

    /** 已退现金累计 */
    @Excel(name = "已退现金累计")
    private BigDecimal refundAmount;

    /** 已回退积分累计 */
    @Excel(name = "已回退积分累计")
    private Long refundPoints;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    /** 订单编号（JOIN查出，非持久化） */
    private String orderNo;

    public void setItemId(Long itemId) 
    {
        this.itemId = itemId;
    }

    public Long getItemId() 
    {
        return itemId;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setSkuType(String skuType) 
    {
        this.skuType = skuType;
    }

    public String getSkuType() 
    {
        return skuType;
    }

    public void setRefId(Long refId) 
    {
        this.refId = refId;
    }

    public Long getRefId() 
    {
        return refId;
    }

    public void setItemName(String itemName) 
    {
        this.itemName = itemName;
    }

    public String getItemName() 
    {
        return itemName;
    }

    public void setQuantity(Long quantity) 
    {
        this.quantity = quantity;
    }

    public Long getQuantity() 
    {
        return quantity;
    }

    public void setListAmount(BigDecimal listAmount) 
    {
        this.listAmount = listAmount;
    }

    public BigDecimal getListAmount() 
    {
        return listAmount;
    }

    public void setCouponShare(BigDecimal couponShare) 
    {
        this.couponShare = couponShare;
    }

    public BigDecimal getCouponShare() 
    {
        return couponShare;
    }

    public void setPointsShare(BigDecimal pointsShare) 
    {
        this.pointsShare = pointsShare;
    }

    public BigDecimal getPointsShare() 
    {
        return pointsShare;
    }

    public void setNetPayShare(BigDecimal netPayShare) 
    {
        this.netPayShare = netPayShare;
    }

    public BigDecimal getNetPayShare() 
    {
        return netPayShare;
    }

    public void setServiceFeeShare(BigDecimal serviceFeeShare) 
    {
        this.serviceFeeShare = serviceFeeShare;
    }

    public BigDecimal getServiceFeeShare() 
    {
        return serviceFeeShare;
    }

    public void setRebateShare(BigDecimal rebateShare) 
    {
        this.rebateShare = rebateShare;
    }

    public BigDecimal getRebateShare() 
    {
        return rebateShare;
    }

    public void setRefundAmount(BigDecimal refundAmount) 
    {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getRefundAmount() 
    {
        return refundAmount;
    }

    public void setRefundPoints(Long refundPoints) 
    {
        this.refundPoints = refundPoints;
    }

    public Long getRefundPoints() 
    {
        return refundPoints;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("itemId", getItemId())
            .append("orderId", getOrderId())
            .append("skuType", getSkuType())
            .append("refId", getRefId())
            .append("itemName", getItemName())
            .append("quantity", getQuantity())
            .append("listAmount", getListAmount())
            .append("couponShare", getCouponShare())
            .append("pointsShare", getPointsShare())
            .append("netPayShare", getNetPayShare())
            .append("serviceFeeShare", getServiceFeeShare())
            .append("rebateShare", getRebateShare())
            .append("refundAmount", getRefundAmount())
            .append("refundPoints", getRefundPoints())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
