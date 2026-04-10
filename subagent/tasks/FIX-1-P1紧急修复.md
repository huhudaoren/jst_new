# 任务卡 FIX-1 - P1 紧急修复（阻塞赛事方闭环）

## 阶段
阶段 E 收尾 / **后端 SQL + 前端 api URL**

## 背景
Test Agent Layer 2 发现赛事方端到端链路 E 🔴 失败。根因是 2 个 P1 阻塞 + 1 个 P2 路径不一致。本卡必须最先完成。

## 交付物

### Part A — 赛事方权限补齐（P1 阻塞）
**问题**：H4 赛事方提交审核返回 403，partner 角色缺 `jst:event:contest:submit` 权限

**修改**：`架构设计/ddl/99-migration-partner-menus.sql`

追加以下按钮权限到 `sys_menu`（若不存在）+ 绑定到 `jst_partner` 的 `sys_role_menu`：

```sql
-- 赛事提交审核（F7 已有接口，但缺权限菜单）
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (9715, '赛事提交审核', 9702, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:event:contest:submit', '#', 'migration', NOW());

-- 同时确保 partner 绑定了 F7 生成器产出的 jst:event:contest:* 权限
-- 用 96-grant 的模式补全
```

同时检查 `96-grant-jst-partner-permissions.sql` 是否已覆盖 `jst:event:contest:submit`。若未覆盖，追加。

**测试库执行验证**：跑完后用 partner 账号重试 H4 → 预期 200。

### Part B — 赛事结算 fixture 导入（P1 阻塞）
**问题**：PA7-A2~A5 全失败，fixture 数据 94701-94705 未在测试库

**操作**：
1. 检查 `99-test-fixtures.sql` 中是否有 `jst_event_settlement` 相关 fixture
2. 若有 → 在测试库执行该段
3. 若无 → 追加 fixture（settlement_id=94701~94705，关联 partner_id + contest_id）
4. 重跑 PA7-A1~A5

### Part C — 团队预约 API URL 修正（P2）
**问题**：前端 `/jst/wx/appointment/team/create` vs 后端 `/jst/wx/team-appointment/apply`

**修改**：`jst-uniapp/api/appointment.js`

```js
// 改前
export function createTeamAppointment(body) {
  return request({ url: '/jst/wx/appointment/team/create', method: 'POST', data: body })
}
export function getTeamAppointmentDetail(id) {
  return request({ url: `/jst/wx/appointment/team/${id}`, method: 'GET' })
}

// 改后
export function createTeamAppointment(body) {
  return request({ url: '/jst/wx/team-appointment/apply', method: 'POST', data: body })
}
export function getTeamAppointmentDetail(id) {
  return request({ url: `/jst/wx/team-appointment/${id}`, method: 'GET' })
}
```

### Part D — 赛事创建缺字段默认值（P2）
**问题**：H3 赛事创建时 `appointmentCapacity` / `allowRepeatAppointment` 为 NOT NULL 无默认值

**修改**：`架构设计/ddl/98-migration-phase-e-prep.sql` 追加：
```sql
ALTER TABLE jst_contest MODIFY COLUMN appointment_capacity INT NOT NULL DEFAULT 0;
ALTER TABLE jst_contest MODIFY COLUMN allow_repeat_appointment TINYINT(1) NOT NULL DEFAULT 0;
```
或在 `JstContest.java` entity 中设默认值。二选一，优先 DDL。

## DoD
- [ ] partner 角色有 contest:submit 权限 → H4 返回 200
- [ ] PA7 fixture 导入 → PA7-A1~A5 至少 A1/A2 通过
- [ ] 团队预约 URL 修正 → 不再 404
- [ ] appointmentCapacity 默认值 → H3 不再缺字段
- [ ] mvn compile SUCCESS
- [ ] 任务报告 `FIX-1-回答.md`
- [ ] commit: `fix: FIX-1 P1 赛事方权限+fixture+团队预约URL+默认值`

## 不许做
- ❌ 不许改已有接口逻辑
- ❌ 不许改前端页面视觉

## 依赖：无
## 优先级：P0（赛事方闭环阻塞）

---
派发时间：2026-04-10
