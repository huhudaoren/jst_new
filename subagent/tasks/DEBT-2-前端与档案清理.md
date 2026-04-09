# 任务卡 DEBT-2 - 前端占位清理 + profile 字段后端白名单

## 阶段 / 端
代码债 / **jst-uniapp** + **RuoYi-Vue/jst-user**

## 背景

WX-C2 子 Agent 审计发现两条历史债：
1. `pages/my/index.vue` 有 7 个旧的 `showComingSoon(...)` 占位 tile 与新版真实导航项并列，用户点到占位会看到"待开放"toast。
2. `pages-sub/my/profile-edit.vue` 允许编辑 `birthday` / `guardianMobile`，但后端 `WxUserController` 的 profile 更新白名单里没这俩字段，属于静默丢弃。

## 交付物

### Part A — 小程序清理（jst-uniapp）

文件：`jst-uniapp/pages/my/index.vue`

**删除**以下 7 个 `showComingSoon` 占位 tile（大约在"我的服务" grid 内的第 9~15 项）：
- 我的报名（占位）
- 我的订单（占位）
- 我的成绩
- 我的证书
- 优惠券
- 设置
- 以及多余的"我的课程"重复项

**保留**前 8 个真实导航项（我的订单/退款/报名/绑定/课程/资料/档案 + 需要的其他真实项）。

同时如果 `showComingSoon` 方法不再被任何 tile 引用，在 methods 里删除它；如果其他 tile 还在用就保留。

### Part B — 后端 profile 白名单补齐（jst-user）

文件待子 Agent 自查：
- `jst-user/.../controller/wx/WxUserController.java` 的 profile 更新接口
- 对应 DTO（`UserProfileUpdateDTO` 或类似名）
- `jst-user/.../service/impl/...` 的 update 实现
- `jst_user` / `jst_user_profile` 表是否已有 `birthday` / `guardian_mobile` 列（**先查 DDL**，没有就在任务卡产出里加迁移脚本 `架构设计/ddl/98-migration-*.sql`，MySQL 5.7 兼容写法，idempotent）

**交付**：
1. 白名单允许 `birthday`（Date）和 `guardianMobile`（手机号正则校验，允许空）
2. 若字段不存在，产出迁移脚本并跑一遍（测试库 `jdbc:mysql://59.110.53.165:3306/jst_new`，凭据见 CLAUDE.md）
3. `test/wx-tests.http` 的 profile update 段补 2 条用例：修改 birthday / 修改 guardianMobile，再查一次 profile 验证回显

## DoD

- [ ] my/index.vue 无占位 tile，showComingSoon 方法清理或保留（视是否还有引用）
- [ ] profile-edit.vue 提交 birthday/guardianMobile 后，再次 fetchProfile 能读到相同值
- [ ] mvn compile SUCCESS
- [ ] 迁移脚本（如果需要）落盘并在测试库执行
- [ ] 任务报告 `DEBT-2-回答.md`
- [ ] commit: `chore: DEBT-2 前端占位清理 + profile 字段白名单`

## 不许做

- ❌ 不许改动"渠道方工作台" tile 区块（WX-C2 已做，保持不动）
- ❌ 不许重构整个 my/index.vue 的样式或布局
- ❌ 不许在前端加 mock 数据

## 依赖：无
## 优先级：低（可并行）

---
派发时间：2026-04-09
