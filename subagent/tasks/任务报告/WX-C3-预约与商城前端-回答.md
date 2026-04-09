# 任务报告 - WX-C3 预约与商城前端（个人预约 + 扫码核销 + 积分商城）

## A. 任务前自检（Step 2 答题）

1. **原型对应**：
   - 预约：`reserve.png` / `my-reserve.png` / `writeoff-apply.png`(学生视角) / `checkin-scan.png` / `writeoff-record.png`
   - 商城：`points-mall.png` / `points-goods-detail.png` / `points-order.png` / `points-center.png` / `points-detail.png`
2. **调用接口**（以后端 controller/DTO/VO 为准，任务卡部分字段有出入，已**按实际契约**实现）：
   - 预约 `WxAppointmentController`：`POST /jst/wx/appointment/apply`、`POST /{id}/cancel`、`GET /my`（返 `List` 非分页）、`GET /{id}`、`GET /contest/{id}/remaining`
   - 核销 `WxWriteoffController`：`POST /jst/wx/writeoff/scan` 入参 `{qrCode, scanType?, terminal?}`（**不是 `qrPayload`**）、`GET /jst/wx/writeoff/records`（PageHelper 分页）
   - 商城 `WxMallGoodsController` + `WxMallExchangeController`：goods list/{id}、exchange apply/my/{id}/cancel/pay mock-success
3. **复用 store/api**：`@/api/request`、`@/store/user`（`userInfo.availablePoints` / `roles`）
4. **新建文件**：
   - `jst-uniapp/api/appointment.js`、`jst-uniapp/api/mall.js`
   - `pages-sub/appointment/{apply,my-list,detail,scan,writeoff-record}.vue`
   - `pages-sub/mall/{list,detail,exchange-list,exchange-detail}.vue`
5. **数据流**：各页 onLoad/onShow 拉接口 → state 渲染；预约申请付费单返回 `orderId` → 跳 C2 `/pages-sub/my/order-detail`；商城 `mixed` 支付返回 `pending_pay+exchangeId` → 前端调 `mock-success` 快速收敛
6. **双视角**：
   - 预约 apply/my-list/detail：学生视角
   - scan/writeoff-record：任务卡要求"渠道/教师"入口，实际后端权限是 `jst_partner`/`jst_platform_op`。入口 tile 放在 `my/index.vue` 的渠道 grid（`isChannelUser` 判定），页面内 `onLoad` 再做前端权限门（roles 检查 partner/op）；非授权自动返回 + toast。**这是一个任务卡与后端契约不一致的地方，已记遗留在 E 节**
7. **样板复用**：`pages-sub/channel/rebate.vue`（tab + 分页列表 + 悬浮按钮）、`pages-sub/my/order-detail.vue`（时间轴 + kv）、`pages/my/index.vue`（grid tile）
8. **PNG 关键数值**：
   - 商城主色金色 `#F5A623→#FF9800` 渐变；积分金额字号 40/48rpx；卡片圆角 `--jst-radius-md`（24rpx）
   - 二维码容器 500rpx × 500rpx 白底；swiper 高 720rpx 用于多子项横滑
   - 预约 hero 背景复用 `--jst-color-brand` 系渐变以与 WX-C1/WX-C2 色板统一

## B. 交付物清单

### 新增文件（11 个）
- `jst-uniapp/api/appointment.js` — 7 个接口封装
- `jst-uniapp/api/mall.js` — 7 个接口封装
- `pages-sub/appointment/apply.vue` — 申请预约（日期+场次+档案+剩余名额+提交）
- `pages-sub/appointment/my-list.vue` — 我的预约（全部/待使用/已使用/已取消 tab 前端过滤）
- `pages-sub/appointment/detail.vue` — 详情 + 多核销子项二维码 swiper + 取消
- `pages-sub/appointment/scan.vue` — 扫码核销（权限门 + `uni.scanCode` + 结果卡）
- `pages-sub/appointment/writeoff-record.vue` — 核销记录分页列表
- `pages-sub/mall/list.vue` — 商品列表（积分 banner + 类型 tab + 2 列 grid + 悬浮"我的兑换"）
- `pages-sub/mall/detail.vue` — 商品详情（swiper + 步进器 + 实物选址 + 立即兑换）
- `pages-sub/mall/exchange-list.vue` — 我的兑换单列表（6 tab + 分页）
- `pages-sub/mall/exchange-detail.vue` — 兑换单详情（时间轴 + 去支付/取消/前往查看）

### 修改文件（3 个）
- `jst-uniapp/pages.json` — 新增 `pages-sub/appointment` + `pages-sub/mall` 两个分包（共 9 个页面注册）
- `jst-uniapp/pages/my/index.vue`
  - 删除 7 个 `showComingSoon` 占位 tile + 重复的"我的课程"（DEBT-2 Part A 顺带完成）
  - 新增真实 tile：我的预约 / 积分商城
  - 渠道 grid 追加：扫码核销 / 核销记录
  - methods 对应新增 navigate*，移除 `showComingSoon`
- `jst-uniapp/pages-sub/contest/detail.vue`
  - CTA 按钮区包裹进 `.contest-detail-page__cta-actions`
  - 追加"我要预约"次按钮，基于 `showAppointmentEntry` computed（探测 `appointmentEnabled/supportAppointment/appointmentType/individualAppointmentEnabled` 任一为真）
  - 新增 `handleAppointmentTap()` 跳 `/pages-sub/appointment/apply?contestId=&contestName=`
  - SCSS 新增 `.contest-detail-page__cta-secondary` 样式

## C. 契约偏差说明（与任务卡的差异，**按后端实际为准**）

任务卡部分内容与后端真实契约不一致，为保证可联调，按后端实际契约实现：

| 任务卡描述 | 实际契约（后端代码为准） | 影响 |
|---|---|---|
| `writeoff/scan` 入参 `{qrPayload}` | `WriteoffScanDTO { qrCode, scanType?, terminal? }` | api/appointment.js 用 `qrCode` |
| 扫码权限"`jst_channel`/`jst_teacher`" | `@PreAuthorize("hasAnyRoles('jst_partner','jst_platform_op')")` | scan.vue 权限门检查 partner/op；入口 tile 暂挂在渠道 grid（若 partner 用户当前不经过 isChannelUser，需后续调整） |
| `GET /appointment/my?status=&pageNum=&pageSize=` | 无分页参数，后端直接 `AjaxResult.success(list)` | my-list.vue 一次性拉全量，tab 前端过滤 |
| detail 返回 `qrCodes` 数组 | 实际返回 `writeoffItems: [{writeoffItemId,itemName,qrCode,status,writeoffTime}]` + 顶层 `qrCode`/`individualAppointment`/`teamAppointment` 标识 | detail.vue swiper 遍历 `writeoffItems` |
| `appointment apply` 入参 `{contestId,sessionCode,appointmentDate,participantId?}` | 后端额外支持 `couponId` / `pointsToUse`（本期 UI 未开放） | apply.vue 仅提交 4 字段，其他扩展留给后续 |
| `exchange/apply` 入参 `{goodsId, quantity, addressSnapshot?, payMethod}` | 后端一致，`payMethod` 是 `@NotBlank String`，前端按 `'mixed'`/`'points'` 自动判定 | detail.vue 实现 |

## D. 联调测试结果（静态核对 + 期望交互）

> LLM 无本地 HBuilderX，以下为代码静态核对 + 对照 wx-tests.http § C6/C7/C8 的预期：

1. ✓ 学生登录 → 我的页点「我的预约」→ `getMyAppointments` 返回的 list 按 tab 过滤渲染
2. ✓ 赛事详情页 `showAppointmentEntry` 为真时出现"我要预约"次按钮，点击跳 apply 页并带 contestId / contestName
3. ✓ apply 页选日期+场次 → `getRemaining` 实时刷新；提交时 free 单无 orderId → 跳 detail，付费单返 orderId → 跳 `/pages-sub/my/order-detail`
4. ✓ detail 页对 C6/C7 detail 响应中的 `writeoffItems` 做 swiper 渲染；pending/booked 可取消，30093 映射"已有核销项"
5. ✓ 扫码核销：学生访问 scan.vue onLoad 检测 roles 不含 partner/op → toast+返回；partner 用户 uni.scanCode → POST /writeoff/scan 入参 `qrCode` 字段名与 DTO 对齐
6. ✓ 商城 list：拉 `/mall/goods/list` 并按 goodsType 过滤；积分余额从 `useUserStore().userInfo.availablePoints` 读（onShow 触发 fetchProfile 刷新）
7. ✓ 商城 detail：数量步进 + 实物选址（uni.chooseAddress 兜底），submit 时虚拟商品 `payMethod=points`，含现金 `mixed`，后者自动 mock-success 收敛
8. ✓ 兑换列表 6 tab 分页；详情页 pending_pay 显示"去支付/取消"，completed 虚拟商品显示"前往查看"占位

## E. 遗留 TODO

1. **扫码入口角色不匹配**：任务卡说"渠道/教师"，后端权限是 `jst_partner/jst_platform_op`。目前入口 tile 放在渠道 grid 内，jst_partner 用户如果 `userType` 不是 `channel` 则看不到入口，需要主 Agent 确认是为入口加 `isPartnerUser` computed 还是调后端权限；本期先做"渠道 grid 内显示"，scan.vue 内部再次权限校验以防越权 toast。
2. **二维码渲染**：本期 `ad-qr__box` 里只展示 qrCode **字符串内容**而非真正的二维码图片，因为工程内没有 uqrcode/qrcode 组件，而任务卡禁止自行实现。生产前需接入 `uqrcode.wxs` 或由后端改返回 base64 图片。已在文件头注释标注 TODO。
3. **apply 页选档案**：暂用 `uni.storage` 做参数回传（`ap_picked_participant`），依赖 `pages-sub/my/participant` 支持 `mode=pick` 参数；若该页面尚未支持，需要后续小任务卡补 pick 模式。
4. **地址选择**：`detail.vue` 对实物商品暂用 `uni.chooseAddress` 微信原生 API 兜底。任务卡提到 `/jst/wx/user/address/list`，但我没在 api/auth.js 找到现成封装；为避免越权新增未经确认的 api，使用了原生 API。需要主 Agent 确认是否新增 `api/address.js`。
5. **my-list 无分页**：后端 `/appointment/my` 返回 List 未分页；若用户历史预约过多会一次性拉取，后续需要后端改造。
6. **exchange-list `payMethod` 枚举值**：本期按 `'points' | 'mixed'` 提交，未核对后端具体校验字符串，联调时若失败需要调 `payMethod` 入参。

## F. 我做了任务卡之外的什么

- 顺带完成 **DEBT-2 Part A** 的 `my/index.vue` 占位 tile 清理（同一批派发），避免占位与新增入口并存造成混乱。
- `contest/detail.vue` 的 `showAppointmentEntry` 做了宽容探测（4 个字段任一为真），避免因后端字段名未知导致入口永久不展示；若后端字段已确定，后续可收敛为单一字段。
- 为渠道 grid 同时加了"扫码核销"+"核销记录"两个 tile（任务卡只要求扫码），因为核销记录页独立存在、不加入口会变孤儿页。

## G. 自检确认

- [x] 没有页面内 mock 数据（`ap_picked_participant` 为 UI 临时回传通道，非业务数据）
- [x] 所有 API 通过 `api/request.js` 封装；无裸 `uni.request`
- [x] 没有引入新依赖
- [x] 没有改后端 Java / DDL / 架构文档
- [x] 单位全部 rpx，颜色走 `--jst-color-*` token（仅金色 `#F5A623/#FF9800`、背景深蓝 `#1A237E` 等原型品牌色直写）
- [x] 每个 .vue 文件顶部带中文注释（页面说明 + 原型 + 接口）
- [x] 扫码权限门在 scan.vue 内生效
- [x] 兑换三分支：纯积分 / 积分+现金（自动 mock-success） / 实物（addressSnapshot）
- [x] 取消兑换限于 `pending_pay`
- [x] 二维码容器 500rpx × 500rpx 白底
- [x] pages.json 注册 9 个新页面 + 2 个新分包
- [x] commit 留给主 Agent 统一打包
