<template>
  <view class="login-page">
    <view class="login-page__orb login-page__orb--top"></view>
    <view class="login-page__orb login-page__orb--bottom"></view>

    <view class="login-page__back" @tap="handleBack">←</view>

    <view class="login-page__logo-box">
      <view class="login-page__logo">🏆</view>
      <text class="login-page__title">竞赛通</text>
      <text class="login-page__subtitle">一站式竞赛服务平台</text>
    </view>

    <view class="login-page__badge">
      <text class="login-page__badge-dot"></text>
      <text class="login-page__badge-text">学生 / 家长端登录</text>
    </view>

    <view class="login-page__tip">
      <text class="login-page__tip-title">开发联调入口</text>
      <text class="login-page__tip-text">使用真实后端接口登录，成功后进入“我的”页完成资料与档案闭环。</text>
    </view>

    <view class="login-page__card">
      <text class="login-page__card-title">欢迎登录</text>
      <text class="login-page__card-desc">Mock 按钮用于联调测试账号；微信登录按钮保留生产占位。</text>

      <button class="login-page__button login-page__button--primary" :loading="submitting" @tap="handleMockLogin">
        Mock 登录（测试账号）
      </button>

      <button class="login-page__button login-page__button--secondary" @tap="handleWxLogin">
        微信登录（待生产开通）
      </button>

      <text v-if="message" class="login-page__message">{{ message }}</text>
    </view>

    <view class="login-page__footer">
      <text class="login-page__footer-line">登录即表示同意《用户服务协议》与《隐私保护政策》</text>
      <text class="login-page__footer-line">微信一键登录，开启竞赛之旅</text>
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

    async handleMockLogin() {
      if (this.submitting) {
        return
      }

      this.submitting = true
      this.message = '登录中...'

      try {
        const userStore = useUserStore()
        const res = await userStore.login('MOCK_1003')
        await this.finishLogin(res)
      } catch (error) {
        this.message = '登录失败，请检查后端或测试数据'
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
.login-page {
  position: relative;
  min-height: 100vh;
  padding: 88rpx 40rpx 56rpx;
  overflow: hidden;
  background: var(--jst-color-card-bg);
}

.login-page__orb {
  position: absolute;
  border-radius: 50%;
  opacity: 0.9;
  pointer-events: none;
}

.login-page__orb--top {
  top: -120rpx;
  right: -120rpx;
  width: 360rpx;
  height: 360rpx;
  background: var(--jst-color-brand-orb);
}

.login-page__orb--bottom {
  left: -120rpx;
  bottom: -120rpx;
  width: 320rpx;
  height: 320rpx;
  background: var(--jst-color-primary-orb);
}

.login-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;
  border-radius: var(--jst-radius-sm);
  background: var(--jst-color-page-bg);
  color: var(--jst-color-text-secondary);
  font-size: 32rpx;
}

.login-page__logo-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 72rpx;
}

.login-page__logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 136rpx;
  height: 136rpx;
  border-radius: 32rpx;
  background: linear-gradient(135deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%);
  box-shadow: var(--jst-shadow-strong);
  font-size: 64rpx;
}

.login-page__title {
  margin-top: 28rpx;
  font-size: 52rpx;
  font-weight: 700;
  color: var(--jst-color-text);
  line-height: 1.2;
}

.login-page__subtitle {
  margin-top: 12rpx;
  font-size: 26rpx;
  color: var(--jst-color-text-tertiary);
}

.login-page__badge {
  display: flex;
  align-items: center;
  align-self: center;
  width: fit-content;
  margin: 48rpx auto 0;
  padding: 12rpx 20rpx;
  border: 2rpx solid var(--jst-color-success-border);
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-success-soft);
}

.login-page__badge-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: var(--jst-color-success);
}

.login-page__badge-text {
  margin-left: 12rpx;
  font-size: 22rpx;
  font-weight: 600;
  color: var(--jst-color-success);
}

.login-page__tip {
  margin-top: 24rpx;
  padding: 20rpx 24rpx;
  border-radius: var(--jst-radius-md);
  background: var(--jst-color-primary-soft);
}

.login-page__tip-title {
  display: block;
  font-size: 24rpx;
  font-weight: 700;
  color: var(--jst-color-primary);
}

.login-page__tip-text {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.7;
  color: var(--jst-color-text-secondary);
}

.login-page__card {
  margin-top: 28rpx;
  padding: 32rpx 28rpx;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.login-page__card-title {
  display: block;
  font-size: 36rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.login-page__card-desc {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: var(--jst-color-text-secondary);
}

.login-page__button {
  margin-top: 24rpx;
  height: 88rpx;
  border-radius: var(--jst-radius-md);
  font-size: 28rpx;
  font-weight: 700;
}

.login-page__button--primary {
  background: linear-gradient(135deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%);
  color: var(--jst-color-card-bg);
}

.login-page__button--secondary {
  border: 2rpx solid var(--jst-color-border);
  background: var(--jst-color-card-bg);
  color: var(--jst-color-text-secondary);
}

.login-page__message {
  display: block;
  margin-top: 16rpx;
  text-align: center;
  font-size: 22rpx;
  color: var(--jst-color-text-tertiary);
}

.login-page__footer {
  margin-top: 28rpx;
}

.login-page__footer-line {
  display: block;
  text-align: center;
  font-size: 22rpx;
  line-height: 1.8;
  color: var(--jst-color-text-tertiary);
}
</style>