package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstRebateLedger;
import com.ruoyi.jst.channel.dto.RebateLedgerQueryReqDTO;
import com.ruoyi.jst.channel.vo.RebateLedgerListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Extended rebate ledger mapper.
 */
@Mapper
public interface RebateLedgerMapperExt {

    Map<String, Object> selectSummaryByChannelId(@Param("channelId") Long channelId);

    JstRebateLedger selectByLedgerId(@Param("ledgerId") Long ledgerId);

    List<Long> selectAutoSettleLedgerIds(@Param("deadlineTime") Date deadlineTime,
                                         @Param("lastLedgerId") Long lastLedgerId,
                                         @Param("limit") Integer limit);

    int settleToWithdrawable(@Param("ledgerId") Long ledgerId,
                             @Param("expectedStatus") String expectedStatus,
                             @Param("targetStatus") String targetStatus,
                             @Param("deadlineTime") Date deadlineTime,
                             @Param("accrualTime") Date accrualTime,
                             @Param("updateBy") String updateBy,
                             @Param("updateTime") Date updateTime,
                             @Param("remark") String remark);

    List<RebateLedgerListVO> selectLedgerList(RebateLedgerQueryReqDTO query);

    List<JstRebateLedger> selectByLedgerIdsForUpdate(@Param("ledgerIds") List<Long> ledgerIds);

    int markInReview(@Param("channelId") Long channelId,
                     @Param("settlementId") Long settlementId,
                     @Param("ledgerIds") List<Long> ledgerIds,
                     @Param("updateBy") String updateBy,
                     @Param("updateTime") Date updateTime);

    int restoreWithdrawableBySettlementId(@Param("channelId") Long channelId,
                                          @Param("settlementId") Long settlementId,
                                          @Param("updateBy") String updateBy,
                                          @Param("updateTime") Date updateTime);

    List<JstRebateLedger> selectUnboundNegativeByChannelId(@Param("channelId") Long channelId);

    List<JstRebateLedger> selectUnboundNegativeByChannelIdForUpdate(@Param("channelId") Long channelId);

    int bindSettlementToNegativeLedgers(@Param("channelId") Long channelId,
                                        @Param("settlementId") Long settlementId,
                                        @Param("ledgerIds") List<Long> ledgerIds,
                                        @Param("updateBy") String updateBy,
                                        @Param("updateTime") Date updateTime);

    int markPaidBySettlementId(@Param("channelId") Long channelId,
                               @Param("settlementId") Long settlementId,
                               @Param("updateBy") String updateBy,
                               @Param("updateTime") Date updateTime);

    List<RebateLedgerListVO> selectBySettlementId(@Param("settlementId") Long settlementId);
}
