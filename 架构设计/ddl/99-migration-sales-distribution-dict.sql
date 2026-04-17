-- =====================================================
-- 销售管理字典数据
-- 关联 spec §1
-- 字典段：jst_sales_*（避免与既有字典冲突）
-- =====================================================
SET NAMES utf8mb4;

-- ========== 字典类型定义 ==========
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES
('销售业务类型',          'jst_sales_business_type',  '0', 'admin', NOW(), '销售提成按业务类型差异化'),
('跟进类型',              'jst_sales_followup_type',  '0', 'admin', NOW(), 'CRM 跟进沟通类型'),
('跟进意向',              'jst_sales_mood',           '0', 'admin', NOW(), '跟进时记录的客户意向'),
('销售-渠道绑定来源',     'jst_sales_bind_source',    '0', 'admin', NOW(), '销售归属绑定的来源'),
('销售状态',              'jst_sales_status',         '0', 'admin', NOW(), '销售在职/申请离职/已离职'),
('销售提成台账状态',      'jst_sales_ledger_status',  '0', 'admin', NOW(), '销售提成 ledger 状态机'),
('销售月结单状态',        'jst_sales_settlement_status', '0', 'admin', NOW(), '月结单状态机'),
('渠道邀请分享场景',      'jst_channel_share_scene',  '0', 'admin', NOW(), '渠道邀请关系建立的场景'),
('渠道标签',              'jst_sales_channel_tag',    '0', 'admin', NOW(), '销售给渠道打的标签');

-- ========== jst_sales_business_type ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES
(1, '赛事报名',     'enroll',                'jst_sales_business_type', NULL, 'primary', 'N', '0', 'admin', NOW(), NULL),
(2, '课程购买',     'course',                'jst_sales_business_type', NULL, 'success', 'N', '0', 'admin', NOW(), NULL),
(3, '商城兑换',     'mall',                  'jst_sales_business_type', NULL, 'info',    'N', '0', 'admin', NOW(), NULL),
(4, '团队预约',     'appointment_team',      'jst_sales_business_type', NULL, 'warning', 'N', '0', 'admin', NOW(), NULL),
(5, '个人预约',     'appointment_personal',  'jst_sales_business_type', NULL, 'warning', 'N', '0', 'admin', NOW(), NULL);

-- ========== jst_sales_followup_type ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '电话',  'phone',  'jst_sales_followup_type', 'primary', '0', 'admin', NOW()),
(2, '微信',  'wechat', 'jst_sales_followup_type', 'success', '0', 'admin', NOW()),
(3, '拜访',  'visit',  'jst_sales_followup_type', 'warning', '0', 'admin', NOW()),
(4, '邮件',  'email',  'jst_sales_followup_type', 'info',    '0', 'admin', NOW()),
(5, '其他',  'other',  'jst_sales_followup_type', 'default', '0', 'admin', NOW());

-- ========== jst_sales_mood ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '意向高',   'high', 'jst_sales_mood', 'success', '0', 'admin', NOW()),
(2, '意向中',   'mid',  'jst_sales_mood', 'primary', '0', 'admin', NOW()),
(3, '意向低',   'low',  'jst_sales_mood', 'warning', '0', 'admin', NOW()),
(4, '无意向',   'none', 'jst_sales_mood', 'danger',  '0', 'admin', NOW());

-- ========== jst_sales_bind_source ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '预录入命中',     'pre_register',     'jst_sales_bind_source', 'success', '0', 'admin', NOW()),
(2, '业务编号命中',   'business_no',      'jst_sales_bind_source', 'primary', '0', 'admin', NOW()),
(3, 'admin 手动',     'manual',           'jst_sales_bind_source', 'info',    '0', 'admin', NOW()),
(4, '离职转移',       'transfer_resign',  'jst_sales_bind_source', 'warning', '0', 'admin', NOW()),
(5, '手动调整',       'manual_transfer',  'jst_sales_bind_source', 'info',    '0', 'admin', NOW());

-- ========== jst_sales_status ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '在职',           'active',                   'jst_sales_status', 'success', '0', 'admin', NOW()),
(2, '申请离职',       'resign_apply',             'jst_sales_status', 'warning', '0', 'admin', NOW()),
(3, '已离职待结算',   'resigned_pending_settle',  'jst_sales_status', 'info',    '0', 'admin', NOW()),
(4, '彻底离职',       'resigned_final',           'jst_sales_status', 'danger',  '0', 'admin', NOW());

-- ========== jst_sales_ledger_status ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '待计提',  'pending',   'jst_sales_ledger_status', 'info',    '0', 'admin', NOW()),
(2, '已计提',  'accrued',   'jst_sales_ledger_status', 'primary', '0', 'admin', NOW()),
(3, '已结算',  'settled',   'jst_sales_ledger_status', 'warning', '0', 'admin', NOW()),
(4, '已打款',  'paid',      'jst_sales_ledger_status', 'success', '0', 'admin', NOW()),
(5, '已驳回',  'rejected',  'jst_sales_ledger_status', 'danger',  '0', 'admin', NOW()),
(6, '已撤销',  'cancelled', 'jst_sales_ledger_status', 'default', '0', 'admin', NOW());

-- ========== jst_sales_settlement_status ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '待审核',  'pending_review', 'jst_sales_settlement_status', 'warning', '0', 'admin', NOW()),
(2, '已审核',  'approved',       'jst_sales_settlement_status', 'primary', '0', 'admin', NOW()),
(3, '已打款',  'paid',           'jst_sales_settlement_status', 'success', '0', 'admin', NOW()),
(4, '已驳回',  'rejected',       'jst_sales_settlement_status', 'danger',  '0', 'admin', NOW());

-- ========== jst_channel_share_scene ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '小程序分享', 'mp_share',     'jst_channel_share_scene', 'primary', '0', 'admin', NOW()),
(2, '二维码',     'qrcode',       'jst_channel_share_scene', 'info',    '0', 'admin', NOW()),
(3, 'admin 录入', 'manual_admin', 'jst_channel_share_scene', 'warning', '0', 'admin', NOW());

-- ========== jst_sales_channel_tag (默认 7 个，自定义以 custom: 前缀) ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, 'VIP',          'vip',           'jst_sales_channel_tag', 'success', '0', 'admin', NOW()),
(2, '重点维护',     'key_account',   'jst_sales_channel_tag', 'primary', '0', 'admin', NOW()),
(3, '流失风险',     'at_risk',       'jst_sales_channel_tag', 'danger',  '0', 'admin', NOW()),
(4, '新客',         'new',           'jst_sales_channel_tag', 'info',    '0', 'admin', NOW()),
(5, '沉默客户',     'silent',        'jst_sales_channel_tag', 'warning', '0', 'admin', NOW()),
(6, '高价值',       'high_value',    'jst_sales_channel_tag', 'success', '0', 'admin', NOW()),
(7, '试合作',       'trial',         'jst_sales_channel_tag', 'info',    '0', 'admin', NOW());
