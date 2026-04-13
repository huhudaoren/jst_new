# WEB-ADMIN-ID2NAME-FE — 管理端列表 ID→名称 前端展示+跳转

> 优先级：P1 | 预估：M | Agent：Web Admin Agent
> 依赖：WEB-ADMIN-ID2NAME-BE（后端需先返回 xxxName 字段）

---

## 一、背景

后端已在列表接口中 JOIN 补齐了 `contestName`、`channelName`、`userName` 等名称字段。前端需要：
1. 把表格中的 ID 列替换为名称展示
2. 名称做成可点击的 `el-link`，跳转到对应业务详情页

## 二、改动范围（15 个页面）

### 2.1 渠道模块

| 页面 | 原列 | 改为 | 跳转目标 |
|---|---|---|---|
| `channel/jst_rebate_ledger/index.vue` | 渠道ID → `channelName` | 渠道名称(link) | `/jst/channel`（详情） |
| | 订单ID → `orderNo` | 订单号(link) | `/jst/order/admin-order?orderId=xxx&autoOpen=1` |
| `channel/jst_rebate_rule/index.vue` | 赛事ID → `contestName` | 赛事名称(link) | `/jst/contest`（详情） |
| | 渠道ID → `channelName` | 渠道名称 | — |
| `channel/jst_event_settlement/index.vue` | 赛事方 → `partnerName` | 赛事方名称 | — |
| | 赛事 → `contestName` | 赛事名称(link) | `/jst/contest` |
| `channel/jst_rebate_settlement/index.vue` | 渠道ID → `channelName` | 渠道名称 | — |

### 2.2 订单模块

| 页面 | 原列 | 改为 | 跳转目标 |
|---|---|---|---|
| `order/jst_appointment_record/index.vue` | 关联订单ID → `orderNo` | 订单号(link) | `/jst/order/admin-order?orderId=xxx&autoOpen=1` |
| `order/jst_order_item/index.vue` | 订单号/ID → `orderNo` | 订单号(link) | 已有跳转，确认使用 `orderNo` |

### 2.3 积分模块

| 页面 | 原列 | 改为 | 跳转目标 |
|---|---|---|---|
| `points/jst_points_account/index.vue` | 持有者ID → `ownerName` | 用户名称(link) | `/jst/user`（详情） |
| | 当前等级ID → `levelName` | 等级名称 | — |
| `points/jst_points_ledger/index.vue` | 持有者ID → `ownerName` | 用户名称(link) | `/jst/user` |
| `points/jst_mall_exchange_order/index.vue` | 用户ID → `userName` | 用户名称(link) | `/jst/user` |
| | 商品ID → `goodsName` | 商品名称(link) | `/jst/mall` |

### 2.4 营销模块

| 页面 | 原列 | 改为 | 跳转目标 |
|---|---|---|---|
| `marketing/jst_user_coupon/index.vue` | 用户ID → `userName` | 用户名称(link) | `/jst/user` |
| | 模板ID → `couponTemplateName` | 券模板名 | — |
| | 使用订单ID → `orderNo` | 订单号(link) | `/jst/order/admin-order?orderId=xxx&autoOpen=1` |
| `marketing/jst_user_rights/index.vue` | 用户ID → `userName` | 用户名称 | `/jst/user` |

### 2.5 用户模块

| 页面 | 原列 | 改为 | 跳转目标 |
|---|---|---|---|
| `user/jst_channel/index.vue` | 用户ID → `userName` | 用户名称(link) | `/jst/user` |
| `user/jst_student_channel_binding/index.vue` | 学生ID → `studentName` | 学生名称(link) | `/jst/user` |
| | 渠道ID → `channelName` | 渠道名称(link) | `/jst/channel` |
| `participant/index.vue` | 认领用户ID → `claimUserName` | 用户名称 | `/jst/user` |
| `user/detail.vue` | 嵌套表中的 ID 列 | 相应名称 | — |

### 2.6 赛事模块

| 页面 | 原列 | 改为 | 跳转目标 |
|---|---|---|---|
| `event/jst_enroll_form_template/index.vue` | 所属方ID → `ownerName` | 所属赛事名 | `/jst/contest` |
| `event/jst_cert_template/index.vue` | 所属方ID → `ownerName` | 所属赛事名 | `/jst/contest` |

## 三、实现规范

### 3.1 名称展示

将原来的：
```html
<el-table-column label="渠道ID" prop="channelId" />
```

改为：
```html
<el-table-column label="渠道" min-width="140" show-overflow-tooltip>
  <template slot-scope="scope">
    <el-link v-if="scope.row.channelName" type="primary" :underline="false" @click="goChannel(scope.row)">
      {{ scope.row.channelName }}
    </el-link>
    <span v-else>{{ scope.row.channelId || '--' }}</span>
  </template>
</el-table-column>
```

### 3.2 跳转方法

统一使用 `$router.push`：
```javascript
goChannel(row) {
  // 跳转到渠道列表，携带 ID 参数自动打开详情
  this.$router.push({ path: '/jst/channel', query: { channelId: row.channelId, autoOpen: '1' } })
},
goOrder(row) {
  this.$router.push({ path: '/jst/order/admin-order', query: { orderId: String(row.orderId), autoOpen: '1' } })
},
goUser(row) {
  this.$router.push({ path: '/jst/user', query: { userId: row.userId || row.ownerId, autoOpen: '1' } })
},
goContest(row) {
  this.$router.push({ path: '/jst/contest', query: { contestId: row.contestId, autoOpen: '1' } })
}
```

### 3.3 手机端卡片同步

手机端 `mobile-card` 中的 ID 展示也要同步改为名称。

### 3.4 详情抽屉同步

`el-drawer` 中的 `el-descriptions-item` 如果展示 ID，也要改为名称。

### 3.5 降级处理

如果后端未返回名称（字段为 null），保持显示原 ID，不报错。

## 四、DoD

- [ ] 15 个页面的 ID 列全部替换为名称展示
- [ ] 有跳转目标的名称用 `el-link` 包裹，可点击跳转
- [ ] 手机端卡片同步更新
- [ ] 详情抽屉同步更新
- [ ] null 降级为显示 ID
- [ ] `npm run build:prod` 通过
- [ ] 报告交付
