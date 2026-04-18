-- =====================================================================
-- 文件名：99-migration-fix-menu-hierarchy.sql
-- 用途  ：修复 ADMIN-FIX-URGENT 导致的 404（菜单层级错位）
-- 背景  ：
--   1) 99-migration-polished-menus.sql 要求 9810-9897 全部 parent_id=9800
--      ⇒ URL 为 /jst/xxx，与 permission.js JST_FLAT_FEATURE_PATHS 一致
--   2) 但当前 DB 中这些菜单全部 parent_id=0（平铺在根），并新增了 9753-9762
--      这些 group-* 目录当作二级分组
--   3) 前端 permission.js 期望 /jst/xxx 前缀，dashboard candidates 全部用
--      /jst/xxx，DB 当前状态下所有 candidate 都找不到 → 点击 404
--   4) A 卡（ADMIN-FIX-URGENT）把 dashboard 的 /jst/group-* 兜底删了，
--      未解决根因反而放大了问题
-- 修复策略：
--   1) 把所有 jst/ 组件菜单 re-parent 到 9800（符合原始迁移脚本意图）
--   2) 隐藏 9753-9762 那批 group-* 伪目录（运行时 permission.js 会自己分组）
--   3) sys_role_menu 不动（按 menu_id 绑定，层级变化不影响）
-- =====================================================================

SET NAMES utf8mb4;

START TRANSACTION;

-- =====================
-- 1) 精品页（C 型）re-parent 到 9800
-- =====================
-- 包括原迁移脚本的 9810~9897 以及后续新增的 9903/9904
UPDATE sys_menu
SET parent_id = 9800,
    update_by = 'fix-menu-hierarchy',
    update_time = NOW()
WHERE menu_id IN (
    -- 赛事运营
    9810, 9811, 9812, 9813, 9814,
    -- 课程
    9820, 9821,
    -- 订单交易
    9830, 9831, 9832, 9833, 9834, 9835, 9836,
    -- 用户渠道
    9840, 9841, 9842, 9845, 9846, 9847,
    -- 营销权益
    9850, 9851, 9852, 9853, 9854, 9855, 9856, 9857,
    -- 积分商城
    9860, 9861, 9862, 9863, 9864, 9865, 9866, 9867, 9868,
    -- 公告消息
    9870, 9871, 9872,
    -- 赛事数据
    9880, 9881, 9882, 9883, 9884, 9885,
    -- 财务
    9890, 9891, 9892,
    -- 风控
    9893, 9894, 9895, 9896, 9897,
    -- 后加的两个
    9903, 9904
);

-- =====================
-- 2) 隐藏失效的 group-* 伪目录（前端会运行时分组）
-- =====================
UPDATE sys_menu
SET visible = '1',
    status = '1',
    update_by = 'fix-menu-hierarchy',
    update_time = NOW()
WHERE menu_id IN (
    9753, -- 营销权益
    9754, -- 积分商城
    9755, -- 赛事数据
    9756, -- 财务管理
    9757, -- 风控审计
    9760, -- 赛事运营
    9761, -- 订单交易
    9762  -- 用户渠道
);

-- =====================
-- 3) 保证 9800 本身可见且正常
-- =====================
UPDATE sys_menu
SET visible = '0',
    status = '0',
    update_by = 'fix-menu-hierarchy',
    update_time = NOW()
WHERE menu_id = 9800;

COMMIT;

-- =====================
-- 4) 验证查询
-- =====================
-- 4.1 9800 下应该有 ~55 个精品页子菜单
SELECT COUNT(*) AS under_9800 FROM sys_menu WHERE parent_id = 9800;

-- 4.2 不应再有 component LIKE 'jst/%' 且 parent_id=0 的菜单
SELECT menu_id, menu_name, parent_id, path, component
FROM sys_menu
WHERE component LIKE 'jst/%' AND parent_id = 0
ORDER BY menu_id;

-- 4.3 group-* 伪目录应全部 visible='1'
SELECT menu_id, menu_name, visible, status
FROM sys_menu
WHERE menu_id IN (9753, 9754, 9755, 9756, 9757, 9760, 9761, 9762)
ORDER BY menu_id;
