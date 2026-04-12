package com.ruoyi.jst.order.task;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.order.mapper.RefundRecordMapperExt;
import com.ruoyi.jst.order.service.RefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 退款超时自动关闭任务。
 * <p>
 * 关联表：jst_refund_record。<br/>
 * 关联状态机：SM-2（pending -> closed）。
 *
 * @author jst
 * @since 1.0.0
 */
@Component("refundTimeoutCloseTask")
public class RefundTimeoutCloseTask {

    private static final Logger log = LoggerFactory.getLogger(RefundTimeoutCloseTask.class);

    private static final String CLOSE_REMARK = "系统自动关闭：超时未审核";

    @Autowired
    private RefundRecordMapperExt refundRecordMapperExt;

    @Autowired
    private RefundService refundService;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Value("${jst.quartz.refund-timeout-close-days:7}")
    private int timeoutDays;

    @Value("${jst.quartz.refund-timeout-close-batch-size:200}")
    private int batchSize;

    /**
     * 扫描并关闭超时待审核退款单。
     * <p>
     * 权限：系统任务（无需接口权限）。<br/>
     * 事务：单条关闭逻辑由 RefundService.closeTimeoutPending() 负责。
     */
    public void execute() {
        long start = System.currentTimeMillis();
        Counter counter = new Counter();
        log.info("[TASK] refundTimeoutCloseTask 开始执行");
        try {
            // LOCK: lock:job:refundTimeoutCloseTask
            jstLockTemplate.execute("lock:job:refundTimeoutCloseTask", 0, 60, () -> {
                process(counter);
                return null;
            });
        } catch (ServiceException ex) {
            log.warn("[TASK] refundTimeoutCloseTask 跳过执行，任务锁未获取: {}", ex.getMessage());
            return;
        } catch (Exception ex) {
            counter.fail++;
            log.error("[TASK] refundTimeoutCloseTask 执行异常", ex);
        }
        long cost = System.currentTimeMillis() - start;
        log.info("[TASK] refundTimeoutCloseTask 完成，total={} success={} fail={} skipped={} cost={}ms",
                counter.total, counter.success, counter.fail, counter.skipped, cost);
    }

    private void process(Counter counter) {
        Date deadline = DateUtils.addDays(DateUtils.getNowDate(), -Math.max(timeoutDays, 1));
        Long lastRefundId = null;
        int limit = safeBatchSize();
        while (true) {
            List<Long> refundIds = refundRecordMapperExt.selectTimeoutPendingRefundIds(deadline, lastRefundId, limit);
            if (refundIds == null || refundIds.isEmpty()) {
                break;
            }
            for (Long refundId : refundIds) {
                counter.total++;
                lastRefundId = refundId;
                try {
                    boolean closed = refundService.closeTimeoutPending(refundId, CLOSE_REMARK);
                    if (closed) {
                        counter.success++;
                    } else {
                        counter.skipped++;
                    }
                } catch (Exception ex) {
                    counter.fail++;
                    log.error("[TASK] refundTimeoutCloseTask 处理退款失败 refundId={}", refundId, ex);
                }
            }
            if (refundIds.size() < limit) {
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
        private int skipped;
    }
}
