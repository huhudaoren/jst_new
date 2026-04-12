package com.ruoyi.jst.channel.task;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.channel.mapper.RebateLedgerMapperExt;
import com.ruoyi.jst.channel.service.IJstRebateLedgerService;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 返点售后期自动计提任务。
 * <p>
 * 关联表：jst_rebate_ledger、jst_order_main。<br/>
 * 关联状态机：SM-8（pending_accrual -> withdrawable）。
 *
 * @author jst
 * @since 1.0.0
 */
@Component("rebateAutoSettleTask")
public class RebateAutoSettleTask {

    private static final Logger log = LoggerFactory.getLogger(RebateAutoSettleTask.class);

    private static final String SETTLE_REMARK = "系统自动计提：售后期结束";

    @Autowired
    private RebateLedgerMapperExt rebateLedgerMapperExt;

    @Autowired
    private IJstRebateLedgerService rebateLedgerService;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Value("${jst.quartz.rebate-auto-settle-batch-size:200}")
    private int batchSize;

    /**
     * 扫描并计提满足售后期的返点台账。
     * <p>
     * 权限：系统任务（无需接口权限）。<br/>
     * 事务：单条计提逻辑由 IJstRebateLedgerService.settlePendingAccrual() 负责。
     */
    public void execute() {
        long start = System.currentTimeMillis();
        Counter counter = new Counter();
        log.info("[TASK] rebateAutoSettleTask 开始执行");
        try {
            // LOCK: lock:job:rebateAutoSettleTask
            jstLockTemplate.execute("lock:job:rebateAutoSettleTask", 0, 60, () -> {
                process(counter);
                return null;
            });
        } catch (ServiceException ex) {
            log.warn("[TASK] rebateAutoSettleTask 跳过执行，任务锁未获取: {}", ex.getMessage());
            return;
        } catch (Exception ex) {
            counter.fail++;
            log.error("[TASK] rebateAutoSettleTask 执行异常", ex);
        }
        long cost = System.currentTimeMillis() - start;
        log.info("[TASK] rebateAutoSettleTask 完成，total={} success={} fail={} skipped={} cost={}ms",
                counter.total, counter.success, counter.fail, counter.skipped, cost);
    }

    private void process(Counter counter) {
        Date deadline = DateUtils.getNowDate();
        Long lastLedgerId = null;
        int limit = safeBatchSize();
        while (true) {
            List<Long> ledgerIds = rebateLedgerMapperExt.selectAutoSettleLedgerIds(deadline, lastLedgerId, limit);
            if (ledgerIds == null || ledgerIds.isEmpty()) {
                break;
            }
            for (Long ledgerId : ledgerIds) {
                counter.total++;
                lastLedgerId = ledgerId;
                try {
                    boolean settled = rebateLedgerService.settlePendingAccrual(ledgerId, SETTLE_REMARK);
                    if (settled) {
                        counter.success++;
                    } else {
                        counter.skipped++;
                    }
                } catch (Exception ex) {
                    counter.fail++;
                    log.error("[TASK] rebateAutoSettleTask 处理台账失败 ledgerId={}", ledgerId, ex);
                }
            }
            if (ledgerIds.size() < limit) {
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
