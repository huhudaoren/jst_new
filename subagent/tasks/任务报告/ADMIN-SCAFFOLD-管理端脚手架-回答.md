# 任务报告 ADMIN-SCAFFOLD — ruoyi-ui 管理端脚手架整理

> 完成时间：2026-04-11
> 执行者：Web Admin Agent

---

## 一、交付清单

| Part | 内容 | 状态 |
|---|---|---|
| **A** 后端菜单驱动 | 删除 `partnerHomeRoute` 硬编码 + permission.js store 清理 | ✅ |
| **B** 响应式 CSS 基础 | 新建 `responsive.scss` 全局生效 | ✅ |
| **C** 公共组件提取 | `JstStatusBadge` + `JstEmptyState` 全局注册 | ✅ |
| **D** partner 默认跳转 | 验证兜底逻辑仍生效，无需修改 | ✅ |
| **E** router 清理 | 随 Part A 完成，无冗余 | ✅ |
| **F** 构建验证 | `npm run build:prod` SUCCESS | ✅ |

---

## 二、改动文件明细

### 2.1 `src/router/index.js`

**删除**：`partnerHomeRoute` 整个 export（约 63 行），包含 9 个子路由的硬编码定义。

**新增**：将 `contest-edit`（新建/编辑）2 个隐藏路由移入 `dynamicRoutes` 数组：
```js
{
  path: '/partner',
  component: Layout,
  hidden: true,
  permissions: ['jst:partner:contest:list'],
  children: [
    { path: 'contest-edit', name: 'PartnerContestCreate', ... },
    { path: 'contest-edit/:contestId(\\d+)', name: 'PartnerContestEdit', ... }
  ]
}
```

**原因**：后端菜单 `99-migration-partner-menus.sql` 只注册了 8 个可见菜单项，contest-edit 是隐藏子页面不在菜单树中，需保留为本地动态路由。使用 `permissions` 字段让 `filterDynamicRoutes` 按权限点过滤，与若依其他隐藏路由（user-auth、role-auth、dict-data 等）保持一致。

**保留**：`partner-apply/*` 3 个公开页路由留在 `constantRoutes`（无需登录）。

### 2.2 `src/store/modules/permission.js`

**删除**：
- `import { partnerHomeRoute }` 引用
- `getPartnerLocalRoutes()` 函数（5 行）
- `cloneRoute()` 函数（10 行）
- `GenerateRoutes` action 中的 partner 注入逻辑（4 行）

**结果**：`GenerateRoutes` 恢复若依原生流程：`getRouters()` → `filterAsyncRouter()` → 注册路由。partner 菜单完全由后端驱动。

### 2.3 `src/assets/styles/responsive.scss`（新建）

全局响应式基础，`@media (max-width: 768px)` 下：
- `.app-container` padding 缩小至 12px
- `.el-table` 横向滚动
- `.el-form--inline` 表单项垂直堆叠
- `.el-dialog` 宽度 95%、距顶 5vh
- `.el-drawer` 全屏
- `.pagination-container` 紧凑布局 + flex 换行
- `.mb8 .el-col` 增加底边距

### 2.4 `src/assets/styles/index.scss`

新增 1 行：`@import './responsive.scss';`

### 2.5 `src/components/JstStatusBadge/index.vue`（新建）

通用状态徽章组件：
- Props：`status`（String）、`statusMap`（Object，格式 `{ key: { label, type } }`）
- Computed：`tagType`、`label`，fallback 为 `info` / 原始值
- 渲染 `<el-tag :type size="small">`

可替代 `contest-list.vue`、`enroll-manage.vue` 等页面中重复的 `AUDIT_STATUS_META` + `el-tag` 模式。

### 2.6 `src/components/JstEmptyState/index.vue`（新建）

空状态封装：
- Props：`description`（默认"暂无数据"）、`imageSize`（默认 120）
- 包裹 `<el-empty>` + 默认 slot（可放操作按钮）

### 2.7 `src/main.js`

新增 4 行：
```js
import JstStatusBadge from '@/components/JstStatusBadge'
import JstEmptyState from '@/components/JstEmptyState'
Vue.component('JstStatusBadge', JstStatusBadge)
Vue.component('JstEmptyState', JstEmptyState)
```

---

## 三、Part D 分析：默认跳转

`src/permission.js` 中的 partner 跳转逻辑**无需修改**：

```js
const shouldUsePartnerHome = (to) => {
  return isPartnerUser() && (to.path === '/' || to.path === '/index')
}
```

在后端菜单驱动模式下，`GenerateRoutes` 完成后 `/partner/home` 路由已由 `filterAsyncRouter` 注册（后端返回 `component: 'partner/home'` → `loadView('partner/home')` → `import('@/views/partner/home')`）。`shouldUsePartnerHome` 仅做角色判断 + 路径匹配，不依赖本地路由定义，因此仍然生效。

---

## 四、构建验证

```
npm run build:prod
 DONE  Build complete. The dist directory is ready to be deployed.
```

- 零 Error
- 2 个 Warning 为若依原生 asset size 警告（login-background.jpg / chunk-elementUI.js 等），非本次引入

---

## 五、后续建议（不在本卡范围）

1. **已有 partner 页面迁移**：`contest-list.vue`、`enroll-manage.vue` 等页面中的内联 `STATUS_META` + `el-tag` 可逐步替换为 `<JstStatusBadge>`，减少约 30 行/页的重复代码
2. **菜单 ID 9708**（账号设置）：后端已注册但前端尚无 `partner/settings.vue` 页面，需后续任务补充
3. **响应式深度适配**：`responsive.scss` 提供了全局基础，各 partner 页面自身的 `@media` 规则（如 contest-list 的卡片布局）仍保留，二者互补
