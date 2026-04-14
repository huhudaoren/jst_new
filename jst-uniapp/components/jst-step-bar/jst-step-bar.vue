<!-- jst-step-bar: 横向步骤条组件，用于报名/审核等多步骤流程 -->
<template>
  <view class="jst-step-bar">
    <view
      v-for="(step, index) in steps"
      :key="index"
      class="jst-step-bar__item"
      :class="{
        'jst-step-bar__item--active': index === current,
        'jst-step-bar__item--done': index < current
      }"
    >
      <view class="jst-step-bar__circle">
        <text v-if="index < current" class="jst-step-bar__check">&#10003;</text>
        <text v-else class="jst-step-bar__num">{{ index + 1 }}</text>
      </view>
      <text class="jst-step-bar__label">{{ step }}</text>
      <view v-if="index < steps.length - 1" class="jst-step-bar__connector" :class="{ 'jst-step-bar__connector--done': index < current }"></view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'JstStepBar',
  props: {
    steps: {
      type: Array,
      default: function() { return [] }
    },
    current: {
      type: Number,
      default: 0
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-step-bar {
  display: flex;
  align-items: flex-start;
  padding: $jst-space-lg $jst-page-padding;
}

.jst-step-bar__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex: 1;
}

.jst-step-bar__circle {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background: $jst-bg-grey;
  border: 3rpx solid $jst-border;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all $jst-duration-normal $jst-easing;
  z-index: 1;
}

.jst-step-bar__item--active .jst-step-bar__circle {
  background: $jst-brand;
  border-color: $jst-brand;
  box-shadow: 0 0 0 6rpx rgba($jst-brand, 0.15);
}

.jst-step-bar__item--done .jst-step-bar__circle {
  background: $jst-success;
  border-color: $jst-success;
}

.jst-step-bar__num {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-text-secondary;
}

.jst-step-bar__item--active .jst-step-bar__num {
  color: $jst-text-inverse;
}

.jst-step-bar__check {
  font-size: 22rpx;
  color: $jst-text-inverse;
  line-height: 1;
}

.jst-step-bar__label {
  margin-top: 10rpx;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
  text-align: center;
}

.jst-step-bar__item--active .jst-step-bar__label {
  color: $jst-brand;
  font-weight: $jst-weight-medium;
}

.jst-step-bar__item--done .jst-step-bar__label {
  color: $jst-success;
}

.jst-step-bar__connector {
  position: absolute;
  top: 24rpx;
  left: calc(50% + 28rpx);
  right: calc(-50% + 28rpx);
  height: 3rpx;
  background: $jst-border;
  z-index: 0;
  transition: background $jst-duration-normal $jst-easing;
}

.jst-step-bar__connector--done {
  background: $jst-success;
}
</style>
