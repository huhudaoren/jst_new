# POLISH-PICKER-APPLY 任务报告

> 执行时间：2026-04-18
> 执行人：Web Admin Agent
> 任务卡：`subagent/tasks/POLISH-PICKER-APPLY.md`
> 工作区：`D:\coding\jst_v1`
> 代码主目录：`D:\coding\jst_v1\RuoYi-Vue`

---

## 一、任务目标与本次边界

本卡是 `plan-06` 的收尾工作，目标是把管理端剩余白名单里的 `_id` 输入框继续从原始 `el-input / el-input-number` 改成对应的 Picker。

本次严格遵守以下边界：

1. 只改编辑态和筛选区的 `_id` 输入框。
2. 详情展示区的 `_id` 展示位不动，包括：
   - `el-descriptions-item`
   - `el-table-column`
3. `views/system/`、`views/monitor/`、`views/tool/` 一律不动。
4. 每批 5-8 个文件独立 commit。
5. 先做 Step 1 扫描 review，review 通过后再落代码。
6. 对于仓库里没有对应 Picker 的字段，按任务卡要求先跳过并报告，不硬改。

本次依赖前置卡 `POLISH-PICKER-TEMPLATE-FAMILY` 已完成，以下 4 个 Picker 已可直接使用：

1. `FormTemplatePicker`
2. `CertTemplatePicker`
3. `CouponTemplatePicker`
4. `RightsTemplatePicker`

---

## 二、Step 1 扫描与 review 结论

### 2.1 扫描范围

本次按任务卡要求扫描：

1. `RuoYi-Vue/ruoyi-ui/src/views/jst`
2. `RuoYi-Vue/ruoyi-ui/src/views/partner`
3. `RuoYi-Vue/ruoyi-ui/src/views/sales`

并且只统计白名单里的 `_id` 输入框，不把详情展示位和若依系统页算进本卡。

### 2.2 初始 review 结论

Step 1 review 的结论如下：

1. 初始命中原始 `_id` 输入框 26 处，分布在 13 个文件。
2. 其中 25 处存在对应 Picker，可直接改造。
3. 1 处是 `courseId`，仓库当前没有 `CoursePicker`，按任务卡要求暂不改。

### 2.3 初始阻塞项

| 文件 | 字段 | 结论 |
|---|---|---|
| `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_course_learn_record/index.vue` | `form.courseId` | 当前无 `CoursePicker`，本卡跳过并保留为阻塞项 |

---

## 三、实际交付内容

### 3.1 批次 1

提交：

- commit：`a1babdd`
- message：`feat(admin): POLISH-PICKER-APPLY 批次 1 — contest/enroll/participant/partner 筛选与表单改 Picker`

涉及文件：

1. `RuoYi-Vue/ruoyi-ui/src/views/jst/contest/edit.vue`
2. `RuoYi-Vue/ruoyi-ui/src/views/jst/enroll/index.vue`
3. `RuoYi-Vue/ruoyi-ui/src/views/jst/participant/index.vue`
4. `RuoYi-Vue/ruoyi-ui/src/views/jst/organizer/jst_event_partner_apply/index.vue`
5. `RuoYi-Vue/ruoyi-ui/src/views/partner/enroll-manage.vue`
6. `RuoYi-Vue/ruoyi-ui/src/views/partner/settlement.vue`

本批完成点：

1. 把赛事、报名、参赛人认领、赛事方申请、赛事方报名审核、赛事方结算页中的白名单 `_id` 输入框切成对应 Picker。
2. 保留原有查询参数和表单字段名，不改后端接口契约。
3. 只替换输入组件，不扩大页面结构改动面。

### 3.2 批次 2

提交：

- commit：`878deea`
- message：`feat(admin): POLISH-PICKER-APPLY 批次 2 — scaffold/marketing 剩余 _id 改 Picker`

涉及文件：

1. `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_contest/index.vue`
2. `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_enroll_record/index.vue`
3. `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_cert_record/index.vue`
4. `RuoYi-Vue/ruoyi-ui/src/views/jst/marketing/jst_coupon_issue_batch/index.vue`
5. `RuoYi-Vue/ruoyi-ui/src/views/jst/marketing/jst_user_coupon/index.vue`
6. `RuoYi-Vue/ruoyi-ui/src/views/jst/marketing/jst_user_rights/index.vue`

本批完成点：

1. 赛事、报名记录、证书记录、优惠券批次、用户优惠券、用户权益中的筛选区和编辑态 `_id` 输入框切成 Picker。
2. 接上前置卡新增的 4 个模板类 Picker。
3. 保持详情展示位原样不动，未越权去改 Task 3 的内容。

---

## 四、字段替换清单

### 4.1 已完成替换

| 页面 | 字段 | 替换结果 |
|---|---|---|
| `jst/contest/edit.vue` | `form.formTemplateId` | `FormTemplatePicker` |
| `jst/enroll/index.vue` | `queryParams.contestId` | `ContestPicker` |
| `jst/participant/index.vue` | `claimForm.userId` | `UserPicker` |
| `jst/organizer/jst_event_partner_apply/index.vue` | `queryParams.partnerId` | `PartnerPicker` |
| `partner/enroll-manage.vue` | `queryParams.contestId` | `ContestPicker` |
| `partner/settlement.vue` | `queryParams.contestId` | `ContestPicker` |
| `jst/event/jst_contest/index.vue` | `queryParams.partnerId` | `PartnerPicker` |
| `jst/event/jst_contest/index.vue` | `queryParams.formTemplateId` | `FormTemplatePicker` |
| `jst/event/jst_contest/index.vue` | `form.formTemplateId` | `FormTemplatePicker` |
| `jst/event/jst_enroll_record/index.vue` | `queryParams.contestId` | `ContestPicker` |
| `jst/event/jst_enroll_record/index.vue` | `queryParams.userId` | `UserPicker` |
| `jst/event/jst_enroll_record/index.vue` | `queryParams.participantId` | `ParticipantPicker` |
| `jst/event/jst_enroll_record/index.vue` | `queryParams.channelId` | `ChannelPicker` |
| `jst/event/jst_enroll_record/index.vue` | `queryParams.templateId` | `FormTemplatePicker` |
| `jst/event/jst_enroll_record/index.vue` | `queryParams.orderId` | `OrderPicker` |
| `jst/event/jst_enroll_record/index.vue` | `form.contestId` | `ContestPicker` |
| `jst/event/jst_enroll_record/index.vue` | `form.userId` | `UserPicker` |
| `jst/event/jst_enroll_record/index.vue` | `form.participantId` | `ParticipantPicker` |
| `jst/event/jst_enroll_record/index.vue` | `form.channelId` | `ChannelPicker` |
| `jst/event/jst_enroll_record/index.vue` | `form.templateId` | `FormTemplatePicker` |
| `jst/event/jst_enroll_record/index.vue` | `form.orderId` | `OrderPicker` |
| `jst/event/jst_cert_record/index.vue` | `form.templateId` | `CertTemplatePicker` |
| `jst/marketing/jst_coupon_issue_batch/index.vue` | `queryParams.couponTemplateId` | `CouponTemplatePicker` |
| `jst/marketing/jst_user_coupon/index.vue` | `queryParams.couponTemplateId` | `CouponTemplatePicker` |
| `jst/marketing/jst_user_rights/index.vue` | `queryParams.rightsTemplateId` | `RightsTemplatePicker` |

### 4.2 明确未改

| 文件 | 字段 | 原因 |
|---|---|---|
| `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_course_learn_record/index.vue` | `form.courseId` | 当前无 `CoursePicker` |
| 详情展示区相关页 | 各类 `*_id` 展示位 | 任务卡要求不动，留给 `MAPPER-NAME-JOIN` |
| `views/system/` / `views/monitor/` / `views/tool/` | 任意 `_id` 输入框 | 任务卡明确排除 |

---

## 五、构建验证

执行目录：

- `D:\coding\jst_v1\RuoYi-Vue\ruoyi-ui`

执行命令：

```bash
npm run build:prod
```

结果：

1. 构建成功，`dist` 产物正常生成。
2. 最终输出为 `DONE  Build complete. The dist directory is ready to be deployed.`
3. 有 2 条 webpack 体积 warning：
   - asset size limit
   - entrypoint size limit
4. 这 2 条 warning 为现有体积提示，不是本卡新增编译错误。

结论：

- `npm run build:prod` 已通过。

---

## 六、改后白名单回归扫描

改动完成后，对 `src/views/jst`、`src/views/partner`、`src/views/sales` 再次做白名单 `_id` 原始输入框扫描。

结果只剩 1 处：

| 文件 | 行号 | 字段 | 结论 |
|---|---:|---|---|
| `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_course_learn_record/index.vue` | 175 | `form.courseId` | 阻塞项，等待 `CoursePicker` |

结论：

1. 本卡白名单内可改项已清完。
2. 剩余项与 Step 1 review 一致，没有新增漏网项。

---

## 七、冒烟验证

## 7.1 已完成的冒烟证据

### 场景 A：筛选区 Picker

页面：

- `/partner/enroll-manage`

验证方式：

1. 启动本地后端 `8080` 与前端 dev server `81`。
2. 用 Playwright 打开真实页面。
3. 在“赛事 ID”筛选位选择 `ContestPicker` 第一项。
4. 触发搜索并抓浏览器真实请求。

实测结果：

```json
{"scenario":"filter","page":"/partner/enroll-manage","contestId":"8805","displayed":"星光艺术节 · 少儿钢琴独奏邀请赛"}
```

结论：

1. Picker 能正常拉取实体列表。
2. 选中后查询请求确实带上了 `contestId=8805`。
3. 筛选区场景可视为通过。

### 场景 B：新建 / 编辑对话框内 Picker 绑定

页面：

- `/group-event/event/cert-record`

验证方式：

1. 打开“新增证书记录”对话框。
2. 依次在 3 个 Picker 中选择第一项：
   - 证书模板
   - 赛事
   - 参赛人
3. 直接在页面运行时读取 `form` 和 `validate()` 结果。

实测结果：

```json
{
  "selected": [
    { "id": 97051, "name": "PA6_PARTNER_TEMPLATE" },
    { "id": 8805, "name": "星光艺术节 · 少儿钢琴独奏邀请赛" },
    { "id": 3606, "name": "FIX2_HTTP_A_UPDATED" }
  ],
  "formSnapshot": {
    "certNo": "DIAG-1776519867656",
    "contestId": 8805,
    "enrollId": 1,
    "participantId": 3606,
    "templateId": 97051,
    "issueStatus": "pending",
    "remark": "diag"
  },
  "validateResult": true,
  "errors": []
}
```

结论：

1. 新建态 3 个 Picker 的值已经正确写入页面表单模型。
2. 表单校验通过，不存在“Picker 选了但 form 仍为空”的问题。
3. 编辑态再次读取已打开对话框里的 Picker，可拿到已回填的名称/值，说明编辑态 hydration 正常。

## 7.2 本地 dev 联调中的阻塞

虽然 Picker 值绑定和表单校验都正常，但在本地 dev server 场景下，点击提交后的浏览器 POST 遇到了环境级拦截：

```text
REQ  POST http://127.0.0.1:81/dev-api/jst/event/jst_cert_record
RESP 403 http://127.0.0.1:81/dev-api/jst/event/jst_cert_record Invalid CORS request
```

同样的问题在 `localhost:81` 下也能复现：

```text
RESP 403 http://localhost:81/dev-api/jst/event/jst_cert_record Invalid CORS request
```

这说明：

1. 不是 Picker 没选上。
2. 不是表单校验不过。
3. 是本地 `dev-api` POST 链路存在既有 CORS/代理问题，导致浏览器端新增 / 编辑无法在本地 dev server 下完整闭环。

## 7.3 后端 API 补充验证

为了确认不是后端接口本身坏掉，额外做了直接 API 验证。

已验证：

1. `POST http://127.0.0.1:8080/jst/event/jst_cert_record`
2. 使用等价 payload 时返回：

```json
{"msg":"操作成功","code":200}
```

并且列表页可查到此前 API 烟测生成的记录：

- `SMOKE-1776512510`

### 7.4 冒烟结论

当前可确认：

1. 筛选区 Picker 场景通过。
2. 新建态 / 编辑态 Picker 本身的取数、选中、表单绑定、校验均正常。
3. 但浏览器端“点确定后直达业务 POST/PUT 200”这一闭环，在本地 dev server 下被 `Invalid CORS request` 阻断，未能在本卡内完全消掉。

换句话说：

- 本卡的 Picker 改造没有发现字段绑定层面的功能问题。
- 当前未完全闭环的是本地联调环境的 POST CORS 问题，而不是 Picker 替换本身。

---

## 八、隐患点

### 8.1 `CoursePicker` 缺失

`jst_course_learn_record/index.vue` 的 `courseId` 仍是原始输入框。

影响：

1. 本卡无法做到 100% 全白名单收口。
2. 后续如果继续推“全站 `_id` 全 Picker 化”，这里一定会再次命中。

### 8.2 本地 dev server 的 `/dev-api` POST CORS 问题

这是本次联调里最关键的环境风险。

表现：

1. GET 请求正常。
2. 表单值与校验正常。
3. 浏览器端 POST 到 `/dev-api/jst/event/jst_cert_record` 返回 `403 Invalid CORS request`。

影响：

1. 新建 / 编辑类真实点击冒烟容易被误判成“前端表单坏了”。
2. 后续其他需要 POST/PUT 的页面联调也可能继续撞上。

### 8.3 详情展示位仍然是 ID 文本

这是任务边界内的“有意不改”，不是遗漏。

影响：

1. 用户在详情 / 表格区仍可能看到原始 ID。
2. 这部分体验问题要等 `MAPPER-NAME-JOIN` 统一处理。

---

## 九、TODO 建议

1. 新开一张小卡补 `CoursePicker`，完成 `jst_course_learn_record/index.vue` 的 `courseId` 收尾。
2. 排查本地 dev server 下 `/dev-api` 的 POST CORS 问题。
   - 优先检查前端 dev proxy 与后端 CORS 白名单/Origin 判定。
   - 修完后重新跑一次真实浏览器的新增 / 编辑 smoke。
3. 在 `cert-record` 页面复跑以下最终闭环：
   - 新建：点确定返回 200，并能在列表查到新增记录。
   - 编辑：打开既有记录，确认 Picker 名称回填后提交返回 200。
4. 待 `MAPPER-NAME-JOIN` 落地后，对本卡涉及页面做一次联合回归，确保：
   - 输入位用 Picker
   - 展示位显示名称
   - 详情跳转链路正常

---

## 十、最终结论

从代码交付角度，本卡的主要目标已经完成：

1. 白名单内可改的 25 处 `_id` 输入框已替换为对应 Picker。
2. 2 批文件均已独立 commit。
3. `npm run build:prod` 通过。
4. 改后白名单回归扫描仅剩 `courseId` 阻塞项。

从联调验证角度，本卡现状是：

1. Picker 绑定层面通过。
2. 筛选区真实请求通过。
3. 新建 / 编辑页面的 Picker 取数、选中、表单落值、校验均通过。
4. 但浏览器端 POST/PUT 在本地 dev server 下被现有 CORS 问题拦截，因此“3 场景全部在本地浏览器直达 200”这一条，目前还差环境问题排除后的最后一跳。

如果后续要作为严格验收口径，建议先解决 `Invalid CORS request`，然后基于本报告里的页面和数据直接补跑最终 smoke。
