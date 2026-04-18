package com.ruoyi.jst.channel.dto;

import com.ruoyi.jst.common.annotation.Sensitive;

import java.math.BigDecimal;
import java.util.List;

/**
 * 销售业绩聚合 VO。
 * <p>
 * money/rate 字段被 @Sensitive 标记 → 销售本人查询时由 MaskSalaryAspect 自动置 null。
 * 主管 / admin 查询返完整数值。
 */
public class SalesPerformanceVO {

    /** 订单笔数（销售可见） */
    private Integer orderCount;

    /** 覆盖渠道数（销售可见） */
    private Integer channelCount;

    @Sensitive(money = true)
    private BigDecimal totalGmv;

    @Sensitive(money = true)
    private BigDecimal totalCommission;

    /** 待入账 (pending + accrued) */
    @Sensitive(money = true)
    private BigDecimal pendingCommission;

    /** 已结算 (settled + paid) */
    @Sensitive(money = true)
    private BigDecimal settledCommission;

    /** 按业务类型分布 */
    private List<SalesPerformanceByTypeVO> byType;

    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer v) { this.orderCount = v; }
    public Integer getChannelCount() { return channelCount; }
    public void setChannelCount(Integer v) { this.channelCount = v; }
    public BigDecimal getTotalGmv() { return totalGmv; }
    public void setTotalGmv(BigDecimal v) { this.totalGmv = v; }
    public BigDecimal getTotalCommission() { return totalCommission; }
    public void setTotalCommission(BigDecimal v) { this.totalCommission = v; }
    public BigDecimal getPendingCommission() { return pendingCommission; }
    public void setPendingCommission(BigDecimal v) { this.pendingCommission = v; }
    public BigDecimal getSettledCommission() { return settledCommission; }
    public void setSettledCommission(BigDecimal v) { this.settledCommission = v; }
    public List<SalesPerformanceByTypeVO> getByType() { return byType; }
    public void setByType(List<SalesPerformanceByTypeVO> v) { this.byType = v; }

    /** 按业务类型分组行 */
    public static class SalesPerformanceByTypeVO {
        private String businessType;
        private Integer orderCount;

        @Sensitive(money = true)
        private BigDecimal gmv;

        @Sensitive(money = true)
        private BigDecimal commission;

        public String getBusinessType() { return businessType; }
        public void setBusinessType(String v) { this.businessType = v; }
        public Integer getOrderCount() { return orderCount; }
        public void setOrderCount(Integer v) { this.orderCount = v; }
        public BigDecimal getGmv() { return gmv; }
        public void setGmv(BigDecimal v) { this.gmv = v; }
        public BigDecimal getCommission() { return commission; }
        public void setCommission(BigDecimal v) { this.commission = v; }
    }
}
