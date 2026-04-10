# UI 反馈: 临时档案合并标注字段需求

**来源**: E1-CH-7 Part D (Q-03 决策)
**前端页面**: `pages-sub/channel/order-detail.vue` + `pages-sub/my/order-detail.vue`
**日期**: 2026-04-10

## 需求

订单详情接口返回的 VO 中需包含 `participantSnapshot.originalParticipantName` 字段。

### 场景
当临时档案被正式用户认领合并后，后端应在订单的 `participantSnapshot` JSON 中回写 `originalParticipantName`（原临时档案姓名），前端据此在订单详情顶部显示标注：

> 📎 此订单原以"张小明（临时档案）"名义报名

### 涉及接口
- `GET /jst/wx/channel/orders/{id}` → `OrderDetailVO.participantSnapshot`
- `GET /jst/wx/order/{id}` → `OrderDetailVO.participantSnapshot`

### 前端处理
- 若 `participantSnapshot.originalParticipantName` 存在且非空 → 显示标注
- 若字段不存在或为空 → 不显示（正常订单）

### 建议后端实现
在 `participant_user_map` 合并认领逻辑中，更新关联订单的 `participantSnapshot` JSON，追加 `originalParticipantName` 字段。
