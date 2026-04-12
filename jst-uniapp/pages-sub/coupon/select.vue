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
      <u-skeleton v-if="loading && !candidates.length" :loading="true" :rows="6" title :avatar="false" class="cs-skeleton" />
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
      <u-empty v-if="!candidates.length && !loading" mode="data" />
      <u-loadmore v-if="loading && candidates.length" status="loading" />
    </view>

    <view class="cs-footer">
      <u-button class="cs-footer__btn" @click="onConfirm">确定</u-button>
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
@import '@/styles/design-tokens.scss';

.cs-page {
  min-height: 100vh;
  padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
  background: $jst-bg-page;
}

.cs-hero {
  padding: 72rpx $jst-space-xl 48rpx;
  color: $jst-text-inverse;
  background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light);
}

.cs-hero__label {
  display: block;
  font-size: $jst-font-sm;
  color: rgba($jst-text-inverse, 0.76);
}

.cs-hero__amount {
  display: block;
  margin-top: $jst-space-xs;
  font-size: 56rpx;
  font-weight: $jst-weight-bold;
}

.cs-list {
  padding: $jst-space-md 0;
}

.cs-skeleton {
  margin: $jst-space-sm $jst-space-xl 0;
}

.cs-card {
  display: flex;
  align-items: center;
  gap: $jst-space-lg;
  margin: $jst-space-md $jst-space-xl 0;
  padding: $jst-space-lg $jst-space-xl;
  border-radius: $jst-radius-md;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.cs-card--active {
  border: 4rpx solid $jst-indigo;
}

.cs-card--disabled {
  opacity: 0.5;
}

.cs-card__left {
  min-width: 120rpx;
  text-align: center;
  color: $jst-indigo;
}

.cs-card__big {
  font-size: 40rpx;
  font-weight: $jst-weight-bold;
}

.cs-card__body {
  flex: 1;
  min-width: 0;
}

.cs-card__name {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.cs-card__info {
  display: block;
  margin-top: 4rpx;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.cs-card__reason {
  display: block;
  margin-top: 4rpx;
  font-size: $jst-font-xs;
  color: $jst-danger;
}

.cs-card__check {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid $jst-border;
  border-radius: $jst-radius-round;
  color: $jst-indigo;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
}

.cs-card--active .cs-card__check {
  border-color: $jst-indigo;
  background: $jst-indigo;
  color: $jst-text-inverse;
}

.cs-card--none {
  justify-content: space-between;
}

.cs-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: $jst-space-lg $jst-space-xl calc($jst-space-lg + env(safe-area-inset-bottom));
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.cs-footer__btn {
  height: 96rpx;
  border-radius: $jst-radius-md;
  background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light);
  color: $jst-text-inverse;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
}

::v-deep .cs-footer__btn.u-button {
  min-height: 96rpx;
  border: none;
}
</style>
