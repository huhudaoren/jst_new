# WEB-ADMIN-POLISH — 管理端页面精品化（10 页升级）

> 优先级：P2 | 预估：M | Agent：Web Admin Agent

---

## 一、目标

将 10 个基础页面升级为精品页风格，对齐 `partner/contest-edit.vue` 和 WEB-ADMIN-5~8 的视觉标准。核心改动：Hero banner + 搜索面板 + 详情抽屉 + 响应式 + 帮助提示。

## 二、参考标准

精品页必备 5 要素（参考 `views/partner/contest-edit.vue` 和 `views/jst/user/index.vue`）：

1. **Hero Banner**：eyebrow 小标题 + h2 大标题 + 描述文字 + 操作按钮
2. **搜索面板**：`query-panel` 卡片包裹，关键字段 + 状态下拉 + 时间范围
3. **双模式列表**：PC `el-table` + 手机 `mobile-card`（`isMobile` 切换）
4. **详情抽屉**：`el-drawer`（PC 50~60% / 手机 100%）+ `el-descriptions`
5. **帮助提示**：Hero 区 `?` 按钮 + `el-popover` 3~5 行说明文字

## 三、重点升级：course/edit.vue

当前问题：用 `el-page-header` + 平铺 `el-card`，和 partner/contest-edit 的 Tab 化体验差距大。

### 改造方案

**Hero 区**：
```html
<div class="edit-hero">
  <div>
    <div class="edit-eyebrow">课程管理</div>
    <div class="edit-title">{{ isEdit ? '编辑课程' : '新建课程' }}</div>
    <div class="edit-desc">按模块完成课程资料、讲师信息与课程目录配置。</div>
  </div>
  <div class="hero-actions">
    <el-button icon="el-icon-back" @click="goBack">返回列表</el-button>
    <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
  </div>
</div>
```

**Tab 化**：将现有 5 个 el-card 改为 `el-tabs`：
```
Tab A: 基本信息（名称/类型/封面/简介/价格/可见范围）
Tab B: 讲师信息（姓名/头像/简介）
Tab C: 课程目录（章节+课时可视化编辑器 — 保留现有 addChapter/addLesson 逻辑不动）
Tab D: 课程介绍（富文本编辑器）
Tab E: 统计信息（课时数/总时长/学习人数 — 只读或可编辑）
```

**帮助提示**：每个 Tab 顶部加一行灰色提示文字说明该模块要填什么。

**关键约束**：`<script>` 内的 methods（addChapter / addLesson / moveChapter / handleSubmit 等）**一行不动**，只改 template 结构和 style。

## 四、列表页升级（9 页）

| # | 文件 | 当前 | 升级要点 |
|---|---|---|---|
| 1 | `course/index.vue` | 基础 form+table | +Hero("课程中心/管理所有课程的发布与学习数据") +搜索面板 +帮助 |
| 2 | `coupon/template.vue` | 基础 form+table+modal | +Hero("优惠券模板/创建和管理优惠券模板") +modal→drawer +帮助 |
| 3 | `coupon/issued.vue` | 基础 | +Hero("已发优惠券/查看用户持有的优惠券状态") +drawer +帮助 |
| 4 | `rights/template.vue` | 基础 form+table+modal | +Hero("权益模板/创建和管理专属权益模板") +modal→drawer +帮助 |
| 5 | `rights/issued.vue` | 基础 | +Hero("已发权益/查看用户持有的权益使用情况") +drawer +帮助 |
| 6 | `mall/index.vue` | 基础 form+table+modal | +Hero("积分商城/管理兑换商品的上下架与库存") +drawer +库存预警 |
| 7 | `mall/exchange.vue` | 基础 | +Hero("兑换订单/处理积分商城兑换订单与发货") +drawer +帮助 |
| 8 | `form-template/index.vue` | 基础 form+table+modal | +Hero("表单模板/管理赛事报名的动态字段模板") +JSON 预览 drawer +帮助 |
| 9 | `notice/index.vue` | 基础 form+table+modal | +Hero("公告管理/发布与管理平台公告和资讯") +帮助 |

### 每个列表页的统一改造步骤

1. 在 `<template>` 顶部加 Hero banner（eyebrow + h2 + desc + 帮助 popover + 刷新按钮）
2. 搜索 form 改为 `query-panel` 卡片包裹
3. 加 `isMobile` 判断，手机端切换为卡片列表
4. 新增/编辑弹窗从 `el-dialog` 改为 `el-drawer`（PC 50% / 手机 100%）
5. Hero 右上角加 `el-popover` 帮助按钮（3~5 行操作说明）
6. 保持 `<script>` 业务逻辑不变

## 五、帮助文字参考

| 页面 | 帮助内容 |
|---|---|
| course/index | 课程支持普通和 AI 两种类型。创建后默认草稿，手动上线后对用户可见。 |
| course/edit | 按 Tab 依次完善课程信息。课程目录支持多章节多课时，拖拽排序。 |
| coupon/template | 模板定义优惠券的面额、门槛和有效期。发放时从模板批量生成。 |
| rights/template | 权益模板定义次数、有效期。发放后用户可在小程序端查看和使用。 |
| mall/index | 商品上架后用户可在积分商城兑换。库存不足 10 件时自动预警。 |
| form-template | 表单模板定义报名时需要填写的字段。支持文本、选择、文件上传等类型。 |
| notice/index | 公告发布后所有用户可见。支持置顶、分类和定时发布。 |

## 六、样式规范

复用已有的精品页 CSS class：

```css
.page-hero / .edit-hero     /* Hero 区 */
.hero-eyebrow / .edit-eyebrow  /* 小标题 */
.hero-desc / .edit-desc      /* 描述文字 */
.hero-actions                /* 操作按钮区 */
.query-panel                 /* 搜索面板 */
.mobile-card                 /* 手机端卡片 */
.section-card                /* 内容区卡片 */
.drawer-body                 /* 抽屉内容 */
```

如果页面已有同名 class，直接复用；没有的按 `jst/user/index.vue` 的样式复制。

## 七、DoD

- [ ] course/edit.vue Hero + Tab 化（5 Tab）
- [ ] 9 个列表页 Hero + 搜索面板 + 帮助 popover
- [ ] 手机端响应式（768px 断点）
- [ ] dialog → drawer 改造（至少 coupon/rights/mall/form-template/notice 5 页）
- [ ] 帮助文字内嵌
- [ ] script 业务逻辑不变
- [ ] `npm run build:prod` 通过
- [ ] 报告交付
