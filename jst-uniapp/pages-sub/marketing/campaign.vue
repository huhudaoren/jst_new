<!-- 中文注释: 营销活动专题
     对应原型: 小程序原型图/campaign-topic.html
     调用接口: GET /jst/wx/campaign/{id}
               POST /jst/wx/coupon/claim -->
<template>
  <view class="cg-page" :style="{ paddingTop: navPaddingTop }">
    <image class="cg-banner" :src="campaign.bannerUrl || ''" mode="aspectFill" />
    <view class="cg-hero">
      <text class="cg-hero__title">{{ campaign.title || '--' }}</text>
      <text class="cg-hero__desc">{{ campaign.description || '' }}</text>
      <view class="cg-hero__countdown" v-if="countdownText">{{ countdownText }}</view>
    </view>

    <view class="cg-section">
      <text class="cg-section__title">可领优惠券</text>
      <view v-for="item in (campaign.linkedCoupons || [])" :key="item.couponTemplateId" class="cg-card">
        <view class="cg-card__left">
          <text class="cg-card__big">
            <text v-if="item.couponType === 'discount'">{{ item.discountRate }}折</text>
            <text v-else>¥{{ item.faceValue || 0 }}</text>
          </text>
          <text class="cg-card__th">满{{ item.thresholdAmount || 0 }}可用</text>
        </view>
        <view class="cg-card__body">
          <text class="cg-card__name">{{ item.couponName || '--' }}</text>
          <text class="cg-card__range">活动专享 · 有效期 {{ item.validDays || '--' }} 天</text>
        </view>
        <button class="cg-card__btn" @tap="onClaim(item)" :disabled="item._claiming || item._claimed">
          {{ item._claimed ? '已领' : (item._claiming ? '...' : '领取') }}
        </button>
      </view>
      <view v-if="!(campaign.linkedCoupons || []).length" class="cg-empty">暂无券</view>
    </view>

    <view class="cg-section">
      <text class="cg-section__title">相关赛事</text>
      <text class="cg-empty">赛事关联数据待后端补齐，暂不展示</text>
    </view>
  </view>
</template>

<script>
import { getCampaignDetail } from '@/api/marketing'
import { claimCoupon } from '@/api/coupon'

export default {
  data() { return { campaignId: null, campaign: {}, countdownText: '' } },
  onLoad(query) {
    this.campaignId = query && query.id
    if (this.campaignId) this.load()
  },
  methods: {
    async load() {
      try {
        this.campaign = (await getCampaignDetail(this.campaignId)) || {}
        this.updateCountdown()
      } catch (e) {}
    },
    updateCountdown() {
      const end = this.campaign.endTime
      if (!end) { this.countdownText = ''; return }
      const diff = new Date(String(end).replace(' ', 'T')).getTime() - Date.now()
      if (diff <= 0) { this.countdownText = '活动已结束'; return }
      const d = Math.floor(diff / 86400000)
      const h = Math.floor((diff % 86400000) / 3600000)
      this.countdownText = `距结束 ${d} 天 ${h} 小时`
    },
    async onClaim(item) {
      item._claiming = true
      this.campaign = { ...this.campaign }
      try {
        await claimCoupon(item.couponTemplateId)
        item._claimed = true
        uni.showToast({ title: '领取成功', icon: 'success' })
      } catch (e) {} finally {
        item._claiming = false
        this.campaign = { ...this.campaign }
      }
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.cg-page { min-height: 100vh; background: #F7F8FA; padding-bottom: 48rpx; }
.cg-banner { width: 100%; height: 420rpx; background: $jst-brand-light; }
.cg-hero { margin: -48rpx 32rpx 0; padding: 32rpx; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04); position: relative; }
.cg-hero__title { display: block; font-size: 36rpx; font-weight: 600; color: $jst-text-primary; }
.cg-hero__desc { display: block; margin-top: 10rpx; font-size: 24rpx; color: $jst-text-regular; line-height: 1.7; }
.cg-hero__countdown { margin-top: 16rpx; display: inline-block; padding: 8rpx 20rpx; border-radius: $jst-radius-round; background: $jst-danger-light; color: $jst-danger; font-size: 22rpx; font-weight: 600; }

.cg-section { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04); }
.cg-section__title { display: block; font-size: 28rpx; font-weight: 600; color: $jst-text-primary; margin-bottom: 16rpx; }
.cg-card { display: flex; align-items: center; padding: 20rpx 0; border-bottom: 2rpx solid $jst-border; }
.cg-card:last-child { border-bottom: none; }
.cg-card__left { min-width: 180rpx; text-align: center; color: #F4511E; }
.cg-card__big { font-size: 36rpx; font-weight: 600; }
.cg-card__th { display: block; margin-top: 4rpx; font-size: 20rpx; color: $jst-text-secondary; }
.cg-card__body { flex: 1; min-width: 0; }
.cg-card__name { display: block; font-size: 26rpx; font-weight: 600; color: $jst-text-primary; }
.cg-card__range { display: block; margin-top: 4rpx; font-size: 22rpx; color: $jst-text-secondary; }
.cg-card__btn { height: 64rpx; line-height: 64rpx; padding: 0 24rpx; border-radius: $jst-radius-round; background: #F4511E; color: #fff; font-size: 22rpx; font-weight: 600; border: none; }
.cg-card__btn[disabled] { opacity: 0.5; }
.cg-empty { display: block; padding: 32rpx; text-align: center; font-size: 22rpx; color: $jst-text-secondary; }
</style>
