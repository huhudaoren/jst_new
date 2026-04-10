# 任务卡 ADMIN-SCAFFOLD - ruoyi-ui 管理端脚手架整理

## 阶段
阶段 F / **ruoyi-ui**（Web Admin Agent）

## 背景
ruoyi-ui 赛事方页面已开发 8 个（PA-1~PA-8），但存在以下脚手架问题：
1. `permission.js` 用客户端 hack 注入 partner 路由，应改为后端菜单驱动
2. 响应式 CSS 各页面各写各的，无统一基础
3. 公共组件（状态徽章/空状态/时间轴/费用明细）在多个页面重复
4. `99-migration-partner-menus.sql` 已注册菜单但前端未适配后端菜单格式

本卡做框架层面的整理，不新增业务页面。

## 交付物

### Part A — 后端菜单驱动替代客户端 hack
**修改**：`src/store/modules/permission.js`

当前状态（PA-2 的 hack）：
```js
// 仅对 jst_partner 注入 partnerHomeRoute
if (roles.includes('jst_partner')) {
  rewriteRoutes.push(partnerHomeRoute)
  sidebarRoutes.push(partnerHomeRoute)
}
```

改为：
- 删除 `partnerHomeRoute` 硬编码
- 信任后端 `getRouters()` 返回的菜单树（`99-migration-partner-menus.sql` 已注册）
- 如果后端菜单返回的 `component` 路径是 `partner/home`，前端 `loadView` 应能解析为 `() => import('@/views/partner/home.vue')`
- 验证：partner 登录后侧边栏显示 8 个菜单项，且来自后端而非本地注入

**⚠️ 注意**：若 `getRouters()` 返回格式与前端解析不匹配（如 `component` 字段缺 `views/` 前缀），需要在 `filterAsyncRouter` 中兜容。先 grep 现有逻辑确认。

### Part B — 响应式 CSS 基础
**新建**：`src/assets/styles/responsive.scss`

```scss
// 断点变量
$breakpoint-mobile: 768px;
$breakpoint-tablet: 1024px;

// 手机下表格改卡片
@media (max-width: $breakpoint-mobile) {
  .app-container {
    padding: 12px !important;
  }
  // 表格横向滚动
  .el-table {
    overflow-x: auto;
  }
  // 搜索表单垂直堆叠
  .el-form--inline .el-form-item {
    display: block;
    margin-right: 0;
    margin-bottom: 10px;
  }
  // 弹窗全屏
  .el-dialog {
    width: 95% !important;
    margin-top: 5vh !important;
  }
  // 抽屉全屏
  .el-drawer {
    width: 100% !important;
  }
}
```

**修改**：`src/main.js` 或 `src/assets/styles/index.scss`，引入 `responsive.scss`

### Part C — 公共组件提取
**新建**：`src/components/JstStatusBadge/index.vue`

从 `partner/enroll-manage.vue` / `partner/contest-list.vue` 等页面提取重复的状态徽章组件：
```vue
<template>
  <el-tag :type="tagType" size="small">{{ label }}</el-tag>
</template>
<script>
export default {
  props: {
    status: String,
    statusMap: { type: Object, default: () => ({}) }
  },
  computed: {
    tagType() { return (this.statusMap[this.status] || {}).type || 'info' },
    label() { return (this.statusMap[this.status] || {}).label || this.status }
  }
}
</script>
```

**新建**：`src/components/JstEmptyState/index.vue`
```vue
<template>
  <el-empty :description="description" :image-size="imageSize">
    <slot />
  </el-empty>
</template>
```

全局注册到 `src/main.js`：
```js
import JstStatusBadge from '@/components/JstStatusBadge'
import JstEmptyState from '@/components/JstEmptyState'
Vue.component('JstStatusBadge', JstStatusBadge)
Vue.component('JstEmptyState', JstEmptyState)
```

### Part D — partner 默认跳转优化
**修改**：`src/permission.js`

当前状态（PA-2）：
```js
if (roles.includes('jst_partner') && (to.path === '/' || to.path === '/index')) {
  next('/partner/home')
}
```

确认在后端菜单驱动模式下仍然生效（`getRouters` 返回后，`/partner/home` 路由已注册），如果不生效则保留此兜底。

### Part E — router/index.js 清理
**修改**：`src/router/index.js`

- 检查是否有重复的 partner 路由定义
- 公开页路由（`partner-apply/*`）保留在本地
- partner 业务页路由交给后端菜单
- 清理注释和冗余代码

### Part F — 构建验证
```bash
npm run build:prod
```
必须通过，无 import 错误。

## DoD
- [ ] Part A 后端菜单驱动（partner 登录侧边栏 8 菜单来自后端）
- [ ] Part B responsive.scss 全局生效
- [ ] Part C 2 个公共组件 + 全局注册
- [ ] Part D 默认跳转仍正常
- [ ] Part E router 清理无冗余
- [ ] npm run build:prod SUCCESS
- [ ] 任务报告 `ADMIN-SCAFFOLD-回答.md`

## 不许做
- ❌ 不许改后端
- ❌ 不许新增业务页面（脚手架整理，不新增功能）
- ❌ 不许改已完成的 partner 页面的业务逻辑
- ❌ 不许引入新 npm 依赖
- ❌ 不许删 partner-apply 公开页路由（这些必须保留在本地）

## 依赖：99-migration-partner-menus.sql 已执行 ✅
## 优先级：P2

---
派发时间：2026-04-10
