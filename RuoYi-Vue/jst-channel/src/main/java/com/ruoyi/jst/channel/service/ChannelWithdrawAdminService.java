package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.dto.WithdrawAuditDTO;
import com.ruoyi.jst.channel.dto.WithdrawQueryReqDTO;
import com.ruoyi.jst.channel.vo.WithdrawAdminDetailVO;
import com.ruoyi.jst.channel.vo.WithdrawAdminListVO;

import java.util.List;

/**
 * Admin withdrawal audit service.
 */
public interface ChannelWithdrawAdminService {

    List<WithdrawAdminListVO> selectAdminList(WithdrawQueryReqDTO query);

    WithdrawAdminDetailVO getAdminDetail(Long settlementId);

    void approve(Long settlementId, WithdrawAuditDTO req);

    void reject(Long settlementId, WithdrawAuditDTO req);

    void execute(Long settlementId);
}
