package com.ruoyi.jst.channel.mapper.lookup;

import com.ruoyi.jst.channel.dto.ChannelDashboardQueryDTO;
import com.ruoyi.jst.channel.vo.DashboardOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Read-only lookup for order_main.
 */
@Mapper
public interface OrderMainLookupMapper {

    Long countOrdersByMonth(@Param("channelId") Long channelId,
                            @Param("startTime") Date startTime,
                            @Param("endTime") Date endTime);

    BigDecimal sumPaidAmountByMonth(@Param("channelId") Long channelId,
                                    @Param("startTime") Date startTime,
                                    @Param("endTime") Date endTime);

    List<DashboardOrderVO> selectOrderList(ChannelDashboardQueryDTO query);

    List<Map<String, Object>> selectOrderTrend(@Param("channelId") Long channelId,
                                               @Param("startTime") Date startTime,
                                               @Param("endTime") Date endTime);

    List<Map<String, Object>> selectTopContests(@Param("channelId") Long channelId,
                                                @Param("startTime") Date startTime,
                                                @Param("endTime") Date endTime);
}
