package com.ruoyi.jst.order.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 批量支付响应 VO。
 * <p>
 * 设计：本期采用"简化聚合"路径，不引入 jst_batch_pay_order 新表。
 * 每个 order 独立生成 wx prepay 参数，前端轮询逐个调起 wx.requestPayment。
 * batchPayNo 仅用于日志审计 / 业务聚合标识。
 *
 * @author jst
 * @since 1.0.0
 */
public class BatchPayResVO {

    /** 批量支付编号（审计用，格式：BP{12位uuid}{epochMillis}） */
    private String batchPayNo;

    /** 本批次总金额 */
    private BigDecimal totalAmount;

    /** 本批次订单数 */
    private Integer count;

    /** 明细列表（与 orderIds 一一对应） */
    private List<BatchPayItemVO> items;

    public String getBatchPayNo() {
        return batchPayNo;
    }

    public void setBatchPayNo(String batchPayNo) {
        this.batchPayNo = batchPayNo;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<BatchPayItemVO> getItems() {
        return items;
    }

    public void setItems(List<BatchPayItemVO> items) {
        this.items = items;
    }
}
