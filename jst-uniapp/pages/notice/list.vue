<template>
  <view class="notice-page" :style="{ paddingTop: navPaddingTop }">
    <!-- 骨架屏 -->
    <view v-if="skeletonShow" class="notice-page__skeleton">
      <view class="notice-page__skeleton-header">
        <u-skeleton rows="1" rows-width="200rpx" rows-height="36rpx" :loading="true" :animate="true"></u-skeleton>
        <u-skeleton rows="1" rows-width="160rpx" rows-height="24rpx" :loading="true" :animate="true"></u-skeleton>
      </view>
      <view class="notice-page__skeleton-tabs">
        <u-skeleton rows="1" rows-width="100%" rows-height="40rpx" :loading="true" :animate="true"></u-skeleton>
      </view>
      <view v-for="i in 4" :key="i" class="notice-page__skeleton-card">
        <u-skeleton rows="3" :loading="true" :animate="true"></u-skeleton>
      </view>
    </view>

    <template v-else>
      <view class="notice-page__header">
        <view>
          <text class="notice-page__title">公告资讯</text>
          <text class="notice-page__subtitle">平台公告、赛事通知与成绩发布</text>
        </view>
        <view class="notice-page__search" @tap="toggleSearch">
          <u-icon name="search" :size="20" :color="textSecondary"></u-icon>
        </view>
      </view>

      <!-- 搜索栏 -->
      <view v-if="showSearch" class="notice-page__search-bar">
        <u-search
          v-model="searchKeyword"
          placeholder="搜索公告标题..."
          shape="round"
          :show-action="true"
          action-text="取消"
          :animation="true"
          @search="onSearchConfirm"
          @custom="cancelSearch"
        ></u-search>
      </view>

      <scroll-view class="notice-page__tabs" scroll-x show-scrollbar="false">
        <view class="notice-page__tabs-inner">
          <view v-for="(tab, idx) in categoryTabs" :key="idx" class="notice-page__tab" :class="{ 'notice-page__tab--active': currentCategory === tab.value }" @tap="handleCategoryChange(tab)">{{ tab.label }}</view>
        </view>
      </scroll-view>

      <view v-if="topNotice && !currentCategory" class="notice-page__top" @tap="openNotice(topNotice)">
        <view class="notice-page__top-icon">📌</view>
        <view class="notice-page__top-body">
          <text class="notice-page__top-title">{{ topNotice.title }}</text>
          <text class="notice-page__top-desc">{{ topNotice.summary || '点击查看置顶公告详情' }}</text>
        </view>
        <u-tag text="置顶" type="error" plain :plain-fill="true" size="mini" shape="circle" />
      </view>

      <view class="notice-page__list">
        <jst-loading :loading="firstLoading" text="公告加载中..." />

        <view v-for="(notice, nIdx) in noticeList" :key="notice.noticeId" class="jst-anim-slide-up" :style="{ animationDelay: getStaggerDelay(nIdx) }">
          <jst-notice-card class="notice-page__card" :notice="notice" @item-tap="openNotice" />
        </view>

        <jst-empty
          v-if="!firstLoading && !noticeList.length"
          :illustration="EMPTY_MESSAGES.illustration"
          text="暂无公告数据"
          :button-text="EMPTY_MESSAGES.buttonText"
          :button-url="EMPTY_MESSAGES.buttonUrl"
        />

        <u-loadmore
          v-if="noticeList.length"
          :status="loadMoreState"
          :loading-text="'加载中...'"
          :loadmore-text="'点击加载更多'"
          :nomore-text="'没有更多公告了'"
          @loadmore="handleLoadMore"
        />
      </view>
    </template>
  </view>
</template>

<script>
import { getDict, getNoticeList } from '@/api/notice'
import JstNoticeCard from '@/components/jst-notice-card/jst-notice-card.vue'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
// [visual-effect]
import { staggerDelay } from '@/utils/visual-effects'
import { EMPTY_MESSAGES } from '@/utils/empty-state-preset'

const FALLBACK_CATEGORY_TABS = [
  { label: '全部', value: '' },
  { label: '平台公告', value: 'platform' },
  { label: '赛事通知', value: 'contest' },
  { label: '成绩发布', value: 'score' },
  { label: '证书通知', value: 'certificate' }
]

export default {
  components: { JstNoticeCard, JstEmpty, JstLoading },
  data() {
    return {
      EMPTY_MESSAGES,
      categoryTabs: FALLBACK_CATEGORY_TABS,
      currentCategory: '',
      noticeList: [],
      topNotice: null,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      firstLoading: false,
      loadingMore: false,
      loadedOnce: false,
      showSearch: false,
      searchKeyword: '',
      skeletonShow: true, // [visual-effect]
      textSecondary: '#8A8A8A' // [visual-effect] u-icon color prop
    }
  },
  computed: {
    // [visual-effect] u-loadmore 状态映射
    loadMoreState() {
      if (this.loadingMore) return 'loading'
      if (this.noticeList.length >= this.total && this.total > 0) return 'nomore'
      return 'loadmore'
    },
    loadMoreText() {
      if (this.loadingMore) return '加载中...'
      if (this.noticeList.length >= this.total && this.total > 0) return '没有更多公告了'
      return '点击加载更多'
    }
  },
  onLoad() { this.initPage() },
  onShow() { if (this.loadedOnce) this.refreshList() },
  onReachBottom() { this.handleLoadMore() },
  methods: {
    async initPage() { await Promise.allSettled([this.loadCategories(), this.refreshList()]); this.skeletonShow = false /* [visual-effect] */ },
    async loadCategories() {
      try {
        const list = await getDict('jst_notice_category')
        const tabs = Array.isArray(list) ? list.map((item) => ({ label: item.label || item.dictLabel || item.name || '未命名分类', value: item.value || item.dictValue || '' })).filter((item) => item.label) : []
        this.categoryTabs = [{ label: '全部', value: '' }].concat(tabs)
      } catch (error) { this.categoryTabs = FALLBACK_CATEGORY_TABS }
    },
    async refreshList() {
      this.pageNum = 1
      this.total = 0
      this.noticeList = []
      this.topNotice = null
      this.firstLoading = true
      try {
        await this.fetchNoticeList()
        this.loadedOnce = true
      } finally { this.firstLoading = false }
    },
    async fetchNoticeList() {
      const response = await getNoticeList({ category: this.currentCategory || undefined, pageNum: this.pageNum, pageSize: this.pageSize, keyword: this.searchKeyword || undefined })
      const rows = this.extractRows(response).map(this.normalizeNotice)
      const incomingTopNotice = response && response.topNotice ? this.normalizeNotice(response.topNotice) : null
      if (this.pageNum === 1) {
        this.topNotice = incomingTopNotice || rows.find((item) => item.topFlag) || null
        this.noticeList = this.deduplicateRows(rows, this.topNotice)
      } else {
        this.noticeList = this.noticeList.concat(this.deduplicateRows(rows))
      }
      const responseTotal = response && typeof response.total === 'number' ? response.total : this.noticeList.length
      this.total = this.pageNum === 1 && !incomingTopNotice && this.topNotice ? Math.max(responseTotal - 1, 0) : responseTotal
    },
    extractRows(response) { if (Array.isArray(response)) return response; if (response && Array.isArray(response.rows)) return response.rows; return [] },
    deduplicateRows(rows = [], topNotice) { const topNoticeId = topNotice && topNotice.noticeId; return rows.filter((item) => item.noticeId && item.noticeId !== topNoticeId) },
    normalizeNotice(item = {}) { return { noticeId: item.noticeId || item.id || '', title: item.title || '', category: item.category || '', categoryLabel: item.categoryLabel || this.getCategoryLabel(item.category), coverImage: item.coverImage || item.image || '', topFlag: Boolean(item.topFlag), publishTime: item.publishTime || item.createTime || '', summary: item.summary || '' } },
    getCategoryLabel(category) { const categoryMap = { platform: '平台公告', contest: '赛事通知', score: '成绩发布', certificate: '证书通知' }; return categoryMap[category] || '公告资讯' },
    async handleCategoryChange(tab) { if (!tab || this.currentCategory === tab.value || this.firstLoading) return; this.currentCategory = tab.value; await this.refreshList() },
    async handleLoadMore() { if (this.firstLoading || this.loadingMore) return; if (this.total > 0 && this.noticeList.length >= this.total) return; this.loadingMore = true; this.pageNum += 1; try { await this.fetchNoticeList() } catch (error) { this.pageNum -= 1 } finally { this.loadingMore = false } },
    openNotice(notice) { if (!notice || !notice.noticeId) return; uni.navigateTo({ url: `/pages-sub/notice/detail?id=${notice.noticeId}` }) },
    toggleSearch() { this.showSearch = !this.showSearch; if (!this.showSearch) { this.searchKeyword = ''; this.refreshList() } },
    cancelSearch() { this.showSearch = false; this.searchKeyword = ''; this.refreshList() },
    onSearchConfirm() {
      // 后端若不支持 keyword，做前端本地 filter 兜底
      this.refreshList()
    },
    // [visual-effect]
    getStaggerDelay(index) {
      return 'animation-delay:' + staggerDelay(index)
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.notice-page {
  min-height: 100vh;
  padding: $jst-space-lg $jst-space-lg 140rpx;
  background: $jst-bg-page;
}

// 骨架屏
.notice-page__skeleton {
  padding-top: $jst-space-lg;
}
.notice-page__skeleton-header {
  margin-bottom: $jst-space-lg;
}
.notice-page__skeleton-tabs {
  margin-bottom: $jst-space-lg;
}
.notice-page__skeleton-card {
  margin-bottom: $jst-space-md;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
}

.notice-page__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-page__title {
  display: block;
  font-size: $jst-font-xl;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.notice-page__subtitle {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.notice-page__search {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.notice-page__search-bar {
  margin-top: $jst-space-lg;
}

.notice-page__tabs {
  margin-top: $jst-space-lg;
}

.notice-page__tabs-inner {
  display: inline-flex;
  gap: $jst-space-sm;
}

.notice-page__tab {
  padding: $jst-space-sm $jst-space-lg;
  border-radius: $jst-radius-round;
  background: $jst-bg-card;
  color: $jst-text-secondary;
  font-size: $jst-font-sm;
  transition: background $jst-duration-fast $jst-easing, color $jst-duration-fast $jst-easing;
}

.notice-page__tab--active {
  background: $jst-brand-light;
  color: $jst-brand;
  font-weight: $jst-weight-semibold;
}

.notice-page__top {
  display: flex;
  gap: $jst-space-md;
  margin-top: $jst-space-lg;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: linear-gradient(135deg, $jst-warning-light 0%, lighten($jst-warning-light, 2%) 100%);
  transition: transform $jst-duration-fast $jst-easing;
}
.notice-page__top:active { transform: scale(0.99); }

.notice-page__top-icon {
  font-size: $jst-font-lg;
  flex-shrink: 0;
}

.notice-page__top-body {
  flex: 1;
}

.notice-page__top-title {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  line-height: 1.5;
  color: $jst-text-primary;
}

.notice-page__top-desc {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  line-height: 1.6;
  color: $jst-text-secondary;
}

.notice-page__list {
  margin-top: $jst-space-lg;
}

.notice-page__card + .notice-page__card {
  margin-top: $jst-space-md;
}
</style>
