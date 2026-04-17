package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesFollowupTask;
import java.util.List;

public interface JstSalesFollowupTaskMapper {
    JstSalesFollowupTask selectJstSalesFollowupTaskByTaskId(Long taskId);
    List<JstSalesFollowupTask> selectJstSalesFollowupTaskList(JstSalesFollowupTask query);
    int insertJstSalesFollowupTask(JstSalesFollowupTask row);
    int updateJstSalesFollowupTask(JstSalesFollowupTask row);
    int deleteJstSalesFollowupTaskByTaskId(Long taskId);
}
