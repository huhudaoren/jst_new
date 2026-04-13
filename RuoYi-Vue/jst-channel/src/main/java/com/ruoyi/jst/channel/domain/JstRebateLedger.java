package com.ruoyi.jst.channel.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 返点计提台账对象 jst_rebate_ledger
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstRebateLedger extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 台账ID */
    private Long ledgerId;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 订单明细ID */
    @Excel(name = "订单明细ID")
    private Long itemId;

    /** 渠道方ID */
    @Excel(name = "渠道方ID")
    private Long channelId;

    /** 赛事ID */
    @Excel(name = "赛事ID")
    private Long contestId;

    /** 命中规则ID */
    @Excel(name = "命中规则ID")
    private Long ruleId;

    /** 标价金额 */
    @Excel(name = "标价金额")
    private BigDecimal listAmount;

    /** 净实付金额 */
    @Excel(name = "净实付金额")
    private BigDecimal netPayAmount;

    /** 服务费 */
    @Excel(name = "服务费")
    private BigDecimal serviceFee;

    /** 返点基数 = max(0, 标价 - 服务费) */
    @Excel(name = "返点基数 = max(0, 标价 - 服务费)")
    private BigDecimal rebateBase;

    /** 返点金额（可负） */
    @Excel(name = "返点金额", readConverterExp = "可=负")
    private BigDecimal rebateAmount;

    /** 方向：positive正向 / negative负向 */
    @Excel(name = "方向：positive正向 / negative负向")
    private String direction;

    /** 台账状态：pending_accrual/withdrawable/in_review/paid/rolled_back/negative */
    @Excel(name = "台账状态：pending_accrual/withdrawable/in_review/paid/rolled_back/negative")
    private String status;

    /** 计提时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计提时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date accrualTime;

    /** 赛事结束时间快照 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "赛事结束时间快照", width = 30, dateFormat = "yyyy-MM-dd")
    private Date eventEndTime;

    /** 关联结算单ID */
    @Excel(name = "关联结算单ID")
    private Long settlementId;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    /** 渠道名称（JOIN查出，非持久化） */
    private String channelName;

    /** 订单编号（JOIN查出，非持久化） */
    private String orderNo;

    public void setLedgerId(Long ledgerId) 
    {
        this.ledgerId = ledgerId;
    }

    public Long getLedgerId() 
    {
        return ledgerId;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setItemId(Long itemId) 
    {
        this.itemId = itemId;
    }

    public Long getItemId() 
    {
        return itemId;
    }

    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }

    public void setContestId(Long contestId) 
    {
        this.contestId = contestId;
    }

    public Long getContestId() 
    {
        return contestId;
    }

    public void setRuleId(Long ruleId) 
    {
        this.ruleId = ruleId;
    }

    public Long getRuleId() 
    {
        return ruleId;
    }

    public void setListAmount(BigDecimal listAmount) 
    {
        this.listAmount = listAmount;
    }

    public BigDecimal getListAmount() 
    {
        return listAmount;
    }

    public void setNetPayAmount(BigDecimal netPayAmount) 
    {
        this.netPayAmount = netPayAmount;
    }

    public BigDecimal getNetPayAmount() 
    {
        return netPayAmount;
    }

    public void setServiceFee(BigDecimal serviceFee) 
    {
        this.serviceFee = serviceFee;
    }

    public BigDecimal getServiceFee() 
    {
        return serviceFee;
    }

    public void setRebateBase(BigDecimal rebateBase) 
    {
        this.rebateBase = rebateBase;
    }

    public BigDecimal getRebateBase() 
    {
        return rebateBase;
    }

    public void setRebateAmount(BigDecimal rebateAmount) 
    {
        this.rebateAmount = rebateAmount;
    }

    public BigDecimal getRebateAmount() 
    {
        return rebateAmount;
    }

    public void setDirection(String direction) 
    {
        this.direction = direction;
    }

    public String getDirection() 
    {
        return direction;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setAccrualTime(Date accrualTime) 
    {
        this.accrualTime = accrualTime;
    }

    public Date getAccrualTime() 
    {
        return accrualTime;
    }

    public void setEventEndTime(Date eventEndTime) 
    {
        this.eventEndTime = eventEndTime;
    }

    public Date getEventEndTime() 
    {
        return eventEndTime;
    }

    public void setSettlementId(Long settlementId) 
    {
        this.settlementId = settlementId;
    }

    public Long getSettlementId() 
    {
        return settlementId;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public void setChannelName(String channelName)
    {
        this.channelName = channelName;
    }

    public String getChannelName()
    {
        return channelName;
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
            .append("ledgerId", getLedgerId())
            .append("orderId", getOrderId())
            .append("itemId", getItemId())
            .append("channelId", getChannelId())
            .append("contestId", getContestId())
            .append("ruleId", getRuleId())
            .append("listAmount", getListAmount())
            .append("netPayAmount", getNetPayAmount())
            .append("serviceFee", getServiceFee())
            .append("rebateBase", getRebateBase())
            .append("rebateAmount", getRebateAmount())
            .append("direction", getDirection())
            .append("status", getStatus())
            .append("accrualTime", getAccrualTime())
            .append("eventEndTime", getEventEndTime())
            .append("settlementId", getSettlementId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
