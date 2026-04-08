package com.ruoyi.jst.order.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.order.mapper.JstPaymentRecordMapper;
import com.ruoyi.jst.order.domain.JstPaymentRecord;
import com.ruoyi.jst.order.service.IJstPaymentRecordService;

/**
 * 支付记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstPaymentRecordServiceImpl implements IJstPaymentRecordService 
{
    @Autowired
    private JstPaymentRecordMapper jstPaymentRecordMapper;

    /**
     * 查询支付记录
     * 
     * @param paymentId 支付记录主键
     * @return 支付记录
     */
    @Override
    public JstPaymentRecord selectJstPaymentRecordByPaymentId(Long paymentId)
    {
        return jstPaymentRecordMapper.selectJstPaymentRecordByPaymentId(paymentId);
    }

    /**
     * 查询支付记录列表
     * 
     * @param jstPaymentRecord 支付记录
     * @return 支付记录
     */
    @Override
    public List<JstPaymentRecord> selectJstPaymentRecordList(JstPaymentRecord jstPaymentRecord)
    {
        return jstPaymentRecordMapper.selectJstPaymentRecordList(jstPaymentRecord);
    }

    /**
     * 新增支付记录
     * 
     * @param jstPaymentRecord 支付记录
     * @return 结果
     */
    @Override
    public int insertJstPaymentRecord(JstPaymentRecord jstPaymentRecord)
    {
        jstPaymentRecord.setCreateTime(DateUtils.getNowDate());
        return jstPaymentRecordMapper.insertJstPaymentRecord(jstPaymentRecord);
    }

    /**
     * 修改支付记录
     * 
     * @param jstPaymentRecord 支付记录
     * @return 结果
     */
    @Override
    public int updateJstPaymentRecord(JstPaymentRecord jstPaymentRecord)
    {
        jstPaymentRecord.setUpdateTime(DateUtils.getNowDate());
        return jstPaymentRecordMapper.updateJstPaymentRecord(jstPaymentRecord);
    }

    /**
     * 批量删除支付记录
     * 
     * @param paymentIds 需要删除的支付记录主键
     * @return 结果
     */
    @Override
    public int deleteJstPaymentRecordByPaymentIds(Long[] paymentIds)
    {
        return jstPaymentRecordMapper.deleteJstPaymentRecordByPaymentIds(paymentIds);
    }

    /**
     * 删除支付记录信息
     * 
     * @param paymentId 支付记录主键
     * @return 结果
     */
    @Override
    public int deleteJstPaymentRecordByPaymentId(Long paymentId)
    {
        return jstPaymentRecordMapper.deleteJstPaymentRecordByPaymentId(paymentId);
    }
}
