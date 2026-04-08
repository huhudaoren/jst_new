# 竞赛通 UI 开发 · Agent 使用指南

> **适用对象**：任何被要求开发竞赛通界面的 AI Agent  
> **必读程度**：开始写任何一行代码前，必须完整阅读本文档  
> **文件位置**：`小程序原型/AGENT_GUIDE.md`

---

## 一、你在做什么项目

「竞赛通」是一个 **手机端 微信小程序** 的竞赛服务平台，运行在微信内置浏览器中。

- 技术形态：uniapp ，**无框架依赖**
- 首要环境：微信
- 文件目录：`D:\coding\jst_v1\小程序原型\`

---

## 二、开始前必须做的一件事

**每次开发新页面，第一步是阅读 `design-system.html`。**

```
小程序原型/
├── design-system.html   ← 完整设计规范文档（浏览器打开可预览所有组件）
├── design-system.css    ← 所有页面共用的样式库，必须引入
├── AGENT_GUIDE.md       ← 本文档
├── index.html           ← 首页
├── contest-detail.html  ← 赛事详情页
└── enroll.html          ← 报名流程页
```

`design-system.html` 包含：
- 色彩系统（所有 CSS 变量的色值和用途）
- 字体排版规则
- 间距 / 圆角 / 阴影 token
- 全部组件的实物展示（按钮、标签、卡片、表单、导航栏等）
- 交互规范和 CSS 类名速查表

---

## 三、每个页面的标准骨架

复制以下骨架开始写新页面，**一个字符都不要删**：

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <!-- ① 禁止用户手动缩放，禁止系统自动调整字号 -->
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <!-- ② 微信分享卡片标题 -->
  <title>页面名称 · 竞赛通</title>
  <!-- ③ 必须引入设计系统 CSS -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="design-system.css">
  <style>
    /* 只写本页面专属样式，公共样式全部来自 design-system.css */
  </style>
</head>
<!-- ④ 有底部 Tab 导航的页面加 has-tabbar 类 -->
<body class="has-tabbar">

  <!-- ⑤ 模拟状态栏（原型展示用） -->
  <div class="status-bar">
    <span class="time">9:41</span>
    <span class="icons">📶 🔋</span>
  </div>

  <!-- ⑥ 页面主内容 -->
  <div class="animate-in">
    <!-- 内容写在这里 -->
  </div>

  <!-- ⑦ 底部 Tab 导航（5个 tab，当前页加 active 类） -->
  <nav class="bottom-nav">
    <div class="nav-tab" onclick="location.href='index.html'">
      <span class="nav-tab-icon">🏠</span>
      <span class="nav-tab-label">首页</span>
    </div>
    <div class="nav-tab" onclick="location.href='contest-list.html'">
      <span class="nav-tab-icon">🏆</span>
      <span class="nav-tab-label">赛事</span>
    </div>
    <div class="nav-tab" onclick="location.href='course-list.html'">
      <span class="nav-tab-icon">📚</span>
      <span class="nav-tab-label">课程</span>
    </div>
    <div class="nav-tab" onclick="location.href='notice.html'">
      <span class="nav-tab-icon">📢</span>
      <span class="nav-tab-label">公告</span>
    </div>
    <div class="nav-tab" onclick="location.href='my.html'">
      <span class="nav-tab-icon">👤</span>
      <span class="nav-tab-label">我的</span>
    </div>
  </nav>

</body>
</html>
```

---

## 四、设计 Token：只用变量，禁止硬编码

**规则：绝对不允许在页面专属 CSS 里写 `#1058A0` 这样的色值。**  
所有颜色、间距、圆角、阴影，必须通过 `var(--token)` 引用。

### 最常用 Token 速查

| 用途 | Token | 实际值 |
|------|-------|--------|
| 页面背景 | `--c-gray-50` | #F4F6FA |
| 卡片背景 | `--c-white` | #FFFFFF |
| 正文标题 | `--c-gray-900` | #1A1F2E |
| 辅助文字 | `--c-gray-500` | #6B7490 |
| 占位/禁用 | `--c-gray-300` | #B0B8CC |
| 分隔线/边框 | `--c-gray-100` | #E8ECF4 |
| 主色（深蓝） | `--c-primary-600` | #1058A0 |
| 主色（中蓝） | `--c-primary-400` | #1A7CD9 |
| 主色浅底 | `--c-primary-50` | #EBF4FC |
| CTA 强调色 | `--c-accent` | #FF5722 |
| 价格/强调色 | `--c-accent` | #FF5722 |
| 成功绿 | `--c-success` | #27AE60 |
| 警告黄 | `--c-warning` | #F39C12 |
| 错误红 | `--c-danger` | #E74C3C |
| 卡片圆角 | `--radius-lg` | 16px |
| 输入框圆角 | `--radius-md` | 12px |
| 卡片阴影 | `--shadow-sm` | 0 2px 10px ... |
| 页面内边距 | `--space-4` | 16px |
| 点击过渡时长 | `--duration-fast` | 150ms |

---

## 五、组件使用规则

### 按钮（每页最多 1 个 btn-primary）

```html
<!-- 主 CTA — 报名、购买、提交 -->
<button class="btn btn-primary btn-lg btn-block">立即报名</button>

<!-- 次级操作 -->
<button class="btn btn-solid-blue btn-md">查看详情</button>

<!-- 线框操作 -->
<button class="btn btn-outline btn-md">加入收藏</button>

<!-- 取消/弱操作 -->
<button class="btn btn-ghost btn-md">取消</button>

<!-- 禁用态 -->
<button class="btn btn-primary btn-lg btn-block disabled">已截止</button>
```

### 标签（状态必须用对应语义色）

```html
<span class="tag tag-accent">报名中</span>      <!-- 橙：进行中 -->
<span class="tag tag-success tag-dot">已报名</span>  <!-- 绿+圆点：完成 -->
<span class="tag tag-warning tag-dot">待审核</span>  <!-- 黄+圆点：等待 -->
<span class="tag tag-danger tag-dot">已驳回</span>   <!-- 红+圆点：失败 -->
<span class="tag tag-gray tag-dot">已关闭</span>     <!-- 灰+圆点：结束 -->
<span class="tag tag-primary">数学</span>        <!-- 蓝：分类信息 -->
```

### 表单（font-size 必须 16px，防 iOS 缩放）

```html
<div class="form-group">
  <label class="form-label">姓名 <span class="required">*</span></label>
  <input class="form-input" type="text" placeholder="请输入真实姓名">
  <!-- 错误态：加 .error 类 + 显示错误文字 -->
  <!-- <input class="form-input error" ...> -->
  <!-- <div class="form-error">⚠ 请输入姓名</div> -->
</div>
```

### 通知横幅

```html
<div class="notice notice-info">
  <span class="notice-icon">ℹ️</span>
  <div>报名成功！请关注后续通知。</div>
</div>
<div class="notice notice-warning">...</div>
<div class="notice notice-danger">...</div>
<div class="notice notice-success">...</div>
```

### 页面进入动画（stagger 效果）

```html
<!-- 每个主要内容块加 animate-in 类，用 d1-d6 控制延迟 -->
<div class="animate-in">第一块内容</div>
<div class="animate-in animate-in-d1">第二块</div>
<div class="animate-in animate-in-d2">第三块</div>
<div class="animate-in animate-in-d3">第四块</div>
```

---

## 六、iOS / Android 兼容性强制规范

以下规则每次写代码前必须检查：

| 规则 | 原因 | 正确做法 |
|------|------|---------|
| 所有 `input` / `select` / `textarea` font-size ≥ 16px | iOS 对 <16px 输入框自动触发页面缩放 | 已在 design-system.css 全局处理，不要覆盖 |
| 有 Tab 导航的页面，body 加 `class="has-tabbar"` | 自动适配 iOS Home Bar 安全区 | 见骨架代码 |
| 不要用 `height: 100vh` | iOS Safari 100vh 包含地址栏高度，会导致内容溢出 | 用 `min-height: -webkit-fill-available` |
| fixed 定位元素加 `transform: translateZ(0)` | 修复 iOS Safari fixed 元素在滚动时抖动 | 已在底部导航写好 |
| `backdrop-filter` 必须配 fallback 背景色 | Android 微信不支持 | 先写 `background: #fff`，再写带透明度版本 |
| 可点击元素加 `touch-action: manipulation` | 消除 300ms 点击延迟 | 已在 design-system.css 覆盖常用类 |

---

## 七、页面列表与跳转关系

```
首页 (index.html)
  ├── 赛事列表 (contest-list.html)
  │     └── 赛事详情 (contest-detail.html)
  │           └── 报名流程 (enroll.html)
  │                 └── 支付确认（内嵌步骤，不单独建页）
  ├── 课程列表 (course-list.html)
  │     └── 课程详情 (course-detail.html)
  ├── 公告列表 (notice.html)
  └── 个人中心 (my.html)
        ├── 我的报名 (my-enroll.html)
        ├── 我的订单 (my-order.html)
        ├── 我的成绩 (my-score.html)
        ├── 我的证书 (my-cert.html)
        └── 我的课程 (my-course.html)
```

---

## 八、典型对话模板

当你需要让 Agent 开发某个页面时，复制以下模板，填入 `[]` 中的内容：

---

**模板 1：开发新页面**

> 请根据 `ui_v2/design-system.html` 中的设计规范，开发 [页面名称] 页面，保存到 `ui_v2/[文件名].html`。
>
> **页面功能要求：**
> [描述页面的主要内容和交互]
>
> **强制要求：**
> - 引入 `design-system.css`，所有样式通过 var(--token) 引用，禁止硬编码色值
> - 适配 iOS 安全区（有底部 Tab 的页面 body 加 `class="has-tabbar"`）
> - 所有 input/select/textarea font-size ≥ 16px
> - 页面骨架使用 AGENT_GUIDE.md 中的标准骨架
> - 有返回按钮的页面使用 `.page-header` 组件
> - 参考现有页面（如 index.html）保持风格一致

---

**模板 2：修改已有页面**

> 请修改 `ui_v2/[文件名].html`，[描述要改的内容]。
>
> **注意：**
> - 不要改动已引入的 design-system.css
> - 不要修改页面骨架结构（status-bar / bottom-nav）
> - 新增样式用 var(--token)，不要硬编码

---

**模板 3：开发一组相关页面**

> 请根据 `ui_v2/design-system.html` 中的设计规范，开发以下页面并保存到 `ui_v2/` 目录：
> 1. [页面1文件名]：[功能描述]
> 2. [页面2文件名]：[功能描述]
>
> 页面之间通过 `location.href` 跳转，跳转关系：[A → B → C]
>
> **公共要求：**（与模板1相同）

---

## 九、常见错误与正确做法

| ❌ 错误 | ✅ 正确 |
|--------|--------|
| `color: #1058A0` | `color: var(--c-primary-600)` |
| `border-radius: 16px` | `border-radius: var(--radius-lg)` |
| `font-size: 15px` 的 input | `font-size: 16px`（或继承 design-system.css 设定） |
| body 没有 `has-tabbar` 类 | 有底部导航的页面必须加 `class="has-tabbar"` |
| 每页写多个 `btn-primary` | 每页最多 1 个 `.btn-primary`，其余用 `.btn-solid-blue` |
| 状态标签乱用颜色 | 报名中→accent / 通过→success / 待审→warning / 驳回→danger |
| 用 `position: absolute` 做底部 CTA | 用 `.fixed-cta`（position: sticky） |
| 图片 `width: 120px`（写死） | 图片宽度用百分比或 `max-width: 100%` |
| 直接写 `padding-bottom: 80px` | 用 `body.has-tabbar` 类，自动含安全区 |

---

## 十、文件约定

| 文件 | 说明 |
|------|------|
| `design-system.css` | 公共样式库，**只读，不允许修改** |
| `design-system.html` | 设计规范文档，**只读，不允许修改** |
| `AGENT_GUIDE.md` | 本文档，**只读** |
| 其他 `.html` 文件 | 各业务页面，按需开发 |

---

*竞赛通 UI 团队 · v1.0 · 2026-03-18*
