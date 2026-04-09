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

    List<RebateLedgerListVO> selectBySettlementId(@Param("settlementId") Long settlementId);
}
