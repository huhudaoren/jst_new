package com.ruoyi.jst.points.mapper;

import java.util.List;
import com.ruoyi.jst.points.domain.JstMallExchangeOrder;

/**
 * 积分商城兑换订单Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstMallExchangeOrderMapper 
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
     * 删除积分商城兑换订单
     * 
     * @param exchangeId 积分商城兑换订单主键
     * @return 结果
     */
    public int deleteJstMallExchangeOrderByExchangeId(Long exchangeId);

    /**
     * 批量删除积分商城兑换订单
     * 
     * @param exchangeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstMallExchangeOrderByExchangeIds(Long[] exchangeIds);
}
