# 31. API-First 开发流水线（阶段切片模式）

> 决策：采纳「API-First」执行方案，但**按阶段切片**而非全量一次完成
> 与 25 文档 §1 阶段流水线协同：B/C/D/E/F 每个阶段独立闭环

---

## 1. 核心原则

```
              ┌─────────────────┐
              │  本阶段全部接口  │
              │  (15-25 个)     │
              └────────┬────────┘
                       │
        ┌──────────────┼──────────────┐
        │              │              │
        ▼              ▼              ▼
   Backend         Backend         Backend
   Agent A         Agent B         Agent C    ← 多 Agent 并行
        │              │              │
        └──────────────┼──────────────┘
                       │
                       ▼
              ┌─────────────────┐
              │ 主 Agent 审查    │
              │ 编译 + 集成测试  │
              └────────┬────────┘
                       │
                       ▼
              ┌─────────────────┐
              │ 你跑 .http 测试  │  ← 人工 Gate
              │  全部绿色 ✓     │
              └────────┬────────┘
                       │
                       ▼
              ┌─────────────────┐
              │ Frontend Agent  │  ← 该阶段前端启动
              │ 实现页面对接    │
              └─────────────────┘
                       │
                       ▼
                  下一个阶段
```

---

## 2. 阶段定义与接口分配

### 阶段 B - 基线域（25 接口估算）

| Feature | wx 接口 | admin 接口 | 总数 |
|---|---|---|---|
| F1+F2 jst-user 注册登录 | 3 | 0 | ✅ 已完成 |
| F3 临时档案 | 3 wx + 4 admin | — | ✅ 已完成 |
| F4 学生-渠道绑定 | 3 wx + 2 admin | — | 5 |
| F5 赛事方入驻 | 2 wx + 4 admin | — | 6 |
| F6 渠道认证 | 2 wx + 4 admin | — | 6 |
| F7 赛事 CRUD + 双状态机 | 4 wx + 8 admin | — | 12 |
| F8 动态表单模板 | 1 wx + 4 admin | — | 5 |
| 阶段 B 总计 | | | **本阶段约 34 接口** |

**Gate B**：34 接口全部 .http 测试通过 → 启动前端阶段 B

### 阶段 C - 交易主线（20 接口）⭐ 最高风险

| Feature | 接口 |
|---|---|
| F9 报名记录 + form_snapshot | 4 wx |
| 订单创建混合支付 | 3 wx + 4 admin |
| 微信支付 mock + 回调 | 2 wx + 1 admin |
| 退款全流程 | 2 wx + 4 admin |
| 阶段 C 总计 | **20 接口** |

**Gate C** ⭐：必须 CCB 模式审批，每个 feature 单独走，跑过完整资金回退测试

### 阶段 D - 预约/核销 / 商城

| Feature | 接口 |
|---|---|
| 个人/团队预约 | 5 |
| 扫码核销 (Redisson 锁) | 3 |
| 积分账户 + 流水 | 4 |
| 商城商品 + 兑换 | 5 |
| **阶段 D 总计** | **17 接口** |

### 阶段 E - 增值/营销/消息/风控

| Feature | 接口 |
|---|---|
| 优惠券 + 权益 | 8 |
| 公告/消息 | 5 |
| 课程 + VOD | 6 |
| 成绩/证书 | 5 |
| 老师工作台 | 8 |
| 渠道返点 + 提现 | 6 |
| 财务结算 | 5 |
| 公开查询 | 2 |
| **阶段 E 总计** | **45 接口** |

**全部接口估算**：34 + 20 + 17 + 45 ≈ **116 接口**（与 27 文档 60+ wx 不矛盾，加上 admin 端就是这数）

---

## 3. 子 Agent 派发模板（主 Agent 用）

每个子 Agent 派发时，主 Agent 必须给出**完整的任务卡**：

```markdown
# Backend Agent 任务卡 - F4 学生-渠道绑定

## 上下文阅读清单（必读）
- 架构设计/13-技术栈与依赖清单.md
- 架构设计/15-Redis-Key与锁规约.md  (lock:bind:{userId})
- 架构设计/16-安全与敏感字段.md
- 架构设计/11-状态机定义.md (SM-15)
- 架构设计/27-用户端API契约.md §3.1
- ddl/01-jst-user.sql (jst_student_channel_binding 字段)
- jst-user/.../service/impl/ParticipantClaimServiceImpl.java (样板)

## 交付物清单
1. enums/BindingStatus.java (实现 StateMachine 接口,SM-15)
2. domain/JstStudentChannelBinding.java (gen 已生成,需改造)
3. dto/SwitchChannelReqDTO.java (JSR-303)
4. vo/BindingVO.java
5. mapper/JstStudentChannelBindingMapper.java + .xml (含按 user_id 取活跃记录的 SQL)
6. service/BindingService.java + impl (锁+事务+状态机+审计)
7. controller/wx/WxBindingController.java (3 接口 GET my, POST switch, POST unbind)
8. test fixture: 在 99-test-fixtures.sql 追加测试绑定关系
9. test/api-tests.http: 追加 F4 测试块

## 强约束
- 必须 @Transactional(rollbackFor=Exception.class)
- 必须 jstLockTemplate.execute("lock:bind:" + userId, ...)
- 必须 SM-15 跃迁校验
- 必须 @OperateLog 审计
- 禁止直返 Entity,全部 ResVO
- 禁止前端硬编码,fixture 必须入库

## DoD (Definition of Done)
- [ ] mvn compile 通过
- [ ] api-tests.http 中 F4 块全部绿色 ✓
- [ ] 完整 commit message 含 SM/表/锁名引用

## 报告格式
完成后向主 Agent 返回:
- 新增文件路径列表
- 修改文件列表
- 测试结果 (复制 IntelliJ 输出)
- 遗留 TODO (如有)
```

---

## 4. Frontend Agent 启动条件

只有满足**全部**才能启动该阶段的 Frontend Agent：

- [ ] Backend Agent 完成全部接口
- [ ] mvn compile 18 模块全部 SUCCESS
- [ ] api-tests.http 该阶段全部接口测试 ✓ 绿色
- [ ] 接口契约文档（27 文档）已更新（如有变更）
- [ ] OperateLog/Lock/Transaction/状态机自检通过
- [ ] 测试 fixture 已追加

---

## 5. Frontend Agent 任务卡模板

```markdown
# Frontend Agent 任务卡 - 阶段 B Uniapp 实现

## 上下文阅读清单
- 架构设计/26-Uniapp用户端架构.md
- 架构设计/27-用户端API契约.md §3.1-3.2
- 小程序原型图/login.html, my.html, my-binding.html, ...
- 小程序原型图/design-system.css
- 架构设计/30-接口测试指南.md
- jst-uniapp/api/request.js (已存在的请求封装)

## 交付页面 (本阶段 B)
- pages-sub/auth/login.vue       ← login.html
- pages-sub/my/binding.vue       ← binding.html
- pages-sub/teacher/identity.vue ← teacher-identity.html
- pages-sub/teacher/home.vue     ← teacher-home.html (基础版)
- pages/my/index.vue             ← my.html
- pages-sub/auth/profile-edit.vue ← profile-edit.html

## 强约束
- 禁止页面内 mock 数据,全部从 /jst/wx/* 接口拉
- 接口未实现 → 看 27 文档,确认是否真未实现 → 报回主 Agent
- 复用样式从 design-system.css 抽取到 styles/
- HTML 原型只是视觉参考,业务字段以 PRD 为准

## 联调测试
每完成一页,在小程序模拟器或真机测试:
- 登录 → 查我的资料 → 改昵称 → 看到生效
- 绑定老师 → 切换 → 解绑
- 老师工作台首页能加载

## DoD
- [ ] 所有页面真机/模拟器跑通
- [ ] 没有 console.error
- [ ] 接口请求都走 api/request.js 封装
```

---

## 6. 与 25 文档的关系

25 文档定义了**全局开发顺序**和**单 feature 9 步流程**。
本 31 文档定义了**阶段切片 + API-First**的执行节奏。

**两者关系**：
- 25 文档是「How」（怎么写一个 feature）
- 31 文档是「When/Order」（什么顺序写 + 什么时候启动前端）

**冲突处理**：以 31 文档为准（它是 25 的扩展）。

---

## 7. 当前状态

| 阶段 | Backend 进度 | Frontend 进度 | Gate 状态 |
|---|---|---|---|
| **B** | F1+F2+F3 完成 (8/34 接口) | 未启动 | 阻塞 |
| C | 未启动 | 未启动 | 阻塞 |
| D | 未启动 | 未启动 | 阻塞 |
| E | 未启动 | 未启动 | 阻塞 |

**主 Agent 下一动作建议**：派发 F4+F5+F6 三个 Backend Agent 并行（互不依赖），完成后自动进入 F7+F8（依赖 F5/F6 的赛事方和渠道身份）。预计 1 周完成阶段 B 全部接口。

---

## 8. 强制约束（违反退回）

- ❌ 禁止跨阶段抢跑：阶段 C 的接口未在阶段 B Gate 后开发
- ❌ 禁止接口未通过 .http 测试就启动 Frontend Agent
- ❌ 禁止 Frontend Agent 反过来催 Backend 改接口（应反馈到主 Agent → 重新派发）
- ❌ 禁止 Backend Agent 私自加新表/改 ddl（必须主 Agent 同意 + 改 ddl/*.sql）
- ❌ 禁止跳过 .http 测试块的追加（DoD 不通过）
