package com.ruoyi.jst.points.mapper;

import java.util.List;
import com.ruoyi.jst.points.domain.JstPointsLedger;

/**
 * 积分流水Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstPointsLedgerMapper 
{
    /**
     * 查询积分流水
     * 
     * @param ledgerId 积分流水主键
     * @return 积分流水
     */
    public JstPointsLedger selectJstPointsLedgerByLedgerId(Long ledgerId);

    /**
     * 查询积分流水列表
     * 
     * @param jstPointsLedger 积分流水
     * @return 积分流水集合
     */
    public List<JstPointsLedger> selectJstPointsLedgerList(JstPointsLedger jstPointsLedger);

    /**
     * 新增积分流水
     * 
     * @param jstPointsLedger 积分流水
     * @return 结果
     */
    public int insertJstPointsLedger(JstPointsLedger jstPointsLedger);

    /**
     * 修改积分流水
     * 
     * @param jstPointsLedger 积分流水
     * @return 结果
     */
    public int updateJstPointsLedger(JstPointsLedger jstPointsLedger);

    /**
     * 删除积分流水
     * 
     * @param ledgerId 积分流水主键
     * @return 结果
     */
    public int deleteJstPointsLedgerByLedgerId(Long ledgerId);

    /**
     * 批量删除积分流水
     * 
     * @param ledgerIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstPointsLedgerByLedgerIds(Long[] ledgerIds);
}
