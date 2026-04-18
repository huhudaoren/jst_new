# ADMIN-UX-B2 — 弹窗分区 + JSON 尾单收尾

> 优先级：**P1** | 预估：**M**（2-3 天） | Agent：**Web Admin Agent**
> 依赖：ADMIN-UX-B1 完成（JstJsonEditor 组件族已就绪） | 派发时间：2026-04-17 | 版本：任务卡 v1

---

## 一、业务背景

B1 已把 3 个高频 JSON 页面（表单模板 / 风控规则 / 消息模板）改造为可视化编辑，并沉淀了 `JstJsonEditor/` 组件族。

本卡是 B1 的**收尾 + 扩面**：

1. **弹窗分区（主任务）**：管理端多数新增/编辑 Dialog 是一长条无结构的 el-form，运营找字段费力。本卡给字段多的 Dialog 用 `el-divider` 或 `el-card` 分区，降低认知负担。
2. **JSON 尾单收尾（副任务）**：审计 `views/jst/**` 仍存在的裸 JSON textarea，复用 B1 组件或标注为弃用。
3. **已弃用页面处置（清尾）**：隐藏但仍存在的 9883 "报名表单模板" 页面（`views/jst/event/jst_enroll_form_template/index.vue`）加 deprecation 横幅或直接删除。

---

## 二、必读上下文

1. `CLAUDE.md` § 五编码规范
2. `subagent/WEB_ADMIN_AGENT_PROMPT.md`
3. `subagent/tasks/ADMIN-UX-B1-JSON可视化核心.md`（B1 任务卡）
4. `subagent/tasks/任务报告/ADMIN-UX-B1-报告.md`（B1 报告，了解已有组件能力）
5. 已有可复用组件（B1 产出）：
   - `ruoyi-ui/src/components/JstJsonEditor/FormSchemaEditor.vue`
   - `ruoyi-ui/src/components/JstJsonEditor/FieldEditDrawer.vue`
   - `ruoyi-ui/src/components/JstJsonEditor/SchemaPreview.vue`
   - `ruoyi-ui/src/components/JstJsonEditor/ThresholdEditor.vue`
   - `ruoyi-ui/src/components/JstJsonEditor/MessageTemplateEditor.vue`
   - `ruoyi-ui/src/components/JstJsonEditor/variable-map.js`
6. 现有"已分区"的 Dialog 参考（照抄其分区风格）：
   - `ruoyi-ui/src/views/jst/contest/edit.vue`（8-Tab 大改造）
   - `ruoyi-ui/src/views/jst/course/edit.vue`（5-Tab）

---

## 三、三条主线

### 主线 A：弹窗分区（覆盖 8-12 页）

**判定标准**：同一 Dialog / Drawer 内 el-form-item ≥ 6 项、字段跨越 ≥ 2 个概念分组的，进入改造名单。

**目标清单（候选，Agent 需自己 audit 确认）**：

| # | 页面 | 典型字段分组建议 |
|---|---|---|
| 1 | `jst/points/jst_points_rule/index.vue` | 基本信息 / 触发条件 / 奖励配置 / 限制 |
| 2 | `jst/points/jst_level_config/index.vue` | 等级基本信息 / 门槛 / 权益描述 |
| 3 | `jst/points/jst_mall_goods/index.vue` | 基本信息 / 价格与库存 / 媒资 / 发放规则 |
| 4 | `jst/coupon/template.vue` | 基本信息 / 面额与门槛 / 有效期 / 适用范围 |
| 5 | `jst/rights/template.vue` | 基本信息 / 配额模式 / 有效期 / 适用赛事 |
| 6 | `jst/marketing/jst_coupon_issue_batch/index.vue` | 基本信息 / 发放目标 / 时间窗口 / 限额 |
| 7 | `jst/event/jst_cert_template/index.vue` | 基本信息 / 模板底图 / 布局（链接到设计器） |
| 8 | `jst/risk/jst_risk_rule/index.vue` | 已在 B1 改，本卡确认分区已合理 |
| 9 | `jst/message/jst_message_template/index.vue` | 已在 B1 改，本卡确认分区已合理 |
| 10-12 | Agent 自行 audit 补充 | — |

**分区实现模板**：

```vue
<el-form ref="form" :model="form" :rules="formRules">
  <div class="form-section">
    <div class="form-section__title">基本信息</div>
    <el-row :gutter="16">
      <el-col :xs="24" :sm="12">
        <el-form-item label="xxx" prop="xxx">...</el-form-item>
      </el-col>
    </el-row>
  </div>

  <el-divider />

  <div class="form-section">
    <div class="form-section__title">规则配置</div>
    ...
  </div>
</el-form>

<style lang="scss" scoped>
.form-section__title {
  font-size: 14px; font-weight: 600; color: #303133;
  margin-bottom: 12px; padding-left: 8px; border-left: 3px solid #409eff;
}
</style>
```

或使用 `el-collapse` / `el-tabs`（字段 >15 项时）。

**验收**：每个目标页在 PC 端 Dialog 打开时看到清晰分组，手机端回落垂直排列但分组标题保留。

---

### 主线 B：JSON 尾单审计（audit-first）

**Agent 第一步必须产出审计报告**：

```bash
grep -rn "type=\"textarea\"" ruoyi-ui/src/views/jst/ | grep -iE "json|schema|layout|rule|condition|benefits|config"
```

然后按结果分类：

| 类型 | 处置 |
|---|---|
| schemaJson（表单字段结构） | 复用 `FormSchemaEditor` |
| thresholdJson / conditionJson（阈值/条件） | 复用 `ThresholdEditor` |
| contentTpl / messageTpl（消息内容） | 复用 `MessageTemplateEditor` |
| layoutJson（证书布局） | **不要可视化**，改为展示"当前布局数据已由拖拽设计器管理"提示 + 跳转至证书设计器入口 |
| 纯快照 JSON（只读展示，如 formSnapshotJson / addressSnapshot） | 只读美化：折叠 + 语法高亮（可用原生 `<pre>`），不提供编辑 |
| 已弃用页面内的 JSON textarea | 随页面一起处理（见主线 C） |
| 未知 JSON | 在报告里 TODO，不动，留给后续评估 |

**产出**：
- 审计清单（报告内嵌）
- 实际改动页面清单（建议不超过 5 页，保守一些）

---

### 主线 C：已弃用页面处置

**页面**：`views/jst/event/jst_enroll_form_template/index.vue`（菜单 9883，2026-04-17 已隐藏 `visible=1`）

**两个选项**：

**选项 C1（推荐）**：保留文件 + 首屏 banner：
```vue
<el-alert
  title="此页已弃用，请使用「表单模板管理」"
  description="本页基于旧代码生成器，功能不完整。新版入口在 平台数据管理 → 用户渠道 → 表单模板管理，支持可视化字段编辑。"
  type="warning"
  show-icon
  :closable="false"
/>
<el-button type="primary" @click="goNew">跳转到新版</el-button>
```
+ `goNew()` 跳转到 `/jst/form-template`。

**选项 C2**：直接删除文件 + 清理路由。更干脆但不可逆。

**由 Agent 决策**：建议 C1，除非 Agent 确认 router 配置里该路径已不再被引用。

---

## 四、涉及权限 / 后端

**无**新增。纯前端 + 可能的一条 SQL（移除 3039 代码生成旧菜单行，可选）。

---

## 五、测试场景

### B2-A-1 弹窗分区视觉
- 打开至少 5 个目标页 Dialog，截图分区效果
- 手机浏览器尺寸（360px）复查，分组标题不塌

### B2-B-1 JSON 审计完整性
- 报告里清单包含 `grep` 输出全部命中，且每条都有处置决策

### B2-B-2 尾单页面可用性
- 实际改的每个页面：新建 + 编辑 + 保存 + 重开还原，无 JSON parse error

### B2-C-1 9883 页处置
- 打开 `/jst/event/jst_enroll_form_template`（如果路由还在）看到 deprecation banner
- 或确认该路径已被删除，访问返回 404

---

## 六、DoD 验收标准

- [ ] 主线 A：完成至少 8 页分区改造，截图对比（每页 2 张，PC + 手机）
- [ ] 主线 B：审计报告 + 实际改动页面 ≤ 5，每页验收通过
- [ ] 主线 C：9883 页面处置完成（banner 或删除）
- [ ] `npm run build:prod` 通过
- [ ] 任务报告：`subagent/tasks/任务报告/ADMIN-UX-B2-报告.md`
- [ ] commit message：`feat(admin): ADMIN-UX-B2 弹窗分区 + JSON 尾单收尾`

---

## 七、不许做的事

- ❌ **不许**改后端 DTO / VO / Controller / DDL
- ❌ **不许**改 B1 已落地的 3 页与 6 组件的核心逻辑（只允许本卡内分区美化）
- ❌ **不许**改 partner 工作台页面（`views/partner/*`）
- ❌ **不许**把证书 layoutJson 做成可视化编辑器（那是 REFACTOR-5-CERT 的 Fabric.js 设计器地盘）
- ❌ **不许**顺手合并或重构通用表单组件（保持最小改动）
- ❌ **不许**引入新的外部组件库

---

## 八、交付物

- 分区改造 diff（8-12 页）
- JSON 审计报告（内嵌任务报告）
- 尾单改动 diff（≤ 5 页）
- 9883 处置 diff
- 测试截图（至少 20 张）
- 任务报告：`subagent/tasks/任务报告/ADMIN-UX-B2-报告.md`
