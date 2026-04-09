<!-- 中文注释: 渠道方 · 我的提现单列表
     调用接口: GET /jst/wx/channel/withdraw/list?status=&pageNum=&pageSize= -->
<template>
  <view class="wl-page">
    <scroll-view class="wl-tabs" scroll-x>
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['wl-tabs__item', activeStatus === tab.value && 'wl-tabs__item--active']"
        @tap="onChange(tab.value)"
      >{{ tab.label }}</view>
    </scroll-view>

    <view class="wl-list">
      <view v-for="item in list" :key="item.settlementId" class="wl-card" @tap="goDetail(item)">
        <view class="wl-card__head">
          <text class="wl-card__no">{{ item.settlementNo }}</text>
          <text :class="['wl-card__status', 'wl-card__status--' + item.status]">{{ statusLabel(item.status) }}</text>
        </view>
        <view class="wl-card__body">
          <view class="wl-card__money">
            <text class="wl-card__amount">¥{{ formatAmount(item.applyAmount) }}</text>
            <text class="wl-card__sub">实付 ¥{{ formatAmount(item.actualPayAmount) }}</text>
          </view>
          <text class="wl-card__time">{{ formatTime(item.applyTime) }}</text>
        </view>
      </view>
      <view v-if="!list.length && !loading" class="wl-empty">暂无提现记录</view>
      <view v-if="loading" class="wl-empty">加载中...</view>
      <view v-if="!hasMore && list.length" class="wl-empty wl-empty--end">没有更多了</view>
    </view>
  </view>
</template>

<script>
import { getWithdrawList } from '@/api/channel'

const TABS = [
  { value: '', label: '全部' },
  { value: 'pending', label: '待审核' },
  { value: 'in_review', label: '审核中' },
  { value: 'paid', label: '已打款' },
  { value: 'rejected', label: '已驳回' },
  { value: 'cancelled', label: '已取消' }
]
const STATUS_LABEL = { pending: '待审核', in_review: '审核中', paid: '已打款', rejected: '已驳回', cancelled: '已取消' }

export default {
  data() {
    return { tabs: TABS, activeStatus: '', list: [], pageNum: 1, pageSize: 10, total: 0, loading: false, hasMore: true }
  },
  onLoad() { this.load(true) },
  onShow() {
    // 从详情页返回时刷新
    if (this.list.length) this.load(true)
  },
  onPullDownRefresh() { this.load(true).finally(() => uni.stopPullDownRefresh()) },
  onReachBottom() { if (this.hasMore && !this.loading) this.load(false) },
  methods: {
    async load(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await getWithdrawList({
          status: this.activeStatus || undefined,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        })
        const rows = (res && res.rows) || []
        this.total = (res && res.total) || 0
        this.list = reset ? rows : this.list.concat(rows)
        this.hasMore = this.list.length < this.total
        if (this.hasMore) this.pageNum += 1
      } finally { this.loading = false }
    },
    onChange(v) { if (this.activeStatus === v) return; this.activeStatus = v; this.load(true) },
    goDetail(item) { uni.navigateTo({ url: '/pages-sub/channel/withdraw-detail?id=' + item.settlementId }) },
    formatAmount(v) { if (v == null || v === '') return '0.00'; return String(v) },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' }
  }
}
</script>

<style scoped lang="scss">
.wl-page { min-height: 100vh; background: var(--jst-color-page-bg); }
.wl-tabs { white-space: nowrap; background: var(--jst-color-card-bg); border-bottom: 2rpx solid var(--jst-color-border); }
.wl-tabs__item { display: inline-block; padding: 0 32rpx; height: 88rpx; line-height: 88rpx; font-size: 26rpx; color: var(--jst-color-text-tertiary); position: relative; }
.wl-tabs__item--active { color: var(--jst-color-brand); font-weight: 700; }
.wl-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 24rpx; right: 24rpx; height: 4rpx; background: var(--jst-color-brand); border-radius: 2rpx; }

.wl-list { padding: 8rpx 0 32rpx; }
.wl-card { margin: 20rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.wl-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.wl-card__no { font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.wl-card__status { padding: 4rpx 16rpx; border-radius: var(--jst-radius-full); font-size: 22rpx; background: var(--jst-color-brand-soft); color: var(--jst-color-brand); }
.wl-card__status--pending, .wl-card__status--in_review { background: var(--jst-color-warning-soft); color: var(--jst-color-warning); }
.wl-card__status--paid { background: var(--jst-color-success-soft); color: var(--jst-color-success); }
.wl-card__status--rejected, .wl-card__status--cancelled { background: var(--jst-color-gray-soft); color: var(--jst-color-text-tertiary); }
.wl-card__body { display: flex; justify-content: space-between; align-items: flex-end; }
.wl-card__money { display: flex; flex-direction: column; }
.wl-card__amount { font-size: 40rpx; font-weight: 800; color: #F5A623; }
.wl-card__sub { margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.wl-card__time { font-size: 22rpx; color: var(--jst-color-text-tertiary); }

.wl-empty { padding: 60rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.wl-empty--end { padding: 40rpx; }
</style>
