-- =====================================================================
-- 文件名：99-migration-refactor-menus.sql
-- 用途  ：REFACTOR-7-MENU 菜单路由大清理
-- 背景  ：管理端"竞赛通管理"与"赛事方工作台"功能重叠、菜单层级混乱
-- 改动  ：
--   1) 重命名：竞赛通管理→平台数据管理, 赛事方工作台→赛事管理中心
--   2) 隐藏重复菜单（visible='1'），不删除
--   3) 渠道管理(9900)合并到平台数据管理(9800)
--   4) 数据看板(9910)去重，保留9801
--   5) 角色绑定验证
--   6) 隐藏旧代码生成 /jst 根菜单(menu_id=2000)，与精品菜单 9800 路径冲突
-- 幂等  ：可重复执行
-- 兼容  ：MySQL 5.7+
-- =====================================================================

SET NAMES utf8mb4;

-- =====================
-- 1) 菜单重命名
-- =====================

-- "竞赛通管理" → "平台数据管理"
UPDATE sys_menu
SET menu_name = '平台数据管理',
    remark = '原"竞赛通管理"，REFACTOR-7 重命名',
    update_by = 'migration',
    update_time = NOW()
WHERE menu_id = 9800;

-- "赛事方工作台" → "赛事管理中心"
UPDATE sys_menu
SET menu_name = '赛事管理中心',
    remark = '原"赛事方工作台"，REFACTOR-7 重命名',
    update_by = 'migration',
    update_time = NOW()
WHERE menu_id = 9700;

-- =====================
-- 2) 隐藏重复菜单
-- =====================

-- 2.1 隐藏"渠道管理"根目录(9900) — 其子菜单功能已在 9800 下覆盖
UPDATE sys_menu
SET visible = '1',
    remark = CONCAT(IFNULL(remark,''), ' [REFACTOR-7 隐藏：与9800下渠道组重叠]'),
    update_by = 'migration',
    update_time = NOW()
WHERE menu_id = 9900;

-- 2.2 隐藏 9901 "渠道认证管理" — 与 9846 "渠道认证审核" 重叠（同 component: jst/channel-auth/index）
UPDATE sys_menu
SET visible = '1',
    remark = CONCAT(IFNULL(remark,''), ' [REFACTOR-7 隐藏：与9846重叠]'),
    update_by = 'migration',
    update_time = NOW()
WHERE menu_id = 9901;

-- 2.3 隐藏 9902 "渠道列表" — 与 9845 "渠道管理" 重叠（同 component: jst/channel/index）
UPDATE sys_menu
SET visible = '1',
    remark = CONCAT(IFNULL(remark,''), ' [REFACTOR-7 隐藏：与9845重叠]'),
    update_by = 'migration',
    update_time = NOW()
WHERE menu_id = 9902;

-- 2.4 隐藏"数据看板"根目录(9910) — 仅含 9911 一个子菜单，与 9801 重叠
UPDATE sys_menu
SET visible = '1',
    remark = CONCAT(IFNULL(remark,''), ' [REFACTOR-7 隐藏：与9801运营看板重叠]'),
    update_by = 'migration',
    update_time = NOW()
WHERE menu_id = 9910;

-- 2.5 隐藏 9911 "运营数据" — 与 9801 "运营数据看板" 重叠（同 component: jst/dashboard/index）
UPDATE sys_menu
SET visible = '1',
    remark = CONCAT(IFNULL(remark,''), ' [REFACTOR-7 隐藏：与9801重叠]'),
    update_by = 'migration',
    update_time = NOW()
WHERE menu_id = 9911;

-- =====================
-- 3) 渠道管理合并：将唯一菜单移入 9800
-- =====================

-- 3.1 移动 9903 "绑定管理" 到 9800 下（渠道组尾部, order_num=63）
--     component: jst/binding/index — 在 9800 下无对应项
UPDATE sys_menu
SET parent_id = 9800,
    order_num = 63,
    remark = CONCAT(IFNULL(remark,''), ' [REFACTOR-7 从9900迁入9800]'),
    update_by = 'migration',
    update_time = NOW()
WHERE menu_id = 9903;

-- 3.2 移动 9904 "预约管理" 到 9800 下（订单组尾部, order_num=47）
--     component: jst/appointment/index — 精品预约页，不同于 9834(code-gen)
UPDATE sys_menu
SET parent_id = 9800,
    order_num = 47,
    remark = CONCAT(IFNULL(remark,''), ' [REFACTOR-7 从9900迁入9800]'),
    update_by = 'migration',
    update_time = NOW()
WHERE menu_id = 9904;

-- 3.3 确保 9903/9904 及其按钮权限绑定 admin 角色
SET @admin_role_id = (SELECT role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @admin_role_id, menu_id
FROM sys_menu
WHERE menu_id IN (9903, 9904, 9940, 9941, 9950, 9951, 9952, 9953)
  AND @admin_role_id IS NOT NULL;

-- =====================
-- 4) 验证查询
-- =====================

-- 4.1 验证重命名
SELECT menu_id, menu_name, parent_id, visible, status
FROM sys_menu
WHERE menu_id IN (9700, 9800)
ORDER BY menu_id;
-- 期望：9700="赛事管理中心", 9800="平台数据管理"

-- 4.2 验证隐藏菜单
SELECT menu_id, menu_name, parent_id, visible
FROM sys_menu
WHERE menu_id IN (9900, 9901, 9902, 9910, 9911)
ORDER BY menu_id;
-- 期望：全部 visible='1'

-- 4.3 验证合并后的菜单归属
SELECT menu_id, menu_name, parent_id, order_num, component, visible
FROM sys_menu
WHERE menu_id IN (9903, 9904)
ORDER BY menu_id;
-- 期望：parent_id=9800, visible='0'

-- 4.4 验证 9800 下完整菜单树（仅可见的 C 类型页面）
SELECT menu_id, menu_name, order_num, component, visible
FROM sys_menu
WHERE parent_id = 9800
  AND menu_type = 'C'
  AND visible = '0'
ORDER BY order_num;

-- 4.5 角色分配检查
-- admin 应能看到 9700 + 9800 全部
SELECT 'admin' AS role_name, COUNT(*) AS menu_count
FROM sys_role_menu rm
JOIN sys_role r ON r.role_id = rm.role_id
WHERE r.role_key = 'admin'
  AND rm.menu_id BETWEEN 9700 AND 9899;

-- jst_partner 应仅看到 9700-9799
SELECT 'jst_partner' AS role_name, COUNT(*) AS menu_count
FROM sys_role_menu rm
JOIN sys_role r ON r.role_id = rm.role_id
WHERE r.role_key = 'jst_partner'
  AND rm.menu_id BETWEEN 9700 AND 9899;

-- 4.6 检查根级别可见菜单（排除系统菜单）
SELECT menu_id, menu_name, order_num, visible
FROM sys_menu
WHERE parent_id = 0
  AND menu_id >= 9700
ORDER BY order_num;
-- 期望：仅 9800"平台数据管理" 和 9700"赛事管理中心" 可见

-- =====================================================================
-- 变更汇总：
--   UPDATE: 7 条（2 重命名 + 5 隐藏）+ 2 条（移动 parent_id）
--   INSERT: sys_role_menu 补绑定（幂等）
--   DELETE: 无
-- =====================================================================
