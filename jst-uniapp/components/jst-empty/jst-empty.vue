<!-- 中文注释: 通用空状态组件；无原型；无直接接口 -->
<template>
  <view class="jst-empty">
    <u-empty
      :mode="uEmptyMode"
      :text="text"
      :icon-size="120"
      :text-size="14"
      :margin-top="0"
    />
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
  },
  computed: {
    uEmptyMode() {
      // 根据 icon emoji 映射到 u-empty 内置 mode
      const modeMap = {
        '📭': 'message',
        '📋': 'list',
        '🔍': 'search',
        '🛒': 'order',
        '💳': 'order',
        '📰': 'data',
        '🏆': 'data',
        '📚': 'data',
        '🎓': 'data'
      }
      return modeMap[this.icon] || 'data'
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $jst-space-xxl $jst-space-xl;
  text-align: center;
}
</style>
