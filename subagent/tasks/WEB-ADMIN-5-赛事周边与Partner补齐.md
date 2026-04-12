# WEB-ADMIN-5 — 赛事周边与 Partner 补齐

> 优先级：P1 | 预估：M | Agent：Web Admin Agent

---

## 一、目标

增强 6 个代码生成页面 + 新建 2 个 Partner 缺页，达到与 WEB-ADMIN-1~4 同等质量标准。

## 二、增强页面清单（6 页）

所有页面路径前缀：`ruoyi-ui/src/views/jst/`

| # | 文件 | 业务 | 增强要点 |
|---|---|---|---|
| 1 | `event/jst_score_record/index.vue` | 成绩记录 | +赛事名/参赛人搜索 +状态标签 +详情抽屉 |
| 2 | `event/jst_cert_record/index.vue` | 证书记录 | +赛事/参赛人/证书编号搜索 +状态标签 +证书预览 |
| 3 | `event/jst_cert_template/index.vue` | 证书模板 | +模板名搜索 +预览缩略图 +编辑弹窗 |
| 4 | `event/jst_enroll_form_template/index.vue` | 报名表单模板 | +模板名/赛事搜索 +JSON 预览 +复制模板操作 |
| 5 | `event/jst_event_partner/index.vue` | 赛事方关联 | +赛事名/赛事方名搜索 +状态 +详情抽屉 |
| 6 | `event/jst_course_learn_record/index.vue` | 学习记录 | +课程名/用户搜索 +进度条展示 +时间范围筛选 |

## 三、新建页面（2 页）

| # | 文件 | 业务 | 说明 |
|---|---|---|---|
| 7 | `views/partner/writeoff.vue` | 现场核销 | 菜单 9707 已注册。扫码入口 + 核销记录列表 + 核销详情。参考小程序 `appointment/scan.vue` 的业务逻辑 |
| 8 | `views/partner/settings.vue` | 账号设置 | 菜单 9708 已注册。赛事方信息展示 + 修改密码 + 联系方式编辑 |

## 四、清理重复页（3 页）

以下页面与 WEB-ADMIN-1 手工页面重复，在 `<template>` 顶部加注释标记 `<!-- DEPRECATED: 请使用 jst/contest/index.vue -->`，并在菜单中设为隐藏（`visible = 1`）：

| 文件 | 被替代者 |
|---|---|
| `event/jst_contest/index.vue` | `contest/index.vue` |
| `event/jst_course/index.vue` | `course/index.vue` |
| `event/jst_enroll_record/index.vue` | `enroll/index.vue` |

## 五、增强规范（适用本卡所有增强页）

### 搜索区
- 关键字段加 `el-input`（支持模糊搜索）
- 状态字段加 `el-select`（选项从字典加载或硬编码枚举）
- 时间字段加 `el-date-picker` type="daterange"
- 768px 以下搜索项纵向堆叠

### 表格
- 关键列保留，辅助列用 `el-table-column` 的 `v-if="!isMobile"` 在手机端隐藏
- 状态列统一使用 `JstStatusBadge` 或 `el-tag`
- 操作列：详情 / 编辑 / 删除（按权限 `v-hasPermi`）
- 手机端表格改为卡片列表（参考 WEB-ADMIN-1 的 `user/index.vue` 实现）

### 详情
- 列表页行点击 / 详情按钮 → 右侧 `el-drawer` 展示详情
- 详情内容用 `el-descriptions` 布局
- 手机端 drawer 全屏（`:size="isMobile ? '100%' : '50%'"`）

### 响应式
- 引入 `@/styles/responsive.scss`
- 断点：768px（手机）/ 1024px（平板）
- 搜索/表格/弹窗三处必须适配

### 空状态 & 分页
- 表格空态使用 `el-empty`
- 分页使用 `<pagination>` 组件

## 六、参考
- 精品页面范例：`views/jst/user/index.vue`、`views/jst/contest/index.vue`
- 公共组件：`components/JstStatusBadge`、`components/JstEmptyState`
- 响应式：`styles/responsive.scss`

## 七、DoD
- [ ] 6 个增强页面搜索/响应式/详情三项齐全
- [ ] 2 个新建页面功能完整
- [ ] 3 个重复页标记 deprecated
- [ ] `npm run build:prod` 无编译错误
- [ ] 报告交付
