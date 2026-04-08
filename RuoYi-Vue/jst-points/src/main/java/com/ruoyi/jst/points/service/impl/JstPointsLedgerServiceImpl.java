package com.ruoyi.jst.points.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.points.mapper.JstPointsLedgerMapper;
import com.ruoyi.jst.points.domain.JstPointsLedger;
import com.ruoyi.jst.points.service.IJstPointsLedgerService;

/**
 * 积分流水Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstPointsLedgerServiceImpl implements IJstPointsLedgerService 
{
    @Autowired
    private JstPointsLedgerMapper jstPointsLedgerMapper;

    /**
     * 查询积分流水
     * 
     * @param ledgerId 积分流水主键
     * @return 积分流水
     */
    @Override
    public JstPointsLedger selectJstPointsLedgerByLedgerId(Long ledgerId)
    {
        return jstPointsLedgerMapper.selectJstPointsLedgerByLedgerId(ledgerId);
    }

    /**
     * 查询积分流水列表
     * 
     * @param jstPointsLedger 积分流水
     * @return 积分流水
     */
    @Override
    public List<JstPointsLedger> selectJstPointsLedgerList(JstPointsLedger jstPointsLedger)
    {
        return jstPointsLedgerMapper.selectJstPointsLedgerList(jstPointsLedger);
    }

    /**
     * 新增积分流水
     * 
     * @param jstPointsLedger 积分流水
     * @return 结果
     */
    @Override
    public int insertJstPointsLedger(JstPointsLedger jstPointsLedger)
    {
        jstPointsLedger.setCreateTime(DateUtils.getNowDate());
        return jstPointsLedgerMapper.insertJstPointsLedger(jstPointsLedger);
    }

    /**
     * 修改积分流水
     * 
     * @param jstPointsLedger 积分流水
     * @return 结果
     */
    @Override
    public int updateJstPointsLedger(JstPointsLedger jstPointsLedger)
    {
        jstPointsLedger.setUpdateTime(DateUtils.getNowDate());
        return jstPointsLedgerMapper.updateJstPointsLedger(jstPointsLedger);
    }

    /**
     * 批量删除积分流水
     * 
     * @param ledgerIds 需要删除的积分流水主键
     * @return 结果
     */
    @Override
    public int deleteJstPointsLedgerByLedgerIds(Long[] ledgerIds)
    {
        return jstPointsLedgerMapper.deleteJstPointsLedgerByLedgerIds(ledgerIds);
    }

    /**
     * 删除积分流水信息
     * 
     * @param ledgerId 积分流水主键
     * @return 结果
     */
    @Override
    public int deleteJstPointsLedgerByLedgerId(Long ledgerId)
    {
        return jstPointsLedgerMapper.deleteJstPointsLedgerByLedgerId(ledgerId);
    }
}
