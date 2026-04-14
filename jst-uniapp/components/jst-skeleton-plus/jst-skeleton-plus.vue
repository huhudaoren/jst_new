<!-- jst-skeleton-plus: 增强骨架屏，支持卡片形态匹配 + 微光脉冲动画 -->
<template>
  <view v-if="loading" class="jst-skeleton-plus" :class="'jst-skeleton-plus--' + mode">
    <!-- hero 模式: 大图 + 标题 + 标签 -->
    <template v-if="mode === 'hero'">
      <view class="jst-skeleton-plus__hero-img jst-anim-glow-pulse"></view>
      <view class="jst-skeleton-plus__body">
        <view class="jst-skeleton-plus__bar jst-skeleton-plus__bar--title jst-anim-glow-pulse"></view>
        <view class="jst-skeleton-plus__bar jst-skeleton-plus__bar--tags jst-anim-glow-pulse"></view>
        <view class="jst-skeleton-plus__bar jst-skeleton-plus__bar--text jst-anim-glow-pulse"></view>
        <view class="jst-skeleton-plus__bar jst-skeleton-plus__bar--text jst-skeleton-plus__bar--short jst-anim-glow-pulse"></view>
      </view>
    </template>

    <!-- card 模式: 多张卡片 -->
    <template v-else-if="mode === 'card'">
      <view v-for="i in count" :key="i" class="jst-skeleton-plus__card jst-anim-glow-pulse">
        <view class="jst-skeleton-plus__card-cover"></view>
        <view class="jst-skeleton-plus__card-body">
          <view class="jst-skeleton-plus__bar jst-skeleton-plus__bar--title"></view>
          <view class="jst-skeleton-plus__bar jst-skeleton-plus__bar--text"></view>
          <view class="jst-skeleton-plus__bar jst-skeleton-plus__bar--short"></view>
        </view>
      </view>
    </template>

    <!-- list 模式: 行列表 -->
    <template v-else-if="mode === 'list'">
      <view v-for="i in count" :key="i" class="jst-skeleton-plus__list-item jst-anim-glow-pulse">
        <view class="jst-skeleton-plus__list-avatar"></view>
        <view class="jst-skeleton-plus__list-body">
          <view class="jst-skeleton-plus__bar jst-skeleton-plus__bar--title"></view>
          <view class="jst-skeleton-plus__bar jst-skeleton-plus__bar--text jst-skeleton-plus__bar--short"></view>
        </view>
      </view>
    </template>

    <!-- default: 通用条形 -->
    <template v-else>
      <view class="jst-skeleton-plus__body">
        <view v-for="i in count" :key="i" class="jst-skeleton-plus__bar jst-anim-glow-pulse" :class="i === 1 ? 'jst-skeleton-plus__bar--title' : 'jst-skeleton-plus__bar--text'" :style="{ width: i === count ? '60%' : '100%' }"></view>
      </view>
    </template>
  </view>
</template>

<script>
export default {
  name: 'JstSkeletonPlus',
  props: {
    loading: { type: Boolean, default: true },
    mode: {
      type: String,
      default: 'default',
      validator: function(v) { return ['default', 'hero', 'card', 'list'].indexOf(v) !== -1 }
    },
    count: { type: Number, default: 3 }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-skeleton-plus {
  padding: $jst-page-padding;
}

.jst-skeleton-plus__body {
  padding: $jst-space-lg;
}

.jst-skeleton-plus__bar {
  border-radius: $jst-radius-sm;
  background: $jst-bg-grey;
  margin-bottom: $jst-space-md;
}

.jst-skeleton-plus__bar--title {
  height: 36rpx;
  width: 70%;
}

.jst-skeleton-plus__bar--tags {
  height: 28rpx;
  width: 45%;
}

.jst-skeleton-plus__bar--text {
  height: 24rpx;
  width: 100%;
}

.jst-skeleton-plus__bar--short {
  width: 60%;
}

// Hero
.jst-skeleton-plus__hero-img {
  width: 100%;
  height: 360rpx;
  background: $jst-bg-grey;
  border-radius: 0 0 $jst-radius-card $jst-radius-card;
}

// Card
.jst-skeleton-plus__card {
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  margin-bottom: $jst-space-lg;
  overflow: hidden;
  box-shadow: $jst-shadow-ring;
}

.jst-skeleton-plus__card-cover {
  width: 100%;
  height: 200rpx;
  background: $jst-bg-grey;
}

.jst-skeleton-plus__card-body {
  padding: $jst-space-lg;
}

// List
.jst-skeleton-plus__list-item {
  display: flex;
  align-items: center;
  gap: $jst-space-md;
  padding: $jst-space-md 0;
}

.jst-skeleton-plus__list-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: $jst-bg-grey;
  flex-shrink: 0;
}

.jst-skeleton-plus__list-body {
  flex: 1;
}
</style>
