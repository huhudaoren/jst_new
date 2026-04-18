package com.ruoyi.jst.user.mapper;

import java.util.List;
import com.ruoyi.jst.user.domain.JstChannel;
import org.apache.ibatis.annotations.Param;

/**
 * 渠道方档案Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstChannelMapper 
{
    /**
     * 查询渠道方档案
     * 
     * @param channelId 渠道方档案主键
     * @return 渠道方档案
     */
    public JstChannel selectJstChannelByChannelId(Long channelId);

    /**
     * 查询渠道方档案列表
     * 
     * @param jstChannel 渠道方档案
     * @return 渠道方档案集合
     */
    public List<JstChannel> selectJstChannelList(JstChannel jstChannel);

    /**
     * 新增渠道方档案
     * 
     * @param jstChannel 渠道方档案
     * @return 结果
     */
    public int insertJstChannel(JstChannel jstChannel);

    /**
     * 修改渠道方档案
     * 
     * @param jstChannel 渠道方档案
     * @return 结果
     */
    public int updateJstChannel(JstChannel jstChannel);

    /**
     * 删除渠道方档案
     * 
     * @param channelId 渠道方档案主键
     * @return 结果
     */
    public int deleteJstChannelByChannelId(Long channelId);

    /**
     * 批量删除渠道方档案
     *
     * @param channelIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstChannelByChannelIds(Long[] channelIds);

    /**
     * 按邀请码查渠道（唯一）。
     *
     * @param inviteCode 邀请码
     * @return 渠道，或 null
     */
    JstChannel selectByInviteCode(@Param("inviteCode") String inviteCode);

    /**
     * 回写渠道自身的邀请码。
     *
     * @param channelId  渠道 ID
     * @param inviteCode 生成的邀请码
     * @return 更新行数
     */
    int updateInviteCode(@Param("channelId") Long channelId, @Param("inviteCode") String inviteCode);

    /**
     * 记录注册时填写的上级邀请码（M1 互斥时无法建关系，仅留痕）。
     *
     * @param channelId  渠道 ID
     * @param inviteCode 填写的上级邀请码
     * @return 更新行数
     */
    int updateParentInviteAttempted(@Param("channelId") Long channelId, @Param("inviteCode") String inviteCode);

    /**
     * 回写上级邀请关系 ID。
     *
     * @param channelId     渠道 ID
     * @param parentInviteId 邀请关系 ID
     * @return 更新行数
     */
    int updateParentInviteId(@Param("channelId") Long channelId, @Param("parentInviteId") Long parentInviteId);
}
