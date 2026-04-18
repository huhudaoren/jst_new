package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesFollowupRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface JstSalesFollowupRecordMapper {
    JstSalesFollowupRecord selectJstSalesFollowupRecordByRecordId(Long recordId);
    List<JstSalesFollowupRecord> selectJstSalesFollowupRecordList(JstSalesFollowupRecord query);
    int insertJstSalesFollowupRecord(JstSalesFollowupRecord row);
    int updateJstSalesFollowupRecord(JstSalesFollowupRecord row);
    int deleteJstSalesFollowupRecordByRecordId(Long recordId);

    /** 按渠道查全部跟进记录，按时间倒序 */
    List<JstSalesFollowupRecord> listByChannel(@Param("channelId") Long channelId);

    /** 销售本人视角过滤查询 */
    List<JstSalesFollowupRecord> listMineWithFilter(
            @Param("salesId") Long salesId,
            @Param("channelId") Long channelId,
            @Param("followupType") String followupType,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo);

    /** 今日待提醒（next_followup_at 落在 [startOfDay, endOfDay)） */
    List<JstSalesFollowupRecord> selectDueReminders(
            @Param("startOfDay") Date startOfDay,
            @Param("endOfDay") Date endOfDay);

    /**
     * Dashboard：各销售本月跟进笔数 + 覆盖渠道数。
     * 返回 map keys: salesId, salesName(JOIN), followupCount, coveredChannelCount。
     */
    List<java.util.Map<String, Object>> aggregateFollowupActivity(
            @Param("periodStart") Date periodStart,
            @Param("periodEnd") Date periodEnd);

    /** 渠道画像：按渠道统计总跟进次数，返回 [{cnt}] */
    List<java.util.Map<String, Object>> selectFollowupCountByChannel(
            @Param("channelId") Long channelId);

    /** 渠道画像：按渠道 + 时间段统计跟进次数，返回 [{cnt}] */
    List<java.util.Map<String, Object>> selectFollowupCountByChannelAndPeriod(
            @Param("channelId") Long channelId,
            @Param("periodStart") Date periodStart,
            @Param("periodEnd") Date periodEnd);
}
