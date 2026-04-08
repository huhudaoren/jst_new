package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstEventSettlement;

/**
 * 赛事方结算单Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstEventSettlementService 
{
    /**
     * 查询赛事方结算单
     * 
     * @param eventSettlementId 赛事方结算单主键
     * @return 赛事方结算单
     */
    public JstEventSettlement selectJstEventSettlementByEventSettlementId(Long eventSettlementId);

    /**
     * 查询赛事方结算单列表
     * 
     * @param jstEventSettlement 赛事方结算单
     * @return 赛事方结算单集合
     */
    public List<JstEventSettlement> selectJstEventSettlementList(JstEventSettlement jstEventSettlement);

    /**
     * 新增赛事方结算单
     * 
     * @param jstEventSettlement 赛事方结算单
     * @return 结果
     */
    public int insertJstEventSettlement(JstEventSettlement jstEventSettlement);

    /**
     * 修改赛事方结算单
     * 
     * @param jstEventSettlement 赛事方结算单
     * @return 结果
     */
    public int updateJstEventSettlement(JstEventSettlement jstEventSettlement);

    /**
     * 批量删除赛事方结算单
     * 
     * @param eventSettlementIds 需要删除的赛事方结算单主键集合
     * @return 结果
     */
    public int deleteJstEventSettlementByEventSettlementIds(Long[] eventSettlementIds);

    /**
     * 删除赛事方结算单信息
     * 
     * @param eventSettlementId 赛事方结算单主键
     * @return 结果
     */
    public int deleteJstEventSettlementByEventSettlementId(Long eventSettlementId);
}
