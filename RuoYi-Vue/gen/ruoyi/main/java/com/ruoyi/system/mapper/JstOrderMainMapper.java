package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.JstOrderMain;

/**
 * 订单主Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstOrderMainMapper 
{
    /**
     * 查询订单主
     * 
     * @param orderId 订单主主键
     * @return 订单主
     */
    public JstOrderMain selectJstOrderMainByOrderId(Long orderId);

    /**
     * 查询订单主列表
     * 
     * @param jstOrderMain 订单主
     * @return 订单主集合
     */
    public List<JstOrderMain> selectJstOrderMainList(JstOrderMain jstOrderMain);

    /**
     * 新增订单主
     * 
     * @param jstOrderMain 订单主
     * @return 结果
     */
    public int insertJstOrderMain(JstOrderMain jstOrderMain);

    /**
     * 修改订单主
     * 
     * @param jstOrderMain 订单主
     * @return 结果
     */
    public int updateJstOrderMain(JstOrderMain jstOrderMain);

    /**
     * 删除订单主
     * 
     * @param orderId 订单主主键
     * @return 结果
     */
    public int deleteJstOrderMainByOrderId(Long orderId);

    /**
     * 批量删除订单主
     * 
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstOrderMainByOrderIds(Long[] orderIds);
}
