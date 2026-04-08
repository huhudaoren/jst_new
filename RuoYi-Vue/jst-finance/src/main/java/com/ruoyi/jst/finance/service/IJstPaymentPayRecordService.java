package com.ruoyi.jst.finance.service;

import java.util.List;
import com.ruoyi.jst.finance.domain.JstPaymentPayRecord;

/**
 * 统一打款记录Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstPaymentPayRecordService 
{
    /**
     * 查询统一打款记录
     * 
     * @param payRecordId 统一打款记录主键
     * @return 统一打款记录
     */
    public JstPaymentPayRecord selectJstPaymentPayRecordByPayRecordId(Long payRecordId);

    /**
     * 查询统一打款记录列表
     * 
     * @param jstPaymentPayRecord 统一打款记录
     * @return 统一打款记录集合
     */
    public List<JstPaymentPayRecord> selectJstPaymentPayRecordList(JstPaymentPayRecord jstPaymentPayRecord);

    /**
     * 新增统一打款记录
     * 
     * @param jstPaymentPayRecord 统一打款记录
     * @return 结果
     */
    public int insertJstPaymentPayRecord(JstPaymentPayRecord jstPaymentPayRecord);

    /**
     * 修改统一打款记录
     * 
     * @param jstPaymentPayRecord 统一打款记录
     * @return 结果
     */
    public int updateJstPaymentPayRecord(JstPaymentPayRecord jstPaymentPayRecord);

    /**
     * 批量删除统一打款记录
     * 
     * @param payRecordIds 需要删除的统一打款记录主键集合
     * @return 结果
     */
    public int deleteJstPaymentPayRecordByPayRecordIds(Long[] payRecordIds);

    /**
     * 删除统一打款记录信息
     * 
     * @param payRecordId 统一打款记录主键
     * @return 结果
     */
    public int deleteJstPaymentPayRecordByPayRecordId(Long payRecordId);
}
