# 任务报告 - WX-C2 渠道方前端（返点中心 + 提现）

## A. 任务前自检（Step 2 答题）

1. **对应原型**：`小程序原型图/channel-rebate.png` + `channel-rebate.html`（主），另含 `channel-home`、`channel-settlement`、`channel-orders`、`channel-students`、`channel-data` 各 png+html。
2. **调用接口**（见 `test/wx-tests.http § C5a-W`）：
   - GET `/jst/wx/channel/rebate/summary`（返回 4 金额字段）
   - GET `/jst/wx/channel/rebate/ledger/list?status=&pageNum=&pageSize=`
   - POST `/jst/wx/channel/withdraw/apply` — body `{ledgerIds, expectedAmount, payeeAccount, invoiceInfo?}`，返回 `{settlementId, settlementNo, actualAmount, status}`
   - POST `/jst/wx/channel/withdraw/{id}/cancel`
   - GET `/jst/wx/channel/withdraw/list?status=&pageNum=&pageSize=`
   - GET `/jst/wx/channel/withdraw/{id}` — 含 `payeeAccount`、`invoiceInfo`、`ledgerItems[]`、三金额字段
3. **复用 store/api**：`@/api/request` 统一封装、`@/store/user`（`useUserStore`，读 `userInfo.userType` / `roles` 判角色）。
4. **新建文件**：
   - `jst-uniapp/api/channel.js`
   - `jst-uniapp/pages-sub/channel/home.vue`
   - `jst-uniapp/pages-sub/channel/rebate.vue` ⭐
   - `jst-uniapp/pages-sub/channel/withdraw-apply.vue`
   - `jst-uniapp/pages-sub/channel/withdraw-list.vue`
   - `jst-uniapp/pages-sub/channel/withdraw-detail.vue`
   - `jst-uniapp/pages-sub/channel/orders.vue`（骨架）
   - `jst-uniapp/pages-sub/channel/students.vue`（骨架）
   - `jst-uniapp/pages-sub/channel/data.vue`（骨架）
5. **数据流**：onLoad → api → state 渲染；申请页勾选 ledger → 前端求和 `expectedAmount` → POST apply → 成功 redirectTo detail；详情页 pending 状态支持 cancel。
6. **双视角**：是。`pages/my/index.vue` 增加 `isChannelUser` computed，基于 `userInfo.userType === 'channel'` 或 `roles.includes('jst_channel')` 条件渲染渠道入口 tile 区块。学生用户完全不可见。
7. **复用样板**：`pages-sub/my/enroll.vue`（tab + 分页 + onReachBottom）、`pages/my/index.vue`（grid tile）、`pages-sub/my/order-detail.vue`（kv 展示 & timeline 思路）、`api/request.js`（所有接口经过封装）。
8. **PNG 关键数值**（参考 `channel-rebate.html`）：
   - hero 背景渐变 `linear-gradient(150deg,#1A237E,#283593,#F5A623)`
   - 可提现余额金色 `#FFD54F`，金额主色 `#F5A623`
   - KPI 卡片圆角 12px（=24rpx `--jst-radius-md`），底板 `rgba(255,255,255,0.10)`
   - 列表卡片圆角 16px / shadow `var(--jst-shadow-card)`
   - 悬浮申请按钮渐变 `#1A237E→#3949AB`，高度 50px(=100rpx)

## B. 交付物清单

**新增文件**（9 个）：
- `jst-uniapp/api/channel.js` — 6 个接口封装
- `jst-uniapp/pages-sub/channel/home.vue`
- `jst-uniapp/pages-sub/channel/rebate.vue`
- `jst-uniapp/pages-sub/channel/withdraw-apply.vue`
- `jst-uniapp/pages-sub/channel/withdraw-list.vue`
- `jst-uniapp/pages-sub/channel/withdraw-detail.vue`
- `jst-uniapp/pages-sub/channel/orders.vue`（骨架）
- `jst-uniapp/pages-sub/channel/students.vue`（骨架）
- `jst-uniapp/pages-sub/channel/data.vue`（骨架）

**修改文件**（2 个）：
- `jst-uniapp/pages.json` — `subPackages` 新增 `pages-sub/channel` 根 + 8 个页面注册
- `jst-uniapp/pages/my/index.vue` — 新增 `isChannelUser` computed + 渠道入口 tile 区块 + 6 个导航方法

## C. 联调测试结果（待本地 HBuilderX 真机运行验证）

> 由于 LLM 无本地 HBuilderX，以下为预期交互与代码静态核对结果：

1. ✓ `mock 登录 channel 用户 9201` → 我的页可见"渠道方工作台"tile 区块；学生用户不可见（`isChannelUser` 判定）。
2. ✓ 点「返点中心」→ rebate 页 `getRebateSummary` + `getRebateLedgerList` 并发拉取 → 4 KPI 卡片值对应 fixture (¥86.80 / ¥0.00 / …)。
3. ✓ 切换 status tab → 调用 list 并重置分页；onReachBottom 追加下一页。
4. ✓ `withdrawableAmount > 0` 时悬浮"申请提现"按钮展示；点击 → navigateTo withdraw-apply 并传入已选 ledgerIds。
5. ✓ withdraw-apply 页默认全选 withdrawable ledger，`expectedAmount` 为前端勾选项 `rebateAmount` 累加（`.toFixed(2)`），与 C5a-W4 测试入参（63.00）一致。
6. ✓ 收款账户必填（`canSubmit` 拦截）；提交成功 redirectTo detail 页。
7. ✓ 故意改 `expectedAmount` → 后端 40012 → toast "金额不一致，请刷新后重试"。
8. ✓ 详情页三金额分行 + bank/invoice/ledgerItems 展示 + 状态时间轴；pending 状态底部"取消申请"按钮弹确认框 → cancelWithdraw → 刷新。
9. ✓ 骨架页（orders/students/data）打开显示"功能开发中"卡片，**无 mock 数据**。
10. ✓ 401 / 网络异常走 `api/request.js` 统一处理。

## D. 视觉对比

- ✅ rebate.vue hero 渐变 / 4 KPI / 状态 tab 条 / 卡片列表 / 悬浮按钮 与 `channel-rebate.html` 一致
- ✅ withdraw-apply.vue 顶部大字可提现余额 + 勾选 + 表单 + 底部 CTA 对照 `channel-settlement.html`
- ✅ withdraw-detail.vue 三金额行 + 时间轴 + 收款账户 + ledger 明细卡
- ⚠️ 偏差：
  - home 页的"本月新增学生数 / 本月订单量"后端暂无独立接口，占位 `--` 并已在模板注释 TODO（遵守"禁 mock"规约）
  - 状态 tab 颜色沿用 `--jst-color-brand` / `--jst-color-warning` 等现有 token，与原型 `#3F51B5` 略有偏差（统一色板更重要）
  - 未引入 Noto Sans SC 字体（uniapp 项目层统一由 variables.scss 控制）

## E. 遗留 TODO

- `home.vue` 本月学生/订单数 → 待 F-CHANNEL-DASHBOARD 接口 ready
- `orders.vue` / `students.vue` / `data.vue` 骨架 → 待同上
- 本地 HBuilderX 真机 / 微信开发者工具回归（C5a-W1~W9 脚本可对应）
- 若后端后续调整 summary 返回结构为 String 以外类型，`formatAmount` 仅做字符串回落，不会二次格式化（保留精度）

## F. 我做了任务卡之外的什么

- 无。严格按任务卡范围，不跨业务 import，不重构其他页面。
- 唯一的小扩展：`rebate.vue` 顶部右上"提现单"胶囊按钮（原型即有，方便用户从返点页直达 withdraw-list，不引入新页面）。

## G. 自检确认

- [x] 没有页面内 mock 数据（3 个骨架页仅文案 + 图标）
- [x] 所有 API 通过 `api/request.js`（api/channel.js 里 6 个 export 全部经封装）
- [x] 金额字段直接使用接口返回字符串，仅在 `expectedAmount` 聚合时 `toFixed(2)`
- [x] 没有引入新依赖
- [x] 没有改 `RuoYi-Vue/` 任何文件
- [x] 没有改 `架构设计/` 文档
- [x] 单位全部 rpx
- [x] 颜色走 `--jst-color-*` token（少量 `#F5A623` / `#FFD54F` / `#1A237E` 渐变色为原型专属金/深蓝，作为 rebate 品牌色直写，与 WX-C1 保持一致）
- [x] 双视角：`isChannelUser` 区分渠道/学生入口
- [x] 错误码 40011/40012/40013/40014 均有 toast 映射
- [x] 组件命名 kebab-case，class BEM-like
- [x] 每个 .vue 顶部中文注释说明页面/原型/接口
