package com.ruoyi.jst.finance.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.finance.mapper.JstInvoiceRecordMapper;
import com.ruoyi.jst.finance.domain.JstInvoiceRecord;
import com.ruoyi.jst.finance.service.IJstInvoiceRecordService;

/**
 * 发票记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstInvoiceRecordServiceImpl implements IJstInvoiceRecordService 
{
    @Autowired
    private JstInvoiceRecordMapper jstInvoiceRecordMapper;

    /**
     * 查询发票记录
     * 
     * @param invoiceId 发票记录主键
     * @return 发票记录
     */
    @Override
    public JstInvoiceRecord selectJstInvoiceRecordByInvoiceId(Long invoiceId)
    {
        return jstInvoiceRecordMapper.selectJstInvoiceRecordByInvoiceId(invoiceId);
    }

    /**
     * 查询发票记录列表
     * 
     * @param jstInvoiceRecord 发票记录
     * @return 发票记录
     */
    @Override
    public List<JstInvoiceRecord> selectJstInvoiceRecordList(JstInvoiceRecord jstInvoiceRecord)
    {
        return jstInvoiceRecordMapper.selectJstInvoiceRecordList(jstInvoiceRecord);
    }

    /**
     * 新增发票记录
     * 
     * @param jstInvoiceRecord 发票记录
     * @return 结果
     */
    @Override
    public int insertJstInvoiceRecord(JstInvoiceRecord jstInvoiceRecord)
    {
        jstInvoiceRecord.setCreateTime(DateUtils.getNowDate());
        return jstInvoiceRecordMapper.insertJstInvoiceRecord(jstInvoiceRecord);
    }

    /**
     * 修改发票记录
     * 
     * @param jstInvoiceRecord 发票记录
     * @return 结果
     */
    @Override
    public int updateJstInvoiceRecord(JstInvoiceRecord jstInvoiceRecord)
    {
        jstInvoiceRecord.setUpdateTime(DateUtils.getNowDate());
        return jstInvoiceRecordMapper.updateJstInvoiceRecord(jstInvoiceRecord);
    }

    /**
     * 批量删除发票记录
     * 
     * @param invoiceIds 需要删除的发票记录主键
     * @return 结果
     */
    @Override
    public int deleteJstInvoiceRecordByInvoiceIds(Long[] invoiceIds)
    {
        return jstInvoiceRecordMapper.deleteJstInvoiceRecordByInvoiceIds(invoiceIds);
    }

    /**
     * 删除发票记录信息
     * 
     * @param invoiceId 发票记录主键
     * @return 结果
     */
    @Override
    public int deleteJstInvoiceRecordByInvoiceId(Long invoiceId)
    {
        return jstInvoiceRecordMapper.deleteJstInvoiceRecordByInvoiceId(invoiceId);
    }
}
