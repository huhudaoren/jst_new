-- ============================================================
-- ADMIN-POLISH-BATCH3 字典补齐（14 类型）
-- 幂等：WHERE NOT EXISTS，可重复执行
-- ============================================================

-- ---- 1. jst_pay_method 支付方式 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '支付方式', 'jst_pay_method', '0', 'admin', NOW(), '订单支付方式'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_pay_method');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '微信支付', 'wechat', 'jst_pay_method', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_pay_method' AND dict_value = 'wechat');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '银行转账', 'bank_transfer', 'jst_pay_method', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_pay_method' AND dict_value = 'bank_transfer');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '积分支付', 'points', 'jst_pay_method', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_pay_method' AND dict_value = 'points');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '混合支付', 'mix', 'jst_pay_method', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_pay_method' AND dict_value = 'mix');

-- ---- 2. jst_sku_type 商品类型 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '商品类型', 'jst_sku_type', '0', 'admin', NOW(), '订单商品类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_sku_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '报名', 'enroll', 'jst_sku_type', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_sku_type' AND dict_value = 'enroll');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '预约成员', 'appointment_member', 'jst_sku_type', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_sku_type' AND dict_value = 'appointment_member');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '商品', 'goods', 'jst_sku_type', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_sku_type' AND dict_value = 'goods');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '课程', 'course', 'jst_sku_type', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_sku_type' AND dict_value = 'course');

-- ---- 3. jst_refund_type 退款类型 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '退款类型', 'jst_refund_type', '0', 'admin', NOW(), '退款申请类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_refund_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '仅退款', 'refund_only', 'jst_refund_type', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_refund_type' AND dict_value = 'refund_only');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '退货退款', 'return_refund', 'jst_refund_type', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_refund_type' AND dict_value = 'return_refund');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '特批退款', 'special_refund', 'jst_refund_type', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_refund_type' AND dict_value = 'special_refund');

-- ---- 4. jst_change_type 变更类型（积分/成长值） ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '变更类型', 'jst_change_type', '0', 'admin', NOW(), '积分/成长值变更类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_change_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '获取', 'earn', 'jst_change_type', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_change_type' AND dict_value = 'earn');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '消费', 'spend', 'jst_change_type', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_change_type' AND dict_value = 'spend');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '冻结', 'freeze', 'jst_change_type', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_change_type' AND dict_value = 'freeze');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '解冻', 'unfreeze', 'jst_change_type', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_change_type' AND dict_value = 'unfreeze');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 5, '调整', 'adjust', 'jst_change_type', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_change_type' AND dict_value = 'adjust');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 6, '回退', 'rollback', 'jst_change_type', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_change_type' AND dict_value = 'rollback');

-- ---- 5. jst_source_type_points 积分来源 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '积分来源', 'jst_source_type_points', '0', 'admin', NOW(), '积分获取来源'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_source_type_points');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '报名', 'enroll', 'jst_source_type_points', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_source_type_points' AND dict_value = 'enroll');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '课程', 'course', 'jst_source_type_points', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_source_type_points' AND dict_value = 'course');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '签到', 'sign', 'jst_source_type_points', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_source_type_points' AND dict_value = 'sign');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '邀请', 'invite', 'jst_source_type_points', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_source_type_points' AND dict_value = 'invite');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 5, '学习', 'learn', 'jst_source_type_points', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_source_type_points' AND dict_value = 'learn');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 6, '奖励', 'award', 'jst_source_type_points', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_source_type_points' AND dict_value = 'award');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 7, '兑换', 'exchange', 'jst_source_type_points', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_source_type_points' AND dict_value = 'exchange');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 8, '手动', 'manual', 'jst_source_type_points', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_source_type_points' AND dict_value = 'manual');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 9, '退款', 'refund', 'jst_source_type_points', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_source_type_points' AND dict_value = 'refund');

-- ---- 6. jst_coupon_type 优惠券类型 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '优惠券类型', 'jst_coupon_type', '0', 'admin', NOW(), '优惠券类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_coupon_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '满减券', 'full_reduce', 'jst_coupon_type', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_coupon_type' AND dict_value = 'full_reduce');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '直减券', 'direct_reduce', 'jst_coupon_type', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_coupon_type' AND dict_value = 'direct_reduce');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '折扣券', 'discount', 'jst_coupon_type', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_coupon_type' AND dict_value = 'discount');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '赛事专享', 'contest_specific', 'jst_coupon_type', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_coupon_type' AND dict_value = 'contest_specific');

-- ---- 7. jst_rights_type 权益类型 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '权益类型', 'jst_rights_type', '0', 'admin', NOW(), '会员权益类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_rights_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '报名抵扣', 'enroll_deduct', 'jst_rights_type', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_rights_type' AND dict_value = 'enroll_deduct');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '场地减免', 'venue_reduce', 'jst_rights_type', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_rights_type' AND dict_value = 'venue_reduce');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '专属课程', 'exclusive_course', 'jst_rights_type', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_rights_type' AND dict_value = 'exclusive_course');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '优先客服', 'cs_priority', 'jst_rights_type', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_rights_type' AND dict_value = 'cs_priority');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 5, '自定义', 'custom', 'jst_rights_type', '', 'default', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_rights_type' AND dict_value = 'custom');

-- ---- 8. jst_risk_level 风险等级 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '风险等级', 'jst_risk_level', '0', 'admin', NOW(), '风控告警级别'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_risk_level');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '低风险', 'low', 'jst_risk_level', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_risk_level' AND dict_value = 'low');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '中风险', 'mid', 'jst_risk_level', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_risk_level' AND dict_value = 'mid');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '高风险', 'high', 'jst_risk_level', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_risk_level' AND dict_value = 'high');

-- ---- 9. jst_risk_action 风控动作 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '风控动作', 'jst_risk_action', '0', 'admin', NOW(), '风控规则触发动作'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_risk_action');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '告警', 'warn', 'jst_risk_action', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_risk_action' AND dict_value = 'warn');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '拦截', 'intercept', 'jst_risk_action', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_risk_action' AND dict_value = 'intercept');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '人工审核', 'manual', 'jst_risk_action', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_risk_action' AND dict_value = 'manual');

-- ---- 10. jst_list_type 名单类型 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '名单类型', 'jst_list_type', '0', 'admin', NOW(), '风控黑白名单类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_list_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '黑名单', 'black', 'jst_list_type', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_list_type' AND dict_value = 'black');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '白名单', 'white', 'jst_list_type', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_list_type' AND dict_value = 'white');

-- ---- 11. jst_contract_type 合同类型 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '合同类型', 'jst_contract_type', '0', 'admin', NOW(), '合同签署类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_contract_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '赛事方合作', 'partner_coop', 'jst_contract_type', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_contract_type' AND dict_value = 'partner_coop');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '渠道结算', 'channel_settle', 'jst_contract_type', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_contract_type' AND dict_value = 'channel_settle');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '补充协议', 'supplement', 'jst_contract_type', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_contract_type' AND dict_value = 'supplement');

-- ---- 12. jst_invoice_status 发票状态 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '发票状态', 'jst_invoice_status', '0', 'admin', NOW(), '发票处理状态'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_invoice_status');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '待开票', 'pending', 'jst_invoice_status', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_invoice_status' AND dict_value = 'pending');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '已开票', 'issued', 'jst_invoice_status', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_invoice_status' AND dict_value = 'issued');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '已作废', 'void', 'jst_invoice_status', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_invoice_status' AND dict_value = 'void');

-- 补充已有的几个状态值（合同页面使用的老 STATUS_META）
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '待申请', 'pending_apply', 'jst_invoice_status', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_invoice_status' AND dict_value = 'pending_apply');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 5, '审核中', 'reviewing', 'jst_invoice_status', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_invoice_status' AND dict_value = 'reviewing');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 6, '开票中', 'issuing', 'jst_invoice_status', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_invoice_status' AND dict_value = 'issuing');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 7, '已作废', 'voided', 'jst_invoice_status', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_invoice_status' AND dict_value = 'voided');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 8, '红冲', 'red_offset', 'jst_invoice_status', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_invoice_status' AND dict_value = 'red_offset');

-- ---- 13. jst_writeoff_mode 核销方式 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '核销方式', 'jst_writeoff_mode', '0', 'admin', NOW(), '预约核销方式'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_writeoff_mode');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '线上自动', 'online_auto', 'jst_writeoff_mode', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_writeoff_mode' AND dict_value = 'online_auto');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '人工审核', 'manual_review', 'jst_writeoff_mode', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_writeoff_mode' AND dict_value = 'manual_review');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '线下确认', 'offline_confirm', 'jst_writeoff_mode', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_writeoff_mode' AND dict_value = 'offline_confirm');

-- ---- 14. jst_cert_issue_status 证书状态 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '证书状态', 'jst_cert_issue_status', '0', 'admin', NOW(), '证书颁发状态'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_cert_issue_status');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '草稿', 'draft', 'jst_cert_issue_status', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_cert_issue_status' AND dict_value = 'draft');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '审核中', 'pending', 'jst_cert_issue_status', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_cert_issue_status' AND dict_value = 'pending');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '已颁发', 'granted', 'jst_cert_issue_status', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_cert_issue_status' AND dict_value = 'granted');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '已作废', 'voided', 'jst_cert_issue_status', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_cert_issue_status' AND dict_value = 'voided');

-- ---- 补充 jst_risk_case_status 风控案例状态 ----
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '待处理', 'open', 'jst_risk_case_status', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_risk_case_status' AND dict_value = 'open');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '已分配', 'assigned', 'jst_risk_case_status', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_risk_case_status' AND dict_value = 'assigned');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '处理中', 'processing', 'jst_risk_case_status', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_risk_case_status' AND dict_value = 'processing');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '审核中', 'reviewing', 'jst_risk_case_status', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_risk_case_status' AND dict_value = 'reviewing');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 5, '已关闭', 'closed', 'jst_risk_case_status', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_risk_case_status' AND dict_value = 'closed');

-- ---- 补充 jst_audit_status list_class（原有数据缺 list_class） ----
UPDATE sys_dict_data SET list_class = 'info' WHERE dict_type = 'jst_audit_status' AND dict_value = 'draft' AND (list_class IS NULL OR list_class = '');
UPDATE sys_dict_data SET list_class = 'warning' WHERE dict_type = 'jst_audit_status' AND dict_value = 'pending' AND (list_class IS NULL OR list_class = '');
UPDATE sys_dict_data SET list_class = 'success' WHERE dict_type = 'jst_audit_status' AND dict_value = 'online' AND (list_class IS NULL OR list_class = '');
UPDATE sys_dict_data SET list_class = 'info' WHERE dict_type = 'jst_audit_status' AND dict_value = 'offline' AND (list_class IS NULL OR list_class = '');
UPDATE sys_dict_data SET list_class = 'danger' WHERE dict_type = 'jst_audit_status' AND dict_value = 'rejected' AND (list_class IS NULL OR list_class = '');
UPDATE sys_dict_data SET list_class = 'success' WHERE dict_type = 'jst_audit_status' AND dict_value = 'approved' AND (list_class IS NULL OR list_class = '');

-- 补充合同状态 dict_data（contract_record 页面使用）
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '草稿', 'draft', 'jst_contract_status', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_contract_status' AND dict_value = 'draft');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '待签署', 'pending_sign', 'jst_contract_status', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_contract_status' AND dict_value = 'pending_sign');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '已签署', 'signed', 'jst_contract_status', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_contract_status' AND dict_value = 'signed');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '已过期', 'expired', 'jst_contract_status', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_contract_status' AND dict_value = 'expired');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 5, '已归档', 'archived', 'jst_contract_status', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_contract_status' AND dict_value = 'archived');

-- ============================================================
-- END
-- ============================================================
