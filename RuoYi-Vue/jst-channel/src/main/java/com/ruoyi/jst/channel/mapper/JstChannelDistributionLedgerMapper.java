package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstChannelDistributionLedger;
import java.util.List;

public interface JstChannelDistributionLedgerMapper {
    JstChannelDistributionLedger selectJstChannelDistributionLedgerByLedgerId(Long ledgerId);
    List<JstChannelDistributionLedger> selectJstChannelDistributionLedgerList(JstChannelDistributionLedger query);
    int insertJstChannelDistributionLedger(JstChannelDistributionLedger row);
    int updateJstChannelDistributionLedger(JstChannelDistributionLedger row);
    int deleteJstChannelDistributionLedgerByLedgerId(Long ledgerId);
}
