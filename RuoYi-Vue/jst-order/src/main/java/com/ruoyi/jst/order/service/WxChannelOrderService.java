package com.ruoyi.jst.order.service;

import com.ruoyi.jst.order.dto.BatchPayOrderReqDTO;
import com.ruoyi.jst.order.vo.BatchPayResVO;

/**
 * 小程序-渠道方代支付领域服务。
 * <p>
 * 对应端点：POST /jst/wx/channel/orders/batch-pay（Round 2B · B1）。
 * 关联状态机：SM-1（订单主状态机，订单保持 pending_pay，不发生流转）。
 *
 * @author jst
 * @since 1.0.0
 */
public interface WxChannelOrderService {

    /**
     * 批量支付渠道方名下的多笔 pending_pay 订单。
     * <p>
     * 约束：
     * <ul>
     *   <li>所有 orderId 必须属于当前登录渠道方 + status=pending_pay</li>
     *   <li>任一订单校验失败整批拒绝（要么全过要么全拒，避免前端半半拒）</li>
     *   <li>零元订单不支持批量支付（需单独走 mock-success / 退款流程）</li>
     *   <li>不会变更订单状态（前端 wx.requestPayment 成功后走既有 /jst/wx/pay/notify 回调）</li>
     * </ul>
     *
     * @param req 订单 ID 列表
     * @return 聚合响应（batchPayNo + totalAmount + items[]）
     * @关联表 jst_order_main
     * @关联状态机 SM-1
     * @关联权限 hasRole('jst_channel')
     */
    BatchPayResVO batchPay(BatchPayOrderReqDTO req);
}
