# 任务卡 E1-CH-1 - 渠道认证申请前端（3 页）

## 阶段 / 端
阶段 E 批 1 / **jst-uniapp**

## 背景
PRD §4.2 定义渠道方有 3 种认证类型：**老师 / 机构 / 个人**。后端 F6 已完成，E0-1 已补驳回限制和初始权益发放。但小程序**完全没有认证前端**（CLAUDE.md §四 P6 标注的"教师认证"是错录）。

## 前置条件
- [ ] E0-1 后端接口已合并（认证 4 个接口 + DDL）
- [ ] 主 Agent 确认

## PRD 依据
- §4.2 渠道方 3 类认证
- §5.2 渠道绑定与代报名闭环
- §7.2 "渠道认证类型仅保留：老师/机构/个人"
- 原型：`channel-identity.html`

## CCB 决策
- Q-02：驳回 ≤3 次后锁定，前端禁用重提按钮

## 交付物

### 1. 新建 3 个页面

#### 页面 A：`jst-uniapp/pages-sub/channel/apply-entry.vue`
认证入口 + 类型选择：
- 顶部说明卡：渠道方权益介绍（场地减免/专属课程/客服特权）
- 3 个选择卡：老师 / 机构 / 个人（点击跳 apply-form 并 query 带 type）
- 若已有申请记录（getMyApply 返回非空）→ 跳 apply-status
- 权限：登录用户可访问（未登录跳 login）

#### 页面 B：`jst-uniapp/pages-sub/channel/apply-form.vue`
材料填写表单，按 channelType 分支：

**公共字段**：
- 真实姓名 `@NotBlank`
- 手机号 `@NotBlank + @Pattern`
- 身份证号（密文，使用 JstEncryptUtils 兼容显示）

**teacher 分支额外字段**：
- 任教学校名称
- 教师资格证照片（OSS 上传）
- 职业证明照片（OSS 上传，可选）

**organization 分支额外字段**：
- 机构名称
- 营业执照号
- 营业执照照片（OSS 上传）
- 法人姓名

**individual 分支额外字段**：
- 身份证正面照片（OSS 上传）
- 身份证反面照片（OSS 上传）
- 个人简介说明（textarea 500 字）

底部：
- 同意《渠道方服务协议》checkbox
- 提交按钮 → `POST /channel/auth/apply` 或 `POST /channel/auth/resubmit/{id}`（看 query 带 id 与否）
- 成功后 `redirectTo` apply-status

#### 页面 C：`jst-uniapp/pages-sub/channel/apply-status.vue`
状态与历史：
- Hero 区：大图标 + 状态文案
  - `pending` 审核中 ⏳
  - `approved` 已通过 ✅
  - `rejected` 已驳回 ❌（显示 `reject_reason`）
  - `locked_for_manual` 🔒 已锁定，请联系客服
- 材料摘要（展示已提交信息，脱敏手机号/身份证）
- 操作区：
  - pending → 撤回申请按钮
  - rejected 且 `rejectCount < 3` → "修改重新提交"按钮跳 apply-form
  - rejected 且 `rejectCount >= 3` → 禁用 + "请联系客服"提示
  - approved → "进入渠道工作台"按钮 → `/pages-sub/channel/home`
- 下拉刷新支持

### 2. API 封装
**修改**：`jst-uniapp/api/channel.js`

追加：
```js
export function getMyChannelApply() { return request({ url: '/jst/wx/channel/auth/my', method: 'GET' }) }
export function submitChannelApply(body) { return request({ url: '/jst/wx/channel/auth/apply', method: 'POST', data: body }) }
export function resubmitChannelApply(id, body) { return request({ url: `/jst/wx/channel/auth/resubmit/${id}`, method: 'POST', data: body }) }
export function cancelChannelApply(id) { return request({ url: `/jst/wx/channel/auth/cancel/${id}`, method: 'POST' }) }
```

### 3. 入口接入
**修改**：`jst-uniapp/pages/my/index.vue`

"我的服务" 更多折叠区追加 tile："申请成为渠道方"
- 仅 `!isChannel && !isPartner` 时显示（学生未升级渠道方）
- 点击跳 `/pages-sub/channel/apply-entry`

**修改**：`jst-uniapp/pages.json` 注册 3 个新页面到 pages-sub

### 4. 样式
- 复用 `styles/design-tokens.scss`（若存在）
- 表单控件用 uView 或原生 uni-ui
- 文件上传：复用项目现有 `api/oss.js` 或 `uni.uploadFile` + 签名 URL

## DoD
- [ ] 3 页新建完成
- [ ] api/channel.js 补 4 个方法
- [ ] pages.json 注册
- [ ] my/index 入口 tile
- [ ] 3 种 channelType 字段分支完整
- [ ] 锁定状态 UI 正确禁用
- [ ] HBuilderX H5 预览验证通过
- [ ] 任务报告 `E1-CH-1-回答.md` 含 Before/After 页面截图描述
- [ ] 无 commit（由主 Agent 统一打包）

## 不许做
- ❌ 不许改后端
- ❌ 不许动 pages-sub/channel/ 下的 home/students/orders/rebate/data/withdraw-*（后续卡会改）
- ❌ 不许自己补 OSS 签名 Controller（用项目现成的）
- ❌ 不许跳过驳回锁定 UI

## 依赖
- E0-1 后端 ✅

## 优先级：P1（阻塞后续 CH-2~CH-6）

---
派发时间：2026-04-10
