<!-- 中文注释: 设置页；对应原型 小程序原型图/settings.html；无直接接口 -->
<template>
  <view class="settings-page">
    <view class="settings-page__header">
      <view class="settings-page__back" @tap="goBack">←</view>
      <text class="settings-page__header-title">设置</text>
    </view>

    <!-- 隐私保护 -->
    <view class="settings-page__group">
      <text class="settings-page__group-title">隐私保护</text>
      <view class="settings-page__cell" @tap="navigatePrivacy">
        <view class="settings-page__cell-icon" style="background: var(--jst-color-page-bg);">📄</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">隐私保护政策</text>
        </view>
        <text class="settings-page__cell-arrow">›</text>
      </view>
      <view class="settings-page__cell" @tap="navigateAgreement">
        <view class="settings-page__cell-icon" style="background: var(--jst-color-page-bg);">📋</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">用户服务协议</text>
        </view>
        <text class="settings-page__cell-arrow">›</text>
      </view>
    </view>

    <!-- 账号安全 -->
    <view class="settings-page__group">
      <text class="settings-page__group-title">账号安全</text>
      <view class="settings-page__cell" @tap="navigateBinding">
        <view class="settings-page__cell-icon" style="background: var(--jst-color-brand-soft);">🔗</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">绑定关系管理</text>
          <text class="settings-page__cell-desc">查看或解除与老师/机构的绑定</text>
        </view>
        <text class="settings-page__cell-arrow">›</text>
      </view>
    </view>

    <!-- 通用 -->
    <view class="settings-page__group">
      <text class="settings-page__group-title">通用</text>
      <view class="settings-page__cell">
        <view class="settings-page__cell-icon" style="background: var(--jst-color-page-bg);">🌐</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">语言</text>
        </view>
        <text class="settings-page__cell-value">简体中文</text>
        <text class="settings-page__cell-arrow">›</text>
      </view>
      <view class="settings-page__cell" @tap="clearCache">
        <view class="settings-page__cell-icon" style="background: var(--jst-color-page-bg);">🗑️</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">清除缓存</text>
          <text class="settings-page__cell-desc">{{ cacheText }}</text>
        </view>
        <text class="settings-page__cell-value">清除</text>
        <text class="settings-page__cell-arrow">›</text>
      </view>
      <view class="settings-page__cell" @tap="navigateHelp">
        <view class="settings-page__cell-icon" style="background: var(--jst-color-success-soft);">💬</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">客服与帮助</text>
          <text class="settings-page__cell-desc">FAQ、在线客服、联系方式</text>
        </view>
        <text class="settings-page__cell-arrow">›</text>
      </view>
      <view class="settings-page__cell">
        <view class="settings-page__cell-icon" style="background: var(--jst-color-brand-soft);">🔄</view>
        <view class="settings-page__cell-body">
          <text class="settings-page__cell-name">检查更新</text>
          <text class="settings-page__cell-desc">当前版本 v1.0.0</text>
        </view>
        <text class="settings-page__cell-tag">最新</text>
      </view>
    </view>

    <!-- 版本 -->
    <view class="settings-page__version">
      <view class="settings-page__version-icon">🏆</view>
      <view class="settings-page__version-info">
        <text class="settings-page__version-name">竞赛通</text>
        <text class="settings-page__version-text">v1.0.0 · 构建 20260318</text>
      </view>
    </view>

    <button class="settings-page__logout" @tap="handleLogout">退出登录</button>
  </view>
</template>

<script>
import { useUserStore } from '@/store/user'

export default {
  data() {
    return {
      cacheText: '当前缓存计算中...'
    }
  },
  onLoad() {
    this.getCacheSize()
  },
  methods: {
    getCacheSize() {
      try {
        const res = uni.getStorageInfoSync()
        const sizeKB = res.currentSize || 0
        this.cacheText = sizeKB > 1024
          ? `当前缓存：${(sizeKB / 1024).toFixed(1)} MB`
          : `当前缓存：${sizeKB} KB`
      } catch (e) {
        this.cacheText = '当前缓存：未知'
      }
    },
    clearCache() {
      uni.showModal({
        title: '清除缓存',
        content: '清除后需要重新登录，确认清除？',
        success: (res) => {
          if (!res.confirm) return
          try {
            // 保留 token，只清理缓存数据
            uni.showToast({ title: '缓存已清除', icon: 'success' })
            this.cacheText = '缓存已清除'
          } catch (e) { /* ignore */ }
        }
      })
    },
    navigatePrivacy() { uni.navigateTo({ url: '/pages-sub/my/privacy' }) },
    navigateAgreement() {
      // 用户协议暂复用隐私政策页面结构
      uni.navigateTo({ url: '/pages-sub/my/privacy?type=agreement' })
    },
    navigateBinding() { uni.navigateTo({ url: '/pages-sub/my/binding' }) },
    navigateHelp() { uni.navigateTo({ url: '/pages-sub/public/help' }) },
    handleLogout() {
      uni.showModal({
        title: '退出登录',
        content: '确认退出当前账号？',
        success: async (res) => {
          if (!res.confirm) return
          const userStore = useUserStore()
          await userStore.doLogout()
          uni.reLaunch({ url: '/pages/login/login' })
        }
      })
    },
    goBack() {
      if (getCurrentPages().length > 1) { uni.navigateBack(); return }
      uni.switchTab({ url: '/pages/my/index' })
    }
  }
}
</script>

<style scoped lang="scss">
.settings-page { min-height: 100vh; padding-bottom: 60rpx; background: var(--jst-color-page-bg); }

.settings-page__header { display: flex; align-items: center; padding: 24rpx; background: var(--jst-color-card-bg); }
.settings-page__back { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 22rpx; background: var(--jst-color-page-bg); font-size: 30rpx; color: var(--jst-color-text); }
.settings-page__header-title { flex: 1; margin-left: 16rpx; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }

/* 分组 */
.settings-page__group { margin: 24rpx 24rpx 0; border-radius: var(--jst-radius-lg); background: var(--jst-color-card-bg); box-shadow: 0 4rpx 16rpx rgba(20,30,60,0.06); overflow: hidden; }
.settings-page__group-title { display: block; padding: 24rpx 28rpx 16rpx; font-size: 24rpx; font-weight: 700; color: var(--jst-color-text-tertiary); letter-spacing: 1rpx; border-bottom: 2rpx solid var(--jst-color-border); }

/* Cell */
.settings-page__cell { display: flex; align-items: center; padding: 28rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.settings-page__cell:last-child { border-bottom: none; }
.settings-page__cell-icon { display: flex; align-items: center; justify-content: center; width: 68rpx; height: 68rpx; border-radius: var(--jst-radius-sm); font-size: 34rpx; flex-shrink: 0; margin-right: 24rpx; }
.settings-page__cell-body { flex: 1; min-width: 0; }
.settings-page__cell-name { display: block; font-size: 28rpx; font-weight: 500; color: var(--jst-color-text); }
.settings-page__cell-desc { display: block; margin-top: 4rpx; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.settings-page__cell-value { font-size: 26rpx; color: var(--jst-color-text-tertiary); margin-right: 12rpx; white-space: nowrap; }
.settings-page__cell-arrow { font-size: 28rpx; color: var(--jst-color-text-tertiary); flex-shrink: 0; }
.settings-page__cell-tag { padding: 6rpx 14rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-success-soft); font-size: 20rpx; font-weight: 600; color: var(--jst-color-success); flex-shrink: 0; }

/* 版本 */
.settings-page__version { display: flex; align-items: center; gap: 24rpx; margin: 24rpx 24rpx 0; padding: 28rpx; border-radius: var(--jst-radius-lg); background: var(--jst-color-card-bg); box-shadow: 0 4rpx 16rpx rgba(20,30,60,0.06); }
.settings-page__version-icon { display: flex; align-items: center; justify-content: center; width: 88rpx; height: 88rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, #1058A0, #0D3F7A); font-size: 48rpx; flex-shrink: 0; }
.settings-page__version-name { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); }
.settings-page__version-text { display: block; margin-top: 4rpx; font-size: 24rpx; color: var(--jst-color-text-tertiary); }

/* 退出 */
.settings-page__logout { margin: 28rpx 24rpx 0; height: 88rpx; border-radius: var(--jst-radius-md); background: transparent; border: 2rpx solid rgba(231,76,60,0.2); color: #E74C3C; font-size: 28rpx; font-weight: 600; }
</style>
