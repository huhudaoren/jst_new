package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstSalesFollowupTask;
import com.ruoyi.jst.channel.dto.TaskCreateReqDTO;

import java.util.List;

/**
 * 销售跟进任务服务。
 */
public interface SalesFollowupTaskService {

    /**
     * 主管派任务（status='pending'）。
     */
    JstSalesFollowupTask create(TaskCreateReqDTO req, Long assignerUserId);

    /**
     * 完成任务（仅 assigneeSales 本人 + status in (pending/in_progress)）。
     * linkedRecordId 可为 null。
     */
    void complete(Long taskId, Long currentSalesId, Long linkedRecordId);

    /**
     * 销售本人任务列表，status 可为 null 查全部。
     */
    List<JstSalesFollowupTask> listMine(Long salesId, String status);

    /**
     * 主管查我派出的任务，status 可为 null 查全部。
     */
    List<JstSalesFollowupTask> listAssignedByMe(Long userId, String status);

    /**
     * Quartz 任务用：将逾期任务批量标记为 overdue。
     *
     * @return 影响行数
     */
    int markOverdueByCron();

    /**
     * Quartz 任务用：查今日到期任务（用于推送提醒）。
     */
    List<JstSalesFollowupTask> selectDueTodayForReminder();
}
