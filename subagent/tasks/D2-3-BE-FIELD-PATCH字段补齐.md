# 任务卡 D2-3 - 后端字段补齐合集

## 阶段 / 模块
阶段 D 批 2 / **jst-event** + **jst-marketing**

## 背景
POLISH-BATCH2 与 F-MARKETING-RIGHTS-BE 报告中遗留 3 条后端字段/权限修正，合并为单张卡一次处理。

## 交付物

### Part A — AppointmentListVO 核销进度字段
**背景**：POLISH-BATCH2 F 节发现 `AppointmentListVO` 无 `writeoffDoneCount / writeoffTotalCount`，前端只能隐藏核销进度行。补齐后前端另派卡恢复展示。

**修改**：
1. `RuoYi-Vue/jst-event/.../vo/AppointmentListVO.java`
   - 新增 `Integer writeoffDoneCount` / `Integer writeoffTotalCount`
2. 对应 mapper XML 的 list SQL（grep `AppointmentListVO` 定位）
   - 用子查询或 JOIN 从 `jst_appointment_writeoff` 聚合：
     ```sql
     (SELECT COUNT(*) FROM jst_appointment_writeoff aw
       WHERE aw.appointment_id = a.appointment_id AND aw.del_flag='0') AS writeoff_total_count,
     (SELECT COUNT(*) FROM jst_appointment_writeoff aw
       WHERE aw.appointment_id = a.appointment_id AND aw.del_flag='0' AND aw.status='done') AS writeoff_done_count
     ```
   - 团队预约 `jst_team_writeoff` 同理（若 list 接口同时覆盖）
3. 确认 Service 层无需改动（VO 字段走 mapper 自动映射）

### Part B — RightsWriteoffApplyDTO 次数字段
**背景**：F-MARKETING-RIGHTS-BE 报告 §H 风险 3，当前 DTO 只有 `writeoffAmount`，次数模式复用该字段不语义清晰。本卡新增 `writeoffCount` 字段，服务层按 `quotaMode` 分流。

**修改**：
1. `RuoYi-Vue/jst-marketing/.../dto/RightsWriteoffApplyDTO.java`
   - 新增 `Integer writeoffCount`
2. `RightsUserServiceImpl.applyWriteoff`
   - 根据 `UserRights.quotaMode`：
     - `amount` → 使用 `writeoffAmount`，扣 `remainQuota -= writeoffAmount`
     - `count` → 使用 `writeoffCount`（缺省为 1），扣 `remainQuota -= writeoffCount`
   - 校验：对应模式的字段必填，另一模式字段传了也忽略
3. 写入 `jst_rights_writeoff_record` 时：
   - `amount` 模式 → `use_amount = writeoffAmount`
   - `count` 模式 → `use_amount = writeoffCount`（字段复用，前端已适配）

### Part C — WxCampaignController 权限注解修正
**背景**：DEBT-3 H 项发现 `WxCampaignController` 仍用 `jst:marketing:coupon:my` 兜底，语义不符。

**修改**：`RuoYi-Vue/jst-marketing/.../controller/wx/WxCampaignController.java`
- 列表接口：`@PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:campaign:list')")`
- 详情接口：`@PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:campaign:query')")`

DEBT-3 的 95-migration-phase-c-permissions.sql 已经补了这两个权限点到 `jst_student` 角色，无需再动 SQL。

### Part D — 测试
**修改**：`test/wx-tests.http`

- **Part A**：在 `AP-*` 或 appointment list 段回归一次，验证返回含 `writeoffDoneCount/writeoffTotalCount`
- **Part B**：`MR-RT-W*` 段追加次数模式核销用例：
  - POST `/jst/wx/marketing/rights/writeoff/apply` body `{userRightsId, writeoffCount: 2, remark: "..."}`
  - 验证 `remainQuota` 扣减 2
- **Part C**：`MR-CM-W*` 段现有 campaign 用例应仍返回 200（因 jst_student 已有新权限点）

## DoD
- [ ] Part A AppointmentListVO 补字段 + SQL 聚合
- [ ] Part B RightsWriteoffApplyDTO 补 writeoffCount + 服务层分流
- [ ] Part C WxCampaignController 权限注解修正
- [ ] mvn compile SUCCESS
- [ ] wx-tests.http 三段验证通过（或记录失败原因）
- [ ] 任务报告 `D2-3-BE-FIELD-PATCH-回答.md`
- [ ] commit: `feat(jst-event+marketing): D2-3 后端字段补齐合集（核销进度/次数模式/campaign权限）`

## 不许做
- ❌ 不许动 DDL 表结构（仅加 VO/DTO 字段，不加数据库列）
- ❌ 不许改 `RightsUserService` 的 HMAC / 锁逻辑
- ❌ 不许顺手改其他 VO/DTO
- ❌ 不许改前端

## 依赖：无（与 D2-1/D2-2/D2-4 并行）
## 优先级：中

---
派发时间：2026-04-09
