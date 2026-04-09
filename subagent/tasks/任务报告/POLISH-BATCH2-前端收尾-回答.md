# 任务报告 - POLISH-BATCH2 前端收尾

## 状态总览

| 项 | 状态 | 说明 |
|---|---|---|
| A · uqrcode 接入 | 🟡 partial · 已 wire + 占位渲染 | HBuilderX 工程无 npm/CDN 访问；按任务卡方案 C 新建 `utils/qrcode.js` 自写极简渲染器（21×21 真 QR 定位方格 + 按 hash 填充），兼容 uqrcode API；detail.vue `onLoad → $nextTick → renderQrCodes()` 已接入。**占位图形不可被扫码器解码**，生产前必须按文件头注释替换为真实 uqrcodejs IIFE。调用代码无需改动 |
| B · enroll coupon select 接入 | ✅ done（落点调整） | 任务卡原指定 `event/enroll.vue`，该页面只做审核提交无订单创建；实际订单创建在 `pages-sub/my/enroll-detail.vue` 的 `handleAction('pay')`，故把接入点放在此。增加"选择优惠券"行 + `goPickCoupon` + `onShow` 回传 + `createOrder` 入参 `couponId`；`coupon/select.vue` 同步写入 `cs_selected_coupon_net` 便于调用页显示实付 |
| C · WX-C4 9 字段联调 | ✅ done | 见下方 §C 联调表，9 条全部按后端真实 VO 改齐 |
| D · mall/list defaultCover 空串 | ✅ done | 改为 `v-if=item.coverImage` + `v-else` 占位 view "暂无图片"；`defaultCover` state 删除 |
| E · withdraw-apply 余额字段 | ✅ done | 顶部"可提现余额"新增 `loadSummary()` 调 `/rebate/summary`，`summaryWithdrawable` state 回显；`totalWithdrawable` computed 改为直接返回 `summaryWithdrawable`。`expectedAmount` 仍为勾选项求和 |
| F · my-list writeoffProgress | ✅ done（隐藏） | grep `AppointmentListVO` 确认后端无 `writeoffProgress/writeoffDoneCount/writeoffTotalCount` 字段；按任务卡"没有则隐藏"指示，删除核销进度行，新增 `teamName` 展示行作为替代信息；注释标注"待后端补字段" |
| G · my/index grid 分组折叠 | ✅ done | "我的服务" 拆分为常用 6 项固定展示 + 6 项"更多 ▼"折叠区；新增 `moreExpanded` state + 切换按钮；核销工作台/渠道工作台区块保持独立不受影响 |
| H · exchange-detail QR 渲染 | ⚪ N/A | grep 确认 `mall/exchange-detail.vue` 未展示任何 `qrCode/兑换码/writeoffCode` 字段，当前设计无 QR 显示位，跳过 |

## §C · WX-C4 9 字段联调表

所有字段均 grep 后端 VO 确认后，在前端调整（不改后端）。

| # | 页面 | Before（WX-C4 假设） | After（后端真实 VO） | 决策 |
|---|---|---|---|---|
| 1 | points/center summary | `summary.currentLevel.levelId/levelName/growthValueRequired` | `PointsCenterSummaryVO + LevelVO{levelId,levelCode,levelName,levelNo,growthThreshold,icon,applicableRole,unlocked}` | 等级展示优先用 `levelNo`，锁定检测增加 `unlocked === false` 判断，`growthValueRequired` 全部替换为 `growthThreshold` |
| 2 | points/center levels description | `lv.description\|benefitText` | LevelVO 无描述字段，仅 `growthThreshold/icon` | 改为展示"成长值门槛 xxx"，前缀 icon |
| 3 | points/center tasks | `t.taskId\|code, name\|title, description, pointsReward\|reward` | `PointsTaskVO{taskCode,taskName,taskDesc,icon,rewardText,finished,actionPath}` | 字段全改；奖励列展示 `finished ? '已完成' : rewardText` |
| 4 | points/detail ledger | `item.sourceAction\|action\|description, changeAmount, balanceAfter` | `PointsLedgerVO{ledgerId,changeType,sourceType,sourceRefId,pointsChange,balanceAfter,remark}` + `GrowthLedgerVO{...growthChange,levelBefore,levelAfter}` | 名称列改为 `remark\|sourceType`；`changeValue()` 方法按 activeTab 区分 `pointsChange` vs `growthChange` |
| 5 | coupon center | `item.couponId, scopeText, thresholdText` | `MyCouponVO{userCouponId,couponTemplateId,couponName,couponType,faceValue,discountRate,thresholdAmount,status,expired}` | key 改 `userCouponId`；门槛直接用"满 X 可用"拼接；适用范围行删除 |
| 6 | coupon claimable | `item.templateId, scopeText` | `ClaimableCouponVO{couponTemplateId,applicableContestIds,applicableRole,validDays,stackRule}` | key 改 `couponTemplateId`；claim 请求改用 `couponTemplateId`；scopeLabel computed 按 `applicableContestIds.length` 区分"指定 N 个赛事"/"全场通用" |
| 7 | coupon select | `candidates[], recommendedCouponId, usable, unusableReason, netPayAmount` | `CouponSelectResVO{bestCouponId,bestDiscount,netPayAmount,alternatives:[{couponId,couponTemplateId,couponName,discountAmount,netPayAmount,applicable,reason}]}` | `candidates = res.alternatives`；`selectedId = res.bestCouponId`；模板字段 `usable→applicable` / `unusableReason→reason`；展示改为 `-¥discountAmount` + 实付 |
| 8 | rights center + detail | `remainingAmount/remainingCount` 双字段 | `MyRightsVO/RightsDetailVO{quotaMode:'amount'\|'count',quotaValue,remainQuota,writeoffMode,expired}` | 新增 `quotaText()` 方法按 `quotaMode` 展示"剩余额度¥X/总¥Y"或"剩余次数 X/Y"；`立即使用`按钮加 `!item.expired` 防御 |
| 9 | rights detail writeoffHistory | `h.remark, writeoffAmount, writeoffCount` | `RightsDetailVO.WriteoffRecordVO{recordId,writeoffNo,useAmount,applyRemark,status,auditRemark,writeoffTime}` | 标题改 `applyRemark \|\| '核销单 N'`；副标题 `¥useAmount · status · writeoffTime` |
| 10 | campaign | `campaign.coupons[].templateId` | `CampaignVO{campaignId,title,bannerUrl,description,startTime,endTime,countdownSeconds,linkedCouponTemplateIds,linkedCoupons:ClaimableCouponVO[]}` | `coupons → linkedCoupons`；item key 改 `couponTemplateId` |
| ★ | rights writeoff apply | 两模式分 `writeoffAmount/writeoffCount` | `RightsWriteoffApplyDTO{writeoffAmount,remark}` 只支持金额字段 | 简化为单一 `writeoffAmount` 输入（`type="digit"`），数量模式复用同字段由后端按 `quotaMode` 解释；删除 stepper/`useAmountMode/inc/dec` |
| ★ | api/coupon.js claim | `{ templateId }` 作为 body | 后端 `@RequestParam("templateId")` | 改为 `?templateId=${id}` 拼接 query |

合并计 12 处字段 / 接口契约修正（9 条主项 + 2 条额外发现 + 1 条 claim 参数位）。

## §A · uqrcode 自写渲染器细节

**文件**：`jst-uniapp/utils/qrcode.js`（新增，约 100 行）

```js
import UQRCode from '@/utils/qrcode'
// 调用 API 与真实 uqrcodejs 完全一致:
UQRCode.make({ canvasId: 'qr_0', text: '...', size: 360, context: this })
```

**实现要点**：
- `hash(text)` djb2 哈希 → 生成确定性种子
- `buildMatrix(text)` 输出 21×21 位矩阵，包含三个真 finder pattern（左上/右上/左下 7×7 方框）
- 数据区用 LCG `seed = seed * 1103515245 + 12345` 伪随机填充
- `make({...})` 用 `uni.createCanvasContext` 绘制白底 + 黑格 + `ctx.draw()`

**诚实声明**：矩阵不经 Reed-Solomon 编码、不符合 ISO/IEC 18004，**无法被扫码器解码**；但视觉上有真 QR 的定位方格和点阵密度，在联调/演示期可充当占位。

**生产替换路径**（文件头注释已完整写明）：
- (A) HBuilderX 插件市场装 uqrcode，main.js 全局注入
- (B) 下载 `uqrcodejs/dist/uqrcode.iife.min.js` 到 `static/lib/uqrcode.js`
- (C) 移植 davidshimjs/qrcodejs（~300 行）

替换后 `utils/qrcode.js` 一个文件删除即可，调用代码零改动。

**detail.vue 接入**：
```js
async load() {
  try { this.detail = (await getAppointmentDetail(this.appointmentId)) || {} } catch (e) {}
  this.$nextTick(() => setTimeout(() => this.renderQrCodes(), 60))
},
renderQrCodes() {
  for (let i = 0; i < this.writeoffItems.length; i++) {
    const text = this.writeoffItems[i].qrCode || ''
    if (!text) continue
    try { UQRCode.make({ canvasId: 'qr_' + i, text, size: 360, context: this }) } catch (e) {}
  }
}
```

## §B · Coupon Select 接入点调整

**原任务卡定位**：`jst-uniapp/pages-sub/event/enroll.vue` — 该路径不存在。实际文件是 `pages-sub/contest/enroll.vue`，且该页**只做报名材料提交**，不创建订单。

**实际订单创建点**：`pages-sub/my/enroll-detail.vue:244 handleAction()` 审核通过后点"去支付"→ `createOrder({enrollId, pointsToUse, payMethod})`。

**修改内容**：
1. template 在 actionButton 上方新增"优惠券 / 选择优惠券"行（仅 `action === 'pay'` 时展示）
2. data 新增 `selectedCouponId / couponNetPay`
3. 新增 `goPickCoupon()` → `navigateTo('/pages-sub/coupon/select?contestId=...&orderAmount=0')`
4. `onShow()` 读 storage `cs_selected_coupon_id / cs_selected_coupon_net` 并清理
5. `handleAction()` 的 `createOrder` 入参追加 `couponId: this.selectedCouponId || undefined`
6. `coupon/select.vue` `onConfirm()` 同步写入 `cs_selected_coupon_net = 选中项.netPayAmount`

**遗留**：
- `orderAmount=0` 是兜底值。enroll-detail 当前未从 contest 接口取 price，后端 `/coupon/select` 需容忍 0 金额或由 contestId 兜算。联调时若 `orderAmount` 必须大于 0，需改为先调 `/contest/{id}` 取 price 再跳选券。

## §D~G 变更快照

- **D**：`pages-sub/mall/list.vue` 模板条件渲染 + SCSS 追加 `.ml-card__img--placeholder` 居中灰字；移除 `defaultCover` state
- **E**：`pages-sub/channel/withdraw-apply.vue` 追加 `loadSummary()` + `summaryWithdrawable` state；`totalWithdrawable` computed 指向 summary；`expectedAmount` computed 保持求和逻辑
- **F**：`pages-sub/appointment/my-list.vue` 删除核销进度行，新增 `teamName` 可选展示行；注释说明
- **G**：`pages/my/index.vue` 12 项拆 6+6，`moreExpanded` 切换；CSS 新增 `.my-page__grid--more` 和 `.my-page__more-toggle`

## 修改/新增文件汇总（14 个）

**新增**：
- `jst-uniapp/utils/qrcode.js`

**修改**：
- `jst-uniapp/api/coupon.js`（claim 改 query param）
- `jst-uniapp/pages-sub/appointment/detail.vue`（uqrcode wire）
- `jst-uniapp/pages-sub/appointment/my-list.vue`（F）
- `jst-uniapp/pages-sub/my/enroll-detail.vue`（B）
- `jst-uniapp/pages-sub/coupon/select.vue`（C + B 联动）
- `jst-uniapp/pages-sub/coupon/center.vue`（C）
- `jst-uniapp/pages-sub/coupon/claimable.vue`（C）
- `jst-uniapp/pages-sub/points/center.vue`（C）
- `jst-uniapp/pages-sub/points/detail.vue`（C）
- `jst-uniapp/pages-sub/rights/center.vue`（C）
- `jst-uniapp/pages-sub/rights/detail.vue`（C）
- `jst-uniapp/pages-sub/rights/writeoff-apply.vue`（C）
- `jst-uniapp/pages-sub/marketing/campaign.vue`（C）
- `jst-uniapp/pages-sub/mall/list.vue`（D）
- `jst-uniapp/pages-sub/channel/withdraw-apply.vue`（E）
- `jst-uniapp/pages/my/index.vue`（G）

## DoD 对照

- [x] A~H 8 条全部处理（A partial、B/C/D/E/F/G done、H N/A）
- [x] WX-C4 9 字段联调表齐全（实际 12 处修正）
- [x] 未改后端
- [x] 未改 `pages-sub/mall/detail.vue`（F-USER-ADDRESS 占位）
- [x] 未引入 npm 依赖（自写静态 js）
- [ ] commit 留给主 Agent 统一打包

## 遗留与后续建议

1. **uqrcode 真实替换**：最高优先级。当前占位图扫不了，任何依赖二维码核销的 E2E 测试会失败。建议 HBuilderX 内装 uqrcode 插件，或下载 IIFE 到 `static/lib/`
2. **enroll-detail orderAmount**：B 节传 0 兜底。联调前确认 `/coupon/select` 是否接受 0 金额
3. **rights writeoff 次数模式**：后端 `RightsWriteoffApplyDTO` 只有 `writeoffAmount` 一个字段。次数模式复用该字段是否合适待服务端验证；若后端按 count 解释失败，需要增加 `RightsWriteoffApplyDTO.writeoffCount`（后端小改）
4. **AppointmentListVO 核销进度字段**：F 已隐藏，建议下轮后端任务卡补 `writeoffDoneCount / writeoffTotalCount`
5. **coupon claim query vs body**：当前直接在 URL 拼接 templateId，未用 `request.js` 的 data channel；若未来拦截器对 URL 做签名，需要注意
