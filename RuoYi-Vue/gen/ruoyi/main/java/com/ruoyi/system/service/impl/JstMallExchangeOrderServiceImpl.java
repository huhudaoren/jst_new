package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstMallExchangeOrderMapper;
import com.ruoyi.system.domain.JstMallExchangeOrder;
import com.ruoyi.system.service.IJstMallExchangeOrderService;

/**
 * 积分商城兑换订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstMallExchangeOrderServiceImpl implements IJstMallExchangeOrderService 
{
    @Autowired
    private JstMallExchangeOrderMapper jstMallExchangeOrderMapper;

    /**
     * 查询积分商城兑换订单
     * 
     * @param exchangeId 积分商城兑换订单主键
     * @return 积分商城兑换订单
     */
    @Override
    public JstMallExchangeOrder selectJstMallExchangeOrderByExchangeId(Long exchangeId)
    {
        return jstMallExchangeOrderMapper.selectJstMallExchangeOrderByExchangeId(exchangeId);
    }

    /**
     * 查询积分商城兑换订单列表
     * 
     * @param jstMallExchangeOrder 积分商城兑换订单
     * @return 积分商城兑换订单
     */
    @Override
    public List<JstMallExchangeOrder> selectJstMallExchangeOrderList(JstMallExchangeOrder jstMallExchangeOrder)
    {
        return jstMallExchangeOrderMapper.selectJstMallExchangeOrderList(jstMallExchangeOrder);
    }

    /**
     * 新增积分商城兑换订单
     * 
     * @param jstMallExchangeOrder 积分商城兑换订单
     * @return 结果
     */
    @Override
    public int insertJstMallExchangeOrder(JstMallExchangeOrder jstMallExchangeOrder)
    {
        jstMallExchangeOrder.setCreateTime(DateUtils.getNowDate());
        return jstMallExchangeOrderMapper.insertJstMallExchangeOrder(jstMallExchangeOrder);
    }

    /**
     * 修改积分商城兑换订单
     * 
     * @param jstMallExchangeOrder 积分商城兑换订单
     * @return 结果
     */
    @Override
    public int updateJstMallExchangeOrder(JstMallExchangeOrder jstMallExchangeOrder)
    {
        jstMallExchangeOrder.setUpdateTime(DateUtils.getNowDate());
        return jstMallExchangeOrderMapper.updateJstMallExchangeOrder(jstMallExchangeOrder);
    }

    /**
     * 批量删除积分商城兑换订单
     * 
     * @param exchangeIds 需要删除的积分商城兑换订单主键
     * @return 结果
     */
    @Override
    public int deleteJstMallExchangeOrderByExchangeIds(Long[] exchangeIds)
    {
        return jstMallExchangeOrderMapper.deleteJstMallExchangeOrderByExchangeIds(exchangeIds);
    }

    /**
     * 删除积分商城兑换订单信息
     * 
     * @param exchangeId 积分商城兑换订单主键
     * @return 结果
     */
    @Override
    public int deleteJstMallExchangeOrderByExchangeId(Long exchangeId)
    {
        return jstMallExchangeOrderMapper.deleteJstMallExchangeOrderByExchangeId(exchangeId);
    }
}
