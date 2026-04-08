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
        :key="item.id + '-dot'"
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
      this.$emit('tap', item)
    }
  }
}
</script>

<style scoped lang="scss">
.banner-swiper {
  position: relative;
}

.banner-swiper__inner {
  height: 304rpx;
  border-radius: var(--jst-radius-lg);
  overflow: hidden;
  box-shadow: var(--jst-shadow-strong);
}

.banner-swiper__slide {
  position: relative;
  width: 100%;
  height: 100%;
}

.banner-swiper__image {
  width: 100%;
  height: 100%;
  background: var(--jst-color-border);
}

.banner-swiper__image--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(140deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%);
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
  padding: 28rpx;
  background: linear-gradient(180deg, transparent 0%, var(--jst-color-mask-dark) 100%);
}

.banner-swiper__tag {
  align-self: flex-start;
  padding: 8rpx 16rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-white-18);
  font-size: 20rpx;
  color: var(--jst-color-card-bg);
}

.banner-swiper__title {
  display: block;
  margin-top: 16rpx;
  font-size: 36rpx;
  font-weight: 800;
  line-height: 1.35;
  color: var(--jst-color-card-bg);
}

.banner-swiper__subtitle {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: var(--jst-color-white-78);
}

.banner-swiper__dots {
  position: absolute;
  right: 24rpx;
  bottom: 22rpx;
  display: flex;
  gap: 8rpx;
}

.banner-swiper__dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 999rpx;
  background: var(--jst-color-white-24);
}

.banner-swiper__dot--active {
  width: 30rpx;
  background: var(--jst-color-card-bg);
}
</style>
