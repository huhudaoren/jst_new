# 任务卡 P-POLISH-BATCH1 - 前端遗留清理第 1 批

## 阶段 / 端
前端 polish / **jst-uniapp**

## 业务背景

WX-C2 / WX-C3 批 2~3 交付后累积了 8 条遗留，本卡一次性收敛。此任务**纯前端**，不改后端，不阻塞批 4 的三张功能卡，可并行派发。

## 遗留清单

### A. WX-C3 scan.vue 缺三类核销 + 今日统计 ⭐
**文件**：`jst-uniapp/pages-sub/appointment/scan.vue`

对照原型 `小程序原型图/checkin-scan.html` 补齐：
- **核销类型 Chip（3 个）**：🚪到场入场 / 🎁礼品领取 / 🏆颁奖典礼。切换时 `scanType` 状态变化，提交给 `/writeoff/scan` 入参 `{qrCode, scanType}`
- **活动/场次切换底部 Sheet**：点顶部"当前活动场次信息栏"弹出 Sheet，可切换；本期列表数据源先 mock 3 条（因后端无 active sessions 接口），切换后重置今日统计
- **今日统计 4 格**：今日核销总数 / 入场 / 礼品 / 颁奖。数据从本地 state 累加（扫一次 +1），页面刷新清零；不依赖后端统计接口
- **最近核销记录列表**：本地 state 最近 10 条，非从 `/writeoff/records` 拉（那是另一页）
- 扫码结果三态：✅ 成功（绿卡）/ ❌ 失败（红卡）/ ⚠️ 已使用（橙卡），对应后端不同 errorCode 展示不同文案
- 深色沉浸式 UI：背景 `#0D1B2A`

### B. WX-C3 detail.vue 二维码接入 uqrcode ⭐
**文件**：`jst-uniapp/pages-sub/appointment/detail.vue` + `pages-sub/mall/exchange-detail.vue`（若后者有二维码显示位）

原型文档明确：**接入 qrcode.js 生成真实二维码（传入核销码字符串）**（`界面位置说明-V3.0.md` § 开发占位替换表）。

实施：
1. 安装 `uqrcode`（HBuilderX 插件市场或 npm：`npm install uqrcodejs`）
2. 全局注册或页面内 import
3. `detail.vue` 的 swiper 内每个 writeoffItem 渲染一个 `<canvas>` + 调用 `uqrcode.make({canvasId, text: item.qrCode})`
4. canvas size 500rpx × 500rpx 白底，符合原型

**备注**：若 uqrcode 引入失败（小程序环境兼容性问题），fallback 用 `uni.showToast` 说明 + 显示 qrCode 字符串（当前状态），在报告里记录阻塞，不占本卡 DoD 分数。

### C. WX-C2 rebate.vue tab 数核对
**文件**：`jst-uniapp/pages-sub/channel/rebate.vue`

原型 `channel-rebate.html` 要求 **6 状态 tab**：
1. 待结算 (pending)
2. 可提现 (withdrawable)
3. 审核中 (in_review)
4. 已打款 (paid)
5. 已驳回 (rejected)
6. 已回退 (rolled_back)

检查当前实现是否有"全部"第 7 个多余 tab → 删除；确保 6 个 tab 对应 `/rebate/ledger/list?status=` 的合法值。

### D. WX-C3 扫码入口角色门纠偏 ⭐
**文件**：`jst-uniapp/pages/my/index.vue` + `pages-sub/appointment/scan.vue` + `writeoff-record.vue`

WX-C3 报告 §E 问题 1：
- 后端实际权限是 `jst_partner / jst_platform_op`（不是任务卡写的 `jst_channel/jst_teacher`）
- 目前入口 tile 挂在 `isChannelUser` 渠道 grid 内，jst_partner 用户若 `userType` 不是 `channel` 看不到

修复：
1. 新增 computed `isPartnerOrOp(store)` = `roles.includes('jst_partner') || roles.includes('jst_platform_op')`
2. my/index.vue 扫码核销 / 核销记录 tile 改用 `v-if="isPartnerOrOp"` 独立区块（不挂在渠道 grid 内）
3. scan.vue / writeoff-record.vue 的 onLoad 权限门改检查 `isPartnerOrOp`

### E. participant pick 模式
**文件**：`jst-uniapp/pages-sub/my/participant.vue`

WX-C3 apply.vue 依赖 `pages-sub/my/participant?mode=pick` 回传档案。补齐：
1. participant 页接收 query `mode=pick`
2. pick 模式下卡片点击不跳编辑，改为：`uni.setStorageSync('ap_picked_participant', item)` + `uni.navigateBack()`
3. pick 模式顶部加 toast 提示"选择参赛档案"

### F. 地址列表 API 封装（若后端已有）
先 `grep` 后端是否已有 `/jst/wx/user/address/*` 路径：
- 存在：在 `jst-uniapp/api/address.js` 新建封装 + `mall/detail.vue` 改用此 api（替换 `uni.chooseAddress` 原生兜底）
- 不存在：保持现状，报告里注明"后端无 address 接口，暂用原生兜底"

### G. contest.supportAppointment 字段探测收敛
**文件**：`jst-uniapp/pages-sub/contest/detail.vue`

WX-C3 用了宽容探测 4 个字段 `appointmentEnabled / supportAppointment / appointmentType / individualAppointmentEnabled`。

修复：`grep` 后端 `ContestDetailVO` / `EventContestDetailVO` 查实际字段名，收敛为单一字段。若后端没暴露任何预约标识字段，保持宽容探测并在报告里报缺口。

### H. 小 bug 扫描（30 min budget）
用 10~30 分钟扫一遍以下文件，记录发现的任意明显 bug / 控制台报错 / 未使用 computed / 链接死路径到报告 §H：
- `pages-sub/channel/withdraw-apply.vue`
- `pages-sub/mall/list.vue`
- `pages-sub/appointment/my-list.vue`
- `pages/my/index.vue`

不要求修，只要求记录（后续单独开卡修）。

## 交付物

- 修改：scan.vue / rebate.vue / detail.vue (appointment) / my/index.vue / participant.vue / contest/detail.vue
- 可能新增：`api/address.js`（看后端探测结果）
- 可能新增：uqrcode 依赖引入（pages.json 或 main.js）
- 任务报告 `P-POLISH-BATCH1-回答.md` 列出每条遗留的状态（done/blocked/skipped + 原因）

## DoD
- [ ] A~G 7 条全部处理（done 或 blocked 说明）
- [ ] H §小 bug 扫描报告至少 3 条发现或明确说明"扫过无问题"
- [ ] commit: `polish(wx): P-POLISH-BATCH1 遗留清理`

## 不许做
- ❌ 不许改后端
- ❌ 不许重构整个页面
- ❌ 不许引入新设计 token / 主题色（沿用当前色板）
- ❌ 不许跳过 H 小 bug 扫描

## 依赖：无
## 优先级：中

---
派发时间：2026-04-09
