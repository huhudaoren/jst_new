-- plan-04 分销提成入账 Quartz job 注册
-- 依赖：sys_job 表（若依自带）
-- 执行时机：plan-04 部署后一次性执行

INSERT INTO sys_job (job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
VALUES (2008, '渠道-分销提成入账', 'SALES', 'channelDistributionAccrueTask.execute()', '0 30 2 * * ?', '1', '1', '1', 'admin', NOW(), '售后期满 pending distribution ledger → accrued（每天 02:30）');
