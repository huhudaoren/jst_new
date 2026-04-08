package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.JstPaymentPayRecord;

/**
 * 统一打款记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstPaymentPayRecordMapper 
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
     * 删除统一打款记录
     * 
     * @param payRecordId 统一打款记录主键
     * @return 结果
     */
    public int deleteJstPaymentPayRecordByPayRecordId(Long payRecordId);

    /**
     * 批量删除统一打款记录
     * 
     * @param payRecordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstPaymentPayRecordByPayRecordIds(Long[] payRecordIds);
}
