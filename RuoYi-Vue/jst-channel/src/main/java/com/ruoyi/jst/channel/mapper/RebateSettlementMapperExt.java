package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstRebateSettlement;
import com.ruoyi.jst.channel.dto.WithdrawQueryReqDTO;
import com.ruoyi.jst.channel.vo.WithdrawListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Extended rebate settlement mapper.
 */
@Mapper
public interface RebateSettlementMapperExt {

    JstRebateSettlement selectBySettlementIdForUpdate(@Param("settlementId") Long settlementId);

    int updateStatusByExpected(@Param("settlementId") Long settlementId,
                               @Param("channelId") Long channelId,
                               @Param("expectedStatus") String expectedStatus,
                               @Param("targetStatus") String targetStatus,
                               @Param("updateBy") String updateBy,
                               @Param("updateTime") Date updateTime);

    List<WithdrawListVO> selectMyList(WithdrawQueryReqDTO query);
}
