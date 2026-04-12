package com.ruoyi.jst.channel.mapper.lookup;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * Read-only channel lookup for current user.
 */
@Mapper
public interface ChannelLookupMapper {

    @Select("SELECT channel_id AS channelId, user_id AS userId, channel_name AS channelName, " +
            "auth_status AS authStatus, status, bank_account_name AS bankAccountName, " +
            "bank_account_no AS bankAccountNo, bank_name AS bankName " +
            "FROM jst_channel " +
            "WHERE user_id = #{userId} AND del_flag = '0' " +
            "ORDER BY channel_id ASC LIMIT 1")
    Map<String, Object> selectCurrentByUserId(@Param("userId") Long userId);

    @Select("SELECT user_id FROM jst_channel WHERE channel_id = #{channelId} AND del_flag = '0' LIMIT 1")
    Long selectUserIdByChannelId(@Param("channelId") Long channelId);
}
