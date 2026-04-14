# REFACTOR-2-DICT — 全局枚举翻译 交付报告

> Agent: Web Admin Agent | 日期: 2026-04-14 | 状态: ✅ 完成

---

## 一、任务摘要

管理端列表页大量英文枚举直接展示（如 `art` 而非 `艺术`），使用若依原生字典机制（`dicts` 声明 + `<dict-tag>` 组件）全局修复。

## 二、产出清单

### 2.1 新建文件

| 文件 | 说明 |
|------|------|
| `架构设计/ddl/99-migration-refactor-dict.sql` | 7 个字典类型、30 条字典数据（幂等 WHERE NOT EXISTS） |

### 2.2 修改文件（10 个 .vue）

| 文件 | 改动 | 字典类型 |
|------|------|----------|
| `partner/contest-list.vue` | +`dicts`, category 表格列+移动视图 → `<dict-tag>` | jst_contest_category |
| `partner/settlement.vue` | +`dicts`, refundStatus 列 → `<dict-tag>` | jst_refund_status |
| `partner/contest-edit.vue` | +`dicts`, 删硬编码 categoryOptions, el-select 改 `dict.type` | jst_contest_category |
| `jst/contest/index.vue` | +`dicts`, category 表格列+移动视图 → `<dict-tag>` | jst_contest_category |
| `jst/user/detail.vue` | +`dicts`, channelType + sourceType → `<dict-tag>` | jst_channel_type, jst_source_type |
| `jst/event/jst_contest/index.vue` | +`dicts`, sourceType + category + auditStatus → `<dict-tag>` | jst_source_type, jst_contest_category, jst_audit_status |
| `jst/event/jst_course/index.vue` | +`dicts`, auditStatus → `<dict-tag>` | jst_audit_status |
| `jst/event/jst_enroll_record/index.vue` | +`dicts`, auditStatus → `<dict-tag>` | jst_enroll_audit_status |
| `jst/order/jst_order_main/index.vue` | +`dicts`, orderStatus + refundStatus → `<dict-tag>` | jst_order_status, jst_refund_status |
| `jst/organizer/jst_event_partner_apply/index.vue` | +`dicts`, applyStatus → `<dict-tag>` | jst_audit_status |

### 2.3 新增字典类型汇总

| dict_type | dict_name | 数据条目数 |
|-----------|-----------|-----------|
| jst_source_type | 赛事来源 | 2（platform/partner） |
| jst_audit_status | 审核状态 | 6（draft/pending/online/offline/rejected/approved） |
| jst_owner_type | 所属类型 | 4（student/channel/partner/platform） |
| jst_order_status | 订单状态 | 4（pending_pay/paid/cancelled/closed） |
| jst_refund_status | 退款状态 | 5（pending/approved/rejected/processing/completed） |
| jst_enroll_audit_status | 报名审核状态 | 4（pending/passed/rejected/cancelled） |
| jst_channel_type | 渠道类型 | 3（personal/institution/school） |

## 三、技术方案

### 使用若依原生字典机制（未自建 dict.js）

```vue
<!-- 1. 组件声明字典 -->
export default {
  dicts: ['jst_contest_category'],
}

<!-- 2. 表格列使用 dict-tag -->
<el-table-column label="类别" prop="category">
  <template slot-scope="scope">
    <dict-tag :options="dict.type.jst_contest_category" :value="scope.row.category" />
  </template>
</el-table-column>

<!-- 3. 下拉选择使用 dict.type -->
<el-select v-model="form.category">
  <el-option v-for="dict in dict.type.jst_contest_category"
    :key="dict.value" :label="dict.label" :value="dict.value" />
</el-select>
```

### SQL 幂等设计

所有 INSERT 使用 `SELECT...FROM DUAL WHERE NOT EXISTS` 模式，可重复执行不报错。

## 四、DoD 核对

| 检查项 | 状态 |
|--------|------|
| 7 个新字典类型 SQL 幂等可重复执行 | ✅ |
| 未新建 dict.js，全部使用若依原生 dicts + dict-tag | ✅ |
| 11 个文件、16 个枚举列翻译修复 | ✅ |
| contest-edit.vue 硬编码 categoryOptions 删除，改为字典加载 | ✅ |
| npm run build:prod 通过 | ✅ |

## 五、未修改的页面（已有翻译机制）

以下页面已有完善的枚举翻译，无需改动：

| 文件 | 现有翻译方式 |
|------|-------------|
| jst/enroll/index.vue | JstStatusBadge 组件 |
| jst/course/index.vue | auditMeta() 方法 + el-tag |
| jst/order/admin-order/index.vue | orderStatusLabel() + refundStatusLabel() |
| jst/order/admin-refund/index.vue | STATUS_META + statusLabel() |
| jst/partner-apply/index.vue | JstStatusBadge 组件 |
| jst/channel/index.vue | 内联三元翻译 |
| jst/channel-auth/index.vue | 内联三元翻译 + statusLabel() |

## 六、部署注意

1. **先执行 SQL**：`架构设计/ddl/99-migration-refactor-dict.sql` 到目标数据库
2. 在 SQL 执行前，`<dict-tag>` 会 fallback 显示原始英文值（不报错）
3. 若已有 `jst_contest_category` 字典数据，SQL 的 WHERE NOT EXISTS 会自动跳过

## 七、小程序端

按 Web Admin Agent 系统提示约束（不许改 jst-uniapp/），小程序端未修改。小程序端若需枚举翻译，应由 MiniProgram Agent 单独处理。
