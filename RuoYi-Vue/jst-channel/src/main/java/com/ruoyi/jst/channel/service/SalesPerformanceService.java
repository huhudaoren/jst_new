package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.dto.SalesPerformanceVO;

public interface SalesPerformanceService {

    /**
     * 聚合销售业绩。
     * @param salesId   销售 ID
     * @param yearMonth 可选 YYYY-MM；null 表示本月
     */
    SalesPerformanceVO aggregate(Long salesId, String yearMonth);
}
