# REFACTOR-2-DICT — 全局枚举翻译 + 字典补齐

> 优先级：P0 | 预估：M | Agent：Web Admin Agent

---

## 一、背景

管理端列表页大量英文枚举直接展示给用户（如类别显示 `art` 而非 `艺术`），严重影响易用性。需要全局排查并修复。

## 二、若依字典机制说明（已有，直接复用）

若依框架**已内置**完整的字典体系，**不需要自建 dict.js 工具**：

### 若依字典用法（三种方式）

**方式 1：声明式加载 + `<dict-tag>` 组件**（推荐，最简洁）
```vue
<script>
export default {
  dicts: ['jst_contest_category', 'jst_audit_status'],  // ← 声明即自动加载
  // ...
}
</script>

<template>
  <!-- 列表中展示 -->
  <el-table-column label="类别" prop="category">
    <template slot-scope="scope">
      <dict-tag :options="dict.type.jst_contest_category" :value="scope.row.category" />
    </template>
  </el-table-column>
</template>
```

**方式 2：`selectDictLabel` 做 formatter**
```vue
<el-table-column label="类别" prop="category" :formatter="categoryFormat" />

<script>
export default {
  dicts: ['jst_contest_category'],
  methods: {
    categoryFormat(row) {
      return this.selectDictLabel(this.dict.type.jst_contest_category, row.category)
    }
  }
}
</script>
```

**方式 3：`this.getDicts()` 手动加载**（用于 el-select 选项）
```javascript
created() {
  this.getDicts('jst_contest_category').then(res => {
    this.categoryOptions = res.data
  })
}
```

## 三、任务清单

### 3.1 后端：插入字典数据

产出 SQL 文件：`架构设计/ddl/99-migration-refactor-dict.sql`

已有字典（先查库确认，存在则跳过）：
- `jst_contest_category`（art→艺术 等）

需要新增的字典（**使用 INSERT IGNORE 或先 SELECT 判断，避免重复**）：

```sql
-- ============================================
-- 赛事来源类型
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '赛事来源', 'jst_source_type', '0', 'admin', NOW(), '赛事来源类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_source_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1, '平台自营', 'platform', 'jst_source_type', '0', 'admin', NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_source_type' AND dict_value='platform');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2, '赛事方', 'partner', 'jst_source_type', '0', 'admin', NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_source_type' AND dict_value='partner');

-- ============================================
-- 审核状态（通用）
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '审核状态', 'jst_audit_status', '0', 'admin', NOW(), '赛事/报名/入驻等审核状态'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_audit_status');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'草稿','draft','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='draft');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'待审核','pending','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='pending');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'已上线','online','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='online');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 4,'已下线','offline','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='offline');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 5,'已驳回','rejected','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='rejected');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 6,'已通过','approved','jst_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_audit_status' AND dict_value='approved');

-- ============================================
-- 所属类型
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '所属类型', 'jst_owner_type', '0', 'admin', NOW(), '业务所属方类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_owner_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'学生','student','jst_owner_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_owner_type' AND dict_value='student');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'渠道方','channel','jst_owner_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_owner_type' AND dict_value='channel');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'赛事方','partner','jst_owner_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_owner_type' AND dict_value='partner');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 4,'平台','platform','jst_owner_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_owner_type' AND dict_value='platform');

-- ============================================
-- 订单状态
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '订单状态', 'jst_order_status', '0', 'admin', NOW(), '订单状态'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_order_status');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'待支付','pending_pay','jst_order_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_order_status' AND dict_value='pending_pay');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'已支付','paid','jst_order_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_order_status' AND dict_value='paid');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'已取消','cancelled','jst_order_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_order_status' AND dict_value='cancelled');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 4,'已关闭','closed','jst_order_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_order_status' AND dict_value='closed');

-- ============================================
-- 退款状态
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '退款状态', 'jst_refund_status', '0', 'admin', NOW(), '退款状态'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_refund_status');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'待审核','pending','jst_refund_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_refund_status' AND dict_value='pending');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'已通过','approved','jst_refund_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_refund_status' AND dict_value='approved');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'已驳回','rejected','jst_refund_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_refund_status' AND dict_value='rejected');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 4,'退款中','processing','jst_refund_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_refund_status' AND dict_value='processing');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 5,'已完成','completed','jst_refund_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_refund_status' AND dict_value='completed');

-- ============================================
-- 报名审核状态
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '报名审核状态', 'jst_enroll_audit_status', '0', 'admin', NOW(), '报名审核状态'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_enroll_audit_status');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'待审核','pending','jst_enroll_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_enroll_audit_status' AND dict_value='pending');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'已通过','passed','jst_enroll_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_enroll_audit_status' AND dict_value='passed');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'已驳回','rejected','jst_enroll_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_enroll_audit_status' AND dict_value='rejected');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 4,'已取消','cancelled','jst_enroll_audit_status','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_enroll_audit_status' AND dict_value='cancelled');

-- ============================================
-- 渠道类型
-- ============================================
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '渠道类型', 'jst_channel_type', '0', 'admin', NOW(), '渠道方类型'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'jst_channel_type');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 1,'个人','personal','jst_channel_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_channel_type' AND dict_value='personal');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 2,'机构','institution','jst_channel_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_channel_type' AND dict_value='institution');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
SELECT 3,'学校','school','jst_channel_type','0','admin',NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type='jst_channel_type' AND dict_value='school');
```

### 3.2 前端：全局排查并修复（使用若依原生方式）

**不需要新建 dict.js 工具文件**。直接使用若依内置机制。

**修复模板**：

```vue
<script>
export default {
  // Step 1: 在 dicts 数组声明需要的字典类型
  dicts: ['jst_contest_category', 'jst_audit_status'],

  // ...其他代码
}
</script>

<template>
  <!-- Step 2: 用 dict-tag 展示（自动翻译+带样式标签） -->
  <el-table-column label="类别" prop="category">
    <template slot-scope="scope">
      <dict-tag :options="dict.type.jst_contest_category" :value="scope.row.category" />
    </template>
  </el-table-column>

  <el-table-column label="审核状态" prop="auditStatus">
    <template slot-scope="scope">
      <dict-tag :options="dict.type.jst_audit_status" :value="scope.row.auditStatus" />
    </template>
  </el-table-column>
</template>
```

**修复 el-select（下拉选择场景）**：

```vue
<!-- 筛选条件中的下拉 -->
<el-select v-model="queryParams.category" placeholder="全部分类" clearable>
  <el-option
    v-for="dict in dict.type.jst_contest_category"
    :key="dict.value"
    :label="dict.label"
    :value="dict.value"
  />
</el-select>
```

### 3.3 重点修复页面清单

全局搜索 `el-table-column` 中 prop 包含以下字段名的列，改为 `<dict-tag>` 或 `:formatter`：

| 目录 | 文件 | 需翻译字段 | 使用字典 |
|---|---|---|---|
| partner/ | contest-list.vue | category, auditStatus | jst_contest_category, jst_audit_status |
| partner/ | enroll-manage.vue | auditStatus | jst_enroll_audit_status |
| partner/ | contest-edit.vue | categoryOptions 改字典 | jst_contest_category |
| jst/contest/ | index.vue | category, sourceType, auditStatus | jst_contest_category, jst_source_type, jst_audit_status |
| jst/enroll/ | index.vue | auditStatus | jst_enroll_audit_status |
| jst/order/ | index.vue (admin-order) | orderStatus | jst_order_status |
| jst/order/ | index.vue (admin-refund) | status | jst_refund_status |
| jst/channel/ | index.vue | channelType | jst_channel_type |
| jst/channel-auth/ | index.vue | auditStatus | jst_audit_status |
| jst/participant/ | index.vue | ownerType (如有) | jst_owner_type |
| jst/organizer/ | index.vue | auditStatus | jst_audit_status |
| jst/partner-apply/ | index.vue | auditStatus | jst_audit_status |
| channel-manage/ | 所有 index.vue | auditStatus, channelType | 对应字典 |

### 3.4 contest-edit.vue 分类加载改造

将硬编码的 `categoryOptions` 改为从字典加载：

```javascript
// 修改前
categoryOptions: ['艺术', '音乐', '舞蹈', '美术', '朗诵戏剧', '文化', '科技', '体育'],

// 修改后：在 dicts 声明
export default {
  dicts: ['jst_contest_category'],
  // ...
}

// el-select 改为：
<el-select v-model="form.category" filterable clearable placeholder="请选择分类" class="full-width">
  <el-option
    v-for="dict in dict.type.jst_contest_category"
    :key="dict.value"
    :label="dict.label"
    :value="dict.value"
  />
</el-select>
```

### 3.5 小程序端检查

在 `jst-uniapp/` 中搜索是否有直接展示英文枚举的地方。小程序不使用若依字典 API，用本地 map 翻译：

```javascript
// utils/dict-map.js（小程序专用，简单映射）
export const DICT_MAP = {
  jst_contest_category: { art: '艺术', music: '音乐', dance: '舞蹈', ... },
  jst_audit_status: { draft: '草稿', pending: '待审核', online: '已上线', ... }
}
export function dictLabel(dictType, value) {
  return DICT_MAP[dictType]?.[value] || value
}
```

## 四、DoD

- [ ] 7 个新字典类型在测试库插入成功（幂等 SQL）
- [ ] **不新建 dict.js**，全部使用若依原生 `dicts` 声明 + `<dict-tag>` 组件
- [ ] 全局 15+ 列表页枚举翻译修复
- [ ] contest-edit.vue 分类从字典加载（删除硬编码 categoryOptions）
- [ ] 小程序端枚举翻译检查
- [ ] `npm run build:prod` 通过
- [ ] 报告交付

## 五、注意事项

- **禁止自建字典工具**，若依已有完整方案（getDicts / selectDictLabel / dict-tag）
- 字典声明在组件的 `dicts: [...]` 数组中，框架自动加载并注入 `this.dict.type.xxx`
- `<dict-tag>` 自带样式（不同值可配不同颜色标签），视觉效果优于纯文本
- SQL 使用 `SELECT...FROM DUAL WHERE NOT EXISTS` 保证幂等，可重复执行
- 若已有 `jst_contest_category` 字典但数据不全，补充缺失项即可
