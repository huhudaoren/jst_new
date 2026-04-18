# ADMIN-UX-B1 — JSON 可视化编辑器核心组件 + 关键 3 页改造

> 优先级：**P1** | 预估：**L**（3-5 天） | Agent：**Web Admin Agent**
> 依赖：ADMIN-FIX-URGENT 完成 | 派发时间：2026-04-17 | 版本：任务卡 v1

---

## 一、业务背景

管理端若干页面直接暴露 JSON textarea 给运营填写，无提示、无校验、无结构，严重不符合新人操作：

| 页面 | 路径 | 暴露 JSON 字段 | 业务含义 |
|---|---|---|---|
| 表单模板管理 | `views/jst/form-template/index.vue` | `schemaJson` | 报名表单字段结构 |
| 风控规则 | `views/jst/risk/jst_risk_rule/index.vue` | `thresholdJson` | 阈值 + 时间窗 + 动作 |
| 消息模板 | `views/jst/message/jst_message_template/index.vue` | `contentTpl` | 模板内容 + 变量占位 |

**本卡目标**：沉淀 1 套可复用的 **JSON 可视化编辑器组件家族**（3 种变体），把上述 3 个最痛的页面从 textarea 改成可视化表单。剩余 JSON 页面（优惠券 `ruleJson`、权益 `ruleJson`、积分规则 `conditionJson`、积分等级 `benefitsJson` 等）留给 B2 卡批量覆盖。

---

## 二、必读上下文

1. `CLAUDE.md` § 五编码规范
2. `subagent/WEB_ADMIN_AGENT_PROMPT.md`（完整读）
3. `subagent/tasks/WEB-ADMIN-UX-友好化.md`（已有 JSON 可视化经验，扩展它）
4. `subagent/tasks/WEB-ADMIN-POLISH-页面精品化.md`（Hero / query-panel 标准）
5. 现有组件参考：
   - `RuoYi-Vue/ruoyi-ui/src/components/*`（已有基础组件）
   - `ruoyi-ui/src/views/jst/form-template/edit.vue` 或 dialog（若已有可视化编辑器，增强；若只是 textarea，重写）
6. DDL：
   - `架构设计/ddl/02-jst-event.sql` § jst_enroll_form_template（schema_json 字段结构）
   - `架构设计/ddl/09-jst-risk.sql` § jst_risk_rule（threshold_json 字段结构）
   - `架构设计/ddl/08-jst-message.sql` § jst_message_template（content_tpl 变量约定）

---

## 三、组件设计（新增，放 `ruoyi-ui/src/components/JstJsonEditor/`）

### 组件 1：`<JstFormSchemaEditor>`（表单模板专用）

**功能**：字段列表 + 拖拽排序 + 字段类型选择 + 必填/校验规则配置，输出 JSON Schema。

**支持字段类型**（与小程序动态表单引擎对齐）：
`text / textarea / number / date / time / datetime / radio / checkbox / select / image-upload / file-upload`

**UI 结构**：
```
┌─ 工具栏 ─────────────────────────────┐
│ [+ 新增字段]  [预览表单]  [导入/导出 JSON] │
├─ 字段列表 ───────────────────────────┤
│ ⬍ 姓名 (text)  必填 ✎ 🗑              │
│ ⬍ 年龄 (number) 选填 ✎ 🗑             │
│ ⬍ 性别 (radio) [男/女] 必填 ✎ 🗑      │
│ ⬍ 参赛照片 (image-upload) 必填 ✎ 🗑   │
└────────────────────────────────────┘
```

**字段编辑抽屉**：
- 基本：字段 key、label、placeholder、必填、校验规则
- 类型专属：
  - radio/checkbox/select: 选项列表（label + value）
  - number: min/max/step
  - text: maxLength
  - image/file: 最大文件数 + 文件大小

**Props**：
- `v-model` Array<SchemaField>
- `readonly` Boolean
- `showPreview` Boolean

**Emit**：`change(schema)` / `preview()`

**产出文件**：
- `src/components/JstJsonEditor/FormSchemaEditor.vue`
- `src/components/JstJsonEditor/FieldEditDrawer.vue`
- `src/components/JstJsonEditor/SchemaPreview.vue`（只读预览渲染，模拟学生填表）

---

### 组件 2：`<JstThresholdEditor>`（风控阈值专用）

**功能**：可视化配置"在 X 分钟内累计 Y 次操作触发动作 Z"。

**支持规则类型**（从 `ruleType` 字段选择）：
- `frequency`（频次）：`window_minutes` + `max_count`
- `amount`（金额）：`window_minutes` + `threshold_amount`
- `blacklist_hit`（命中黑名单）：关联 `list_type`
- `whitelist_miss`（未命中白名单）：关联 `list_type`

**UI**：表单 + 实时 JSON 预览（左右两栏），运营看左边，程序员可看右边对照。

```
┌─ 规则类型: [频次阈值 ▼]      │  {                  │
├─ 时间窗口: [___10___] 分钟   │    "type": "freq",  │
├─ 次数阈值: [___5____] 次     │    "window": 10,    │
├─ 命中动作: [○告警 ○拦截 ○人审] │    "max": 5,        │
└────────────────────────────┘    "action": "warn"  │
                                  }                   │
```

**Props / Emit**：同上 pattern。

**产出文件**：
- `src/components/JstJsonEditor/ThresholdEditor.vue`

---

### 组件 3：`<JstMessageTemplateEditor>`（消息模板内容）

**功能**：textarea + 变量高亮 + "插入变量"按钮面板。

**支持场景变量**（按 `scene` 动态过滤，从后端字典或常量取）：
- 报名通过：`{participantName}`、`{contestName}`、`{enrollNo}`
- 订单支付：`{orderNo}`、`{amount}`、`{payTime}`
- 证书颁发：`{certNo}`、`{contestName}`、`{awardLevel}`
- 等等

**UI**：
```
┌─ 场景: [报名通过 ▼] ────────────────┐
├─ 可用变量（点击插入）───────────────┤
│ [参赛人姓名] [赛事名称] [报名编号]    │
├─ 模板内容 ─────────────────────────┤
│ ┌────────────────────────────────┐ │
│ │ 尊敬的 {参赛人姓名}，            │ │
│ │ 您报名的 {赛事名称} 已通过审核。  │ │
│ │ 报名编号：{报名编号}             │ │
│ └────────────────────────────────┘ │
├─ 预览（代入示例数据）────────────── │
│  尊敬的 张三，                      │
│  您报名的 青少年围棋大赛 已通过审核。│
│  报名编号：ENR20260417001          │
└───────────────────────────────────┘
```

**实现要点**：
- textarea 上覆盖 `<pre>` 同步 scroll + 着色（变量用橙色背景）
- 变量点击插入到光标位置
- 预览区代入示例数据（写死 mock 或接后端 `/jst/message/template/preview`）

**产出文件**：
- `src/components/JstJsonEditor/MessageTemplateEditor.vue`
- `src/components/JstJsonEditor/variable-map.js`（变量字典）

---

## 四、页面改造清单

### 4.1 表单模板管理 — 替换 schemaJson textarea

**文件**：`ruoyi-ui/src/views/jst/form-template/index.vue` 或 `edit.vue`（若是 Dialog 结构则改 Dialog 内）

**改动**：
- 把原 `<el-input type="textarea" v-model="form.schemaJson" />` 替换为 `<JstFormSchemaEditor v-model="parsedSchema" />`
- 保存前把 `parsedSchema` 序列化回 `form.schemaJson`
- 列表页列表列追加"字段预览"`<el-tag>`列：`3 字段 · 2 必填` 概览

**字典补齐**（若无）：
- `jst_form_field_type` 字段类型字典（text/textarea/number/...）

**验收**：
- 新建模板 → 可视化加字段 → 保存 → 再打开能正确还原
- 列表页能看到字段概览 tag
- 点"预览"按钮弹抽屉模拟学生填表

---

### 4.2 风控规则 — 替换 thresholdJson textarea

**文件**：`ruoyi-ui/src/views/jst/risk/jst_risk_rule/index.vue`（Dialog 部分）

**改动**：
- Dialog 中找 `thresholdJson` 字段，替换为 `<JstThresholdEditor v-model="parsedThreshold" :rule-type="form.ruleType" />`
- `ruleType` 改成大卡片选项（不是 el-select）：
  ```
  ┌─────────┐ ┌─────────┐ ┌─────────┐
  │ ⏱ 频次 │ │ 💰 金额 │ │ 🚫 黑名单│
  └─────────┘ └─────────┘ └─────────┘
  ```
- `dimension`（user/device/mobile/channel）同样改成大卡片

**列表页**：阈值列已有 `thresholdSummary()` 函数（见第 55 行），保持不变。

**验收**：
- 运营可在不看 JSON 的情况下配置完整规则
- 保存后 JSON 结构与老的一致（后端不用改）
- 右侧 JSON 预览区程序员可用

---

### 4.3 消息模板 — 替换 contentTpl textarea

**文件**：`ruoyi-ui/src/views/jst/message/jst_message_template/index.vue`（Dialog 部分）

**改动**：
- Dialog 中找 `contentTpl` 字段，替换为 `<JstMessageTemplateEditor v-model="form.contentTpl" :scene="form.scene" />`
- `scene` 字段改成 `el-select` + 场景字典（`jst_message_scene`）
- `channel` 字段改成 tab 切换（`inapp / sms / wechat`），每个 tab 下模板内容独立（若后端支持多通道多模板则分开存；当前库是单模板则一个 editor）

**字典补齐**（若无）：
- `jst_message_scene` 场景字典（enroll_approved / order_paid / cert_granted / ...）

**验收**：
- 运营选场景 → 看到该场景可用变量 → 点按钮插入 → 预览代入示例

---

## 五、涉及权限

无新增。所有改动在现有 `jst:event:formTemplate:*` / `jst:risk:rule:*` / `jst:message:message_template:*` 权限下。

---

## 六、接口契约（新增 1 个）

### POST /jst/message/template/preview（可选，若前端 mock 足够则不做）
- 入参：`{ contentTpl, scene }`
- 出参：`{ previewText }`（代入 demo 数据后的预览文本）
- 权限：`jst:message:message_template:query`
- 用途：消息模板编辑器的"预览"按钮实时看效果

**若不做该接口**：前端在 `variable-map.js` 维护 demo 数据，本地 replace。

---

## 七、测试场景

### B1-1 表单模板 happy path
- 新建一个 "围棋赛报名表"，加 5 个字段（姓名 text / 年龄 number / 性别 radio / 照片 image-upload / 自我介绍 textarea）
- 保存 → 重开 → 5 个字段顺序和配置正确还原

### B1-2 表单模板预览
- 点"预览表单" → 弹窗渲染可填写的表单，字段交互正常

### B1-3 风控规则：频次
- 新建规则"同 IP 1 分钟内 3 次登录失败告警"
- 保存 → 后端 `threshold_json = {"type":"frequency","window":1,"max":3,"action":"warn"}`
- 列表页 `thresholdSummary` 显示 `1 分钟内 ≥3 次 → 告警`

### B1-4 风控规则：金额
- 新建规则"单笔订单 > 10000 元人审"
- 保存 → `{"type":"amount","threshold":10000,"action":"manual"}`

### B1-5 消息模板：报名通过
- 场景选"报名通过" → 插入 3 个变量 → 预览代入 `张三/围棋赛/ENR001`

### B1-6 数据兼容
- 老的已存在模板（DB 里有 schema_json），打开后能正确解析渲染（不报 JSON parse error）
- 不兼容的旧数据要有 fallback（切换到 raw JSON 模式的"降级入口"）

---

## 八、DoD 验收标准

- [ ] 3 个组件（FormSchemaEditor / ThresholdEditor / MessageTemplateEditor）就绪，挂到 `components/JstJsonEditor/`
- [ ] 3 个页面改造完成，B1-1 ~ B1-6 全部通过
- [ ] 老数据兼容：`99-test-fixtures.sql` 中已有模板/规则打开后正常渲染
- [ ] 手机浏览器响应式：Drawer / 大卡片在小屏可用（回落到 el-select + textarea 也行）
- [ ] `npm run build:prod` 通过
- [ ] 任务报告：`subagent/tasks/任务报告/ADMIN-UX-B1-报告.md`
  - 组件 API 截图
  - 3 页面改造前后对比截图（每页 2 张）
- [ ] commit message：`feat(admin): ADMIN-UX-B1 JSON 可视化编辑器 + 表单/风控/消息模板页改造`

---

## 九、不许做的事

- ❌ **不许**改后端 DTO / VO / Controller（本卡纯前端改造）
- ❌ **不许**改数据库字段（JSON 仍以字符串保存）
- ❌ **不许**引入外部 JSON 编辑器（如 `@json-editor/json-editor`、`jsoneditor` 等），**必须**手写 Element UI 原生组件
- ❌ **不许**顺手动其他 JSON 页面（留给 B2）
- ❌ **不许**改 partner 工作台页面
- ❌ **不许**破坏现有测试 fixture
- ❌ **不许**删除老的 textarea "降级入口"（保留折叠在"高级"按钮后）

---

## 十、交付物

- 组件源码 3 + 2 辅助（抽屉、预览、变量 map）
- 页面改造 3 份 diff
- 字典补齐 SQL（若需）：`架构设计/ddl/99-migration-admin-ux-b1-dict.sql`
- 测试截图 12 张（每场景 2 张）
- 任务报告
