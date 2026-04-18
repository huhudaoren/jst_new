package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.channel.dto.ChannelDashboardQueryDTO;
import com.ruoyi.jst.channel.mapper.ChannelDashboardMapperExt;
import com.ruoyi.jst.channel.mapper.lookup.ChannelLookupMapper;
import com.ruoyi.jst.channel.mapper.lookup.OrderMainLookupMapper;
import com.ruoyi.jst.channel.mapper.lookup.UserChannelBindingLookupMapper;
import com.ruoyi.jst.channel.service.ChannelDashboardService;
import com.ruoyi.jst.channel.vo.DashboardMonthlyVO;
import com.ruoyi.jst.channel.vo.DashboardOrderVO;
import com.ruoyi.jst.channel.vo.DashboardStatsVO;
import com.ruoyi.jst.channel.vo.DashboardStudentVO;
import com.ruoyi.jst.common.exception.BizErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Channel dashboard service implementation.
 */
@Service
public class ChannelDashboardServiceImpl implements ChannelDashboardService {

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private ChannelLookupMapper channelLookupMapper;

    @Autowired
    private UserChannelBindingLookupMapper userChannelBindingLookupMapper;

    @Autowired
    private OrderMainLookupMapper orderMainLookupMapper;

    @Autowired
    private ChannelDashboardMapperExt channelDashboardMapperExt;

    @Override
    public DashboardMonthlyVO getMonthly(ChannelDashboardQueryDTO query) {
        Long channelId = requireCurrentChannelId();
        YearMonth yearMonth = resolveYearMonth(query == null ? null : query.getYearMonth());
        DateRange range = monthRange(yearMonth);

        DashboardMonthlyVO vo = new DashboardMonthlyVO();
        vo.setYearMonth(yearMonth.toString());
        vo.setNewStudentCount(intValue(userChannelBindingLookupMapper.countNewStudentByMonth(
                channelId, range.getStartTime(), range.getEndTime())));
        // Round 2A A1: 累计绑定学生数
        vo.setTotalStudentCount(intValue(userChannelBindingLookupMapper.countTotalStudentByChannel(channelId)));
        vo.setOrderCount(intValue(orderMainLookupMapper.countOrdersByMonth(
                channelId, range.getStartTime(), range.getEndTime())));
        vo.setOrderPaidAmount(decimalValue(orderMainLookupMapper.sumPaidAmountByMonth(
                channelId, range.getStartTime(), range.getEndTime())));
        vo.setRebateEstimatedAmount(decimalValue(channelDashboardMapperExt.sumEstimatedRebateByMonth(
                channelId, range.getStartTime(), range.getEndTime())));
        return vo;
    }

    @Override
    public List<DashboardStudentVO> selectStudentList(ChannelDashboardQueryDTO query) {
        ChannelDashboardQueryDTO finalQuery = query == null ? new ChannelDashboardQueryDTO() : query;
        finalQuery.setChannelId(requireCurrentChannelId());
        normalizeKeyword(finalQuery);
        com.ruoyi.common.utils.PageUtils.startPage();
        List<DashboardStudentVO> rows = userChannelBindingLookupMapper.selectStudentList(finalQuery);
        if (rows == null) {
            return new ArrayList<>();
        }
        for (DashboardStudentVO row : rows) {
            row.setTotalOrderCount(intValue(row.getTotalOrderCount()));
            row.setTotalPaidAmount(decimalValue(row.getTotalPaidAmount()));
        }
        return rows;
    }

    @Override
    public List<DashboardOrderVO> selectOrderList(ChannelDashboardQueryDTO query) {
        ChannelDashboardQueryDTO finalQuery = query == null ? new ChannelDashboardQueryDTO() : query;
        finalQuery.setChannelId(requireCurrentChannelId());
        applyDateRange(finalQuery);
        com.ruoyi.common.utils.PageUtils.startPage();
        List<DashboardOrderVO> rows = orderMainLookupMapper.selectOrderList(finalQuery);
        if (rows == null) {
            return new ArrayList<>();
        }
        for (DashboardOrderVO row : rows) {
            row.setPayAmount(decimalValue(row.getPayAmount()));
        }
        return rows;
    }

    @Override
    public DashboardStatsVO getStats(ChannelDashboardQueryDTO query) {
        Long channelId = requireCurrentChannelId();
        String period = resolvePeriod(query == null ? null : query.getPeriod());
        PeriodRange range = periodRange(period);

        Map<String, Map<String, Object>> orderMap = toStatMap(orderMainLookupMapper.selectOrderTrend(
                channelId, range.getStartTime(), range.getEndTime()));
        Map<String, Map<String, Object>> rebateMap = toStatMap(channelDashboardMapperExt.selectRebateTrend(
                channelId, range.getStartTime(), range.getEndTime()));

        DashboardStatsVO vo = new DashboardStatsVO();
        vo.setPeriod(period);
        vo.setOrderTrend(buildOrderTrend(range.getStartDate(), range.getEndDateInclusive(), orderMap));
        vo.setRebateTrend(buildRebateTrend(range.getStartDate(), range.getEndDateInclusive(), rebateMap));
        vo.setTopContests(buildTopContests(orderMainLookupMapper.selectTopContests(
                channelId, range.getStartTime(), range.getEndTime())));
        return vo;
    }

    private void normalizeKeyword(ChannelDashboardQueryDTO query) {
        if (query == null || query.getKeyword() == null) {
            return;
        }
        query.setKeyword(query.getKeyword().trim());
    }

    private void applyDateRange(ChannelDashboardQueryDTO query) {
        if (query == null) {
            return;
        }
        if (StringUtils.isBlank(query.getStartDate()) && StringUtils.isBlank(query.getEndDate())) {
            return;
        }
        LocalDate startDate = StringUtils.isBlank(query.getStartDate()) ? null : parseDate(query.getStartDate());
        LocalDate endDate = StringUtils.isBlank(query.getEndDate()) ? null : parseDate(query.getEndDate());
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        query.setStartTime(startDate == null ? null
                : Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        query.setEndTime(endDate == null ? null
                : Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private YearMonth resolveYearMonth(String rawYearMonth) {
        try {
            return StringUtils.isBlank(rawYearMonth) ? YearMonth.now() : YearMonth.parse(rawYearMonth);
        } catch (Exception ex) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private String resolvePeriod(String rawPeriod) {
        return StringUtils.isBlank(rawPeriod) ? "last30d" : rawPeriod;
    }

    private DateRange monthRange(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.plusMonths(1).atDay(1);
        return new DateRange(
                Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        );
    }

    private PeriodRange periodRange(String period) {
        LocalDate endDateInclusive = LocalDate.now();
        LocalDate startDate;
        if ("last7d".equals(period)) {
            startDate = endDateInclusive.minusDays(6);
        } else if ("last90d".equals(period)) {
            startDate = endDateInclusive.minusDays(89);
        } else {
            startDate = endDateInclusive.minusDays(29);
        }
        return new PeriodRange(
                startDate,
                endDateInclusive,
                Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(endDateInclusive.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        );
    }

    private List<DashboardStatsVO.DashboardTrendPointVO> buildOrderTrend(LocalDate startDate,
                                                                         LocalDate endDateInclusive,
                                                                         Map<String, Map<String, Object>> statMap) {
        List<DashboardStatsVO.DashboardTrendPointVO> points = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDateInclusive); date = date.plusDays(1)) {
            String day = date.format(DAY_FORMATTER);
            Map<String, Object> row = statMap.get(day);
            DashboardStatsVO.DashboardTrendPointVO point = new DashboardStatsVO.DashboardTrendPointVO();
            point.setDate(day);
            point.setCount(intValue(row == null ? null : row.get("orderCount")));
            point.setAmount(decimalValue(row == null ? null : row.get("orderAmount")));
            points.add(point);
        }
        return points;
    }

    private List<DashboardStatsVO.DashboardTrendPointVO> buildRebateTrend(LocalDate startDate,
                                                                          LocalDate endDateInclusive,
                                                                          Map<String, Map<String, Object>> statMap) {
        List<DashboardStatsVO.DashboardTrendPointVO> points = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDateInclusive); date = date.plusDays(1)) {
            String day = date.format(DAY_FORMATTER);
            Map<String, Object> row = statMap.get(day);
            DashboardStatsVO.DashboardTrendPointVO point = new DashboardStatsVO.DashboardTrendPointVO();
            point.setDate(day);
            point.setCount(0);
            point.setAmount(decimalValue(row == null ? null : row.get("rebateAmount")));
            points.add(point);
        }
        return points;
    }

    private List<DashboardStatsVO.TopContestVO> buildTopContests(List<Map<String, Object>> rows) {
        List<DashboardStatsVO.TopContestVO> result = new ArrayList<>();
        if (rows == null || rows.isEmpty()) {
            return result;
        }
        for (Map<String, Object> row : rows) {
            DashboardStatsVO.TopContestVO item = new DashboardStatsVO.TopContestVO();
            item.setContestId(longValue(row.get("contestId")));
            item.setContestName(row.get("contestName") == null ? null : String.valueOf(row.get("contestName")));
            item.setOrderCount(intValue(row.get("orderCount")));
            result.add(item);
        }
        return result;
    }

    private Map<String, Map<String, Object>> toStatMap(List<Map<String, Object>> rows) {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        if (rows == null || rows.isEmpty()) {
            return result;
        }
        for (Map<String, Object> row : rows) {
            if (row == null || row.get("statDate") == null) {
                continue;
            }
            result.put(String.valueOf(row.get("statDate")), row);
        }
        return result;
    }

    private Long requireCurrentChannelId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        Map<String, Object> channel = channelLookupMapper.selectCurrentByUserId(userId);
        if (channel == null || channel.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_NOT_AUTHED.message(),
                    BizErrorCode.JST_CHANNEL_NOT_AUTHED.code());
        }
        String authStatus = channel.get("authStatus") == null ? null : String.valueOf(channel.get("authStatus"));
        Integer status = channel.get("status") instanceof Number number ? number.intValue() : null;
        if (!"approved".equals(authStatus) || status == null || status != 1) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_NOT_AUTHED.message(),
                    BizErrorCode.JST_CHANNEL_NOT_AUTHED.code());
        }
        return ((Number) channel.get("channelId")).longValue();
    }

    private LocalDate parseDate(String rawDate) {
        try {
            return LocalDate.parse(rawDate);
        } catch (Exception ex) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private int intValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private Long longValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private BigDecimal decimalValue(Object value) {
        if (value == null) {
            return ZERO_AMOUNT;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.setScale(2, RoundingMode.HALF_UP);
        }
        if (value instanceof Number number) {
            return new BigDecimal(number.toString()).setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP);
    }

    private static class DateRange {

        private final Date startTime;
        private final Date endTime;

        private DateRange(Date startTime, Date endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public Date getStartTime() {
            return startTime;
        }

        public Date getEndTime() {
            return endTime;
        }
    }

    private static class PeriodRange {

        private final LocalDate startDate;
        private final LocalDate endDateInclusive;
        private final Date startTime;
        private final Date endTime;

        private PeriodRange(LocalDate startDate, LocalDate endDateInclusive, Date startTime, Date endTime) {
            this.startDate = startDate;
            this.endDateInclusive = endDateInclusive;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDateInclusive() {
            return endDateInclusive;
        }

        public Date getStartTime() {
            return startTime;
        }

        public Date getEndTime() {
            return endTime;
        }
    }
}
