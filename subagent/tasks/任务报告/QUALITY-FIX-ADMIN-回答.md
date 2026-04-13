# QUALITY-FIX-ADMIN 执行报告

- 执行日期：2026-04-13
- 执行范围：RuoYi-Vue/ruoyi-ui/src
- 执行定位：质量修复（开发残留清理、错误提示补齐、权限补齐、金额工具统一、内联样式收敛）
- 约束遵循：未新增业务能力、未新增任务卡中页面不存在的业务按钮

## 1. 改动清单

### 1.1 开发残留清理
- 删除 views/partner/contest-edit.vue 顶部开发态 el-alert（包装路由/ui-feedback 文案）。
- 清理 views/partner/home.vue 中联调/测试账号类文案残留。
- views/partner/components/EnrollDetailDrawer.vue：文案“暂无作品材料...”统一为“暂无附件”。

### 1.2 静默错误改为可感知提示
- 将同类 catch (_) { this.detail = row } 模式统一改为 catch (e) 并提示失败后回退行数据。
- 命中修复页面：
  - views/jst/channel/jst_event_settlement/index.vue
  - views/jst/channel/jst_rebate_ledger/index.vue
  - views/jst/channel/jst_rebate_settlement/index.vue
  - views/jst/finance/jst_contract_record/index.vue
  - views/jst/finance/jst_invoice_record/index.vue
  - views/jst/finance/jst_payment_pay_record/index.vue
  - views/jst/organizer/jst_channel_auth_apply/index.vue
  - views/jst/risk/jst_audit_log/index.vue
  - views/jst/risk/jst_risk_alert/index.vue
  - views/jst/risk/jst_risk_case/index.vue
  - views/jst/user/jst_channel/index.vue
- 额外修复：
  - views/jst/course/edit.vue：章节 JSON 解析失败时补失败提示，并清空 chapters。
  - views/jst/coupon/issued.vue：getList 增加 catch + msgError + finally，避免 loading 卡死。

### 1.3 权限检查补齐（按 Controller 真值）
- views/jst/mall/index.vue
  - “上架/下架”补：v-hasPermi=['jst:points:mall_goods:edit']。
- views/jst/rights/issued.vue
  - “详情”补权限指令：v-hasPermi=['jst:marketing:user_rights:list']（读权限口径）。
- views/partner/enroll-manage.vue
  - 移动端“审核”补权限。
  - 页面审核相关权限统一为：v-hasRole=['jst_partner']（与 Controller hasRole('jst_partner') 一致）。
- views/jst/mall/exchange.vue
  - 保留业务动作权限：发货/完成使用 jst:points:mall:exchange:ship。
  - 补“查看”权限：jst:points:mall:exchange:list。
- views/jst/order/admin-order/index.vue
  - 保留“特批退款”原权限：jst:order:refund:special。
  - 补“查看详情/详情”权限：jst:order:query。

### 1.4 金额工具统一收口
- 新增：src/utils/format.js
  - 导出 formatMoney(val, fromCents = false)。
- 以下页面统一改为 import 工具 + 页面薄代理（模板最小改动）：
  - views/jst/channel/admin-rebate/index.vue
  - views/jst/channel/admin-settlement/index.vue
  - views/jst/channel/admin-withdraw/index.vue
  - views/jst/channel/index.vue
  - views/jst/channel/jst_event_settlement/index.vue
  - views/jst/channel/jst_rebate_ledger/index.vue
  - views/jst/channel/jst_rebate_rule/index.vue
  - views/jst/channel/jst_rebate_settlement/index.vue
  - views/jst/contest/detail.vue
  - views/jst/contest/index.vue
  - views/jst/dashboard/index.vue
  - views/jst/finance/jst_invoice_record/index.vue
  - views/jst/finance/jst_payment_pay_record/index.vue
  - views/jst/order/admin-order/index.vue
  - views/jst/order/admin-refund/index.vue
  - views/jst/order/jst_order_item/index.vue
  - views/jst/order/jst_payment_record/index.vue
  - views/partner/settlement.vue
- 语义保持：分转元页面传 fromCents=true；无货币符号页面在代理层去掉 ¥；其余默认带 ¥。

### 1.5 内联样式收敛
- 在 src/assets/styles/ruoyi.scss 追加公共类：
  - .full-width
  - .text-brand
  - .date-picker-width
- 替换 src/views 下三类指定内联样式：
  - style="width:100%"
  - style="color:#2f6fec"
  - style="width:190px"

## 2. 权限冲突反馈与最终口径

- 冲突处理原则：以当前前端实际调用接口对应后端 Controller @PreAuthorize 为唯一真值。
- 关键冲突与结论：
  - rights/issued.vue
    - 任务卡建议“edit”权限；当前页面仅详情只读。
    - 结论：详情按钮采用读权限 jst:marketing:user_rights:list。
  - partner/enroll-manage.vue
    - 任务卡建议 jst:partner:enroll:audit；后端为 hasRole('jst_partner')。
    - 结论：统一使用 v-hasRole=['jst_partner']。
  - mall/exchange.vue
    - 任务卡提到“确认收货/退货确认”；当前页面实际动作为“发货/确认完成”。
    - 结论：保留 ship 权限给业务动作，仅补“查看”读权限。
  - admin-order/index.vue
    - 任务卡提到 refund_record:add；当前页面已有“特批退款”并使用 jst:order:refund:special。
    - 结论：不改业务动作权限，仅补详情读权限 jst:order:query。

## 3. grep 前后结果（范围固定 RuoYi-Vue/ruoyi-ui/src）

说明：本机 rg.exe 执行被拒绝（Access is denied），改用 PowerShell Get-ChildItem + Select-String 完成同范围扫描。

### 3.1 关键词残留
- 模式：联调|包装路由|ui-feedback|测试账号
- 修复前：3
- 修复后：0

### 3.2 静默详情 catch
- 模式：catch (_) { this.detail = row }
- 修复前：11
- 修复后：0

### 3.3 指定内联样式
- 模式：style="width:100%"、style="color:#2f6fec"、style="width:190px"
- 修复前：19
- 修复后：0

## 4. 构建结果

- 执行目录：D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui
- 命令：npm run build:prod
- 结果：通过（Build complete. The dist directory is ready to be deployed.）
- 备注：存在 webpack 产物体积 warning，不影响本卡 DoD。

## 5. 未扩展项说明（卡片与现状差异）

- 未新增页面中不存在的业务按钮：
  - mall/exchange.vue 未新增“退货确认”按钮（页面无该业务动作与接口调用）。
  - admin-order/index.vue 未新增其他退款按钮，仅沿用既有“特批退款”。
  - mall/index.vue 未新增不存在按钮，仅为现有上架/下架补权限。
  - partner/enroll-manage.vue 未新增“锁定”按钮（页面现状无该动作）。
- 本轮仅做质量修复，不扩展后端与业务流。

## 6. DoD 对照

- [x] 开发态残留清理
- [x] 静默错误提示补齐
- [x] 权限检查补齐（按 Controller 真值）
- [x] formatMoney 工具提取与页面统一
- [x] 指定内联样式收敛
- [x] npm run build:prod 通过
- [x] 报告交付