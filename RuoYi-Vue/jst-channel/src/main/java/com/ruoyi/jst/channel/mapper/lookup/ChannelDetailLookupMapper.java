package com.ruoyi.jst.channel.mapper.lookup;

import com.ruoyi.jst.channel.dto.ChannelBasicDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 渠道详情查询/更新 mapper（plan-04 invite/distribution 用）。
 * <p>
 * jst-channel 不依赖 jst-user，通过 lookup 方式跨模块查询/更新 jst_channel 表。
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface ChannelDetailLookupMapper {

    @Select("SELECT channel_id AS channelId, status, is_official AS isOfficial, " +
            "invite_code AS inviteCode, parent_invite_id AS parentInviteId " +
            "FROM jst_channel WHERE channel_id = #{channelId} AND del_flag = '0' LIMIT 1")
    ChannelBasicDTO selectBasicByChannelId(@Param("channelId") Long channelId);

    @Select("SELECT channel_id AS channelId, status, is_official AS isOfficial, " +
            "invite_code AS inviteCode, parent_invite_id AS parentInviteId " +
            "FROM jst_channel WHERE invite_code = #{inviteCode} AND del_flag = '0' LIMIT 1")
    ChannelBasicDTO selectBasicByInviteCode(@Param("inviteCode") String inviteCode);

    @Update("UPDATE jst_channel SET invite_code = #{inviteCode}, update_time = NOW() " +
            "WHERE channel_id = #{channelId}")
    int updateInviteCode(@Param("channelId") Long channelId, @Param("inviteCode") String inviteCode);

    @Update("UPDATE jst_channel SET parent_invite_attempted = #{inviteCode}, update_time = NOW() " +
            "WHERE channel_id = #{channelId}")
    int updateParentInviteAttempted(@Param("channelId") Long channelId, @Param("inviteCode") String inviteCode);

    @Update("UPDATE jst_channel SET parent_invite_id = #{parentInviteId}, update_time = NOW() " +
            "WHERE channel_id = #{channelId}")
    int updateParentInviteId(@Param("channelId") Long channelId, @Param("parentInviteId") Long parentInviteId);
}
