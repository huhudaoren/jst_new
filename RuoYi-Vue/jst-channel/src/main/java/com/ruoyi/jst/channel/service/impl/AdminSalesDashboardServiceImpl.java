package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.jst.channel.mapper.JstSalesCommissionLedgerMapper;
import com.ruoyi.jst.channel.mapper.JstSalesFollowupRecordMapper;
import com.ruoyi.jst.channel.mapper.JstSalesMapper;
import com.ruoyi.jst.channel.service.AdminSalesDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 销售管理 Dashboard 聚合 Service 实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class AdminSalesDashboardServiceImpl implements AdminSalesDashboardService {

    private static final int DEFAULT_RANKING_LIMIT = 10;

    @Autowired
    private JstSalesMapper salesMapper;

    @Autowired
    private JstSalesCommissionLedgerMapper ledgerMapper;

    @Autowired
    private JstSalesFollowupRecordMapper followupMapper;

    @Override
    public Map<String, Object> getOverview(String month) {
        DateRange range = parseDateRange(month);

        // 活跃销售数
        int activeSalesCount = salesMapper.countActive();

        // 月度提成汇总
        Map<String, Object> monthSummary = ledgerMapper.aggregateMonthSummary(range.start, range.end);
        if (monthSummary == null) monthSummary = new HashMap<>();

        // 销售排行
        List<Map<String, Object>> salesRanking =
                ledgerMapper.selectSalesRanking(range.start, range.end, DEFAULT_RANKING_LIMIT);

        Map<String, Object> result = new HashMap<>();
        result.put("activeSalesCount", activeSalesCount);
        result.put("monthOrderCount", monthSummary.getOrDefault("orderCount", 0));
        result.put("monthGmv", monthSummary.getOrDefault("gmv", 0));
        result.put("monthCommissionCost", monthSummary.getOrDefault("commissionCost", 0));
        result.put("salesRanking", salesRanking);
        // 前端兼容别名
        result.put("salesRank", salesRanking);
        return result;
    }

    @Override
    public Map<String, Object> getSalesDetail(Long salesId, String month) {
        DateRange range = parseDateRange(month);

        // 整体聚合
        Map<String, Object> agg = ledgerMapper.aggregatePerformance(salesId, range.start, range.end);
        if (agg == null) agg = new HashMap<>();

        // 按业务类型明细
        List<Map<String, Object>> byType =
                ledgerMapper.aggregateByBusinessType(salesId, range.start, range.end);

        Map<String, Object> result = new HashMap<>(agg);
        result.put("salesId", salesId);
        result.put("byBusinessType", byType);
        return result;
    }

    @Override
    public List<Map<String, Object>> getFollowupActivity(String month) {
        DateRange range = parseDateRange(month);
        return followupMapper.aggregateFollowupActivity(range.start, range.end);
    }

    // ===== private helpers =====

    private DateRange parseDateRange(String month) {
        YearMonth ym;
        if (month != null && !month.isBlank()) {
            ym = YearMonth.parse(month.trim(), DateTimeFormatter.ofPattern("yyyy-MM"));
        } else {
            ym = YearMonth.now();
        }
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.plusMonths(1).atDay(1);
        ZoneId zone = ZoneId.systemDefault();
        return new DateRange(
                Date.from(start.atStartOfDay(zone).toInstant()),
                Date.from(end.atStartOfDay(zone).toInstant())
        );
    }

    private static class DateRange {
        final Date start;
        final Date end;
        DateRange(Date start, Date end) {
            this.start = start;
            this.end = end;
        }
    }
}
