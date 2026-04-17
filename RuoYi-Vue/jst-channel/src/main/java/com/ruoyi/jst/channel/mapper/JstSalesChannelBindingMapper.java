package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesChannelBinding;
import java.util.List;

public interface JstSalesChannelBindingMapper {
    JstSalesChannelBinding selectJstSalesChannelBindingByBindingId(Long bindingId);
    List<JstSalesChannelBinding> selectJstSalesChannelBindingList(JstSalesChannelBinding query);
    int insertJstSalesChannelBinding(JstSalesChannelBinding row);
    int updateJstSalesChannelBinding(JstSalesChannelBinding row);
    int deleteJstSalesChannelBindingByBindingId(Long bindingId);

    JstSalesChannelBinding selectCurrentByChannelId(@org.apache.ibatis.annotations.Param("channelId") Long channelId);
    JstSalesChannelBinding selectAtTime(@org.apache.ibatis.annotations.Param("channelId") Long channelId,
                                        @org.apache.ibatis.annotations.Param("atTime") java.util.Date atTime);
    int closeBinding(@org.apache.ibatis.annotations.Param("bindingId") Long bindingId,
                     @org.apache.ibatis.annotations.Param("closeAt") java.util.Date closeAt);
    java.util.List<JstSalesChannelBinding> selectCurrentByOwnerSales(@org.apache.ibatis.annotations.Param("salesId") Long salesId);
}
