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

        // 暂存 filledInviteCode / businessNo（insert 前清空 invite_code，
        // 防止上级邀请码污染自己的 invite_code 字段；
        // 自己的邀请码将由 ChannelInviteBindingListener → InviteCodeService AFTER_COMMIT 生成并回写）。
        String filledInviteCode = jstChannel.getInviteCode();
        String businessNo = jstChannel.getBusinessNo();
        jstChannel.setInviteCode(null);  // 自己的邀请码等 listener 生成

        int rows = jstChannelMapper.insertJstChannel(jstChannel);

        // 发布渠道注册事件 → 触发：
        //   1. SalesAutoBindingListener (Order=0): 销售自动绑定 (plan-02)
        //   2. ChannelInviteBindingListener (Order=20): 生成邀请码 + 建立邀请关系 (plan-04)
        if (rows > 0 && jstChannel.getChannelId() != null) {
            eventPublisher.publishEvent(new ChannelRegisteredEvent(this,
                jstChannel.getChannelId(),
                jstChannel.getContactMobile(),   // 渠道联系手机号
                businessNo,                       // filledBusinessNo: 销售推荐码
                filledInviteCode                  // filledInviteCode: 上级渠道邀请码
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
