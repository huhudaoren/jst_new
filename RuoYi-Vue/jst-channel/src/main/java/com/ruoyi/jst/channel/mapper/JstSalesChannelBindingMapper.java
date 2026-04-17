package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesChannelBinding;
import java.util.List;

public interface JstSalesChannelBindingMapper {
    JstSalesChannelBinding selectJstSalesChannelBindingByBindingId(Long bindingId);
    List<JstSalesChannelBinding> selectJstSalesChannelBindingList(JstSalesChannelBinding query);
    int insertJstSalesChannelBinding(JstSalesChannelBinding row);
    int updateJstSalesChannelBinding(JstSalesChannelBinding row);
    int deleteJstSalesChannelBindingByBindingId(Long bindingId);
}
