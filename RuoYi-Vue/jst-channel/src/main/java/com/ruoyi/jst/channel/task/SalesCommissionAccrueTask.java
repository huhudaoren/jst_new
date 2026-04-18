package com.ruoyi.jst.channel.task;

import com.ruoyi.jst.channel.service.SalesCommissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 销售提成入账推进（每天 02:00）。
 * <p>
 * 售后期满的 pending ledger → accrued，spec §3.4。
 * 幂等 UPDATE：重跑不会重复推进已处于 accrued 的行。
 *
 * @author jst
 * @since 1.0.0
 */
@Component("salesCommissionAccrueTask")
public class SalesCommissionAccrueTask {

    private static final Logger log = LoggerFactory.getLogger(SalesCommissionAccrueTask.class);

    @Autowired
    private SalesCommissionService commissionService;

    public void execute() {
        int n = commissionService.accruePendingByCron();
        log.info("[SalesCommissionAccrueTask] 推进 {} 行 pending → accrued", n);
    }
}
