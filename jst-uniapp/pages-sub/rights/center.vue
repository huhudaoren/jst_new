<!-- 中文注释: 我的权益
     对应原型: 小程序原型图/rights-center.html
     调用接口: GET /jst/wx/rights/my?status= -->
<template>
  <view class="rc-page">
    <view class="rc-hero">
      <view class="rc-hero__cell"><text class="rc-hero__num">{{ stats.available || 0 }}</text><text class="rc-hero__lbl">可用权益</text></view>
      <view class="rc-hero__cell"><text class="rc-hero__num">{{ stats.applying || 0 }}</text><text class="rc-hero__lbl">申请中</text></view>
      <view class="rc-hero__cell"><text class="rc-hero__num">{{ stats.used || 0 }}</text><text class="rc-hero__lbl">已使用</text></view>
    </view>

    <scroll-view class="rc-tabs" scroll-x>
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['rc-tabs__item', activeStatus === tab.value && 'rc-tabs__item--active']"
        @tap="onChange(tab.value)"
      >{{ tab.label }}</view>
    </scroll-view>

    <view class="rc-list">
      <view v-for="item in list" :key="item.userRightsId" class="rc-card" @tap="goDetail(item.userRightsId)">
        <view class="rc-card__head">
          <text class="rc-card__name">{{ item.rightsName || '--' }}</text>
          <text :class="['rc-card__status', 'rc-card__status--' + item.status]">{{ statusLabel(item.status) }}</text>
        </view>
        <view class="rc-card__meta">
          <text v-if="item.remainingAmount != null" class="rc-card__m">剩余额度 ¥{{ item.remainingAmount }}</text>
          <text v-if="item.remainingCount != null" class="rc-card__m">剩余次数 {{ item.remainingCount }}</text>
          <text class="rc-card__m">有效期至 {{ formatDate(item.validEnd) }}</text>
        </view>
        <button v-if="item.status === 'available'" class="rc-card__btn" @tap.stop="goApply(item.userRightsId)">立即使用</button>
      </view>
      <view v-if="!list.length && !loading" class="rc-empty">暂无权益</view>
      <view v-if="loading" class="rc-empty">加载中...</view>
      <view v-if="!hasMore && list.length" class="rc-empty rc-empty--end">没有更多了</view>
    </view>
  </view>
</template>

<script>
import { getMyRights } from '@/api/rights'

const TABS = [
  { value: 'available', label: '可用' },
  { value: 'applying', label: '申请中' },
  { value: 'used', label: '已使用' },
  { value: 'expired', label: '已过期' }
]
const STATUS_LABEL = { available: '可用', applying: '申请中', used: '已使用', expired: '已过期' }

export default {
  data() { return { tabs: TABS, activeStatus: 'available', list: [], stats: { available: 0, applying: 0, used: 0 }, pageNum: 1, pageSize: 10, total: 0, loading: false, hasMore: true } },
  onShow() { this.load(true) },
  onPullDownRefresh() { this.load(true).finally(() => uni.stopPullDownRefresh()) },
  onReachBottom() { if (this.hasMore && !this.loading) this.load(false) },
  methods: {
    async load(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await getMyRights({ status: this.activeStatus, pageNum: this.pageNum, pageSize: this.pageSize })
        const rows = (res && res.rows) || []
        this.total = (res && res.total) || 0
        if (res && res.stats) this.stats = res.stats
        this.list = reset ? rows : this.list.concat(rows)
        this.hasMore = this.list.length < this.total
        if (this.hasMore) this.pageNum += 1
      } finally { this.loading = false }
    },
    onChange(v) { if (this.activeStatus === v) return; this.activeStatus = v; this.load(true) },
    goDetail(id) { uni.navigateTo({ url: '/pages-sub/rights/detail?id=' + id }) },
    goApply(id) { uni.navigateTo({ url: '/pages-sub/rights/writeoff-apply?id=' + id }) },
    formatDate(v) { if (!v) return '--'; return String(v).slice(0, 10) },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' }
  }
}
</script>

<style scoped lang="scss">
.rc-page { min-height: 100vh; background: var(--jst-color-page-bg); padding-bottom: 48rpx; }
.rc-hero { display: flex; padding: 72rpx 32rpx 56rpx; background: linear-gradient(135deg, #1B5E20, #2E7D32); color: #fff; border-bottom-left-radius: 40rpx; border-bottom-right-radius: 40rpx; }
.rc-hero__cell { flex: 1; text-align: center; }
.rc-hero__num { display: block; font-size: 48rpx; font-weight: 900; }
.rc-hero__lbl { display: block; margin-top: 6rpx; font-size: 22rpx; color: var(--jst-color-white-76); }

.rc-tabs { white-space: nowrap; margin-top: 16rpx; background: var(--jst-color-card-bg); border-bottom: 2rpx solid var(--jst-color-border); }
.rc-tabs__item { display: inline-block; padding: 0 40rpx; height: 88rpx; line-height: 88rpx; font-size: 26rpx; color: var(--jst-color-text-tertiary); position: relative; }
.rc-tabs__item--active { color: #1B5E20; font-weight: 700; }
.rc-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 20rpx; right: 20rpx; height: 4rpx; background: #1B5E20; border-radius: 2rpx; }

.rc-list { padding: 8rpx 0 32rpx; }
.rc-card { margin: 20rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.rc-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.rc-card__name { font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); flex: 1; min-width: 0; }
.rc-card__status { padding: 4rpx 16rpx; border-radius: var(--jst-radius-full); font-size: 22rpx; background: var(--jst-color-success-soft); color: #1B5E20; }
.rc-card__status--applying { background: var(--jst-color-warning-soft); color: var(--jst-color-warning); }
.rc-card__status--used, .rc-card__status--expired { background: var(--jst-color-gray-soft); color: var(--jst-color-text-tertiary); }
.rc-card__meta { display: flex; flex-wrap: wrap; gap: 12rpx; }
.rc-card__m { padding: 6rpx 14rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-page-bg); font-size: 22rpx; color: var(--jst-color-text-secondary); }
.rc-card__btn { margin-top: 20rpx; align-self: flex-end; height: 72rpx; line-height: 72rpx; border-radius: var(--jst-radius-full); background: linear-gradient(135deg, #1B5E20, #2E7D32); color: #fff; font-size: 26rpx; font-weight: 700; border: none; }
.rc-empty { padding: 80rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.rc-empty--end { padding: 40rpx; }
</style>
