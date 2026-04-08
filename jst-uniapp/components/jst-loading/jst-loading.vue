<!-- 中文注释: 通用加载组件；无原型；无直接接口 -->
<template>
  <view v-if="loading" class="jst-loading">
    <view class="jst-loading__mask"></view>
    <view class="jst-loading__panel">
      <view class="jst-loading__spinner"></view>
      <text class="jst-loading__text">{{ text }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'JstLoading',
  props: {
    loading: {
      type: Boolean,
      default: false
    },
    text: {
      type: String,
      default: '加载中...'
    }
  }
}
</script>

<style scoped lang="scss">
.jst-loading {
  position: fixed;
  inset: 0;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.jst-loading__mask {
  position: absolute;
  inset: 0;
  background: var(--jst-color-white-72);
}

.jst-loading__panel {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 220rpx;
  min-height: 180rpx;
  padding: 32rpx 24rpx;
  border-radius: var(--jst-radius-md);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-strong);
}

.jst-loading__spinner {
  width: 60rpx;
  height: 60rpx;
  border: 6rpx solid var(--jst-color-brand-soft);
  border-top-color: var(--jst-color-primary);
  border-radius: 50%;
  animation: jst-loading-spin 0.9s linear infinite;
}

.jst-loading__text {
  margin-top: 24rpx;
  font-size: 26rpx;
  color: var(--jst-color-text-secondary);
}

@keyframes jst-loading-spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}
</style>
