package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstChannelInvite;
import java.util.List;

public interface JstChannelInviteMapper {
    JstChannelInvite selectJstChannelInviteByInviteId(Long inviteId);
    List<JstChannelInvite> selectJstChannelInviteList(JstChannelInvite query);
    int insertJstChannelInvite(JstChannelInvite row);
    int updateJstChannelInvite(JstChannelInvite row);
    int deleteJstChannelInviteByInviteId(Long inviteId);
}
