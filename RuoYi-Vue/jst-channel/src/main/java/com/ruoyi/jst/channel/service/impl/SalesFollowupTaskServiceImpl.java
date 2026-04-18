package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSalesFollowupTask;
import com.ruoyi.jst.channel.dto.TaskCreateReqDTO;
import com.ruoyi.jst.channel.mapper.JstSalesFollowupTaskMapper;
import com.ruoyi.jst.channel.service.SalesFollowupTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 跟进任务服务实现。
 * <p>
 * 状态机：pending → in_progress（手动推进，预留）→ completed / overdue（Quartz 自动）。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class SalesFollowupTaskServiceImpl implements SalesFollowupTaskService {

    private static final Logger log = LoggerFactory.getLogger(SalesFollowupTaskServiceImpl.class);

    private static final List<String> COMPLETABLE_STATUSES = Arrays.asList("pending", "in_progress");

    @Autowired
    private JstSalesFollowupTaskMapper taskMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JstSalesFollowupTask create(TaskCreateReqDTO req, Long assignerUserId) {
        if (req == null) throw new ServiceException("任务参数不能为空");
        JstSalesFollowupTask task = new JstSalesFollowupTask();
        task.setTaskId(snowId());
        task.setAssigneeSalesId(req.getAssigneeSalesId());
        task.setAssignerId(assignerUserId);
        task.setChannelId(req.getChannelId());
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setDueDate(req.getDueDate());
        task.setStatus("pending");
        taskMapper.insertJstSalesFollowupTask(task);
        log.info("[SalesFollowupTask] 派发任务 taskId={} assignee={} assigner={}", task.getTaskId(), req.getAssigneeSalesId(), assignerUserId);
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long taskId, Long currentSalesId, Long linkedRecordId) {
        JstSalesFollowupTask task = requireTask(taskId);
        if (!task.getAssigneeSalesId().equals(currentSalesId)) {
            throw new ServiceException("只有被分配的销售才能完成此任务");
        }
        if (!COMPLETABLE_STATUSES.contains(task.getStatus())) {
            throw new ServiceException("任务状态为 [" + task.getStatus() + "]，无法完成");
        }
        task.setStatus("completed");
        task.setCompletedAt(new Date());
        if (linkedRecordId != null) {
            task.setLinkedRecordId(linkedRecordId);
        }
        taskMapper.updateJstSalesFollowupTask(task);
    }

    @Override
    public List<JstSalesFollowupTask> listMine(Long salesId, String status) {
        return taskMapper.listMine(salesId, status);
    }

    @Override
    public List<JstSalesFollowupTask> listAssignedByMe(Long userId, String status) {
        return taskMapper.listAssignedBy(userId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int markOverdueByCron() {
        List<Long> ids = taskMapper.selectOverdueTaskIds();
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        int n = taskMapper.markOverdueBatch(ids);
        log.info("[SalesFollowupTask] Quartz 标记逾期任务 {} 条", n);
        return n;
    }

    @Override
    public List<JstSalesFollowupTask> selectDueTodayForReminder() {
        return taskMapper.selectDueToday();
    }

    private JstSalesFollowupTask requireTask(Long taskId) {
        JstSalesFollowupTask task = taskMapper.selectJstSalesFollowupTaskByTaskId(taskId);
        if (task == null) {
            throw new ServiceException("任务不存在");
        }
        return task;
    }

    private Long snowId() {
        return System.currentTimeMillis() * 10_000L + new Random().nextInt(10_000);
    }
}
