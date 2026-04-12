# 任务报告 WEB-ADMIN-3 — 内容与营销管理页面

> 完成时间：2026-04-12

## 交付物清单

### API 文件（6 个）

| 文件 | 说明 |
|---|---|
| `src/api/jst/notice.js` | 公告 CRUD + publish/offline |
| `src/api/jst/course.js` | 课程 CRUD + submit/approve/reject/on/off |
| `src/api/jst/formTemplate.js` | 表单模板 save/list/detail + 提审/审核 |
| `src/api/jst/coupon.js` | 券模板 CRUD + 已发券只读列表 |
| `src/api/jst/rights.js` | 权益模板 CRUD + 已发权益只读列表 |
| `src/api/jst/mall.js` | 商品 CRUD + 兑换订单 list/detail/ship/complete |

### 页面文件（10 个）

| 文件 | 说明 |
|---|---|
| `views/jst/notice/index.vue` | 公告管理列表 + 编辑弹窗（含 `<editor>` 富文本 + `<image-upload>` 封面 + topic 专题逻辑） |
| `views/jst/course/index.vue` | 课程列表 + 审核/上下架操作 |
| `views/jst/course/edit.vue` | 课程编辑页（4 区块：基本信息/讲师/课程目录可视化编辑/富文本介绍） |
| `views/jst/form-template/index.vue` | 表单模板列表 + 编辑弹窗（JSON Schema textarea + 实时预览面板 + 驳回弹窗） |
| `views/jst/coupon/template.vue` | 券模板列表 + 编辑弹窗 + 启停 |
| `views/jst/coupon/issued.vue` | 已发券只读列表 |
| `views/jst/rights/template.vue` | 权益模板列表 + 编辑弹窗 + 启停 |
| `views/jst/rights/issued.vue` | 已发权益只读列表 |
| `views/jst/mall/index.vue` | 积分商城商品列表 + 编辑弹窗 + 上下架 |
| `views/jst/mall/exchange.vue` | 兑换记录列表 + 发货弹窗（物流公司+单号）+ 确认完成 + 详情抽屉 |

### 路由

| 文件 | 改动 |
|---|---|
| `src/router/index.js` | 新增 `/jst` dynamicRoute：`course-edit` / `course-edit/:courseId` |

## 后端 API 映射

| 模块 | 后端接口前缀 | 说明 |
|---|---|---|
| 公告 | `/jst/message/notice/` | 新 Controller（list/add/edit/publish/offline） |
| 课程 | `/jst/event/course/` | 新 Controller（list/get/add/edit/submit/audit/on/off） |
| 表单模板 | `/jst/event/form/template/` | 新 Controller（list/get/save/submit/audit） |
| 券模板 | `/jst/marketing/jst_coupon_template/` | 生成 Controller（CRUD） |
| 已发券 | `/jst/marketing/jst_user_coupon/` | 生成 Controller（只读 list） |
| 权益模板 | `/jst/marketing/jst_rights_template/` | 生成 Controller（CRUD） |
| 已发权益 | `/jst/marketing/jst_user_rights/` | 生成 Controller（只读 list） |
| 商品 | `/jst/points/jst_mall_goods/` | 生成 Controller（CRUD） |
| 兑换订单 | `/jst/points/mall/exchange/` | 新 Controller（list/detail/ship/complete） |

## 关键实现细节

1. **公告 topic 专题**：category='topic' 时封面图必填验证，remark 字段显示为"跳转链接"输入框
2. **课程目录可视化编辑**：chaptersJson 用树形结构（章节 → 课时列表），每个课时含 name/duration/free，支持增删和上下移动排序
3. **表单模板 Schema 预览**：左侧 textarea 输入 JSON，右侧实时解析 fields/properties 显示字段列表（label + type + required 标签）
4. **兑换订单发货**：弹窗收集 logisticsCompany + logisticsNo，调用 `/ship` 接口；shipped 状态可调用 `/complete`
5. **响应式**：全部页面用 `isMobile` 计算属性（`$store.state.app.device === 'mobile'`），手机下表格切换为卡片列表，弹窗 fullscreen

## DoD 检查

- [x] 6 个管理模块页面完成（公告/课程/表单模板/优惠券/权益/积分商城）
- [x] 富文本编辑器正常工作（公告内容 + 课程介绍，用若依全局 `<editor>` 组件）
- [x] `npm run build:prod` ✅ SUCCESS（Build complete）
- [x] 响应式适配（mobile 卡片 + desktop 表格 + 弹窗 fullscreen）
- [x] 权限控制（v-hasPermi 按钮级）
- [x] 未改后端 / 未改小程序 / 未引入新依赖

## 遗留说明

- 页面通过后端 `getRouters()` 动态菜单驱动，需要在数据库菜单表中配置对应路由才能在侧边栏出现
- 已发券/已发权益的 `userName` 字段取决于后端生成 Controller 是否关联查询了用户表，可能需要后端补充
