package com.ruoyi.jst.channel.listener;

import com.ruoyi.jst.channel.service.ChannelInviteService;
import com.ruoyi.jst.channel.service.InviteCodeService;
import com.ruoyi.jst.channel.service.SalesChannelBindingService;
import com.ruoyi.jst.common.event.ChannelRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 监听 ChannelRegisteredEvent，完成：
 * <ol>
 *   <li>为新渠道生成自己的邀请码（{@link InviteCodeService#generateForChannel}）</li>
 *   <li>M1 互斥检查（是否已有销售绑定）</li>
 *   <li>建立邀请关系（{@link ChannelInviteService#establishOnRegister}）</li>
 * </ol>
 *
 * <p>
 * {@code @Order(20)}：{@link SalesAutoBindingListener} 默认 Order=0 先跑完销售绑定，
 * 本 listener 再检查 M1 互斥状态（确保此时 binding 已写入）。
 * <p>
 * AFTER_COMMIT 阶段：主事务提交后运行，catch 异常不抛，不影响渠道注册主流程。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
@Order(20)
public class ChannelInviteBindingListener {

    private static final Logger log = LoggerFactory.getLogger(ChannelInviteBindingListener.class);

    @Autowired
    private InviteCodeService inviteCodeService;

    @Autowired
    private ChannelInviteService inviteService;

    @Autowired
    private SalesChannelBindingService bindingService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelRegisteredEvent event) {
        Long channelId = event.getChannelId();
        try {
            // Step 1: 为新渠道生成自己的邀请码（用于以后分享）
            inviteCodeService.generateForChannel(channelId);
            log.info("[ChannelInviteBinding] channel={} 邀请码生成完成", channelId);
        } catch (Exception ex) {
            log.error("[ChannelInviteBinding] channel={} 邀请码生成失败", channelId, ex);
            // 邀请码生成失败不阻断邀请关系建立（但可能导致 resolveInviter 无法找到）
        }

        try {
            // Step 2: M1 互斥检查
            boolean hasSalesBinding = bindingService.getCurrentByChannelId(channelId) != null;

            // Step 3: 建立邀请关系
            Long inviteId = inviteService.establishOnRegister(
                    channelId, event.getFilledInviteCode(), hasSalesBinding);
            log.info("[ChannelInviteBinding] channel={} inviteId={} (null=无邀请关系)", channelId, inviteId);
        } catch (Exception ex) {
            log.error("[ChannelInviteBinding] channel={} 邀请关系建立失败", channelId, ex);
        }
    }
}
