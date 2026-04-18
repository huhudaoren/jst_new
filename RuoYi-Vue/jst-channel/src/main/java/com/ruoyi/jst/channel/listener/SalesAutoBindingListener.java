package com.ruoyi.jst.channel.listener;

import com.ruoyi.jst.channel.service.SalesAutoBindingService;
import com.ruoyi.jst.common.event.ChannelRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 监听 ChannelRegisteredEvent，触发销售自动绑定算法 (spec §2.1)。
 * <p>
 * AFTER_COMMIT phase：渠道注册主事务必须先提交，再触发绑定（避免回滚时残留绑定关系）。
 * listener 内 catch 异常不抛，绑定失败不影响渠道注册主流程；admin 后续可手动绑。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class SalesAutoBindingListener {

    private static final Logger log = LoggerFactory.getLogger(SalesAutoBindingListener.class);

    @Autowired
    private SalesAutoBindingService autoBindingService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelRegisteredEvent event) {
        try {
            Long salesId = autoBindingService.autoBind(
                event.getChannelId(),
                event.getRegisteredPhone(),
                event.getFilledBusinessNo()
            );
            log.info("[SalesAutoBinding] channel={} bind={} (null=未匹配)",
                    event.getChannelId(), salesId);
        } catch (Exception ex) {
            log.error("[SalesAutoBinding] channel={} 绑定失败", event.getChannelId(), ex);
        }
    }
}
