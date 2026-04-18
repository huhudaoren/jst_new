-- =================================================================
-- Plan-03 CRM: sys_job 注册 — 销售跟进提醒定时任务
-- =================================================================

INSERT INTO sys_job (
    job_id, job_name, job_group, invoke_target,
    cron_expression, misfire_policy, concurrent, status,
    create_by, create_time, remark
) VALUES (
    2007,
    '销售-跟进提醒',
    'SALES',
    'salesFollowupReminderTask.execute()',
    '0 0 8 * * ?',
    '1',
    '1',
    '1',
    'admin',
    NOW(),
    '每天 08:00 推今日待跟进+到期任务提醒，并将逾期任务标记为 overdue'
);
