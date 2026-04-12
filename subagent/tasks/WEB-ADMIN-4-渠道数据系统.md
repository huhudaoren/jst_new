# 任务卡 WEB-ADMIN-4 - 渠道管理与数据看板

## 阶段
E-4-WEB / **ruoyi-ui**（Web Admin Agent）

## 背景
平台运营需要管理渠道认证、查看平台数据看板。后端 API 已就绪。

## 系统提示词
使用 `subagent/WEB_ADMIN_AGENT_PROMPT.md`

## 交付物

### 页面清单

#### 1. 渠道认证管理 `views/jst/channel-auth/`
- **index.vue** — 认证申请列表
  - 搜索：applyName / channelType / applyStatus / 提交时间
  - 表格：applyId / applyName / channelType / applyStatus / rejectCount / submitTime / auditTime
  - 操作：审核通过 / 驳回（填驳回原因）/ 暂停 / 查看材料
  - 详情抽屉：展示 materialsJson（证件照片等）
  - API：`api/jst/organizer/` — 渠道认证审核

#### 2. 渠道列表 `views/jst/channel/`
- **index.vue** — 已认证渠道列表
  - 搜索：channelName / channelType / status
  - 表格：channelId / channelName / channelType / contactMobile / authStatus / bindStudentCount / totalRebate / status
  - 操作：查看详情 / 暂停 / 恢复
  - API：`api/jst/channel/` — 渠道列表

#### 3. 绑定关系管理 `views/jst/binding/`
- **index.vue** — 学生-渠道绑定列表
  - 搜索：studentName / channelName / status
  - 表格：bindingId / studentName / channelName / bindTime / status
  - 操作：解绑（admin 强制解绑）
  - API：`api/jst/user/` — 绑定关系

#### 4. 预约管理 `views/jst/appointment/`
- **index.vue** — 预约记录列表
  - 搜索：contestName / participantName / mainStatus / appointmentDate
  - 表格：appointmentId / contestName / participantName / appointmentDate / timeSlot / mainStatus
  - 操作：查看详情 / 取消预约
  - API：预约相关接口

#### 5. 平台数据看板 `views/jst/dashboard/`
- **index.vue** — 平台运营数据（管理员首页）
  - 顶部 4 格统计：今日订单数 / 今日营收 / 活跃用户 / 待处理事项
  - 图表区（用 echarts 或纯数字，不引入新依赖则用纯数字+趋势箭头）：
    - 近 7 天订单趋势
    - 赛事报名排行 Top 5
    - 渠道返点排行 Top 5
  - 待办事项快捷入口：待审核赛事 / 待审核退款 / 待审核提现
  - 如果若依已有 echarts 则复用，否则用纯文字+数字展示
  - API：可复用已有列表接口聚合，或用简单 SQL 统计

#### 6. 菜单注册 SQL

**新建** `架构设计/ddl/99-migration-admin-menus.sql`

为以上页面注册 `sys_menu`（menu_id 从 9900 开始），绑定到 admin 角色。包含：
- 一级菜单：用户管理 / 赛事管理 / 订单管理 / 内容管理 / 渠道管理 / 数据看板
- 二级菜单：各子页面
- 按钮权限：list / query / add / edit / remove / audit 等

## DoD
- [ ] 5 个管理模块 + 1 个数据看板页面完成
- [ ] 菜单注册 SQL 幂等
- [ ] 审核操作有二次确认
- [ ] `npm run build:prod` SUCCESS
- [ ] 任务报告 `WEB-ADMIN-4-回答.md`

## 不许做
- ❌ 不许改后端
- ❌ 不许改小程序
- ❌ 不许引入 echarts 等新依赖（用纯 CSS + 数字展示，若若依已有 echarts 则可复用）

## 优先级：P1
---
派发时间：2026-04-12
