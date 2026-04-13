# QUALITY-FIX-ADMIN — 管理端质量批量修复

> 优先级：P0 | 预估：M | Agent：Web Admin Agent

---

## 一、背景

三路质量审计发现管理端 45+ 个体验问题，涵盖开发态残留、静默错误、权限缺失、工具函数分散等。本卡一次性批量修复。

## 二、修复清单

### A. 移除开发态残留（HIGH）

| # | 文件 | 问题 | 修复方式 |
|---|---|---|---|
| 1 | `views/partner/contest-edit.vue` 约 18-24 行 | `el-alert` 展示 "已切换至 /jst/partner/contest 包装路由；若仍有字段缺口，请按 ui-feedback 文档跟进" | **删除整个 el-alert 元素** |
| 2 | `views/partner/components/EnrollDetailDrawer.vue` 约 86 行 | "暂无作品材料，或当前接口尚未返回标准化附件字段。" | **改为 "暂无附件"** |
| 3 | `views/jst/coupon/issued.vue` 约 136-141 行 | `listUserCoupon()` 无 catch，API 失败时 loading 卡死 | **加 try/finally 包裹** |

### B. 静默错误改为用户提示（MEDIUM，10 处）

以下页面存在 `catch(_) { this.detail = row }` 模式——API 请求详情失败时静默吞掉错误，用户无感知。

**统一改为**：
```javascript
catch (e) {
  this.$modal.msgError('加载详情失败')
  this.detail = row
}
```

| # | 文件 |
|---|---|
| 1 | `views/jst/channel/jst_event_settlement/index.vue` |
| 2 | `views/jst/finance/jst_invoice_record/index.vue` |
| 3 | `views/jst/finance/jst_payment_pay_record/index.vue` |
| 4 | `views/jst/channel/jst_rebate_settlement/index.vue` |
| 5 | `views/jst/risk/jst_audit_log/index.vue` |
| 6 | `views/jst/risk/jst_risk_alert/index.vue` |
| 7 | `views/jst/channel/jst_rebate_ledger/index.vue` |
| 8 | `views/jst/organizer/jst_channel_auth_apply/index.vue` |
| 9 | `views/jst/course/edit.vue`（catch 中 `this.chapters = []` 改为加 toast 后再清空） |
| 10 | `views/jst/coupon/issued.vue`（同 A3，确保 catch 有提示） |

### C. 补齐权限检查（MEDIUM，5 处）

以下页面的操作按钮缺少 `v-hasPermi` 指令：

| # | 文件 | 缺失按钮 | 补充权限点 |
|---|---|---|---|
| 1 | `views/jst/mall/exchange.vue` | 确认收货 / 退货确认 | `v-hasPermi="['jst:points:mall_exchange_order:edit']"` |
| 2 | `views/jst/order/admin-order/index.vue` | 特殊退款 | `v-hasPermi="['jst:order:refund_record:add']"` |
| 3 | `views/jst/mall/index.vue` | 新增/编辑/删除/上下架 | `v-hasPermi="['jst:points:mall_goods:add/edit/remove']"` |
| 4 | `views/jst/rights/issued.vue` | 操作按钮 | `v-hasPermi="['jst:marketing:user_rights:edit']"` |
| 5 | `views/partner/enroll-manage.vue` | 批量审核 / 锁定 | `v-hasPermi="['jst:partner:enroll:audit']"` |

**注意**：权限点字符串参考对应 Controller 的 `@PreAuthorize` 注解。如果找不到精确权限点，使用该模块的 `list` 权限作为兜底。

### D. 提取公共 formatMoney 工具（MEDIUM）

**新建文件**：`src/utils/format.js`

```javascript
/**
 * 金额格式化（分转元或直接格式化）
 * @param {Number} val 金额值
 * @param {Boolean} fromCents 是否从分转元（默认 false）
 * @returns {String} 格式化后的金额字符串，如 ¥12.50
 */
export function formatMoney(val, fromCents = false) {
  if (!val && val !== 0) return '¥0.00'
  const n = fromCents ? Number(val) / 100 : Number(val)
  return '¥' + n.toFixed(2)
}
```

**替换以下页面中分散定义的 formatMoney 方法**（改为 import）：

- `views/jst/dashboard/index.vue`
- `views/jst/order/jst_order_item/index.vue`
- `views/jst/channel/jst_rebate_ledger/index.vue`
- `views/jst/channel/jst_rebate_settlement/index.vue`
- `views/jst/channel/jst_event_settlement/index.vue`
- `views/jst/finance/jst_payment_pay_record/index.vue`
- `views/jst/points/jst_points_ledger/index.vue`
- 其他包含 `formatMoney` 方法定义的页面

**替换方式**：
```javascript
// 旧：methods 中定义
formatMoney(val) { ... }

// 新：顶部 import + methods 中调用
import { formatMoney } from '@/utils/format'
// methods 中删除 formatMoney 定义，template 中引用保持不变
// 在 methods 中保留一个代理：
formatMoney(val) { return formatMoney(val) }
```

或者更简洁地将 formatMoney 挂到 Vue.prototype（在 main.js 中）：
```javascript
import { formatMoney } from '@/utils/format'
Vue.prototype.$formatMoney = formatMoney
```
然后 template 中改为 `{{ $formatMoney(xxx) }}`。**选择哪种方式由你决定，优先改动量最小的。**

### E. 内联样式收敛（LOW，重点文件）

只处理以下增强页中的集中内联样式（不追求全量）：

| 模式 | 出现频次 | 改为 |
|---|---|---|
| `style="width:100%"` | ~20 处 | 添加 CSS class `.full-width { width: 100%; }` |
| `style="color:#2f6fec"` | ~6 处 | 添加 CSS class `.text-brand { color: #2f6fec; }` |
| `style="width:190px"` | ~3 处 | 添加 CSS class `.date-picker-width { width: 190px; }` |

在 `src/assets/styles/ruoyi.scss` 末尾追加公共 class，然后替换内联 style。

## 三、规范

- 不改后端 Java 代码
- 不改小程序（jst-uniapp）
- `npm run build:prod` 必须通过
- 改动后 grep 确认零 "ui-feedback"/"包装路由"/"联调" 残留

## 四、DoD

- [ ] 3 处开发态残留删除
- [ ] 10 处静默 catch 改为 toast 提示
- [ ] 5 处权限检查补齐
- [ ] formatMoney 工具提取 + 各页面统一引用
- [ ] 重点内联样式收敛
- [ ] `npm run build:prod` 通过
- [ ] 报告交付
