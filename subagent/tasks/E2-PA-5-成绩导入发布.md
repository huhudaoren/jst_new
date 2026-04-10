# 任务卡 E2-PA-5 - 成绩导入与发布

## 阶段 / 端
阶段 E 批 2 / **ruoyi-ui** + 少量后端

## 背景
赛事方录入学生成绩，提交平台审核后发布。PRD §7.6 + §8.4 要求成绩发布必须经过平台审核。

## CCB 决策
- **Q-12**：成绩发布后仅平台可改，赛事方需申请更正

## PRD 依据
- §7.6 成绩导入/发布
- 原型：`event-score-manage.html`

## 交付物

### Part A — 后端接口（赛事方视角）

**新建**：`RuoYi-Vue/jst-event/.../controller/partner/PartnerScoreController.java`

路由 `/jst/partner/score/*`：
- `POST /import` — Excel 导入成绩（内部解析 + 批量插 `jst_score` + 状态 `draft`）
- `GET /list` — 按赛事查询成绩列表（PartnerScope 过滤）
- `PUT /{id}` — 单条编辑（仅 `draft` 状态）
- `PUT /submit-review` — 批量提交审核（`draft → pending_review`）
- `GET /correction/apply` — 申请更正（已发布成绩）
- `POST /correction/apply` — 提交更正申请（走工单到平台）

走 PartnerScope 切面（PA-9 实现）

### Part B — 前端页面 `ruoyi-ui/src/views/partner/score-manage.vue`

#### 顶部
- 选择赛事（带搜索）
- 显示报名人数 / 已录成绩数 / 审核中 / 已发布

#### 操作区
- 下载成绩模板（Excel）
- 上传成绩文件 → 调 `POST /import`
- 手动单条新增
- 批量提交审核

#### 表格
- 学生姓名 / 学号（脱敏手机号）
- 赛事名称
- 成绩（分数 or 等级）
- 排名（若有）
- 评语
- 状态：draft / pending_review / published / correction_requested
- 操作：编辑（draft 可编辑）/ 申请更正（published 才显示）

#### 更正申请弹窗
- 原成绩（只读）
- 新成绩
- 更正原因（textarea）
- 提交 → 后台生成工单

### Part C — API

**新建**：`ruoyi-ui/src/api/partner/score.js`

完整封装上述 6 个接口。

## DoD
- [ ] 后端 PartnerScoreController + 6 个接口
- [ ] 前端成绩管理页 + 更正申请弹窗
- [ ] Excel 模板 + 导入解析
- [ ] mvn compile SUCCESS
- [ ] 任务报告 `E2-PA-5-回答.md`
- [ ] commit: `feat(jst-event+web): E2-PA-5 赛事方成绩导入与发布`

## 不许做
- ❌ 不许让赛事方直接发布成绩（必须走平台审核）
- ❌ 不许让赛事方修改已发布成绩
- ❌ 不许动 F9 报名审核

## 依赖：E2-PA-1~4 + PA-9
## 优先级：P2

---
派发时间：2026-04-10
