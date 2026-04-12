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
      <u-button class="cc-header__cta" size="mini" plain @click="goClaimable">去领券 ›</u-button>
    </view>

    <view class="cc-list">
      <u-skeleton v-if="loading && !list.length" :loading="true" :rows="6" title :avatar="false" class="cc-skeleton" />
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
          <u-button v-if="item.status === 'unused'" class="cc-card__btn" @click="goUse(item)">去使用</u-button>
          <text v-else class="cc-card__status">{{ statusLabel(item.status) }}</text>
        </view>
      </view>
      <u-empty v-if="!list.length && !loading" mode="data" />
      <u-loadmore v-else :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')" />
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
@import '@/styles/design-tokens.scss';

.cc-page {
  min-height: 100vh;
  background: $jst-bg-page;
}

.cc-header {
  display: flex;
  align-items: center;
  border-bottom: 2rpx solid $jst-border;
  background: $jst-bg-card;
}

.cc-tabs {
  flex: 1;
  white-space: nowrap;
}

.cc-tabs__item {
  position: relative;
  display: inline-flex;
  align-items: center;
  height: 88rpx;
  padding: 0 $jst-space-xl;
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.cc-tabs__item--active {
  color: $jst-warning;
  font-weight: $jst-weight-semibold;
}

.cc-tabs__item--active::after {
  content: '';
  position: absolute;
  left: 20rpx;
  right: 20rpx;
  bottom: 0;
  height: 4rpx;
  border-radius: $jst-radius-xs;
  background: $jst-warning;
}

.cc-tabs__badge {
  margin-left: $jst-space-xs;
  padding: 0 $jst-space-sm;
  height: 32rpx;
  line-height: 32rpx;
  border-radius: $jst-radius-round;
  background: $jst-warning;
  color: $jst-text-inverse;
  font-size: $jst-font-xs;
}

.cc-list {
  padding: $jst-space-md 0 $jst-space-xl;
}

.cc-skeleton {
  margin: $jst-space-sm $jst-space-xl 0;
}

.cc-card {
  display: flex;
  margin: $jst-space-lg $jst-space-xl 0;
  border-radius: $jst-radius-md;
  overflow: hidden;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.cc-card__left {
  width: 220rpx;
  padding: $jst-space-xl $jst-space-md;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: $jst-text-inverse;
  background: $jst-warning;
}

.cc-card--discount .cc-card__left {
  background: $jst-indigo;
}

.cc-card--direct_reduction .cc-card__left {
  background: $jst-gold;
}

.cc-card--disabled .cc-card__left {
  background: $jst-text-placeholder;
}

.cc-card__amount {
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
}

.cc-card__big {
  font-size: 56rpx;
}

.cc-card__unit {
  margin-left: 4rpx;
  font-size: $jst-font-base;
}

.cc-card__threshold {
  display: block;
  margin-top: 10rpx;
  opacity: 0.9;
  font-size: $jst-font-xs;
}

.cc-card__right {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: $jst-space-lg;
}

.cc-card__name {
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.cc-card__range {
  margin-top: 6rpx;
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.cc-card__valid {
  margin-top: 6rpx;
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
}

.cc-card__status {
  margin-top: auto;
  align-self: flex-end;
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
}

::v-deep .cc-header__cta.u-button {
  margin-right: $jst-space-lg;
  min-width: 120rpx;
  height: 56rpx;
  border-color: $jst-warning;
  border-radius: $jst-radius-round;
  color: $jst-warning;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
}

::v-deep .cc-card__btn.u-button {
  margin-top: auto;
  align-self: flex-end;
  min-height: 56rpx;
  padding: 0 $jst-space-lg;
  border: none;
  border-radius: $jst-radius-round;
  background: $jst-warning;
  color: $jst-text-inverse;
  font-size: $jst-font-xs;
  font-weight: $jst-weight-semibold;
}
</style>
