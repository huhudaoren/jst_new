package com.ruoyi.jst.channel.mapper;

import java.util.List;
import com.ruoyi.jst.channel.domain.JstRebateLedger;

/**
 * 返点计提台账Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstRebateLedgerMapper 
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
     * 删除返点计提台账
     * 
     * @param ledgerId 返点计提台账主键
     * @return 结果
     */
    public int deleteJstRebateLedgerByLedgerId(Long ledgerId);

    /**
     * 批量删除返点计提台账
     * 
     * @param ledgerIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstRebateLedgerByLedgerIds(Long[] ledgerIds);
}
