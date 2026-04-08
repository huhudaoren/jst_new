package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstOrderMainMapper;
import com.ruoyi.system.domain.JstOrderMain;
import com.ruoyi.system.service.IJstOrderMainService;

/**
 * 订单主Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstOrderMainServiceImpl implements IJstOrderMainService 
{
    @Autowired
    private JstOrderMainMapper jstOrderMainMapper;

    /**
     * 查询订单主
     * 
     * @param orderId 订单主主键
     * @return 订单主
     */
    @Override
    public JstOrderMain selectJstOrderMainByOrderId(Long orderId)
    {
        return jstOrderMainMapper.selectJstOrderMainByOrderId(orderId);
    }

    /**
     * 查询订单主列表
     * 
     * @param jstOrderMain 订单主
     * @return 订单主
     */
    @Override
    public List<JstOrderMain> selectJstOrderMainList(JstOrderMain jstOrderMain)
    {
        return jstOrderMainMapper.selectJstOrderMainList(jstOrderMain);
    }

    /**
     * 新增订单主
     * 
     * @param jstOrderMain 订单主
     * @return 结果
     */
    @Override
    public int insertJstOrderMain(JstOrderMain jstOrderMain)
    {
        jstOrderMain.setCreateTime(DateUtils.getNowDate());
        return jstOrderMainMapper.insertJstOrderMain(jstOrderMain);
    }

    /**
     * 修改订单主
     * 
     * @param jstOrderMain 订单主
     * @return 结果
     */
    @Override
    public int updateJstOrderMain(JstOrderMain jstOrderMain)
    {
        jstOrderMain.setUpdateTime(DateUtils.getNowDate());
        return jstOrderMainMapper.updateJstOrderMain(jstOrderMain);
    }

    /**
     * 批量删除订单主
     * 
     * @param orderIds 需要删除的订单主主键
     * @return 结果
     */
    @Override
    public int deleteJstOrderMainByOrderIds(Long[] orderIds)
    {
        return jstOrderMainMapper.deleteJstOrderMainByOrderIds(orderIds);
    }

    /**
     * 删除订单主信息
     * 
     * @param orderId 订单主主键
     * @return 结果
     */
    @Override
    public int deleteJstOrderMainByOrderId(Long orderId)
    {
        return jstOrderMainMapper.deleteJstOrderMainByOrderId(orderId);
    }
}
