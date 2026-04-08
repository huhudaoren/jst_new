package com.ruoyi.jst.channel.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.channel.mapper.JstEventSettlementMapper;
import com.ruoyi.jst.channel.domain.JstEventSettlement;
import com.ruoyi.jst.channel.service.IJstEventSettlementService;

/**
 * 赛事方结算单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstEventSettlementServiceImpl implements IJstEventSettlementService 
{
    @Autowired
    private JstEventSettlementMapper jstEventSettlementMapper;

    /**
     * 查询赛事方结算单
     * 
     * @param eventSettlementId 赛事方结算单主键
     * @return 赛事方结算单
     */
    @Override
    public JstEventSettlement selectJstEventSettlementByEventSettlementId(Long eventSettlementId)
    {
        return jstEventSettlementMapper.selectJstEventSettlementByEventSettlementId(eventSettlementId);
    }

    /**
     * 查询赛事方结算单列表
     * 
     * @param jstEventSettlement 赛事方结算单
     * @return 赛事方结算单
     */
    @Override
    public List<JstEventSettlement> selectJstEventSettlementList(JstEventSettlement jstEventSettlement)
    {
        return jstEventSettlementMapper.selectJstEventSettlementList(jstEventSettlement);
    }

    /**
     * 新增赛事方结算单
     * 
     * @param jstEventSettlement 赛事方结算单
     * @return 结果
     */
    @Override
    public int insertJstEventSettlement(JstEventSettlement jstEventSettlement)
    {
        jstEventSettlement.setCreateTime(DateUtils.getNowDate());
        return jstEventSettlementMapper.insertJstEventSettlement(jstEventSettlement);
    }

    /**
     * 修改赛事方结算单
     * 
     * @param jstEventSettlement 赛事方结算单
     * @return 结果
     */
    @Override
    public int updateJstEventSettlement(JstEventSettlement jstEventSettlement)
    {
        jstEventSettlement.setUpdateTime(DateUtils.getNowDate());
        return jstEventSettlementMapper.updateJstEventSettlement(jstEventSettlement);
    }

    /**
     * 批量删除赛事方结算单
     * 
     * @param eventSettlementIds 需要删除的赛事方结算单主键
     * @return 结果
     */
    @Override
    public int deleteJstEventSettlementByEventSettlementIds(Long[] eventSettlementIds)
    {
        return jstEventSettlementMapper.deleteJstEventSettlementByEventSettlementIds(eventSettlementIds);
    }

    /**
     * 删除赛事方结算单信息
     * 
     * @param eventSettlementId 赛事方结算单主键
     * @return 结果
     */
    @Override
    public int deleteJstEventSettlementByEventSettlementId(Long eventSettlementId)
    {
        return jstEventSettlementMapper.deleteJstEventSettlementByEventSettlementId(eventSettlementId);
    }
}
