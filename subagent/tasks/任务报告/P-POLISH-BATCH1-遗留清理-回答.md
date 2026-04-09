# 任务报告 - P-POLISH-BATCH1 前端遗留清理第 1 批

## 状态总览

| 项 | 状态 | 说明 |
|---|---|---|
| A · scan.vue 补齐 | ✅ done | 3 类 chip / 场次 sheet / 今日统计 / 最近记录 / 三态结果卡 / 深色沉浸式 UI |
| B · uqrcode 接入 | ⚠️ blocked | jst-uniapp 无 `package.json`（HBuilderX 工程），无法通过 CLI 安装 `uqrcodejs`。代码已预留 canvas + `canvas-id="qr_"+idx`，并在文件头注释了接入步骤；字符串回落仍在，安装后只需 4 行 import/make/drawCanvas 即可启用 |
| C · rebate 6 tab | ✅ done | 删除"全部" tab，`STATUS_TABS` 6 项，`activeStatus` 默认 `pending` |
| D · 扫码权限门纠偏 | ✅ done | `my/index.vue` 新增 `isPartnerOrOp` computed + 独立"核销工作台"区块；`scan.vue`（已在 A 节合入）+ `writeoff-record.vue` onLoad 均加 `roles.includes('jst_partner')||'jst_platform_op'` 权限门 |
| E · participant pick 模式 | ✅ done | `?mode=pick` → 顶部 toast 提示 + 卡片点击改为 `setStorageSync('ap_picked_participant',{id,name})+navigateBack`，与 `appointment/apply.vue` onShow 对齐 |
| F · 地址 API | ⚠️ skipped (backend 缺失) | grep 后端 `/jst/wx/user/address*` / `WxUserAddressController` / `UserAddressController` 均**无结果**，`jst-user` 模块 wx controller 无 address。`mall/detail.vue` 保持 `uni.chooseAddress` 原生兜底 |
| G · 预约字段收敛 | ✅ done | grep 到 `WxContestDetailVO.supportAppointment: Integer`，`contest/detail.vue` computed 从宽容探测 4 字段收敛为单一 `v === 1 \|\| true \|\| '1'` |
| H · 小 bug 扫描 | ✅ done | 3 条发现，见下方 §H |

## A · scan.vue 重写细节

**文件**：`jst-uniapp/pages-sub/appointment/scan.vue`

- 三类 chip：`scanType: 'entry'/'gift'/'award'`，切换时写入后续 `scanWriteoff` 入参 `{qrCode, scanType, terminal}`
- 场次 sheet：本地 3 条 mock（`MOCK_SESSIONS`），切换后今日统计 + 最近记录清零；已在 sheet 底部 tip 说明"待后端提供 active sessions 接口"
- 今日统计 4 格：`todayStats: {total, entry, gift, award}`，扫码成功后 `handleResult('success',...)` 累加
- 最近核销：`recentList` FIFO 最多 10 条，含 state/qrCode/itemName/scanType/time
- 三态结果卡：根据 errorCode `30095/30096` 归为 `used`，其他错误为 `fail`，成功为 `success`；对应 ✅/❌/⚠️ 图标 + 绿/红/橙背景
- 深色沉浸式：`$dark-bg: #0D1B2A`，卡片 `#152840`，主文本 `#E8F1FF`，强调 `#FFD54F`
- 权限门：onLoad 检查 `jst_partner/jst_platform_op`，否则 toast + navigateBack

## B · uqrcode 阻塞详情

**阻塞原因**：`jst-uniapp/` 无 `package.json` / `node_modules`（HBuilderX 项目结构），LLM 无法执行 `npm install uqrcodejs`，也无法注入 HBuilderX 插件。

**已完成的前置工作**：
- `detail.vue` swiper 内每个 `swiper-item` 添加 `<canvas canvas-id="'qr_' + idx">`，CSS 设 400rpx×400rpx
- 文件头部注释完整记录了接入步骤：
  ```js
  // 1. import UQRCode from 'uqrcodejs'
  // 2. onReady 里遍历 writeoffItems:
  //    const qr = new UQRCode()
  //    qr.data = item.qrCode; qr.size = 250
  //    qr.make()
  //    qr.canvasContext = uni.createCanvasContext('qr_' + idx, this)
  //    qr.drawCanvas()
  ```
- 字符串 payload 作为 fallback 仍展示在 canvas 下方，开发期可验证 payload 正确性

**接入步骤**（留给用户）：
1. HBuilderX 插件市场搜 `uqrcode` 或 `npm install uqrcodejs` 在能跑 npm 的工作区
2. 解除 detail.vue 注释块，补 onReady 逻辑

## C · rebate 6 tab 变更

```diff
-const STATUS_TABS = [
-  { value: '', label: '全部' },
-  { value: 'pending', label: '待结算' }, ...
-]
+// POLISH-C: 6 tab, 移除"全部" (原型 channel-rebate.html 仅 6 状态)
+const STATUS_TABS = [
+  { value: 'pending', label: '待结算' }, ...
+]
```
`activeStatus: ''` → `'pending'`。

## D · 扫码权限门纠偏

**问题**：WX-C3 把 scan/writeoff-record tile 挂在 `v-if="isChannelUser"` 渠道 grid 内，但 `jst_partner` 用户若 `userType !== 'channel'` 看不到。

**修复**：
1. `my/index.vue` 新增：
   ```js
   isPartnerOrOp() {
     const roles = (useUserStore().userInfo?.roles) || []
     return roles.includes('jst_partner') || roles.includes('jst_platform_op')
   }
   ```
2. 两个 tile 移出渠道区块，独立成"核销工作台"区块，`v-if="isPartnerOrOp"`
3. `scan.vue`（随 A 节合入）+ `writeoff-record.vue` onLoad 内部再做一次权限门兜底

## E · participant pick 模式细节

- `onLoad(query)` 读 `mode=pick`，`data.pickMode = true`
- toast 提示"请选择参赛档案"
- `openDetail(item)` 首行拦截：pick 模式下 `setStorageSync('ap_picked_participant',{id,name}) + navigateBack`，不再打开详情弹层
- 与 `appointment/apply.vue` 已写好的 `onShow` 消费逻辑对齐（无需改 apply.vue）

## F · 地址 API 探测结果（backend 缺失）

- `grep "/jst/wx/user/address" RuoYi-Vue/**/*.java` → 0 结果
- `grep "UserAddressController|WxUserAddress" RuoYi-Vue/**` → 0 结果
- `jst-user/controller/wx/*.java` 未发现 address 相关 controller

**结论**：后端暂无用户地址列表接口。`mall/detail.vue` 保持 `uni.chooseAddress` 原生兜底不变；等后端补 `/jst/wx/user/address/list|create|update|delete` 后再开 `api/address.js`。

## G · 预约字段收敛

- 后端实际字段（确认）：
  - `RuoYi-Vue/jst-event/.../vo/WxContestDetailVO.java:30` `private Integer supportAppointment`
  - 同项在 `ContestDetailVO` 与 `JstContest` 领域对象
- 前端改动：
  ```diff
  -return !!(d.appointmentEnabled || d.supportAppointment || d.appointmentType || d.individualAppointmentEnabled)
  +const v = this.detail && this.detail.supportAppointment
  +return v === 1 || v === true || v === '1'
  ```
- 兼容 Integer(1)/Boolean/字符串 '1' 三种返回形式，避免 Jackson 序列化差异

## H · 小 bug 扫描（预算 30 min，发现 3 条）

扫描范围：`pages-sub/channel/withdraw-apply.vue` / `pages-sub/mall/list.vue` / `pages-sub/appointment/my-list.vue` / `pages/my/index.vue`

### H1 · mall/list.vue defaultCover 始终为空串
**位置**：`pages-sub/mall/list.vue:32` + `:73`
**问题**：模板 `<image :src="item.coverImage || defaultCover">`，但 `defaultCover: ''` 为空字符串，回落路径渲染空 `<image>`，真机会看到空白占位区而非 logo。
**建议**：放一张 `jst-uniapp/static/mall/default-cover.png` 并改 `defaultCover: '/static/mall/default-cover.png'`，或用 `v-if="item.coverImage"` 条件渲染 + `v-else` 纯色占位 block。
**严重度**：P2 · UI 小瑕疵

### H2 · withdraw-apply.vue 顶部"可提现余额"语义不准确
**位置**：`pages-sub/channel/withdraw-apply.vue:9, :97`
**问题**：`totalWithdrawable` computed 对**当前页面 ledgerList 中所有条目** `rebateAmount` 求和。然而 ledgerList 是 `status=withdrawable` 的 ledger，接口分页上限 200 条，若后端实际 `summary.withdrawableAmount` 字段与前端累加值不一致（例如存在已被锁定的行被过滤），显示会误导用户。
**建议**：顶部余额应直接取 `rebate/summary` 的 `withdrawableAmount`（单调用 1 次即可）而非前端累加；前端累加仅用于 `expectedAmount` 后端校验。
**严重度**：P1 · 数据可信度风险

### H3 · appointment/my-list.vue writeoffProgress 字段后端无显式契约
**位置**：`pages-sub/appointment/my-list.vue:28`
**问题**：模板用 `item.writeoffProgress` 判断展示核销进度，但后端 `AppointmentListVO`（jst-event）未见这个字段（实际可能是 writeoffItems 的 used/total，前端需自行计算）。当前代码对 undefined 只是 `v-if` 隐藏，不会崩，但永远显示不出"核销进度"。
**建议**：联调时确认字段，若后端无则前端从 `writeoffItems.filter(i=>i.status==='used').length / writeoffItems.length` 计算；本次 my-list 接口返回是 List 无 writeoffItems 简表，则等后端增加 `writeoffDoneCount/writeoffTotalCount` 字段再展示。
**严重度**：P2 · 功能缺失但不崩

**其他**：my/index.vue 在 H 扫描期间整体 OK，服务 grid 现在共 12 项（3 列 4 行），密度偏高但在可用范围内，建议 POLISH-BATCH2 时考虑分组折叠。

## 修改文件汇总

- `jst-uniapp/pages-sub/appointment/scan.vue`（重写）
- `jst-uniapp/pages-sub/appointment/detail.vue`（canvas 预留）
- `jst-uniapp/pages-sub/appointment/writeoff-record.vue`（权限门）
- `jst-uniapp/pages-sub/channel/rebate.vue`（6 tab）
- `jst-uniapp/pages/my/index.vue`（isPartnerOrOp + 独立核销区块）
- `jst-uniapp/pages-sub/my/participant.vue`（pick 模式）
- `jst-uniapp/pages-sub/contest/detail.vue`（supportAppointment 收敛）

## DoD 对照

- [x] A~G 7 条全部处理（A/C/D/E/G done，B blocked 详细说明，F skipped backend 缺失）
- [x] H 小 bug 扫描报告 3 条发现
- [x] 未改后端
- [x] 未重构整页
- [x] 未引入新设计 token
- [ ] commit 留给主 Agent
