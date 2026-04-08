package com.ruoyi.jst.order.mapper;

import java.util.List;
import com.ruoyi.jst.order.domain.JstRefundRecord;

/**
 * 退款/售后单Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstRefundRecordMapper 
{
    /**
     * 查询退款/售后单
     * 
     * @param refundId 退款/售后单主键
     * @return 退款/售后单
     */
    public JstRefundRecord selectJstRefundRecordByRefundId(Long refundId);

    /**
     * 查询退款/售后单列表
     * 
     * @param jstRefundRecord 退款/售后单
     * @return 退款/售后单集合
     */
    public List<JstRefundRecord> selectJstRefundRecordList(JstRefundRecord jstRefundRecord);

    /**
     * 新增退款/售后单
     * 
     * @param jstRefundRecord 退款/售后单
     * @return 结果
     */
    public int insertJstRefundRecord(JstRefundRecord jstRefundRecord);

    /**
     * 修改退款/售后单
     * 
     * @param jstRefundRecord 退款/售后单
     * @return 结果
     */
    public int updateJstRefundRecord(JstRefundRecord jstRefundRecord);

    /**
     * 删除退款/售后单
     * 
     * @param refundId 退款/售后单主键
     * @return 结果
     */
    public int deleteJstRefundRecordByRefundId(Long refundId);

    /**
     * 批量删除退款/售后单
     * 
     * @param refundIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstRefundRecordByRefundIds(Long[] refundIds);
}
