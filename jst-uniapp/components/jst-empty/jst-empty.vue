<!-- 中文注释: 通用空状态组件；无原型；无直接接口 -->
<template>
  <view class="jst-empty">
    <text class="jst-empty__icon">{{ icon }}</text>
    <text class="jst-empty__text">{{ text }}</text>
  </view>
</template>

<script>
export default {
  name: 'JstEmpty',
  props: {
    text: {
      type: String,
      default: '暂无数据'
    },
    icon: {
      type: String,
      default: '📭'
    }
  }
}
</script>

<style scoped lang="scss">
.jst-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 96rpx 32rpx;
  text-align: center;
}

.jst-empty__icon {
  font-size: 72rpx;
  line-height: 1;
}

.jst-empty__text {
  margin-top: 24rpx;
  font-size: 28rpx;
  line-height: 1.6;
  color: var(--jst-color-text-tertiary);
}
</style>
