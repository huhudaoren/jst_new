package com.ruoyi.jst.points.service;

import java.util.List;
import com.ruoyi.jst.points.domain.JstGrowthLedger;

/**
 * 成长值流水Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstGrowthLedgerService 
{
    /**
     * 查询成长值流水
     * 
     * @param ledgerId 成长值流水主键
     * @return 成长值流水
     */
    public JstGrowthLedger selectJstGrowthLedgerByLedgerId(Long ledgerId);

    /**
     * 查询成长值流水列表
     * 
     * @param jstGrowthLedger 成长值流水
     * @return 成长值流水集合
     */
    public List<JstGrowthLedger> selectJstGrowthLedgerList(JstGrowthLedger jstGrowthLedger);

    /**
     * 新增成长值流水
     * 
     * @param jstGrowthLedger 成长值流水
     * @return 结果
     */
    public int insertJstGrowthLedger(JstGrowthLedger jstGrowthLedger);

    /**
     * 修改成长值流水
     * 
     * @param jstGrowthLedger 成长值流水
     * @return 结果
     */
    public int updateJstGrowthLedger(JstGrowthLedger jstGrowthLedger);

    /**
     * 批量删除成长值流水
     * 
     * @param ledgerIds 需要删除的成长值流水主键集合
     * @return 结果
     */
    public int deleteJstGrowthLedgerByLedgerIds(Long[] ledgerIds);

    /**
     * 删除成长值流水信息
     * 
     * @param ledgerId 成长值流水主键
     * @return 结果
     */
    public int deleteJstGrowthLedgerByLedgerId(Long ledgerId);
}
