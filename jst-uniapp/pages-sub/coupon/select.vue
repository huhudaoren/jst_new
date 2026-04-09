<!-- 中文注释: 选券页 (下单前挑选)
     对应原型: 小程序原型图/coupon-select.html
     调用接口: POST /jst/wx/coupon/select
     使用: 调用方通过 query 传 orderAmount/contestId/goodsId, 选完后通过 storage 回传 selectedCouponId -->
<template>
  <view class="cs-page">
    <view class="cs-hero">
      <text class="cs-hero__label">订单金额</text>
      <text class="cs-hero__amount">¥{{ Number(orderAmount || 0).toFixed(2) }}</text>
    </view>

    <view class="cs-list">
      <view
        v-for="item in candidates"
        :key="item.couponId"
        :class="['cs-card', selectedId === item.couponId && 'cs-card--active', !item.applicable && 'cs-card--disabled']"
        @tap="item.applicable && onSelect(item)"
      >
        <view class="cs-card__left">
          <text class="cs-card__big">
            <text v-if="item.discountAmount != null">-¥{{ Number(item.discountAmount).toFixed(2) }}</text>
            <text v-else>优惠</text>
          </text>
        </view>
        <view class="cs-card__body">
          <text class="cs-card__name">{{ item.couponName || '--' }}</text>
          <text class="cs-card__info">实付 ¥{{ item.netPayAmount != null ? Number(item.netPayAmount).toFixed(2) : '--' }}</text>
          <text v-if="!item.applicable && item.reason" class="cs-card__reason">{{ item.reason }}</text>
        </view>
        <view class="cs-card__check">
          <text v-if="selectedId === item.couponId">✓</text>
        </view>
      </view>
      <view
        :class="['cs-card cs-card--none', selectedId == null && 'cs-card--active']"
        @tap="onSelect(null)"
      >
        <view class="cs-card__body"><text class="cs-card__name">不使用优惠券</text></view>
        <view class="cs-card__check"><text v-if="selectedId == null">✓</text></view>
      </view>
      <view v-if="!candidates.length && !loading" class="cs-empty">暂无可用券</view>
      <view v-if="loading" class="cs-empty">加载中...</view>
    </view>

    <view class="cs-footer">
      <button class="cs-footer__btn" @tap="onConfirm">确定</button>
    </view>
  </view>
</template>

<script>
import { selectCoupon } from '@/api/coupon'

export default {
  data() {
    return {
      orderAmount: 0,
      contestId: null,
      goodsId: null,
      candidates: [],
      selectedId: null,
      loading: false
    }
  },
  onLoad(query) {
    this.orderAmount = Number((query && query.orderAmount) || 0)
    this.contestId = query && query.contestId ? Number(query.contestId) : null
    this.goodsId = query && query.goodsId ? Number(query.goodsId) : null
    this.load()
  },
  methods: {
    async load() {
      this.loading = true
      try {
        const body = { orderAmount: this.orderAmount }
        if (this.contestId) body.contestId = this.contestId
        if (this.goodsId) body.goodsId = this.goodsId
        // 后端返回 CouponSelectResVO: { bestCouponId, bestDiscount, netPayAmount, alternatives[] }
        const res = await selectCoupon(body)
        this.candidates = (res && res.alternatives) || []
        if (res && res.bestCouponId != null) this.selectedId = res.bestCouponId
      } finally { this.loading = false }
    },
    onSelect(item) { this.selectedId = item ? item.couponId : null },
    onConfirm() {
      uni.setStorageSync('cs_selected_coupon_id', this.selectedId)
      // 同步 netPayAmount 以便调用页展示
      const picked = this.candidates.find((i) => i.couponId === this.selectedId)
      if (picked && picked.netPayAmount != null) {
        uni.setStorageSync('cs_selected_coupon_net', String(picked.netPayAmount))
      } else {
        uni.removeStorageSync('cs_selected_coupon_net')
      }
      uni.navigateBack()
    }
  }
}
</script>

<style scoped lang="scss">
.cs-page { min-height: 100vh; background: var(--jst-color-page-bg); padding-bottom: calc(180rpx + env(safe-area-inset-bottom)); }
.cs-hero { padding: 72rpx 32rpx 48rpx; background: linear-gradient(135deg, #7C3AED, #A855F7); color: #fff; }
.cs-hero__label { display: block; font-size: 24rpx; color: var(--jst-color-white-76); }
.cs-hero__amount { display: block; margin-top: 8rpx; font-size: 56rpx; font-weight: 900; }

.cs-list { padding: 16rpx 0; }
.cs-card { display: flex; align-items: center; gap: 20rpx; margin: 16rpx 32rpx 0; padding: 24rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.cs-card--active { border: 4rpx solid #7C3AED; }
.cs-card--disabled { opacity: 0.5; }
.cs-card__left { min-width: 120rpx; text-align: center; color: #7C3AED; }
.cs-card__big { font-size: 40rpx; font-weight: 900; }
.cs-card__body { flex: 1; min-width: 0; }
.cs-card__name { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); }
.cs-card__info { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-secondary); }
.cs-card__reason { display: block; margin-top: 4rpx; font-size: 20rpx; color: var(--jst-color-danger); }
.cs-card__check { width: 48rpx; height: 48rpx; border-radius: 50%; border: 2rpx solid var(--jst-color-border); display: flex; align-items: center; justify-content: center; color: #7C3AED; font-size: 28rpx; font-weight: 800; }
.cs-card--active .cs-card__check { background: #7C3AED; color: #fff; border-color: #7C3AED; }
.cs-card--none { justify-content: space-between; }
.cs-empty { padding: 60rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }

.cs-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: var(--jst-color-card-bg); box-shadow: 0 -8rpx 24rpx rgba(16,88,160,0.08); }
.cs-footer__btn { height: 96rpx; line-height: 96rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, #7C3AED, #A855F7); color: #fff; font-size: 30rpx; font-weight: 800; border: none; }
</style>
