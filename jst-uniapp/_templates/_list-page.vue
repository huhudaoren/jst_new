<!--
  ═══════════════════════════════════════════════════════════════
  列表页模板 — 复制我, 重命名, 替换 __PageName__
  ═══════════════════════════════════════════════════════════════

  覆盖场景:
    - 下拉刷新 + 上拉加载 (mixin 内置)
    - 首屏骨架屏 + 列表态 + 空态三段切换
    - 筛选 Tab (可删)
    - 底部固定 safe-area-inset

  替换 checklist:
    1) 把 __PageName__ 换成实际组件名 (如 MyOrders)
    2) 改 __API__ 换成真实 api 函数
    3) 改 EMPTY_XXX 换成对应 empty-state-preset 常量
    4) 把本文件另存到 pages/ 或 pages-sub/ 目录, 在 pages.json 登记
    5) 若不需要 tab 筛选, 删除 <scroll-view class="page__tabs"> 整块
-->
<template>
  <view class="page">
    <!-- 自定义导航 (配合 navigationStyle:custom; 若用原生导航可删) -->
    <view class="page__nav" :style="{ paddingTop: navPaddingTop }">
      <view class="page__back" @tap="handleBack">←</view>
      <text class="page__title">__页面标题__</text>
      <view class="page__nav-placeholder"></view>
    </view>

    <!-- 可选: Tab 筛选 (不需要则删) -->
    <scroll-view class="page__tabs" scroll-x>
      <view class="page__tabs-inner">
        <view
          v-for="t in tabs"
          :key="t.value"
          class="page__tab"
          :class="{ 'page__tab--active': activeTab === t.value }"
          @tap="onTabChange(t.value)"
        >{{ t.label }}</view>
      </view>
    </scroll-view>

    <!-- 首屏骨架屏 -->
    <jst-skeleton-plus v-if="pageLoading && !list.length" mode="card" :count="3" />

    <!-- 列表 -->
    <block v-else-if="list.length">
      <view class="page__list">
        <view
          v-for="item in list"
          :key="item.id"
          class="page__card"
          @tap="openDetail(item)"
        >
          <text class="page__card-title">{{ item.title || '--' }}</text>
          <text class="page__card-meta">{{ item.summary || '' }}</text>
        </view>
      </view>
      <u-loadmore :status="loadMoreStatus" />
    </block>

    <!-- 空态: 统一常量, 有 CTA -->
    <jst-empty v-else v-bind="emptyPreset" @action="onEmptyAction" />

    <!-- 底部安全区 -->
    <view class="page__safe" :style="{ height: safeAreaBottom }"></view>
  </view>
</template>

<script>
import jstPagination from '@/mixins/jst-pagination'
import jstPageHelper from '@/mixins/jst-page-helper'
import { EMPTY_DEFAULT } from '@/utils/empty-state-preset'
// import { __API__ } from '@/api/__module__'  // TODO: 替换为真实 API

export default {
  name: '__PageName__',
  mixins: [jstPagination, jstPageHelper],
  data() {
    return {
      tabs: [
        { label: '全部', value: '' }
        // TODO: 其他 tab
      ],
      activeTab: '',
      emptyPreset: EMPTY_DEFAULT  // TODO: 换成实际场景常量 (EMPTY_ORDERS / EMPTY_ENROLL ...)
    }
  },
  onShow() {
    this.fetchList(true)
  },
  methods: {
    /**
     * jst-pagination mixin 必须实现的方法
     * 返回 { rows, total }
     */
    async fetchData({ pageNum, pageSize }) {
      const params = { pageNum, pageSize }
      if (this.activeTab) params.status = this.activeTab
      // TODO: 调用真实 API
      // return __API__(params, { silent: true })
      return { rows: [], total: 0 }
    },

    onTabChange(v) {
      if (this.activeTab === v) return
      this.activeTab = v
      this.fetchList(true)
    },

    openDetail(item) {
      uni.navigateTo({ url: '/pages-sub/xxx/detail?id=' + item.id })
    },

    onEmptyAction() {
      // 空态无 url 时交页面处理, 常用于"重新加载"
      this.fetchList(true)
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.switchTab({ url: '/pages/my/index' })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.page {
  min-height: 100vh;
  background: $jst-bg-page;
}

.page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-page-padding;
  background: linear-gradient(135deg, $jst-brand-dark 0%, $jst-brand 100%);
  position: sticky;
  top: 0;
  z-index: 40;
}

.page__back,
.page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.page__back {
  font-size: 36rpx;
  color: $jst-text-inverse;
}

.page__title {
  flex: 1;
  text-align: center;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
}

.page__tabs {
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
  white-space: nowrap;
}

.page__tabs-inner {
  display: inline-flex;
  padding: $jst-space-md $jst-space-md $jst-space-sm;
}

.page__tab {
  min-width: 136rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: $jst-space-sm;
  padding: 0 $jst-space-lg;
  border-radius: $jst-radius-round;
  background: $jst-bg-page;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.page__tab--active {
  background: $jst-brand-light;
  color: $jst-brand;
  font-weight: $jst-weight-bold;
}

.page__list {
  padding: $jst-space-lg $jst-page-padding;
}

.page__card {
  padding: $jst-space-lg;
  margin-bottom: $jst-space-md;
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-ambient;
}

.page__card-title {
  display: block;
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.page__card-meta {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.page__safe {
  width: 100%;
}
</style>
