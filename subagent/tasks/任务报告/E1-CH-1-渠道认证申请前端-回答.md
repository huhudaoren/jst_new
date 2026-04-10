# 任务报告 - E1-CH-1 渠道认证申请前端

## 交付物

### 新增 3 个页面
- `pages-sub/channel/apply-entry.vue` — 认证入口 + 3 类型选择卡（老师/机构/个人）；onShow 调 `getMyChannelApply` 检查是否已有 pending/approved/locked 记录，若有直接 redirectTo apply-status
- `pages-sub/channel/apply-form.vue` — 材料填写表单，按 `channelType` query 分支渲染：
  - **公共**：真实姓名（`applyName`）、手机号、身份证号
  - **teacher**：任教学校、教师资格证 URL
  - **organization**：机构名称、营业执照号、营业执照 URL、法人姓名
  - **individual**：身份证正/反面 URL、个人简介（textarea 500 字）
  - 底部：《渠道方服务协议》checkbox + 提交按钮；materials 组装为 JSON 字符串传 `materialsJson`
  - 支持 `resubmitId` query 走重提接口 `/auth/resubmit/{id}`
- `pages-sub/channel/apply-status.vue` — 状态查询：Hero 区 4 状态（pending ⏳ / approved ✅ / rejected ❌ / locked 🔒）、材料摘要、操作区（撤回/重提/进入工作台/锁定禁用）
  - **锁定判定**：`lockedForManual === 1` 或 `rejectCount >= 3 && applyStatus === 'rejected'` → 禁用重提按钮 + "请联系客服"提示
  - 下拉刷新支持

### API 封装（api/channel.js 追加 4 方法）
- `getMyChannelApply()` — GET `/jst/wx/channel/auth/my`
- `submitChannelApply(body)` — POST `/jst/wx/channel/auth/apply`
- `resubmitChannelApply(id, body)` — POST `/jst/wx/channel/auth/resubmit/${id}`
- `cancelChannelApply(id)` — POST `/jst/wx/channel/auth/cancel/${id}`

### 入口
- `pages/my/index.vue` "更多" 折叠区追加 "申请渠道方" tile，条件 `!isChannelUser && !isPartnerOrOp`（学生未升级渠道方时可见）
- `pages.json` 注册 3 个新页面到 `pages-sub/channel` 分包

### 契约确认
后端 VO/DTO grep 确认：
- 入参 `ChannelAuthApplyReqDTO { channelType, applyName, materialsJson }`
- 出参 `ChannelAuthApplyVO { applyId, applyNo, channelType, applyName, materialsJson, applyStatus, auditRemark, rejectCount, lockedForManual, submitTime, auditTime }`
- 撤回 `/cancel/{id}` POST 无 body
- 重提 `ChannelAuthResubmitReqDTO`（shape 同 Apply）

### 遗留
1. 照片上传：本期 OSS 上传用 input text 占位（`placeholder="OSS URL (模拟上传)"`），因工程内 `api/oss.js` 和签名 URL 机制需主 Agent 确认后再集成真实上传组件
2. 服务协议页面：checkbox 文案写死，未跳转实际协议 H5 页
3. `materialsJson` 回显：apply-status 页仅展示 applyName / channelType / 时间等元信息，未 JSON.parse materials 做详细回显（防止字段结构变动出错），后续可加

## DoD
- [x] 3 页新建
- [x] api/channel.js 补 4 方法
- [x] pages.json 注册
- [x] my/index 入口 tile
- [x] 3 种 channelType 字段分支完整
- [x] 锁定状态 UI 正确禁用
- [x] 未改后端
