<!-- 中文注释: 优惠券中心 (我的券)
     对应原型: 小程序原型图/coupon-center.html
     调用接口: GET /jst/wx/coupon/my?status= -->
<template>
  <view class="cc-page">
    <view class="cc-header">
      <scroll-view class="cc-tabs" scroll-x>
        <view
          v-for="tab in tabs"
          :key="tab.value"
          :class="['cc-tabs__item', activeStatus === tab.value && 'cc-tabs__item--active']"
          @tap="onChange(tab.value)"
        >
          <text>{{ tab.label }}</text>
          <text v-if="tab.value === 'unused' && unusedCount > 0" class="cc-tabs__badge">{{ unusedCount }}</text>
        </view>
      </scroll-view>
      <view class="cc-header__cta" @tap="goClaimable">去领券 ›</view>
    </view>

    <view class="cc-list">
      <view
        v-for="item in list"
        :key="item.userCouponId"
        :class="['cc-card', 'cc-card--' + (item.couponType || 'full_reduction'), (item.status !== 'unused') && 'cc-card--disabled']"
      >
        <view class="cc-card__left">
          <text class="cc-card__amount">
            <text v-if="item.couponType === 'discount'">{{ item.discountRate || '--' }}<text class="cc-card__unit">折</text></text>
            <text v-else>¥<text class="cc-card__big">{{ item.faceValue || 0 }}</text></text>
          </text>
          <text class="cc-card__threshold">满{{ item.thresholdAmount || 0 }}可用</text>
        </view>
        <view class="cc-card__right">
          <text class="cc-card__name">{{ item.couponName || '--' }}</text>
          <text class="cc-card__valid">有效期 {{ formatDate(item.validStart) }} ~ {{ formatDate(item.validEnd) }}</text>
          <button v-if="item.status === 'unused'" class="cc-card__btn" @tap="goUse(item)">去使用</button>
          <text v-else class="cc-card__status">{{ statusLabel(item.status) }}</text>
        </view>
      </view>
      <view v-if="!list.length && !loading" class="cc-empty">暂无券</view>
      <view v-if="loading" class="cc-empty">加载中...</view>
      <view v-if="!hasMore && list.length" class="cc-empty cc-empty--end">没有更多了</view>
    </view>
  </view>
</template>

<script>
import { getMyCoupons } from '@/api/coupon'

const TABS = [
  { value: 'unused', label: '未使用' },
  { value: 'used', label: '已使用' },
  { value: 'expired', label: '已过期' }
]
const STATUS_LABEL = { unused: '未使用', used: '已使用', expired: '已过期' }

export default {
  data() { return { tabs: TABS, activeStatus: 'unused', list: [], unusedCount: 0, pageNum: 1, pageSize: 10, total: 0, loading: false, hasMore: true } },
  onShow() { this.load(true) },
  onPullDownRefresh() { this.load(true).finally(() => uni.stopPullDownRefresh()) },
  onReachBottom() { if (this.hasMore && !this.loading) this.load(false) },
  methods: {
    async load(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await getMyCoupons({ status: this.activeStatus, pageNum: this.pageNum, pageSize: this.pageSize })
        const rows = (res && res.rows) || []
        this.total = (res && res.total) || 0
        if (this.activeStatus === 'unused' && reset) this.unusedCount = this.total
        this.list = reset ? rows : this.list.concat(rows)
        this.hasMore = this.list.length < this.total
        if (this.hasMore) this.pageNum += 1
      } finally { this.loading = false }
    },
    onChange(v) { if (this.activeStatus === v) return; this.activeStatus = v; this.load(true) },
    goClaimable() { uni.navigateTo({ url: '/pages-sub/coupon/claimable' }) },
    goUse(item) {
      // 本期直接跳赛事列表, 后续 enroll 页接入后可跳报名
      uni.switchTab({ url: '/pages/contest/list' })
    },
    formatDate(v) { if (!v) return '--'; return String(v).slice(0, 10) },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' }
  }
}
</script>

<style scoped lang="scss">
.cc-page { min-height: 100vh; background: var(--jst-color-page-bg); }
.cc-header { display: flex; align-items: center; background: var(--jst-color-card-bg); border-bottom: 2rpx solid var(--jst-color-border); }
.cc-tabs { flex: 1; white-space: nowrap; }
.cc-tabs__item { display: inline-flex; align-items: center; padding: 0 32rpx; height: 88rpx; font-size: 26rpx; color: var(--jst-color-text-tertiary); position: relative; }
.cc-tabs__item--active { color: #F4511E; font-weight: 700; }
.cc-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 20rpx; right: 20rpx; height: 4rpx; background: #F4511E; border-radius: 2rpx; }
.cc-tabs__badge { margin-left: 8rpx; padding: 0 12rpx; height: 32rpx; line-height: 32rpx; border-radius: 16rpx; background: #F4511E; color: #fff; font-size: 20rpx; }
.cc-header__cta { padding: 0 32rpx; font-size: 24rpx; color: #F4511E; font-weight: 700; }

.cc-list { padding: 16rpx 0 32rpx; }
.cc-card { display: flex; margin: 20rpx 32rpx 0; border-radius: var(--jst-radius-md); overflow: hidden; box-shadow: var(--jst-shadow-card); background: var(--jst-color-card-bg); }
.cc-card__left { width: 220rpx; padding: 32rpx 16rpx; text-align: center; color: #fff; background: #F4511E; display: flex; flex-direction: column; align-items: center; justify-content: center; }
.cc-card--discount .cc-card__left { background: #7C3AED; }
.cc-card--direct_reduction .cc-card__left { background: #F5A623; }
.cc-card--disabled .cc-card__left { background: var(--jst-color-text-tertiary); }
.cc-card__amount { font-size: 32rpx; font-weight: 800; }
.cc-card__big { font-size: 56rpx; }
.cc-card__unit { font-size: 28rpx; margin-left: 4rpx; }
.cc-card__threshold { display: block; margin-top: 10rpx; font-size: 20rpx; opacity: 0.9; }
.cc-card__right { flex: 1; padding: 24rpx; display: flex; flex-direction: column; position: relative; }
.cc-card__name { font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); }
.cc-card__range { margin-top: 6rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.cc-card__valid { margin-top: 6rpx; font-size: 20rpx; color: var(--jst-color-text-tertiary); }
.cc-card__btn { margin-top: auto; align-self: flex-end; height: 56rpx; line-height: 56rpx; padding: 0 24rpx; border-radius: var(--jst-radius-full); background: #F4511E; color: #fff; font-size: 22rpx; font-weight: 700; border: none; }
.cc-card__status { margin-top: auto; align-self: flex-end; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.cc-empty { padding: 80rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.cc-empty--end { padding: 40rpx; }
</style>
