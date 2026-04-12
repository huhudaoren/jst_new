# 任务卡 WEB-ADMIN-3 - 内容与营销管理页面

## 阶段
E-4-WEB / **ruoyi-ui**（Web Admin Agent）

## 背景
平台运营需要管理公告、课程、表单模板、优惠券、权益、积分商城。后端 API 已就绪。

## 系统提示词
使用 `subagent/WEB_ADMIN_AGENT_PROMPT.md`

## 交付物

### 页面清单

#### 1. 公告管理 `views/jst/notice/`
- **index.vue** — 公告列表
  - 搜索：title / category / status
  - 表格：noticeId / title / category / status / topFlag / publishTime / createTime
  - 操作：新增 / 编辑 / 发布 / 下线 / 删除
  - 编辑弹窗：标题 / 分类（下拉含 `platform`/`contest`/`points`/`mall`/**`topic`**）/ 封面图 / 富文本内容（用若依已有富文本编辑器）/ 置顶
  - ⚠️ `category='topic'` 为**专题活动**，小程序首页展示。编辑时封面图必填，`remark` 字段用作跳转链接（linkUrl）
  - API：`api/jst/message/` — 公告 CRUD

#### 2. 课程管理 `views/jst/course/`
- **index.vue** — 课程列表
  - 搜索：courseName / courseType / auditStatus / status
  - 表格：courseId / courseName / courseType / price / lessonCount / learnerCount / auditStatus / status
  - 操作：新增 / 编辑 / 审核 / 上架下架 / 删除

- **edit.vue** — 课程编辑（新页面或大弹窗）
  - 基本信息：名称 / 类型 / 封面 / 价格 / 积分价
  - 统计信息：课时数（lesson_count，手动填）/ 总时长（total_duration，手动填如"12小时30分"）/ 学习人数（learner_count，只读展示）
  - 讲师信息：讲师姓名（teacher_name）/ 讲师头像（teacher_avatar，图片上传）/ 讲师简介（teacher_desc）
  - **课程目录**：可视化编辑 `chapters_json`（树形表格：章节名 → 课时列表[课时名+时长+是否免费]，支持增删章节和课时）
  - 课程介绍：富文本
  - ⚠️ 课程目录是小程序课程详情页的核心内容，必须支持可视化编辑，不能只做 textarea JSON
  - API：`api/jst/event/` — 课程 CRUD

#### 3. 表单模板管理 `views/jst/form-template/`
- **index.vue** — 模板列表
  - 搜索：templateName / status
  - 表格：templateId / templateName / templateVersion / status / createTime
  - 操作：新增 / 编辑 / 启用禁用

- **edit.vue** — 模板编辑
  - 模板名称 / JSON Schema 编辑器（textarea + 预览）
  - API：`api/jst/event/` — 表单模板 CRUD

#### 4. 优惠券管理 `views/jst/coupon/`
- **template.vue** — 券模板列表
  - 搜索：couponName / couponType / status
  - 表格：templateId / couponName / couponType / faceValue / threshold / validDays / status
  - 操作：新增 / 编辑 / 启用禁用
  - API：`api/jst/marketing/` — 优惠券模板

- **issued.vue** — 已发券列表
  - 搜索：userName / couponName / status
  - 表格：userCouponId / userName / couponName / status / validEnd / orderId
  - 只读

#### 5. 权益管理 `views/jst/rights/`
- **template.vue** — 权益模板列表
  - 搜索：rightsName / rightsType / status
  - 表格：templateId / rightsName / rightsType / quotaMode / quotaValue / writeoffMode / status
  - 操作：新增 / 编辑

- **issued.vue** — 已发权益列表
  - 搜索：userName / rightsName / status
  - 表格：userRightsId / userName / rightsName / remainQuota / status / validEnd
  - 只读

#### 6. 积分商城管理 `views/jst/mall/`
- **index.vue** — 商品列表
  - 搜索：goodsName / goodsType / status
  - 表格：goodsId / goodsName / goodsType / pointsPrice / cashPrice / stock / status
  - 操作：新增 / 编辑 / 上下架

- **exchange.vue** — 兑换记录
  - 搜索：userName / goodsName / status
  - 表格：exchangeId / userName / goodsName / status / createTime
  - 操作：发货（实物）/ 查看

## DoD
- [ ] 6 个管理模块页面完成
- [ ] 富文本编辑器正常工作（公告/课程）
- [ ] `npm run build:prod` SUCCESS
- [ ] 任务报告 `WEB-ADMIN-3-回答.md`

## 不许做
- ❌ 不许改后端
- ❌ 不许改小程序

## 优先级：P1
---
派发时间：2026-04-12
