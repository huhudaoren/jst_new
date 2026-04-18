package com.ruoyi.jst.channel.service;

import java.util.List;
import java.util.Map;

/**
 * 销售管理 Dashboard 聚合 Service（admin 端）。
 * <p>
 * 聚合表：jst_sales / jst_sales_commission_ledger / jst_sales_followup_record
 *
 * @author jst
 * @since 1.0.0
 */
public interface AdminSalesDashboardService {

    /**
     * 平台月度概览。
     *
     * @param month 格式 YYYY-MM（为 null 时取当月）
     * @return { activeSalesCount, monthOrderCount, monthGmv, monthCommissionCost, salesRanking }
     */
    Map<String, Object> getOverview(String month);

    /**
     * 单销售月度详情。
     *
     * @param salesId 销售 ID
     * @param month   格式 YYYY-MM（为 null 时取当月）
     * @return { salesId, salesName, orderCount, gmv, commission, byBusinessType[] }
     */
    Map<String, Object> getSalesDetail(Long salesId, String month);

    /**
     * 各销售跟进活动概览。
     *
     * @param month 格式 YYYY-MM（为 null 时取当月）
     * @return [ { salesId, salesName, followupCount, coveredChannelCount } ]
     */
    List<Map<String, Object>> getFollowupActivity(String month);

    /**
     * 提成成本趋势。
     *
     * @param bucket       聚合粒度：day / week / month
     * @param startDate    开始日期（yyyy-MM-dd，含）
     * @param endDate      结束日期（yyyy-MM-dd，含）
     * @param businessType 业务类型（可选）
     * @param region       地区关键字（可选）
     * @return [ { bucket, commissionTotal, orderNetTotal, costRate } ]
     */
    List<Map<String, Object>> getCommissionTrend(String bucket,
                                                 String startDate,
                                                 String endDate,
                                                 String businessType,
                                                 String region);

    /**
     * J 上限压缩统计。
     *
     * @param startDate    开始日期（yyyy-MM-dd，含）
     * @param endDate      结束日期（yyyy-MM-dd，含）
     * @param businessType 业务类型（可选）
     * @param region       地区关键字（可选）
     * @return { totalCount, triggeredCount, triggerRate, compressedAmount, originalTotal, compressedRate }
     */
    Map<String, Object> getCompressionStats(String startDate,
                                            String endDate,
                                            String businessType,
                                            String region);

    /**
     * 渠道业绩热力图。
     *
     * @param startDate    开始日期（yyyy-MM-dd，含）
     * @param endDate      结束日期（yyyy-MM-dd，含）
     * @param businessType 业务类型（可选）
     * @param region       地区关键字（可选）
     * @return [ { region, businessType, channelCount, gmv } ]
     */
    List<Map<String, Object>> getChannelHeatmap(String startDate,
                                                String endDate,
                                                String businessType,
                                                String region);
}
