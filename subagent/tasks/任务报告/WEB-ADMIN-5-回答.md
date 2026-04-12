# WEB-ADMIN-5 交付报告（赛事周边与 Partner 补齐）

## 1. 任务概览
- 任务卡：`subagent/tasks/WEB-ADMIN-5-赛事周边与Partner补齐.md`
- 目标：增强 6 个 `jst/event` 页面 + 补齐 2 个 Partner 页面 + 清理 3 个重复页标记 + 菜单隐藏处理。
- 执行范围：仅前端 `ruoyi-ui` 与菜单 SQL（`gen/ruoyi`），未改后端 Java。

## 2. 已完成内容

### 2.1 增强页面（6 页）
已将以下页面从代码生成风格升级为“精品页”结构（对齐 `views/jst/user/index.vue`、`views/jst/contest/index.vue`）：
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_score_record/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_cert_record/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_cert_template/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_enroll_form_template/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_event_partner/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_course_learn_record/index.vue`

每页已补齐：
- Hero 区 + 查询区 + 表格区 + 分页区
- 手机端卡片列表（`isMobile` 分支）与触控按钮尺寸
- 状态标签（`JstStatusBadge`）与空态（`el-empty`）
- 详情抽屉（`el-drawer`，手机端全屏）
- 查询增强（输入/状态下拉/时间范围）
- 权限点统一使用 `v-hasPermi`（并修正旧页面中的 `system:*` 前缀为 `jst:event:*`）

专项增强：
- `jst_cert_record`：增加证书预览（弹窗内 iframe + 新窗口打开）
- `jst_cert_template`：增加底图缩略图与 JSON 详情预览
- `jst_enroll_form_template`：增加 JSON 预览与“复制模板”操作
- `jst_course_learn_record`：增加学习进度条展示与时长格式化

### 2.2 新建/补齐 Partner 页面（2 页）
- 重构 `RuoYi-Vue/ruoyi-ui/src/views/partner/writeoff.vue`
  - 保留扫码核销与手工输入
  - 补齐记录检索（赛事ID/日期范围）、列表分页、详情抽屉、手机端卡片
- 新建 `RuoYi-Vue/ruoyi-ui/src/views/partner/settings.vue`
  - 赛事方账号信息展示
  - 联系方式编辑（昵称/手机号/邮箱/性别）
  - 修改密码（旧/新/确认 + 表单校验）

### 2.3 重复页处理（3 页）
已在以下页面 `<template>` 顶部添加标记：
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_contest/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_course/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_enroll_record/index.vue`

标记内容：
- `<!-- DEPRECATED: 请使用 jst/contest/index.vue -->`

### 2.4 菜单隐藏（visible=1）
已将重复页面菜单项 `visible` 改为 `1`：
- `RuoYi-Vue/gen/ruoyi/jst_contestMenu.sql`
- `RuoYi-Vue/gen/ruoyi/jst_courseMenu.sql`
- `RuoYi-Vue/gen/ruoyi/jst_enroll_recordMenu.sql`
- `RuoYi-Vue/gen/ruoyi/_all-menu-jst.sql`

## 3. 构建验证
- 执行目录：`D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui`
- 执行命令：`npm run build:prod`
- 结果：**通过**
- 说明：存在 webpack `asset size limit` / `entrypoint size limit` 告警（历史体积告警），不影响本次编译通过。

## 4. 交付文件清单（本卡相关）
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_score_record/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_cert_record/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_cert_template/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_enroll_form_template/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_event_partner/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_course_learn_record/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/partner/writeoff.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/partner/settings.vue`（新增）
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_contest/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_course/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_enroll_record/index.vue`
- `RuoYi-Vue/gen/ruoyi/jst_contestMenu.sql`
- `RuoYi-Vue/gen/ruoyi/jst_courseMenu.sql`
- `RuoYi-Vue/gen/ruoyi/jst_enroll_recordMenu.sql`
- `RuoYi-Vue/gen/ruoyi/_all-menu-jst.sql`

## 5. 备注
- 当前工作区存在其他并行改动（非本卡范围文件），本次未回滚、未覆盖，仅在本卡文件内交付。
- 若后续需要，我可以补一轮“按页面截图验收”的视觉巡检清单（桌面 + 手机断点）。
