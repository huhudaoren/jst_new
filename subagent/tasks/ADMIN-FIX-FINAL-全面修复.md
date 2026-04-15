# ADMIN-FIX-FINAL — 管理端全面修复

> 优先级：P0 | 预估：L | Agent：Backend Agent + Web Admin Agent

---

## 一、问题清单（6 项）

### 问题 1：运营看板 + 首页提示"无权限"
**根因**：菜单分组后路由路径变了（`/jst/contest` → `/jst/group-contest/contest`），但 dashboard 的快捷入口 candidates 还是旧路径。
**修复**：更新 dashboard candidates 路径，或改 collectAvailablePaths 逻辑支持模糊匹配。

### 问题 2：参赛档案页 API 404
**根因**：`admin-participant.js` 调用 `/system/jst_participant/list`，但后端 Controller 在 gen/ 未编译。
**修复**：在 jst-user 模块新增 admin 级 ParticipantAdminController。

### 问题 3：菜单缺少引导
**需求**：运营看板增加每个菜单的便捷入口和功能说明。
**修复**：丰富 dashboard 的快捷操作区域 + 增加菜单分组说明卡片。

### 问题 4：渠道→用户链路不通
**需求**：渠道管理界面应有绑定用户的二级界面。
**修复**：渠道详情 drawer 中增加"绑定用户"Tab，展示该渠道下的所有绑定学生。

### 问题 5：详情展示 JSON 原始数据
**根因**：多个详情 drawer/dialog 直接展示 JSON 字段（form_snapshot_json、qualification_json 等）。
**修复**：将 JSON 展示改为可视化卡片/表格。

### 问题 6：首页 404
**根因**：可能与 dashboard 路径变化有关，或者 "首页" 指的是 /index 路由。
**修复**：检查并修复。

---

## 二、修复方案

### 2.1 Backend: 参赛档案 Admin Controller（问题 2）

新增 `jst-user/src/main/java/com/ruoyi/jst/user/controller/admin/ParticipantAdminController.java`：

```java
@RestController
@RequestMapping("/jst/admin/participant")
public class ParticipantAdminController extends BaseController {
    
    @PreAuthorize("@ss.hasPermi('jst:participant:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstParticipant query) { ... }
    
    @PreAuthorize("@ss.hasPermi('jst:participant:query')")
    @GetMapping("/{participantId}")
    public AjaxResult getInfo(@PathVariable Long participantId) { ... }
}
```

同时新增 `/jst/admin/participant-user-map/list`（认领关系查询）。

然后修改前端 `admin-participant.js`：
```javascript
// 修改前
url: '/system/jst_participant/list'
// 修改后
url: '/jst/admin/participant/list'
```

### 2.2 Frontend: Dashboard 路径修复 + 引导增强（问题 1 + 3）

**修复 candidates 路径**：

```javascript
// 方案：候选路径加上分组路径
candidates: ['/jst/contest', '/jst/group-contest/contest']
// 或者：修改 resolveFirstAvailablePath 支持末段匹配
```

推荐方案：**修改 `route-access.js` 的匹配逻辑**，使 `/jst/contest` 能匹配 `/jst/group-contest/contest`（按路径末段匹配）。这样无论菜单怎么分组，候选路径都能工作。

```javascript
export function resolveFirstAvailablePath(candidates, availableSet) {
  if (!Array.isArray(candidates) || !availableSet) return null
  for (const candidate of candidates) {
    const path = normalizePath(candidate)
    // 精确匹配
    if (availableSet.has(path)) return path
    // 末段匹配（兼容分组路径变化）
    const segments = path.split('/').filter(Boolean)
    const lastSegment = segments[segments.length - 1]
    for (const available of availableSet) {
      if (available.endsWith('/' + lastSegment)) return available
    }
  }
  return null
}
```

**增强 dashboard 引导**：

在运营数据看板中增加"功能导航"区域：

```vue
<el-card class="nav-guide-card">
  <div slot="header">功能导航</div>
  <el-row :gutter="16">
    <el-col :span="8" v-for="group in menuGroups" :key="group.key">
      <div class="nav-group" @click="$router.push(group.path)">
        <i :class="group.icon" class="nav-group__icon" />
        <div class="nav-group__title">{{ group.title }}</div>
        <div class="nav-group__desc">{{ group.desc }}</div>
      </div>
    </el-col>
  </el-row>
</el-card>
```

menuGroups 数据：
```javascript
menuGroups: [
  { key: 'contest', title: '赛事运营', desc: '赛事管理、报名审核、课程管理、入驻审核', icon: 'el-icon-trophy', path: '/jst/group-contest/contest' },
  { key: 'order', title: '订单交易', desc: '订单管理、退款处理、预约记录、核销明细', icon: 'el-icon-shopping-bag-1', path: '/jst/group-order/order/admin-order' },
  { key: 'user', title: '用户渠道', desc: '用户管理、参赛档案、渠道管理、绑定关系', icon: 'el-icon-user', path: '/jst/group-user/user' },
  { key: 'marketing', title: '营销权益', desc: '优惠券、权益模板、发放管理', icon: 'el-icon-present', path: '/jst/group-marketing/coupon/template' },
  { key: 'points', title: '积分商城', desc: '积分账户、规则配置、商城商品、兑换订单', icon: 'el-icon-coin', path: '/jst/group-points/points/points-account' },
  { key: 'finance', title: '财务管理', desc: '打款记录、合同管理、发票管理', icon: 'el-icon-wallet', path: '/jst/group-finance/finance/payout' },
  { key: 'risk', title: '风控审计', desc: '风控规则、告警处理、黑名单、审计日志', icon: 'el-icon-warning', path: '/jst/group-risk/risk/risk-rule' },
  { key: 'event', title: '赛事数据', desc: '成绩记录、证书管理、表单模板', icon: 'el-icon-document', path: '/jst/group-event/event/score-record' }
]
```

### 2.3 Frontend: 渠道→用户链路（问题 4）

在渠道管理页 `jst/channel/index.vue` 的详情 drawer 中增加"绑定用户"Tab：

```vue
<el-tabs v-model="detailTab">
  <el-tab-pane label="渠道信息" name="info">...</el-tab-pane>
  <el-tab-pane label="绑定用户" name="bindings">
    <el-table :data="channelBindings" v-loading="bindingLoading">
      <el-table-column label="学生姓名" prop="studentName" />
      <el-table-column label="手机号" prop="mobile" />
      <el-table-column label="绑定时间" prop="bindTime" />
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click="viewStudent(scope.row)">查看档案</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-tab-pane>
</el-tabs>
```

加载渠道绑定用户列表：`GET /jst/admin/binding/list?channelId=xxx`

### 2.4 Frontend: JSON 展示可视化（问题 5）

排查所有详情页中直接展示 JSON 的位置，改为可视化：

| 位置 | JSON 字段 | 改造方式 |
|---|---|---|
| 入驻审核详情 | qualification_json | 附件列表（文件名 + 下载链接） |
| 入驻审核详情 | settlement_info_json | 表格展示（户名/账号/开户行） |
| 入驻审核详情 | invoice_info_json | 表格展示（发票类型/税号） |
| 渠道认证详情 | materials_json | 附件列表 |
| 报名详情 | form_snapshot_json | 复用 jst-form-render 或 el-descriptions 渲染 |
| 风控规则 | threshold_json | key-value 表格 |
| 优惠券批次 | target_json | 目标用户列表/标签 |

通用 JSON 可视化组件：

```vue
<!-- components/JsonDisplay.vue -->
<template>
  <div v-if="isArray">
    <el-tag v-for="(item, i) in parsed" :key="i" size="small" class="json-tag">
      {{ item.name || item.fileName || item.label || JSON.stringify(item) }}
    </el-tag>
  </div>
  <el-descriptions v-else-if="isObject" :column="1" border size="small">
    <el-descriptions-item v-for="(val, key) in parsed" :key="key" :label="labelMap[key] || key">
      {{ val }}
    </el-descriptions-item>
  </el-descriptions>
  <span v-else>{{ value }}</span>
</template>
```

---

## 三、DoD

- [ ] 参赛档案 Admin Controller（后端）
- [ ] admin-participant.js API 路径修复
- [ ] dashboard candidates 路径修复（route-access.js 末段匹配）
- [ ] dashboard 功能导航区域（8 个分组入口 + 描述）
- [ ] 渠道详情增加"绑定用户"Tab
- [ ] JSON 展示可视化（至少 5 处）
- [ ] `mvn compile` + `npm run build:prod` 通过
- [ ] 部署到测试服务器
