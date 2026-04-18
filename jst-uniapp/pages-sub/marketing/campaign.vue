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
      <view
        v-if="countdownText"
        class="cg-hero__countdown"
        :class="{ 'cg-hero__countdown--urgent': countdownUrgent }"
      >{{ countdownText }}</view>
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
import { formatCountdown } from '@/utils/countdown'

export default {
  data() {
    return {
      campaignId: null,
      campaign: {},
      countdownText: '',
      countdownUrgent: false,
      countdownTimer: null
    }
  },
  onLoad(query) {
    this.campaignId = query && query.id
    if (this.campaignId) this.load()
  },
  beforeDestroy() {
    this.stopCountdown()
  },
  onUnload() {
    this.stopCountdown()
  },
  methods: {
    async load() {
      try {
        this.campaign = (await getCampaignDetail(this.campaignId)) || {}
        this.startCountdown()
      } catch (e) {}
    },
    // 中文注释: 每 1 秒刷新倒计时；≤1 天只显示 hh:mm:ss，>1 天显示 d天hh:mm:ss；<10 分钟进入 urgent 闪烁
    startCountdown() {
      this.stopCountdown()
      const end = this.campaign && this.campaign.endTime
      if (!end) { this.countdownText = ''; this.countdownUrgent = false; return }
      const tick = () => {
        const c = formatCountdown(end)
        if (c.expired) {
          this.countdownText = '活动已结束'
          this.countdownUrgent = false
          this.stopCountdown()
          return
        }
        if (c.d > 0) {
          this.countdownText = `${c.d}天${c.hh}:${c.mm}:${c.ss}`
        } else {
          this.countdownText = `${c.hh}:${c.mm}:${c.ss}`
        }
        this.countdownUrgent = !!c.urgent
      }
      tick()
      this.countdownTimer = setInterval(tick, 1000)
    },
    stopCountdown() {
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
        this.countdownTimer = null
      }
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
.cg-page { min-height: 100vh; background: $jst-bg-page; padding-bottom: $jst-space-xxl; }
.cg-banner { width: 100%; height: 420rpx; background: $jst-brand-light; }
.cg-hero { margin: -$jst-space-xxl $jst-space-xl 0; padding: $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; position: relative; }
.cg-hero__title { display: block; font-size: $jst-font-xl; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.cg-hero__desc { display: block; margin-top: $jst-space-sm; font-size: $jst-font-sm; color: $jst-text-regular; line-height: 1.7; }
.cg-hero__countdown { margin-top: $jst-space-md; display: inline-block; padding: $jst-space-xs 20rpx; border-radius: $jst-radius-round; background: $jst-danger-light; color: $jst-danger; font-size: 22rpx; font-weight: $jst-weight-semibold; font-variant-numeric: tabular-nums; letter-spacing: 1rpx; }
.cg-hero__countdown--urgent { animation: cgBlink 1s $jst-easing infinite; }
@keyframes cgBlink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.45; }
}

.cg-section { margin: $jst-space-lg $jst-space-xl 0; padding: 28rpx $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; }
.cg-section__title { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; margin-bottom: $jst-space-md; }
.cg-card { display: flex; align-items: center; padding: 20rpx 0; border-bottom: 2rpx solid $jst-border; }
.cg-card:last-child { border-bottom: none; }
.cg-card__left { min-width: 180rpx; text-align: center; color: $jst-danger; }
.cg-card__big { font-size: $jst-font-xl; font-weight: $jst-weight-semibold; }
.cg-card__th { display: block; margin-top: 4rpx; font-size: $jst-font-xs; color: $jst-text-secondary; }
.cg-card__body { flex: 1; min-width: 0; }
.cg-card__name { display: block; font-size: $jst-font-sm; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.cg-card__range { display: block; margin-top: 4rpx; font-size: 22rpx; color: $jst-text-secondary; }
.cg-card__btn { height: 64rpx; line-height: 64rpx; padding: 0 $jst-space-lg; border-radius: $jst-radius-round; background: $jst-danger; color: $jst-text-inverse; font-size: 22rpx; font-weight: $jst-weight-semibold; border: none; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.95); } }
.cg-card__btn[disabled] { opacity: 0.5; }
.cg-empty { display: block; padding: $jst-space-xl; text-align: center; font-size: 22rpx; color: $jst-text-secondary; }
</style>
