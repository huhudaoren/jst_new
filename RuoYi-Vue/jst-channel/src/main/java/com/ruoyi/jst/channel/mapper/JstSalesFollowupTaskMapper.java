package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesFollowupTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JstSalesFollowupTaskMapper {
    JstSalesFollowupTask selectJstSalesFollowupTaskByTaskId(Long taskId);
    List<JstSalesFollowupTask> selectJstSalesFollowupTaskList(JstSalesFollowupTask query);
    int insertJstSalesFollowupTask(JstSalesFollowupTask row);
    int updateJstSalesFollowupTask(JstSalesFollowupTask row);
    int deleteJstSalesFollowupTaskByTaskId(Long taskId);

    /** 我的任务列表（销售本人视角），status 可为 null 查全部 */
    List<JstSalesFollowupTask> listMine(
            @Param("salesId") Long salesId,
            @Param("status") String status);

    /** 我派出的任务（主管视角），status 可为 null */
    List<JstSalesFollowupTask> listAssignedBy(
            @Param("userId") Long userId,
            @Param("status") String status);

    /** 查已逾期任务 ID（status in pending/in_progress AND due_date < CURDATE） */
    List<Long> selectOverdueTaskIds();

    /** 批量标记为 overdue */
    int markOverdueBatch(@Param("ids") List<Long> ids);

    /** 查今日到期任务（Quartz 提醒用） */
    List<JstSalesFollowupTask> selectDueToday();
}
