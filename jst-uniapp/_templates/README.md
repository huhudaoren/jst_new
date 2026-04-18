# 竞赛通小程序页面模板 & 一致性机制

> Wave 1 架构基石 — 让后续**新页 / 改页**强制走统一约束。既有页面在 Wave 2 再迁移。

---

## 一、本目录内容

```
_templates/
├── README.md                 本文件
├── _list-page.vue           列表页模板 (分页 + 下拉 + 上拉 + 空态 + 骨架屏)
├── _detail-page.vue         详情页模板 (Hero + navPaddingTop + 错误态 + 骨架屏)
└── _form-page.vue           表单页模板 (底部固定 CTA + 校验 + 禁用态 + 成功回流)
```

### 为什么放在 `_templates/` 而不是 `pages/_templates/`

uniapp 的 `pages/` 目录受 `pages.json` 管控，但文件结构和 IDE 可能扫描其中文件触发校验。
模板文件带占位符 `__PageName__`，**不应**被当作真实页面编译。放到项目根的 `_templates/` 可确保：

- `pages.json` 不会误注册
- 编译器不会 lint
- 模板重命名 / 修改不影响构建

**使用模板：** 新建页面时，**复制** `_templates/_list-page.vue` 到目标位置，替换占位符，再把路径注册到 `pages.json`。

---

## 二、三件套机制一览

| 机制 | 路径 | 作用 |
|---|---|---|
| 分页 mixin | `mixins/jst-pagination.js` | 统一分页 / 下拉 / 上拉 / loadMoreStatus |
| 页面 helper mixin | `mixins/jst-page-helper.js` | safeAreaBottom / toast 包装 / confirmAsync |
| 全局动作 | `utils/global-actions.js` | 非组件场景的 toast / modal / loading |
| 空态常量 | `utils/empty-state-preset.js` | 13 场景 x (文案 + CTA) |
| 请求增强 | `utils/request-interceptor.js` | 可重试请求 + 业务码映射 |

---

## 三、最小使用示例

### 列表页

```vue
<template>
  <view class="page">
    <jst-skeleton-plus v-if="pageLoading && !list.length" mode="card" :count="3" />
    <block v-else-if="list.length">
      <view v-for="item in list" :key="item.orderId" class="page__card">
        <text>{{ item.orderNo }}</text>
      </view>
      <u-loadmore :status="loadMoreStatus" />
    </block>
    <jst-empty v-else v-bind="EMPTY_ORDERS" />
  </view>
</template>

<script>
import jstPagination from '@/mixins/jst-pagination'
import jstPageHelper from '@/mixins/jst-page-helper'
import { EMPTY_ORDERS } from '@/utils/empty-state-preset'
import { getMyOrders } from '@/api/order'

export default {
  mixins: [jstPagination, jstPageHelper],
  data() {
    return { EMPTY_ORDERS }
  },
  onShow() {
    this.fetchList(true)
  },
  methods: {
    async fetchData({ pageNum, pageSize }) {
      return getMyOrders({ pageNum, pageSize }, { silent: true })
    }
  }
}
</script>
```

### 表单页二次确认

```js
import jstPageHelper from '@/mixins/jst-page-helper'

export default {
  mixins: [jstPageHelper],
  methods: {
    async onDelete() {
      const ok = await this.confirmAsync({
        title: '删除确认',
        content: '删除后不可恢复，确定继续？',
        destructive: true
      })
      if (!ok) return
      this.showToastLoading('删除中...')
      try {
        await api.remove()
        uni.hideLoading()
        this.showToastSuccess('已删除')
      } catch (e) {
        uni.hideLoading()
        this.showToastError('删除失败')
      }
    }
  }
}
```

### 非组件场景用 global-actions

```js
// store / api / utils 里没有 this.$jst 时
import { showToast, withLoading } from '@/utils/global-actions'

export async function uploadFile(file) {
  return withLoading('上传中...', async () => {
    const url = await api.upload(file)
    showToast('success', '上传完成')
    return url
  })
}
```

### 首屏 GET 用可重试请求

```js
// 只读接口首屏聚合, 网络抖动自动重试
import { retryableRequest } from '@/utils/request-interceptor'

export function getDashboard() {
  return retryableRequest(
    { url: '/jst/wx/home/dashboard', method: 'GET', silent: true },
    { maxRetries: 2, timeoutMs: 15000 }
  )
}
```

> ⚠️ 写接口 (下单 / 支付 / 申请) 不要用 `retryableRequest`，重试可能重复下单。

---

## 四、全局注入 $jst (可选)

如果你想在所有组件 `this.$jst.showToast(...)` 这样用，在 `main.js` 加**一行**：

```js
// main.js 新增这行
import jstActions from '@/utils/global-actions'
Vue.prototype.$jst = jstActions
```

> 本次 Wave 1 **不动** main.js。等你确认需要时自己加。模板文件里用 mixin 方式，不依赖 $jst。

---

## 五、模板 → 真实页面的 checklist

复制模板后请：

1. 替换文件头的 `__PageName__` / `__PagePath__` 占位符
2. 替换 `EMPTY_XXX` 为对应场景常量 (见 `empty-state-preset.js`)
3. 改 `fetchData` 调用真实 API
4. 把路径注册到 `pages.json`
5. 新页面 **禁止**：
   - 手写 `pageNum` / `hasMore` / `loadingMore` (已由 mixin 提供)
   - 手写 "暂无数据" (用空态常量)
   - 直接 `uni.showToast` (用 `this.showToastXxx` 或 `global-actions`)
   - 手写重复的 loadMore 状态映射 (mixin 已提供 `loadMoreStatus`)

---

## 六、Wave 1 不处理的事

- 既有 74 页小程序 / 40 页管理端 **一行不动**
- 不删 `jst-empty-state` 组件 (单独任务)
- 不改 `api/request.js` (避免全站回归)
- 不注入 `Vue.prototype.$jst` (等用户确认)

Wave 2 会逐页迁移到此机制。
