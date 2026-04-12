<!-- 中文注释: 通用加载组件；无原型；无直接接口 -->
<template>
  <view v-if="loading" class="jst-loading">
    <view class="jst-loading__mask"></view>
    <view class="jst-loading__panel">
      <u-loading-icon mode="circle" :color="brandColor" size="44" />
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
  },
  data() {
    return {
      brandColor: '#2B6CFF'
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

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
  background: rgba(255, 255, 255, 0.72);
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
  padding: $jst-space-xl $jst-space-lg;
  border-radius: $jst-radius-md;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-lg;
}

.jst-loading__text {
  margin-top: $jst-space-lg;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}
</style>
