package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstRebateSettlementMapper;
import com.ruoyi.system.domain.JstRebateSettlement;
import com.ruoyi.system.service.IJstRebateSettlementService;

/**
 * 渠道提现/结算单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstRebateSettlementServiceImpl implements IJstRebateSettlementService 
{
    @Autowired
    private JstRebateSettlementMapper jstRebateSettlementMapper;

    /**
     * 查询渠道提现/结算单
     * 
     * @param settlementId 渠道提现/结算单主键
     * @return 渠道提现/结算单
     */
    @Override
    public JstRebateSettlement selectJstRebateSettlementBySettlementId(Long settlementId)
    {
        return jstRebateSettlementMapper.selectJstRebateSettlementBySettlementId(settlementId);
    }

    /**
     * 查询渠道提现/结算单列表
     * 
     * @param jstRebateSettlement 渠道提现/结算单
     * @return 渠道提现/结算单
     */
    @Override
    public List<JstRebateSettlement> selectJstRebateSettlementList(JstRebateSettlement jstRebateSettlement)
    {
        return jstRebateSettlementMapper.selectJstRebateSettlementList(jstRebateSettlement);
    }

    /**
     * 新增渠道提现/结算单
     * 
     * @param jstRebateSettlement 渠道提现/结算单
     * @return 结果
     */
    @Override
    public int insertJstRebateSettlement(JstRebateSettlement jstRebateSettlement)
    {
        jstRebateSettlement.setCreateTime(DateUtils.getNowDate());
        return jstRebateSettlementMapper.insertJstRebateSettlement(jstRebateSettlement);
    }

    /**
     * 修改渠道提现/结算单
     * 
     * @param jstRebateSettlement 渠道提现/结算单
     * @return 结果
     */
    @Override
    public int updateJstRebateSettlement(JstRebateSettlement jstRebateSettlement)
    {
        jstRebateSettlement.setUpdateTime(DateUtils.getNowDate());
        return jstRebateSettlementMapper.updateJstRebateSettlement(jstRebateSettlement);
    }

    /**
     * 批量删除渠道提现/结算单
     * 
     * @param settlementIds 需要删除的渠道提现/结算单主键
     * @return 结果
     */
    @Override
    public int deleteJstRebateSettlementBySettlementIds(Long[] settlementIds)
    {
        return jstRebateSettlementMapper.deleteJstRebateSettlementBySettlementIds(settlementIds);
    }

    /**
     * 删除渠道提现/结算单信息
     * 
     * @param settlementId 渠道提现/结算单主键
     * @return 结果
     */
    @Override
    public int deleteJstRebateSettlementBySettlementId(Long settlementId)
    {
        return jstRebateSettlementMapper.deleteJstRebateSettlementBySettlementId(settlementId);
    }
}
