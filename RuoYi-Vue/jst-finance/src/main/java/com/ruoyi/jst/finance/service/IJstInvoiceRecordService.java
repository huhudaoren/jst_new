package com.ruoyi.jst.finance.service;

import java.util.List;
import com.ruoyi.jst.finance.domain.JstInvoiceRecord;

/**
 * 发票记录Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstInvoiceRecordService 
{
    /**
     * 查询发票记录
     * 
     * @param invoiceId 发票记录主键
     * @return 发票记录
     */
    public JstInvoiceRecord selectJstInvoiceRecordByInvoiceId(Long invoiceId);

    /**
     * 查询发票记录列表
     * 
     * @param jstInvoiceRecord 发票记录
     * @return 发票记录集合
     */
    public List<JstInvoiceRecord> selectJstInvoiceRecordList(JstInvoiceRecord jstInvoiceRecord);

    /**
     * 新增发票记录
     * 
     * @param jstInvoiceRecord 发票记录
     * @return 结果
     */
    public int insertJstInvoiceRecord(JstInvoiceRecord jstInvoiceRecord);

    /**
     * 修改发票记录
     * 
     * @param jstInvoiceRecord 发票记录
     * @return 结果
     */
    public int updateJstInvoiceRecord(JstInvoiceRecord jstInvoiceRecord);

    /**
     * 批量删除发票记录
     * 
     * @param invoiceIds 需要删除的发票记录主键集合
     * @return 结果
     */
    public int deleteJstInvoiceRecordByInvoiceIds(Long[] invoiceIds);

    /**
     * 删除发票记录信息
     * 
     * @param invoiceId 发票记录主键
     * @return 结果
     */
    public int deleteJstInvoiceRecordByInvoiceId(Long invoiceId);
}
