package com.ruoyi.jst.channel.service;

/**
 * 销售通知服务接口。
 * <p>
 * 当前阶段使用日志占位实现，plan-04 接入 JstMessageService 进行站内消息投递。
 */
public interface SalesNotificationService {

    /**
     * 发送跟进提醒：今日有 dueCount 条待跟进记录。
     *
     * @param salesId  销售 ID
     * @param dueCount 今日待跟进数量
     */
    void sendFollowupReminder(Long salesId, int dueCount);

    /**
     * 发送任务提醒：今日有 taskCount 个到期任务。
     *
     * @param salesId   销售 ID
     * @param taskCount 今日到期任务数量
     */
    void sendTaskReminder(Long salesId, int taskCount);
}
