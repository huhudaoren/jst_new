package com.ruoyi.jst.channel.task;

import com.ruoyi.jst.channel.service.SalesPreRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 销售预录入过期清理（每天 01:00）。
 * 90 天未匹配的 pending → expired。
 */
@Component("salesPreRegisterExpireTask")
public class SalesPreRegisterExpireTask {

    private static final Logger log = LoggerFactory.getLogger(SalesPreRegisterExpireTask.class);

    @Autowired
    private SalesPreRegisterService preRegisterService;

    public void execute() {
        int n = preRegisterService.expirePendingByCron();
        log.info("[SalesPreRegisterExpireTask] 失效 {} 条预录入", n);
    }
}
