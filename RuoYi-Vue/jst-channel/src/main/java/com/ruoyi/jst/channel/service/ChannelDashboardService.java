package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.dto.ChannelDashboardQueryDTO;
import com.ruoyi.jst.channel.vo.DashboardMonthlyVO;
import com.ruoyi.jst.channel.vo.DashboardOrderVO;
import com.ruoyi.jst.channel.vo.DashboardStatsVO;
import com.ruoyi.jst.channel.vo.DashboardStudentVO;

import java.util.List;

/**
 * Channel dashboard read service.
 */
public interface ChannelDashboardService {

    DashboardMonthlyVO getMonthly(ChannelDashboardQueryDTO query);

    List<DashboardStudentVO> selectStudentList(ChannelDashboardQueryDTO query);

    List<DashboardOrderVO> selectOrderList(ChannelDashboardQueryDTO query);

    DashboardStatsVO getStats(ChannelDashboardQueryDTO query);
}
