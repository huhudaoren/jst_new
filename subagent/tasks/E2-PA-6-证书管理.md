# 任务卡 E2-PA-6 - 证书管理

## 阶段 / 端
阶段 E 批 2 / **ruoyi-ui** + 后端

## 背景
赛事方配置证书模板 + 批量发证 + 查看已发证记录。证书发放也需平台审核。

## PRD 依据
- §7.6 证书管理
- §8.4 jst_certificate 表
- 原型：`event-cert-manage.html`

## 交付物

### Part A — 后端接口

**新建**：`jst-event/.../controller/partner/PartnerCertController.java`

路由 `/jst/partner/cert/*`：
- `POST /template` — 上传证书模板（PSD/JPG + 占位变量配置）
- `GET /template/list` — 模板列表
- `GET /list?contestId=xxx` — 按赛事查已发证列表
- `POST /batch-grant` — 按成绩批量生成证书（`成绩 published` 状态才允许）
- `POST /submit-review` — 提交证书发放审核
- `GET /{certId}/preview` — 生成证书预览图

### Part B — 前端页面 `ruoyi-ui/src/views/partner/cert-manage.vue`

#### 证书模板管理区
- 模板列表（缩略图 + 名称）
- 上传新模板
- 编辑占位变量（姓名/成绩/赛事/日期/证书编号）

#### 已发证列表
- 选择赛事 → 列出所有已发成绩 + 可批量生成证书
- 每项：学生 / 赛事 / 等级 / 证书编号 / 发证状态（draft/pending/granted）/ 操作（预览/下载/重发）

#### 批量生成
- 勾选学生（仅 `成绩 published` 状态）
- 选择模板
- 一键批量生成 → 进入 `draft` 状态
- 提交审核 → `pending_review`
- 平台审核通过后变 `granted`，学生可在小程序查看下载

### Part C — API

**新建**：`ruoyi-ui/src/api/partner/cert.js`

## DoD
- [ ] 后端 PartnerCertController
- [ ] 前端证书管理页
- [ ] 模板上传 + 批量发证 + 审核流
- [ ] mvn compile SUCCESS
- [ ] 任务报告 `E2-PA-6-回答.md`
- [ ] commit: `feat(jst-event+web): E2-PA-6 赛事方证书管理`

## 不许做
- ❌ 不许让赛事方跳过平台审核直接发证
- ❌ 证书 PDF/PNG 实际渲染可以用后端 BufferedImage 占位（不要求真实字体水印精细对齐）

## 依赖：E2-PA-5
## 优先级：P2

---
派发时间：2026-04-10
