package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.jst.channel.domain.JstChannelInvite;
import com.ruoyi.jst.channel.dto.ChannelBasicDTO;
import com.ruoyi.jst.channel.mapper.JstChannelInviteMapper;
import com.ruoyi.jst.channel.mapper.lookup.ChannelDetailLookupMapper;
import com.ruoyi.jst.channel.service.ChannelInviteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 渠道邀请关系管理服务实现 (plan-04 §2.2)。
 *
 * <p>
 * 邀请规则：
 * <ol>
 *   <li>邀请码空/无效 → 不建关系</li>
 *   <li>inviter.is_official=1 → 不建（官方渠道不充当邀请方）</li>
 *   <li>inviter.status != 1(active) → 不建</li>
 *   <li>反自邀：inviter == invitee → 不建</li>
 *   <li>M1 互斥：hasSalesBinding=true → 不建，记 parent_invite_attempted</li>
 *   <li>其他：INSERT invite 行 + UPDATE new channel.parent_invite_id</li>
 * </ol>
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class ChannelInviteServiceImpl implements ChannelInviteService {

    private static final Logger log = LoggerFactory.getLogger(ChannelInviteServiceImpl.class);

    @Autowired
    private JstChannelInviteMapper inviteMapper;

    @Autowired
    private ChannelDetailLookupMapper channelLookup;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long establishOnRegister(Long newChannelId, String filledInviteCode, boolean hasSalesBinding) {
        if (filledInviteCode == null || filledInviteCode.isBlank()) {
            log.debug("[ChannelInvite] channel={} 未填写邀请码，跳过", newChannelId);
            return null;
        }

        String code = filledInviteCode.toUpperCase().trim();
        ChannelBasicDTO inviter = channelLookup.selectBasicByInviteCode(code);
        if (inviter == null) {
            log.info("[ChannelInvite] channel={} 邀请码={} 无效（inviter 不存在）", newChannelId, code);
            return null;
        }

        // 反自邀
        if (Objects.equals(inviter.getChannelId(), newChannelId)) {
            log.warn("[ChannelInvite] channel={} 尝试自邀，拒绝", newChannelId);
            return null;
        }

        // 官方渠道不充当邀请方
        if (Integer.valueOf(1).equals(inviter.getIsOfficial())) {
            log.info("[ChannelInvite] channel={} inviter={} 是官方渠道，不建邀请关系", newChannelId, inviter.getChannelId());
            return null;
        }

        // inviter 必须启用
        if (!Integer.valueOf(1).equals(inviter.getStatus())) {
            log.info("[ChannelInvite] channel={} inviter={} status={} 非 active，不建邀请关系",
                    newChannelId, inviter.getChannelId(), inviter.getStatus());
            return null;
        }

        // M1 互斥：已有销售绑定
        if (hasSalesBinding) {
            log.info("[ChannelInvite] channel={} 已有销售绑定（M1互斥），记录 parent_invite_attempted={}", newChannelId, code);
            channelLookup.updateParentInviteAttempted(newChannelId, code);
            return null;
        }

        // 建立邀请关系
        JstChannelInvite invite = new JstChannelInvite();
        invite.setInviterChannelId(inviter.getChannelId());
        invite.setInviteeChannelId(newChannelId);
        invite.setInviteCode(code);
        invite.setShareScene("mp_share");
        invite.setCommissionRate(null);  // null = 用系统默认费率
        invite.setInvitedAt(new Date());
        invite.setStatus("active");
        inviteMapper.insertJstChannelInvite(invite);

        // 回写 parent_invite_id
        channelLookup.updateParentInviteId(newChannelId, invite.getInviteId());

        log.info("[ChannelInvite] channel={} 邀请关系建立成功 inviteId={} inviter={}",
                newChannelId, invite.getInviteId(), inviter.getChannelId());
        return invite.getInviteId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindByAdmin(Long inviteId, Long adminUserId, String reason) {
        JstChannelInvite invite = inviteMapper.selectJstChannelInviteByInviteId(inviteId);
        if (invite == null) {
            throw new IllegalArgumentException("邀请关系不存在: " + inviteId);
        }
        inviteMapper.unbindActive(inviteId);
        log.info("[ChannelInvite] admin={} 解绑 inviteId={} reason={}", adminUserId, inviteId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long forceEstablishByAdmin(Long inviterChannelId, Long inviteeChannelId,
                                      BigDecimal customRate, Long adminUserId) {
        // 先解绑 invitee 现有的 active 关系（若有）
        JstChannelInvite existing = inviteMapper.selectActiveByInvitee(inviteeChannelId);
        if (existing != null) {
            inviteMapper.unbindActive(existing.getInviteId());
            log.info("[ChannelInvite] admin={} 强制绑定前解绑旧关系 inviteId={}", adminUserId, existing.getInviteId());
        }

        ChannelBasicDTO inviter = channelLookup.selectBasicByChannelId(inviterChannelId);
        if (inviter == null) {
            throw new IllegalArgumentException("上级渠道不存在: " + inviterChannelId);
        }

        JstChannelInvite invite = new JstChannelInvite();
        invite.setInviterChannelId(inviterChannelId);
        invite.setInviteeChannelId(inviteeChannelId);
        invite.setInviteCode(inviter.getInviteCode() != null ? inviter.getInviteCode() : "ADMIN");
        invite.setShareScene("manual_admin");
        invite.setCommissionRate(customRate);
        invite.setInvitedAt(new Date());
        invite.setStatus("active");
        inviteMapper.insertJstChannelInvite(invite);

        channelLookup.updateParentInviteId(inviteeChannelId, invite.getInviteId());

        log.info("[ChannelInvite] admin={} 强制建立邀请关系 inviteId={} inviter={} invitee={} rate={}",
                adminUserId, invite.getInviteId(), inviterChannelId, inviteeChannelId, customRate);
        return invite.getInviteId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRateByAdmin(Long inviteId, BigDecimal rate) {
        JstChannelInvite update = new JstChannelInvite();
        update.setInviteId(inviteId);
        update.setCommissionRate(rate);
        inviteMapper.updateJstChannelInvite(update);
        log.info("[ChannelInvite] 更新邀请关系 inviteId={} rate={}", inviteId, rate);
    }

    @Override
    public List<JstChannelInvite> listByInviter(Long inviterChannelId) {
        return inviteMapper.selectByInviter(inviterChannelId);
    }

    @Override
    public JstChannelInvite getActiveByInvitee(Long inviteeChannelId) {
        return inviteMapper.selectActiveByInvitee(inviteeChannelId);
    }
}
