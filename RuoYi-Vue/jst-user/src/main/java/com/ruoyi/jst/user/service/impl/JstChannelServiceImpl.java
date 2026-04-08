package com.ruoyi.jst.user.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.user.mapper.JstChannelMapper;
import com.ruoyi.jst.user.domain.JstChannel;
import com.ruoyi.jst.user.service.IJstChannelService;

/**
 * 渠道方档案Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstChannelServiceImpl implements IJstChannelService 
{
    @Autowired
    private JstChannelMapper jstChannelMapper;

    /**
     * 查询渠道方档案
     * 
     * @param channelId 渠道方档案主键
     * @return 渠道方档案
     */
    @Override
    public JstChannel selectJstChannelByChannelId(Long channelId)
    {
        return jstChannelMapper.selectJstChannelByChannelId(channelId);
    }

    /**
     * 查询渠道方档案列表
     * 
     * @param jstChannel 渠道方档案
     * @return 渠道方档案
     */
    @Override
    public List<JstChannel> selectJstChannelList(JstChannel jstChannel)
    {
        return jstChannelMapper.selectJstChannelList(jstChannel);
    }

    /**
     * 新增渠道方档案
     * 
     * @param jstChannel 渠道方档案
     * @return 结果
     */
    @Override
    public int insertJstChannel(JstChannel jstChannel)
    {
        jstChannel.setCreateTime(DateUtils.getNowDate());
        return jstChannelMapper.insertJstChannel(jstChannel);
    }

    /**
     * 修改渠道方档案
     * 
     * @param jstChannel 渠道方档案
     * @return 结果
     */
    @Override
    public int updateJstChannel(JstChannel jstChannel)
    {
        jstChannel.setUpdateTime(DateUtils.getNowDate());
        return jstChannelMapper.updateJstChannel(jstChannel);
    }

    /**
     * 批量删除渠道方档案
     * 
     * @param channelIds 需要删除的渠道方档案主键
     * @return 结果
     */
    @Override
    public int deleteJstChannelByChannelIds(Long[] channelIds)
    {
        return jstChannelMapper.deleteJstChannelByChannelIds(channelIds);
    }

    /**
     * 删除渠道方档案信息
     * 
     * @param channelId 渠道方档案主键
     * @return 结果
     */
    @Override
    public int deleteJstChannelByChannelId(Long channelId)
    {
        return jstChannelMapper.deleteJstChannelByChannelId(channelId);
    }
}
