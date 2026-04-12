package com.ruoyi.jst.channel.service.impl;

import java.util.List;
import java.util.Date;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.channel.enums.RebateLedgerStatus;
import com.ruoyi.jst.channel.mapper.RebateLedgerMapperExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.jst.channel.mapper.JstRebateLedgerMapper;
import com.ruoyi.jst.channel.domain.JstRebateLedger;
import com.ruoyi.jst.channel.service.IJstRebateLedgerService;

/**
 * 返点计提台账Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstRebateLedgerServiceImpl implements IJstRebateLedgerService 
{
    @Autowired
    private JstRebateLedgerMapper jstRebateLedgerMapper;

    @Autowired
    private RebateLedgerMapperExt rebateLedgerMapperExt;

    /**
     * 查询返点计提台账
     * 
     * @param ledgerId 返点计提台账主键
     * @return 返点计提台账
     */
    @Override
    public JstRebateLedger selectJstRebateLedgerByLedgerId(Long ledgerId)
    {
        return jstRebateLedgerMapper.selectJstRebateLedgerByLedgerId(ledgerId);
    }

    /**
     * 查询返点计提台账列表
     * 
     * @param jstRebateLedger 返点计提台账
     * @return 返点计提台账
     */
    @Override
    public List<JstRebateLedger> selectJstRebateLedgerList(JstRebateLedger jstRebateLedger)
    {
        return jstRebateLedgerMapper.selectJstRebateLedgerList(jstRebateLedger);
    }

    /**
     * 新增返点计提台账
     * 
     * @param jstRebateLedger 返点计提台账
     * @return 结果
     */
    @Override
    public int insertJstRebateLedger(JstRebateLedger jstRebateLedger)
    {
        jstRebateLedger.setCreateTime(DateUtils.getNowDate());
        return jstRebateLedgerMapper.insertJstRebateLedger(jstRebateLedger);
    }

    /**
     * 修改返点计提台账
     * 
     * @param jstRebateLedger 返点计提台账
     * @return 结果
     */
    @Override
    public int updateJstRebateLedger(JstRebateLedger jstRebateLedger)
    {
        jstRebateLedger.setUpdateTime(DateUtils.getNowDate());
        return jstRebateLedgerMapper.updateJstRebateLedger(jstRebateLedger);
    }

    /**
     * 批量删除返点计提台账
     * 
     * @param ledgerIds 需要删除的返点计提台账主键
     * @return 结果
     */
    @Override
    public int deleteJstRebateLedgerByLedgerIds(Long[] ledgerIds)
    {
        return jstRebateLedgerMapper.deleteJstRebateLedgerByLedgerIds(ledgerIds);
    }

    /**
     * 删除返点计提台账信息
     * 
     * @param ledgerId 返点计提台账主键
     * @return 结果
     */
    @Override
    public int deleteJstRebateLedgerByLedgerId(Long ledgerId)
    {
        return jstRebateLedgerMapper.deleteJstRebateLedgerByLedgerId(ledgerId);
    }

    /**
     * 系统任务自动计提（SM-8）。
     *
     * @param ledgerId 台账ID
     * @param remark   计提备注
     * @return true=计提成功；false=状态已变化或不满足计提条件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean settlePendingAccrual(Long ledgerId, String remark)
    {
        JstRebateLedger ledger = rebateLedgerMapperExt.selectByLedgerId(ledgerId);
        if (ledger == null)
        {
            return false;
        }
        RebateLedgerStatus current = RebateLedgerStatus.fromDb(ledger.getStatus());
        if (current != RebateLedgerStatus.PENDING_ACCRUAL)
        {
            return false;
        }
        // SM-8: pending_accrual -> withdrawable
        current.assertCanTransitTo(RebateLedgerStatus.WITHDRAWABLE);
        Date now = DateUtils.getNowDate();
        int updated = rebateLedgerMapperExt.settleToWithdrawable(ledgerId, RebateLedgerStatus.PENDING_ACCRUAL.dbValue(),
                RebateLedgerStatus.WITHDRAWABLE.dbValue(), now, now, "system", now, remark);
        return updated > 0;
    }
}
