<!-- 中文注释: 隐私政策/用户协议页；对应原型 小程序原型图/privacy.html；无直接接口 -->
<template>
  <view class="privacy-page">
    <view class="privacy-page__header">
      <view class="privacy-page__back" @tap="goBack">←</view>
      <text class="privacy-page__header-title">{{ pageTitle }}</text>
    </view>

    <view class="privacy-page__content">
      <view v-if="isAgreement" class="privacy-page__body">
        <text class="privacy-page__section-title">用户服务协议</text>
        <text class="privacy-page__text">用户服务协议内容待运营提供。</text>
        <text class="privacy-page__text">本协议将详细说明竞赛通平台的服务条款、用户权利与义务、账号使用规范、内容规范、知识产权保护、免责条款等内容。</text>
        <text class="privacy-page__text">请在正式上线前联系运营团队完善此页面内容。</text>
      </view>
      <view v-else class="privacy-page__body">
        <text class="privacy-page__section-title">隐私保护政策</text>
        <text class="privacy-page__text">隐私政策内容待运营提供。</text>
        <text class="privacy-page__text">竞赛通严格遵守《网络安全法》《个人信息保护法》等相关法律法规，保护您的个人信息安全。如有隐私相关问题，请联系 privacy@jst.edu.cn</text>

        <text class="privacy-page__section-title">个人信息保护</text>
        <text class="privacy-page__text">我们收集的信息包括：手机号、微信 OpenID、真实姓名、学校、年级等报名必要信息。所有敏感字段均做脱敏处理。</text>

        <text class="privacy-page__section-title">数据授权</text>
        <text class="privacy-page__text">个性化推荐授权、第三方数据共享、导出我的数据等功能将在后续版本中完善。</text>

        <text class="privacy-page__section-title">通知与消息</text>
        <text class="privacy-page__text">系统推送通知、营销活动推送等消息偏好可在设置页中管理。</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      isAgreement: false
    }
  },
  computed: {
    pageTitle() {
      return this.isAgreement ? '用户服务协议' : '隐私保护政策'
    }
  },
  onLoad(query) {
    if (query && query.type === 'agreement') {
      this.isAgreement = true
    }
  },
  methods: {
    goBack() {
      if (getCurrentPages().length > 1) { uni.navigateBack(); return }
      uni.switchTab({ url: '/pages/my/index' })
    }
  }
}
</script>

<style scoped lang="scss">
.privacy-page { min-height: 100vh; background: var(--jst-color-page-bg); }

.privacy-page__header { display: flex; align-items: center; padding: 24rpx; background: var(--jst-color-card-bg); }
.privacy-page__back { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 22rpx; background: var(--jst-color-page-bg); font-size: 30rpx; color: var(--jst-color-text); }
.privacy-page__header-title { flex: 1; text-align: center; margin-right: 72rpx; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }

.privacy-page__content { margin: 24rpx; padding: 32rpx; border-radius: var(--jst-radius-lg); background: var(--jst-color-card-bg); box-shadow: 0 4rpx 16rpx rgba(20,30,60,0.06); }

.privacy-page__section-title { display: block; margin-top: 32rpx; margin-bottom: 16rpx; font-size: 32rpx; font-weight: 700; color: var(--jst-color-brand); }
.privacy-page__section-title:first-child { margin-top: 0; }

.privacy-page__text { display: block; margin-bottom: 20rpx; font-size: 28rpx; line-height: 1.8; color: var(--jst-color-text-secondary); }
</style>
