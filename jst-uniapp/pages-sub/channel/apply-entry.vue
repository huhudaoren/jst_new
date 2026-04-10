<!-- 中文注释: 渠道认证入口 · 类型选择
     对应原型: 小程序原型图/channel-identity.html
     调用接口: GET /jst/wx/channel/auth/my -->
<template>
  <view class="ae-page">
    <view class="ae-hero">
      <text class="ae-hero__title">申请成为渠道方</text>
      <text class="ae-hero__desc">认证后可享受渠道方专属权益：场地减免 · 专属课程 · 客服特权 · 返点结算</text>
    </view>

    <view class="ae-section">
      <text class="ae-section__title">选择认证类型</text>
      <view class="ae-card" @tap="go('teacher')">
        <text class="ae-card__icon">🎓</text>
        <view class="ae-card__body"><text class="ae-card__name">老师认证</text><text class="ae-card__desc">适用于在职教师、培训讲师</text></view>
        <text class="ae-card__arrow">›</text>
      </view>
      <view class="ae-card" @tap="go('organization')">
        <text class="ae-card__icon">🏢</text>
        <view class="ae-card__body"><text class="ae-card__name">机构认证</text><text class="ae-card__desc">适用于培训机构、学校、社团</text></view>
        <text class="ae-card__arrow">›</text>
      </view>
      <view class="ae-card" @tap="go('individual')">
        <text class="ae-card__icon">👤</text>
        <view class="ae-card__body"><text class="ae-card__name">个人认证</text><text class="ae-card__desc">适用于个体经营、自由职业</text></view>
        <text class="ae-card__arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getMyChannelApply } from '@/api/channel'

export default {
  onShow() { this.checkExisting() },
  methods: {
    async checkExisting() {
      try {
        const data = await getMyChannelApply()
        if (data && data.applyId && ['pending', 'approved', 'locked_for_manual'].includes(data.applyStatus)) {
          uni.redirectTo({ url: '/pages-sub/channel/apply-status' })
        }
      } catch (e) {}
    },
    go(type) {
      uni.navigateTo({ url: '/pages-sub/channel/apply-form?channelType=' + type })
    }
  }
}
</script>

<style scoped lang="scss">
.ae-page { min-height: 100vh; background: #F7F8FA; }
.ae-hero { padding: 96rpx 32rpx 56rpx; background: linear-gradient(135deg, var(--jst-color-brand), var(--jst-color-brand-light)); color: #fff; border-bottom-left-radius: 40rpx; border-bottom-right-radius: 40rpx; }
.ae-hero__title { display: block; font-size: 40rpx; font-weight: 800; }
.ae-hero__desc { display: block; margin-top: 16rpx; font-size: 24rpx; line-height: 1.7; color: var(--jst-color-white-76); }
.ae-section { margin: 32rpx 32rpx 0; }
.ae-section__title { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 20rpx; }
.ae-card { display: flex; align-items: center; gap: 24rpx; padding: 32rpx; margin-bottom: 20rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: 0 2rpx 8rpx rgba(20,30,60,0.04); }
.ae-card__icon { font-size: 56rpx; }
.ae-card__body { flex: 1; min-width: 0; }
.ae-card__name { display: block; font-size: 30rpx; font-weight: 700; color: var(--jst-color-text); }
.ae-card__desc { display: block; margin-top: 6rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.ae-card__arrow { font-size: 32rpx; color: var(--jst-color-text-tertiary); }
</style>
