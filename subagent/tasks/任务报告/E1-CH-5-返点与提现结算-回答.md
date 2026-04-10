# 任务报告 - E1-CH-5 返点中心 + 提现结算合并

## 交付物

### 1. 重构 `pages-sub/channel/rebate.vue`

- **Hero 4 格**：可提现余额（金色大号）/ 审核中 / 累计打款 / 退款回退，来源 `getRebateSummary`
- **7 状态 Tab**（恢复"全部"，PRD V4.0 原型 channel-rebate.html 列出全部在内）：全部 / 待结算 / 可提现 / 审核中 / 已打款 / 已驳回 / 已回退
- **明细卡片**：赛事名 + 订单号 + 金额 + 状态 badge + 计提时间，分页 onReachBottom
- **底部固定栏**：左"可提现金额"、右"申请提现"按钮 → 跳 `settlement`（原跳 withdraw-apply）
- 右上角"提现单"胶囊按钮也改跳 `settlement`

### 2. 新建 `pages-sub/channel/settlement.vue`

合并 withdraw-list + withdraw-apply 为**单页多视图**：

- **7 状态 Tab**：全部 / 待提交 / 审核中 / 已撤回 / 已驳回 / 待打款 / 已打款
- **"新建提现"折叠入口**：点击展开/收起申请表单
  - 勾选待提现明细（全选默认）
  - 收款账户 3 字段
  - 发票信息（选填）+ 灰标"合同/发票功能 F-2 批次"
  - 提交按钮 → `applyWithdraw`
- **结算单卡片列表**：编号 / 金额 / 状态 badge / 时间，点击跳 `withdraw-detail`
- 分页 + 下拉刷新 + onReachBottom

### 3. 页面清理
- `withdraw-list.vue` / `withdraw-apply.vue` **保留**不删（保守策略），但所有入口（home.vue tile + rebate.vue 按钮）已改跳 settlement
- `withdraw-detail.vue` 保留作为详情子页

### 4. 路由 + 入口
- `pages.json` 注册 settlement 页
- `channel/home.vue` 的"提现记录" tile + "查看全部"链接改跳 settlement

## 与 POLISH-BATCH1 C 项的冲突说明

POLISH-BATCH1 C 移除了 rebate.vue 的"全部" tab（6 tab），本任务 CH-5 依据 PRD V4.0 恢复"全部"（7 tab，`activeStatus: ''`），符合原型 `channel-rebate.html` 实际列出 "全部" 为首 tab。以本任务为准。

## DoD
- [x] rebate.vue 4 Hero + 7 Tab + 底部跳 settlement
- [x] settlement.vue 7 Tab + 卡片列表 + 展开/收起申请表单
- [x] 合同/发票 disable 灰标
- [x] 状态 badge 6+ 种颜色
- [x] 未改后端
- [x] 未删 withdraw-detail.vue
- [x] pages.json 注册
- [x] home.vue 入口更新

## 遗留
1. 旧 `withdraw-list.vue` / `withdraw-apply.vue` 仍占用分包空间，后续清理或 deprecate
2. settlement.vue 申请表单的"合同/发票子入口"为灰标占位，F-2 批次再做
3. 结算单展开详情（驳回原因/打款凭证）暂由点击跳 withdraw-detail 实现，未做卡片内展开
