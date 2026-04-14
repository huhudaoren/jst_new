<!-- jst-timeline: 垂直时间轴组件，用于订单/退款进度、赛程展示 -->
<template>
  <view class="jst-timeline">
    <view
      v-for="(item, index) in items"
      :key="index"
      class="jst-timeline__item"
      :class="{
        'jst-timeline__item--active': index === activeIndex,
        'jst-timeline__item--done': index < activeIndex
      }"
    >
      <view class="jst-timeline__rail">
        <view class="jst-timeline__dot" :class="getDotClass(index)">
          <text v-if="index < activeIndex" class="jst-timeline__dot-check">&#10003;</text>
        </view>
        <view v-if="index < items.length - 1" class="jst-timeline__line" :class="{ 'jst-timeline__line--done': index < activeIndex }"></view>
      </view>
      <view class="jst-timeline__content">
        <text class="jst-timeline__label">{{ item.label || item.title || '' }}</text>
        <text v-if="item.time || item.date" class="jst-timeline__time">{{ item.time || item.date }}</text>
        <text v-if="item.desc || item.description" class="jst-timeline__desc">{{ item.desc || item.description }}</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'JstTimeline',
  props: {
    items: {
      type: Array,
      default: function() { return [] }
    },
    activeIndex: {
      type: Number,
      default: -1
    }
  },
  methods: {
    getDotClass: function(index) {
      if (index === this.activeIndex) return 'jst-timeline__dot--active'
      if (index < this.activeIndex) return 'jst-timeline__dot--done'
      return ''
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-timeline {
  padding: $jst-space-md 0;
}

.jst-timeline__item {
  display: flex;
  gap: $jst-space-md;
  min-height: 96rpx;
}

.jst-timeline__rail {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 40rpx;
  flex-shrink: 0;
}

.jst-timeline__dot {
  width: 24rpx;
  height: 24rpx;
  border-radius: 50%;
  background: $jst-border;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all $jst-duration-normal $jst-easing;
}

.jst-timeline__dot--active {
  width: 28rpx;
  height: 28rpx;
  background: $jst-brand;
  box-shadow: 0 0 0 6rpx rgba($jst-brand, 0.15);
}

.jst-timeline__dot--done {
  background: $jst-success;
}

.jst-timeline__dot-check {
  font-size: 16rpx;
  color: $jst-text-inverse;
  line-height: 1;
}

.jst-timeline__line {
  flex: 1;
  width: 3rpx;
  min-height: 48rpx;
  background: $jst-border;
  margin: 6rpx 0;
  transition: background $jst-duration-normal $jst-easing;
}

.jst-timeline__line--done {
  background: $jst-success;
}

.jst-timeline__content {
  flex: 1;
  padding-bottom: $jst-space-lg;
}

.jst-timeline__label {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-medium;
  color: $jst-text-primary;
  line-height: 1.4;
}

.jst-timeline__item--active .jst-timeline__label {
  color: $jst-brand;
  font-weight: $jst-weight-semibold;
}

.jst-timeline__time {
  display: block;
  margin-top: 6rpx;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.jst-timeline__desc {
  display: block;
  margin-top: 8rpx;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
  line-height: 1.6;
}
</style>
