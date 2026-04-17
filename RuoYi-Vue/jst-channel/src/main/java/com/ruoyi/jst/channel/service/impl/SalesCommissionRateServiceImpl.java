package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.domain.JstSalesCommissionRate;
import com.ruoyi.jst.channel.mapper.JstSalesCommissionRateMapper;
import com.ruoyi.jst.channel.service.SalesCommissionRateService;
import com.ruoyi.jst.channel.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

@Service
public class SalesCommissionRateServiceImpl implements SalesCommissionRateService {

    @Autowired
    private JstSalesCommissionRateMapper rateMapper;

    @Autowired
    private SalesService salesService;

    @Override
    public BigDecimal resolveRate(Long salesId, String businessType, Date atTime) {
        if (salesId == null || businessType == null || atTime == null) {
            throw new ServiceException("resolveRate 参数缺失");
        }
        BigDecimal rate = rateMapper.selectLatestRate(salesId, businessType, atTime);
        if (rate != null) return rate;
        // 回退默认费率
        JstSales s = salesService.getById(salesId);
        if (s == null) throw new ServiceException("销售不存在: " + salesId);
        return s.getCommissionRateDefault() == null ? BigDecimal.ZERO : s.getCommissionRateDefault();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertRate(Long salesId, String businessType, BigDecimal rate) {
        if (salesId == null || businessType == null || rate == null) {
            throw new ServiceException("upsertRate 参数缺失");
        }
        JstSalesCommissionRate row = new JstSalesCommissionRate();
        row.setId(snowId());
        row.setSalesId(salesId);
        row.setBusinessType(businessType);
        row.setRate(rate);
        row.setEffectiveFrom(new Date());
        rateMapper.insertJstSalesCommissionRate(row);
    }

    private Long snowId() {
        return System.currentTimeMillis() * 10_000L + new Random().nextInt(10_000);
    }
}
