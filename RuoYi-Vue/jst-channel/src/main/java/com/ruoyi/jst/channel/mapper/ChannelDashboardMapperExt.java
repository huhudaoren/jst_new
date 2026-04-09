package com.ruoyi.jst.channel.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Dashboard aggregate mapper for channel-side metrics.
 */
@Mapper
public interface ChannelDashboardMapperExt {

    BigDecimal sumEstimatedRebateByMonth(@Param("channelId") Long channelId,
                                         @Param("startTime") Date startTime,
                                         @Param("endTime") Date endTime);

    List<Map<String, Object>> selectRebateTrend(@Param("channelId") Long channelId,
                                                @Param("startTime") Date startTime,
                                                @Param("endTime") Date endTime);
}
