<!-- jst-hero-banner: 沉浸式头图组件，支持视差滚动 + 渐变遮罩 + 标题叠加 -->
<template>
  <view class="jst-hero-banner" :style="{ height: height }">
    <view class="jst-hero-banner__bg" :style="bgStyle">
      <image
        v-if="src"
        class="jst-hero-banner__image"
        :src="src"
        mode="aspectFill"
      />
      <view v-else class="jst-hero-banner__fallback" :style="{ background: fallbackBg }">
        <text v-if="fallbackIcon" class="jst-hero-banner__fallback-icon">{{ fallbackIcon }}</text>
      </view>
    </view>
    <view class="jst-hero-banner__overlay"></view>
    <view class="jst-hero-banner__content" :style="contentStyle">
      <slot />
    </view>
    <view v-if="showNav" class="jst-hero-banner__nav" :style="{ paddingTop: navPaddingTop }">
      <view class="jst-hero-banner__nav-back" @tap="$emit('back')">
        <text class="jst-hero-banner__nav-arrow">&#8592;</text>
      </view>
      <text v-if="navTitle" class="jst-hero-banner__nav-title" :style="{ opacity: navTitleOpacity }">{{ navTitle }}</text>
      <view class="jst-hero-banner__nav-right">
        <slot name="nav-right" />
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'JstHeroBanner',
  props: {
    src: { type: String, default: '' },
    height: { type: String, default: '480rpx' },
    fallbackBg: { type: String, default: '' },
    fallbackIcon: { type: String, default: '' },
    showNav: { type: Boolean, default: true },
    navTitle: { type: String, default: '' },
    scrollTop: { type: Number, default: 0 },
    parallaxRatio: { type: Number, default: 0.35 }
  },
  computed: {
    // [visual-effect]
    navPaddingTop: function() {
      var info = uni.getSystemInfoSync()
      return (info.statusBarHeight || 44) + 'px'
    },
    // [visual-effect]
    bgStyle: function() {
      var ty = this.scrollTop * this.parallaxRatio
      var scale = 1 + this.scrollTop * 0.0003
      return {
        transform: 'translateY(' + ty + 'rpx) scale(' + Math.min(scale, 1.15) + ')'
      }
    },
    // [visual-effect]
    contentStyle: function() {
      var opacity = Math.max(0, 1 - this.scrollTop / 300)
      return { opacity: opacity }
    },
    // [visual-effect]
    navTitleOpacity: function() {
      return Math.min(1, Math.max(0, (this.scrollTop - 200) / 100))
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-hero-banner {
  position: relative;
  overflow: hidden;
}

.jst-hero-banner__bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  will-change: transform;
}

.jst-hero-banner__image {
  width: 100%;
  height: 100%;
}

.jst-hero-banner__fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $jst-hero-gradient;
}

.jst-hero-banner__fallback-icon {
  font-size: 96rpx;
}

.jst-hero-banner__overlay {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 60%;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.5) 0%, transparent 100%);
  pointer-events: none;
}

.jst-hero-banner__content {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: $jst-space-xl $jst-page-padding;
  z-index: 2;
  will-change: opacity;
}

.jst-hero-banner__nav {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-space-lg;
}

.jst-hero-banner__nav-back {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.jst-hero-banner__nav-arrow {
  font-size: 36rpx;
  color: $jst-text-inverse;
}

.jst-hero-banner__nav-title {
  flex: 1;
  text-align: center;
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  color: $jst-text-inverse;
  will-change: opacity;
}

.jst-hero-banner__nav-right {
  width: 64rpx;
  flex-shrink: 0;
  display: flex;
  justify-content: flex-end;
}
</style>
