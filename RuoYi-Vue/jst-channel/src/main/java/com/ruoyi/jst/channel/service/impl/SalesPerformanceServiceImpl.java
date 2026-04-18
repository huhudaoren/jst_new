package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.jst.channel.dto.SalesPerformanceVO;
import com.ruoyi.jst.channel.dto.SalesPerformanceVO.SalesPerformanceByTypeVO;
import com.ruoyi.jst.channel.mapper.JstSalesCommissionLedgerMapper;
import com.ruoyi.jst.channel.service.SalesPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SalesPerformanceServiceImpl implements SalesPerformanceService {

    @Autowired
    private JstSalesCommissionLedgerMapper ledgerMapper;

    @Override
    public SalesPerformanceVO aggregate(Long salesId, String yearMonth) {
        YearMonth ym = (yearMonth == null || yearMonth.isEmpty())
                ? YearMonth.now()
                : YearMonth.parse(yearMonth);
        Date start = Date.from(ym.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(ym.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Map<String, Object> m = ledgerMapper.aggregatePerformance(salesId, start, end);
        SalesPerformanceVO vo = new SalesPerformanceVO();
        if (m != null) {
            vo.setOrderCount(intValue(m.get("orderCount")));
            vo.setChannelCount(intValue(m.get("channelCount")));
            vo.setTotalGmv(decimalValue(m.get("totalGmv")));
            vo.setTotalCommission(decimalValue(m.get("totalCommission")));
            vo.setPendingCommission(decimalValue(m.get("pendingCommission")));
            vo.setSettledCommission(decimalValue(m.get("settledCommission")));
        }

        List<Map<String, Object>> byTypeRaw = ledgerMapper.aggregateByBusinessType(salesId, start, end);
        List<SalesPerformanceByTypeVO> byType = new ArrayList<>();
        if (byTypeRaw != null) {
            for (Map<String, Object> row : byTypeRaw) {
                SalesPerformanceByTypeVO t = new SalesPerformanceByTypeVO();
                t.setBusinessType(strValue(row.get("businessType")));
                t.setOrderCount(intValue(row.get("orderCount")));
                t.setGmv(decimalValue(row.get("gmv")));
                t.setCommission(decimalValue(row.get("commission")));
                byType.add(t);
            }
        }
        vo.setByType(byType);
        return vo;
    }

    private Integer intValue(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(v.toString()); } catch (NumberFormatException e) { return 0; }
    }

    private BigDecimal decimalValue(Object v) {
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal b) return b;
        if (v instanceof Number n) return new BigDecimal(n.toString());
        try { return new BigDecimal(v.toString()); } catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }

    private String strValue(Object v) { return v == null ? null : v.toString(); }
}
