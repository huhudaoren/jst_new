package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstMallGoodsMapper;
import com.ruoyi.system.domain.JstMallGoods;
import com.ruoyi.system.service.IJstMallGoodsService;

/**
 * 积分商城商品Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstMallGoodsServiceImpl implements IJstMallGoodsService 
{
    @Autowired
    private JstMallGoodsMapper jstMallGoodsMapper;

    /**
     * 查询积分商城商品
     * 
     * @param goodsId 积分商城商品主键
     * @return 积分商城商品
     */
    @Override
    public JstMallGoods selectJstMallGoodsByGoodsId(Long goodsId)
    {
        return jstMallGoodsMapper.selectJstMallGoodsByGoodsId(goodsId);
    }

    /**
     * 查询积分商城商品列表
     * 
     * @param jstMallGoods 积分商城商品
     * @return 积分商城商品
     */
    @Override
    public List<JstMallGoods> selectJstMallGoodsList(JstMallGoods jstMallGoods)
    {
        return jstMallGoodsMapper.selectJstMallGoodsList(jstMallGoods);
    }

    /**
     * 新增积分商城商品
     * 
     * @param jstMallGoods 积分商城商品
     * @return 结果
     */
    @Override
    public int insertJstMallGoods(JstMallGoods jstMallGoods)
    {
        jstMallGoods.setCreateTime(DateUtils.getNowDate());
        return jstMallGoodsMapper.insertJstMallGoods(jstMallGoods);
    }

    /**
     * 修改积分商城商品
     * 
     * @param jstMallGoods 积分商城商品
     * @return 结果
     */
    @Override
    public int updateJstMallGoods(JstMallGoods jstMallGoods)
    {
        jstMallGoods.setUpdateTime(DateUtils.getNowDate());
        return jstMallGoodsMapper.updateJstMallGoods(jstMallGoods);
    }

    /**
     * 批量删除积分商城商品
     * 
     * @param goodsIds 需要删除的积分商城商品主键
     * @return 结果
     */
    @Override
    public int deleteJstMallGoodsByGoodsIds(Long[] goodsIds)
    {
        return jstMallGoodsMapper.deleteJstMallGoodsByGoodsIds(goodsIds);
    }

    /**
     * 删除积分商城商品信息
     * 
     * @param goodsId 积分商城商品主键
     * @return 结果
     */
    @Override
    public int deleteJstMallGoodsByGoodsId(Long goodsId)
    {
        return jstMallGoodsMapper.deleteJstMallGoodsByGoodsId(goodsId);
    }
}
