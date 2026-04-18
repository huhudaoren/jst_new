# Plan-06 管理端 UX 精品化 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 消除管理端 140 处 UX 缺陷，让业务人员零技术门槛操作。核心：所有 `_id` 外键字段改为 RelationPicker / EntityLink（编辑选 / 展示跳转）+ 三角色仪表盘 + 术语友好化。

**Architecture:** 先建基础组件（PageHeader/EmptyStateCTA/EntityLink/RelationPicker 族）+ 字段别名字典 + 后端统一 search/brief API → 按模块批量改造 56 处 `_id` 输入 + 30 处 `_id` 展示 → 三角色 dashboard → 清理 Deprecated。

**Tech Stack:** Vue 2 + Element UI 2.x + echarts（已有）+ Spring Boot 4 + MyBatis。新建 9 个 Vue 组件 + 1 工具 + 8 后端端点。

**关联 spec:** `docs/superpowers/specs/2026-04-18-plan-06-ux-polish-design.md`

---

## 文件结构

### 新建 Vue 组件
```
ruoyi-ui/src/components/
├── PageHeader.vue                  # 面包屑+help-text+主操作
├── EmptyStateCTA.vue               # 空态 + CTA 按钮
├── EntityLink.vue                  # 业务名链接 → 详情页
├── StatCardGroup.vue               # 仪表盘 4 卡片（复用）
└── RelationPicker/
    ├── ChannelPicker.vue           # 渠道选择
    ├── UserPicker.vue              # sys_user 或 jst_user 选择
    ├── SalesPicker.vue             # 销售选择
    ├── ContestPicker.vue           # 赛事选择
    ├── ParticipantPicker.vue       # 参赛档案选择
    ├── PartnerPicker.vue           # 赛事方选择
    ├── OrderPicker.vue             # 订单选择
    └── BasePicker.vue              # 共用基类（filterable remote 模板）
```

### 工具
```
ruoyi-ui/src/utils/fieldLabelMap.js    # 字段别名字典 + $fieldLabel 全局函数
ruoyi-ui/src/plugins/field-label.js    # 注册 Vue.prototype.$fieldLabel
```

### 后端新建 Controller
```
RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/
└── EntityBriefController.java         # /admin/entity-brief/* 统一 + 各实体 search
```

### 前端页面改造清单（归类）
**Round A (Phase 1 底线)** — 销售工作台 + admin 销售管理 的 8 处 `_id` 输入
**Round B (Phase 3 全量)** — 剩余 48 处跨模块 `_id` 输入
**Round C (展示替换)** — 15 个列表页 LEFT JOIN 补名称 + EntityLink 替换

### 销售 / 主管 / admin 三角色 dashboard
```
ruoyi-ui/src/views/sales/me/dashboard.vue       # 改造强化（已有）
ruoyi-ui/src/views/sales/manager/dashboard.vue  # 新建
ruoyi-ui/src/views/jst/sales/dashboard.vue      # 扩展（已有）
```

### SQL 迁移
```
架构设计/ddl/99-migration-plan-06-menus.sql           # 菜单 978500/978507 注册 + 默认路由
架构设计/ddl/99-migration-plan-06-dict-rebate-strategy.sql  # 补几个字典类型
```

---

## Task 1: 准备分支 + 环境检查

**Files:** （无改动，仅分支 + 验证）

- [ ] **Step 1: 拉 main 开新分支**

```bash
cd D:/coding/jst_v1
git checkout main
git pull
git checkout -b feature/plan-06-ux-polish
```

- [ ] **Step 2: 验证 plan-01~05 产物就位**

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SELECT COUNT(*) AS sales_tables FROM information_schema.tables WHERE table_schema='jst' AND table_name LIKE 'jst_sales%';
SELECT role_id, role_key FROM sys_role WHERE role_id IN (501,502);
" jst
```
Expected: ≥ 10 张 sales 表，2 个角色。

- [ ] **Step 3: 核对 CLAUDE.md 反映 SALES-DISTRIBUTION 4 plan 完成**

Read file head — 应有 `4 plan + 集成修复 plan-05 完成`。

---

## Task 2: PageHeader 组件

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/components/PageHeader.vue`

- [ ] **Step 1: 创建 PageHeader.vue**

```vue
<template>
  <div class="page-header">
    <el-breadcrumb v-if="breadcrumb && breadcrumb.length" separator="/" class="page-header__bc">
      <el-breadcrumb-item v-for="(item, i) in breadcrumb" :key="i" :to="item.to || null">
        {{ item.text }}
      </el-breadcrumb-item>
    </el-breadcrumb>

    <div class="page-header__row">
      <div class="page-header__title-wrap">
        <h2 class="page-header__title">{{ title }}</h2>
        <p v-if="helpText" class="page-header__help">
          <i class="el-icon-info"></i> {{ helpText }}
        </p>
      </div>

      <div class="page-header__actions">
        <el-button
          v-if="primaryAction"
          type="primary"
          :icon="primaryAction.icon || 'el-icon-plus'"
          @click="primaryAction.handler"
        >
          {{ primaryAction.text }}
        </el-button>
        <el-button
          v-if="secondaryAction"
          :icon="secondaryAction.icon"
          @click="secondaryAction.handler"
        >
          {{ secondaryAction.text }}
        </el-button>
        <el-dropdown v-if="moreActions && moreActions.length" @command="onMoreCommand">
          <el-button>更多<i class="el-icon-arrow-down el-icon--right"></i></el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="a in moreActions"
              :key="a.text"
              :command="a.text"
              :divided="a.divided"
              :icon="a.icon"
            >{{ a.text }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PageHeader',
  props: {
    title: { type: String, required: true },
    breadcrumb: { type: Array, default: () => [] },
    helpText: { type: String, default: '' },
    primaryAction: { type: Object, default: null },
    secondaryAction: { type: Object, default: null },
    moreActions: { type: Array, default: () => [] }
  },
  methods: {
    onMoreCommand(text) {
      const item = this.moreActions.find(a => a.text === text)
      if (item && item.handler) item.handler()
    }
  }
}
</script>

<style scoped>
.page-header { padding: 16px 20px; background: #fff; margin-bottom: 16px; border-radius: 4px; }
.page-header__bc { margin-bottom: 8px; font-size: 12px; }
.page-header__row { display: flex; justify-content: space-between; align-items: flex-start; }
.page-header__title { margin: 0; font-size: 20px; font-weight: 600; color: #303133; }
.page-header__help { margin: 6px 0 0; color: #909399; font-size: 13px; }
.page-header__help .el-icon-info { color: #409eff; margin-right: 4px; }
.page-header__actions { display: flex; gap: 8px; }
@media (max-width: 768px) {
  .page-header__row { flex-direction: column; gap: 12px; }
}
</style>
```

- [ ] **Step 2: 全局注册组件**

编辑 `RuoYi-Vue/ruoyi-ui/src/main.js`，在现有 Element UI 注册段后添加：

```javascript
import PageHeader from '@/components/PageHeader.vue'
Vue.component('PageHeader', PageHeader)
```

- [ ] **Step 3: 验证构建**

Run:
```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui && npm run build:prod 2>&1 | tail -3
```
Expected: BUILD SUCCESS。

- [ ] **Step 4: Commit**

```bash
cd D:/coding/jst_v1
git add RuoYi-Vue/ruoyi-ui/src/components/PageHeader.vue RuoYi-Vue/ruoyi-ui/src/main.js
git commit -m "feat(ui): PageHeader 组件 (面包屑+help-text+primary/secondary/more 操作)"
```

---

## Task 3: EmptyStateCTA 组件

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/components/EmptyStateCTA.vue`

- [ ] **Step 1: 创建 EmptyStateCTA.vue**

```vue
<template>
  <div class="empty-state">
    <el-empty :image-size="imageSize" :description="title">
      <template #description>
        <div class="empty-state__title">{{ title }}</div>
        <div v-if="description" class="empty-state__desc">{{ description }}</div>
      </template>
      <div class="empty-state__actions" v-if="primaryText || secondaryText">
        <el-button
          v-if="primaryText"
          type="primary"
          :icon="primaryIcon"
          @click="$emit('primary-click')"
        >{{ primaryText }}</el-button>
        <el-button
          v-if="secondaryText"
          :icon="secondaryIcon"
          @click="$emit('secondary-click')"
        >{{ secondaryText }}</el-button>
      </div>
    </el-empty>
  </div>
</template>

<script>
export default {
  name: 'EmptyStateCTA',
  props: {
    title: { type: String, default: '暂无数据' },
    description: { type: String, default: '' },
    primaryText: { type: String, default: '' },
    primaryIcon: { type: String, default: 'el-icon-plus' },
    secondaryText: { type: String, default: '' },
    secondaryIcon: { type: String, default: '' },
    imageSize: { type: Number, default: 100 }
  }
}
</script>

<style scoped>
.empty-state { padding: 40px 20px; text-align: center; }
.empty-state__title { font-size: 16px; color: #606266; margin-bottom: 4px; }
.empty-state__desc { font-size: 13px; color: #909399; }
.empty-state__actions { margin-top: 20px; display: flex; gap: 12px; justify-content: center; }
</style>
```

- [ ] **Step 2: 全局注册** (在 main.js 追加)

```javascript
import EmptyStateCTA from '@/components/EmptyStateCTA.vue'
Vue.component('EmptyStateCTA', EmptyStateCTA)
```

- [ ] **Step 3: Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/components/EmptyStateCTA.vue RuoYi-Vue/ruoyi-ui/src/main.js
git commit -m "feat(ui): EmptyStateCTA 组件 (标题+描述+primary/secondary CTA 按钮)"
```

---

## Task 4: fieldLabelMap 字段别名字典 + Vue plugin

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/utils/fieldLabelMap.js`
- Create: `RuoYi-Vue/ruoyi-ui/src/plugins/field-label.js`

- [ ] **Step 1: 创建 fieldLabelMap.js**

```javascript
// 字段别名字典：技术字段名 → 业务人员友好名称
// 用法：$fieldLabel('channelId') → '渠道'
// 任何 <el-table-column label> / <el-form-item label> 都应使用 $fieldLabel() 而非直接写字段名
export const FIELD_LABEL_MAP = {
  // 实体主键/外键
  userId: '用户',
  sysUserId: '系统用户',
  participantId: '参赛档案',
  channelId: '渠道',
  channelName: '渠道名',
  salesId: '销售',
  salesNo: '销售编号',
  salesName: '销售姓名',
  managerId: '直属主管',
  partnerId: '赛事方',
  contestId: '赛事',
  orderId: '订单',
  orderNo: '订单编号',
  refundId: '退款单',
  enrollId: '报名记录',
  scoreId: '成绩记录',
  templateId: '模板',
  formTemplateId: '报名表单模板',
  certTemplateId: '证书模板',
  couponId: '优惠券',
  couponTemplateId: '优惠券模板',
  rightsTemplateId: '权益模板',
  userRightsId: '用户权益',
  bindingId: '绑定关系',
  settlementId: '结算单',
  ledgerId: '台账',
  conflictId: '冲突单',
  inviteId: '邀请关系',
  recordId: '记录',
  taskId: '任务',
  preId: '预录入',

  // 业务字段
  businessType: '业务类型',
  bindingType: '归属类型',
  bindSource: '归属来源',
  orderStatus: '订单状态',
  refundStatus: '退款状态',
  auditStatus: '审核状态',
  mood: '跟进意向',
  followupType: '跟进类型',
  status: '状态',
  createBy: '创建人',
  createTime: '创建时间',
  updateBy: '更新人',
  updateTime: '更新时间',

  // 金额 / 数量
  baseAmount: '基数金额',
  rawAmount: '原始金额',
  amount: '金额',
  totalAmount: '总金额',
  payAmount: '支付金额',
  netPayAmount: '实付金额',
  commissionAmount: '提成金额',
  gmv: 'GMV',
  compressRatio: '压缩系数',
  appliedRate: '适用费率',
  commissionRateDefault: '默认费率',

  // 特殊字段（去技术化）
  contactMobile: '联系手机号',
  followupAt: '跟进时间',
  accrueAt: '入账时间',
  accruedAt: '已入账时间',
  effectiveFrom: '生效时间',
  effectiveTo: '失效时间',
  expireAt: '过期时间'
}

/**
 * 根据字段名返回业务显示名。未命中返回原字段名（避免显示为空）。
 */
export function fieldLabel(fieldName) {
  if (!fieldName) return ''
  return FIELD_LABEL_MAP[fieldName] || fieldName
}
```

- [ ] **Step 2: 创建 Vue plugin**

```javascript
// RuoYi-Vue/ruoyi-ui/src/plugins/field-label.js
import { fieldLabel } from '@/utils/fieldLabelMap'

export default {
  install(Vue) {
    Vue.prototype.$fieldLabel = fieldLabel
  }
}
```

- [ ] **Step 3: 在 main.js 注册 plugin**

```javascript
import FieldLabelPlugin from '@/plugins/field-label'
Vue.use(FieldLabelPlugin)
```

- [ ] **Step 4: Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/utils/fieldLabelMap.js RuoYi-Vue/ruoyi-ui/src/plugins/field-label.js RuoYi-Vue/ruoyi-ui/src/main.js
git commit -m "feat(ui): fieldLabelMap 字段别名字典 + Vue.prototype.\$fieldLabel 全局函数"
```

---

## Task 5: EntityLink 组件 + 路由映射

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/components/EntityLink.vue`
- Create: `RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js`

- [ ] **Step 1: 创建实体路由映射**

```javascript
// RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js
// 实体类型 → 详情页路由模板 + 所需权限点
export const ENTITY_ROUTE_MAP = {
  channel:     { path: '/jst/channel/detail',             perm: 'jst:channel:list' },
  sales:       { path: '/jst/sales',                      perm: 'jst:sales:list' },
  user:        { path: '/system/user-detail',             perm: 'system:user:query' },
  jstUser:     { path: '/jst/user/detail',                perm: 'jst:user:list' },
  partner:     { path: '/jst/event/jst_event_partner',    perm: 'jst:event_partner:list' },
  contest:     { path: '/jst/event/jst_contest',          perm: 'jst:contest:list' },
  participant: { path: '/jst/participant',                perm: 'jst:participant:list' },
  order:       { path: '/jst/order/admin-order',          perm: 'jst:order:list' },
  settlement:  { path: '/jst/sales/settlement/detail',    perm: 'jst:sales:settlement:review' }
}

export function resolveEntityRoute(entity, id) {
  const cfg = ENTITY_ROUTE_MAP[entity]
  if (!cfg) return null
  return { path: `${cfg.path}/${id}`, perm: cfg.perm }
}
```

- [ ] **Step 2: 创建 EntityLink.vue**

```vue
<template>
  <span class="entity-link-wrap">
    <a
      v-if="canClick && name"
      class="entity-link"
      :class="[`entity-link--${mode}`]"
      @click.stop.prevent="onClick"
    >{{ name }}</a>
    <span v-else-if="name" class="entity-link--disabled">{{ name }}</span>
    <span v-else class="entity-link--loading">{{ id ? '#' + id : '-' }}</span>
  </span>
</template>

<script>
import { resolveEntityRoute } from '@/utils/entityRouteMap'
import { checkPermi } from '@/utils/permission'

export default {
  name: 'EntityLink',
  props: {
    entity: { type: String, required: true },      // 'channel' | 'sales' | 'user' | ...
    id: { type: [Number, String], default: null },
    name: { type: String, default: '' },
    mode: { type: String, default: 'text' },       // text | tag | card
    newTab: { type: Boolean, default: false }
  },
  computed: {
    routeInfo() {
      if (!this.id) return null
      return resolveEntityRoute(this.entity, this.id)
    },
    canClick() {
      if (!this.routeInfo) return false
      // 权限检查（复用若依 checkPermi）
      try {
        return checkPermi([this.routeInfo.perm])
      } catch (e) {
        return true  // 无权限工具可用时 fallback 允许点击
      }
    }
  },
  methods: {
    onClick() {
      if (!this.routeInfo) return
      if (this.newTab) {
        const href = this.$router.resolve(this.routeInfo.path).href
        window.open(href, '_blank')
      } else {
        this.$router.push(this.routeInfo.path)
      }
    }
  }
}
</script>

<style scoped>
.entity-link-wrap { display: inline-block; }
.entity-link { color: #409eff; cursor: pointer; text-decoration: none; }
.entity-link:hover { text-decoration: underline; }
.entity-link--tag { padding: 2px 8px; background: #ecf5ff; border-radius: 3px; font-size: 12px; }
.entity-link--disabled { color: #606266; }
.entity-link--loading { color: #c0c4cc; font-size: 12px; }
</style>
```

- [ ] **Step 3: 全局注册**

```javascript
// main.js
import EntityLink from '@/components/EntityLink.vue'
Vue.component('EntityLink', EntityLink)
```

- [ ] **Step 4: Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/components/EntityLink.vue RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js RuoYi-Vue/ruoyi-ui/src/main.js
git commit -m "feat(ui): EntityLink 组件 (展示业务名+点击跳转详情+权限拦截)"
```

---

## Task 6: 后端 EntityBriefController + 统一 search API

**Files:**
- Create: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/EntityBriefController.java`

- [ ] **Step 1: 创建统一 Controller**

```java
package com.ruoyi.web.controller.jst;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一实体选择器后端（配合 RelationPicker / EntityLink 前端组件）。
 * <p>
 * 端点：
 * <ul>
 *   <li>GET /admin/entity/search?type=channel&kw=xxx  → 搜索候选项（Picker 用）</li>
 *   <li>GET /admin/entity/brief?type=channel&id=123   → 获取单个实体摘要（EntityLink 用）</li>
 * </ul>
 * <p>
 * 返回统一格式 {id, name, subtitle, statusTag}。
 */
@RestController
@RequestMapping("/admin/entity")
@PreAuthorize("isAuthenticated()")
public class EntityBriefController extends BaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ISysUserService sysUserService;

    @GetMapping("/search")
    public AjaxResult search(@RequestParam String type,
                             @RequestParam(required = false) String kw,
                             @RequestParam(defaultValue = "20") int limit) {
        List<Map<String, Object>> rows = switch (type) {
            case "channel"     -> searchChannel(kw, limit);
            case "sales"       -> searchSales(kw, limit);
            case "user"        -> searchSysUser(kw, limit);
            case "jstUser"     -> searchJstUser(kw, limit);
            case "partner"     -> searchPartner(kw, limit);
            case "contest"     -> searchContest(kw, limit);
            case "participant" -> searchParticipant(kw, limit);
            case "order"       -> searchOrder(kw, limit);
            default -> new ArrayList<>();
        };
        return AjaxResult.success(rows);
    }

    @GetMapping("/brief")
    public AjaxResult brief(@RequestParam String type, @RequestParam Long id) {
        Map<String, Object> row = switch (type) {
            case "channel"     -> briefOne("SELECT channel_id AS id, channel_name AS name, contact_mobile AS subtitle, status AS statusTag FROM jst_channel WHERE channel_id = ?", id);
            case "sales"       -> briefOne("SELECT sales_id AS id, sales_name AS name, sales_no AS subtitle, status AS statusTag FROM jst_sales WHERE sales_id = ?", id);
            case "user"        -> briefSysUser(id);
            case "jstUser"     -> briefOne("SELECT user_id AS id, nick_name AS name, phone AS subtitle FROM jst_user WHERE user_id = ?", id);
            case "partner"     -> briefOne("SELECT partner_id AS id, partner_name AS name, contact_phone AS subtitle, audit_status AS statusTag FROM jst_event_partner WHERE partner_id = ?", id);
            case "contest"     -> briefOne("SELECT contest_id AS id, contest_name AS name, category AS subtitle, audit_status AS statusTag FROM jst_contest WHERE contest_id = ?", id);
            case "participant" -> briefOne("SELECT participant_id AS id, participant_name AS name, phone AS subtitle FROM jst_participant WHERE participant_id = ?", id);
            case "order"       -> briefOne("SELECT order_id AS id, order_no AS name, net_pay_amount AS subtitle, order_status AS statusTag FROM jst_order_main WHERE order_id = ?", id);
            default -> null;
        };
        return AjaxResult.success(row);
    }

    // ===== 各实体 search 实现 =====

    private List<Map<String, Object>> searchChannel(String kw, int limit) {
        String sql = "SELECT channel_id AS id, channel_name AS name, contact_mobile AS subtitle, status AS statusTag FROM jst_channel";
        if (StringUtils.isNotBlank(kw)) {
            return jdbcTemplate.queryForList(sql + " WHERE channel_name LIKE ? OR contact_mobile LIKE ? LIMIT ?",
                    "%" + kw + "%", "%" + kw + "%", limit);
        }
        return jdbcTemplate.queryForList(sql + " ORDER BY channel_id DESC LIMIT ?", limit);
    }

    private List<Map<String, Object>> searchSales(String kw, int limit) {
        String sql = "SELECT sales_id AS id, sales_name AS name, sales_no AS subtitle, status AS statusTag FROM jst_sales";
        if (StringUtils.isNotBlank(kw)) {
            return jdbcTemplate.queryForList(sql + " WHERE sales_name LIKE ? OR sales_no LIKE ? OR phone LIKE ? LIMIT ?",
                    "%" + kw + "%", "%" + kw + "%", "%" + kw + "%", limit);
        }
        return jdbcTemplate.queryForList(sql + " WHERE status IN ('active','resign_apply') ORDER BY sales_id DESC LIMIT ?", limit);
    }

    private List<Map<String, Object>> searchSysUser(String kw, int limit) {
        // sys_user 敏感，走 service 而非直接 SQL
        List<SysUser> users = sysUserService.selectAllocatedList(new SysUser());  // 或 selectUserList，视 ruoyi 版本
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SysUser u : users) {
            if (rows.size() >= limit) break;
            if (StringUtils.isNotBlank(kw) &&
                !(u.getUserName() != null && u.getUserName().contains(kw)) &&
                !(u.getNickName() != null && u.getNickName().contains(kw))) continue;
            Map<String, Object> row = new HashMap<>();
            row.put("id", u.getUserId());
            row.put("name", u.getNickName());
            row.put("subtitle", u.getUserName());
            row.put("statusTag", u.getStatus());
            rows.add(row);
        }
        return rows;
    }

    private Map<String, Object> briefSysUser(Long id) {
        SysUser u = sysUserService.selectUserById(id);
        if (u == null) return null;
        Map<String, Object> row = new HashMap<>();
        row.put("id", u.getUserId());
        row.put("name", u.getNickName());
        row.put("subtitle", u.getUserName());
        return row;
    }

    private List<Map<String, Object>> searchJstUser(String kw, int limit) {
        String sql = "SELECT user_id AS id, nick_name AS name, phone AS subtitle FROM jst_user";
        if (StringUtils.isNotBlank(kw)) {
            return jdbcTemplate.queryForList(sql + " WHERE nick_name LIKE ? OR phone LIKE ? LIMIT ?",
                    "%" + kw + "%", "%" + kw + "%", limit);
        }
        return jdbcTemplate.queryForList(sql + " ORDER BY user_id DESC LIMIT ?", limit);
    }

    private List<Map<String, Object>> searchPartner(String kw, int limit) {
        String sql = "SELECT partner_id AS id, partner_name AS name, contact_phone AS subtitle, audit_status AS statusTag FROM jst_event_partner";
        if (StringUtils.isNotBlank(kw)) {
            return jdbcTemplate.queryForList(sql + " WHERE partner_name LIKE ? LIMIT ?", "%" + kw + "%", limit);
        }
        return jdbcTemplate.queryForList(sql + " ORDER BY partner_id DESC LIMIT ?", limit);
    }

    private List<Map<String, Object>> searchContest(String kw, int limit) {
        String sql = "SELECT contest_id AS id, contest_name AS name, category AS subtitle, audit_status AS statusTag FROM jst_contest";
        if (StringUtils.isNotBlank(kw)) {
            return jdbcTemplate.queryForList(sql + " WHERE contest_name LIKE ? LIMIT ?", "%" + kw + "%", limit);
        }
        return jdbcTemplate.queryForList(sql + " ORDER BY contest_id DESC LIMIT ?", limit);
    }

    private List<Map<String, Object>> searchParticipant(String kw, int limit) {
        String sql = "SELECT participant_id AS id, participant_name AS name, phone AS subtitle FROM jst_participant";
        if (StringUtils.isNotBlank(kw)) {
            return jdbcTemplate.queryForList(sql + " WHERE participant_name LIKE ? OR phone LIKE ? LIMIT ?",
                    "%" + kw + "%", "%" + kw + "%", limit);
        }
        return jdbcTemplate.queryForList(sql + " ORDER BY participant_id DESC LIMIT ?", limit);
    }

    private List<Map<String, Object>> searchOrder(String kw, int limit) {
        String sql = "SELECT order_id AS id, order_no AS name, net_pay_amount AS subtitle, order_status AS statusTag FROM jst_order_main";
        if (StringUtils.isNotBlank(kw)) {
            return jdbcTemplate.queryForList(sql + " WHERE order_no LIKE ? LIMIT ?", "%" + kw + "%", limit);
        }
        return jdbcTemplate.queryForList(sql + " ORDER BY order_id DESC LIMIT ?", limit);
    }

    private Map<String, Object> briefOne(String sql, Long id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);
        return rows.isEmpty() ? null : rows.get(0);
    }
}
```

> Note: `jst_participant` / `jst_event_partner` / `jst_contest` 的字段名若与实际不一致，编译时会在 JdbcTemplate 运行期才发现。Task 28 端到端 smoke 会验证。

- [ ] **Step 2: 编译**

```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn -pl ruoyi-admin -am compile -DskipTests -q 2>&1 | grep -E "ERROR|BUILD FAIL" | head -3
```
Expected: 无 ERROR（若 SysUserService.selectAllocatedList 签名不同可能报错，改用 `selectUserList(new SysUser())`）

- [ ] **Step 3: Commit**

```bash
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/EntityBriefController.java
git commit -m "feat(admin): EntityBriefController 统一 /admin/entity/search + /brief (9 实体支持)"
```

---

## Task 7: BasePicker (RelationPicker 公共基类)

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/BasePicker.vue`

- [ ] **Step 1: 创建基类（所有 Picker 共用查询逻辑）**

```vue
<template>
  <div class="base-picker">
    <el-select
      :value="value"
      :placeholder="placeholder"
      :clearable="clearable"
      :disabled="disabled"
      :filterable="true"
      :remote="true"
      :remote-method="doSearch"
      :loading="loading"
      size="medium"
      style="width: 100%;"
      @change="onChange"
      @visible-change="onFocus"
    >
      <el-option
        v-for="item in options"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      >
        <div class="base-picker__option">
          <span class="base-picker__option-name">{{ item.name }}</span>
          <span v-if="item.subtitle" class="base-picker__option-sub">{{ item.subtitle }}</span>
          <el-tag v-if="item.statusTag" size="mini" :type="statusTagType(item.statusTag)">
            {{ item.statusTag }}
          </el-tag>
        </div>
      </el-option>
      <template #empty>
        <div class="base-picker__empty">
          <span>没有匹配的数据</span>
        </div>
      </template>
    </el-select>
    <a
      v-if="showDetailLink && value && selectedName"
      class="base-picker__detail-link"
      @click="onDetailClick"
    >查看详情 →</a>
  </div>
</template>

<script>
import request from '@/utils/request'
import { resolveEntityRoute } from '@/utils/entityRouteMap'

export default {
  name: 'BasePicker',
  props: {
    value: { type: [Number, String, null], default: null },
    entity: { type: String, required: true },
    placeholder: { type: String, default: '请选择（输入名称搜索）' },
    clearable: { type: Boolean, default: true },
    disabled: { type: Boolean, default: false },
    showDetailLink: { type: Boolean, default: true }
  },
  data() {
    return {
      options: [],
      loading: false,
      selectedName: ''
    }
  },
  watch: {
    value: { immediate: true, handler: 'hydrateSelected' }
  },
  methods: {
    async hydrateSelected(v) {
      if (!v) { this.selectedName = ''; return }
      if (this.options.find(o => o.id === v)) {
        this.selectedName = this.options.find(o => o.id === v).name
        return
      }
      // 用 brief 接口补名称
      const res = await request({
        url: '/admin/entity/brief',
        method: 'get',
        params: { type: this.entity, id: v }
      })
      if (res.data) {
        this.selectedName = res.data.name
        if (!this.options.find(o => o.id === v)) this.options.unshift(res.data)
      }
    },
    async doSearch(kw) {
      this.loading = true
      try {
        const res = await request({
          url: '/admin/entity/search',
          method: 'get',
          params: { type: this.entity, kw, limit: 30 }
        })
        this.options = res.data || []
      } finally { this.loading = false }
    },
    async onFocus(visible) {
      // 第一次打开时加载初始 20 条
      if (visible && !this.options.length) await this.doSearch('')
    },
    onChange(id) {
      this.$emit('input', id)
      const picked = this.options.find(o => o.id === id)
      this.selectedName = picked ? picked.name : ''
      this.$emit('change', picked || null)
    },
    onDetailClick() {
      const r = resolveEntityRoute(this.entity, this.value)
      if (r) this.$router.push(r.path)
    },
    statusTagType(s) {
      const map = { active: 'success', pending: 'warning', rejected: 'danger', '0': 'success', '1': 'info' }
      return map[s] || ''
    }
  }
}
</script>

<style scoped>
.base-picker__option { display: flex; align-items: center; gap: 8px; }
.base-picker__option-name { flex: 1; }
.base-picker__option-sub { color: #909399; font-size: 12px; }
.base-picker__detail-link { display: inline-block; margin-top: 4px; font-size: 12px; color: #409eff; cursor: pointer; }
.base-picker__empty { padding: 16px; text-align: center; color: #909399; font-size: 13px; }
</style>
```

- [ ] **Step 2: Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/BasePicker.vue
git commit -m "feat(ui): RelationPicker/BasePicker 公共基类 (filterable remote+brief+查看详情)"
```

---

## Task 8: 7 个具体 RelationPicker 组件（薄包装）

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/{Channel,User,Sales,Contest,Participant,Partner,Order}Picker.vue`

每个 Picker 都是 BasePicker 的薄包装，指定 entity + 默认 placeholder。

- [ ] **Step 1: ChannelPicker.vue**

```vue
<template>
  <base-picker
    :value="value"
    entity="channel"
    :placeholder="placeholder"
    :clearable="clearable"
    :disabled="disabled"
    :show-detail-link="showDetailLink"
    @input="$emit('input', $event)"
    @change="$emit('change', $event)"
  />
</template>
<script>
import BasePicker from './BasePicker.vue'
export default {
  name: 'ChannelPicker',
  components: { BasePicker },
  props: {
    value: { type: [Number, String, null], default: null },
    placeholder: { type: String, default: '请选择渠道（输入名称/手机号搜索）' },
    clearable: { type: Boolean, default: true },
    disabled: { type: Boolean, default: false },
    showDetailLink: { type: Boolean, default: true }
  }
}
</script>
```

- [ ] **Step 2-7: 其余 6 个 Picker (复制 Step 1 改 entity + placeholder)**

分别创建：
- `UserPicker.vue` — entity="user", placeholder="请选择系统用户"
- `SalesPicker.vue` — entity="sales", placeholder="请选择销售"
- `ContestPicker.vue` — entity="contest", placeholder="请选择赛事"
- `ParticipantPicker.vue` — entity="participant", placeholder="请选择参赛档案"
- `PartnerPicker.vue` — entity="partner", placeholder="请选择赛事方"
- `OrderPicker.vue` — entity="order", placeholder="请选择订单"

- [ ] **Step 8: 全局注册 7 个 Picker**

在 `main.js` 追加：

```javascript
import ChannelPicker from '@/components/RelationPicker/ChannelPicker.vue'
import UserPicker from '@/components/RelationPicker/UserPicker.vue'
import SalesPicker from '@/components/RelationPicker/SalesPicker.vue'
import ContestPicker from '@/components/RelationPicker/ContestPicker.vue'
import ParticipantPicker from '@/components/RelationPicker/ParticipantPicker.vue'
import PartnerPicker from '@/components/RelationPicker/PartnerPicker.vue'
import OrderPicker from '@/components/RelationPicker/OrderPicker.vue'
Vue.component('ChannelPicker', ChannelPicker)
Vue.component('UserPicker', UserPicker)
Vue.component('SalesPicker', SalesPicker)
Vue.component('ContestPicker', ContestPicker)
Vue.component('ParticipantPicker', ParticipantPicker)
Vue.component('PartnerPicker', PartnerPicker)
Vue.component('OrderPicker', OrderPicker)
```

- [ ] **Step 9: Build 验证**

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui && npm run build:prod 2>&1 | tail -3
```
Expected: BUILD SUCCESS。

- [ ] **Step 10: Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/*.vue RuoYi-Vue/ruoyi-ui/src/main.js
git commit -m "feat(ui): 7 个 RelationPicker (Channel/User/Sales/Contest/Participant/Partner/Order) 薄包装+全局注册"
```

---

## Task 9: Round A 改造 — 销售工作台 + admin 销售管理的 8 处 _id 输入

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/index.vue` (sysUserId / managerId 改 Picker)
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/conflict.vue` (finalSalesId 改 Picker)

- [ ] **Step 1: jst/sales/index.vue 把 sysUserId 改 UserPicker**

在"新建销售"dialog 的 form 中，把：

```vue
<el-form-item label="系统用户ID" prop="sysUserId">
  <el-input-number v-model="form.sysUserId" placeholder="请输入 sys_user_id" />
</el-form-item>
```

替换为：

```vue
<el-form-item label="关联系统用户" prop="sysUserId">
  <user-picker v-model="form.sysUserId" placeholder="搜索用户名或昵称" />
</el-form-item>
```

如果 managerId 也是 input 改为：

```vue
<el-form-item label="直属主管" prop="managerId">
  <sales-picker v-model="form.managerId" placeholder="选择直属主管（可留空）" :clearable="true" />
</el-form-item>
```

- [ ] **Step 2: jst/sales/conflict.vue 的 resolve dialog 已经是 el-select，核实它查下属销售列表的方式正确（若是 hardcoded 改 SalesPicker）**

如果现状是 select + options 硬写，改为：
```vue
<sales-picker v-model="resolveForm.finalSalesId" placeholder="选择最终归属销售" />
```

- [ ] **Step 3: Build 验证 + 运行时验证**

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui && npm run build:prod 2>&1 | tail -3
```
Expected: BUILD SUCCESS。

- [ ] **Step 4: Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/jst/sales/index.vue RuoYi-Vue/ruoyi-ui/src/views/jst/sales/conflict.vue
git commit -m "feat(ui): Round A 销售工作台 _id 输入改 Picker (sysUserId+managerId+finalSalesId)"
```

---

## Task 10-13: Round B 改造 — 跨模块 _id 输入批量改造（按模块分 4 组）

> 这 4 个 task 都是机械替换：定位 `<el-input v-model="form.xxxId"` → 改 `<xxx-picker v-model="form.xxxId"`。每个 task 处理一个模块。

### Task 10: Event 模块（20 处）

**Files to modify:**
- `views/jst/event/jst_score_record/index.vue` — contestId/enrollId/userId/participantId
- `views/jst/event/jst_enroll_record/index.vue` — contestId/userId/participantId/channelId/templateId/orderId
- `views/jst/event/jst_cert_record/index.vue` — templateId/contestId/scoreId/enrollId/participantId
- `views/jst/event/jst_course/index.vue` — creatorId/maicSourceId（maicSourceId 如无独立实体可保留 input）
- `views/jst/event/jst_contest/index.vue` — partnerId/formTemplateId/createdUserId
- `views/jst/event/jst_cert_template/index.vue` — ownerId
- `views/jst/event/jst_enroll_form_template/index.vue` — ownerId
- `views/jst/event/jst_course_learn_record/index.vue` — courseId/userId
- `views/jst/event/jst_event_partner/index.vue` — partnerId

- [ ] **Step 1-2: 对每个文件替换 el-input → 对应 Picker**

示例（jst_score_record）：
```vue
<!-- 原 -->
<el-form-item label="赛事ID" prop="contestId">
  <el-input v-model="form.contestId" placeholder="请输入赛事ID" />
</el-form-item>

<!-- 改为 -->
<el-form-item label="赛事" prop="contestId">
  <contest-picker v-model="form.contestId" />
</el-form-item>
```

映射表：
- `contestId` → `<contest-picker>`
- `enrollId` → 暂保留 input（无 Picker，后续扩展 EnrollPicker，本期如频率低可接受）
- `userId` → `<user-picker>` 或 `<jst-user-picker>`（看是 sys_user 还是 jst_user）
- `participantId` → `<participant-picker>`
- `channelId` → `<channel-picker>`
- `templateId` / `formTemplateId` / `certTemplateId` — 模板类本期不建 TemplatePicker，用字典或保留 input + 加 "去模板管理复制ID" 帮助文字
- `orderId` → `<order-picker>`
- `partnerId` → `<partner-picker>`
- `creatorId` / `createdUserId` / `ownerId` → `<user-picker>`
- `courseId` / `scoreId` — 本期无专用 Picker 保留

- [ ] **Step 3: Build + Commit**

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui && npm run build:prod
cd D:/coding/jst_v1
git add RuoYi-Vue/ruoyi-ui/src/views/jst/event/
git commit -m "feat(ui): Round B-1 Event 模块 _id 改 Picker (contest/user/participant/channel/partner 共 ~18 处)"
```

### Task 11: Order 模块（12 处）

**Files:**
- `views/jst/order/admin-order/index.vue` 或 `views/jst/order/jst_order_main/index.vue` — 查找当前活跃的订单列表页
- `views/jst/order/admin-refund/index.vue` 或 `views/jst/order/jst_refund_record/index.vue`
- `views/jst/order/jst_appointment_record/index.vue`

映射：
- `userId` → `<user-picker>`
- `participantId` → `<participant-picker>`
- `channelId` → `<channel-picker>`
- `contestId` → `<contest-picker>`
- `partnerId` → `<partner-picker>`
- `teamAppointmentId` — 本期保留 input
- `payInitiatorId` → `<user-picker>`
- `couponId` / `couponTemplateId` — 本期保留 input（无 CouponPicker）
- `orderId` → `<order-picker>`
- `itemId` → 保留（订单明细子表，通过 order 查）
- `operatorId` → `<user-picker>`

- [ ] **Step 1-2: 替换 + Build + Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/jst/order/
git commit -m "feat(ui): Round B-2 Order 模块 _id 改 Picker (channel/user/participant/contest/partner)"
```

### Task 12: Channel / Marketing / Organizer / Risk / Message 模块（12 处）

**Files:**
- `views/jst/channel/jst_rebate_rule/index.vue` — contestId/channelId
- `views/jst/channel/jst_rebate_settlement/index.vue` — channelId
- `views/jst/channel/admin-rebate/index.vue` — orderId
- `views/jst/marketing/jst_user_coupon/index.vue` — userId
- `views/jst/marketing/jst_user_rights/index.vue` — ownerId (若是 sys_user 用 UserPicker，否则保留)
- `views/jst/marketing/jst_rights_writeoff_record/index.vue` — userRightsId（无 Picker 保留）
- `views/jst/organizer/jst_event_partner_apply/index.vue` — partnerId/auditUserId
- `views/jst/organizer/jst_channel_auth_apply/index.vue` — userId
- `views/jst/risk/jst_risk_alert/index.vue` — riskRuleId（保留）
- `views/jst/risk/jst_risk_case/index.vue` — assigneeId → `<user-picker>`
- `views/jst/message/jst_message_log/index.vue` — targetUserId → `<user-picker>`

- [ ] **Step 1-2: 替换 + Build + Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/jst/
git commit -m "feat(ui): Round B-3 Channel/Marketing/Organizer/Risk/Message 模块 _id 改 Picker"
```

### Task 13: Points / Participant / User / Contest / Binding 模块（14 处）

**Files:**
- `views/jst/points/jst_points_account/index.vue` — ownerId
- `views/jst/points/jst_points_ledger/index.vue` — ownerId, sourceRefId（保留）
- `views/jst/points/jst_growth_ledger/index.vue` — accountId（保留）
- `views/jst/points/jst_mall_exchange_order/index.vue` — userId
- `views/jst/participant/index.vue` — userId
- `views/jst/user/jst_user/index.vue` — currentLevelId（字典）/ boundChannelId → `<channel-picker>`
- `views/jst/user/jst_student_channel_binding/index.vue` — userId / channelId
- `views/jst/contest/edit.vue` — formTemplateId（本期保留）

- [ ] **Step 1-2: 替换 + Build + Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/jst/
git commit -m "feat(ui): Round B-4 Points/Participant/User/Binding 模块 _id 改 Picker"
```

---

## Task 14: Round C 改造 — 列表页展示用 EntityLink 替换（15 页）

**目标**：列表页 `<el-table-column label="渠道ID" prop="channelId">` 里原本显示数字的，改为 EntityLink 组件显示业务名 + 可跳转。

**Files:** 覆盖 Task 10-13 同样的 15 个列表页。

- [ ] **Step 1: 替换每个列表页的 ID 列**

示例（jst/enroll_record）：
```vue
<!-- 原 -->
<el-table-column label="渠道ID" prop="channelId" width="120" />

<!-- 改为 -->
<el-table-column :label="$fieldLabel('channelId')" width="160">
  <template slot-scope="s">
    <entity-link entity="channel" :id="s.row.channelId" :name="s.row.channelName" />
  </template>
</el-table-column>
```

- [ ] **Step 2: Mapper XML 补 LEFT JOIN（如果 name 字段尚未返回）**

如 `JstEnrollRecordMapper.xml`：
```xml
<select id="selectJstEnrollRecordList" ...>
    SELECT
        e.*,
        c.channel_name AS channelName,
        u.nick_name AS userName,
        p.participant_name AS participantName
    FROM jst_enroll_record e
    LEFT JOIN jst_channel c ON e.channel_id = c.channel_id
    LEFT JOIN sys_user u ON e.user_id = u.user_id
    LEFT JOIN jst_participant p ON e.participant_id = p.participant_id
    ...
</select>
```

15 个列表页逐个补。在 Domain 类加 `channelName / userName / participantName` 等冗余字段（@Transient 或直接 private）。

- [ ] **Step 3: Build + Commit**

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui && npm run build:prod
cd D:/coding/jst_v1/RuoYi-Vue && mvn -pl jst-event,jst-order,jst-channel,jst-marketing,jst-risk,jst-points -am compile -DskipTests -q
cd D:/coding/jst_v1
git add RuoYi-Vue/ruoyi-ui/ RuoYi-Vue/jst-*/src/main/resources/mapper/ RuoYi-Vue/jst-*/src/main/java/com/ruoyi/jst/*/domain/
git commit -m "feat(ui+backend): Round C 15 列表页 EntityLink 替换 + Mapper LEFT JOIN 补名称"
```

---

## Task 15: StatCardGroup 组件（仪表盘复用）

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/components/StatCardGroup.vue`

- [ ] **Step 1: 创建组件**

```vue
<template>
  <el-row :gutter="16" class="stat-card-group">
    <el-col v-for="card in cards" :key="card.label" :xs="12" :sm="6">
      <div class="stat-card" :class="[`stat-card--${card.theme || 'default'}`]">
        <div class="stat-card__label">{{ card.label }}</div>
        <div class="stat-card__value">
          <span v-if="card.prefix">{{ card.prefix }}</span>{{ formatValue(card.value, card.format) }}
        </div>
        <div v-if="card.trend !== undefined" class="stat-card__trend"
             :class="card.trend > 0 ? 'stat-card__trend--up' : 'stat-card__trend--down'">
          <i :class="card.trend > 0 ? 'el-icon-caret-top' : 'el-icon-caret-bottom'"></i>
          {{ Math.abs(card.trend) }}%
        </div>
        <div v-if="card.hint" class="stat-card__hint">{{ card.hint }}</div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: 'StatCardGroup',
  props: {
    cards: { type: Array, required: true }
    // card: { label, value, format?: 'money'|'number'|'percent', prefix?, theme?: 'default'|'blue'|'green'|'orange', trend?: number, hint? }
  },
  methods: {
    formatValue(v, format) {
      if (v == null) return '-'
      if (format === 'money') return Number(v).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
      if (format === 'percent') return (Number(v) * 100).toFixed(1) + '%'
      if (format === 'number') return Number(v).toLocaleString('zh-CN')
      return v
    }
  }
}
</script>

<style scoped>
.stat-card-group { margin-bottom: 16px; }
.stat-card { padding: 20px; background: #fff; border-radius: 4px; border-left: 4px solid #409eff; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.stat-card--green { border-left-color: #67c23a; }
.stat-card--orange { border-left-color: #e6a23c; }
.stat-card--red { border-left-color: #f56c6c; }
.stat-card__label { color: #909399; font-size: 13px; }
.stat-card__value { font-size: 24px; font-weight: 600; color: #303133; margin-top: 6px; }
.stat-card__trend { margin-top: 4px; font-size: 12px; }
.stat-card__trend--up { color: #67c23a; }
.stat-card__trend--down { color: #f56c6c; }
.stat-card__hint { margin-top: 4px; color: #c0c4cc; font-size: 12px; }
</style>
```

- [ ] **Step 2: 注册 + Commit**

```javascript
// main.js
import StatCardGroup from '@/components/StatCardGroup.vue'
Vue.component('StatCardGroup', StatCardGroup)
```

```bash
git add RuoYi-Vue/ruoyi-ui/src/components/StatCardGroup.vue RuoYi-Vue/ruoyi-ui/src/main.js
git commit -m "feat(ui): StatCardGroup 组件 (4 卡片统一 + trend + format 金额/百分比/数字)"
```

---

## Task 16: 销售个人 dashboard 强化

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/sales/me/dashboard.vue`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/sales/me/reminder.js`
- Create: Backend `SalesReminderController` 或在 SalesPerformance 加 endpoint

- [ ] **Step 1: 后端加提醒端点**

在 `SalesPerformanceService` 加接口：
```java
/** 超过 N 天未跟进的名下渠道 */
List<Map<String, Object>> listInactiveChannels(Long salesId, int days);

/** 快到期的预录入（expire_at 距今 N 天内） */
List<Map<String, Object>> listExpiringPreReg(Long salesId, int days);
```

在 `SalesPerformanceController` 加 endpoint `/sales/me/performance/reminders`。

- [ ] **Step 2: dashboard.vue 改造**

用 PageHeader + StatCardGroup + 日期范围筛选 + 提醒 panel：

```vue
<template>
  <div>
    <page-header title="销售工作台" help-text="你的业绩一目了然，今日该跟进的客户都在下面" />

    <!-- 日期筛选 -->
    <el-card class="filter-card" shadow="never">
      <el-radio-group v-model="dateRange" @change="load">
        <el-radio-button label="7">近 7 天</el-radio-button>
        <el-radio-button label="30">近 30 天</el-radio-button>
        <el-radio-button label="thisMonth">本月</el-radio-button>
        <el-radio-button label="lastMonth">上月</el-radio-button>
      </el-radio-group>
    </el-card>

    <stat-card-group :cards="statCards" />

    <el-row :gutter="16">
      <el-col :span="14">
        <el-card>
          <div slot="header">今日待跟进</div>
          <followup-task-card v-for="t in todayTasks" :key="t.taskId" :task="t" @complete="onComplete" />
          <empty-state-cta v-if="!todayTasks.length" title="今日无待跟进" description="放松心情，业绩稳如泰山" />
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <div slot="header">本月业务类型分布</div>
          <div ref="pieChart" style="height: 260px" />
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px" v-if="inactiveChannels.length">
      <div slot="header">
        <i class="el-icon-warning" style="color:#e6a23c"></i>
        ⚠️ 超过 14 天未跟进的渠道
      </div>
      <el-table :data="inactiveChannels" size="small">
        <el-table-column label="渠道">
          <template slot-scope="s"><entity-link entity="channel" :id="s.row.channelId" :name="s.row.channelName" /></template>
        </el-table-column>
        <el-table-column label="最后联系" prop="lastFollowupAt" />
        <el-table-column label="操作">
          <template slot-scope="s">
            <el-button type="text" size="mini" @click="goFollowup(s.row)">去跟进</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getMyPerformance } from '@/api/sales/me/performance'
import { getReminders } from '@/api/sales/me/reminder'
import { listMyTasks, completeTask } from '@/api/sales/me/task'
import * as echarts from 'echarts'

export default {
  data() {
    return {
      dateRange: 'thisMonth',
      performance: {},
      todayTasks: [],
      inactiveChannels: []
    }
  },
  computed: {
    statCards() {
      return [
        { label: '本月成交订单', value: this.performance.orderCount, format: 'number' },
        { label: '覆盖渠道', value: this.performance.channelCount, format: 'number', theme: 'green' },
        { label: '本月待跟进', value: this.todayTasks.length, format: 'number', theme: 'orange' },
        { label: '预录入命中', value: this.performance.matchedCount || 0, format: 'number' }
      ]
    }
  },
  created() { this.load() },
  mounted() { this.$nextTick(() => this.renderPie()) },
  methods: {
    async load() {
      const { month, days } = this.computeRange()
      const [p, r, t] = await Promise.all([
        getMyPerformance(month || ''),
        getReminders(),
        listMyTasks({ status: 'pending' })
      ])
      this.performance = p.data || {}
      this.inactiveChannels = r.data?.inactiveChannels || []
      this.todayTasks = (t.rows || []).filter(x => x.dueDate === this.today())
      this.$nextTick(() => this.renderPie())
    },
    computeRange() {
      // 简化：thisMonth 用当前 YYYY-MM，其他模式也转 month
      const now = new Date()
      if (this.dateRange === 'thisMonth') return { month: `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}` }
      if (this.dateRange === 'lastMonth') {
        const m = now.getMonth() === 0 ? 12 : now.getMonth()
        const y = now.getMonth() === 0 ? now.getFullYear() - 1 : now.getFullYear()
        return { month: `${y}-${String(m).padStart(2,'0')}` }
      }
      return { days: Number(this.dateRange) }
    },
    today() { return new Date().toISOString().slice(0, 10) },
    renderPie() {
      if (!this.$refs.pieChart) return
      const c = echarts.init(this.$refs.pieChart)
      const data = (this.performance.byType || []).map(x => ({ name: x.businessType, value: x.orderCount }))
      c.setOption({
        tooltip: { trigger: 'item' },
        series: [{ type: 'pie', data, radius: ['50%', '70%'] }]
      })
    },
    async onComplete(t) { await completeTask(t.taskId); this.$message.success('已完成'); this.load() },
    goFollowup(row) { this.$router.push(`/sales-workbench/channels/${row.channelId}`) }
  }
}
</script>

<style scoped>
.filter-card { margin-bottom: 12px; }
</style>
```

- [ ] **Step 3: Commit**

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui && npm run build:prod
cd D:/coding/jst_v1
git add RuoYi-Vue/ruoyi-ui/src/views/sales/me/dashboard.vue RuoYi-Vue/ruoyi-ui/src/api/sales/me/reminder.js RuoYi-Vue/jst-channel/
git commit -m "feat(sales-dashboard): 个人仪表盘强化 (PageHeader+StatCardGroup+日期筛选+今日待跟进+提醒 panel)"
```

---

## Task 17: 销售主管 dashboard（新建）

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/views/sales/manager/dashboard.vue`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/sales/manager/dashboard.js`
- Modify: Backend 加 `SalesManagerController.dashboard()` 端点

- [ ] **Step 1: 后端 API**

在 `SalesManagerController` 加：

```java
@GetMapping("/dashboard")
public AjaxResult dashboard(@RequestParam(required = false) String month) {
    Long currentSalesId = SalesScopeUtils.currentSalesIdRequired();
    // 聚合：团队人数 / 团队 GMV / 团队订单 / 团队跟进活跃度
    // 下属销售排行 (top 10)
    // 健康度告警（连续 3 周零业绩的销售）
    Map<String, Object> data = managerDashboardService.aggregate(currentSalesId, month);
    return AjaxResult.success(data);
}
```

需新建 `ManagerDashboardService` + 实现，用 JdbcTemplate 聚合查询。

- [ ] **Step 2: 前端 dashboard.vue**

```vue
<template>
  <div>
    <page-header title="团队看板" help-text="你的下属业绩状况、团队健康度、派任务都在这里" />

    <el-card class="filter-card" shadow="never">
      <el-radio-group v-model="dateRange" @change="load">
        <el-radio-button label="7">近 7 天</el-radio-button>
        <el-radio-button label="30">近 30 天</el-radio-button>
        <el-radio-button label="thisMonth">本月</el-radio-button>
      </el-radio-group>
    </el-card>

    <stat-card-group :cards="statCards" />

    <el-row :gutter="16">
      <el-col :span="14">
        <el-card>
          <div slot="header">下属销售业绩排行</div>
          <el-table :data="ranking" size="small">
            <el-table-column label="销售">
              <template slot-scope="s"><entity-link entity="sales" :id="s.row.salesId" :name="s.row.salesName" /></template>
            </el-table-column>
            <el-table-column prop="orderCount" label="订单" width="80" />
            <el-table-column label="GMV">
              <template slot-scope="s">¥{{ s.row.gmv }}</template>
            </el-table-column>
            <el-table-column label="提成">
              <template slot-scope="s">¥{{ s.row.commissionAmount }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <div slot="header">团队客户类型分布</div>
          <div ref="barChart" style="height: 260px" />
        </el-card>
      </el-col>
    </el-row>

    <el-card v-if="healthAlerts.length" style="margin-top: 16px">
      <div slot="header">
        <i class="el-icon-warning" style="color:#f56c6c"></i>
        ⚠️ 团队健康度告警
      </div>
      <el-alert
        v-for="a in healthAlerts" :key="a.salesId"
        :title="a.message" type="warning" :closable="false" show-icon
        style="margin-bottom: 8px"
      >
        <el-button size="mini" slot="default" @click="contactSales(a.salesId)">联系销售</el-button>
      </el-alert>
    </el-card>
  </div>
</template>

<script>
import { getManagerDashboard } from '@/api/sales/manager/dashboard'
import * as echarts from 'echarts'

export default {
  data() {
    return {
      dateRange: 'thisMonth',
      overview: {},
      ranking: [],
      healthAlerts: []
    }
  },
  computed: {
    statCards() {
      return [
        { label: '团队人数', value: this.overview.teamCount },
        { label: '本月团队订单', value: this.overview.orderCount, theme: 'green' },
        { label: '本月团队 GMV', value: this.overview.gmv, format: 'money', prefix: '¥', theme: 'orange' },
        { label: '本月跟进活跃度', value: this.overview.followupCount }
      ]
    }
  },
  created() { this.load() },
  methods: {
    async load() {
      const res = await getManagerDashboard(this.computeMonth())
      this.overview = res.data.overview || {}
      this.ranking = res.data.ranking || []
      this.healthAlerts = res.data.healthAlerts || []
      this.$nextTick(() => this.renderBar())
    },
    computeMonth() {
      const now = new Date()
      return this.dateRange === 'thisMonth' ? `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}` : null
    },
    renderBar() {
      if (!this.$refs.barChart) return
      const c = echarts.init(this.$refs.barChart)
      c.setOption({
        xAxis: { type: 'category', data: (this.overview.tagDistribution || []).map(x => x.name) },
        yAxis: { type: 'value' },
        series: [{ type: 'bar', data: (this.overview.tagDistribution || []).map(x => x.count) }]
      })
    },
    contactSales(id) { this.$router.push(`/jst/sales/${id}`) }
  }
}
</script>
```

- [ ] **Step 3: Commit**

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui && npm run build:prod
cd D:/coding/jst_v1
git add RuoYi-Vue/ruoyi-ui/src/views/sales/manager/dashboard.vue RuoYi-Vue/ruoyi-ui/src/api/sales/manager/dashboard.js RuoYi-Vue/jst-channel/
git commit -m "feat(manager-dashboard): 销售主管团队看板 (4 stat+排行+客户分布+健康度告警)"
```

---

## Task 18: admin 业绩看板扩展

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/dashboard.vue`
- Modify: Backend `AdminSalesDashboardService` 加指标

- [ ] **Step 1: 后端加 3 个指标**

```java
// 1. 销售成本趋势（近 6 月 ledger 总支出）
List<Map<String, Object>> costTrend6Months();
// 2. 压缩触发比例（订单触发 compress_ratio < 1 的占比）
Map<String, Object> compressionRate(String month);
// 3. 渠道业务类型热力图（销售×业务类型 matrix）
List<Map<String, Object>> channelTypeHeatmap(String month);
```

- [ ] **Step 2: 前端加日期/地区/业务类型筛选**

在 dashboard.vue 顶部加：

```vue
<el-card class="filter-card" shadow="never">
  <el-form :inline="true">
    <el-form-item label="日期">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd"
        @change="load"
      />
    </el-form-item>
    <el-form-item label="地区">
      <el-cascader v-model="region" :options="regionOptions" placeholder="全部地区" clearable @change="load" />
    </el-form-item>
    <el-form-item label="业务类型">
      <el-select v-model="businessType" clearable placeholder="全部业务" @change="load">
        <el-option v-for="d in businessTypeDict" :key="d.value" :label="d.label" :value="d.value" />
      </el-select>
    </el-form-item>
  </el-form>
</el-card>
```

+ 3 个新 card：成本趋势折线图 / 压缩触发率 stat-card（带 tooltip 解释）/ 热力图 echarts。

- [ ] **Step 3: Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/jst/sales/dashboard.vue RuoYi-Vue/jst-channel/
git commit -m "feat(admin-dashboard): 日期/地区/业务筛选 + 成本趋势+压缩触发率+渠道热力图"
```

---

## Task 19: 菜单 SQL 注册销售/主管 dashboard + 默认路由

**Files:**
- Create: `架构设计/ddl/99-migration-plan-06-menus.sql`

- [ ] **Step 1: 创建菜单 SQL**

```sql
-- =====================================================
-- Plan-06 菜单注册：销售工作台首页 + 主管团队看板
-- 销售登录默认进 978500 / 主管登录默认进 978507
-- =====================================================
SET NAMES utf8mb4;

-- 销售工作台首页（默认页）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (978500, '工作台首页', 9785, 0, 'index', 'sales/me/dashboard', 1, 0, 'C', '0', '0', 'sales:me:dashboard:view', 'dashboard', 'admin', NOW());

-- 销售主管团队看板
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (978507, '团队看板', 9785, 4, 'manager-dashboard', 'sales/manager/dashboard', 1, 0, 'C', '0', '0', 'sales:manager:dashboard:view', 'chart', 'admin', NOW());

-- 角色绑定：jst_sales (501) 绑工作台首页
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (501, 978500), (502, 978500), (1, 978500);
-- 主管团队看板：仅 jst_sales_manager + admin
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (502, 978507), (1, 978507);

-- 默认路由：登录后根据角色跳不同 dashboard，由前端 router/permission 处理（见 Task 20）
```

- [ ] **Step 2: 应用 + 验证**

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 jst < "架构设计/ddl/99-migration-plan-06-menus.sql"
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SELECT menu_id, menu_name FROM sys_menu WHERE menu_id IN (978500, 978507);" jst
```

- [ ] **Step 3: Commit**

```bash
git add "架构设计/ddl/99-migration-plan-06-menus.sql"
git commit -m "feat(ddl): 菜单注册 978500 工作台首页 + 978507 团队看板 + 角色绑定"
```

---

## Task 20: 登录后默认路由按角色分发

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/permission.js` 或 `store/modules/permission.js`

- [ ] **Step 1: 在登录成功后根据角色跳 dashboard**

找 `permission.js` 的 `router.beforeEach` 逻辑，在 `next({ ...to, replace: true })` 之前加：

```javascript
// Plan-06: 登录后按角色跳默认 dashboard
if (to.path === '/' || to.path === '/index') {
  const roles = store.getters.roles || []
  if (roles.includes('jst_sales_manager')) {
    return next('/sales-workbench/manager-dashboard')
  }
  if (roles.includes('jst_sales')) {
    return next('/sales-workbench/index')
  }
  // admin / operator 保持默认 /jst/dashboard
}
```

- [ ] **Step 2: Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/permission.js
git commit -m "feat(permission): 登录后按角色分发默认路由（销售→工作台首页，主管→团队看板）"
```

---

## Task 21: 销售主管 settlement scope 精细化（plan-05 遗留）

**Files:**
- Modify: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/admin/AdminSettlementController.java`
- Modify: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/dto/SettlementQueryReqDTO.java` 或 `JstSalesCommissionSettlement` Query
- Modify: `JstSalesCommissionSettlementMapper.xml`

- [ ] **Step 1: Settlement query 加 scopeSalesIds 过滤支持**

Query DTO 加字段：
```java
private java.util.List<Long> scopeSalesIds;
```

Mapper XML `selectJstSalesCommissionSettlementList` 加：
```xml
<if test="scopeSalesIds != null and scopeSalesIds.size() > 0">
    AND sales_id IN
    <foreach collection="scopeSalesIds" item="id" open="(" separator="," close=")">#{id}</foreach>
</if>
```

- [ ] **Step 2: Controller 逻辑**

`AdminSettlementController.list()` 改：
```java
public TableDataInfo list(SettlementQueryReqDTO query) {
    LoginUser u = SalesScopeUtils.getLoginUserQuietly();
    // 主管自动按下属过滤（除非 admin 显式传 scopeSalesIds）
    if (SalesScopeUtils.isManagerRole(u) && (query.getScopeSalesIds() == null || query.getScopeSalesIds().isEmpty())) {
        Long managerId = SalesScopeUtils.currentSalesId();
        query.setScopeSalesIds(salesService.resolveScopeSalesIds(managerId));
    }
    startPage();
    return getDataTable(settlementService.listForAdmin(query));
}
```

- [ ] **Step 3: Commit**

```bash
cd D:/coding/jst_v1/RuoYi-Vue && mvn -pl jst-channel -am compile -DskipTests -q
cd D:/coding/jst_v1
git add RuoYi-Vue/jst-channel/
git commit -m "feat(channel): 销售主管 settlement scope 精细化 (自动按下属 salesIds 过滤)"
```

---

## Task 22: 空态 CTA 批量替换

**Files:**
- 全仓 grep `el-empty` 定位 ~30 处列表页空态

- [ ] **Step 1: 定位 + 替换**

```bash
grep -rn "<el-empty " D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/jst/ D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/sales/ --include="*.vue" | head -20
```

对每个定位：
- 若是**列表页 table 为空**：改为 `<empty-state-cta :title="..." :primary-text="..." @primary-click="..." />`
- 若是**详情页某 section 为空**：保持 `<el-empty>` 但传 description 变化友好提示

典型替换：
```vue
<!-- 原 -->
<el-empty description="暂无数据" />

<!-- 改为 -->
<empty-state-cta
  title="还没有销售记录"
  description="创建第一个销售才能记录业绩"
  primary-text="新建销售"
  @primary-click="openCreate"
/>
```

- [ ] **Step 2: Commit**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/
git commit -m "feat(ui): 30+ 列表页空态替换为 EmptyStateCTA (带 CTA 引导业务员下一步)"
```

---

## Task 23: 删除 3 个 Deprecated 页面 + 清菜单

**Files:**
- Delete: `views/jst/order/jst_order_main/index.vue` (若确认 redirect 无实际使用)
- Delete: `views/jst/order/jst_enroll_record/index.vue`
- Delete: `views/jst/order/jst_refund_record/index.vue`
- Create: `架构设计/ddl/99-migration-plan-06-menu-cleanup.sql`

- [ ] **Step 1: 验证 redirect 目标页存在**

```bash
ls D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/jst/order/
```
确认 `admin-order/index.vue`、`jst/enroll/*`、`admin-refund/*` 存在。

- [ ] **Step 2: 删除 3 个 Deprecated 页面**

```bash
rm D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/jst/order/jst_order_main/index.vue
rm D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/jst/order/jst_enroll_record/index.vue
rm D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/jst/order/jst_refund_record/index.vue
```

- [ ] **Step 3: SQL 清理对应菜单**

```sql
-- 架构设计/ddl/99-migration-plan-06-menu-cleanup.sql
-- 隐藏/删除 3 个 Deprecated 菜单
-- 具体 menu_id 需要先查 SELECT menu_id FROM sys_menu WHERE component LIKE 'jst/order/jst_%';
-- 假设查出为 X, Y, Z
UPDATE sys_menu SET visible = '1', status = '1' WHERE component IN (
    'jst/order/jst_order_main/index',
    'jst/order/jst_enroll_record/index',
    'jst/order/jst_refund_record/index'
);
```

- [ ] **Step 4: Apply + Commit**

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 jst < "架构设计/ddl/99-migration-plan-06-menu-cleanup.sql"
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui && npm run build:prod
cd D:/coding/jst_v1
git add -A
git commit -m "chore: 删除 3 个 Deprecated 订单/报名/退款老页面 + 菜单隐藏"
```

---

## Task 24: 全量 build 验证

**Files:** (无改动，仅验证)

- [ ] **Step 1: Backend 编译**

```bash
cd D:/coding/jst_v1/RuoYi-Vue && mvn clean compile -DskipTests -q 2>&1 | tail -3
```
Expected: BUILD SUCCESS

- [ ] **Step 2: Frontend build**

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui && npm run build:prod 2>&1 | tail -3
```
Expected: BUILD SUCCESS

- [ ] **Step 3: Backend 单测**

```bash
cd D:/coding/jst_v1/RuoYi-Vue && mvn -pl jst-common,jst-channel test 2>&1 | grep -E "Tests run:|BUILD" | tail -5
```
Expected: 所有既有单测绿（不破坏 plan-01~05 的 84+ 单测）

- [ ] **Step 4: Spring-Boot 启动 sanity**

```bash
cd D:/coding/jst_v1/RuoYi-Vue && mvn -pl ruoyi-admin spring-boot:run -DskipTests
```
(Ctrl+C 停止)。应看到 Bean 正常注册：EntityBriefController、ManagerDashboardService 等。

---

## Task 25: CLAUDE.md 更新 + merge main

**Files:**
- Modify: `CLAUDE.md`

- [ ] **Step 1: CLAUDE.md 更新**

将 §六 SALES-DISTRIBUTION 行加一行 plan-06 状态：
```
+ plan-06 UX 精品化（7 Picker + EntityLink + 三角色 dashboard + 56 处 _id 改造 + 30 处 EntityLink 展示 + 删 3 Deprecated）
```

- [ ] **Step 2: Merge**

```bash
git add CLAUDE.md
git commit -m "docs: CLAUDE.md plan-06 UX 精品化完成"
git checkout main
git merge --no-ff feature/plan-06-ux-polish -m "Merge plan-06 UX 精品化

- 9 新组件: PageHeader/EmptyStateCTA/EntityLink/StatCardGroup + 7 RelationPicker
- 1 工具: fieldLabelMap + \$fieldLabel 全局函数
- 1 后端 Controller: EntityBriefController (/admin/entity/{search,brief})
- 56 处 _id 输入改 Picker 覆盖 sales/order/event/channel/marketing/risk/points
- 30 处列表 EntityLink 替换 + Mapper LEFT JOIN 名称
- 销售/主管/admin 三角色 dashboard (+日期/地区/业务筛选)
- 菜单新增 978500 工作台首页 + 978507 团队看板 + 登录默认路由
- 销售主管 settlement scope 精细化
- 空态 CTA 标准化
- 删 3 Deprecated 页面 + 清菜单"
git push origin main
```

---

## 自检清单

- [ ] 9 新 Vue 组件全部创建且 build 绿
- [ ] fieldLabelMap 覆盖 50+ 字段
- [ ] EntityBriefController 支持 9 种实体
- [ ] 56 处 `_id` 输入全改为 Picker（spot check 5 个文件）
- [ ] 30 个列表页 EntityLink 替换
- [ ] 销售/主管/admin 3 dashboard 全新建/强化
- [ ] 菜单 978500/978507 已注册 + 登录默认路由正常
- [ ] settlement 主管只能看下属 ✓
- [ ] 3 Deprecated 页面物理删除 + 菜单 visible=1
- [ ] 空态 CTA ≥ 15 处替换
- [ ] mvn clean compile 绿 / npm build:prod 绿
- [ ] 既有 84 单测全绿（未破坏）
- [ ] admin/销售/主管 3 账号均可以登录 → 看到自己的 dashboard → 点任意 _id 字段都是下拉/跳转

---

**End of Plan**
