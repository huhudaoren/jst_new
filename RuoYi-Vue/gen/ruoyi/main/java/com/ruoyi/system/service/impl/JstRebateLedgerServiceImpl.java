package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstRebateLedgerMapper;
import com.ruoyi.system.domain.JstRebateLedger;
import com.ruoyi.system.service.IJstRebateLedgerService;

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
}
