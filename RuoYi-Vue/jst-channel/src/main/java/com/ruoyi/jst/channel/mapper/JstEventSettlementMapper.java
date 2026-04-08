package com.ruoyi.jst.channel.mapper;

import java.util.List;
import com.ruoyi.jst.channel.domain.JstEventSettlement;

/**
 * 赛事方结算单Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstEventSettlementMapper 
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
     * 删除赛事方结算单
     * 
     * @param eventSettlementId 赛事方结算单主键
     * @return 结果
     */
    public int deleteJstEventSettlementByEventSettlementId(Long eventSettlementId);

    /**
     * 批量删除赛事方结算单
     * 
     * @param eventSettlementIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstEventSettlementByEventSettlementIds(Long[] eventSettlementIds);
}
