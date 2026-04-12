<!-- 中文注释: 领券中心
     调用接口: GET /jst/wx/coupon/template/claimable
              POST /jst/wx/coupon/claim?templateId= -->
<template>
  <view class="cl-page">
    <view class="cl-hero">
      <text class="cl-hero__title">领券中心</text>
      <text class="cl-hero__sub">精选优惠，限时领取</text>
    </view>

    <view class="cl-list">
      <u-skeleton v-if="loading && !list.length" :loading="true" :rows="6" title :avatar="false" class="cl-skeleton" />
      <view v-for="item in list" :key="item.couponTemplateId" class="cl-card">
        <view class="cl-card__left">
          <text class="cl-card__big">
            <text v-if="item.couponType === 'discount'">{{ item.discountRate }}<text class="cl-card__unit">折</text></text>
            <text v-else>¥{{ item.faceValue || 0 }}</text>
          </text>
          <text class="cl-card__th">满{{ item.thresholdAmount || 0 }}可用</text>
        </view>
        <view class="cl-card__right">
          <text class="cl-card__name">{{ item.couponName || '--' }}</text>
          <text class="cl-card__range">{{ scopeLabel(item) }}</text>
          <text class="cl-card__valid">有效期 {{ item.validDays || '--' }} 天</text>
          <u-button class="cl-card__btn" :loading="item._claiming" :disabled="item._claiming || item._claimed" @click="onClaim(item)">
            {{ item._claimed ? '已领取' : (item._claiming ? '领取中...' : '立即领取') }}
          </u-button>
        </view>
      </view>
      <u-empty v-if="!list.length && !loading" mode="data" />
      <u-loadmore v-if="loading && list.length" status="loading" />
    </view>
  </view>
</template>

<script>
import { getClaimableCoupons, claimCoupon } from '@/api/coupon'

export default {
  data() { return { list: [], loading: false } },
  onLoad() { this.load() },
  methods: {
    async load() {
      this.loading = true
      try {
        const res = await getClaimableCoupons()
        this.list = (res && (res.rows || res)) || []
      } finally { this.loading = false }
    },
    async onClaim(item) {
      item._claiming = true
      this.list = [...this.list]
      try {
        await claimCoupon(item.couponTemplateId)
        item._claimed = true
        uni.showToast({ title: '领取成功', icon: 'success' })
      } catch (e) {} finally {
        item._claiming = false
        this.list = [...this.list]
      }
    },
    scopeLabel(item) {
      if (item.applicableContestIds && item.applicableContestIds.length) return `指定 ${item.applicableContestIds.length} 个赛事`
      return '全场通用'
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.cl-page {
  min-height: 100vh;
  padding-bottom: $jst-space-xxl;
  background: $jst-bg-page;
}

.cl-hero {
  padding: 72rpx $jst-space-xl 56rpx;
  color: $jst-text-inverse;
  background: linear-gradient(135deg, $jst-warning, $jst-danger);
}

.cl-hero__title {
  display: block;
  font-size: 40rpx;
  font-weight: $jst-weight-bold;
}

.cl-hero__sub {
  display: block;
  margin-top: 10rpx;
  font-size: $jst-font-sm;
  color: rgba($jst-text-inverse, 0.76);
}

.cl-list {
  padding: $jst-space-md 0 $jst-space-xl;
}

.cl-skeleton {
  margin: $jst-space-sm $jst-space-xl 0;
}

.cl-card {
  display: flex;
  margin: $jst-space-lg $jst-space-xl 0;
  border-radius: $jst-radius-md;
  overflow: hidden;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.cl-card__left {
  width: 220rpx;
  padding: $jst-space-xl $jst-space-md;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: $jst-text-inverse;
  background: linear-gradient(135deg, $jst-warning, $jst-danger);
}

.cl-card__big {
  font-size: 48rpx;
  font-weight: $jst-weight-bold;
}

.cl-card__unit {
  font-size: $jst-font-base;
}

.cl-card__th {
  display: block;
  margin-top: $jst-space-xs;
  opacity: 0.9;
  font-size: $jst-font-xs;
}

.cl-card__right {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: $jst-space-lg;
}

.cl-card__name {
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.cl-card__range {
  margin-top: 6rpx;
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.cl-card__valid {
  margin-top: 6rpx;
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
}

::v-deep .cl-card__btn.u-button {
  margin-top: auto;
  align-self: flex-end;
  min-height: 64rpx;
  padding: 0 $jst-space-xl;
  border: none;
  border-radius: $jst-radius-round;
  background: $jst-warning;
  color: $jst-text-inverse;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
}

::v-deep .cl-card__btn.u-button--disabled {
  opacity: 0.5;
}
</style>
