# 任务卡 PARTNER-WRAPPER - 赛事方独立接口包装层

## 阶段
阶段 F / **jst-event**

## 背景
E2-PA-3/PA-4 前端目前桥接 F7 admin 路由（`/jst/event/contest/*` + `/jst/event/enroll/*`），但这意味着：
1. 赛事方用的是 admin 权限点（可能越权看到别人的赛事）
2. `@PartnerDataScope` 无法注入（admin 接口没标注）
3. 接口签名与前端 `api/partner/contest.js` 语义不匹配

本卡在 `jst-event` 新建 `controller/partner/` 包装层，走 PartnerDataScope 做数据隔离，前端只需改 URL 即可切换。

## PRD 依据
- Q-08 赛事方数据完全隔离
- `架构设计/35-PartnerScope切面使用说明.md`

## 交付物

### Part A — 赛事 CRUD wrapper
**新建**：`jst-event/.../controller/partner/PartnerContestController.java`

继承 `BasePartnerController`，路由 `/jst/partner/contest/*`：

| 方法 | 路径 | 说明 | 内部复用 |
|---|---|---|---|
| `GET /list` | 赛事列表 | `@PartnerDataScope(deptAlias="c")` | 复用 `ContestServiceImpl.selectContestList` 或新建 partner 版 |
| `GET /{id}` | 赛事详情 | 校验 partnerId 归属 | 复用 `ContestServiceImpl.selectContestById` |
| `POST` | 新建赛事 | 自动填充 `partnerId` | 复用 `ContestServiceImpl.addContest` |
| `PUT /{id}` | 编辑赛事 | 校验 partnerId 归属 | 复用 `ContestServiceImpl.editContest` |
| `PUT /{id}/submit` | 提交审核 | 校验 partnerId + 状态 draft/rejected | 复用 `ContestServiceImpl.submitContest` |
| `PUT /{id}/offline` | 下线赛事 | 校验 partnerId + 状态 online | 复用 `ContestServiceImpl.offlineContest` |
| `DELETE /{id}` | 删除草稿 | 校验 partnerId + 状态 draft | 复用删除逻辑 |

**关键**：每个方法都加 `@PreAuthorize("@ss.hasRole('jst_partner')")`，详情/写操作校验 `contest.partnerId == currentPartnerId`。

**列表 SQL**：在现有 `ContestMapperExt.xml` 的 `selectContestList` 末尾已有 `${params.dataScope}` 占位（DEBT-3/PA-9 已预留），`@PartnerDataScope(deptAlias="c")` 自动注入 `AND c.partner_id = ?`。

### Part B — 报名审核 wrapper
**新建**：`jst-event/.../controller/partner/PartnerEnrollController.java`

路由 `/jst/partner/enroll/*`：

| 方法 | 路径 | 说明 |
|---|---|---|
| `GET /list` | 赛事方的报名列表 | `@PartnerDataScope` 按 partner_id 过滤关联赛事 |
| `GET /{id}` | 报名详情 | 校验归属 |
| `PUT /{id}/audit` | 单条审核 | 校验归属 + 状态 |
| `PUT /batch-audit` | 批量审核 | 事务化，替代前端 Promise.all fan-out |

批量审核实现：
```java
@Transactional(rollbackFor = Exception.class)
public int batchAudit(List<Long> enrollIds, String auditStatus, String remark) {
    int count = 0;
    for (Long id : enrollIds) {
        // 逐条校验归属 + 审核
        count += auditSingle(id, auditStatus, remark);
    }
    return count;
}
```

### Part C — 表单模板 wrapper
**新建**：`jst-event/.../controller/partner/PartnerFormTemplateController.java`

路由 `/jst/partner/form-template/*`：

| 方法 | 路径 | 说明 |
|---|---|---|
| `GET /list` | 赛事方可用的表单模板列表 | 含公共模板 + 自己创建的 |

（PA-3 前端 `listPartnerFormTemplates` 已封装此路由）

### Part D — ContestSaveReqDTO 补字段（UI 反馈 partner-contest）
**修改**：`ContestSaveReqDTO.java` + `ContestDetailVO.java` + `ContestMapperExt.xml`

根据 `subagent/ui-feedback/2026-04-10-partner-contest-字段需求.md`，补齐以下字段的 DTO/VO/SQL 映射：

| 字段 | 说明 |
|---|---|
| `allowAppointmentRefund` | Q-07 预约退款开关（DTO+VO+XML insert/update） |
| `eventStartTime` / `eventEndTime` | 列表 VO 补充 |
| `enrollCount` | 列表 VO 补充（子查询聚合） |
| `auditRemark` | 列表/详情 VO 补充 |

其他高级字段（主办方/协办方/场地/详情图集等）**本卡不做**，留到 Web 后台阶段。

### Part E — 前端 URL 切换
**修改**：`ruoyi-ui/src/api/partner/contest.js`

将所有桥接 URL 改为 partner wrapper：
```js
// 改前
url: '/jst/event/contest/list'
// 改后
url: '/jst/partner/contest/list'
```

同理 `api/partner/enroll.js`：
```js
// 改前
url: '/jst/event/enroll/list'
// 改后
url: '/jst/partner/enroll/list'
```

### Part F — 测试
`test/admin-tests.http` 追加：
- `PW-1` partner 登录
- `PW-2` GET /jst/partner/contest/list → 仅返回自己的赛事
- `PW-3` GET /jst/partner/contest/{id} → 详情
- `PW-4` PUT /jst/partner/contest/{id}/submit → 提审
- `PW-5` GET /jst/partner/enroll/list → 仅返回自己赛事的报名
- `PW-6` PUT /jst/partner/enroll/batch-audit → 批量审核
- `PW-7` partner B 访问 partner A 的赛事 → 403/空

## DoD
- [ ] PartnerContestController 7 方法
- [ ] PartnerEnrollController 4 方法（含 batch-audit 事务化）
- [ ] PartnerFormTemplateController 1 方法
- [ ] ContestSaveReqDTO/VO/XML 补 allowAppointmentRefund + 3 列表字段
- [ ] 前端 URL 切换
- [ ] @PartnerDataScope 数据隔离验证
- [ ] mvn compile SUCCESS + npm build SUCCESS
- [ ] 测试段 7 段
- [ ] 任务报告 `PARTNER-WRAPPER-回答.md`
- [ ] commit: `feat(jst-event+web): PARTNER-WRAPPER 赛事方接口包装层`

## 不许做
- ❌ 不许改 F7 admin Controller（平台管理端仍用原路由）
- ❌ 不许改小程序前端（jst-uniapp）
- ❌ 不许在 wrapper 里重写业务逻辑（复用 Service 层）
- ❌ 不许跳过 partnerId 归属校验

## 依赖：PA-9 ✅
## 优先级：P1

---
派发时间：2026-04-10
