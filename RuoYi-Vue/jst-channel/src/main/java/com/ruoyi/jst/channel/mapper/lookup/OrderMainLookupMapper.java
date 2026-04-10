package com.ruoyi.jst.channel.mapper.lookup;

import com.ruoyi.jst.channel.dto.ChannelDashboardQueryDTO;
import com.ruoyi.jst.channel.vo.ChannelTopContestResVO;
import com.ruoyi.jst.channel.vo.ChannelTopStudentResVO;
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

    /**
     * 查询渠道工作台热门赛事排行榜。
     *
     * @param channelId 渠道方ID
     * @param startTime 查询开始时间
     * @param endTime 查询结束时间
     * @param limit 返回条数
     * @return 热门赛事列表
     */
    List<ChannelTopContestResVO> selectTopContestRanks(@Param("channelId") Long channelId,
                                                       @Param("startTime") Date startTime,
                                                       @Param("endTime") Date endTime,
                                                       @Param("limit") Integer limit);

    /**
     * 查询渠道工作台活跃学生排行榜。
     *
     * @param channelId 渠道方ID
     * @param limit 返回条数
     * @return 活跃学生列表
     */
    List<ChannelTopStudentResVO> selectTopStudentRanks(@Param("channelId") Long channelId,
                                                       @Param("limit") Integer limit);

    /**
     * 查询订单归属渠道。
     *
     * @param orderId 订单ID
     * @return 渠道方ID
     */
    Long selectOrderChannelId(@Param("orderId") Long orderId);

    /**
     * 查询渠道订单详情。
     *
     * @param orderId 订单ID
     * @return 订单详情扁平数据
     */
    Map<String, Object> selectChannelOrderDetail(@Param("orderId") Long orderId);
}
