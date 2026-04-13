# WEB-ADMIN-FIX 路由与菜单修复（P0）交付报告

## 1. 任务目标与结论
- 目标：修复管理端首页与运营看板跳转 404，核心通过 `sys_menu` 注册精品页路由并统一前端跳转策略。
- 结论：已完成菜单迁移 SQL、新首页/看板路径收敛、deprecated 页面引导修正、404 体验优化与标题统一，`npm run build:prod` 通过。

## 2. 本次改动清单

### 2.1 菜单迁移 SQL（核心）
- 新增文件：`架构设计/ddl/99-migration-polished-menus.sql`
- 关键点：
  - 使用 `9800-9899` 号段。
  - 注册 `/jst` 根目录与 55 条精品页菜单（含 hidden 编辑/详情页）。
  - 使用 `INSERT ... ON DUPLICATE KEY UPDATE` 保证幂等。
  - 绑定 `admin` 与 `jst_admin`（存在时）到 `9800-9899` 菜单。
  - 补充排序微调：
    - `menu_id=9900`（渠道管理）排序设为 7
    - `menu_id=9700`（赛事方工作台）排序设为 15

### 2.2 首页与看板跳转去 404
- 修改文件：
  - `RuoYi-Vue/ruoyi-ui/src/views/index.vue`
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/dashboard/index.vue`
- 改动点：
  - 入口候选路径只保留新菜单路径，不再回落到 deprecated 路径。
  - 使用可达性判定后再跳转，不可达时禁用按钮并提示“当前账号未配置该菜单或无权限”。
  - 首页与看板涉及路径已与新 SQL 对齐（已做脚本交叉校验，0 缺失）。

### 2.3 Deprecated 页面跳转指向修复
- 修改文件：
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_contest/index.vue`
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_course/index.vue`
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_enroll_record/index.vue`
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/order/jst_order_main/index.vue`
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/order/jst_refund_record/index.vue`
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/user/jst_user/index.vue`
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/message/jst_notice/index.vue`
- 改动点：
  - 顶部补充或修正“页面已废弃”提示和“前往新页面”按钮。
  - 所有入口统一指向可用精品页路径（如 `/jst/contest`、`/jst/order/admin-refund`、`/jst/notice`）。

### 2.4 404 与页面标题规范
- 修改文件：
  - `RuoYi-Vue/ruoyi-ui/src/views/error/404.vue`
  - `RuoYi-Vue/ruoyi-ui/src/permission.js`
- 改动点：
  - 404 页增加“返回首页 / 返回上一页”双按钮与友好提示文案。
  - 新增移动端布局适配（`max-width: 768px`）。
  - 标题改为统一规则：`页面名称 - 竞赛通`（无标题时为 `竞赛通`）。

## 3. 验收与验证
- 构建验证：
  - 在 `RuoYi-Vue/ruoyi-ui` 执行 `npm run build:prod`，构建成功。
  - 仅有体积告警（非阻塞）。
- 路径一致性验证：
  - 已脚本校验 `index.vue + dashboard/index.vue` 的候选路径均可在新 SQL 找到对应菜单路径（缺失数 0）。

## 4. 需要主 Agent 执行/确认的事项
- 必须执行 SQL 文件：
  - `架构设计/ddl/99-migration-polished-menus.sql`
- 执行后建议立即做三项数据库核验：
  1. `9800-9899` 菜单是否全部落库；
  2. `admin` / `jst_admin` 是否成功绑定（若 `jst_admin` 不存在可忽略）；
  3. 是否存在与历史菜单的同级 path 冲突（重点检查 `/jst/*`）。

## 5. 当前缺口与后续资源需求
- 权限点精确性风险：
  - 部分 `perms` 为按现有前端/历史规则推断，建议后续由主 Agent 联合后端按 `@PreAuthorize` 做一次精确回填。
- 环境联调资源：
  - 需要可执行迁移 SQL 的测试库账号（含 `sys_menu/sys_role/sys_role_menu` 写权限）。
  - 需要至少两个账号做回归：
    - `admin`（全量可见）
    - 非全权限运营账号（验证禁用态与提示逻辑）
- 回归清单建议（主 Agent 侧）：
  - 首页 Hero / 快捷入口 / 流程卡逐项点击；
  - 看板待办卡与快捷操作逐项点击；
  - 9 个 deprecated 页“前往新页面”按钮；
  - 无权限账号点击禁用入口后的提示行为。

## 6. 影响范围说明
- 本次未修改后端 Java 业务代码与接口地址。
- 变更集中在：
  - 菜单迁移 SQL
  - 首页/看板前端跳转逻辑
  - deprecated 引导文案与按钮
  - 404 展示与标题拼接规则
