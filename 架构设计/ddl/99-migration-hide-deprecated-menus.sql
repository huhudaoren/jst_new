-- =====================================================================
-- 文件名：99-migration-hide-deprecated-menus.sql
-- 用途  : 隐藏已被精品页替代的旧代码生成菜单
-- 背景  : WEB-ADMIN-UX 任务卡（菜单精简）
-- 幂等  : 可重复执行
-- =====================================================================

SET NAMES utf8mb4;

-- 精确按 component 路径隐藏，避免误伤精品页菜单
UPDATE sys_menu
SET visible = '1'
WHERE component IN (
  'jst/event/jst_contest/index',
  'jst/event/jst_course/index',
  'jst/event/jst_enroll_record/index',
  'jst/marketing/jst_coupon_template/index',
  'jst/marketing/jst_rights_template/index',
  'jst/order/jst_order_main/index',
  'jst/order/jst_refund_record/index',
  'jst/message/jst_notice/index',
  'jst/user/jst_user/index'
);

-- 验证：应返回 9 条且 visible = '1'
SELECT menu_id, menu_name, component, visible
FROM sys_menu
WHERE component IN (
  'jst/event/jst_contest/index',
  'jst/event/jst_course/index',
  'jst/event/jst_enroll_record/index',
  'jst/marketing/jst_coupon_template/index',
  'jst/marketing/jst_rights_template/index',
  'jst/order/jst_order_main/index',
  'jst/order/jst_refund_record/index',
  'jst/message/jst_notice/index',
  'jst/user/jst_user/index'
)
ORDER BY menu_id;
