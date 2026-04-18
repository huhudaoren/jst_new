package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstChannelInvite;

import java.math.BigDecimal;
import java.util.List;

/**
 * 渠道邀请关系管理服务 (plan-04 §2.2)。
 * <p>
 * 规则摘要：
 * <ul>
 *   <li>M1 互斥：已有销售绑定时不建邀请关系，仅记录 parent_invite_attempted</li>
 *   <li>官方渠道（is_official=1）不充当邀请方（避免官方流量分润自己下属）</li>
 *   <li>反自邀：inviter == invitee 时拒绝</li>
 *   <li>唯一活跃：jst_channel_invite 表 uk_invitee_active 保证每渠道至多 1 个 active 关系</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
public interface ChannelInviteService {

    /**
     * 渠道注册时建立邀请关系（由 ChannelInviteBindingListener AFTER_COMMIT 调用）。
     *
     * @param newChannelId      新注册渠道 ID
     * @param filledInviteCode  注册时填写的上级邀请码（可为 null）
     * @param hasSalesBinding   是否已有销售绑定（M1 互斥判断）
     * @return 创建的 invite_id，若不满足建立条件则返回 null
     */
    Long establishOnRegister(Long newChannelId, String filledInviteCode, boolean hasSalesBinding);

    /**
     * 管理员解绑邀请关系。
     *
     * @param inviteId    邀请关系 ID
     * @param adminUserId 操作管理员 ID
     * @param reason      解绑原因（记录到 remark）
     */
    void unbindByAdmin(Long inviteId, Long adminUserId, String reason);

    /**
     * 管理员强制建立邀请关系（绕过 M1 互斥限制）。
     *
     * @param inviterChannelId 上级渠道 ID
     * @param inviteeChannelId 下级渠道 ID
     * @param customRate       自定义费率（null 则用系统默认）
     * @param adminUserId      操作管理员 ID
     * @return 创建的 invite_id
     */
    Long forceEstablishByAdmin(Long inviterChannelId, Long inviteeChannelId,
                               BigDecimal customRate, Long adminUserId);

    /**
     * 管理员更新邀请关系费率。
     *
     * @param inviteId 邀请关系 ID
     * @param rate     新费率（0~1）
     */
    void updateRateByAdmin(Long inviteId, BigDecimal rate);

    /**
     * 查询某渠道邀请的所有下级（不限状态）。
     *
     * @param inviterChannelId 上级渠道 ID
     * @return 邀请关系列表
     */
    List<JstChannelInvite> listByInviter(Long inviterChannelId);

    /**
     * 获取某渠道当前激活的邀请关系（作为下级）。
     *
     * @param inviteeChannelId 下级渠道 ID
     * @return 当前激活关系，或 null
     */
    JstChannelInvite getActiveByInvitee(Long inviteeChannelId);
}
