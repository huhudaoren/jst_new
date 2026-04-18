package com.ruoyi.jst.channel.task;

import com.ruoyi.jst.channel.domain.JstSalesFollowupRecord;
import com.ruoyi.jst.channel.domain.JstSalesFollowupTask;
import com.ruoyi.jst.channel.service.SalesFollowupService;
import com.ruoyi.jst.channel.service.SalesFollowupTaskService;
import com.ruoyi.jst.channel.service.SalesNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 销售跟进提醒定时任务（每天 08:00）。
 * <p>
 * 执行步骤：
 * <ol>
 *   <li>将逾期任务批量标记为 overdue</li>
 *   <li>查今日到期任务，按 assigneeSalesId 分组，推送任务提醒</li>
 *   <li>查今日待跟进记录（next_followup_at 今天），按 salesId 分组，推送跟进提醒</li>
 * </ol>
 *
 * @author jst
 * @since 1.0.0
 */
@Component("salesFollowupReminderTask")
public class SalesFollowupReminderTask {

    private static final Logger log = LoggerFactory.getLogger(SalesFollowupReminderTask.class);

    @Autowired
    private SalesFollowupTaskService taskService;

    @Autowired
    private SalesFollowupService followupService;

    @Autowired
    private SalesNotificationService notificationService;

    public void execute() {
        log.info("[SalesFollowupReminderTask] 开始执行");

        // Step 1: 标记逾期任务
        int overdueCount = taskService.markOverdueByCron();
        log.info("[SalesFollowupReminderTask] 标记逾期任务 {} 条", overdueCount);

        // Step 2: 今日到期任务提醒
        List<JstSalesFollowupTask> dueTodayTasks = taskService.selectDueTodayForReminder();
        if (dueTodayTasks != null && !dueTodayTasks.isEmpty()) {
            Map<Long, Integer> taskCountBySales = new HashMap<>();
            for (JstSalesFollowupTask task : dueTodayTasks) {
                taskCountBySales.merge(task.getAssigneeSalesId(), 1, Integer::sum);
            }
            for (Map.Entry<Long, Integer> entry : taskCountBySales.entrySet()) {
                notificationService.sendTaskReminder(entry.getKey(), entry.getValue());
            }
            log.info("[SalesFollowupReminderTask] 今日到期任务提醒推送：{} 名销售，共 {} 个任务",
                    taskCountBySales.size(), dueTodayTasks.size());
        }

        // Step 3: 今日待跟进记录提醒（next_followup_at 今天）
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfDay = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endOfDay = cal.getTime();

        List<JstSalesFollowupRecord> dueFollowups = followupService.selectDueRemindersForJob(startOfDay, endOfDay);
        if (dueFollowups != null && !dueFollowups.isEmpty()) {
            Map<Long, Integer> followupCountBySales = new HashMap<>();
            for (JstSalesFollowupRecord record : dueFollowups) {
                followupCountBySales.merge(record.getSalesId(), 1, Integer::sum);
            }
            for (Map.Entry<Long, Integer> entry : followupCountBySales.entrySet()) {
                notificationService.sendFollowupReminder(entry.getKey(), entry.getValue());
            }
            log.info("[SalesFollowupReminderTask] 今日待跟进提醒推送：{} 名销售，共 {} 条",
                    followupCountBySales.size(), dueFollowups.size());
        }

        log.info("[SalesFollowupReminderTask] 执行完毕");
    }
}
