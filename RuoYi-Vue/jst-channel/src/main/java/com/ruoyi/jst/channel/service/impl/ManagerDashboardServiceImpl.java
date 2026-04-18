package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.jst.channel.mapper.JstSalesMapper;
import com.ruoyi.jst.channel.service.ManagerDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManagerDashboardServiceImpl implements ManagerDashboardService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JstSalesMapper salesMapper;

    @Override
    public Map<String, Object> aggregate(Long managerSalesId, String month) {
        YearMonth ym = (month == null || month.isEmpty()) ? YearMonth.now() : YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.plusMonths(1).atDay(1);

        // admin: null → 全部；主管: 返自己下属
        List<Long> scopeIds = managerSalesId == null ? null : salesMapper.selectActiveTeamIds(managerSalesId);

        Map<String, Object> overview = new HashMap<>();
        overview.put("teamCount", scopeIds == null ? countAllSales() : scopeIds.size());
        overview.put("orderCount", sumOrderCount(scopeIds, start, end));
        overview.put("gmv", sumGmv(scopeIds, start, end));
        overview.put("followupCount", sumFollowup(scopeIds, start, end));
        overview.put("tagDistribution", tagDistribution(scopeIds));

        List<Map<String, Object>> ranking = salesRanking(scopeIds, start, end);
        List<Map<String, Object>> healthAlerts = healthAlerts(scopeIds);

        Map<String, Object> result = new HashMap<>();
        result.put("overview", overview);
        result.put("ranking", ranking);
        result.put("healthAlerts", healthAlerts);
        return result;
    }

    private int countAllSales() {
        Integer n = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM jst_sales WHERE status='active'", Integer.class);
        return n == null ? 0 : n;
    }

    private int sumOrderCount(List<Long> ids, LocalDate s, LocalDate e) {
        String sql = "SELECT COUNT(DISTINCT order_id) FROM jst_sales_commission_ledger WHERE create_time >= ? AND create_time < ?";
        if (ids != null && !ids.isEmpty()) sql += " AND sales_id IN (" + idList(ids) + ")";
        Integer n = jdbcTemplate.queryForObject(sql, Integer.class, Date.valueOf(s), Date.valueOf(e));
        return n == null ? 0 : n;
    }

    private BigDecimal sumGmv(List<Long> ids, LocalDate s, LocalDate e) {
        String sql = "SELECT COALESCE(SUM(base_amount), 0) FROM jst_sales_commission_ledger WHERE create_time >= ? AND create_time < ?";
        if (ids != null && !ids.isEmpty()) sql += " AND sales_id IN (" + idList(ids) + ")";
        BigDecimal v = jdbcTemplate.queryForObject(sql, BigDecimal.class, Date.valueOf(s), Date.valueOf(e));
        return v == null ? BigDecimal.ZERO : v;
    }

    private int sumFollowup(List<Long> ids, LocalDate s, LocalDate e) {
        String sql = "SELECT COUNT(*) FROM jst_sales_followup_record WHERE followup_at >= ? AND followup_at < ?";
        if (ids != null && !ids.isEmpty()) sql += " AND sales_id IN (" + idList(ids) + ")";
        Integer n = jdbcTemplate.queryForObject(sql, Integer.class, Date.valueOf(s), Date.valueOf(e));
        return n == null ? 0 : n;
    }

    private List<Map<String, Object>> tagDistribution(List<Long> ids) {
        String sql = "SELECT t.tag_code AS name, COUNT(*) AS count FROM jst_sales_channel_tag t " +
                     "JOIN jst_sales_channel_binding b ON b.channel_id = t.channel_id AND b.effective_to IS NULL ";
        if (ids != null && !ids.isEmpty()) {
            sql += "WHERE b.sales_id IN (" + idList(ids) + ") ";
        }
        sql += "GROUP BY t.tag_code ORDER BY count DESC LIMIT 8";
        return jdbcTemplate.queryForList(sql);
    }

    private List<Map<String, Object>> salesRanking(List<Long> ids, LocalDate s, LocalDate e) {
        String sql = "SELECT l.sales_id AS salesId, s.sales_name AS salesName, " +
                     "COUNT(DISTINCT l.order_id) AS orderCount, " +
                     "COALESCE(SUM(l.base_amount), 0) AS gmv, " +
                     "COALESCE(SUM(l.amount), 0) AS commissionAmount " +
                     "FROM jst_sales_commission_ledger l " +
                     "LEFT JOIN jst_sales s ON s.sales_id = l.sales_id " +
                     "WHERE l.create_time >= ? AND l.create_time < ? ";
        if (ids != null && !ids.isEmpty()) sql += "AND l.sales_id IN (" + idList(ids) + ") ";
        sql += "GROUP BY l.sales_id, s.sales_name ORDER BY commissionAmount DESC LIMIT 10";
        return jdbcTemplate.queryForList(sql, Date.valueOf(s), Date.valueOf(e));
    }

    private List<Map<String, Object>> healthAlerts(List<Long> ids) {
        String sql = "SELECT s.sales_id AS salesId, s.sales_name AS salesName, " +
                     "CONCAT(s.sales_name, ' 连续 3 周无业绩') AS message " +
                     "FROM jst_sales s WHERE s.status='active' " +
                     "AND NOT EXISTS (SELECT 1 FROM jst_sales_commission_ledger l WHERE l.sales_id = s.sales_id " +
                     "AND l.create_time >= DATE_SUB(NOW(), INTERVAL 21 DAY)) ";
        if (ids != null && !ids.isEmpty()) sql += "AND s.sales_id IN (" + idList(ids) + ") ";
        sql += "LIMIT 20";
        return jdbcTemplate.queryForList(sql);
    }

    private String idList(List<Long> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(ids.get(i));
        }
        return sb.toString();
    }
}
