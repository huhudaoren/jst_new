# 任务卡 FIX-2 - 后端接口补齐（含 UI 反馈字段需求）

## 阶段
阶段 E 收尾 / **jst-channel + jst-user + jst-event**

## 背景
Test Agent 发现 5 处 API 404 + 1 处渠道订单详情缺端点。UI Polish Agent 反馈了 4 份字段需求文档（`subagent/ui-feedback/` 目录），合并到本卡一起补齐。

## 交付物

### Part A — 渠道排行榜 2 接口
**文件**：`WxChannelDashboardController.java` + `ChannelSupplementService`

- `GET /jst/wx/channel/dashboard/top-contests?period=&limit=5`
  - 按渠道方绑定学生的报名数聚合 Top N 赛事
  - 返回：`[{ contestId, contestName, category, enrollCount }]`
  - 权限：`@ss.hasRole('jst_channel')`

- `GET /jst/wx/channel/dashboard/top-students?limit=5`
  - 按绑定学生的报名数/支付金额排行
  - 返回：`[{ studentName(脱敏), enrollCount, payAmount }]`

### Part B — 渠道订单详情 1 接口
**文件**：`WxChannelDashboardController.java`

- `GET /jst/wx/channel/orders/{orderId}`
  - 查询单条订单详情（校验 channel_id 归属）
  - 返回费用明细：priceOriginal / couponDiscount / pointsDiscount / userNetPay / platformFee / rebateBase / rebateRate / rebateAmount
  - 归属渠道信息 + 时间轴
  - 字段从 jst_order_main + jst_order_item + jst_rebate_ledger 关联查询
  - 不存在的字段用 null 兜底

### Part C — 临时参赛档案管理 3 接口
**文件**：`WxParticipantController.java`

- `GET /jst/wx/channel/participant/my?status=&pageNum=&pageSize=`
  - 查询当前渠道方创建的临时档案列表
  - 按 status 筛选：unclaimed / claimed / conflict
  - 校验 create_by = currentUserId

- `PUT /jst/wx/channel/participant/{id}`
  - 更新临时档案（仅 unclaimed 允许修改）
  - 校验 create_by + status

- `DELETE /jst/wx/channel/participant/{id}`
  - 软删除（del_flag='2'，仅 unclaimed 允许）

### Part D — 测试
`test/wx-tests.http` 追加 FIX-2-* 段：
- FIX-2-1 GET top-contests
- FIX-2-2 GET top-students
- FIX-2-3 GET orders/{orderId}
- FIX-2-4 GET channel/participant/my
- FIX-2-5 PUT channel/participant/{id}
- FIX-2-6 DELETE channel/participant/{id}

### Part D — UserProfileVO 补字段（UI 反馈 my-index）
**文件**：`jst-user/.../vo/UserProfileVO.java` + `WxUserController.getProfile` + 对应 Mapper

UI Polish Agent 反馈 `pages/my/index.vue` 需要以下字段来渲染完整的"我的"页面：

| 字段 | 类型 | 来源 | 说明 |
|---|---|---|---|
| `enrollCount` | Integer | `SELECT COUNT(*) FROM jst_enroll_record WHERE user_id=?` | 报名记录总数 |
| `scoreCount` | Integer | `SELECT COUNT(*) FROM jst_score_record WHERE user_id=? AND publish_status='published'` | 已发布成绩数 |
| `certCount` | Integer | `SELECT COUNT(*) FROM jst_cert_record WHERE user_id=? AND issue_status='granted'` | 已发证书数 |
| `courseCount` | Integer | `SELECT COUNT(*) FROM jst_user_course WHERE user_id=?` | 在学课程数 |
| `frozenPoints` | Integer | 从 `jst_points_account` 取 `frozen_points` | 冻结积分 |
| `levelName` | String | 从 `jst_points_level` 按 `currentLevelId` 查 `level_name` | 等级名称 |
| `couponCount` | Integer | `SELECT COUNT(*) FROM jst_user_coupon WHERE user_id=? AND status='available'` | 可用优惠券数 |
| `unreadMsgCount` | Integer | `SELECT COUNT(*) FROM jst_message WHERE user_id=? AND read_status=0` | 未读消息数（若 jst_message 表无此列则返回 0） |
| `channelBinding` | Object | 从 `jst_student_channel_binding` 取当前有效绑定的渠道方 `{ name, school, remark }` | 绑定渠道方信息 |

**兼容**：不存在的表/列返回 0 或 null，不报错。

### Part E — 赛事方工作台 3 接口（UI 反馈 partner-dashboard）
**文件**：`jst-event/.../controller/partner/PartnerDashboardController.java`（新建）

- `GET /jst/partner/dashboard/summary` → 返回 `{ pendingEnrollCount, monthEnrollCount, pendingScoreCount, pendingCertificateCount }`
- `GET /jst/partner/dashboard/todo` → 返回 `{ pendingEnrollList(Top5), pendingScoreList(Top5) }`
- `GET /jst/partner/notice/recent` → 返回最近 3 条已发布公告（复用 jst_notice 表，按 publish_time DESC LIMIT 3）

权限：`@PreAuthorize("hasRole('jst_partner')")`
数据隔离：`@PartnerDataScope` 按 partner_id 过滤

### Part F — 订单 VO 补 originalParticipantName（UI 反馈 participant-merge）
**文件**：`OrderDetailVO.java` 或 `participantSnapshot` JSON 构建逻辑

在 `participant_user_map` 合并认领逻辑中，更新关联订单的 `participantSnapshot` JSON 追加 `originalParticipantName` 字段。

若当前合并逻辑尚未实现（E0-1 auto-claim 遗留），本卡只做 VO 字段预留 + 读取兜底（JSON 中有就展示，没有就不展示），不改合并逻辑。

### Part G — 测试
`test/wx-tests.http` 追加：
- FIX-2-7 GET /jst/wx/user/profile → 验证新增 9 字段
- FIX-2-8 GET /jst/partner/dashboard/summary
- FIX-2-9 GET /jst/partner/dashboard/todo
- FIX-2-10 GET /jst/partner/notice/recent

## DoD
- [ ] Part A~C 原有 6 端点
- [ ] Part D UserProfileVO 补 9 字段
- [ ] Part E 赛事方工作台 3 接口
- [ ] Part F 订单 VO 补 originalParticipantName
- [ ] @PreAuthorize + 归属校验
- [ ] mvn compile 18 模块 SUCCESS
- [ ] 测试段追加 10 段
- [ ] 任务报告 `FIX-2-回答.md`
- [ ] commit: `fix(be): FIX-2 后端接口补齐（排行榜/profile/partner-dashboard/临时档案/订单标注）`

## 不许做
- ❌ 不许改前端
- ❌ 不许改已有接口签名（只追加新端点/新字段）
- ❌ 不存在的表/列返回 0 或 null，不报错

## 参考文档
- `subagent/ui-feedback/2026-04-10-my-index-字段需求.md`
- `subagent/ui-feedback/2026-04-10-partner-dashboard-字段需求.md`
- `subagent/ui-feedback/2026-04-10-partner-contest-字段需求.md`
- `subagent/ui-feedback/2026-04-10-participant-merge-字段需求.md`

## 依赖：无（与 FIX-1 并行）
## 优先级：P1（影响"我的"页面第一印象 + 赛事方工作台数据）

---
派发时间：2026-04-10（修订）
