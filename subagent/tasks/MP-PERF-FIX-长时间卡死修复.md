# 任务卡 MP-PERF-FIX：小程序端「长时间卡死」性能修复

## 任务编号
MP-PERF-FIX

## 阶段
运维收尾 / 性能优化

## 涉及模块
`jst-uniapp`（小程序前端，不动后端）

## 业务背景

用户反馈：**小程序连续使用较长时间（半小时~一小时）后会卡顿甚至卡死**。

主 Agent 已完成根因诊断（详见计划文档 `C:\Users\X\.claude\plans\proud-hatching-dolphin.md`）。结论：
- **不是业务逻辑问题**（Controller/Store/Service 层结构规范）。
- 根因是 **UI 重塑引入视觉效果（countUp 数字滚动、IntersectionObserver 入场动画、setTimeout 延迟交互、CSS infinite 动画、backdrop-filter 毛玻璃）之后，绝大多数页面缺少配套的资源释放钩子**。

**卡死链路**：用户反复 `navigateTo` → 页面无 `beforeDestroy` → 前一页的 `setTimeout` 回调 / IntersectionObserver / countUp RAF / 骨架屏 infinite 动画都还在后台运行 → 栈深后 GPU 合成层 + JS 线程都饱和 → 卡死。

## 量化证据（主 Agent 已 Grep 核实）

| 指标 | 数据 |
|---|---|
| 业务 page 总数（`pages/` + `pages-sub/`） | **75 个** |
| 含 `beforeDestroy` / `onUnload` 清理钩子的页面 | **仅 2 个**（`pages/index/index.vue`、`pages/my/index.vue`） |
| 含 `setInterval` / `setTimeout` 的业务页 | 18 个（`uni_modules/` 内的不算） |
| 使用 `createSelectorQuery` / `createIntersectionObserver` 但未 `.disconnect()` | 多处 |
| 全局 `animation: ... infinite` CSS 类 | 3 个（`jstPulse` / `jstScanLine` / `jstGlowPulse`） |
| `backdrop-filter: blur(...)` 使用 | **7 个文件 23 处**（含滚动列表卡片，iOS 微信合成层代价极大） |

## 必读上下文

子 Agent 开工前必须读完以下 4 份文件：

1. `jst-uniapp/utils/visual-effects.js`（`countUp` / `batchCountUp` / `createObserver` 的源实现 —— **它们都返回 `cancel` 函数或 `observer` 实例，调用方必须保存并在 `beforeDestroy` 中调用**）
2. `jst-uniapp/pages/my/index.vue` 约 353 行 `beforeDestroy()`：**已落地的清理范本**，清 `summaryAnimCancels`。新页面照此写。
3. `jst-uniapp/styles/animations.scss`：全局动画类源。P1 不改源，只管调用点。
4. `jst-uniapp/components/jst-glass-card/jst-glass-card.vue`：毛玻璃卡片统一出口，P1 加 `solid` prop。

## 修复范围

### P0 — 补齐所有页面的资源清理（必做）

**目标文件**：`jst-uniapp/pages/**/*.vue` + `jst-uniapp/pages-sub/**/*.vue` 下**所有不含 `beforeDestroy` 的 `.vue` 页面**（73 个）。

**处理策略**：**分两类处理，不要盲目全量改**。

#### 类别 A：页面里**有**以下任一副作用 → 必须补完整清理钩子

副作用识别：
- `setTimeout(` / `setInterval(`（非 uni_modules 源自己写的）
- `countUp(` / `batchCountUp(`（来自 `utils/visual-effects`）
- `createObserver(` / `uni.createIntersectionObserver(` / `createSelectorQuery(`
- `uni.$on(` / `addEventListener(`（业务订阅）
- `requestAnimationFrame(`

**做法（统一模板）**：

```js
data() {
  return {
    // ...原有数据
    _timers: [],      // 存 setTimeout/setInterval ID
    _observers: [],   // 存 IntersectionObserver 实例
    _cancels: []      // 存 countUp/batchCountUp 返回的 cancel 函数
  }
},
methods: {
  // ...原有方法
  // （可选便利方法，推荐）
  _setTimer(fn, ms) {
    const id = setTimeout(fn, ms)
    this._timers.push(id)
    return id
  }
},
beforeDestroy() {
  this._timers.forEach(function(id) { clearTimeout(id); clearInterval(id) })
  this._observers.forEach(function(o) { if (o && o.disconnect) o.disconnect() })
  this._cancels.forEach(function(c) { if (typeof c === 'function') c() })
  this._timers = []
  this._observers = []
  this._cancels = []
}
```

同时**把已有的裸写副作用改造成存入数组**：
- `setTimeout(() => {...}, 300)` → `const id = setTimeout(() => {...}, 300); this._timers.push(id)`
- `const cancel = countUp(...)` → `this._cancels.push(countUp(...))`
- `const observer = createObserver(this, ...)` → `this._observers.push(createObserver(this, ...))`
- `uni.createIntersectionObserver(this, ...)` 同理

#### 类别 B：页面**没有**上述任何副作用 → **不需要**加钩子（避免无意义 diff）

**不要给纯展示页、纯表单页盲目加空 `beforeDestroy`**，那只会污染 diff。

### 明确需要处理的高危页面（主 Agent 预筛）

以下 19 页确定有副作用，必须处理（子 Agent 仍需亲自 Grep 确认无遗漏）：

```
pages-sub/my/order-detail.vue
pages-sub/my/cert-detail.vue
pages-sub/my/address-edit.vue
pages-sub/my/profile-edit.vue
pages-sub/contest/enroll.vue
pages-sub/contest/team-enroll.vue
pages-sub/contest/detail.vue
pages-sub/appointment/detail.vue
pages-sub/appointment/apply.vue
pages-sub/appointment/scan.vue
pages-sub/appointment/writeoff-record.vue
pages-sub/mall/detail.vue
pages-sub/channel/invoice-apply.vue
pages-sub/channel/apply-form.vue
pages-sub/channel/appointment.vue
pages-sub/channel/batch-enroll.vue
pages-sub/channel/withdraw-apply.vue
pages-sub/rights/writeoff-apply.vue
pages-sub/course/detail.vue
```

**子 Agent 第一步必做**：
```
Grep "setTimeout|setInterval|countUp|createObserver|createIntersectionObserver|createSelectorQuery|uni\.\$on" 
  在 jst-uniapp/pages 和 jst-uniapp/pages-sub 下
  输出完整命中列表
```
对照上面 19 页，补全清单，再开始动手。

---

### P1 — 裁剪无限 CSS 动画作用域

**问题**：`styles/animations.scss` 里这 3 个全局类带 `animation: ... infinite`：
- `jst-anim-pulse`（骨架屏呼吸）
- `jst-anim-scan-line`（扫码引导线，只该在扫码页用）
- `jst-anim-glow-pulse`（骨架屏微光）

当骨架屏用 `v-show` / opacity 隐藏而非 `v-if` 销毁时，动画继续跑占 GPU。

**处理**：
1. `Grep "jst-anim-pulse|jst-anim-glow-pulse|jst-anim-scan-line"` 罗列所有调用点。
2. 每个调用点确认：骨架屏外层必须 `v-if="loading"`（销毁节点），不能 `v-show`。
3. `jst-anim-scan-line` 只允许出现在 `pages-sub/appointment/scan.vue`；其他页如果误用一律删除。
4. `animations.scss` 源文件**不要改**（定义无罪，调用点才是问题）。

---

### P1 — 限制 `backdrop-filter` 到非滚动容器

**问题**：iOS 微信对 `backdrop-filter: blur()` 的合成代价远高于 Android。当 blur 容器出现在滚动列表 / 长内容里，每滚一帧都在重新合成。

**已知使用点**（主 Agent 已 Grep 核实）：
- `pages/login/login.vue`（8 处）
- `pages/index/index.vue`（4 处）
- `pages-sub/contest/detail.vue`（4 处）
- `pages-sub/contest/enroll.vue`（2 处）
- `pages-sub/course/detail.vue`（1 处）
- `pages/contest/list.vue`（2 处）
- `components/jst-glass-card/jst-glass-card.vue`（2 处）

**处理策略**：

1. **改造 `jst-glass-card` 组件**：新增 `solid` prop，默认 `false`。
   - 当 `solid === true`：样式降级为纯色半透 `background: rgba(255, 255, 255, 0.85)`，**去掉 backdrop-filter**。
   - 当 `solid === false`：保持原 blur 行为。

2. **逐个调用点判断**：
   - 位于滚动容器（`scroll-view`）内 → 传 `solid` prop。
   - 位于 `v-for` 列表项里 → 传 `solid` prop。
   - 位于首屏 Hero / sticky 导航 / 弹窗遮罩 / tabbar（**每屏最多 2 层固定位置**）→ 保持 blur。

3. 对**非 jst-glass-card 的直接 `backdrop-filter` 裸写**（如 `pages/login/login.vue` 的 8 处），逐一判断所在容器：
   - 首屏固定元素 → 保留。
   - 滚动内容 → 改为纯色半透。

---

### P2（可选）— 事件总线遗漏检查

`Grep "uni\.\$on"` 在 `jst-uniapp/pages*/` 下，确认无业务订阅遗漏（主 Agent 预查只在 `uni_modules` 里命中）。若发现业务页订阅，补 `uni.$off` 到 `beforeDestroy`。

---

## 交付物清单

### 修改文件

预计涉及 20~25 个 `.vue` 页面（P0 约 19 页 + P1 约 7 页，其中 `contest/detail.vue`、`contest/enroll.vue`、`course/detail.vue` 会同时命中 P0 + P1）。

**单独列出**：
- `jst-uniapp/components/jst-glass-card/jst-glass-card.vue` ← P1 加 `solid` prop

其余文件列表由子 Agent 在完成 `Grep` 排查后补齐到完工报告中。

### 新增文件

**无**。不要新建 mixin / 工具文件 —— 清理模板直接内联到页面即可。若后续觉得有必要抽象，留作二期。

### 不产生的产物

- ❌ 不新增组件 / mixin / 工具函数
- ❌ 不修改业务逻辑（script 块里除清理钩子和副作用数组化之外，一行不改）
- ❌ 不修改 `animations.scss` 源
- ❌ 不修改 `visual-effects.js` 源
- ❌ 不动任何后端 / API / Store

---

## 测试场景（运行态回归）

子 Agent **自验**步骤（用 `mcp__jst-mp-automator__*` MCP）：

### MP-PERF-FIX-1：切页清理验证（必做）
1. `mp_launch` 启动小程序
2. `mp_mock_login` 学生账号登录
3. 编写 flow：**反复 navigateTo 进入 → 返回**各 3 轮：
   - 赛事详情页
   - 课程详情页
   - 订单详情页
   - 我的预约详情
4. `mp_get_console` 抓 warning
5. **验收**：连续切页 20 次后，console **不应**出现：
   - `"setTimeout called on destroyed instance"`
   - `"Cannot read property of undefined"`（常见于 observer 回调拿不到 this 的数据）
   - uniapp 的页面栈溢出警告

### MP-PERF-FIX-2：骨架屏动画作用域验证
1. 进入任一带骨架屏的页面（如 `pages-sub/my/order-list.vue`）
2. 数据加载完成后 `mp_screenshot`
3. `mp_get_element` 拿到骨架屏节点，**验收**：节点应**不存在**（被 v-if 销毁），而不是存在但 display:none。

### MP-PERF-FIX-3：jst-glass-card solid 模式视觉验证
1. 一张列表页（`pages/contest/list.vue`）带 `solid` 截图
2. 首屏 Hero 保留 blur 的页面（`pages/index/index.vue`）截图
3. **验收**：列表项视觉无明显割裂，Hero 毛玻璃效果保留。

---

## DoD 验收标准

- [ ] Grep 产出的副作用页面清单 **100%** 有对应 `beforeDestroy` 清理（子 Agent 报告中附前后对比清单）
- [ ] `jst-glass-card` 的 `solid` prop 已实现，列表/滚动场景全部传 `solid`
- [ ] `jst-anim-scan-line` 仅出现在 `pages-sub/appointment/scan.vue`
- [ ] `mp_run_flow` 反复切页 20 次后，`mp_get_console` 无资源泄漏告警
- [ ] HBuilderX 编译通过（若 Agent 能本地跑），无语法报错
- [ ] 完工报告按 `subagent/tasks/任务报告/` 模板提交，路径 `subagent/tasks/任务报告/MP-PERF-FIX-完工报告.md`
- [ ] commit message：`perf(miniprogram): MP-PERF-FIX 补齐页面资源清理与动画/毛玻璃作用域`

---

## 不许做的事

- ❌ **不许动 script 块里的业务逻辑**（调用顺序、数据结构、API 请求、computed 一律不改；只允许把 `setTimeout(...)` 包装成存数组 + 补 `beforeDestroy`）
- ❌ **不许改 `utils/visual-effects.js`** 源（它设计正确，是调用方没收 cancel 函数）
- ❌ **不许改 `styles/animations.scss`** 源
- ❌ **不许改任何后端代码 / .http 测试文件 / DDL**
- ❌ **不许新建 mixin / 公共组件** —— 先把散点问题改完再谈抽象，避免过度设计
- ❌ **不许删除 `backdrop-filter` 的全部使用** —— 登录 Hero、sticky 导航、弹窗这些固定元素必须保留视觉效果
- ❌ **不许盲目给纯展示页加空 `beforeDestroy` 钩子**（没副作用就不加，避免 diff 污染）

---

## 预计工作量

- P0（19+ 页清理钩子）：4~6 小时
- P1（动画作用域 + backdrop-filter solid 改造）：2~3 小时
- 自验 + 报告：1 小时
- **合计：约 1 人日**

## 优先级
高（用户体感问题，影响使用时长）

---

主 Agent 签名：Claude (Opus 4.7)
派发时间：2026-04-17
版本：任务卡 v1
依赖计划：`C:\Users\X\.claude\plans\proud-hatching-dolphin.md`
