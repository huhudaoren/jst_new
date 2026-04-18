package com.ruoyi.jst.channel.task;

import com.ruoyi.jst.channel.service.SalesResignationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 销售离职执行（每天 04:00）。
 * <p>
 * 扫描 status='resign_apply' AND expected_resign_date <= NOW() 的销售，
 * 自动走阶段 2 离职流程（停权 + 转移渠道 + 失效预录入）。
 *
 * @author jst
 * @since 1.0.0
 */
@Component("salesResignExecuteTask")
public class SalesResignExecuteTask {

    private static final Logger log = LoggerFactory.getLogger(SalesResignExecuteTask.class);

    @Autowired
    private SalesResignationService resignationService;

    public void execute() {
        int n = resignationService.batchExecuteExpectedResigns();
        log.info("[SalesResignExecuteTask] 执行 {} 个销售离职", n);
    }
}
