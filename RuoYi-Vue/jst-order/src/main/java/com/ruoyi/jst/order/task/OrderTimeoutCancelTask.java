package com.ruoyi.jst.order.task;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.order.mapper.OrderMainMapperExt;
import com.ruoyi.jst.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 订单超时自动取消任务。
 * <p>
 * 关联表：jst_order_main。<br/>
 * 关联状态机：SM-1（pending_pay -> cancelled）。
 *
 * @author jst
 * @since 1.0.0
 */
@Component("orderTimeoutCancelTask")
public class OrderTimeoutCancelTask {

    private static final Logger log = LoggerFactory.getLogger(OrderTimeoutCancelTask.class);

    private static final String CANCEL_REASON = "系统自动取消：超时未支付";

    @Autowired
    private OrderMainMapperExt orderMainMapperExt;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Value("${jst.quartz.order-timeout-cancel-minutes:30}")
    private int timeoutMinutes;

    @Value("${jst.quartz.order-timeout-cancel-batch-size:200}")
    private int batchSize;

    /**
     * 扫描并取消超时未支付订单。
     * <p>
     * 权限：系统任务（无需接口权限）。<br/>
     * 事务：单条取消逻辑由 OrderService.cancelTimeoutOrder() 负责。
     */
    public void execute() {
        long start = System.currentTimeMillis();
        Counter counter = new Counter();
        log.info("[TASK] orderTimeoutCancelTask 开始执行");
        try {
            // LOCK: lock:job:orderTimeoutCancelTask
            jstLockTemplate.execute("lock:job:orderTimeoutCancelTask", 0, 60, () -> {
                process(counter);
                return null;
            });
        } catch (ServiceException ex) {
            log.warn("[TASK] orderTimeoutCancelTask 跳过执行，任务锁未获取: {}", ex.getMessage());
            return;
        } catch (Exception ex) {
            counter.fail++;
            log.error("[TASK] orderTimeoutCancelTask 执行异常", ex);
        }
        long cost = System.currentTimeMillis() - start;
        log.info("[TASK] orderTimeoutCancelTask 完成，total={} success={} fail={} cost={}ms",
                counter.total, counter.success, counter.fail, cost);
    }

    private void process(Counter counter) {
        Date deadline = DateUtils.addMinutes(DateUtils.getNowDate(), -Math.max(timeoutMinutes, 1));
        Long lastOrderId = null;
        int limit = safeBatchSize();
        while (true) {
            List<Long> orderIds = orderMainMapperExt.selectTimeoutPendingPayOrderIds(deadline, lastOrderId, limit);
            if (orderIds == null || orderIds.isEmpty()) {
                break;
            }
            for (Long orderId : orderIds) {
                counter.total++;
                lastOrderId = orderId;
                try {
                    orderService.cancelTimeoutOrder(orderId, CANCEL_REASON);
                    counter.success++;
                } catch (Exception ex) {
                    counter.fail++;
                    log.error("[TASK] orderTimeoutCancelTask 处理订单失败 orderId={}", orderId, ex);
                }
            }
            if (orderIds.size() < limit) {
                break;
            }
        }
    }

    private int safeBatchSize() {
        if (batchSize <= 0) {
            return 200;
        }
        return Math.min(batchSize, 1000);
    }

    private static class Counter {
        private int total;
        private int success;
        private int fail;
    }
}
