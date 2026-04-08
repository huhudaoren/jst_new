package com.ruoyi.jst.marketing.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.marketing.mapper.JstCouponIssueBatchMapper;
import com.ruoyi.jst.marketing.domain.JstCouponIssueBatch;
import com.ruoyi.jst.marketing.service.IJstCouponIssueBatchService;

/**
 * 发券批次Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstCouponIssueBatchServiceImpl implements IJstCouponIssueBatchService 
{
    @Autowired
    private JstCouponIssueBatchMapper jstCouponIssueBatchMapper;

    /**
     * 查询发券批次
     * 
     * @param batchId 发券批次主键
     * @return 发券批次
     */
    @Override
    public JstCouponIssueBatch selectJstCouponIssueBatchByBatchId(Long batchId)
    {
        return jstCouponIssueBatchMapper.selectJstCouponIssueBatchByBatchId(batchId);
    }

    /**
     * 查询发券批次列表
     * 
     * @param jstCouponIssueBatch 发券批次
     * @return 发券批次
     */
    @Override
    public List<JstCouponIssueBatch> selectJstCouponIssueBatchList(JstCouponIssueBatch jstCouponIssueBatch)
    {
        return jstCouponIssueBatchMapper.selectJstCouponIssueBatchList(jstCouponIssueBatch);
    }

    /**
     * 新增发券批次
     * 
     * @param jstCouponIssueBatch 发券批次
     * @return 结果
     */
    @Override
    public int insertJstCouponIssueBatch(JstCouponIssueBatch jstCouponIssueBatch)
    {
        jstCouponIssueBatch.setCreateTime(DateUtils.getNowDate());
        return jstCouponIssueBatchMapper.insertJstCouponIssueBatch(jstCouponIssueBatch);
    }

    /**
     * 修改发券批次
     * 
     * @param jstCouponIssueBatch 发券批次
     * @return 结果
     */
    @Override
    public int updateJstCouponIssueBatch(JstCouponIssueBatch jstCouponIssueBatch)
    {
        jstCouponIssueBatch.setUpdateTime(DateUtils.getNowDate());
        return jstCouponIssueBatchMapper.updateJstCouponIssueBatch(jstCouponIssueBatch);
    }

    /**
     * 批量删除发券批次
     * 
     * @param batchIds 需要删除的发券批次主键
     * @return 结果
     */
    @Override
    public int deleteJstCouponIssueBatchByBatchIds(Long[] batchIds)
    {
        return jstCouponIssueBatchMapper.deleteJstCouponIssueBatchByBatchIds(batchIds);
    }

    /**
     * 删除发券批次信息
     * 
     * @param batchId 发券批次主键
     * @return 结果
     */
    @Override
    public int deleteJstCouponIssueBatchByBatchId(Long batchId)
    {
        return jstCouponIssueBatchMapper.deleteJstCouponIssueBatchByBatchId(batchId);
    }
}
