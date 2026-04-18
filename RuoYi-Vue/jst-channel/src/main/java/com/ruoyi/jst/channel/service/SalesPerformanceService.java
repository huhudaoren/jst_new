package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.dto.SalesPerformanceVO;

import java.util.List;
import java.util.Map;

public interface SalesPerformanceService {

    /**
     * 聚合销售业绩。
     * @param salesId   销售 ID
     * @param yearMonth 可选 YYYY-MM；null 表示本月
     */
    SalesPerformanceVO aggregate(Long salesId, String yearMonth);

    /**
     * 超过 days 天未跟进的渠道列表。salesId=null 时查全部。
     */
    List<Map<String, Object>> listInactiveChannels(Long salesId, int days);

    /**
     * days 天内即将过期的预录入列表。salesId=null 时查全部。
     */
    List<Map<String, Object>> listExpiringPreReg(Long salesId, int days);
}
