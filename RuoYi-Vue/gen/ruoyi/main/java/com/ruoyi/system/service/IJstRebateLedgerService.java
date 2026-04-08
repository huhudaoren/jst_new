package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstRebateLedger;

/**
 * 返点计提台账Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstRebateLedgerService 
{
    /**
     * 查询返点计提台账
     * 
     * @param ledgerId 返点计提台账主键
     * @return 返点计提台账
     */
    public JstRebateLedger selectJstRebateLedgerByLedgerId(Long ledgerId);

    /**
     * 查询返点计提台账列表
     * 
     * @param jstRebateLedger 返点计提台账
     * @return 返点计提台账集合
     */
    public List<JstRebateLedger> selectJstRebateLedgerList(JstRebateLedger jstRebateLedger);

    /**
     * 新增返点计提台账
     * 
     * @param jstRebateLedger 返点计提台账
     * @return 结果
     */
    public int insertJstRebateLedger(JstRebateLedger jstRebateLedger);

    /**
     * 修改返点计提台账
     * 
     * @param jstRebateLedger 返点计提台账
     * @return 结果
     */
    public int updateJstRebateLedger(JstRebateLedger jstRebateLedger);

    /**
     * 批量删除返点计提台账
     * 
     * @param ledgerIds 需要删除的返点计提台账主键集合
     * @return 结果
     */
    public int deleteJstRebateLedgerByLedgerIds(Long[] ledgerIds);

    /**
     * 删除返点计提台账信息
     * 
     * @param ledgerId 返点计提台账主键
     * @return 结果
     */
    public int deleteJstRebateLedgerByLedgerId(Long ledgerId);
}
