package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.dto.RebateLedgerQueryReqDTO;
import com.ruoyi.jst.channel.dto.WithdrawApplyDTO;
import com.ruoyi.jst.channel.dto.WithdrawQueryReqDTO;
import com.ruoyi.jst.channel.vo.RebateLedgerListVO;
import com.ruoyi.jst.channel.vo.RebateSummaryVO;
import com.ruoyi.jst.channel.vo.WithdrawApplyResVO;
import com.ruoyi.jst.channel.vo.WithdrawDetailVO;
import com.ruoyi.jst.channel.vo.WithdrawListVO;

import java.util.List;

/**
 * Channel withdrawal service.
 */
public interface ChannelWithdrawService {

    RebateSummaryVO getSummary();

    List<RebateLedgerListVO> selectLedgerList(RebateLedgerQueryReqDTO query);

    WithdrawApplyResVO apply(WithdrawApplyDTO req);

    void cancel(Long settlementId);

    List<WithdrawListVO> selectMyWithdrawList(WithdrawQueryReqDTO query);

    WithdrawDetailVO getWithdrawDetail(Long settlementId);
}
