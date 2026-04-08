package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstOrderItemMapper;
import com.ruoyi.system.domain.JstOrderItem;
import com.ruoyi.system.service.IJstOrderItemService;

/**
 * 订单明细（最小核算单元，承载分摊与回滚）Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstOrderItemServiceImpl implements IJstOrderItemService 
{
    @Autowired
    private JstOrderItemMapper jstOrderItemMapper;

    /**
     * 查询订单明细（最小核算单元，承载分摊与回滚）
     * 
     * @param itemId 订单明细（最小核算单元，承载分摊与回滚）主键
     * @return 订单明细（最小核算单元，承载分摊与回滚）
     */
    @Override
    public JstOrderItem selectJstOrderItemByItemId(Long itemId)
    {
        return jstOrderItemMapper.selectJstOrderItemByItemId(itemId);
    }

    /**
     * 查询订单明细（最小核算单元，承载分摊与回滚）列表
     * 
     * @param jstOrderItem 订单明细（最小核算单元，承载分摊与回滚）
     * @return 订单明细（最小核算单元，承载分摊与回滚）
     */
    @Override
    public List<JstOrderItem> selectJstOrderItemList(JstOrderItem jstOrderItem)
    {
        return jstOrderItemMapper.selectJstOrderItemList(jstOrderItem);
    }

    /**
     * 新增订单明细（最小核算单元，承载分摊与回滚）
     * 
     * @param jstOrderItem 订单明细（最小核算单元，承载分摊与回滚）
     * @return 结果
     */
    @Override
    public int insertJstOrderItem(JstOrderItem jstOrderItem)
    {
        jstOrderItem.setCreateTime(DateUtils.getNowDate());
        return jstOrderItemMapper.insertJstOrderItem(jstOrderItem);
    }

    /**
     * 修改订单明细（最小核算单元，承载分摊与回滚）
     * 
     * @param jstOrderItem 订单明细（最小核算单元，承载分摊与回滚）
     * @return 结果
     */
    @Override
    public int updateJstOrderItem(JstOrderItem jstOrderItem)
    {
        jstOrderItem.setUpdateTime(DateUtils.getNowDate());
        return jstOrderItemMapper.updateJstOrderItem(jstOrderItem);
    }

    /**
     * 批量删除订单明细（最小核算单元，承载分摊与回滚）
     * 
     * @param itemIds 需要删除的订单明细（最小核算单元，承载分摊与回滚）主键
     * @return 结果
     */
    @Override
    public int deleteJstOrderItemByItemIds(Long[] itemIds)
    {
        return jstOrderItemMapper.deleteJstOrderItemByItemIds(itemIds);
    }

    /**
     * 删除订单明细（最小核算单元，承载分摊与回滚）信息
     * 
     * @param itemId 订单明细（最小核算单元，承载分摊与回滚）主键
     * @return 结果
     */
    @Override
    public int deleteJstOrderItemByItemId(Long itemId)
    {
        return jstOrderItemMapper.deleteJstOrderItemByItemId(itemId);
    }
}
