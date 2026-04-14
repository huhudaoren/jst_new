<!-- jst-empty-state: 增强空状态组件（插画 + 文案 + CTA 按钮） -->
<template>
  <view class="jst-empty-state jst-anim-fade-up">
    <view class="jst-empty-state__icon-wrap">
      <text class="jst-empty-state__icon">{{ icon }}</text>
    </view>
    <text class="jst-empty-state__title">{{ title }}</text>
    <text v-if="desc" class="jst-empty-state__desc">{{ desc }}</text>
    <u-button
      v-if="actionText"
      class="jst-empty-state__btn"
      type="primary"
      shape="circle"
      size="small"
      :text="actionText"
      @click="$emit('action')"
    />
  </view>
</template>

<script>
export default {
  name: 'JstEmptyState',
  props: {
    icon: { type: String, default: '📭' },
    title: { type: String, default: '暂无数据' },
    desc: { type: String, default: '' },
    actionText: { type: String, default: '' }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $jst-space-xxl $jst-page-padding;
}

.jst-empty-state__icon-wrap {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  background: $jst-bg-grey;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $jst-space-lg;
}

.jst-empty-state__icon {
  font-size: 72rpx;
}

.jst-empty-state__title {
  font-size: $jst-font-md;
  font-weight: $jst-weight-medium;
  color: $jst-text-primary;
  margin-bottom: $jst-space-xs;
}

.jst-empty-state__desc {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
  text-align: center;
  line-height: 1.6;
  margin-bottom: $jst-space-lg;
}

.jst-empty-state__btn {
  min-width: 200rpx;
}
</style>
