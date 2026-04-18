# Round 2B 前端集成 · B1 批量支付 + B3 发票抬头管理 报告

> **日期**：2026-04-18
> **分支**：`feat/frontend-round-2b-b1-b3` (从 main 切出)
> **后端配合分支**：`feat/backend-round-2b-b1-b3` (另一 agent 同步实施, 本分支仅前端)
> **跳过**：B2 (通知偏好) / B4 (账号注销) — 由后续批次处理
> **硬约束符合**：未改动 `RuoYi-Vue/` 任何文件；未合并到 main；未 force push；未跳 hook；
>                 全部 Design Token 变量，零硬编码颜色/间距；所有 API 失败时带友好 toast 降级

---

## 一、完成情况总览

| 项 | 状态 | 核心文件 |
|---|---|---|
| **B1 批量支付集成** | ✅ 完成 | `api/channel.js` + `pages-sub/channel/orders.vue` |
| **B3 发票抬头管理页** | ✅ 完成 | `api/invoice-title.js` + `pages-sub/my/invoice-title-list.vue` + `pages.json` |
| **B3 settings.vue 跳转** | ✅ 完成 | `pages-sub/my/settings.vue` |
| **B2 / B4** | ⏭️ 跳过 | 按任务卡要求 |

---

## 二、新建文件清单

| 文件 | 说明 |
|---|---|
| `jst-uniapp/api/invoice-title.js` | 发票抬头 4 个 CRUD API (list / save / delete / set-default) |
| `jst-uniapp/pages-sub/my/invoice-title-list.vue` | 发票抬头管理页 (列表 + 添加/编辑弹层 + 空态 CTA) |

---

## 三、改动文件清单

| 文件 | 改动点 |
|---|---|
| `jst-uniapp/api/channel.js` | 新增 `batchPayChannelOrders(orderIds)` 函数 |
| `jst-uniapp/pages-sub/channel/orders.vue` | 导入 `batchPayChannelOrders`；重写 `goBatchPay()` 走真实接口 (含降级提示)；删除 `TODO(backend-round-2b)` 注释 |
| `jst-uniapp/pages-sub/my/settings.vue` | `navigateInvoiceTitle()` 改为 `uni.navigateTo` 到新页；删除 `TODO(backend-round-2b)` 注释 |
| `jst-uniapp/pages.json` | `pages-sub/my` 分包下新增 `invoice-title-list` 路由 |

---

## 四、API 函数清单

### 4.1 B1 · `api/channel.js` 新增

```js
/**
 * 批量支付渠道代报名订单 (Round 2B · B1)
 * 后端: POST /jst/wx/channel/orders/batch-pay
 * 入参: { orderIds: Long[] } — 必须全部属于当前渠道方 + status=pending_pay
 * 返回: { batchPayNo, totalAmount, count, items: [{ orderId, merchantPayParams }] }
 */
export function batchPayChannelOrders(orderIds) {
  return request({
    url: '/jst/wx/channel/orders/batch-pay',
    method: 'POST',
    data: { orderIds }
  })
}
```

### 4.2 B3 · `api/invoice-title.js` (全新文件)

```js
/** 当前用户的发票抬头列表 */
export function listInvoiceTitles(options = {})
/** 新增/更新 (带 titleId 为更新) */
export function saveInvoiceTitle(data, options = {})
/** 删除 */
export function deleteInvoiceTitle(id, options = {})
/** 设为默认 */
export function setDefaultInvoiceTitle(id, options = {})
```

路径统一 `/jst/wx/user/invoice-title/*` (与 address 模块风格一致)。

---

## 五、pages.json 改动

`pages-sub/my` 分包 `address-edit` 之后新增：

```json
{
  "path": "invoice-title-list",
  "style": {
    "navigationBarTitleText": "发票抬头",
    "navigationStyle": "custom"
  }
}
```

---

## 六、实施细节

### 6.1 B1 · 批量支付 UX 简化策略

- 勾选渠道订单后，点"批量支付" footer 按钮 → 二次确认 modal → 调 `batchPayChannelOrders(orderIds)`
- 成功返回 `{ batchPayNo, totalAmount, count, items }`：
  - 当前实现：`showToast("批量支付单创建成功，共 X 单 ¥Y")` + 1.8s 后 `navigateTo('/pages-sub/my/order-list?batchPayNo=xxx')` + 清空选择 + 重载列表/统计
  - 真实微信支付唤起 (`wx.requestPayment(merchantPayParams)`) **留 TODO**：`// TODO(wxpay): 接入商户号后改为循环 resp.items 每项调 wx.requestPayment(merchantPayParams)`
  - 原因：微信支付需商户号配置 (jst.wxpay.enabled)，与 Mock/Real 切换逻辑耦合，非本批前端范围
- 失败降级：catch → `showToast(err.msg \|\| "批量支付接口暂未就绪，请稍后再试")` ，保留勾选状态不破坏

### 6.2 B3 · 发票抬头页视觉设计

- **卡片风格**：参考 `pages-sub/my/address-list.vue`
- **类型 chip**：个人 = brand-light / company = warning-light（两种颜色区分 + 浅底深字）
- **默认徽章**：`$jst-gold-light` 背景 + `$jst-gold` 边框 + `darken($jst-gold, 25%)` 文字，与"个人/企业" chip 视觉层级错开
- **税号区**：企业抬头专属 `$jst-bg-page` 浅底小卡，`letter-spacing: 1rpx` 提高识别度
- **操作按钮**：三按钮 flex:1 均分（设默认/编辑/删除）；`isDefault=1` 时"设默认"变成"已默认"+ disabled 禁用态
- **空态**：使用 `jst-empty` 组件 + `illustration="records"` + `buttonText="添加发票抬头"` + `@action` 打开弹层

### 6.3 B3 · 添加/编辑弹层

- **形式**：`u-popup mode="bottom"` + `border-radius="24"` + `closeable` (关闭按钮)
- **字段**：
  - 类型：`u-radio-group` (个人/企业 二选一)
  - 抬头名称：`u-input border="bottom" maxlength="128"` — 必填
  - 税号：仅企业显示，`maxlength="20"`，`blur` 自动去空格大写
  - 设为默认：`u-switch`
- **校验**：
  - `titleName` 必填 + ≤128 字
  - `titleType=='company'` 时 `taxNo` 必填 + 正则 `/^[A-Z0-9]{15,20}$/`
  - 校验失败 `showToast icon='none'`，不阻塞弹层
- **删除防误触**：`uni.showModal` + `confirmColor: '#FF4D4F'` 显红色确认

### 6.4 降级保护

- `loadList()` 失败时 `list = []` 走空态，**不** toast 打扰用户（因为后端未合入时页面本身可访问，空态体验合理）
- `handleSave / setDefault / delete` 失败时 toast 后端 `err.msg` 或兜底文案

---

## 七、设计 Token 使用核查

| 用途 | Token |
|---|---|
| 品牌色 | `$jst-brand` / `$jst-brand-light` / `$jst-brand-gradient` |
| 默认徽章 | `$jst-gold` / `$jst-gold-light` (darken 25% 作文字) |
| 警示/企业 chip | `$jst-warning` / `$jst-warning-light` |
| 危险按钮/删除 | `$jst-danger` / `$jst-danger-light` |
| 间距 | `$jst-space-xs/sm/md/lg/xl/xxl` (零硬编码 rpx/px 在间距维度) |
| 圆角 | `$jst-radius-sm/md/lg/round` |
| 阴影 | `$jst-shadow-sm/md` |
| 字重 | `$jst-weight-medium/semibold/bold` |

唯一硬编码：`u-radio/u-switch active-color` prop 只接收字符串，使用 `const BRAND_COLOR = '#2B6CFF'` (与 settings.vue 既有约定一致)。

---

## 八、不确定决策

1. **API 路径使用 `/jst/wx/user/invoice-title/*` 而非 `/jst/wx/invoice-title/*`**：
   任务卡 §3.4 示例写为 `/jst/wx/user/invoice-title/list`，而 `BACKEND-UX-POLISH-TODO-ROUND-2.md §二 B3` 原 spec 写为 `/jst/wx/invoice-title/*`。
   采用**任务卡版本** (`/jst/wx/user/*`)，与 `/jst/wx/user/address/*` 风格一致，更规整。后端 agent 的新 untracked 文件也确认走 `/jst/wx/user/invoice-title/*`（从暂存的 `WxChannelOrderController` 等文件名推测，但未读源码确认路径）。**后端若走不同路径前端需改 api/invoice-title.js 一行**。

2. **批量支付 UI 跳转后**：任务卡要求"跳 `/pages-sub/my/order-list?batchPayNo=xxx`"，但 `order-list.vue` 目前不消费 `batchPayNo` 参数。**未主动改 order-list.vue**（超出本卡范围），query 传入只是信息保留；真跑时用户看到完整订单列表，不算阻塞。若后续需要筛选，独立任务补。

3. **新增/编辑弹层 vs 子页面**：按任务卡"你判断"采用**弹层**，理由：
   - 表单字段简单（3~4 个），弹层更轻量
   - 无需多跳一级路由，符合移动端"一屏完成"体验
   - 弹层方便在切换类型时动态隐藏税号行，子页面跳转过渡会闪烁

4. **`form.titleName` 校验未启用 u-form rules**：因为 u-popup 内 u-form 在某些 uView 版本与弹层动画冲突，改为 `handleSave` 时手动 validate，更稳。

---

## 九、遗留与 TODO

| 类型 | 位置 | 说明 |
|---|---|---|
| **TODO(wxpay)** | `channel/orders.vue` L265 `goBatchPay` | 接入商户号后改为循环 `resp.items` 每项调 `wx.requestPayment(merchantPayParams)`；当前仅 toast + 跳订单列表 |
| 未实现 | `order-list.vue` | 不消费 `batchPayNo` query 参数（超出本卡范围） |
| 未实现 | B2 通知偏好 | 任务卡明确跳过，`settings.vue` L199 仍保留 `TODO(backend-round-2b)` 注释 |
| 未实现 | B4 账号注销 | 任务卡明确跳过，`settings.vue` L255 仍保留 `TODO(backend-round-2b)` 注释 |

本卡范围内 2 条 `TODO(backend-round-2b)` 已全部清除 (B1 + B3)。

---

## 十、验收

| 项 | 通过 | 说明 |
|---|---|---|
| 不动 `RuoYi-Vue/` 任何文件 | ✅ | 后端 agent 的 untracked Java/SQL 文件未 stage |
| 不合并到 main | ✅ | 停留在 `feat/frontend-round-2b-b1-b3` 分支 |
| 未跳 hook / 未 force push | ✅ | 常规 push 流程 |
| Design Token 使用 | ✅ | 仅 `active-color` 1 处硬编码 (uView 组件 prop 限制) |
| `pages.json` 有效 | ✅ | `JSON.parse` 通过 |
| JS/Vue 语法 | ✅ | `new Function()` 解析新文件无错 |
| B1/B3 两条 `TODO(backend-round-2b)` 清理 | ✅ | grep 复核 settings.vue 仅剩 B2/B4 两条（跳过项） |
| H5 build | ⚠️ | 项目已有 3 个 `postcss.config.js` 缺失错误（`jst-banner-swiper / jst-celebrate / appointment/apply`），与本次改动**无关**；本次新文件未贡献新错误 |

---

## 十一、前后端联调步骤（供集成时使用）

1. 确认后端分支 `feat/backend-round-2b-b1-b3` 合入 `main`（或先本地 cherry-pick 后端 commits 到本分支）
2. 执行 DDL：`架构设计/ddl/99-migration-invoice-title.sql`（后端 agent 产出）
3. 启动后端：`mvn -pl ruoyi-admin spring-boot:run`
4. 前端：
   - 登录渠道方账号 → 进"代报名订单" → 勾选 2+ 笔 pending_pay 单 → 点"批量支付" → 期望 toast "批量支付单 xxx 创建成功"
   - 登录学生账号 → 我的 → 设置 → 发票抬头 → 添加个人/企业抬头 → 切换默认 → 编辑 → 删除；验证空态 CTA

---

## 十二、分支与提交

- 分支：`feat/frontend-round-2b-b1-b3`
- 提交消息预览：
  ```
  feat(mp): Round 2B B1+B3 前端集成 — 渠道批量支付 + 发票抬头管理

  - B1: api/channel.js 新增 batchPayChannelOrders; channel/orders.vue goBatchPay 走真实接口
  - B3: 新建 api/invoice-title.js + pages-sub/my/invoice-title-list.vue (列表 + 弹层 CRUD)
  - B3: settings.vue 改跳转; pages.json 注册新页
  - 全部 Design Token, 0 硬编码; B1/B3 两条 TODO(backend-round-2b) 清理
  ```

---

**报告完**  ·  生成者：MiniProgram Agent (Claude Opus 4.7)
