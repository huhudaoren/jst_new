package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.JstMallGoods;

/**
 * 积分商城商品Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstMallGoodsMapper 
{
    /**
     * 查询积分商城商品
     * 
     * @param goodsId 积分商城商品主键
     * @return 积分商城商品
     */
    public JstMallGoods selectJstMallGoodsByGoodsId(Long goodsId);

    /**
     * 查询积分商城商品列表
     * 
     * @param jstMallGoods 积分商城商品
     * @return 积分商城商品集合
     */
    public List<JstMallGoods> selectJstMallGoodsList(JstMallGoods jstMallGoods);

    /**
     * 新增积分商城商品
     * 
     * @param jstMallGoods 积分商城商品
     * @return 结果
     */
    public int insertJstMallGoods(JstMallGoods jstMallGoods);

    /**
     * 修改积分商城商品
     * 
     * @param jstMallGoods 积分商城商品
     * @return 结果
     */
    public int updateJstMallGoods(JstMallGoods jstMallGoods);

    /**
     * 删除积分商城商品
     * 
     * @param goodsId 积分商城商品主键
     * @return 结果
     */
    public int deleteJstMallGoodsByGoodsId(Long goodsId);

    /**
     * 批量删除积分商城商品
     * 
     * @param goodsIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstMallGoodsByGoodsIds(Long[] goodsIds);
}
