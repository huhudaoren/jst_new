package com.ruoyi.jst.channel.task;

import com.ruoyi.jst.channel.service.SalesResignationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 销售过渡期终结（每天 04:30）。
 * <p>
 * 扫描 status='resigned_pending_settle' AND transition_end_date <= NOW() 的销售，
 * 自动推进到 resigned_final。
 *
 * @author jst
 * @since 1.0.0
 */
@Component("salesTransitionEndTask")
public class SalesTransitionEndTask {

    private static final Logger log = LoggerFactory.getLogger(SalesTransitionEndTask.class);

    @Autowired
    private SalesResignationService resignationService;

    public void execute() {
        int n = resignationService.batchEndExpiredTransitions();
        log.info("[SalesTransitionEndTask] 终结 {} 个过渡期", n);
    }
}
