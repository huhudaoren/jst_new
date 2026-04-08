package com.ruoyi.jst.channel.service;

import java.util.List;
import com.ruoyi.jst.channel.domain.JstRebateSettlement;

/**
 * 渠道提现/结算单Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstRebateSettlementService 
{
    /**
     * 查询渠道提现/结算单
     * 
     * @param settlementId 渠道提现/结算单主键
     * @return 渠道提现/结算单
     */
    public JstRebateSettlement selectJstRebateSettlementBySettlementId(Long settlementId);

    /**
     * 查询渠道提现/结算单列表
     * 
     * @param jstRebateSettlement 渠道提现/结算单
     * @return 渠道提现/结算单集合
     */
    public List<JstRebateSettlement> selectJstRebateSettlementList(JstRebateSettlement jstRebateSettlement);

    /**
     * 新增渠道提现/结算单
     * 
     * @param jstRebateSettlement 渠道提现/结算单
     * @return 结果
     */
    public int insertJstRebateSettlement(JstRebateSettlement jstRebateSettlement);

    /**
     * 修改渠道提现/结算单
     * 
     * @param jstRebateSettlement 渠道提现/结算单
     * @return 结果
     */
    public int updateJstRebateSettlement(JstRebateSettlement jstRebateSettlement);

    /**
     * 批量删除渠道提现/结算单
     * 
     * @param settlementIds 需要删除的渠道提现/结算单主键集合
     * @return 结果
     */
    public int deleteJstRebateSettlementBySettlementIds(Long[] settlementIds);

    /**
     * 删除渠道提现/结算单信息
     * 
     * @param settlementId 渠道提现/结算单主键
     * @return 结果
     */
    public int deleteJstRebateSettlementBySettlementId(Long settlementId);
}
