# ADMIN-UX-B2 — 弹窗分区 + 批量确认 + 剩余 JSON 页批量覆盖

> 优先级：**P1** | 预估：**L**（3-5 天） | Agent：**Web Admin Agent**
> 依赖：ADMIN-UX-B1 完成（复用 JsonEditor 组件） | 派发时间：2026-04-17 | 版本：任务卡 v1

---

## 一、业务背景

ADMIN-UX-B1 已把 3 个最痛的 JSON 页面（表单模板/风控规则/消息模板）做成可视化。本卡聚焦剩余全局体验问题：

1. **弹窗/抽屉字段太多且无分组**：用户打开新增/编辑对话框，一眼望去 20+ 字段，不知道哪些必填、哪些是"高级"。
2. **批量操作 / 删除 / 审核无确认或确认信息不够**：有些删除没弹框直接执行；审核通过/驳回没显示关键对象信息。
3. **剩余含 JSON textarea 的页面**（用 B1 组件批量替换）：
   - 优惠券模板 `ruleJson`（满减/直减/折扣）
   - 权益模板 `ruleJson`（权益配置）
   - 积分规则 `conditionJson`（触发条件）
   - 积分等级 `benefitsJson`（等级权益）
   - 证书模板 `layoutJson`（已有 Fabric.js 设计器，保持；只改入口）
   - 表单模板关联赛事 ID 列表等

---

## 二、必读上下文

1. `CLAUDE.md` § 五编码规范 + § 六已知问题与待办
2. `subagent/WEB_ADMIN_AGENT_PROMPT.md`
3. `subagent/tasks/ADMIN-UX-B1-JSON可视化核心.md`（本卡前置）
4. B1 产出的组件：`ruoyi-ui/src/components/JstJsonEditor/*`
5. `subagent/tasks/ADMIN-POLISH-BATCH1/2/3-*.md`（历史精品化卡，S1-S6 标准）

---

## 三、改造清单

### 3.1 弹窗字段分区（所有新增/编辑 Dialog）

**适用范围**：任何 Dialog 中字段数 ≥ 8 的页面（估算 15 页）。

**分区标准**（用 `el-divider` 或 `el-collapse` 实现）：
```
【基本信息】  ← 默认展开，必填聚集
  名称 * / 编码 * / 类型 * / 状态 *

【业务配置】  ← 默认展开，常用选填
  关联赛事 / 生效时间 / 适用范围 / 限制条件

【高级选项】  ← 默认折叠
  扩展字段 / 备注 / 内部标记

【系统字段】  ← 隐藏
  create_by / create_time / update_by / update_time / del_flag
```

**必改页面**（先按这 10 页做，其余在此基础上复制粘贴模板）：

| # | 文件 | 字段数估 | 分区建议 |
|---|---|---|---|
| 1 | `jst/coupon/template.vue` Dialog | 12 | 基本 4 / 规则 4 / 高级 4 |
| 2 | `jst/rights/template.vue` Dialog | 10 | 基本 3 / 规则 4 / 高级 3 |
| 3 | `jst/mall/index.vue` Dialog（积分商品） | 14 | 基本 4 / 价格 3 / 库存 3 / 高级 4 |
| 4 | `jst/points/jst_points_rule/index.vue` Dialog | 9 | 基本 3 / 触发 4 / 高级 2 |
| 5 | `jst/points/jst_level_config/index.vue` Dialog | 8 | 基本 3 / 权益 3 / 高级 2 |
| 6 | `jst/notice/index.vue` Dialog | 7 | 基本 3 / 内容 2 / 高级 2 |
| 7 | `jst/form-template/edit.vue` | 6 + schema | 基本 3 / Schema 编辑 / 高级 3 |
| 8 | `jst/user/index.vue` Dialog（用户编辑） | 10 | 基本 4 / 联系 3 / 高级 3 |
| 9 | `jst/contest/edit.vue`（赛事编辑，已有 8 Tab）保持 Tab 不动 | - | - |
| 10 | `jst/channel/index.vue` Dialog | 9 | 基本 3 / 返点 3 / 高级 3 |

---

### 3.2 统一确认组件 `<JstConfirmDialog>`

**Props**：`title`、`message`、`level`（info/warning/danger）、`detail`（对象展示关键信息）、`confirmText`、`cancelText`。

**典型用法**：

```vue
<!-- 删除 -->
this.$jstConfirm.danger({
  title: '确认删除？',
  detail: { '优惠券名称': row.couponName, '发放数量': row.issueCount },
  message: '删除后不可恢复，已发放的优惠券不受影响'
}).then(() => { ... })

<!-- 审核通过 -->
this.$jstConfirm.warn({
  title: '确认通过该申请？',
  detail: { '机构名称': row.partnerName, '申请单号': row.applyNo, '联系人': row.contactName },
  message: '通过后将自动创建赛事方账号并发送短信通知'
})

<!-- 批量操作 -->
this.$jstConfirm.info({
  title: '批量提交审核',
  detail: { '选中数量': selected.length, '包含赛事': 3 },
  message: '确认提交？'
})
```

**实现**：
- 挂到 `Vue.prototype.$jstConfirm`
- 基于 `$modal.confirm` 包装，统一视觉
- 产出文件：
  - `src/components/JstConfirmDialog/index.vue`
  - `src/components/JstConfirmDialog/install.js`
  - `main.js` 注册

---

### 3.3 全局补齐确认（用 B2.2 的组件扫一遍）

对所有页面的 `handleDelete / handleApprove / handleReject / handleCancel / handleBatch*` 方法做一次全量扫描。

**规则**：
- 任何写操作（DELETE / PUT status / 批量）都必须有 `$jstConfirm.danger` 或 `.warn`
- Toast 消息格式统一："操作成功 → 某某某已完成"（不要只 "成功"）
- 失败有明确 error message

**产出**：一份"确认补齐清单"（每页几个按钮加了确认），至少覆盖 25 个页面。

---

### 3.4 剩余 JSON 页面批量覆盖（用 B1 组件）

| # | 文件 | JSON 字段 | 用哪个 B1 组件 |
|---|---|---|---|
| 1 | `jst/coupon/template.vue` | ruleJson | 新增 `<JstCouponRuleEditor>`（可复用 ThresholdEditor 思路，3 种类型卡片） |
| 2 | `jst/rights/template.vue` | ruleJson | 新增 `<JstRightsRuleEditor>` |
| 3 | `jst/points/jst_points_rule/index.vue` | conditionJson | 复用 `<JstThresholdEditor>` |
| 4 | `jst/points/jst_level_config/index.vue` | benefitsJson | 新增 `<JstBenefitsEditor>`（列表式） |
| 5 | `jst/event/jst_cert_template/index.vue` | layoutJson | **保持不动**（已有 Fabric.js 设计器） |
| 6 | `jst/event/jst_enroll_form_template/index.vue` | schemaJson | 复用 B1 的 `<JstFormSchemaEditor>` |
| 7 | `jst/message/jst_message_template/index.vue` | paramsJson | 新增 `<JstVariableMapEditor>`（KV 列表） |

**设计原则**：能复用 B1 的就复用；不能复用的，新增的编辑器**必须**跟 B1 同构（工具栏 + 可视化左栏 + JSON 预览右栏）。

---

### 3.5 列表页全量 S1-S3 补齐（扫尾）

对所有未在"高频核心页"和 BATCH1~3 覆盖范围内的剩余列表页（约 10 页），补齐：
- S1 Hero 区
- S2 搜索表单精简 + placeholder
- S3 枚举列 dict-tag + 金额格式化 + 时间 yyyy-MM-dd HH:mm
- S6 空状态 `<el-empty>`

清单请 Agent 开工前自行扫描 `views/jst/**/index.vue`，列出没有 `page-hero` 的页面，贴报告。

---

## 四、涉及权限 / 接口

无新增。

---

## 五、测试场景

### B2-1 分区 Dialog 视觉
- 打开 10 个改造的 Dialog
- 验：字段按"基本/业务/高级"分区，高级默认折叠

### B2-2 统一确认组件
- 全局任意页触发删除 / 审核 / 批量
- 验：都弹 `$jstConfirm` 而非原生 `confirm()` 或裸 `$modal.confirm`
- 验：confirm 框显示对象关键信息（名称/编号/数量）

### B2-3 JSON 页全部可视化
- 7 个 JSON 页面（含 B1 的 3 个）全部可视化
- 每个页面"保存 → 重开 → 数据正确"
- 每个页面都保留"高级：原始 JSON"降级入口

### B2-4 列表页 S1-S3 扫尾
- 运行扫描脚本列出缺 Hero 的页面 → 全部补齐

### B2-5 回归
- 老数据兼容（fixtures 打开不炸）
- `npm run build:prod` 通过

---

## 六、DoD 验收标准

- [ ] 10 个 Dialog 字段分区完成（B2-1 截图对比）
- [ ] `<JstConfirmDialog>` 组件 + `$jstConfirm` 全局挂载
- [ ] 25+ 页面 handleDelete/Approve/Reject 全部用 `$jstConfirm`（贴清单）
- [ ] 7 个 JSON 页面可视化全部到位（每页前后截图）
- [ ] 剩余列表页 S1-S3 补齐（清单 + 截图）
- [ ] `npm run build:prod` 通过
- [ ] 任务报告：`subagent/tasks/任务报告/ADMIN-UX-B2-报告.md`
- [ ] commit message：`feat(admin): ADMIN-UX-B2 弹窗分区 + 统一确认 + JSON 可视化批量覆盖`

---

## 七、不许做的事

- ❌ **不许**改后端
- ❌ **不许**破坏已有"高频核心页"（赛事、订单、退款、用户、渠道、公告等精品页保持现状或仅加分区）
- ❌ **不许**动证书模板 Fabric.js 设计器
- ❌ **不许**删 textarea 降级入口
- ❌ **不许**引入外部 UI 库（只用 Element UI 2.x + 自己的 JstJsonEditor）
- ❌ **不许**顺手动小程序端

---

## 八、交付物

- 组件新增：4 个（ConfirmDialog / CouponRuleEditor / RightsRuleEditor / BenefitsEditor / VariableMapEditor，合计 ≥5）
- 页面改造 diff：10 个 Dialog 分区 + 7 个 JSON 页 + ~10 个列表页扫尾 + 25+ 页确认补齐
- 字典补齐 SQL（若需）：`架构设计/ddl/99-migration-admin-ux-b2-dict.sql`
- 测试截图 20+ 张
- 任务报告
