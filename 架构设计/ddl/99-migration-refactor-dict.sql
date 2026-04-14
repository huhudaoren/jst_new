-- =============================================
-- REFACTOR-2-DICT: 全局枚举字典数据补齐
-- 日期: 2026-04-14
-- 说明: 为管理端列表页枚举翻译提供字典数据支持
-- 幂等: 所有 INSERT 使用 WHERE NOT EXISTS 可重复执行
-- =============================================

-- ============================================
-- 赛事来源类型
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '赛事来源', 'jst_source_type', '0', 'admin', NOW(), '赛事来源类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_source_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1, '平台自营', 'platform', 'jst_source_type', '0', 'admin', NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_source_type' AND dict_value='platform');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2, '赛事方', 'partner', 'jst_source_type', '0', 'admin', NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_source_type' AND dict_value='partner');

-- ============================================
-- 审核状态（通用）
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '审核状态', 'jst_audit_status', '0', 'admin', NOW(), '赛事/报名/入驻等审核状态'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_audit_status');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'草稿','draft','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='draft');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'待审核','pending','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='pending');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'已上线','online','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='online');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 4,'已下线','offline','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='offline');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 5,'已驳回','rejected','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='rejected');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 6,'已通过','approved','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='approved');

-- ============================================
-- 所属类型
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '所属类型', 'jst_owner_type', '0', 'admin', NOW(), '业务所属方类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_owner_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'学生','student','jst_owner_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_owner_type' AND dict_value='student');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'渠道方','channel','jst_owner_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_owner_type' AND dict_value='channel');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'赛事方','partner','jst_owner_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_owner_type' AND dict_value='partner');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 4,'平台','platform','jst_owner_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_owner_type' AND dict_value='platform');

-- ============================================
-- 订单状态
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '订单状态', 'jst_order_status', '0', 'admin', NOW(), '订单状态'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_order_status');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'待支付','pending_pay','jst_order_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_order_status' AND dict_value='pending_pay');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'已支付','paid','jst_order_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_order_status' AND dict_value='paid');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'已取消','cancelled','jst_order_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_order_status' AND dict_value='cancelled');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 4,'已关闭','closed','jst_order_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_order_status' AND dict_value='closed');

-- ============================================
-- 退款状态
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '退款状态', 'jst_refund_status', '0', 'admin', NOW(), '退款状态'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_refund_status');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'待审核','pending','jst_refund_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_refund_status' AND dict_value='pending');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'已通过','approved','jst_refund_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_refund_status' AND dict_value='approved');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'已驳回','rejected','jst_refund_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_refund_status' AND dict_value='rejected');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 4,'退款中','processing','jst_refund_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_refund_status' AND dict_value='processing');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 5,'已完成','completed','jst_refund_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_refund_status' AND dict_value='completed');

-- ============================================
-- 报名审核状态
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '报名审核状态', 'jst_enroll_audit_status', '0', 'admin', NOW(), '报名审核状态'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_enroll_audit_status');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'待审核','pending','jst_enroll_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_enroll_audit_status' AND dict_value='pending');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'已通过','passed','jst_enroll_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_enroll_audit_status' AND dict_value='passed');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'已驳回','rejected','jst_enroll_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_enroll_audit_status' AND dict_value='rejected');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 4,'已取消','cancelled','jst_enroll_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_enroll_audit_status' AND dict_value='cancelled');

-- ============================================
-- 渠道类型
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '渠道类型', 'jst_channel_type', '0', 'admin', NOW(), '渠道方类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_channel_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'个人','personal','jst_channel_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_channel_type' AND dict_value='personal');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'机构','institution','jst_channel_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_channel_type' AND dict_value='institution');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'学校','school','jst_channel_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_channel_type' AND dict_value='school');
