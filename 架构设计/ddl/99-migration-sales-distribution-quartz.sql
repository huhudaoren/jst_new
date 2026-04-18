-- =====================================================
-- 销售管理 Quartz 任务注册
-- 关联 plan-02 Task 18
--
-- ⚠️ 一次性迁移文件：job_id 是 PK，重跑会因 Duplicate 报错。
-- 回滚命令：
--   DELETE FROM sys_job WHERE job_id BETWEEN 2001 AND 2010;
-- =====================================================
SET NAMES utf8mb4;

-- 任务组 SALES；misfire_policy=1 (立即执行) / concurrent=1 (禁止并发)
-- status: 0=正常 / 1=暂停。建议生产环境先 status=1 暂停，admin 在 Web 端手工启用。

INSERT INTO sys_job (job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark) VALUES
(2001, '销售-预录入过期清理',     'SALES', 'salesPreRegisterExpireTask.execute()',  '0 0 1 * * ?',  '1', '1', '1', 'admin', NOW(), '90 天未匹配自动失效'),
(2002, '销售-提成入账推进',        'SALES', 'salesCommissionAccrueTask.execute()',   '0 0 2 * * ?',  '1', '1', '1', 'admin', NOW(), '售后期满 pending → accrued'),
(2003, '销售-提成兜底补偿',        'SALES', 'salesCommissionRepairTask.execute()',   '0 0 * * * ?',  '1', '1', '1', 'admin', NOW(), '每小时扫已付款无 ledger 的订单补偿'),
(2004, '销售-月结单生成',          'SALES', 'salesMonthlySettlementTask.execute()',  '0 0 3 1 * ?',  '1', '1', '1', 'admin', NOW(), '每月 1 日 03:00 月结上月 accrued'),
(2005, '销售-离职执行',            'SALES', 'salesResignExecuteTask.execute()',      '0 0 4 * * ?',  '1', '1', '1', 'admin', NOW(), '到期 resign_apply 自动转 resigned_pending_settle + 转移渠道'),
(2006, '销售-过渡期终结',          'SALES', 'salesTransitionEndTask.execute()',      '0 30 4 * * ?', '1', '1', '1', 'admin', NOW(), '到期 resigned_pending_settle 自动转 resigned_final');
