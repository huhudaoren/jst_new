package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.jst.channel.dto.ChannelBasicDTO;
import com.ruoyi.jst.channel.mapper.lookup.ChannelDetailLookupMapper;
import com.ruoyi.jst.channel.service.InviteCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * 渠道邀请码生成与解析服务实现。
 * <p>
 * 字符集：ABCDEFGHJKMNPQRSTUVWXYZ23456789（排除易混淆字符 0/O/1/I/L）。
 * 8 位码，碰撞概率极低；5 次重试兜底。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class InviteCodeServiceImpl implements InviteCodeService {

    private static final Logger log = LoggerFactory.getLogger(InviteCodeServiceImpl.class);

    /** 排除 0/O/1/I/L 后的安全字符集（32 个字符），共 32^8 ≈ 10^12 组合 */
    private static final String CHARSET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    private static final int CODE_LEN = 8;
    private static final int MAX_RETRY = 5;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private ChannelDetailLookupMapper channelLookup;

    @Override
    public String generateForChannel(Long channelId) {
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            String code = randomCode();
            ChannelBasicDTO existing = channelLookup.selectBasicByInviteCode(code);
            if (existing == null) {
                int rows = channelLookup.updateInviteCode(channelId, code);
                if (rows > 0) {
                    log.info("[InviteCode] channel={} 生成邀请码={}", channelId, code);
                    return code;
                }
            }
            log.debug("[InviteCode] channel={} 邀请码={} 已存在，重试 {}/{}", channelId, code, attempt, MAX_RETRY);
        }
        throw new IllegalStateException("[InviteCode] channel=" + channelId + " 生成邀请码失败：" + MAX_RETRY + " 次重试后仍冲突");
    }

    @Override
    public ChannelBasicDTO resolveInviter(String inviteCode) {
        if (inviteCode == null || inviteCode.isBlank()) return null;
        ChannelBasicDTO channel = channelLookup.selectBasicByInviteCode(inviteCode.toUpperCase().trim());
        if (channel == null) return null;
        // 渠道必须处于启用状态（status=1）
        if (!Integer.valueOf(1).equals(channel.getStatus())) {
            log.debug("[InviteCode] inviteCode={} 对应渠道 {} status={} 非 active，拒绝",
                    inviteCode, channel.getChannelId(), channel.getStatus());
            return null;
        }
        return channel;
    }

    // ===

    private static String randomCode() {
        char[] buf = new char[CODE_LEN];
        for (int i = 0; i < CODE_LEN; i++) {
            buf[i] = CHARSET.charAt(RANDOM.nextInt(CHARSET.length()));
        }
        return new String(buf);
    }
}
