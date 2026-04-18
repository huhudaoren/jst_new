package com.ruoyi.jst.order.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.order.domain.JstOrderMain;
import com.ruoyi.jst.order.dto.BatchPayOrderReqDTO;
import com.ruoyi.jst.order.enums.OrderStatus;
import com.ruoyi.jst.order.mapper.JstOrderMainMapper;
import com.ruoyi.jst.order.pay.WxPayService;
import com.ruoyi.jst.order.service.WxChannelOrderService;
import com.ruoyi.jst.order.vo.BatchPayItemVO;
import com.ruoyi.jst.order.vo.BatchPayResVO;
import com.ruoyi.jst.order.vo.WechatPrepayVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 小程序-渠道方代支付领域服务实现。
 * <p>
 * Round 2B · B1 · 采用"简化聚合"方案：每个订单独立调 WxPayService.unifiedOrder，
 * 聚合结果返回。不建 jst_batch_pay_order 表，batchPayNo 仅作审计标记。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class WxChannelOrderServiceImpl implements WxChannelOrderService {

    private static final Logger log = LoggerFactory.getLogger(WxChannelOrderServiceImpl.class);

    @Autowired
    private JstOrderMainMapper jstOrderMainMapper;

    @Autowired
    private WxPayService wxPayService;

    @Override
    // TX: @Transactional 保证全部预下单失败整体回滚（当前实现无库表写操作，
    // 但 WxPayService.unifiedOrder 为真实现时会触发第三方调用，仍保留事务屏障）
    @Transactional(rollbackFor = Exception.class)
    public BatchPayResVO batchPay(BatchPayOrderReqDTO req) {
        Long channelId = JstLoginContext.currentChannelId();
        if (channelId == null) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_NOT_AUTHED.message(),
                    BizErrorCode.JST_CHANNEL_NOT_AUTHED.code());
        }

        List<Long> orderIds = req.getOrderIds();
        // 去重（前端或手误可能重复传）
        List<Long> uniqueIds = new ArrayList<>(new java.util.LinkedHashSet<>(orderIds));

        // 1. 逐个查订单 + 校验（整批校验，任一失败整批拒绝）
        List<JstOrderMain> orders = new ArrayList<>(uniqueIds.size());
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Long orderId : uniqueIds) {
            JstOrderMain order = jstOrderMainMapper.selectJstOrderMainByOrderId(orderId);
            if (order == null) {
                throw new ServiceException("存在不可支付订单：订单不存在 orderId=" + orderId,
                        BizErrorCode.JST_ORDER_NOT_FOUND.code());
            }
            // 校验 A：必须属于当前渠道方
            if (order.getChannelId() == null || !channelId.equals(order.getChannelId())) {
                throw new ServiceException("存在不可支付订单：订单不属于当前渠道方 orderId=" + orderId,
                        BizErrorCode.JST_COMMON_AUTH_DENIED.code());
            }
            // 校验 B：必须 pending_pay
            if (!OrderStatus.PENDING_PAY.dbValue().equals(order.getOrderStatus())) {
                throw new ServiceException("存在不可支付订单：订单状态非 pending_pay orderId=" + orderId
                        + " status=" + order.getOrderStatus(),
                        BizErrorCode.JST_ORDER_ILLEGAL_TRANSIT.code());
            }
            // 校验 C：零元订单不能批量支付
            BigDecimal net = order.getNetPayAmount() == null ? BigDecimal.ZERO : order.getNetPayAmount();
            if (net.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("存在不可支付订单：零元/全额抵扣订单不支持批量支付 orderId=" + orderId,
                        BizErrorCode.JST_ORDER_PAY_METHOD_INVALID.code());
            }
            orders.add(order);
            totalAmount = totalAmount.add(net);
        }

        // 2. 生成 batchPayNo（审计标记）
        String batchPayNo = "BP" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase()
                + System.currentTimeMillis();
        log.info("[batch-pay] channelId={} batchPayNo={} orderCount={} totalAmount={}",
                channelId, batchPayNo, orders.size(), totalAmount);

        // 3. 每单独立调起 prepay（简化方案：循环调 wxPayService.unifiedOrder）
        //    真实微信支付场景如需合单，后续可接入 combinePay 接口，本次留 TODO。
        // TODO(plan-B): 如需真合单支付（combinePay），需：
        //   a) 建 jst_batch_pay_order 主表 + 批次号
        //   b) WxPayService 扩展 combinePay(List<orderId>) 接口
        //   c) 回调分发各子单 paid 状态
        List<BatchPayItemVO> items = new ArrayList<>(orders.size());
        for (JstOrderMain order : orders) {
            WechatPrepayVO prepay = wxPayService.unifiedOrder(order);
            BatchPayItemVO item = new BatchPayItemVO();
            item.setOrderId(order.getOrderId());
            item.setOrderNo(order.getOrderNo());
            item.setPayAmount(order.getNetPayAmount());
            item.setMerchantPayParams(prepay);
            items.add(item);
        }

        BatchPayResVO res = new BatchPayResVO();
        res.setBatchPayNo(batchPayNo);
        res.setTotalAmount(totalAmount);
        res.setCount(items.size());
        res.setItems(items);
        return res;
    }
}
