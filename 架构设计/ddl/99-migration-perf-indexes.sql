-- ============================================================
-- PERF-INDEX: 数据库索引补齐（组合索引覆盖热点查询）
-- 执行前提: 已通过 SHOW INDEX FROM 确认以下索引不存在
-- 执行环境: MySQL 5.7+
-- 创建时间: 2026-04-13
-- ============================================================

-- -------------------------------------------------------
-- 1. jst_order_main: 用户+状态+时间 组合索引
--    热点场景: 我的订单列表 (WHERE user_id=? AND order_status=? ORDER BY create_time DESC)
--    现有: idx_user_id(单列), idx_order_status(单列) → 无法覆盖组合查询
-- -------------------------------------------------------
ALTER TABLE jst_order_main ADD INDEX idx_user_order_status (user_id, order_status, create_time DESC);

-- -------------------------------------------------------
-- 2. jst_enroll_record: 赛事+审核状态+时间 组合索引
--    热点场景: 报名管理列表 (WHERE contest_id=? AND audit_status=? ORDER BY create_time DESC)
--    现有: idx_contest_id(单列), idx_audit_status(单列) → 无法覆盖组合查询
-- -------------------------------------------------------
ALTER TABLE jst_enroll_record ADD INDEX idx_contest_audit (contest_id, audit_status, create_time DESC);

-- -------------------------------------------------------
-- 3. jst_refund_record: 订单+状态+时间 组合索引
--    热点场景: 退款管理列表 (WHERE order_id=? AND status=? ORDER BY create_time DESC)
--    现有: idx_order_id(单列), idx_status(单列) → 无法覆盖组合查询
-- -------------------------------------------------------
ALTER TABLE jst_refund_record ADD INDEX idx_order_status_time (order_id, status, create_time DESC);

-- -------------------------------------------------------
-- 4. jst_points_ledger: 扩展 owner 索引加入 create_time
--    热点场景: 积分流水列表 (WHERE owner_type=? AND owner_id=? ORDER BY create_time DESC)
--    现有: idx_owner(owner_type, owner_id) → 缺 create_time, 排序需回表
--    策略: 先删旧索引, 再建含 create_time 的新索引(新索引是旧索引的超集)
-- -------------------------------------------------------
ALTER TABLE jst_points_ledger DROP INDEX idx_owner;
ALTER TABLE jst_points_ledger ADD INDEX idx_owner_time (owner_type, owner_id, create_time DESC);

-- -------------------------------------------------------
-- 5. jst_message: 扩展 user_read 索引加入 create_time
--    热点场景: 消息中心列表 (WHERE user_id=? AND read_status=? ORDER BY create_time DESC)
--    现有: idx_user_read(user_id, read_status) → 缺 create_time, 排序需回表
--    策略: 先删旧索引, 再建含 create_time 的新索引(新索引是旧索引的超集)
-- -------------------------------------------------------
ALTER TABLE jst_message DROP INDEX idx_user_read;
ALTER TABLE jst_message ADD INDEX idx_user_read_time (user_id, read_status, create_time DESC);
