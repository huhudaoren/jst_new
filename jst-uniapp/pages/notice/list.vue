<template>
  <view class="notice-page">
    <view class="notice-page__header">
      <view>
        <text class="notice-page__title">公告资讯</text>
        <text class="notice-page__subtitle">平台公告、赛事通知与成绩发布</text>
      </view>
      <view class="notice-page__search" @tap="toggleSearch">🔍</view>
    </view>

    <!-- 搜索栏 -->
    <view v-if="showSearch" class="notice-page__search-bar">
      <input
        class="notice-page__search-input"
        v-model="searchKeyword"
        placeholder="搜索公告标题..."
        confirm-type="search"
        focus
        @confirm="onSearchConfirm"
      />
      <text class="notice-page__search-cancel" @tap="cancelSearch">取消</text>
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
      <text class="notice-page__top-tag">置顶</text>
    </view>

    <view class="notice-page__list">
      <jst-loading :loading="firstLoading" text="公告加载中..." />
      <jst-notice-card v-for="notice in noticeList" :key="notice.noticeId" class="notice-page__card" :notice="notice" @item-tap="openNotice" />
      <jst-empty v-if="!firstLoading && !noticeList.length" text="暂无公告数据" icon="📭" />
      <view v-if="noticeList.length" class="notice-page__load-more" @tap="handleLoadMore">{{ loadMoreText }}</view>
    </view>
  </view>
</template>

<script>
import { getDict, getNoticeList } from '@/api/notice'
import JstNoticeCard from '@/components/jst-notice-card/jst-notice-card.vue'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

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
      searchKeyword: ''
    }
  },
  computed: {
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
    async initPage() { await Promise.allSettled([this.loadCategories(), this.refreshList()]) },
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
    }
  }
}
</script>

<style scoped lang="scss">
.notice-page { min-height: 100vh; padding: 24rpx 24rpx 140rpx; background: var(--jst-color-page-bg); }
.notice-page__header { display: flex; justify-content: space-between; align-items: center; }
.notice-page__title { display: block; font-size: 40rpx; font-weight: 800; color: var(--jst-color-text); }
.notice-page__subtitle { display: block; margin-top: 10rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.notice-page__search { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 22rpx; background: var(--jst-color-card-bg); box-shadow: var(--jst-shadow-card); font-size: 28rpx; }
.notice-page__search-bar { display: flex; align-items: center; gap: 16rpx; margin-top: 20rpx; }
.notice-page__search-input { flex: 1; height: 80rpx; padding: 0 24rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-card-bg); border: 2rpx solid var(--jst-color-border); font-size: 26rpx; }
.notice-page__search-cancel { font-size: 26rpx; color: var(--jst-color-brand); font-weight: 600; flex-shrink: 0; }
.notice-page__tabs { margin-top: 24rpx; }
.notice-page__tabs-inner { display: inline-flex; gap: 16rpx; }
.notice-page__tab { padding: 12rpx 24rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-card-bg); color: var(--jst-color-text-secondary); font-size: 24rpx; }
.notice-page__tab--active { background: var(--jst-color-brand-soft); color: var(--jst-color-brand); font-weight: 700; }
.notice-page__top { display: flex; gap: 16rpx; margin-top: 24rpx; padding: 24rpx; border-radius: var(--jst-radius-lg); background: linear-gradient(135deg, var(--jst-color-primary-soft) 0%, #fff7f0 100%); }
.notice-page__top-body { flex: 1; }
.notice-page__top-title { display: block; font-size: 28rpx; font-weight: 700; line-height: 1.5; color: var(--jst-color-text); }
.notice-page__top-desc { display: block; margin-top: 10rpx; font-size: 22rpx; line-height: 1.6; color: var(--jst-color-text-secondary); }
.notice-page__top-tag { align-self: flex-start; padding: 6rpx 16rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-primary); color: var(--jst-color-card-bg); font-size: 20rpx; }
.notice-page__list { margin-top: 24rpx; }
.notice-page__card + .notice-page__card { margin-top: 20rpx; }
.notice-page__load-more { padding: 28rpx 0 0; text-align: center; font-size: 24rpx; color: var(--jst-color-brand); }
</style>