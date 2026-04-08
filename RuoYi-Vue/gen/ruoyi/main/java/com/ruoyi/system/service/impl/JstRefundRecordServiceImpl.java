package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstRefundRecordMapper;
import com.ruoyi.system.domain.JstRefundRecord;
import com.ruoyi.system.service.IJstRefundRecordService;

/**
 * 退款/售后单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstRefundRecordServiceImpl implements IJstRefundRecordService 
{
    @Autowired
    private JstRefundRecordMapper jstRefundRecordMapper;

    /**
     * 查询退款/售后单
     * 
     * @param refundId 退款/售后单主键
     * @return 退款/售后单
     */
    @Override
    public JstRefundRecord selectJstRefundRecordByRefundId(Long refundId)
    {
        return jstRefundRecordMapper.selectJstRefundRecordByRefundId(refundId);
    }

    /**
     * 查询退款/售后单列表
     * 
     * @param jstRefundRecord 退款/售后单
     * @return 退款/售后单
     */
    @Override
    public List<JstRefundRecord> selectJstRefundRecordList(JstRefundRecord jstRefundRecord)
    {
        return jstRefundRecordMapper.selectJstRefundRecordList(jstRefundRecord);
    }

    /**
     * 新增退款/售后单
     * 
     * @param jstRefundRecord 退款/售后单
     * @return 结果
     */
    @Override
    public int insertJstRefundRecord(JstRefundRecord jstRefundRecord)
    {
        jstRefundRecord.setCreateTime(DateUtils.getNowDate());
        return jstRefundRecordMapper.insertJstRefundRecord(jstRefundRecord);
    }

    /**
     * 修改退款/售后单
     * 
     * @param jstRefundRecord 退款/售后单
     * @return 结果
     */
    @Override
    public int updateJstRefundRecord(JstRefundRecord jstRefundRecord)
    {
        jstRefundRecord.setUpdateTime(DateUtils.getNowDate());
        return jstRefundRecordMapper.updateJstRefundRecord(jstRefundRecord);
    }

    /**
     * 批量删除退款/售后单
     * 
     * @param refundIds 需要删除的退款/售后单主键
     * @return 结果
     */
    @Override
    public int deleteJstRefundRecordByRefundIds(Long[] refundIds)
    {
        return jstRefundRecordMapper.deleteJstRefundRecordByRefundIds(refundIds);
    }

    /**
     * 删除退款/售后单信息
     * 
     * @param refundId 退款/售后单主键
     * @return 结果
     */
    @Override
    public int deleteJstRefundRecordByRefundId(Long refundId)
    {
        return jstRefundRecordMapper.deleteJstRefundRecordByRefundId(refundId);
    }
}
