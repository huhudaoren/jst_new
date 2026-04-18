package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.dto.ChannelBasicDTO;

/**
 * 渠道邀请码生成与解析服务 (plan-04 §2.2)。
 * <p>
 * 邀请码格式：8 位字母数字，排除易混淆字符 0/O/1/I/L（字符集 ABCDEFGHJKMNPQRSTUVWXYZ23456789）。
 * 使用 jst_channel.invite_code 唯一索引保证全局唯一；生成最多重试 5 次。
 *
 * @author jst
 * @since 1.0.0
 */
public interface InviteCodeService {

    /**
     * 为指定渠道生成一个唯一邀请码并回写 jst_channel.invite_code 字段。
     *
     * @param channelId 渠道 ID
     * @return 生成并入库成功的 8 位邀请码
     * @throws IllegalStateException 5 次重试后仍冲突（概率极低）
     */
    String generateForChannel(Long channelId);

    /**
     * 根据邀请码解析邀请方渠道基础信息。
     *
     * @param inviteCode 邀请码（忽略大小写）
     * @return 对应渠道基础信息，若不存在或渠道非 active 则返回 null
     */
    ChannelBasicDTO resolveInviter(String inviteCode);
}
