package com.ruoyi.jst.order.service;

import java.util.List;
import com.ruoyi.jst.order.domain.JstOrderItem;

/**
 * 订单明细（最小核算单元，承载分摊与回滚）Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstOrderItemService 
{
    /**
     * 查询订单明细（最小核算单元，承载分摊与回滚）
     * 
     * @param itemId 订单明细（最小核算单元，承载分摊与回滚）主键
     * @return 订单明细（最小核算单元，承载分摊与回滚）
     */
    public JstOrderItem selectJstOrderItemByItemId(Long itemId);

    /**
     * 查询订单明细（最小核算单元，承载分摊与回滚）列表
     * 
     * @param jstOrderItem 订单明细（最小核算单元，承载分摊与回滚）
     * @return 订单明细（最小核算单元，承载分摊与回滚）集合
     */
    public List<JstOrderItem> selectJstOrderItemList(JstOrderItem jstOrderItem);

    /**
     * 新增订单明细（最小核算单元，承载分摊与回滚）
     * 
     * @param jstOrderItem 订单明细（最小核算单元，承载分摊与回滚）
     * @return 结果
     */
    public int insertJstOrderItem(JstOrderItem jstOrderItem);

    /**
     * 修改订单明细（最小核算单元，承载分摊与回滚）
     * 
     * @param jstOrderItem 订单明细（最小核算单元，承载分摊与回滚）
     * @return 结果
     */
    public int updateJstOrderItem(JstOrderItem jstOrderItem);

    /**
     * 批量删除订单明细（最小核算单元，承载分摊与回滚）
     * 
     * @param itemIds 需要删除的订单明细（最小核算单元，承载分摊与回滚）主键集合
     * @return 结果
     */
    public int deleteJstOrderItemByItemIds(Long[] itemIds);

    /**
     * 删除订单明细（最小核算单元，承载分摊与回滚）信息
     * 
     * @param itemId 订单明细（最小核算单元，承载分摊与回滚）主键
     * @return 结果
     */
    public int deleteJstOrderItemByItemId(Long itemId);
}
