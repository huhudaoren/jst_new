package com.ruoyi.jst.channel.task;

import com.ruoyi.jst.channel.service.ChannelDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 渠道分销提成入账推进（每天 02:30）。
 * <p>
 * 售后期满的 pending distribution ledger → accrued。
 * 幂等 UPDATE：重跑不会重复推进已处于 accrued 的行。
 * sys_job id=2008，bean name=channelDistributionAccrueTask。
 *
 * @author jst
 * @since 1.0.0
 */
@Component("channelDistributionAccrueTask")
public class ChannelDistributionAccrueTask {

    private static final Logger log = LoggerFactory.getLogger(ChannelDistributionAccrueTask.class);

    @Autowired
    private ChannelDistributionService distributionService;

    public void execute() {
        int n = distributionService.accruePendingByCron();
        log.info("[ChannelDistributionAccrueTask] 推进 {} 行 pending → accrued", n);
    }
}
