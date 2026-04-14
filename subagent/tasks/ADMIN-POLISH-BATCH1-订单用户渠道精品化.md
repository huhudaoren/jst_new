# ADMIN-POLISH-BATCH1 — 订单交易 + 用户渠道 + 营销权益精品化（17 页）

> 优先级：P1 | 预估：L | Agent：Web Admin Agent

---

## 一、背景

平台数据管理下约 40 个代码生成页面存在"开发者思维"：关联数据用 ID 填写、无中文提示、无操作确认、枚举显示英文、金额无格式化。需要全量改造为傻瓜式操作水平。

## 二、改造标准（每页必须达到）

### S1. 页面顶部 Hero 区
```vue
<div class="page-hero">
  <div class="hero-title">XXX管理</div>
  <div class="hero-desc">一句话说明本页功能</div>
</div>
```

### S2. 搜索表单
- 最多 3-4 个核心筛选，其余折叠
- 所有 input 有中文 placeholder
- 关联字段用 `el-select filterable remote`（搜名称不填 ID）
- 枚举字段用 `el-select` + 字典选项
- 时间用 `el-date-picker type="daterange"`

### S3. 表格列
- 枚举列用 `<dict-tag :options="dict.type.xxx" :value="value" />`
- 金额列加 ¥ + 千分位：`¥{{ formatMoney(row.amount) }}`
- 时间列 `yyyy-MM-dd HH:mm`（去秒）
- ID 列隐藏或放最后
- 状态用彩色 el-tag
- 关联显示名称不显示 ID
- 操作列不超过 3 按钮

### S4. 表单弹窗
- 字段分组（el-divider 分区）
- 隐藏系统字段（create_by/time/update_by/time/del_flag）
- 关联字段改 el-select 远程搜索
- 所有 input 有 placeholder
- 必填标红星 + 校验规则
- 数字用 el-input-number
- 金额 precision=2

### S5. 操作确认
- 删除：`this.$modal.confirm('确认删除？')`
- 审核：确认弹窗
- 批量：显示选中数量

### S6. 空状态
```vue
<el-empty v-if="!loading && list.length === 0" description="暂无数据" />
```

## 三、改造页面清单

在每页的 `<script>` 中引入字典：
```javascript
export default {
  dicts: ['jst_xxx_status', ...],  // 按需声明
  // ...
}
```

引入格式化工具：
```javascript
import { formatMoney } from '@/utils/format'
```

### 3.1 订单交易组（8 页）

| # | 文件 | Hero 标题 | Hero 描述 | 字典 | 改造要点 |
|---|---|---|---|---|---|
| 1 | `jst/order/jst_order_item/index.vue` | 订单明细 | 查看所有订单的商品明细 | jst_sku_type | contestId→contestName, 金额格式化, sku_type翻译 |
| 2 | `jst/order/jst_payment_record/index.vue` | 支付记录 | 查看所有支付与退款流水 | jst_pay_method | pay_method翻译, 金额格式化, 关联订单号 |
| 3 | `jst/order/jst_appointment_record/index.vue` | 预约记录 | 查看所有个人预约记录 | jst_audit_status | 状态标签, 时间格式, 关联赛事名称 |
| 4 | `jst/order/jst_team_appointment/index.vue` | 团队预约 | 查看所有团队预约记录 | jst_audit_status | 状态标签, 人数显示, 关联赛事/渠道 |
| 5 | `jst/order/jst_appointment_writeoff_item/index.vue` | 核销明细 | 查看预约核销详情 | - | 核销类型中文化, 时间格式 |
| 6 | `jst/appointment/index.vue` | 预约管理 | 管理赛事预约时间段 | jst_audit_status | 状态标签, 容量/已约显示 |
| 7 | `jst/channel/admin-withdraw/index.vue` | 提现审核 | 审核渠道方提现申请 | jst_audit_status | 金额格式化, 审核确认弹窗, 状态标签 |
| 8 | `jst/form-template/index.vue` | 表单模板 | 管理报名动态表单模板 | jst_audit_status | schema 字段数预览, 状态标签 |

### 3.2 用户渠道组（1 页）

| # | 文件 | Hero 标题 | Hero 描述 | 改造要点 |
|---|---|---|---|---|
| 9 | `jst/binding/index.vue` | 绑定管理 | 查看学生与渠道方的绑定关系 | userId→userName, channelId→channelName |

### 3.3 营销权益组（6 页）

| # | 文件 | Hero 标题 | Hero 描述 | 字典 | 改造要点 |
|---|---|---|---|---|---|
| 10 | `jst/marketing/jst_coupon_issue_batch/index.vue` | 优惠券批次 | 管理优惠券发放批次 | jst_coupon_type | target_json 可视化, 状态 |
| 11 | `jst/marketing/jst_user_coupon/index.vue` | 用户优惠券 | 查看已发放给用户的优惠券 | jst_coupon_type | 状态(未用/已用/过期), 金额, 有效期 |
| 12 | `jst/marketing/jst_user_rights/index.vue` | 用户权益 | 查看已发放给用户的权益 | jst_rights_type | 状态, 配额/已用显示 |
| 13 | `jst/marketing/jst_rights_writeoff_record/index.vue` | 权益核销记录 | 查看权益核销明细 | jst_writeoff_mode | 核销方式翻译, 时间 |
| 14 | `jst/coupon/issued.vue` | 已发优惠券 | 查看所有已发放优惠券 | jst_coupon_type | 状态标签, 金额, 有效期 |
| 15 | `jst/rights/issued.vue` | 已发权益 | 查看所有已发放权益 | jst_rights_type | 状态, 有效期 |

### 3.4 看板验证（1 页）

| # | 文件 | 改造要点 |
|---|---|---|
| 16 | `jst/dashboard/index.vue` | 确认可正常访问, dict-tag 全覆盖 |

### 3.5 编号规则（1 页）

| # | 文件 | Hero 标题 | 改造要点 |
|---|---|---|---|
| 17 | 新增 `jst/biz-no-rule/index.vue` | 编号规则 | 简单列表: 规则名/前缀/日期格式/序号位数/状态，调用 `/jst/admin/biz-no-rule/list` |

## 四、DoD

- [ ] 17 个页面全部应用 S1-S6 标准
- [ ] 所有枚举列用 dict-tag（声明 dicts 数组）
- [ ] 所有金额列有 ¥ 格式化
- [ ] 所有关联字段显示名称（非 ID）
- [ ] 所有表单弹窗关联字段改为 el-select 搜索
- [ ] 所有删除/审核操作有确认弹窗
- [ ] 所有搜索框有中文 placeholder
- [ ] `npm run build:prod` 通过
- [ ] 报告交付
