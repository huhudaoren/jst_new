-- =====================================================
-- 销售管理：角色 / 菜单 / 权限点
-- 关联 spec §5.1 / 关联 plan-01 Task 5
--
-- ID 段约定（避免与既有数据冲突）：
--   - 角色 ID：501 jst_sales / 502 jst_sales_manager
--     （CLAUDE.md 既有角色 ID 1=admin、2-7=运营/财务/客服/营销/风控/分析师、8=jst_partner，
--      501-502 留出充足缓冲区）
--   - 菜单 ID：9785 / 978500-978599（销售工作台子菜单及按钮）
--                976210-976299（admin 销售管理子菜单及按钮，挂在 9762 用户渠道下）
--     （既有菜单段：9700 赛事管理中心、9800 平台数据管理；选 978/976 段不冲突）
--
-- ⚠️ 一次性迁移文件：role_id / menu_id 是 PK，重跑会因 Duplicate 报错。
-- 回滚命令：
--   DELETE FROM sys_role_menu WHERE role_id IN (501,502);
--   DELETE FROM sys_role WHERE role_id IN (501,502);
--   DELETE FROM sys_menu WHERE menu_id BETWEEN 978500 AND 978599;
--   DELETE FROM sys_menu WHERE menu_id BETWEEN 976210 AND 976299;
--   DELETE FROM sys_menu WHERE menu_id = 9785;
-- =====================================================
SET NAMES utf8mb4;

-- ========== 1. 角色 ==========
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark) VALUES
(501, '销售',     'jst_sales',         50, '5', 1, 1, '0', '0', 'admin', NOW(), '销售个人，SalesScope 数据隔离 + 金额脱敏'),
(502, '销售主管', 'jst_sales_manager', 51, '5', 1, 1, '0', '0', 'admin', NOW(), '看下属全明细 + 完整金额，可派任务、调整下属归属');

-- ========== 2. 销售工作台菜单 (9785 段) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(9785,    '销售工作台', 0,    50, 'sales-workbench', NULL,                          NULL, 1, 0, 'M', '0', '0', NULL,                       'people',     'admin', NOW(), '销售/销售主管专用'),
(978501,  '我的渠道',   9785, 1,  'channels',        'sales/me/channels/index',     NULL, 1, 0, 'C', '0', '0', 'sales:me:channels:list',   'tree',       'admin', NOW(), NULL),
(978502,  '我的业绩',   9785, 2,  'performance',     'sales/me/performance/index',  NULL, 1, 0, 'C', '0', '0', 'sales:me:performance:view','chart',      'admin', NOW(), NULL),
(978503,  '我的预录入', 9785, 3,  'pre-register',    'sales/me/preregister/index',  NULL, 1, 0, 'C', '0', '0', 'sales:me:prereg:list',     'edit',       'admin', NOW(), NULL),
(978504,  '跟进任务',   9785, 4,  'tasks',           'sales/me/tasks/index',        NULL, 1, 0, 'C', '0', '0', 'sales:me:tasks:list',      'checkbox',   'admin', NOW(), NULL),
(978505,  '团队管理',   9785, 5,  'team',            'sales/manager/team/index',    NULL, 1, 0, 'C', '0', '0', 'sales:manager:team:view',  'team',       'admin', NOW(), '仅 jst_sales_manager 可见'),
(978506,  '业绩明细',   9785, 6,  'settlement',      'sales/manager/settlement/index', NULL, 1, 0, 'C', '0', '0', 'sales:manager:settlement:view', 'money', 'admin', NOW(), '仅 jst_sales_manager / admin 可见，含金额');

-- ========== 3. admin 端菜单 (9762 用户渠道下追加 3 项) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(976210, '销售管理',         9762, 10, 'sales',         'jst/sales/index',     NULL, 1, 0, 'C', '0', '0', 'jst:sales:list',     'people',  'admin', NOW(), 'admin 看全员销售档案'),
(976211, '销售业绩看板',     9762, 11, 'sales-board',   'jst/sales/dashboard', NULL, 1, 0, 'C', '0', '0', 'jst:sales:dashboard','chart',   'admin', NOW(), '每销售业绩/提成成本/跟进活跃度'),
(976212, '归属冲突队列',     9762, 12, 'sales-conflict','jst/sales/conflict',  NULL, 1, 0, 'C', '0', '0', 'jst:sales:conflict', 'shield',  'admin', NOW(), 'admin 处理预录入冲突');

-- ========== 4. 销售工作台 — 按钮权限点 ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, create_by, create_time) VALUES
(978511, '渠道详情', 978501, 1, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:channels:detail',  'admin', NOW()),
(978512, '查看完整手机号', 978501, 2, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:channels:phone-full', 'admin', NOW()),
(978531, '新建预录入', 978503, 1, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:prereg:add',     'admin', NOW()),
(978532, '续期预录入', 978503, 2, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:prereg:renew',   'admin', NOW()),
(978533, '删除预录入', 978503, 3, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:prereg:remove',  'admin', NOW()),
(978521, '新建跟进',  978501, 11, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:followup:add',  'admin', NOW()),
(978522, '编辑跟进',  978501, 12, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:followup:edit', 'admin', NOW()),
(978523, '打标签',    978501, 13, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:tags:edit',     'admin', NOW()),
(978541, '完成任务',  978504, 1, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:tasks:complete', 'admin', NOW()),
(978551, '派任务',     978505, 1, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:manager:tasks:assign', 'admin', NOW()),
(978552, '调整下属归属',978505, 2, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:manager:transfer',     'admin', NOW());

-- ========== 5. admin 销售管理 — 按钮权限点 ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, create_by, create_time) VALUES
(976220, '新建销售',           976210, 1,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:add',            'admin', NOW()),
(976221, '修改费率',           976210, 2,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:edit:rate',      'admin', NOW()),
(976222, '设置主管',           976210, 3,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:edit:manager',   'admin', NOW()),
(976223, '提交离职申请',       976210, 4,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:resign-apply',   'admin', NOW()),
(976224, '立即执行离职',       976210, 5,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:resign-execute', 'admin', NOW()),
(976225, '终结过渡期',         976210, 6,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:transition-end', 'admin', NOW()),
(976230, '裁决冲突',           976212, 1,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:conflict:resolve', 'admin', NOW()),
(976231, '手动绑定渠道-销售',  976212, 2,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:binding:manual',   'admin', NOW()),
(976240, '审核月结单',         976210, 10, NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:settlement:review','admin', NOW()),
(976241, '录入打款流水',       976210, 11, NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:settlement:pay',   'admin', NOW()),
(976250, '查看脱敏审计',       976210, 20, NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:audit:view',       'admin', NOW());

-- ========== 6. 角色-菜单绑定 ==========
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(501, 9785),
(501, 978501), (501, 978511), (501, 978512), (501, 978521), (501, 978522), (501, 978523),
(501, 978502),
(501, 978503), (501, 978531), (501, 978532), (501, 978533),
(501, 978504), (501, 978541);

INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(502, 9785),
(502, 978501), (502, 978511), (502, 978512), (502, 978521), (502, 978522), (502, 978523),
(502, 978502),
(502, 978503), (502, 978531), (502, 978532), (502, 978533),
(502, 978504), (502, 978541),
(502, 978505), (502, 978551), (502, 978552),
(502, 978506);
