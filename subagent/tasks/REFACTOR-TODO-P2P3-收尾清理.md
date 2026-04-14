# REFACTOR-TODO-P2P3 — 收尾清理（P2+P3 合并卡）

> 优先级：P2-P3 | 预估：S | Agent：Backend Agent + MiniProgram Agent（分阶段）
> 依赖：无（全部独立）

---

## 一、背景

傻瓜化重构 12 卡完成后的收尾清理项，均不阻塞系统运行，但提升完整性。

## 二、Backend 部分（Backend Agent）

### 2.1 评审老师自动过滤（P2）

**需求**：评审老师登录后，`GET /jst/partner/enroll/list` 自动只返回其负责赛事的报名。

**改动**：
- `EnrollRecordServiceImpl.selectAdminList()` 或 `PartnerEnrollController.list()`
- 获取当前登录用户 userId → 查 `jst_contest_reviewer WHERE user_id=?` 获取负责的 contestId 列表
- 如果用户是评审老师（reviewer 表有记录），自动追加 `WHERE contest_id IN (...)` 条件
- 如果用户是 partner 本身（非评审老师），保持现有逻辑（PartnerScope 切面）

### 2.2 Redis Key 登记（P3）

在 `架构设计/15-Redis-Key与锁规约.md` 补充：
```
lock:enroll:team:{userId}:{contestId}  — 团队报名并发锁（wait 5s / lease 10s）
```

### 2.3 团队报名 participantId 关联（P3）

当前团队报名按 name/phone 存入 formSnapshot，未关联 `jst_participant`。
- 报名时通过 phone 查询是否已有参赛档案
- 有则关联 participantId
- 无则自动创建临时档案（复用 E1-CH-7 批量报名的 createTempParticipant 逻辑）

## 三、MiniProgram 部分（MiniProgram Agent）

### 3.1 旧组件清理（P3）

- 全局搜索 `jst-form-render` 的引用
- 如果已完全被 `jst-dynamic-form` 替代，删除 `components/jst-form-render/` 目录
- 如果仍有页面引用，逐个替换为 `jst-dynamic-form`

### 3.2 微信邀请自动加入（P2）

当前团队报名分享仅传递 contestId，被邀请者需手动进入页面填写信息。

增强方案（如后端支持）：
- 分享链接带 `teamId` 参数
- 被邀请者打开小程序 → 自动跳转团队页 → 显示"xxx 邀请你加入团队" → 填写自己信息 → 确认加入
- 需后端新增：`POST /jst/wx/enroll/team/{teamId}/join` 接口

**如后端未提供 join 接口，本项跳过。**

## 四、DoD

- [ ] 评审老师自动过滤逻辑
- [ ] Redis Key 文档补充
- [ ] 团队报名 participantId 关联（如可行）
- [ ] 旧 jst-form-render 组件清理
- [ ] 微信邀请增强（如后端支持）
- [ ] `mvn compile` + HBuilderX 编译通过
- [ ] 报告交付
