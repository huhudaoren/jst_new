package com.ruoyi.jst.points.service;

import java.util.List;
import com.ruoyi.jst.points.domain.JstMallExchangeOrder;

/**
 * 积分商城兑换订单Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstMallExchangeOrderService 
{
    /**
     * 查询积分商城兑换订单
     * 
     * @param exchangeId 积分商城兑换订单主键
     * @return 积分商城兑换订单
     */
    public JstMallExchangeOrder selectJstMallExchangeOrderByExchangeId(Long exchangeId);

    /**
     * 查询积分商城兑换订单列表
     * 
     * @param jstMallExchangeOrder 积分商城兑换订单
     * @return 积分商城兑换订单集合
     */
    public List<JstMallExchangeOrder> selectJstMallExchangeOrderList(JstMallExchangeOrder jstMallExchangeOrder);

    /**
     * 新增积分商城兑换订单
     * 
     * @param jstMallExchangeOrder 积分商城兑换订单
     * @return 结果
     */
    public int insertJstMallExchangeOrder(JstMallExchangeOrder jstMallExchangeOrder);

    /**
     * 修改积分商城兑换订单
     * 
     * @param jstMallExchangeOrder 积分商城兑换订单
     * @return 结果
     */
    public int updateJstMallExchangeOrder(JstMallExchangeOrder jstMallExchangeOrder);

    /**
     * 批量删除积分商城兑换订单
     * 
     * @param exchangeIds 需要删除的积分商城兑换订单主键集合
     * @return 结果
     */
    public int deleteJstMallExchangeOrderByExchangeIds(Long[] exchangeIds);

    /**
     * 删除积分商城兑换订单信息
     * 
     * @param exchangeId 积分商城兑换订单主键
     * @return 结果
     */
    public int deleteJstMallExchangeOrderByExchangeId(Long exchangeId);
}
