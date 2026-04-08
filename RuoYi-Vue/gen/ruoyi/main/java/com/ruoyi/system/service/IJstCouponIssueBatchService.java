package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstCouponIssueBatch;

/**
 * 发券批次Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstCouponIssueBatchService 
{
    /**
     * 查询发券批次
     * 
     * @param batchId 发券批次主键
     * @return 发券批次
     */
    public JstCouponIssueBatch selectJstCouponIssueBatchByBatchId(Long batchId);

    /**
     * 查询发券批次列表
     * 
     * @param jstCouponIssueBatch 发券批次
     * @return 发券批次集合
     */
    public List<JstCouponIssueBatch> selectJstCouponIssueBatchList(JstCouponIssueBatch jstCouponIssueBatch);

    /**
     * 新增发券批次
     * 
     * @param jstCouponIssueBatch 发券批次
     * @return 结果
     */
    public int insertJstCouponIssueBatch(JstCouponIssueBatch jstCouponIssueBatch);

    /**
     * 修改发券批次
     * 
     * @param jstCouponIssueBatch 发券批次
     * @return 结果
     */
    public int updateJstCouponIssueBatch(JstCouponIssueBatch jstCouponIssueBatch);

    /**
     * 批量删除发券批次
     * 
     * @param batchIds 需要删除的发券批次主键集合
     * @return 结果
     */
    public int deleteJstCouponIssueBatchByBatchIds(Long[] batchIds);

    /**
     * 删除发券批次信息
     * 
     * @param batchId 发券批次主键
     * @return 结果
     */
    public int deleteJstCouponIssueBatchByBatchId(Long batchId);
}
