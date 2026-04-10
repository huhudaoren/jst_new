<!-- 中文注释: 公告消息中心(合并通知+公告)；对应原型 小程序原型图/notice-msg.html；对应接口 GET /jst/wx/notice/list -->
<template>
  <view class="notice-msg-page">
    <view class="notice-msg-page__header">
      <view class="notice-msg-page__back" @tap="goBack">←</view>
      <text class="notice-msg-page__header-title">公告消息</text>
      <view class="notice-msg-page__read-all" @tap="handleReadAll">全部已读</view>
    </view>

    <!-- Tab -->
    <scroll-view class="notice-msg-page__tabs" scroll-x>
      <view class="notice-msg-page__tabs-inner">
        <view
          v-for="tab in tabs"
          :key="tab.value"
          class="notice-msg-page__tab"
          :class="{ 'notice-msg-page__tab--active': currentTab === tab.value }"
          @tap="switchTab(tab.value)"
        >
          {{ tab.label }}
        </view>
      </view>
    </scroll-view>

    <jst-loading :loading="loading" text="公告加载中..." />

    <view v-if="noticeList.length" class="notice-msg-page__list">
      <view
        v-for="item in noticeList"
        :key="item.noticeId"
        class="notice-msg-page__item"
        @tap="openNotice(item)"
      >
        <view class="notice-msg-page__item-tag" :class="'notice-msg-page__item-tag--' + getTagTheme(item.category)">
          {{ item.categoryLabel || '公告' }}
        </view>
        <view class="notice-msg-page__item-body">
          <text class="notice-msg-page__item-title">{{ item.title }}</text>
          <text class="notice-msg-page__item-summary">{{ item.summary || '' }}</text>
        </view>
        <text class="notice-msg-page__item-time">{{ formatDate(item.publishTime) }}</text>
      </view>
    </view>

    <jst-empty v-else-if="!loading" text="暂无公告消息" icon="📭" />

    <view v-if="noticeList.length && hasMore" class="notice-msg-page__load-more" @tap="loadMore">
      {{ loadingMore ? '加载中...' : '加载更多' }}
    </view>
  </view>
</template>

<script>
import { getNoticeList } from '@/api/notice'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: { JstEmpty, JstLoading },
  data() {
    return {
      loading: false,
      loadingMore: false,
      noticeList: [],
      currentTab: 'all',
      pageNum: 1,
      pageSize: 10,
      total: 0,
      tabs: [
        { label: '全部', value: 'all' },
        { label: '报名', value: 'contest' },
        { label: '审核', value: 'platform' },
        { label: '成绩', value: 'score' },
        { label: '证书', value: 'certificate' }
      ]
    }
  },
  computed: {
    hasMore() {
      return this.noticeList.length < this.total
    }
  },
  onLoad() {
    this.refreshList()
  },
  methods: {
    async refreshList() {
      this.pageNum = 1
      this.noticeList = []
      this.total = 0
      this.loading = true
      try {
        await this.fetchList()
      } finally {
        this.loading = false
      }
    },
    async fetchList() {
      const params = { pageNum: this.pageNum, pageSize: this.pageSize }
      if (this.currentTab !== 'all') params.category = this.currentTab
      const res = await getNoticeList(params)
      const rows = Array.isArray(res) ? res : (res && Array.isArray(res.rows) ? res.rows : [])
      const mapped = rows.map(item => ({
        noticeId: item.noticeId || item.id || '',
        title: item.title || '',
        summary: item.summary || '',
        category: item.category || '',
        categoryLabel: item.categoryLabel || this.getCategoryLabel(item.category),
        publishTime: item.publishTime || item.createTime || ''
      }))
      this.noticeList = this.pageNum === 1 ? mapped : this.noticeList.concat(mapped)
      this.total = (res && typeof res.total === 'number') ? res.total : this.noticeList.length
    },
    switchTab(val) {
      if (this.currentTab === val) return
      this.currentTab = val
      this.refreshList()
    },
    async loadMore() {
      if (this.loadingMore || !this.hasMore) return
      this.loadingMore = true
      this.pageNum++
      try { await this.fetchList() } catch (e) { this.pageNum-- }
      finally { this.loadingMore = false }
    },
    getCategoryLabel(cat) {
      const map = { platform: '平台公告', contest: '赛事通知', score: '成绩发布', certificate: '证书通知' }
      return map[cat] || '公告资讯'
    },
    getTagTheme(cat) {
      const map = { platform: 'red', contest: 'blue', score: 'gold', certificate: 'green' }
      return map[cat] || 'gray'
    },
    formatDate(val) {
      if (!val) return ''
      const d = new Date(val)
      if (isNaN(d.getTime())) return val
      return `${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },
    openNotice(item) {
      if (!item.noticeId) return
      uni.navigateTo({ url: `/pages-sub/notice/detail?id=${item.noticeId}` })
    },
    handleReadAll() {
      uni.showToast({ title: '已全部标记为已读', icon: 'none' })
    },
    goBack() {
      if (getCurrentPages().length > 1) { uni.navigateBack(); return }
      uni.switchTab({ url: '/pages/notice/list' })
    }
  }
}
</script>

<style scoped lang="scss">
.notice-msg-page { min-height: 100vh; padding-bottom: 60rpx; background: var(--jst-color-page-bg); }

.notice-msg-page__header { display: flex; align-items: center; padding: 24rpx; background: var(--jst-color-card-bg); }
.notice-msg-page__back { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 22rpx; background: var(--jst-color-page-bg); font-size: 30rpx; color: var(--jst-color-text); }
.notice-msg-page__header-title { flex: 1; margin-left: 16rpx; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }
.notice-msg-page__read-all { font-size: 26rpx; font-weight: 600; color: var(--jst-color-brand); }

/* Tabs */
.notice-msg-page__tabs { margin-top: 16rpx; padding: 0 24rpx; }
.notice-msg-page__tabs-inner { display: inline-flex; gap: 16rpx; }
.notice-msg-page__tab { padding: 12rpx 24rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-card-bg); font-size: 24rpx; color: var(--jst-color-text-secondary); white-space: nowrap; }
.notice-msg-page__tab--active { background: var(--jst-color-brand-soft); color: var(--jst-color-brand); font-weight: 700; }

/* 列表 */
.notice-msg-page__list { padding: 24rpx; }
.notice-msg-page__item { display: flex; gap: 16rpx; padding: 24rpx; border-radius: var(--jst-radius-lg); background: var(--jst-color-card-bg); margin-bottom: 16rpx; box-shadow: 0 2rpx 8rpx rgba(20,30,60,0.04); }

.notice-msg-page__item-tag { display: flex; align-items: center; justify-content: center; padding: 8rpx 16rpx; border-radius: var(--jst-radius-full); font-size: 20rpx; font-weight: 700; flex-shrink: 0; align-self: flex-start; margin-top: 4rpx; }
.notice-msg-page__item-tag--blue { background: var(--jst-color-brand-soft); color: var(--jst-color-brand); }
.notice-msg-page__item-tag--red { background: var(--jst-color-danger-soft); color: var(--jst-color-danger); }
.notice-msg-page__item-tag--gold { background: var(--jst-color-warning-soft); color: var(--jst-color-warning); }
.notice-msg-page__item-tag--green { background: var(--jst-color-success-soft); color: var(--jst-color-success); }
.notice-msg-page__item-tag--gray { background: var(--jst-color-page-bg); color: var(--jst-color-text-tertiary); }

.notice-msg-page__item-body { flex: 1; min-width: 0; }
.notice-msg-page__item-title { display: block; font-size: 28rpx; font-weight: 600; color: var(--jst-color-text); line-height: 1.4; }
.notice-msg-page__item-summary { display: block; margin-top: 8rpx; font-size: 24rpx; color: var(--jst-color-text-secondary); line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.notice-msg-page__item-time { font-size: 22rpx; color: var(--jst-color-text-tertiary); flex-shrink: 0; white-space: nowrap; }

.notice-msg-page__load-more { padding: 28rpx 0; text-align: center; font-size: 24rpx; color: var(--jst-color-brand); }
</style>
