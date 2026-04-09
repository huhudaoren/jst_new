# 任务报告 - WX-C4 积分中心 + 优惠券 + 权益 + 营销前端

## A. 任务前自检

1. **原型**：points-center / points-detail（积分），coupon-center / coupon-select（券），rights-center / rights-detail / writeoff-apply（权益），campaign-topic（营销）。
2. **接口**（并行卡契约，后端未合并）：
   - 积分：`/jst/wx/points/center/summary|levels|/ledger|/growth/ledger|/tasks`
   - 券：`/jst/wx/coupon/template/claimable|/claim|/my|/select`
   - 权益：`/jst/wx/rights/my|/{id}|/{id}/apply-writeoff`
   - 营销：`/jst/wx/campaign/list|/{id}`
   - 商城售后（mall.js 扩展）：`/jst/wx/mall/aftersale/apply|/my|/{id}`
3. **复用**：`api/request.js`、`useUserStore`、现有列表 + tab + 时间轴样板
4. **新建文件**：4 个 api + 9 个页面（见 B 节）
5. **数据流**：各 center 页 onShow 拉接口；select.vue 通过 `storage['cs_selected_coupon_id']` 回传调用方；claim 乐观更新 `_claiming/_claimed` 本地状态
6. **双视角**：纯学生视角，无角色切换
7. **样板**：rebate.vue（hero+tab+list+悬浮）、mall/list.vue（grid+tab+积分 banner）、order-detail.vue（时间轴）、my/index.vue（grid tile）
8. **PNG 关键色**：
   - 积分紫金渐变 `#4A0072→#7B1FA2→#F5A623`；等级权益 tag 背景 `#F3E8FF`
   - 券卡：满减 `#F4511E`、折扣 `#7C3AED`、直减 `#F5A623`
   - 权益：`#1B5E20→#2E7D32` 核销绿
   - 进度条高 16rpx，卡片圆角 `--jst-radius-md`（24rpx）

## B. 交付物清单

### 新增 API（4 个）+ mall.js 扩展
- `api/points.js` — summary / levels / ledger / growth / tasks
- `api/coupon.js` — claimable / claim / my / select
- `api/rights.js` — my / detail / apply-writeoff
- `api/marketing.js` — campaign list / detail
- `api/mall.js` **扩展** — applyAftersale / getMyAftersales / getAftersaleDetail

### 新增页面（9 个 = 7 核心 + 2 附属）
- `pages-sub/points/center.vue` ⭐ — 紫金 hero + 等级进度条 + 3 统计 + 4 tile + 等级权益列表（当前高亮/锁定灰显）+ 赚积分任务
- `pages-sub/points/detail.vue` — 积分/成长值双 tab 流水
- `pages-sub/coupon/center.vue` — 3 tab（未使用含角标/已用/已过期）+ 3 色券卡 + 顶部"去领券"
- `pages-sub/coupon/claimable.vue` — 领券中心
- `pages-sub/coupon/select.vue` — 订单选券（storage 回传 `cs_selected_coupon_id`；enroll 接入留 POLISH）
- `pages-sub/rights/center.vue` — 3 KPI + 4 tab + 权益卡 + 立即使用
- `pages-sub/rights/detail.vue` — 权益详情 + 核销规则 + 核销历史时间轴
- `pages-sub/rights/writeoff-apply.vue` — 按剩余额度/次数二选一表单 + 备注
- `pages-sub/marketing/campaign.vue` — banner + 倒计时 + 可领券列表

### 修改文件
- `pages.json` — 新增 4 个分包（points/coupon/rights/marketing）共注册 9 个页面
- `pages/my/index.vue` — 我的服务 grid 追加 3 个 tile：积分中心 / 我的优惠券 / 我的权益；methods 对应新增 `navigatePointsCenter/navigateCouponCenter/navigateRightsCenter`

## C. 契约偏差与假设（后端并行未合并）

本任务**基于并行后端卡契约**开发，以下字段为假设，合并后需联调确认：

| 页面 | 假设字段 | 说明 |
|---|---|---|
| points/center | `summary.currentLevel.{levelId,levelName,growthValueRequired}`、`nextLevel.{...}`、`pointsToNextLevel` | 进度条百分比基于 `growthValue - cur / next - cur` 计算；若后端直接返百分比可简化 |
| points/center | `levels[].{levelId,levelName,growthValueRequired,description|benefitText}` | 兼容 description 或 benefitText |
| points/center | `tasks[].{taskId|code,name|title,description,icon,pointsReward|reward}` | 字段名宽容多种命名 |
| points/detail | `ledger[].{sourceAction|action|description,changeAmount,balanceAfter,createTime}` | `changeAmount` 直接用正负号判定颜色 |
| coupon | `couponType: 'full_reduction'|'discount'|'direct_reduction'`，`faceValue`、`discountRate`、`thresholdAmount` | 券卡颜色按 type class 切换 |
| coupon/select | 返回 `{candidates:[{couponId,...,usable,unusableReason,netPayAmount}], recommendedCouponId}` | 若返回结构不同需调整 `load()` 取数路径 |
| rights/center | `stats:{available,applying,used}`（可选），列表 `{userRightsId,rightsName,status,remainingAmount|remainingCount,validEnd}` | 统计数后端若不提供，前端仅展示 tab |
| rights/detail | 含 `writeoffHistory:[{recordId,remark,writeoffAmount|writeoffCount,createTime}]` | |
| campaign | `{bannerUrl,title,description,endTime,coupons:[...]}` | 相关赛事段本期占位 |

## D. 静态核对（预期行为）

1. ✓ 我的页新增 3 tile 点击分别跳 points/coupon/rights center
2. ✓ 积分中心：等级 hero 进度条按 growthValue 计算；等级列表当前高亮、未达 `pc-level--locked` 灰显并显示差值
3. ✓ 积分明细：双 tab 切换调用不同接口，分页 + onReachBottom
4. ✓ 券中心 3 tab，unusedCount 角标从 total 读取
5. ✓ 领券：乐观更新 `_claimed` 防重复点击
6. ✓ 选券：`selectCoupon` 返回 recommendedCouponId 自动选中；选完通过 storage 回传
7. ✓ 权益中心 4 tab + KPI + 立即使用跳 writeoff-apply
8. ✓ 权益申请：按 `remainingAmount != null` 切换"金额模式 / 次数模式"，前端校验不越界
9. ✓ 活动页倒计时从 `endTime` 计算（仅 onLoad 一次，未做秒级刷新）

## E. 遗留 TODO

1. **coupon/select 无调用方**：contest enroll 页尚未接入本选券页，任务卡明确留 P-POLISH
2. **tasks 任务入口**：center 页 tasks 卡片未接入"去完成"跳转（后端未定义 actionUrl 字段）
3. **campaign 相关赛事段**：原型有"参与赛事列表"，本期因后端未定义字段占位
4. **倒计时不实时刷新**：为避免 setInterval 泄漏风险，仅在进入页面时计算一次；若需要实时可后续加 onUnload 清理
5. **mall aftersale UI 未做**：本任务仅扩展 api，UI 入口（商城兑换详情页追加"申请售后"按钮）未做，需主 Agent 决定放在 POLISH 还是单独卡
6. **后端合并后必联调**：C 节 9 个字段假设处都需要确认

## F. 我做了任务卡之外的什么

- 选券页 storage key 用 `cs_selected_coupon_id`，契约需要调用方知晓；已在文件头注释说明
- 积分中心 `isCurrentLevel/isLocked/lockedGap` 3 个辅助方法，为了让等级列表支持任意数量 level
- 券卡容器在 disabled 状态把左侧渐变区置灰，避免用户误点

## G. 自检确认

- [x] 无页面内 mock 数据
- [x] 所有 API 经 `api/request.js` 封装
- [x] 未引入新依赖
- [x] 未改后端 / DDL / 架构文档 / 已有 mall 页面
- [x] 单位 rpx；颜色绝大多数走 `--jst-color-*` token，仅积分紫金 / 券三色 / 权益绿为原型专属品牌色直写
- [x] 每个 .vue 顶部中文注释（页面说明 + 原型 + 接口）
- [x] pages.json 注册 4 分包 9 页面
- [x] my/index.vue 3 个 tile
- [x] commit 留给主 Agent
