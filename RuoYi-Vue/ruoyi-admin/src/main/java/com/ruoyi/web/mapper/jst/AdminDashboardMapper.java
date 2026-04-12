package com.ruoyi.web.mapper.jst;

import com.ruoyi.web.controller.jst.vo.ChannelRankVO;
import com.ruoyi.web.controller.jst.vo.ContestRankVO;
import com.ruoyi.web.controller.jst.vo.OverviewVO;
import com.ruoyi.web.controller.jst.vo.TodoVO;
import com.ruoyi.web.controller.jst.vo.TrendPointVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 平台运营数据聚合 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface AdminDashboardMapper {

    /**
     * 查询平台运营总览指标。
     *
     * @param todayStart 今日开始时间
     * @param todayEnd 今日结束时间
     * @param monthStart 本月开始时间
     * @param monthEnd 本月结束时间
     * @return 总览指标
     */
    OverviewVO selectOverview(@Param("todayStart") Date todayStart,
                              @Param("todayEnd") Date todayEnd,
                              @Param("monthStart") Date monthStart,
                              @Param("monthEnd") Date monthEnd);

    /**
     * 查询订单日趋势。
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日趋势列表
     */
    List<TrendPointVO> selectOrderTrend(@Param("startTime") Date startTime,
                                        @Param("endTime") Date endTime);

    /**
     * 查询收入日趋势。
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日趋势列表
     */
    List<TrendPointVO> selectRevenueTrend(@Param("startTime") Date startTime,
                                          @Param("endTime") Date endTime);

    /**
     * 查询报名日趋势。
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日趋势列表
     */
    List<TrendPointVO> selectEnrollTrend(@Param("startTime") Date startTime,
                                         @Param("endTime") Date endTime);

    /**
     * 查询赛事报名排行。
     *
     * @param limit 返回条数
     * @return 排行列表
     */
    List<ContestRankVO> selectTopContests(@Param("limit") Integer limit);

    /**
     * 查询渠道返点排行。
     *
     * @param limit 返回条数
     * @return 排行列表
     */
    List<ChannelRankVO> selectTopChannels(@Param("limit") Integer limit);

    /**
     * 查询待办事项计数。
     *
     * @return 待办计数
     */
    TodoVO selectTodo();
}
