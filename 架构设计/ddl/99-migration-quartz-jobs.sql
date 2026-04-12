-- =====================================================================
-- 文件名：99-migration-quartz-jobs.sql
-- 说明：幂等注册 QUARTZ-TASKS 四个定时任务
-- 注意：依赖对应 @Component("xxxTask") 的 Spring Bean 已存在
-- =====================================================================

INSERT IGNORE INTO sys_job
(
    job_name,
    job_group,
    invoke_target,
    cron_expression,
    misfire_policy,
    concurrent,
    status,
    create_by,
    create_time,
    remark
)
VALUES
    ('订单超时自动取消', 'JST_TASK', 'orderTimeoutCancelTask.execute()', '0 */5 * * * ?', '2', '1', '0', 'migration', NOW(), '每5分钟扫描 pending_pay 超时订单'),
    ('退款超时自动关闭', 'JST_TASK', 'refundTimeoutCloseTask.execute()', '0 0 2 * * ?', '2', '1', '0', 'migration', NOW(), '每天 02:00 扫描 pending 超 7 天退款'),
    ('返点售后期自动计提', 'JST_TASK', 'rebateAutoSettleTask.execute()', '0 0 3 * * ?', '2', '1', '0', 'migration', NOW(), '每天 03:00 扫描 pending_accrual 可计提台账'),
    ('预约过期自动取消', 'JST_TASK', 'appointmentExpireTask.execute()', '0 0 1 * * ?', '2', '1', '0', 'migration', NOW(), '每天 01:00 扫描 booked 且过期预约');
