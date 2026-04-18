package com.ruoyi.jst.channel.service;

import java.math.BigDecimal;
import java.util.Date;

public interface SalesCommissionRateService {
    /**
     * 查销售在某业务类型在某时刻的费率，回退默认 commission_rate_default。
     * 业务类型未配明细 → 返销售的 commissionRateDefault。
     */
    BigDecimal resolveRate(Long salesId, String businessType, Date atTime);

    /** 设置某业务类型的费率（写入 effective_from=now 行） */
    void upsertRate(Long salesId, String businessType, BigDecimal rate);
}
