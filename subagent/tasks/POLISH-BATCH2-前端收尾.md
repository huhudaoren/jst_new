# 任务卡 POLISH-BATCH2 - 前端收尾（批 4 遗留 + 联调）

## 阶段 / 端
阶段 D / **jst-uniapp**

## 背景
批 4 合并完成，以下 8 条遗留本卡一次收敛，完成 MVP 可上线前的最后一公里前端打磨。**纯前端**，不改后端，与 DEBT-3 / F-USER-ADDRESS 并行。

## 遗留清单

### A. uqrcode 接入 ⭐（P-POLISH-BATCH1 B 项阻塞延续）
**文件**：`jst-uniapp/pages-sub/appointment/detail.vue`

原型文档要求真实二维码。前卡 blocked 在无 package.json。本卡采用**小程序原生渲染路径**：
1. 选方案之一（子 Agent 决定）：
   - (A) 引入 HBuilderX 插件市场的 `uqrcode` 组件（需用户手动装，子 Agent 确认后告知）
   - (B) 从 `https://cdn.jsdelivr.net/npm/uqrcodejs` 下载 `uqrcode.iife.min.js` 到 `jst-uniapp/static/lib/uqrcode.js`，页面内 `const UQRCode = require('@/static/lib/uqrcode.js')`（仅 vendor 静态资源，不算依赖）
   - (C) 写一个极简 JS 二维码生成器（~200 行），参考 https://github.com/davidshimjs/qrcodejs，放 `jst-uniapp/utils/qrcode.js`
2. **推荐 (B)**：零构建改动，兼容性最好
3. 在 `detail.vue` onReady / swiper change 时遍历 `writeoffItems` 对每个 canvas `qr_${idx}` 调 `UQRCode.make({canvasId, text: item.qrCode, size: 250})`
4. 同样处理 `pages-sub/mall/exchange-detail.vue` 如果虚拟商品有兑换码字段需要渲染

### B. coupon/select 在 enroll.vue 接入 ⭐
**文件**：`jst-uniapp/pages-sub/event/enroll.vue`（或现有报名页路径，先 grep 确认）

1. 报名页"优惠券"选项点击 → `uni.navigateTo('/pages-sub/coupon/select?orderAmount=xxx&contestId=xxx')`
2. 返回时 onShow 读 `uni.getStorageSync('cs_selected_coupon_id')` → 更新本地 couponId + 重新计算 netPayAmount → `uni.removeStorageSync('cs_selected_coupon_id')`
3. 提交订单入参带 couponId

### C. WX-C4 9 个字段联调 ⭐⭐
对照 `subagent/tasks/任务报告/WX-C4-积分权益营销前端-回答.md` §C 契约偏差表，对每一条做：
1. `grep` 后端 VO 找真实字段名
2. 不匹配则修前端（不改后端）
3. 在本卡报告 §C 列出 9 条的"before / after / 决策"

具体 9 条：
- `points/center` summary 的 level 字段名
- `points/center` levels description vs benefitText
- `points/center` tasks 命名
- `points/detail` ledger 字段
- coupon couponType 值枚举
- `coupon/select` 返回结构 candidates / recommendedCouponId
- `rights/center` stats / 列表字段
- `rights/detail` writeoffHistory 字段
- campaign bannerUrl / coupons

### D. H1 · mall/list.vue defaultCover 空串
**文件**：`pages-sub/mall/list.vue:32,73`

方案：条件渲染
```vue
<image v-if="item.coverImage" :src="item.coverImage" />
<view v-else class="mall-list__cover-placeholder">暂无图片</view>
```
不要新建 static 图片。

### E. H2 · withdraw-apply 可提现余额取错字段
**文件**：`pages-sub/channel/withdraw-apply.vue:9,97`

改：顶部"可提现余额"直接从 `rebate/summary` 的 `withdrawableAmount` 字段拿（onLoad 调一次 `/rebate/summary`），不要从 ledgerList 求和。`expectedAmount` 仍是前端勾选项求和，用于 apply 入参。

### F. H3 · my-list writeoffProgress 字段
**文件**：`pages-sub/appointment/my-list.vue:28`

`grep` 后端 `AppointmentListVO` 查实际字段。若后端已有 `writeoffDoneCount/writeoffTotalCount` 则用；若没有则前端暂时**隐藏核销进度区**，在报告记录 "待后端补字段"，不展示占位符。

### G. my/index grid 分组折叠
**文件**：`pages/my/index.vue`

批 4 后"我的服务" grid 有 12+ 项密度过高。改造：
1. 拆成两组：**常用 6 项**（订单/退款/报名/绑定/课程/资料）+ **更多 ▼ 折叠**（档案/积分中心/优惠券/权益/成绩/证书 等）
2. 点"更多"展开 → 下半部分列出剩余 tile
3. 保留 "核销工作台" 独立区块不动

### H. uqrcode 同步接入 exchange-detail（若需要）
扫 `mall/exchange-detail.vue` 是否展示了虚拟商品的兑换码字段，若有则按 A 方案同等渲染；若不展示跳过并记录。

## 交付物
- 修改上述 8 个文件
- 可能新增：`jst-uniapp/static/lib/uqrcode.js`（vendor）或 `jst-uniapp/utils/qrcode.js`（自写）
- 任务报告 `POLISH-BATCH2-前端收尾-回答.md` 列出 A~H 每条的状态

## DoD
- [ ] A~H 8 条全部处理（done 或 blocked 说明）
- [ ] WX-C4 9 字段联调表（before/after/决策）齐全
- [ ] commit: `polish(wx): POLISH-BATCH2 前端收尾`

## 不许做
- ❌ 不许改后端
- ❌ 不许改 pages-sub/mall/detail.vue（F-USER-ADDRESS 正在改）
- ❌ 不许引入 npm 依赖（直接 require 静态 js 不算依赖）
- ❌ 不许跳过 C 节联调表

## 依赖：批 4 ✅
## 优先级：高

---
派发时间：2026-04-09
