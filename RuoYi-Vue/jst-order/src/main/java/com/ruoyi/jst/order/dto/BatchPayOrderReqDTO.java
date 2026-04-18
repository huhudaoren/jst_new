package com.ruoyi.jst.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 渠道方批量支付订单请求 DTO。
 * <p>
 * 对应端点：POST /jst/wx/channel/orders/batch-pay（Round 2B · B1）。
 * <p>
 * 任务卡约束：orderIds 非空 + 最多 50 条防刷。
 *
 * @author jst
 * @since 1.0.0
 */
public class BatchPayOrderReqDTO {

    /** 待支付订单 ID 列表（必须全部属于当前渠道方 + status=pending_pay） */
    @NotEmpty(message = "订单列表不能为空")
    @Size(min = 1, max = 50, message = "单次批量支付订单数量须在 1~50 之间")
    private List<Long> orderIds;

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }
}
