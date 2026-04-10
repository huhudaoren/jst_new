# 任务卡 E1-CH-2 - 渠道工作台 home + my 渠道视角重构

## 阶段 / 端
阶段 E 批 1 / **jst-uniapp**

## 背景
当前 `pages-sub/channel/home.vue` 是 WX-C2 时期的骨架版，仅有简单 KPI。原型 `channel-home.html` 要求完整工作台：4 格 Hero KPI + 返点横幅 + 功能宫格 + 权益区 + 最近订单。`pages/my/index.vue` 的渠道视角 Tab 也是空壳。

## 前置条件
- [ ] E0-1 后端 ✅
- [ ] E1-CH-1 认证前端 ✅（用户须先完成认证才能进工作台）

## PRD 依据
- §7.2 渠道方工作台
- 原型：`channel-home.html` / `my.html` 渠道 Tab

## 交付物

### 1. 重构 `pages-sub/channel/home.vue`

完全按原型重做，顶向下结构：

#### A. Hero 4 格 KPI（渠道视角）
- 绑定学生数
- 代报名总数
- 待结算返点
- 累计返点
- 数据来源：`GET /channel/dashboard/monthly` + `GET /channel/dashboard/stats`

#### B. 返点横幅
- 可提现余额（突出金色）
- 审核中金额
- 累计已打款
- 点击跳 `channel/rebate`
- 数据来源：`GET /channel/rebate/summary`

#### C. 功能宫格（9 格）
参考原型 channel-home.html：
- 学生管理 → `channel/students`
- 代报名 → `channel/batch-enroll`（CH-7 提供）
- 订单管理 → `channel/orders`
- 返点中心 → `channel/rebate`
- 提现结算 → `channel/withdraw-list`
- 报名数据 → `channel/data`
- 经营分析 → `channel/data`（暂跳 data，后续 F-2 再接 analysis 页）
- 团队预约 → `channel/appointment`（CH-7 提供）
- AI 课程 → `mall/maic-list`（F-2 批次再做）

#### D. 权益区（4 格）
- 场地减免 / 专属课程 / 客服特权 / 申请核销
- 数据来源：`GET /channel/rights/my`（E0-1 已补）
- 每格显示权益名称 + 剩余额度
- 点击展开权益详情或跳核销入口

#### E. 最近订单（3 条）
- 数据来源：`GET /channel/orders?limit=3&sort=createTime desc`
- 卡片展示：学生名 / 赛事名 / 金额 / 状态
- 点击跳 `channel/orders/detail/{orderId}`

### 2. 重构 `pages/my/index.vue` 渠道视角 Tab

#### 视角切换 UI
- 顶部 segmented：[学生] [渠道方]（仅当 `isChannel === true` 时显示第二个）
- 切换时平滑过渡
- 默认视角：若 isChannel，默认渠道方；否则学生

#### 渠道视角下显示（精简版）
- 认证状态卡（已通过/审核中/驳回）→ 点击跳 `channel/apply-status`
- 渠道等级卡 + 成长值进度条 → 点击跳 `points/center`
- "我的工作台"按钮 → 跳 `channel/home`
- 快捷入口 4 格：学生管理 / 订单 / 返点 / 提现
- 财务结算菜单：合同（disable 灰标 F-2）/ 发票（disable 灰标 F-2）

#### 学生视角（保持现有）
- 保留现有 12 项折叠组（阶段 D 批 2 已改好）
- 不动

### 3. API 补充
**修改**：`jst-uniapp/api/channel.js`

追加：
```js
export function getChannelRightsMy() { return request({ url: '/jst/wx/channel/rights/my', method: 'GET' }) }
```

### 4. 适配边界
- 未认证用户访问 `channel/home` → 弹窗"请先申请成为渠道方"→ 跳 apply-entry
- 认证 pending 状态 → Hero 显示占位"审核中，暂无数据"
- 接口失败 → 降级占位，不白屏

## DoD
- [ ] home.vue 5 区块完整
- [ ] my/index 渠道 Tab 完整
- [ ] 视角切换流畅
- [ ] 未认证用户访问拦截
- [ ] HBuilderX 验证
- [ ] 任务报告 `E1-CH-2-回答.md`

## 不许做
- ❌ 不许改后端
- ❌ 不许动学生视角现有内容
- ❌ 不许把合同/发票菜单做成真链接（必须灰标）

## 依赖：E0-1 + E1-CH-1
## 优先级：P1

---
派发时间：2026-04-10
