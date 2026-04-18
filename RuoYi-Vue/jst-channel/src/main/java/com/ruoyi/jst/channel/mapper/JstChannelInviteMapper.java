package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstChannelInvite;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface JstChannelInviteMapper {
    JstChannelInvite selectJstChannelInviteByInviteId(Long inviteId);
    List<JstChannelInvite> selectJstChannelInviteList(JstChannelInvite query);
    int insertJstChannelInvite(JstChannelInvite row);
    int updateJstChannelInvite(JstChannelInvite row);
    int deleteJstChannelInviteByInviteId(Long inviteId);

    /**
     * 查某渠道作为"下级"在某时刻的激活邀请关系（提成穿透 + 分销计算用）。
     * 返回 null 表示该渠道无上级或上级关系已解绑。
     */
    JstChannelInvite selectActiveByInviteeAtTime(@Param("inviteeChannelId") Long inviteeChannelId,
                                                  @Param("atTime") Date atTime);

    /**
     * 查某渠道当前激活的邀请关系（作为下级，不限时间）。
     */
    JstChannelInvite selectActiveByInvitee(@Param("inviteeChannelId") Long inviteeChannelId);

    /**
     * 查某渠道作为上级的所有邀请关系（不限状态，按时间倒序）。
     */
    List<JstChannelInvite> selectByInviter(@Param("inviterChannelId") Long inviterChannelId);

    /**
     * 将指定 inviteId 的关系 status 置为 unbound。
     */
    int unbindActive(@Param("inviteId") Long inviteId);
}
