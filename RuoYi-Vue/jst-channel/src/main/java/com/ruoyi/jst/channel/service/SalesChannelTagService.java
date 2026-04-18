package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstSalesChannelTag;

import java.util.List;

/**
 * 渠道标签服务。
 */
public interface SalesChannelTagService {

    /**
     * 给渠道打标签（UK 冲突时返回友好错误）。
     */
    JstSalesChannelTag addTag(Long channelId, String tagCode, String tagColor, Long currentUserId);

    /**
     * 删除标签（仅创建者或 admin 可删）。
     */
    void removeTag(Long id, String currentUserName, boolean isAdmin);

    /**
     * 按渠道查标签列表。
     */
    List<JstSalesChannelTag> listByChannel(Long channelId);
}
