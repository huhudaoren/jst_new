package com.ruoyi.jst.event.task;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.event.mapper.AppointmentRecordMapperExt;
import com.ruoyi.jst.event.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 预约过期自动取消任务。
 * <p>
 * 关联表：jst_appointment_record。<br/>
 * 关联状态机：SM-11（booked -> expired）。
 *
 * @author jst
 * @since 1.0.0
 */
@Component("appointmentExpireTask")
public class AppointmentExpireTask {

    private static final Logger log = LoggerFactory.getLogger(AppointmentExpireTask.class);

    @Autowired
    private AppointmentRecordMapperExt appointmentRecordMapperExt;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Value("${jst.quartz.appointment-expire-batch-size:200}")
    private int batchSize;

    /**
     * 扫描并过期未核销预约。
     * <p>
     * 权限：系统任务（无需接口权限）。<br/>
     * 事务：单条过期逻辑由 AppointmentService.expireBySystem() 负责。
     */
    public void execute() {
        long start = System.currentTimeMillis();
        Counter counter = new Counter();
        log.info("[TASK] appointmentExpireTask 开始执行");
        try {
            // LOCK: lock:job:appointmentExpireTask
            jstLockTemplate.execute("lock:job:appointmentExpireTask", 0, 60, () -> {
                process(counter);
                return null;
            });
        } catch (ServiceException ex) {
            log.warn("[TASK] appointmentExpireTask 跳过执行，任务锁未获取: {}", ex.getMessage());
            return;
        } catch (Exception ex) {
            counter.fail++;
            log.error("[TASK] appointmentExpireTask 执行异常", ex);
        }
        long cost = System.currentTimeMillis() - start;
        log.info("[TASK] appointmentExpireTask 完成，total={} success={} fail={} skipped={} cost={}ms",
                counter.total, counter.success, counter.fail, counter.skipped, cost);
    }

    private void process(Counter counter) {
        Date cutoffDate = DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd", DateUtils.getNowDate()));
        Long lastAppointmentId = null;
        int limit = safeBatchSize();
        while (true) {
            List<Long> appointmentIds = appointmentRecordMapperExt.selectExpiredBookedAppointmentIds(
                    cutoffDate, lastAppointmentId, limit);
            if (appointmentIds == null || appointmentIds.isEmpty()) {
                break;
            }
            for (Long appointmentId : appointmentIds) {
                counter.total++;
                lastAppointmentId = appointmentId;
                try {
                    boolean expired = appointmentService.expireBySystem(appointmentId);
                    if (expired) {
                        counter.success++;
                    } else {
                        counter.skipped++;
                    }
                } catch (Exception ex) {
                    counter.fail++;
                    log.error("[TASK] appointmentExpireTask 处理预约失败 appointmentId={}", appointmentId, ex);
                }
            }
            if (appointmentIds.size() < limit) {
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
