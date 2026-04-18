package com.ruoyi.jst.channel.task;

import com.ruoyi.jst.channel.service.SalesCommissionSettlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 销售月结单生成（每月 1 日 03:00）。
 * <p>
 * 为所有可参与销售生成上月结算单（pending_review），spec §3.5 / C1 月结。
 * 幂等：同一销售同一期间不重复生成。
 *
 * @author jst
 * @since 1.0.0
 */
@Component("salesMonthlySettlementTask")
public class SalesMonthlySettlementTask {

    private static final Logger log = LoggerFactory.getLogger(SalesMonthlySettlementTask.class);

    @Autowired
    private SalesCommissionSettlementService settlementService;

    public void execute() {
        int n = settlementService.generateLastMonthSettlements();
        log.info("[SalesMonthlySettlementTask] 生成 {} 张月结单", n);
    }
}
