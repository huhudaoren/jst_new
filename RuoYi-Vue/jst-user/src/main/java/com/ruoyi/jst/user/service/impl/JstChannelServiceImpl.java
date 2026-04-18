package com.ruoyi.jst.user.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.event.ChannelRegisteredEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
    @Transactional(rollbackFor = Exception.class)
    public int insertJstChannel(JstChannel jstChannel)
    {
        jstChannel.setCreateTime(DateUtils.getNowDate());
        int rows = jstChannelMapper.insertJstChannel(jstChannel);
        // 发布渠道注册事件 → 触发销售自动绑定 (plan-02 Task 7 SalesAutoBindingService)
        // 以及未来 plan-04 的渠道邀请关系建立 (ChannelInviteBindingListener)。
        // businessNo / inviteCode 暂传 null，plan-04 Task 8 wire DTO 字段后填入。
        if (rows > 0 && jstChannel.getChannelId() != null) {
            eventPublisher.publishEvent(new ChannelRegisteredEvent(this,
                jstChannel.getChannelId(),
                jstChannel.getPhone(),
                null,   // filledBusinessNo: plan-04 wire
                null    // filledInviteCode: plan-04 wire
            ));
        }
        return rows;
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
