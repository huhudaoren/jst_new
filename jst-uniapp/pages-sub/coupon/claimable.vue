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
      <view v-for="item in list" :key="item.templateId" class="cl-card">
        <view class="cl-card__left">
          <text class="cl-card__big">
            <text v-if="item.couponType === 'discount'">{{ item.discountRate }}<text class="cl-card__unit">折</text></text>
            <text v-else>¥{{ item.faceValue || 0 }}</text>
          </text>
          <text class="cl-card__th">满{{ item.thresholdAmount || 0 }}可用</text>
        </view>
        <view class="cl-card__right">
          <text class="cl-card__name">{{ item.couponName || '--' }}</text>
          <text class="cl-card__range">{{ item.scopeText || '全场通用' }}</text>
          <text class="cl-card__valid">有效期 {{ item.validDays || '--' }} 天</text>
          <button class="cl-card__btn" @tap="onClaim(item)" :disabled="item._claiming || item._claimed">
            {{ item._claimed ? '已领取' : (item._claiming ? '领取中...' : '立即领取') }}
          </button>
        </view>
      </view>
      <view v-if="!list.length && !loading" class="cl-empty">暂无可领券</view>
      <view v-if="loading" class="cl-empty">加载中...</view>
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
        await claimCoupon(item.templateId)
        item._claimed = true
        uni.showToast({ title: '领取成功', icon: 'success' })
      } catch (e) {} finally {
        item._claiming = false
        this.list = [...this.list]
      }
    }
  }
}
</script>

<style scoped lang="scss">
.cl-page { min-height: 100vh; background: var(--jst-color-page-bg); padding-bottom: 48rpx; }
.cl-hero { padding: 72rpx 32rpx 56rpx; background: linear-gradient(135deg, #F4511E, #FF7043); color: #fff; }
.cl-hero__title { display: block; font-size: 40rpx; font-weight: 800; }
.cl-hero__sub { display: block; margin-top: 10rpx; font-size: 24rpx; color: var(--jst-color-white-76); }

.cl-list { padding: 16rpx 0 32rpx; }
.cl-card { display: flex; margin: 20rpx 32rpx 0; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); overflow: hidden; }
.cl-card__left { width: 220rpx; padding: 32rpx 16rpx; text-align: center; background: linear-gradient(135deg, #F4511E, #FF7043); color: #fff; display: flex; flex-direction: column; align-items: center; justify-content: center; }
.cl-card__big { font-size: 48rpx; font-weight: 800; }
.cl-card__unit { font-size: 26rpx; }
.cl-card__th { display: block; margin-top: 8rpx; font-size: 20rpx; opacity: 0.9; }
.cl-card__right { flex: 1; padding: 24rpx; display: flex; flex-direction: column; }
.cl-card__name { font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); }
.cl-card__range { margin-top: 6rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.cl-card__valid { margin-top: 6rpx; font-size: 20rpx; color: var(--jst-color-text-tertiary); }
.cl-card__btn { margin-top: auto; align-self: flex-end; height: 64rpx; line-height: 64rpx; padding: 0 32rpx; border-radius: var(--jst-radius-full); background: #F4511E; color: #fff; font-size: 24rpx; font-weight: 700; border: none; }
.cl-card__btn[disabled] { opacity: 0.5; }
.cl-empty { padding: 80rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
</style>
