package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.jst.channel.service.SalesNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * 销售通知服务 — 站内消息占位实现。
 * <p>
 * 当前仅打印日志，plan-04 阶段接入 JstMessageService 实现真实投递。
 * 使用 {@link Primary} 确保多实现场景下此实现优先注入。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
@Primary
public class SalesInternalMessageNotificationServiceImpl implements SalesNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SalesInternalMessageNotificationServiceImpl.class);

    @Override
    public void sendFollowupReminder(Long salesId, int dueCount) {
        log.info("[SalesNotification] salesId={} 今日待跟进 {} 条，请及时处理。", salesId, dueCount);
        // TODO plan-04: 调用 JstMessageService 投递站内消息
    }

    @Override
    public void sendTaskReminder(Long salesId, int taskCount) {
        log.info("[SalesNotification] salesId={} 今日到期任务 {} 个，请及时处理。", salesId, taskCount);
        // TODO plan-04: 调用 JstMessageService 投递站内消息
    }
}
