package com.ruoyi.web.service.jst.impl;

import com.ruoyi.web.controller.jst.vo.ChannelRankVO;
import com.ruoyi.web.controller.jst.vo.ContestRankVO;
import com.ruoyi.web.controller.jst.vo.OverviewVO;
import com.ruoyi.web.controller.jst.vo.TodoVO;
import com.ruoyi.web.controller.jst.vo.TrendPointVO;
import com.ruoyi.web.mapper.jst.AdminDashboardMapper;
import com.ruoyi.web.service.jst.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台运营数据聚合 Service 实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private AdminDashboardMapper adminDashboardMapper;

    /**
     * 获取平台运营总览。
     *
     * @return 总览指标
     * @关联表 jst_order_main / jst_refund_record / jst_user / jst_contest / jst_enroll_record
     * @关联权限 jst:admin:dashboard
     */
    @Override
    public OverviewVO getOverview() {
        LocalDate today = LocalDate.now();
        LocalDate monthStartDate = today.withDayOfMonth(1);
        LocalDate monthEndDate = monthStartDate.plusMonths(1);
        OverviewVO overview = adminDashboardMapper.selectOverview(
                toDate(today),
                toDate(today.plusDays(1)),
                toDate(monthStartDate),
                toDate(monthEndDate)
        );
        if (overview == null) {
            overview = new OverviewVO();
        }
        overview.setTodayOrderCount(defaultCount(overview.getTodayOrderCount()));
        overview.setTodayRevenue(defaultAmount(overview.getTodayRevenue()));
        overview.setMonthRevenue(defaultAmount(overview.getMonthRevenue()));
        overview.setTotalUserCount(defaultCount(overview.getTotalUserCount()));
        overview.setTotalContestCount(defaultCount(overview.getTotalContestCount()));
        overview.setTotalEnrollCount(defaultCount(overview.getTotalEnrollCount()));
        return overview;
    }

    /**
     * 获取平台运营趋势。
     *
     * @param days 近 N 天，仅支持 7/30
     * @return 趋势列表
     * @关联表 jst_order_main / jst_enroll_record
     * @关联权限 jst:admin:dashboard
     */
    @Override
    public List<TrendPointVO> getTrend(int days) {
        LocalDate endDateInclusive = LocalDate.now();
        LocalDate startDate = endDateInclusive.minusDays(days - 1L);
        Date startTime = toDate(startDate);
        Date endTime = toDate(endDateInclusive.plusDays(1));

        Map<String, TrendPointVO> trendMap = new LinkedHashMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDateInclusive); date = date.plusDays(1)) {
            TrendPointVO point = new TrendPointVO();
            point.setDate(date.format(DAY_FORMATTER));
            point.setOrderCount(0);
            point.setRevenue(ZERO_AMOUNT);
            point.setEnrollCount(0);
            trendMap.put(point.getDate(), point);
        }

        mergeOrderTrend(trendMap, adminDashboardMapper.selectOrderTrend(startTime, endTime));
        mergeRevenueTrend(trendMap, adminDashboardMapper.selectRevenueTrend(startTime, endTime));
        mergeEnrollTrend(trendMap, adminDashboardMapper.selectEnrollTrend(startTime, endTime));
        return new ArrayList<>(trendMap.values());
    }

    /**
     * 获取赛事报名排行。
     *
     * @param limit 返回条数
     * @return 排行列表
     * @关联表 jst_contest / jst_enroll_record / jst_order_main
     * @关联权限 jst:admin:dashboard
     */
    @Override
    public List<ContestRankVO> getTopContests(int limit) {
        List<ContestRankVO> rows = adminDashboardMapper.selectTopContests(limit);
        if (rows == null) {
            return new ArrayList<>();
        }
        for (ContestRankVO row : rows) {
            row.setEnrollCount(defaultCount(row.getEnrollCount()));
            row.setTotalRevenue(defaultAmount(row.getTotalRevenue()));
        }
        return rows;
    }

    /**
     * 获取渠道返点排行。
     *
     * @param limit 返回条数
     * @return 排行列表
     * @关联表 jst_channel / jst_rebate_ledger / jst_student_channel_binding
     * @关联权限 jst:admin:dashboard
     */
    @Override
    public List<ChannelRankVO> getTopChannels(int limit) {
        List<ChannelRankVO> rows = adminDashboardMapper.selectTopChannels(limit);
        if (rows == null) {
            return new ArrayList<>();
        }
        for (ChannelRankVO row : rows) {
            row.setStudentCount(defaultCount(row.getStudentCount()));
            row.setTotalRebate(defaultAmount(row.getTotalRebate()));
        }
        return rows;
    }

    /**
     * 获取平台待办计数。
     *
     * @return 待办计数
     * @关联表 jst_contest / jst_enroll_record / jst_refund_record / jst_rebate_settlement / jst_event_partner_apply / jst_channel_auth_apply
     * @关联权限 jst:admin:dashboard
     */
    @Override
    public TodoVO getTodo() {
        TodoVO todo = adminDashboardMapper.selectTodo();
        if (todo == null) {
            todo = new TodoVO();
        }
        todo.setPendingContestAudit(defaultCount(todo.getPendingContestAudit()));
        todo.setPendingEnrollAudit(defaultCount(todo.getPendingEnrollAudit()));
        todo.setPendingRefund(defaultCount(todo.getPendingRefund()));
        todo.setPendingWithdraw(defaultCount(todo.getPendingWithdraw()));
        todo.setPendingPartnerApply(defaultCount(todo.getPendingPartnerApply()));
        todo.setPendingChannelAuth(defaultCount(todo.getPendingChannelAuth()));
        return todo;
    }

    private void mergeOrderTrend(Map<String, TrendPointVO> trendMap, List<TrendPointVO> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (TrendPointVO row : rows) {
            TrendPointVO target = trendMap.get(row.getDate());
            if (target == null) {
                continue;
            }
            target.setOrderCount(defaultCount(row.getOrderCount()));
        }
    }

    private void mergeRevenueTrend(Map<String, TrendPointVO> trendMap, List<TrendPointVO> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (TrendPointVO row : rows) {
            TrendPointVO target = trendMap.get(row.getDate());
            if (target == null) {
                continue;
            }
            target.setRevenue(defaultAmount(row.getRevenue()));
        }
    }

    private void mergeEnrollTrend(Map<String, TrendPointVO> trendMap, List<TrendPointVO> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (TrendPointVO row : rows) {
            TrendPointVO target = trendMap.get(row.getDate());
            if (target == null) {
                continue;
            }
            target.setEnrollCount(defaultCount(row.getEnrollCount()));
        }
    }

    private Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Integer defaultCount(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value == null ? ZERO_AMOUNT : value.setScale(2, RoundingMode.HALF_UP);
    }
}
