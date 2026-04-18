-- FIX-MENU-DEDUP-FORMTEMPLATE
-- 2026-04-17
--
-- 背景：平台数据管理下同时存在两个"表单模板"入口：
--   9842 "表单模板管理"（parent=9762 用户渠道）：
--        views/jst/form-template/index.vue（polished + B1 可视化编辑器）
--   9883 "报名表单模板"（parent=9755 赛事数据）：
--        views/jst/event/jst_enroll_form_template/index.vue（旧代码生成页）
--
-- 处置：保留 9842 作为唯一入口，隐藏 9883。
-- 3039（parent=2200→2000 "竞赛通业务"）父链已隐藏，无需处理。

UPDATE sys_menu
SET visible = '1'
WHERE menu_id = 9883;

-- 回滚：UPDATE sys_menu SET visible = '0' WHERE menu_id = 9883;
