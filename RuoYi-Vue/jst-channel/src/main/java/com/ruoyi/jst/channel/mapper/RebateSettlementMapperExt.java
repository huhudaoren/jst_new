package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstRebateSettlement;
import com.ruoyi.jst.channel.dto.WithdrawQueryReqDTO;
import com.ruoyi.jst.channel.vo.WithdrawAdminDetailVO;
import com.ruoyi.jst.channel.vo.WithdrawAdminListVO;
import com.ruoyi.jst.channel.vo.WithdrawListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
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

    List<WithdrawAdminListVO> selectAdminList(WithdrawQueryReqDTO query);

    WithdrawAdminDetailVO selectAdminDetail(@Param("settlementId") Long settlementId);

    int approveSettlement(@Param("settlementId") Long settlementId,
                          @Param("expectedStatus") String expectedStatus,
                          @Param("negativeOffsetAmount") BigDecimal negativeOffsetAmount,
                          @Param("actualPayAmount") BigDecimal actualPayAmount,
                          @Param("auditRemark") String auditRemark,
                          @Param("updateBy") String updateBy,
                          @Param("updateTime") Date updateTime);

    int rejectSettlement(@Param("settlementId") Long settlementId,
                         @Param("expectedStatus") String expectedStatus,
                         @Param("auditRemark") String auditRemark,
                         @Param("updateBy") String updateBy,
                         @Param("updateTime") Date updateTime);

    int markPaidSettlement(@Param("settlementId") Long settlementId,
                           @Param("expectedStatus") String expectedStatus,
                           @Param("payVoucherUrl") String payVoucherUrl,
                           @Param("payTime") Date payTime,
                           @Param("updateBy") String updateBy,
                           @Param("updateTime") Date updateTime);
}
