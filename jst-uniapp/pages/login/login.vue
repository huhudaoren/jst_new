<template>
  <view class="login-page">
    <!-- 背景光球 -->
    <view class="login-page__orb login-page__orb--top"></view>
    <view class="login-page__orb login-page__orb--bottom"></view>
    <view class="login-page__orb login-page__orb--center"></view>

    <view class="login-page__back" @tap="handleBack">
      <text class="login-page__back-arrow">&#8592;</text>
    </view>

    <!-- Logo 区 -->
    <view class="login-page__logo-box jst-anim-logo-pop">
      <view class="login-page__logo">&#127942;</view>
      <text class="login-page__title">竞赛通</text>
      <text class="login-page__subtitle">一站式竞赛服务平台</text>
    </view>

    <view class="login-page__badge jst-anim-rise-delayed">
      <text class="login-page__badge-dot"></text>
      <text class="login-page__badge-text">学生 / 家长端登录</text>
    </view>

    <!-- 开发提示(降权为小字) -->
    <view v-if="isDev" class="login-page__dev-hint jst-anim-rise-delayed">
      <text class="login-page__dev-hint-text">DEV: 使用后端接口登录，成功后进入个人中心</text>
    </view>

    <!-- 毛玻璃登录卡 -->
    <view class="login-page__card jst-anim-rise-delayed">
      <text class="login-page__card-title">欢迎登录</text>
      <text class="login-page__card-desc">微信一键登录，开启竞赛之旅</text>

      <u-button class="login-page__button login-page__button--wx" @click="handleWxLogin">
        <text class="login-page__button-icon">&#128172;</text>
        微信一键登录
      </u-button>

      <u-button v-if="isDev" class="login-page__button login-page__button--dev" :loading="submitting" @click="handleMockLogin(MOCK_1003)">
        Mock 登录（渠道测试账号）
      </u-button>
<u-button v-if="isDev" class="login-page__button login-page__button--dev" :loading="submitting" @click="handleMockLogin(MOCK_9212)">
        Mock 登录（学生测试账号）
      </u-button>
      <text v-if="message" class="login-page__message">{{ message }}</text>
    </view>

    <view class="login-page__footer jst-anim-rise-delayed">
      <text class="login-page__footer-line">登录即表示同意《用户服务协议》与《隐私保护政策》</text>
    </view>
  </view>
</template>

<script>
import { useUserStore } from '@/store/user'

export default {
  data() {
    return {
      submitting: false,
      message: ''
    }
  },
  computed: {
    isDev() {
      return process.env.NODE_ENV === 'development'
    }
  },
  onShow() {
    this.redirectIfLoggedIn()
  },
  methods: {
    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
      }
    },

    async redirectIfLoggedIn() {
      const userStore = useUserStore()
      if (!userStore.token || this.submitting) {
        return
      }

      try {
        await userStore.fetchProfile()
        uni.switchTab({ url: '/pages/my/index' })
      } catch (error) {}
    },

    async handleMockLogin(mock) {
      if (this.submitting) {
        return
      }

      this.submitting = true
      this.message = '登录中...'

      try {
        const userStore = useUserStore()
        const res = await userStore.login(mock)
        await this.finishLogin(res)
      } catch (error) {
        this.message = '登录失败，请稍后重试'
      } finally {
        this.submitting = false
      }
    },

    handleWxLogin() {
      uni.showToast({
        title: '待生产开通',
        icon: 'none'
      })
    },

    async finishLogin(res) {
      const userStore = useUserStore()
      this.message = '登录成功，正在进入个人中心...'

      if (res.isNewUser) {
        uni.redirectTo({ url: '/pages-sub/my/profile-edit?fromLogin=1' })
        return
      }

      await userStore.fetchProfile().catch(() => null)
      uni.switchTab({ url: '/pages/my/index' })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.login-page {
  position: relative;
  min-height: 100vh;
  padding: 88rpx 40rpx 56rpx;
  overflow: hidden;
  background: $jst-hero-gradient;
}

.login-page__orb {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.login-page__orb--top {
  top: -160rpx;
  right: -100rpx;
  width: 420rpx;
  height: 420rpx;
  background: rgba(255, 255, 255, 0.06);
  filter: blur(40px);
}

.login-page__orb--bottom {
  left: -120rpx;
  bottom: -100rpx;
  width: 360rpx;
  height: 360rpx;
  background: rgba(43, 108, 255, 0.2);
  filter: blur(50px);
}

.login-page__orb--center {
  top: 30%;
  left: 50%;
  margin-left: -100rpx;
  width: 200rpx;
  height: 200rpx;
  background: rgba(255, 255, 255, 0.04);
  filter: blur(30px);
}

.login-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.login-page__back-arrow {
  font-size: 32rpx;
  color: $jst-text-inverse;
}

.login-page__logo-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 80rpx;
}

.login-page__logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 144rpx;
  height: 144rpx;
  border-radius: 36rpx;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
  font-size: 72rpx;
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.15);
}

.login-page__title {
  margin-top: $jst-space-xl;
  font-size: 56rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
  letter-spacing: 4rpx;
}

.login-page__subtitle {
  margin-top: $jst-space-sm;
  font-size: $jst-font-sm;
  color: rgba(255, 255, 255, 0.65);
}

.login-page__badge {
  display: flex;
  align-items: center;
  width: fit-content;
  margin: $jst-space-xxl auto 0;
  padding: $jst-space-sm $jst-space-lg;
  border: 1rpx solid rgba(255, 255, 255, 0.2);
  border-radius: $jst-radius-round;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.login-page__badge-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: $jst-success;
}

.login-page__badge-text {
  margin-left: $jst-space-sm;
  font-size: $jst-font-xs;
  font-weight: $jst-weight-medium;
  color: rgba(255, 255, 255, 0.85);
}

.login-page__dev-hint {
  margin-top: $jst-space-lg;
  text-align: center;
}

.login-page__dev-hint-text {
  font-size: $jst-font-xs;
  color: rgba(255, 255, 255, 0.4);
}

.login-page__card {
  margin-top: $jst-space-xl;
  padding: $jst-space-xl;
  border-radius: $jst-radius-card;
  background: $jst-glass-bg;
  backdrop-filter: blur($jst-glass-blur);
  -webkit-backdrop-filter: blur($jst-glass-blur);
  border: 1rpx solid $jst-glass-border;
  box-shadow: $jst-shadow-float;
}

.login-page__card-title {
  display: block;
  font-size: $jst-font-xl;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.login-page__card-desc {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.login-page__button {
  margin-top: $jst-space-lg;
  height: 96rpx;
  border-radius: $jst-radius-button;
  font-size: $jst-font-base;
  font-weight: $jst-weight-bold;
}

.login-page__button--wx {
  background: #07C160;
  color: $jst-text-inverse;
}

.login-page__button-icon {
  margin-right: $jst-space-sm;
  font-size: $jst-font-lg;
}

.login-page__button--dev {
  background: $jst-bg-grey;
  color: $jst-text-secondary;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-regular;
  height: 80rpx;
}

::v-deep .login-page__button.u-button {
  min-height: 80rpx;
  border: none;
}

.login-page__message {
  display: block;
  margin-top: $jst-space-md;
  text-align: center;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.login-page__footer {
  margin-top: $jst-space-xl;
  text-align: center;
}

.login-page__footer-line {
  display: block;
  font-size: $jst-font-xs;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.4);
}
</style>
