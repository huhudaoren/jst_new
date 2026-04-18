-- ============================================================
-- ADMIN-UX-B1 字典补齐
-- 幂等：WHERE NOT EXISTS，可重复执行
-- ============================================================

-- ---- 1. jst_form_field_type 报名表单字段类型 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '报名表单字段类型', 'jst_form_field_type', '0', 'admin', NOW(), '管理后台报名表单 Schema 可视化字段类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_form_field_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '单行文本', 'text', 'jst_form_field_type', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'text');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '多行文本', 'textarea', 'jst_form_field_type', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'textarea');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '手机号', 'phone', 'jst_form_field_type', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'phone');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '身份证号', 'idcard', 'jst_form_field_type', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'idcard');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 5, '年龄', 'age', 'jst_form_field_type', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'age');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 6, '数字', 'number', 'jst_form_field_type', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'number');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 7, '单选', 'radio', 'jst_form_field_type', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'radio');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 8, '多选', 'checkbox', 'jst_form_field_type', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'checkbox');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 9, '下拉选择', 'select', 'jst_form_field_type', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'select');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 10, '日期', 'date', 'jst_form_field_type', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'date');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 11, '图片', 'image', 'jst_form_field_type', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'image');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 12, '音频', 'audio', 'jst_form_field_type', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'audio');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 13, '视频', 'video', 'jst_form_field_type', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'video');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 14, '文件', 'file', 'jst_form_field_type', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'file');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 15, '分组(raw)', 'group', 'jst_form_field_type', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'group');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 16, '条件组(raw)', 'conditional', 'jst_form_field_type', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_form_field_type' AND dict_value = 'conditional');

-- ---- 2. jst_message_scene 消息业务场景 ----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '消息业务场景', 'jst_message_scene', '0', 'admin', NOW(), '消息模板业务场景'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_message_scene');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 1, '认证结果', 'auth_result', 'jst_message_scene', '', 'info', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_message_scene' AND dict_value = 'auth_result');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 2, '提现结果', 'withdraw_result', 'jst_message_scene', '', 'warning', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_message_scene' AND dict_value = 'withdraw_result');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 3, '结算结果', 'settle_result', 'jst_message_scene', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_message_scene' AND dict_value = 'settle_result');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 4, '积分变动', 'points_change', 'jst_message_scene', '', 'primary', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_message_scene' AND dict_value = 'points_change');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 5, '发货通知', 'ship', 'jst_message_scene', '', 'success', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_message_scene' AND dict_value = 'ship');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, create_by, create_time)
SELECT 6, '售后通知', 'aftersale', 'jst_message_scene', '', 'danger', '0', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'jst_message_scene' AND dict_value = 'aftersale');

-- ============================================================
-- END
-- ============================================================
