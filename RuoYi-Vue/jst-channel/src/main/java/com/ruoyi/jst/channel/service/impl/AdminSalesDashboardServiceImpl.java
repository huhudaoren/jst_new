package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.jst.channel.mapper.JstSalesCommissionLedgerMapper;
import com.ruoyi.jst.channel.mapper.JstSalesFollowupRecordMapper;
import com.ruoyi.jst.channel.mapper.JstSalesMapper;
import com.ruoyi.jst.channel.service.AdminSalesDashboardService;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
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

    @Override
    public List<Map<String, Object>> getCommissionTrend(String bucket,
                                                        String startDate,
                                                        String endDate,
                                                        String businessType,
                                                        String region) {
        DateRange range = parseInclusiveDateRange(startDate, endDate);
        String normalizedBucket = normalizeBucket(bucket);
        List<Map<String, Object>> rows =
                ledgerMapper.selectCommissionTrend(normalizedBucket, range.start, range.end, businessType, region);
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }
        for (Map<String, Object> row : rows) {
            BigDecimal commissionTotal = toBigDecimal(row.get("commissionTotal"));
            BigDecimal orderNetTotal = toBigDecimal(row.get("orderNetTotal"));
            row.put("costRate", safeDivide(commissionTotal, orderNetTotal));
        }
        return rows;
    }

    @Override
    public Map<String, Object> getCompressionStats(String startDate,
                                                   String endDate,
                                                   String businessType,
                                                   String region) {
        DateRange range = parseInclusiveDateRange(startDate, endDate);
        Map<String, Object> stats =
                ledgerMapper.selectCompressionStats(range.start, range.end, businessType, region);
        if (stats == null) {
            stats = new HashMap<>();
        }
        BigDecimal totalCount = toBigDecimal(stats.get("totalCount"));
        BigDecimal triggeredCount = toBigDecimal(stats.get("triggeredCount"));
        BigDecimal compressedAmount = toBigDecimal(stats.get("compressedAmount"));
        BigDecimal originalTotal = toBigDecimal(stats.get("originalTotal"));
        stats.put("triggerRate", safeDivide(triggeredCount, totalCount));
        stats.put("compressedRate", safeDivide(compressedAmount, originalTotal));
        return stats;
    }

    @Override
    public List<Map<String, Object>> getChannelHeatmap(String startDate,
                                                       String endDate,
                                                       String businessType,
                                                       String region) {
        DateRange range = parseInclusiveDateRange(startDate, endDate);
        List<Map<String, Object>> rows =
                ledgerMapper.selectChannelHeatmap(range.start, range.end, businessType, region);
        return rows == null ? Collections.emptyList() : rows;
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

    private DateRange parseInclusiveDateRange(String startDate, String endDate) {
        if (startDate == null || startDate.isBlank() || endDate == null || endDate.isBlank()) {
            throw new ServiceException("开始日期和结束日期不能为空");
        }
        try {
            LocalDate start = LocalDate.parse(startDate.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate endInclusive = LocalDate.parse(endDate.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (endInclusive.isBefore(start)) {
                throw new ServiceException("结束日期不能早于开始日期");
            }
            ZoneId zone = ZoneId.systemDefault();
            return new DateRange(
                    Date.from(start.atStartOfDay(zone).toInstant()),
                    Date.from(endInclusive.plusDays(1).atStartOfDay(zone).toInstant())
            );
        } catch (DateTimeParseException ex) {
            throw new ServiceException("日期格式非法，应为 yyyy-MM-dd");
        }
    }

    private String normalizeBucket(String bucket) {
        if (bucket == null || bucket.isBlank()) {
            return "day";
        }
        String normalized = bucket.trim().toLowerCase();
        if ("day".equals(normalized) || "week".equals(normalized) || "month".equals(normalized)) {
            return normalized;
        }
        throw new ServiceException("bucket 仅支持 day / week / month");
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal safeDivide(BigDecimal numerator, BigDecimal denominator) {
        if (numerator == null || denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return numerator.divide(denominator, 6, RoundingMode.HALF_UP);
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
