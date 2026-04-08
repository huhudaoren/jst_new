package com.ruoyi.jst.finance.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.finance.mapper.JstPaymentPayRecordMapper;
import com.ruoyi.jst.finance.domain.JstPaymentPayRecord;
import com.ruoyi.jst.finance.service.IJstPaymentPayRecordService;

/**
 * 统一打款记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstPaymentPayRecordServiceImpl implements IJstPaymentPayRecordService 
{
    @Autowired
    private JstPaymentPayRecordMapper jstPaymentPayRecordMapper;

    /**
     * 查询统一打款记录
     * 
     * @param payRecordId 统一打款记录主键
     * @return 统一打款记录
     */
    @Override
    public JstPaymentPayRecord selectJstPaymentPayRecordByPayRecordId(Long payRecordId)
    {
        return jstPaymentPayRecordMapper.selectJstPaymentPayRecordByPayRecordId(payRecordId);
    }

    /**
     * 查询统一打款记录列表
     * 
     * @param jstPaymentPayRecord 统一打款记录
     * @return 统一打款记录
     */
    @Override
    public List<JstPaymentPayRecord> selectJstPaymentPayRecordList(JstPaymentPayRecord jstPaymentPayRecord)
    {
        return jstPaymentPayRecordMapper.selectJstPaymentPayRecordList(jstPaymentPayRecord);
    }

    /**
     * 新增统一打款记录
     * 
     * @param jstPaymentPayRecord 统一打款记录
     * @return 结果
     */
    @Override
    public int insertJstPaymentPayRecord(JstPaymentPayRecord jstPaymentPayRecord)
    {
        jstPaymentPayRecord.setCreateTime(DateUtils.getNowDate());
        return jstPaymentPayRecordMapper.insertJstPaymentPayRecord(jstPaymentPayRecord);
    }

    /**
     * 修改统一打款记录
     * 
     * @param jstPaymentPayRecord 统一打款记录
     * @return 结果
     */
    @Override
    public int updateJstPaymentPayRecord(JstPaymentPayRecord jstPaymentPayRecord)
    {
        jstPaymentPayRecord.setUpdateTime(DateUtils.getNowDate());
        return jstPaymentPayRecordMapper.updateJstPaymentPayRecord(jstPaymentPayRecord);
    }

    /**
     * 批量删除统一打款记录
     * 
     * @param payRecordIds 需要删除的统一打款记录主键
     * @return 结果
     */
    @Override
    public int deleteJstPaymentPayRecordByPayRecordIds(Long[] payRecordIds)
    {
        return jstPaymentPayRecordMapper.deleteJstPaymentPayRecordByPayRecordIds(payRecordIds);
    }

    /**
     * 删除统一打款记录信息
     * 
     * @param payRecordId 统一打款记录主键
     * @return 结果
     */
    @Override
    public int deleteJstPaymentPayRecordByPayRecordId(Long payRecordId)
    {
        return jstPaymentPayRecordMapper.deleteJstPaymentPayRecordByPayRecordId(payRecordId);
    }
}
