package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstPaymentRecord;

/**
 * 支付记录Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstPaymentRecordService 
{
    /**
     * 查询支付记录
     * 
     * @param paymentId 支付记录主键
     * @return 支付记录
     */
    public JstPaymentRecord selectJstPaymentRecordByPaymentId(Long paymentId);

    /**
     * 查询支付记录列表
     * 
     * @param jstPaymentRecord 支付记录
     * @return 支付记录集合
     */
    public List<JstPaymentRecord> selectJstPaymentRecordList(JstPaymentRecord jstPaymentRecord);

    /**
     * 新增支付记录
     * 
     * @param jstPaymentRecord 支付记录
     * @return 结果
     */
    public int insertJstPaymentRecord(JstPaymentRecord jstPaymentRecord);

    /**
     * 修改支付记录
     * 
     * @param jstPaymentRecord 支付记录
     * @return 结果
     */
    public int updateJstPaymentRecord(JstPaymentRecord jstPaymentRecord);

    /**
     * 批量删除支付记录
     * 
     * @param paymentIds 需要删除的支付记录主键集合
     * @return 结果
     */
    public int deleteJstPaymentRecordByPaymentIds(Long[] paymentIds);

    /**
     * 删除支付记录信息
     * 
     * @param paymentId 支付记录主键
     * @return 结果
     */
    public int deleteJstPaymentRecordByPaymentId(Long paymentId);
}
