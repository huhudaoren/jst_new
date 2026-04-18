package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSalesCommissionLedger;
import com.ruoyi.jst.channel.domain.JstSalesCommissionSettlement;
import com.ruoyi.jst.channel.mapper.JstSalesCommissionLedgerMapper;
import com.ruoyi.jst.channel.mapper.JstSalesCommissionSettlementMapper;
import com.ruoyi.jst.channel.mapper.JstSalesMapper;
import com.ruoyi.jst.channel.service.SalesCommissionSettlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class SalesCommissionSettlementServiceImpl implements SalesCommissionSettlementService {

    private static final Logger log = LoggerFactory.getLogger(SalesCommissionSettlementServiceImpl.class);

    @Autowired private JstSalesCommissionSettlementMapper settlementMapper;
    @Autowired private JstSalesCommissionLedgerMapper ledgerMapper;
    @Autowired private JstSalesMapper salesMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateLastMonthSettlements() {
        LocalDate firstDayThisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);
        Date periodStart = Date.from(firstDayLastMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date periodEnd = Date.from(firstDayThisMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        String period = String.format("%04d-%02d", firstDayLastMonth.getYear(), firstDayLastMonth.getMonthValue());

        List<Long> salesIds = salesMapper.selectActiveSalesIdsForSettlement();
        if (salesIds == null || salesIds.isEmpty()) return 0;

        int generated = 0;
        for (Long salesId : salesIds) {
            // 幂等：同一销售同一期间不重复生成
            JstSalesCommissionSettlement exists = settlementMapper.selectBySalesAndPeriod(salesId, period);
            if (exists != null) continue;

            List<JstSalesCommissionLedger> rows = ledgerMapper.selectAccruedByPeriod(salesId, periodStart, periodEnd);
            if (rows == null || rows.isEmpty()) continue;

            BigDecimal total = rows.stream()
                    .map(JstSalesCommissionLedger::getAmount)
                    .filter(a -> a != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            JstSalesCommissionSettlement s = new JstSalesCommissionSettlement();
            s.setSettlementId(snowId());
            s.setSalesId(salesId);
            s.setPeriod(period);
            s.setTotalCount(rows.size());
            s.setTotalAmount(total);
            s.setStatus("pending_review");
            settlementMapper.insertJstSalesCommissionSettlement(s);

            ledgerMapper.bindToSettlement(salesId, s.getSettlementId(), periodStart, periodEnd);
            generated++;
            log.info("[Settlement] sales={} period={} 生成月结单 {} 笔 ¥{}", salesId, period, rows.size(), total);
        }
        return generated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long settlementId, Long approverUserId) {
        JstSalesCommissionSettlement s = require(settlementId);
        if (!"pending_review".equals(s.getStatus())) {
            throw new ServiceException("仅待审核月结单可通过");
        }
        s.setStatus("approved");
        s.setApprovedBy(approverUserId);
        s.setApprovedAt(new Date());
        settlementMapper.updateJstSalesCommissionSettlement(s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long settlementId, String reason) {
        JstSalesCommissionSettlement s = require(settlementId);
        if (!"pending_review".equals(s.getStatus())) {
            throw new ServiceException("仅待审核月结单可驳回");
        }
        s.setStatus("rejected");
        s.setRejectReason(reason);
        settlementMapper.updateJstSalesCommissionSettlement(s);
        // ledger 退回 accrued，下月重新汇总
        ledgerMapper.rollbackSettledToAccrued(settlementId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordPayment(Long settlementId, String voucher) {
        JstSalesCommissionSettlement s = require(settlementId);
        if (!"approved".equals(s.getStatus())) {
            throw new ServiceException("仅已审核月结单可录入打款");
        }
        s.setStatus("paid");
        s.setPaidAt(new Date());
        s.setPayVoucher(voucher);
        settlementMapper.updateJstSalesCommissionSettlement(s);
        // ledger 推到 paid
        ledgerMapper.markSettledAsPaid(settlementId);
    }

    @Override
    public List<JstSalesCommissionSettlement> listForAdmin(JstSalesCommissionSettlement query) {
        return settlementMapper.selectJstSalesCommissionSettlementList(query);
    }

    private JstSalesCommissionSettlement require(Long id) {
        JstSalesCommissionSettlement s = settlementMapper.selectJstSalesCommissionSettlementBySettlementId(id);
        if (s == null) throw new ServiceException("月结单不存在: " + id);
        return s;
    }

    private Long snowId() {
        return System.currentTimeMillis() * 10_000L + new Random().nextInt(10_000);
    }
}
