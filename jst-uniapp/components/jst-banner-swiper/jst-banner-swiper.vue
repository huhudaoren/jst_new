<!-- 中文注释: 首页 banner 轮播组件；对应原型 小程序原型图/index.html；对应接口 GET /jst/wx/index/banner -->
<template>
  <view v-if="list.length" class="banner-swiper">
    <swiper
      class="banner-swiper__inner"
      circular
      autoplay
      interval="4000"
      duration="450"
      @change="handleChange"
    >
      <swiper-item
        v-for="item in list"
        :key="item.id"
      >
        <view class="banner-swiper__slide" @tap="handleTap(item)">
          <image
            v-if="item.image"
            class="banner-swiper__image"
            :src="item.image"
            mode="aspectFill"
          />
          <view v-else class="banner-swiper__image banner-swiper__image--fallback">
            <text class="banner-swiper__fallback-icon">🏆</text>
          </view>

          <view class="banner-swiper__overlay">
            <text class="banner-swiper__tag">官方推荐</text>
            <text class="banner-swiper__title">{{ item.title }}</text>
            <text class="banner-swiper__subtitle">{{ item.subtitle || '点击查看公告详情' }}</text>
          </view>
        </view>
      </swiper-item>
    </swiper>

    <view class="banner-swiper__dots">
      <view
        v-for="(item, index) in list"
        :key="index"
        class="banner-swiper__dot"
        :class="{ 'banner-swiper__dot--active': current === index }"
      ></view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'JstBannerSwiper',
  props: {
    list: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      current: 0
    }
  },
  methods: {
    handleChange(event) {
      this.current = event.detail.current || 0
    },

    handleTap(item) {
      this.$emit('item-tap', item)
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.banner-swiper {
  position: relative;
}

.banner-swiper__inner {
  height: 304rpx;
  border-radius: $jst-radius-xl;
  overflow: hidden;
  box-shadow: $jst-shadow-lg;
}

.banner-swiper__slide {
  position: relative;
  width: 100%;
  height: 100%;
}

.banner-swiper__image {
  width: 100%;
  height: 100%;
  background: $jst-border;
}

.banner-swiper__image--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: $jst-brand-gradient;
}

.banner-swiper__fallback-icon {
  font-size: 72rpx;
}

.banner-swiper__overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: $jst-space-lg;
  background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.42) 100%);
}

.banner-swiper__tag {
  align-self: flex-start;
  padding: $jst-space-xs $jst-space-md;
  border-radius: $jst-radius-round;
  background: rgba(255, 255, 255, 0.18);
  font-size: $jst-font-xs;
  color: $jst-text-inverse;
}

.banner-swiper__title {
  display: block;
  margin-top: $jst-space-md;
  font-size: $jst-font-xl;
  font-weight: 800;
  line-height: 1.35;
  color: $jst-text-inverse;
}

.banner-swiper__subtitle {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  color: rgba(255, 255, 255, 0.78);
}

.banner-swiper__dots {
  position: absolute;
  right: $jst-space-lg;
  bottom: 22rpx;
  display: flex;
  gap: $jst-space-xs;
}

.banner-swiper__dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: $jst-radius-round;
  background: rgba(255, 255, 255, 0.24);
  transition: width $jst-duration-normal $jst-easing, background $jst-duration-normal $jst-easing;
}

.banner-swiper__dot--active {
  width: 30rpx;
  background: $jst-text-inverse;
}
</style>
