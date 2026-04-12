package com.ruoyi.jst.order.service;

import com.ruoyi.jst.order.dto.CreateOrderReqDTO;
import com.ruoyi.jst.order.dto.OrderQueryReqDTO;
import com.ruoyi.jst.order.vo.CreateOrderResVO;
import com.ruoyi.jst.order.vo.OrderDetailVO;
import com.ruoyi.jst.order.vo.OrderListVO;

import java.util.List;
import java.util.Map;

/**
 * 订单领域服务。
 * <p>
 * 关联状态机：SM-1 / SM-8 / SM-16。
 *
 * @author jst
 * @since 1.0.0
 */
public interface OrderService {

    CreateOrderResVO createMixedPay(CreateOrderReqDTO req);

    void mockPaySuccess(Long orderId);

    String handleWechatNotify(String body, Map<String, String> headers);

    void cancel(Long orderId);

    /**
     * 系统任务触发超时取消。
     *
     * @param orderId 订单ID
     * @param reason  取消原因（用于日志审计）
     */
    void cancelTimeoutOrder(Long orderId, String reason);

    OrderDetailVO getWxDetail(Long orderId);

    List<OrderListVO> selectMyList(OrderQueryReqDTO query);

    List<OrderListVO> selectAdminList(OrderQueryReqDTO query);

    OrderDetailVO getAdminDetail(Long orderId);
}
