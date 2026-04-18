package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstSalesFollowupRecord;
import com.ruoyi.jst.channel.dto.FollowupCreateReqDTO;
import com.ruoyi.jst.channel.dto.FollowupQueryReqDTO;
import com.ruoyi.jst.channel.dto.FollowupUpdateReqDTO;

import java.util.Date;
import java.util.List;

/**
 * 销售跟进记录服务。
 */
public interface SalesFollowupService {

    /**
     * 新建跟进记录。
     * 自动设置 can_edit_until = now + 24h。
     */
    JstSalesFollowupRecord create(Long salesId, FollowupCreateReqDTO req);

    /**
     * 更新跟进记录（仅 24h 内可改，仅记录拥有者可操作）。
     */
    void update(Long recordId, Long currentSalesId, FollowupUpdateReqDTO req);

    /**
     * 删除跟进记录（仅 24h 内可删，仅记录拥有者可操作）。
     */
    void remove(Long recordId, Long currentSalesId);

    /**
     * 按渠道查所有跟进记录（时间倒序）。
     */
    List<JstSalesFollowupRecord> listByChannel(Long channelId);

    /**
     * 销售本人视角带过滤查询。
     */
    List<JstSalesFollowupRecord> listMine(Long salesId, FollowupQueryReqDTO query);

    /**
     * Quartz 任务用：查今日待提醒记录（next_followup_at 落在 [startOfDay, endOfDay)）。
     */
    List<JstSalesFollowupRecord> selectDueRemindersForJob(Date startOfDay, Date endOfDay);
}
