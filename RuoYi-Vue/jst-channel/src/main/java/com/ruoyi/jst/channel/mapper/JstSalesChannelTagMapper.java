package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesChannelTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JstSalesChannelTagMapper {
    JstSalesChannelTag selectJstSalesChannelTagById(Long id);
    List<JstSalesChannelTag> selectJstSalesChannelTagList(JstSalesChannelTag query);
    int insertJstSalesChannelTag(JstSalesChannelTag row);
    int updateJstSalesChannelTag(JstSalesChannelTag row);
    int deleteJstSalesChannelTagById(Long id);

    /** 按渠道查所有标签，按创建时间升序 */
    List<JstSalesChannelTag> listByChannel(@Param("channelId") Long channelId);
}
