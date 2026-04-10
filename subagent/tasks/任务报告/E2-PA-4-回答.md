# E2-PA-4 报名审核 - 交付报告

## 1. 任务结论

- 已完成赛事方报名审核页前端实现，交付列表页、详情抽屉、批量审核与移动端适配。
- 已新增任务卡要求的 `partner` 侧 API 封装文件，但考虑到当前 F9 后端尚未提供 `/jst/partner/enroll/*`，本次先桥接到现有 `/jst/event/enroll/*` 路由，保证页面可直接接当前后端。
- 已补充后端缺口反馈内容到本报告，前端代码也已对缺失字段做容错，待后端补齐后无需大改页面结构。
- 已完成 `ruoyi-ui` 生产构建验证，最终结果为 `BUILD SUCCESS`。

## 2. 交付物清单

### 2.1 新增文件

- `RuoYi-Vue/ruoyi-ui/src/views/partner/enroll-manage.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/partner/components/EnrollDetailDrawer.vue`
- `RuoYi-Vue/ruoyi-ui/src/api/partner/enroll.js`

### 2.2 为打通构建额外补的最小壳子文件

- `RuoYi-Vue/ruoyi-ui/src/views/partner-apply/form.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/partner-apply/status.vue`

说明：

- 上述两个 `partner-apply` 文件不是本任务卡直接要求的功能页。
- 之所以补这两个壳子，是因为当前仓库 `src/router/index.js` 已经引用了这两个页面，但文件实际缺失，导致全量 `npm run build:prod` 无法通过。
- 本次采用最小包装方式复用现有 `partner-apply/index.vue`，仅用于解除既有构建阻塞，不改已有业务流程。

## 3. 实现说明

### 3.1 报名列表页

在 `enroll-manage.vue` 中完成了以下能力：

- 状态 Tab：`待审核 / 已通过 / 已驳回 / 全部`
- 搜索筛选：赛事 ID、学生姓名、手机号、提交时间范围
- 桌面端表格：报名编号、学生信息、赛事名称、来源、提交时间、状态、操作
- 移动端卡片列表：支持勾选、查看、审核入口
- 批量操作：批量通过 / 批量驳回
- `临时档案` 标签预留：当后端返回 `isTemporaryArchive / tempArchive / participantType / userId` 等字段时自动显示

### 3.2 详情抽屉

在 `EnrollDetailDrawer.vue` 中完成了以下能力：

- 学生基础信息区
- 赛事信息区
- 动态表单快照只读渲染
- 图片 / PDF / 视频附件预览与打开
- 审核记录展示
- 底部审核操作区：通过 / 驳回 / 关闭
- 驳回时强制填写原因
- 手机端自动切换为全屏 drawer

### 3.3 API 封装策略

`src/api/partner/enroll.js` 的函数命名保持任务卡语义：

- `listPartnerEnrolls`
- `getPartnerEnrollDetail`
- `auditPartnerEnroll`
- `batchAuditEnrolls`

但底层暂时桥接到当前已存在的后端接口：

- `GET /jst/event/enroll/list`
- `GET /jst/event/enroll/{id}`
- `POST /jst/event/enroll/{id}/audit`

批量审核当前后端无专用接口，因此前端先采用 `Promise.all` fan-out 到多个单条审核请求。

这样处理的目的：

- 不改 F9 后端，符合任务卡约束
- 页面可以直接连当前后端工作
- 后续一旦后端补出 `/jst/partner/enroll/*`，只需替换 API URL 与 method

## 4. 后端缺口反馈

结合任务卡契约与当前 F9 实现，已确认存在以下缺口：

### 4.1 路由与方法缺口

- 缺少 `GET /jst/partner/enroll/list`
- 缺少 `GET /jst/partner/enroll/{id}`
- 缺少 `PUT /jst/partner/enroll/{id}/audit`
- 缺少 `PUT /jst/partner/enroll/batch-audit`
- 当前实际审核接口是 `POST /jst/event/enroll/{id}/audit`

### 4.2 列表字段缺口

任务卡列表要求里有“来源”和 “Q-03 临时档案标签”，但当前 `EnrollListVO` 未稳定提供以下字段：

- `enrollSource`
- `isTemporaryArchive`
- `tempArchive`
- `participantType`
- `userId`

因此本次页面只能做到：

- 当后端后续返回这些字段时自动显示
- 当前桥接 F9 路由时，`来源` 先展示为 `待后端补充`
- `临时档案` 标签只能走启发式判断，不能完全准确

### 4.3 查询能力缺口

任务卡要求支持“按时间筛选”，但当前 `EnrollQueryReqDTO` / `selectAdminList` 尚未明确支持提交时间区间参数，因此本次前端已预留时间筛选 UI 和参数位，等待后端正式接入。

### 4.4 详情字段缺口

任务卡详情抽屉要求包括：

- 按 F8 模板渲染动态表单
- 作品材料标准化预览
- 历史审核记录

当前详情接口可用字段主要是：

- `formSnapshotJson`
- `auditStatus`
- `auditRemark`
- `supplementRemark`

仍缺少或不稳定的内容：

- F8 模板 schema 本体
- 标准化附件结构
- 审核历史数组
- 明确的来源字段
- 明确的临时档案标记

所以本次前端采用“快照容错渲染 + 字段到位即自动增强”的实现方式。

## 5. 验证结果

### 5.1 SFC 语法校验

为避免全量构建前无法判断新页面本身是否有语法问题，先对以下两个 SFC 做了模板与 script 解析校验：

- `src/views/partner/enroll-manage.vue`
- `src/views/partner/components/EnrollDetailDrawer.vue`

结果：

- `templateErrors: none`
- `scriptErrors: none`

### 5.2 生产构建验证

执行命令：

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui
npm run build:prod
```

结果：

- `BUILD SUCCESS`
- 仅有既有体积告警（asset size / entrypoint size），无编译错误

### 5.3 构建阻塞项处理经过

第一次构建失败原因不是本任务页面，而是仓库已有路由缺失：

- `@/views/partner-apply/form`
- `@/views/partner-apply/status`

因此本次额外补了两个最小路由壳子文件，再次构建后通过。

## 6. 风险与后续联调事项

- 当前批量审核是前端 fan-out，多条请求中途失败时会出现部分成功、部分失败的场景；正式版仍建议后端补 `batch-audit` 事务化接口。
- 当前“来源”和“临时档案标签”依赖后端补字段，现阶段展示不可能 100% 满足任务卡视觉验收。
- 当前“时间筛选”UI 已落，但是否真正生效取决于后端查询参数接入。
- 当前详情抽屉已能渲染 `formSnapshotJson` 与附件数组，但若后端后续改成更标准的 schema + field label + media object，前端可进一步提升展示质量。

## 7. 本任务之外我额外做了什么

- 为解除仓库现有路由缺页导致的前端构建失败，补了 `partner-apply/form.vue` 和 `partner-apply/status.vue` 两个最小路由壳子。
- 这两个文件不改变本任务主功能，仅用于让 `ruoyi-ui` 可以完成全量生产构建验证。
