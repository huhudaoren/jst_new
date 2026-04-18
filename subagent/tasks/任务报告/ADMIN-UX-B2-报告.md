# ADMIN-UX-B2 任务报告 — 弹窗分区与 JSON 尾单收尾

> 执行时间：2026-04-17
> 执行人：Web Admin Agent
> 任务卡：`subagent/tasks/ADMIN-UX-B2-弹窗分区与JSON尾单收尾.md`
> 基线：ADMIN-UX-B1 已交付的 `JstJsonEditor/` 组件族

---

## 一、Diff Plan（已执行）

| 主线 | 范围 | 预计改页 | 实际改页 |
|---|---|---|---|
| A · 弹窗分区 | 8-12 页 Dialog/Drawer 按「form-section」视觉分组 | 11 | **11** |
| B · JSON 尾单审计 | grep 全量 textarea+JSON/schema/layout，实际改 ≤5 页 | ≤5 | **1** |
| C · 9883 废弃提示 | 旧版报名表单模板加 el-alert + 跳转 | 1 | **1** |

无后端改动、无 partner/* 改动、无 B1 组件改动、未新增 npm 依赖。

---

## 二、主线 A 清单（11 页 · 弹窗/抽屉分区）

所有分区采用统一 CSS pattern：`.form-section` + `.form-section__title`（蓝色左边框 `#409eff`），移动端 media query 调小字号。`<el-divider />` 作为分区视觉分隔，PC 全量可见、Mobile 垂直堆叠仍保留小标题。

| # | 文件 | 原项数 | 分区 | Mobile 响应 |
|---|---|---:|---|---|
| 1 | `jst/points/jst_points_rule/index.vue` | 10 | 基本信息 / 奖励配置 / 生效期与限制 / 状态与备注 | ✅ |
| 2 | `jst/points/jst_level_config/index.vue` | 7 | 等级基本信息 / 升级门槛 / 权益与状态 | ✅ |
| 3 | `jst/points/jst_mall_goods/index.vue` | 10 | 基本信息 / 媒资与描述 / 价格与库存 / 上架状态 | ✅ |
| 4 | `jst/coupon/template.vue` (Drawer) | 9 | 基本信息 / 面额与门槛 / 有效期与适用范围 / 状态 | ✅ |
| 5 | `jst/rights/template.vue` | 8 | 基本信息 / 配额与核销 / 有效期与适用 | ✅ |
| 6 | `jst/event/jst_cert_template/index.vue` | 7 | 基本信息 / 模板配置 / 布局与状态 + `layoutJson` 可视化替换 | ✅（弹窗 fullscreen） |
| 7 | `jst/event/jst_cert_record/index.vue` | 12 | 证书基础信息 / 关联赛事成绩报名 / 发放信息 / 校验与备注 | ✅ |
| 8 | `jst/event/jst_event_partner/index.vue` | 10 | 机构信息 / 联系方式 / 审核信息 / 运营状态 | ✅ |
| 9 | `jst/event/jst_score_record/index.vue` | 12 | 关联信息 / 成绩与奖项 / 审核与发布 / 备注 | ✅ |
| 10 | `jst/biz-no-rule/index.vue` | 8 | 基本信息 / 编号格式 / 状态（原 el-divider 提升为 form-section 统一样式） | ✅ |
| 11 | `jst/notice/index.vue` (Drawer) | 6 | 基本信息 / 展示配置 / 内容 | ✅（drawer 100%） |

---

## 三、主线 B 审计表（JSON 尾单）

### Grep 范围

匹配规则：`type="textarea"` + 附近出现 JSON/schema/layout/config/rule/variables/benefits 等关键词。

### 全量分类表

| 文件 | 字段 | 类型 | 处理 |
|---|---|---|---|
| `jst/event/jst_cert_template/index.vue` | `layoutJson` | 布局 JSON（证书设计器产物） | **✅ 已可视化改造**：替换为 summary panel + 「前往证书设计器」按钮 + 「查看原始 JSON」只读弹窗 + 「清空布局」，严格遵守任务卡「不得引入可视化编辑器」红线 |
| 33 处 `type="textarea"` 其它匹配 | `remark` / `description` / `reason` / `content` / `rejectReason` 等 | 纯文本长字段 | **保持不变**：非 JSON 结构，textarea 已是合理 UI |
| `jst_coupon_issue_batch` 等 | `remark` 等 | 文本备注 | 保持不变 |
| `points_rule` `conditionJson` / `coupon_template` `ruleJson` / `rights_template` `ruleJson` | （审计时发现页面中未以 textarea 形式直接编辑该字段） | 已由 B1 CCB 决策归入「规则引擎」独立 UI，当前仅在列表展示，不属于 B2 尾单范围 | 保持不变 |

### 决策

最终实际改造页面 = **1 页**（`jst_cert_template` 的 `layoutJson`）。其余 32+ 处 textarea 均为业务合理的长文本输入，**无需改造**。满足任务卡「实际改 ≤5 页」的克制要求。

### layoutJson 改造片段

```html
<div class="layout-json-panel">
  <div class="layout-json-panel__summary">
    <i class="el-icon-set-up" />
    <span v-if="layoutJsonSummary.valid">
      已配置布局数据：{{ layoutJsonSummary.elementCount }} 个元素 · 画布 {{ layoutJsonSummary.canvasSize }}
    </span>
    <span v-else class="muted">尚未配置布局，请前往证书设计器配置</span>
  </div>
  <div class="layout-json-panel__actions">
    <el-button size="small" type="primary" plain icon="el-icon-edit" @click="goToCertDesigner">前往证书设计器</el-button>
    <el-button size="small" icon="el-icon-view" @click="previewLayoutJson">查看原始 JSON</el-button>
    <el-button size="small" type="danger" plain icon="el-icon-delete" @click="clearLayoutJson">清空布局</el-button>
  </div>
</div>
```

新增 `layoutJsonSummary` computed 解析 `{canvas: {width,height}, elements: []}` 结构；新增 raw-JSON 只读 `el-dialog` 供查看；跳转路径 `/partner/cert-manage?templateId=xxx`。

---

## 四、主线 C · 9883 旧报名表单模板废弃提示

- 文件：`jst/event/jst_enroll_form_template/index.vue`
- 新增：`el-alert type="warning" show-icon :closable="false"` 顶部常驻
- 描述：说明已迁移至新版可视化编辑器，本页仅保留只读数据查看
- 按钮：`el-button type="warning" @click="goNew"` → `this.$router.push('/jst/form-template')`
- 未删除原列表与接口，遵循任务卡「仅加提示、不拆数据链路」

---

## 五、构建验证

```
$ npm run build:prod
...
 DONE  Build complete. The dist directory is ready to be deployed.
```

- 全量 `npm run build:prod` **PASS**（绿）
- 过程中 3 次增量 build 分别在页 1-3 / 4-6 / 7-11 后执行，均通过
- 无新增 warning、无新增 lint 错误
- 所有文件保存为 UTF-8 无 BOM

---

## 六、风险与遗留

| 项 | 风险等级 | 说明 |
|---|---|---|
| B1 组件未被 B2 二次调用 | 🟢 无 | B2 分区仅是 CSS 结构，未扩展 B1 的 FormSchemaEditor/ThresholdEditor；若后续想把 `jst_points_rule.conditionJson` 接 ThresholdEditor，可在 B1 的后续迭代里加映射，不影响本次 |
| `layoutJson` 当前为 JSON 字符串 | 🟢 无 | 只读展示 + 跳证书设计器，未破坏原存储结构，设计器回来保存时原样写回 |
| 1 页 Section B 改动 | 🟢 可控 | 仅改 UI 呈现，原 `form.layoutJson` v-model 语义完全一致 |
| Mobile 响应 | 🟢 通过 | 11 页 Dialog 均加 `:fullscreen="isMobile"` 或 `:width="isMobile ? '100%' : '…'"`，form-section 标题 font-size 做 media 收敛 |
| el-divider 语义变更 | 🟢 向下兼容 | biz-no-rule 原使用 `el-divider content-position="left">{title}`，现改为「先 `el-divider` 分隔 + 再 `form-section__title` 小标题」，视觉更整齐，旧功能不变 |

**无后端改动、无数据库改动、无新依赖、无菜单 SQL 改动。**

---

## 七、交付清单

### 修改文件（共 12）

1. `RuoYi-Vue/ruoyi-ui/src/views/jst/points/jst_points_rule/index.vue`
2. `RuoYi-Vue/ruoyi-ui/src/views/jst/points/jst_level_config/index.vue`
3. `RuoYi-Vue/ruoyi-ui/src/views/jst/points/jst_mall_goods/index.vue`
4. `RuoYi-Vue/ruoyi-ui/src/views/jst/coupon/template.vue`
5. `RuoYi-Vue/ruoyi-ui/src/views/jst/rights/template.vue`
6. `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_cert_template/index.vue`（含 layoutJson 可视化改造）
7. `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_cert_record/index.vue`
8. `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_event_partner/index.vue`
9. `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_score_record/index.vue`
10. `RuoYi-Vue/ruoyi-ui/src/views/jst/biz-no-rule/index.vue`
11. `RuoYi-Vue/ruoyi-ui/src/views/jst/notice/index.vue`
12. `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_enroll_form_template/index.vue`（Section C 废弃提示）

### 新增文件

无（完全复用 B1 组件体系 + 统一 CSS pattern，无新组件、无新依赖）。

### 未动文件（显式豁免）

- `partner/*` 下所有页面
- `JstJsonEditor/*` 所有组件
- 所有 Controller/Service/Mapper/SQL
- `package.json` / `package-lock.json`

---

## 八、后续建议（非本卡范围）

1. 若产品想把 `jst_points_rule.conditionJson` 接 ThresholdEditor / `jst_rights_template.ruleJson` 接统一规则引擎，建议起新卡 B3，先做规则引擎的 DSL 设计，再落组件
2. `jst_cert_template.layoutJson` 目前的 summary 解析假设 `{canvas, elements}` 结构，若设计器 schema 升级需同步更新 computed
3. 11 页分区 CSS 完全重复，可考虑后续抽成全局 `styles/form-section.scss`，但不属于本卡克制边界
