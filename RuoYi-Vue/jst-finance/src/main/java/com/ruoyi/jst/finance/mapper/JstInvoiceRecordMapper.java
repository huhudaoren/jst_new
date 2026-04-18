package com.ruoyi.jst.finance.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.jst.finance.domain.JstInvoiceRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 发票记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstInvoiceRecordMapper 
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
     * 删除发票记录
     * 
     * @param invoiceId 发票记录主键
     * @return 结果
     */
    public int deleteJstInvoiceRecordByInvoiceId(Long invoiceId);

    /**
     * 批量删除发票记录
     * 
     * @param invoiceIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstInvoiceRecordByInvoiceIds(Long[] invoiceIds);

    /**
     * 按对象归属查询发票列表（小程序端）。
     *
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 发票记录集合
     */
    List<JstInvoiceRecord> selectByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    /**
     * 按发票ID+对象归属查询详情（小程序端）。
     *
     * @param invoiceId  发票ID
     * @param targetType 对象类型(channel/partner)
     * @param targetId   对象ID
     * @return 发票记录
     */
    JstInvoiceRecord selectByIdAndTarget(@Param("invoiceId") Long invoiceId,
                                         @Param("targetType") String targetType,
                                         @Param("targetId") Long targetId);

    /**
     * 查询渠道提现单实付金额（按 settlementNo + channel 归属）。
     *
     * @param settlementNo 提现单号
     * @param channelId    渠道ID
     * @return 实付金额
     */
    BigDecimal selectRebateSettlementActualAmount(@Param("settlementNo") String settlementNo,
                                                  @Param("channelId") Long channelId);

    /**
     * 查询赛事方结算单最终金额（按 settlementNo + partner 归属）。
     *
     * @param settlementNo 结算单号
     * @param partnerId    赛事方ID
     * @return 最终金额
     */
    BigDecimal selectEventSettlementFinalAmount(@Param("settlementNo") String settlementNo,
                                                @Param("partnerId") Long partnerId);

    /**
     * 按结算单 ID 查询渠道提现单号（反查 settlementNo）。
     *
     * @param settlementId 提现单 ID
     * @return 结算单号，查无返回 null
     */
    String selectRebateSettlementNoById(@Param("settlementId") Long settlementId);

    /**
     * 按结算单 ID 查询赛事方结算单号。
     *
     * @param settlementId 结算单 ID
     * @return 结算单号，查无返回 null
     */
    String selectEventSettlementNoById(@Param("settlementId") Long settlementId);

    /**
     * 按结算单号反查渠道提现单 ID。
     *
     * @param settlementNo 提现单号
     * @return 提现单 ID，查无返回 null
     */
    Long selectRebateSettlementIdByNo(@Param("settlementNo") String settlementNo);

    /**
     * 按结算单号反查赛事方结算单 ID。
     *
     * @param settlementNo 结算单号
     * @return 结算单 ID，查无返回 null
     */
    Long selectEventSettlementIdByNo(@Param("settlementNo") String settlementNo);

    /**
     * 按结算单 ID 列表汇总渠道提现单实付金额（归属校验 + paid 状态）。
     * 不属于该 channel / 非 paid / 已删除 的结算单不计入。
     *
     * @param settlementIds 结算单 ID 列表
     * @param channelId     渠道 ID
     * @return 匹配的结算单数量与金额汇总
     */
    java.util.Map<String, Object> sumRebateSettlementAmountByIds(@Param("settlementIds") java.util.List<Long> settlementIds,
                                                                  @Param("channelId") Long channelId);

    /**
     * 按结算单 ID 列表汇总赛事方结算单最终金额。
     *
     * @param settlementIds 结算单 ID 列表
     * @param partnerId     赛事方 ID
     * @return 匹配的结算单数量与金额汇总
     */
    java.util.Map<String, Object> sumEventSettlementAmountByIds(@Param("settlementIds") java.util.List<Long> settlementIds,
                                                                 @Param("partnerId") Long partnerId);

    /**
     * 查询哪些结算单已经被开票（status 非 voided/red_offset）。
     * 用于防止重复开票。
     *
     * @param settlementIds 结算单 ID 列表
     * @param targetType    对象类型 (channel/partner)
     * @return 已开票的结算单 ID 列表
     */
    java.util.List<Long> selectAlreadyInvoicedSettlementIds(@Param("settlementIds") java.util.List<Long> settlementIds,
                                                             @Param("targetType") String targetType);
}
