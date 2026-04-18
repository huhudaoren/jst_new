-- =========================================================
-- 2026-04-18 后端 UX 精品化遗留字段补齐 migration · Round 2A
-- 对应任务卡: subagent/tasks/BACKEND-UX-POLISH-TODO-ROUND-2.md
-- 分支: feat/backend-todo-round-2a
-- =========================================================
-- 执行策略：
-- 1. 所有新字段均 DEFAULT NULL / 默认值，向后兼容。现有数据不受影响。
-- 2. MySQL 5.7 不支持 ADD COLUMN IF NOT EXISTS，执行前请确认字段不存在。
-- =========================================================

-- ---------------------------------------------------------
-- A2. jst_channel_auth_apply 增加独立驳回原因
-- 业务语义：reject_reason = 审核驳回业务原因（用户可见）
--           audit_remark = 通用审核备注（审批/驳回通用，向后兼容）
-- 之前前端降级显示 audit_remark，本次拆分后前端优先读 reject_reason。
-- ---------------------------------------------------------
ALTER TABLE jst_channel_auth_apply
    ADD COLUMN reject_reason VARCHAR(500) DEFAULT NULL
    COMMENT '驳回原因 (业务语义, 区别于通用 audit_remark)'
    AFTER audit_remark;

-- ---------------------------------------------------------
-- A4. jst_contest 增加线下预约剩余名额 / 截止时间
-- 业务语义：
--   offline_reserve_remaining: NULL = 不限量；> 0 = 剩余名额
--   offline_reserve_deadline : 线下预约截止时间，用于倒计时提示
-- 小程序赛事详情页使用（pages-sub/channel/apply-form 的兜底替换）。
-- ---------------------------------------------------------
ALTER TABLE jst_contest
    ADD COLUMN offline_reserve_remaining INT DEFAULT NULL
    COMMENT '线下预约剩余名额 (null=不限量)'
    AFTER allow_appointment_refund,
    ADD COLUMN offline_reserve_deadline DATETIME DEFAULT NULL
    COMMENT '线下预约截止时间'
    AFTER offline_reserve_remaining;

-- =========================================================
-- 完成后备注：
-- * JstChannelAuthApply Domain + ChannelAuthApplyVO + RejectReqDTO 新增 rejectReason 字段
-- * JstContest Domain + ContestDetailVO + WxContestDetailVO 新增 offlineReserveRemaining / offlineReserveDeadline
-- * Mapper.xml resultMap / insert / update / selectColumns 同步补齐
-- * OrderVO.refundEnabled / RefundVO.pointsRefund + couponRefund + expectedArrivalTime 都是 Service 计算字段，无需 DDL
-- * A1 totalStudentCount / A5 mall virtual / A6 pointsUsed 都基于现有 schema，无需 DDL
-- =========================================================
