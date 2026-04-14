# REFACTOR-10-MP-VISUAL — C 端视觉与交互重塑

> 优先级：P1 | 预估：XL | Agent：UI Refresh Agent
> 依赖：REFACTOR-1-DDL（新字段展示）、REFACTOR-8-MP-FORM（表单组件）

---

## 一、背景

当前小程序已完成 4 批 UI 刷新（uView 2.0 + Design Token），基础质量已达标。但整体风格仍偏"常规后台模板化"，需要进行**视觉升维**——向极简主义 + 高端艺术感 + AI 科技感靠拢，打造差异化品牌体验。

## 二、设计方向

### 2.1 视觉关键词

- **极简留白**：大面积留白，内容聚焦，减少视觉噪音
- **高级灰**：基础色调偏冷灰，搭配品牌色点缀
- **微光过渡**：卡片边缘微光（subtle glow）、渐变过渡、毛玻璃效果
- **卡片悬浮**：内容卡片带轻微 shadow + 圆角，层次分明
- **动效克制**：入场动画（fade-up）、切换过渡（slide），不花哨但流畅
- **暗色模式预留**：Design Token 支持 light/dark 切换（本期 light 为主）

### 2.2 核心改造页面（15 页）

| # | 页面 | 当前问题 | 目标效果 |
|---|---|---|---|
| 1 | 首页 index | 模块堆砌，缺乏层次 | Hero Banner + 极简分区 + 推荐卡片瀑布流 |
| 2 | 赛事列表 contest/list | 普通列表卡片 | 大图卡片 + 标签云 + 吸顶筛选 |
| 3 | 赛事详情 contest/detail | 基础信息罗列 | 沉浸式头图 + 粘性导航 + 锚点跳转 + 新增赛程/奖项/FAQ 展示 |
| 4 | 报名页 contest/enroll | 简单表单 | 步骤条引导 + 动态表单 + 费用摘要悬浮底栏 |
| 5 | 课程列表 course/list | 普通列表 | 横向大卡 + 标签分类 |
| 6 | 课程详情 course/detail | 基础详情 | 视频头图 + 目录折叠 + 评价区 |
| 7 | 我的 my/index | 功能列表 | 顶部毛玻璃头像卡 + 数据统计 + 功能网格 |
| 8 | 个人预约 appointment | 简单列表 | 日历卡片 + 时间段网格选择器（接新数据） |
| 9 | 订单详情 my/order-detail | 基础信息 | 时间轴进度条 + 信息卡片分组 |
| 10 | 退款详情 my/refund-detail | 基础信息 | 同上 |
| 11 | 成绩查询 my/score | 普通列表 | 成绩单卡片 + 雷达图（各项分数可视化） |
| 12 | 证书展示 my/certificate | 简单列表 | 证书大卡 + 一键保存海报 |
| 13 | 积分中心 points/center | 普通列表 | 积分仪表盘 + 流水时间轴 |
| 14 | 渠道首页 channel/home | 基础骨架 | 数据看板 + 今日待办 + 快捷操作 |
| 15 | 登录页 login | Mock 入口残留 | 品牌沉浸式 + 微信一键登录 |

### 2.3 新增全局组件

| 组件 | 说明 |
|---|---|
| `jst-hero-banner` | 首页/详情页沉浸式头图，支持视差滚动 |
| `jst-score-radar` | 成绩雷达图（基于 Canvas 或 u-charts） |
| `jst-cert-card` | 证书大卡展示 + 海报生成按钮 |
| `jst-timeline` | 订单/退款进度时间轴 |
| `jst-slot-picker` | 预约时间段日历网格选择器 |
| `jst-step-bar` | 报名/审核步骤条 |
| `jst-empty-state` | 统一空状态（插画 + 文案 + 操作按钮） |
| `jst-skeleton-plus` | 增强骨架屏（卡片形态匹配） |

## 三、赛事详情页重点改造

### 3.1 接入新结构化数据

赛事详情接口返回的新字段需要在详情页展示：

- **Banner 大图**：`bannerImage`（沉浸式头图，替代当前封面）
- **主办方/协办方**：信息行展示
- **赛程阶段**：`scheduleList` → 时间轴/日程卡片展示
- **奖项设置**：`awardList` → 奖项卡片/表格展示
- **常见问题**：`faqList` → 折叠面板（u-collapse）
- **成绩项**：`scoreItemList` → 评分维度展示
- **预约时间段**：`appointmentSlotList` → 日历网格
- **团队信息**：`teamMinSize`/`teamMaxSize` → 团队报名入口

### 3.2 粘性导航

滚动到内容区后，顶部出现粘性导航条：
- 赛事简介 | 赛程安排 | 奖项设置 | 报名须知 | 常见问题
- 点击锚点跳转，滚动时高亮当前区域

### 3.3 预览同构

此页面的渲染组件与管理端的 `ContestPreview.vue`（REFACTOR-3 产出）保持**数据结构一致**。管理端预览用 iframe 嵌入此页面的 H5 版本，实现"后台看到什么，用户手机上就显示什么"。

## 四、Design Token 升级

在现有 `styles/variables.scss` 基础上新增：

```scss
// 高级灰色系
$jst-bg-subtle: #F8F9FC;          // 页面底色（更冷更高级）
$jst-card-glow: 0 2px 12px rgba(0,0,0,0.04);  // 微光阴影
$jst-glass-bg: rgba(255,255,255,0.72);         // 毛玻璃背景
$jst-glass-blur: 20px;

// 动效 Token
$jst-transition-enter: 0.3s ease-out;
$jst-transition-page: 0.25s cubic-bezier(0.4, 0, 0.2, 1);

// 圆角升级
$jst-radius-card: 16rpx;    // 从 12 升到 16
$jst-radius-button: 24rpx;  // 胶囊按钮
```

## 五、DoD

- [ ] 15 页视觉重塑
- [ ] 8 个新全局组件
- [ ] 赛事详情接入新结构化数据
- [ ] 预约时间段日历选择器
- [ ] 成绩雷达图
- [ ] 证书卡片 + 海报
- [ ] Design Token 升级
- [ ] 动效系统（入场/切换）
- [ ] HBuilderX 编译通过
- [ ] 报告交付

## 六、注意

⭐ **本卡特许覆盖 MiniProgram Agent 常规约束**：
- 允许自主设计视觉/动效/交互（不限于原型 PNG）
- 允许修改 `styles/variables.scss` 新增 Token（但不删除现有 Token）
- 仍然禁止修改后端代码
- 仍然禁止 mock 数据
