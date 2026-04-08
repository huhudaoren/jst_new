package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstChannel;

/**
 * 渠道方档案Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstChannelService 
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
     * 批量删除渠道方档案
     * 
     * @param channelIds 需要删除的渠道方档案主键集合
     * @return 结果
     */
    public int deleteJstChannelByChannelIds(Long[] channelIds);

    /**
     * 删除渠道方档案信息
     * 
     * @param channelId 渠道方档案主键
     * @return 结果
     */
    public int deleteJstChannelByChannelId(Long channelId);
}
