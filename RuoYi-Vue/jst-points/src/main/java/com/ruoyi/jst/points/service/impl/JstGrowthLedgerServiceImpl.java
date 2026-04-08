package com.ruoyi.jst.points.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.points.mapper.JstGrowthLedgerMapper;
import com.ruoyi.jst.points.domain.JstGrowthLedger;
import com.ruoyi.jst.points.service.IJstGrowthLedgerService;

/**
 * 成长值流水Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstGrowthLedgerServiceImpl implements IJstGrowthLedgerService 
{
    @Autowired
    private JstGrowthLedgerMapper jstGrowthLedgerMapper;

    /**
     * 查询成长值流水
     * 
     * @param ledgerId 成长值流水主键
     * @return 成长值流水
     */
    @Override
    public JstGrowthLedger selectJstGrowthLedgerByLedgerId(Long ledgerId)
    {
        return jstGrowthLedgerMapper.selectJstGrowthLedgerByLedgerId(ledgerId);
    }

    /**
     * 查询成长值流水列表
     * 
     * @param jstGrowthLedger 成长值流水
     * @return 成长值流水
     */
    @Override
    public List<JstGrowthLedger> selectJstGrowthLedgerList(JstGrowthLedger jstGrowthLedger)
    {
        return jstGrowthLedgerMapper.selectJstGrowthLedgerList(jstGrowthLedger);
    }

    /**
     * 新增成长值流水
     * 
     * @param jstGrowthLedger 成长值流水
     * @return 结果
     */
    @Override
    public int insertJstGrowthLedger(JstGrowthLedger jstGrowthLedger)
    {
        jstGrowthLedger.setCreateTime(DateUtils.getNowDate());
        return jstGrowthLedgerMapper.insertJstGrowthLedger(jstGrowthLedger);
    }

    /**
     * 修改成长值流水
     * 
     * @param jstGrowthLedger 成长值流水
     * @return 结果
     */
    @Override
    public int updateJstGrowthLedger(JstGrowthLedger jstGrowthLedger)
    {
        jstGrowthLedger.setUpdateTime(DateUtils.getNowDate());
        return jstGrowthLedgerMapper.updateJstGrowthLedger(jstGrowthLedger);
    }

    /**
     * 批量删除成长值流水
     * 
     * @param ledgerIds 需要删除的成长值流水主键
     * @return 结果
     */
    @Override
    public int deleteJstGrowthLedgerByLedgerIds(Long[] ledgerIds)
    {
        return jstGrowthLedgerMapper.deleteJstGrowthLedgerByLedgerIds(ledgerIds);
    }

    /**
     * 删除成长值流水信息
     * 
     * @param ledgerId 成长值流水主键
     * @return 结果
     */
    @Override
    public int deleteJstGrowthLedgerByLedgerId(Long ledgerId)
    {
        return jstGrowthLedgerMapper.deleteJstGrowthLedgerByLedgerId(ledgerId);
    }
}
