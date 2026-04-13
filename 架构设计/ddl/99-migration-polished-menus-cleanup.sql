-- =====================================================================
-- 文件名：99-migration-polished-menus-cleanup.sql
-- 用途  ：清理“竞赛通管理”重复菜单树（保留 9800 精品平铺）
-- 背景  ：混合导航模式下顶部/左侧出现双子菜单（旧树与精品树同时可见）
-- 说明  ：
--   1) 保留 menu_id=9800 的精品树
--   2) 将其他同名根菜单与旧分组目录统一隐藏
--   3) 不删除数据、不改权限模型，仅 visible='1' 隐藏
-- =====================================================================

SET NAMES utf8mb4;

-- =====================
-- 1) 保证精品根菜单可见
-- =====================
UPDATE sys_menu
SET visible = '0',
    status = '0',
    update_by = 'migration',
    update_time = NOW()
WHERE menu_id = 9800;

-- =====================
-- 2) 隐藏重复的“竞赛通管理”根菜单（保留 9800）
-- =====================
UPDATE sys_menu
SET visible = '1',
    update_by = 'migration',
    update_time = NOW()
WHERE parent_id = 0
  AND menu_name = '竞赛通管理'
  AND menu_id <> 9800;

-- =====================
-- 3) 隐藏旧分组目录，防止在“更多菜单”中再次出现
-- =====================
UPDATE sys_menu
SET visible = '1',
    update_by = 'migration',
    update_time = NOW()
WHERE menu_type = 'M'
  AND menu_name IN (
    '用户与身份',
    '赛事与报名',
    '订单与资金',
    '渠道与返点',
    '积分与商城',
    '营销中心',
    '消息中心',
    '风控中心',
    '财务中心'
  );

-- =====================
-- 4) 验证查询
-- =====================
-- 4.1 应仅有一个可见“竞赛通管理”根菜单（menu_id=9800）
SELECT menu_id, menu_name, parent_id, path, visible, status
FROM sys_menu
WHERE menu_name = '竞赛通管理'
  AND parent_id = 0
ORDER BY menu_id;

-- 4.2 旧分组目录应为 visible='1'
SELECT menu_id, menu_name, parent_id, path, visible
FROM sys_menu
WHERE menu_name IN (
  '用户与身份',
  '赛事与报名',
  '订单与资金',
  '渠道与返点',
  '积分与商城',
  '营销中心',
  '消息中心',
  '风控中心',
  '财务中心'
)
ORDER BY parent_id, menu_id;
