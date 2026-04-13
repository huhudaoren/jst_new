# WEB-ADMIN-UX — 管理端新手友好化改造

> 优先级：P2 | 预估：M | Agent：Web Admin Agent

---

## 一、目标

让管理后台对新手用户更友好：Dashboard 快速操作入口、菜单精简分组、内嵌帮助说明、JSON 配置可视化。

## 二、改造内容

### B. Dashboard 快速操作卡片

**文件**：`views/jst/dashboard/index.vue`

在现有「待处理事项」区块下方，新增一个「快速操作」区块：

```
快速操作
├── 创建赛事 → /partner/contest-edit（或 /jst/contest/edit）
├── 审核报名 → /jst/enroll
├── 处理退款 → /jst/order/admin-refund
├── 审核提现 → /jst/channel/admin-withdraw
├── 发布公告 → /jst/notice
└── 查看渠道 → /jst/channel
```

每个操作一个卡片，包含：图标 + 标题 + 一句话描述 + 点击跳转。
布局：`el-row` 3 列 PC / 2 列手机。
样式对齐现有 todo-entry 风格。

### C. 菜单精简

**需要隐藏的 deprecated 菜单**（在数据库 `sys_menu` 中 `visible` 设为 `1`）：

已被精品页替代的代码生成页菜单（9 个），生成以下 SQL：

```sql
-- 隐藏已废弃的代码生成页菜单
UPDATE sys_menu SET visible = '1' WHERE menu_name IN (
  '赛事管理', '课程管理', '报名记录', '优惠券模板', 
  '权益模板', '订单信息', '退款记录', '公告管理', '用户信息'
) AND component LIKE 'jst/%' AND menu_type = 'C';
```

**注意**：不要误改精品页的菜单。精品页的 `component` 路径不带 `jst_` 前缀（如 `jst/contest/index` 而非 `jst/event/jst_contest/index`）。建议用 component 路径精确匹配：

```sql
UPDATE sys_menu SET visible = '1' WHERE component IN (
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
```

将此 SQL 写入 `架构设计/ddl/99-migration-hide-deprecated-menus.sql`。

### D. 页面内嵌帮助提示

在以下关键页面的 hero 区域或操作区加 `el-popover` 或 `el-tooltip` 帮助说明：

| 页面 | 帮助内容 |
|---|---|
| `jst/contest/index.vue` | "赛事从草稿→提审→上线的完整流程说明" |
| `jst/enroll/index.vue` | "审核操作说明：通过/驳回/补充材料" |
| `jst/order/admin-order/index.vue` | "订单状态说明 + 退款入口指引" |
| `jst/order/admin-refund/index.vue` | "退款审核流程 + 特批入口说明" |
| `jst/channel/admin-withdraw/index.vue` | "提现审核→打款流程说明" |
| `jst/dashboard/index.vue` | "看板数据说明 + 各模块入口指引" |

**实现方式**：在 hero 区右上角加一个 `<el-button circle icon="el-icon-question">` 按钮，点击弹出 `el-popover` 展示帮助文字。

帮助内容不超过 5 行文字，简洁实用。无需外链。

### E. JSON 配置可视化（赛事编辑页）

**重点**：将 `scoreRuleJson`、`certRuleJson`、核销配置 从裸 textarea 改为表单化编辑。

#### E1. `views/partner/contest-edit.vue` — 赛事方编辑页

**成绩规则 scoreRuleJson** 改为：
```
发布模式: [下拉] 手动发布 / 自动发布
成绩项: [标签输入] 总分、单项1、单项2...（el-tag + 输入框动态添加）
```

**证书规则 certRuleJson** 改为：
```
颁发模式: [下拉] 手动颁发 / 自动颁发
证书模板: [下拉多选] 从已有模板列表选择
```

**核销配置 writeoffConfig** 改为：
```
核销方式: [下拉] 二维码 / 手动输入
需要签到: [开关] 是 / 否
```

底层逻辑：表单组件的值变化时，自动 `JSON.stringify()` 写回 `form.xxxJson` 字段，保存时提交 JSON 字符串（后端不变）。

#### E2. `views/jst/contest/edit.vue` — 管理端编辑页

同 E1 的可视化改造。这个页面 schedule/awards/faq 已有可视化表格，只需把剩余 3 个 JSON textarea 改为表单。

## 三、规范

- 响应式必须适配（768px 断点）
- 帮助文字直接写在 Vue 组件中，不走后端接口
- SQL 文件放在 `架构设计/ddl/` 目录
- `npm run build:prod` 必须通过
- 不改后端 Java 代码

## 四、DoD

- [ ] Dashboard 快速操作 6 张卡片
- [ ] deprecated 菜单隐藏 SQL 生成
- [ ] 6 个关键页面帮助 popover
- [ ] partner/contest-edit.vue 3 个 JSON 字段可视化
- [ ] jst/contest/edit.vue 3 个 JSON 字段可视化
- [ ] 响应式适配
- [ ] `npm run build:prod` 通过
- [ ] 报告交付
