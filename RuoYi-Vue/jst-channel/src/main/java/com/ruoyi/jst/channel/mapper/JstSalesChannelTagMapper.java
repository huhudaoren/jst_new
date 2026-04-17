package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesChannelTag;
import java.util.List;

public interface JstSalesChannelTagMapper {
    JstSalesChannelTag selectJstSalesChannelTagById(Long id);
    List<JstSalesChannelTag> selectJstSalesChannelTagList(JstSalesChannelTag query);
    int insertJstSalesChannelTag(JstSalesChannelTag row);
    int updateJstSalesChannelTag(JstSalesChannelTag row);
    int deleteJstSalesChannelTagById(Long id);
}
