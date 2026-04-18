package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstChannelDistributionLedger;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface JstChannelDistributionLedgerMapper {
    JstChannelDistributionLedger selectJstChannelDistributionLedgerByLedgerId(Long ledgerId);
    List<JstChannelDistributionLedger> selectJstChannelDistributionLedgerList(JstChannelDistributionLedger query);
    int insertJstChannelDistributionLedger(JstChannelDistributionLedger row);
    int updateJstChannelDistributionLedger(JstChannelDistributionLedger row);
    int deleteJstChannelDistributionLedgerByLedgerId(Long ledgerId);

    /**
     * 批量插入（事务内一次写入）。
     */
    int batchInsert(List<JstChannelDistributionLedger> rows);

    /**
     * 查询 accrue_at &lt;= NOW() 且 status=pending 的行（Quartz 推进用）。
     *
     * @param limit 最大处理行数（防止单次跑太久）
     */
    List<JstChannelDistributionLedger> selectPendingForAccrue(@Param("limit") int limit);

    /**
     * 批量将 status 置为 accrued，设 accrued_at=NOW()。
     *
     * @param ledgerIds 要推进的 ledger_id 列表
     */
    int markAccruedBatch(@Param("ledgerIds") List<Long> ledgerIds);

    /**
     * 将某订单的所有 pending ledger 置为 cancelled（退款用）。
     *
     * @param orderId 订单 ID
     * @return 撤销行数
     */
    int cancelPendingByOrder(@Param("orderId") Long orderId);

    /**
     * 按上级渠道查分销 ledger（可按 status 过滤，null 则不过滤）。
     *
     * @param inviterChannelId 上级渠道 ID
     * @param status           状态过滤（null=不过滤）
     */
    List<JstChannelDistributionLedger> selectByInviter(@Param("inviterChannelId") Long inviterChannelId,
                                                       @Param("status") String status);

    /**
     * 汇总上级渠道的分销收益（可按 status 过滤，null 则汇总所有）。
     *
     * @param inviterChannelId 上级渠道 ID
     * @param status           状态过滤（null=不过滤）
     * @return 总金额（无记录时返回 null）
     */
    BigDecimal sumAmountByInviter(@Param("inviterChannelId") Long inviterChannelId,
                                   @Param("status") String status);

    /**
     * 汇总上级渠道的本月分销收益（accrue_at &gt;= 本月 1 日 00:00）。
     *
     * @param inviterChannelId 上级渠道 ID
     * @return 本月累计金额（无记录时返回 null）
     */
    BigDecimal sumMonthAmountByInviter(@Param("inviterChannelId") Long inviterChannelId);
}
