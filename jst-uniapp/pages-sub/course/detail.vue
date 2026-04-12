<!-- 中文注释: 课程详情页；对照原型 小程序原型图/course-detail.html；接口 GET /jst/wx/course/{id} -->
<template>
  <view class="cd-page">
    <!-- 骨架屏 -->
    <view v-if="pageLoading" class="cd-skeleton">
      <view class="cd-skeleton__hero" />
      <view class="cd-skeleton__body">
        <view class="cd-skeleton__bar cd-skeleton__bar--title" />
        <view class="cd-skeleton__bar cd-skeleton__bar--tags" />
        <view class="cd-skeleton__bar cd-skeleton__bar--stats" />
        <view class="cd-skeleton__bar cd-skeleton__bar--teacher" />
        <view class="cd-skeleton__bar cd-skeleton__bar--content" />
        <view class="cd-skeleton__bar cd-skeleton__bar--content" />
      </view>
    </view>

    <template v-else-if="!loadError">
      <!-- 1. 封面区 Hero -->
      <view class="cd-hero">
        <image
          v-if="detail.coverImage"
          class="cd-hero__image"
          :src="detail.coverImage"
          mode="aspectFill"
        />
        <view v-else class="cd-hero__fallback">
          <text class="cd-hero__emoji">{{ detail.courseType === 'ai_maic' ? 'AI' : '📖' }}</text>
        </view>
        <!-- 顶部圆角遮罩 -->
        <view class="cd-hero__curve" />
        <!-- 导航 -->
        <view class="cd-hero__nav" :style="{ paddingTop: navPaddingTop }">
          <view class="cd-hero__btn" @tap="handleBack">
            <text>←</text>
          </view>
          <view class="cd-hero__btn" @tap="handleShare">
            <text>⋮</text>
          </view>
        </view>
      </view>

      <view class="cd-body">
        <!-- 2. 标题卡 -->
        <view class="cd-title-card">
          <text class="cd-title-card__name">{{ detail.courseName || '课程名称待完善' }}</text>
          <view class="cd-title-card__tags">
            <view class="cd-tag cd-tag--primary">
              <text class="cd-tag__text">{{ detail.courseType === 'ai_maic' ? 'AI课程' : '精品课程' }}</text>
            </view>
            <view v-if="Number(detail.price || 0) === 0" class="cd-tag cd-tag--success">
              <text class="cd-tag__text">免费</text>
            </view>
          </view>

          <!-- 统计行 -->
          <view class="cd-stats">
            <view class="cd-stats__item">
              <text class="cd-stats__icon">📖</text>
              <text class="cd-stats__value">{{ detail.lessonCount || 0 }}讲</text>
            </view>
            <view class="cd-stats__item">
              <text class="cd-stats__icon">👥</text>
              <text class="cd-stats__value">{{ formatNumber(detail.learnerCount) }}人已学</text>
            </view>
            <view class="cd-stats__item">
              <text class="cd-stats__icon">🕐</text>
              <text class="cd-stats__value">{{ detail.totalDuration || '待更新' }}</text>
            </view>
          </view>
        </view>

        <!-- 3. 讲师卡片 -->
        <view v-if="detail.teacherName" class="cd-teacher">
          <view class="cd-teacher__avatar-wrap">
            <image
              v-if="detail.teacherAvatar"
              class="cd-teacher__avatar"
              :src="detail.teacherAvatar"
              mode="aspectFill"
            />
            <view v-else class="cd-teacher__avatar-fallback">
              <text class="cd-teacher__avatar-emoji">👨‍🏫</text>
            </view>
          </view>
          <view class="cd-teacher__info">
            <text class="cd-teacher__name">{{ detail.teacherName }}</text>
            <text class="cd-teacher__desc">{{ detail.teacherDesc || '' }}</text>
          </view>
          <text class="cd-teacher__arrow">›</text>
        </view>

        <!-- 4. Tab 切换：课程介绍 | 课程目录 -->
        <view class="cd-tabs-card">
          <view class="cd-tabs">
            <view
              class="cd-tabs__item"
              :class="{ 'cd-tabs__item--active': activeTab === 'intro' }"
              @tap="activeTab = 'intro'"
            >
              <text class="cd-tabs__label">课程介绍</text>
            </view>
            <view
              v-if="chapters.length > 0"
              class="cd-tabs__item"
              :class="{ 'cd-tabs__item--active': activeTab === 'chapters' }"
              @tap="activeTab = 'chapters'"
            >
              <text class="cd-tabs__label">课程目录</text>
              <text class="cd-tabs__count">{{ totalLessons }}</text>
            </view>
          </view>

          <!-- 课程介绍面板 -->
          <view v-show="activeTab === 'intro'" class="cd-panel">
            <rich-text class="cd-panel__rich" :nodes="richDescription" />
          </view>

          <!-- 课程目录面板 -->
          <view v-show="activeTab === 'chapters'" class="cd-panel">
            <view
              v-for="(chapter, ci) in chapters"
              :key="ci"
              class="cd-chapter"
            >
              <!-- 章节标题 -->
              <view
                class="cd-chapter__header"
                @tap="toggleChapter(ci)"
              >
                <text class="cd-chapter__title">{{ chapter.title || ('第' + (ci + 1) + '章') }}</text>
                <text class="cd-chapter__toggle">{{ expandedChapters.includes(ci) ? '∧' : '∨' }}</text>
              </view>
              <!-- 课时列表（折叠） -->
              <view
                class="cd-chapter__body"
                :class="{ 'cd-chapter__body--open': expandedChapters.includes(ci) }"
              >
                <view
                  v-for="(lesson, li) in (chapter.lessons || [])"
                  :key="li"
                  class="cd-lesson"
                >
                  <view class="cd-lesson__num">
                    <text class="cd-lesson__num-text">{{ li + 1 }}</text>
                  </view>
                  <view class="cd-lesson__info">
                    <text class="cd-lesson__title">{{ lesson.title }}</text>
                    <text class="cd-lesson__meta">{{ lesson.duration || '' }}</text>
                  </view>
                  <view v-if="lesson.free" class="cd-lesson__free-tag">
                    <text class="cd-lesson__free-text">免费试看</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 5. 底部操作栏 -->
      <view class="cd-bottom">
        <view class="cd-bottom__price-area">
          <text class="cd-bottom__label">课程价格</text>
          <view class="cd-bottom__price-row">
            <text v-if="Number(detail.price || 0) > 0" class="cd-bottom__price">¥{{ formatPrice(detail.price) }}</text>
            <text v-else class="cd-bottom__price cd-bottom__price--free">免费</text>
          </view>
        </view>
        <view class="cd-bottom__actions">
          <view class="cd-bottom__btn-ghost" @tap="handleFavorite">
            <text>{{ isFavorite ? '♥' : '♡' }}</text>
            <text class="cd-bottom__btn-text">收藏</text>
          </view>
          <view class="cd-bottom__btn-buy" @tap="handleBuy">
            <text class="cd-bottom__btn-buy-text">立即购买</text>
          </view>
        </view>
      </view>
    </template>

    <!-- 加载失败 -->
    <jst-empty
      v-else
      text="课程详情暂不可用"
      icon="📖"
    />
  </view>
</template>

<script>
import { getCourseDetail } from '@/api/course'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'

export default {
  components: { JstEmpty },

  data() {
    return {
      courseId: '',
      pageLoading: false,
      loadError: false,
      detail: {},
      richDescription: '',
      // 课程目录
      chapters: [],
      expandedChapters: [0],
      activeTab: 'intro',
      isFavorite: false
    }
  },

  computed: {
    // 计算总课时数
    totalLessons() {
      return this.chapters.reduce((sum, ch) => sum + (ch.lessons ? ch.lessons.length : 0), 0)
    }
  },

  onLoad(options) {
    this.courseId = options.id || ''
    this.loadDetail()
  },

  // 中文注释: 微信分享配置
  onShareAppMessage() {
    return {
      title: this.detail.courseName || '精品课程',
      path: `/pages-sub/course/detail?id=${this.courseId}`
    }
  },

  methods: {
    async loadDetail() {
      if (!this.courseId) {
        this.loadError = true
        return
      }
      this.pageLoading = true
      try {
        const detail = await getCourseDetail(this.courseId, { silent: true })
        this.detail = detail || {}
        this.richDescription = this.buildRichDescription(this.detail.description)
        this.parseChapters(this.detail.chaptersJson)
        this.loadError = false
      } catch (error) {
        this.loadError = true
      } finally {
        this.pageLoading = false
      }
    },

    // 中文注释: 解析 chaptersJson，格式约定为 [{title, lessons: [{title, duration, free}]}]
    parseChapters(json) {
      if (!json) {
        this.chapters = []
        return
      }
      try {
        const parsed = JSON.parse(json)
        this.chapters = Array.isArray(parsed) ? parsed : []
      } catch (e) {
        this.chapters = []
      }
    },

    // 中文注释: rich-text 清洗，去掉 script/事件属性
    buildRichDescription(content) {
      const source = `${content || ''}`
      const sanitized = source
        .replace(/<\s*(script|style|iframe|object|embed|link|meta)[^>]*>[\s\S]*?<\s*\/\s*\1\s*>/gi, '')
        .replace(/\son[a-z]+\s*=\s*(['"]).*?\1/gi, '')
        .replace(/\son[a-z]+\s*=\s*[^\s>]+/gi, '')
        .replace(/javascript:/gi, '')
        .replace(/<img/gi, '<img style="max-width:100%;height:auto;display:block;border-radius:12rpx;margin:16rpx 0;"')
        .replace(/<p/gi, '<p style="font-size:28rpx;line-height:1.8;color:#4A4A4A;margin:0 0 20rpx;"')
        .replace(/<h4/gi, '<h4 style="font-size:28rpx;font-weight:700;color:#1A1A1A;margin:28rpx 0 12rpx;"')
        .replace(/<ul/gi, '<ul style="padding-left:32rpx;margin:0 0 20rpx;font-size:28rpx;line-height:1.8;color:#4A4A4A;"')
        .replace(/<ol/gi, '<ol style="padding-left:32rpx;margin:0 0 20rpx;font-size:28rpx;line-height:1.8;color:#4A4A4A;"')
        .replace(/<li/gi, '<li style="margin-bottom:12rpx;"')
      return sanitized || '<p style="font-size:28rpx;line-height:1.8;color:#4A4A4A;">课程介绍整理中</p>'
    },

    toggleChapter(index) {
      const pos = this.expandedChapters.indexOf(index)
      if (pos >= 0) {
        this.expandedChapters.splice(pos, 1)
      } else {
        this.expandedChapters.push(index)
      }
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.switchTab({ url: '/pages/course/list' })
    },

    handleShare() {
      // 中文注释: 小程序内用 onShareAppMessage，此处留空
    },

    handleFavorite() {
      this.isFavorite = !this.isFavorite
      uni.showToast({
        title: this.isFavorite ? '已收藏' : '已取消收藏',
        icon: 'none'
      })
    },

    handleBuy() {
      uni.showToast({
        title: '课程购买即将开放',
        icon: 'none'
      })
    },

    formatPrice(value) {
      return Number(value || 0).toFixed(2)
    },

    formatNumber(value) {
      const num = Number(value || 0)
      if (num >= 10000) return (num / 10000).toFixed(1) + '万'
      if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
      return String(num)
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
@import '@/styles/mixins.scss';

.cd-page {
  min-height: 100vh;
  background: $jst-bg-page;
  padding-bottom: calc(160rpx + env(safe-area-inset-bottom));
}

/* ========== 骨架屏 ========== */
@keyframes cd-shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.cd-skeleton {
  &__hero {
    height: 400rpx;
    background: linear-gradient(90deg, $jst-border 0%, $jst-bg-grey 50%, $jst-border 100%);
    background-size: 200% 100%;
    animation: cd-shimmer 1.2s ease-in-out infinite;
  }

  &__body {
    padding: $jst-page-padding;
  }

  &__bar {
    background: linear-gradient(90deg, $jst-border 0%, $jst-bg-grey 50%, $jst-border 100%);
    background-size: 200% 100%;
    animation: cd-shimmer 1.2s ease-in-out infinite;
    border-radius: $jst-radius-md;
    margin-bottom: $jst-space-lg;

    &--title  { height: 48rpx; width: 80%; }
    &--tags   { height: 32rpx; width: 50%; }
    &--stats  { height: 36rpx; width: 70%; }
    &--teacher { height: 100rpx; width: 100%; }
    &--content { height: 200rpx; width: 100%; }
  }
}

/* ========== 1. Hero 封面区 ========== */
.cd-hero {
  position: relative;
  height: 400rpx;
  overflow: hidden;
  background: $jst-indigo-gradient;

  &__image {
    width: 100%;
    height: 100%;
  }

  &__fallback {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__emoji {
    font-size: 160rpx;
    opacity: 0.9;
  }

  // 底部圆角遮罩
  &__curve {
    position: absolute;
    bottom: -2rpx;
    left: 0;
    right: 0;
    height: 48rpx;
    background: $jst-bg-page;
    border-radius: 48rpx 48rpx 0 0;
  }

  // 顶部导航按钮
  &__nav {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24rpx $jst-page-padding;
  }

  &__btn {
    width: 72rpx;
    height: 72rpx;
    border-radius: 50%;
    background: rgba($jst-bg-card, 0.2);
    border: 2rpx solid rgba($jst-bg-card, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32rpx;
    color: $jst-text-inverse;
  }
}

/* ========== 内容区 ========== */
.cd-body {
  padding: 0 $jst-page-padding;
  margin-top: -$jst-space-lg;
}

/* ========== 2. 标题卡 ========== */
.cd-title-card {
  background: $jst-bg-card;
  border-radius: $jst-radius-lg;
  padding: $jst-space-lg;
  box-shadow: $jst-shadow-sm;
  margin-bottom: $jst-space-lg;

  &__name {
    display: block;
    font-size: 38rpx;
    font-weight: 800;
    color: $jst-text-primary;
    line-height: 1.35;
    letter-spacing: -0.6rpx;
    margin-bottom: $jst-space-md;
  }

  &__tags {
    display: flex;
    gap: $jst-space-sm;
    flex-wrap: wrap;
    margin-bottom: $jst-space-lg;
  }
}

.cd-tag {
  display: inline-flex;
  align-items: center;
  height: 44rpx;
  padding: 0 $jst-space-md;
  border-radius: $jst-radius-round;
  font-size: $jst-font-xs;
  font-weight: $jst-weight-semibold;

  &--primary {
    background: $jst-color-brand-soft;
    .cd-tag__text { color: $jst-brand; }
  }

  &--success {
    background: $jst-success-light;
    .cd-tag__text { color: $jst-success; }
  }
}

/* 统计行 */
.cd-stats {
  display: flex;
  gap: $jst-space-lg;

  &__item {
    display: flex;
    align-items: center;
    gap: $jst-space-xs;
  }

  &__icon {
    font-size: $jst-font-sm;
  }

  &__value {
    font-size: $jst-font-sm;
    color: $jst-text-secondary;
  }
}

/* ========== 3. 讲师卡片 ========== */
.cd-teacher {
  background: $jst-bg-card;
  border-radius: $jst-radius-lg;
  padding: 28rpx $jst-space-lg;
  box-shadow: $jst-shadow-sm;
  display: flex;
  gap: $jst-space-lg;
  align-items: center;
  margin-bottom: $jst-space-lg;
  transition: transform $jst-duration-fast $jst-easing;

  &:active {
    transform: scale(0.98);
  }

  &__avatar-wrap {
    flex-shrink: 0;
  }

  &__avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    border: 4rpx solid rgba($jst-bg-card, 0.8);
    box-shadow: $jst-shadow-sm;
  }

  &__avatar-fallback {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, $jst-gold, $jst-warning);
    display: flex;
    align-items: center;
    justify-content: center;
    border: 4rpx solid rgba($jst-bg-card, 0.8);
    box-shadow: $jst-shadow-sm;
  }

  &__avatar-emoji {
    font-size: 48rpx;
  }

  &__info {
    flex: 1;
  }

  &__name {
    display: block;
    font-size: $jst-font-md;
    font-weight: $jst-weight-bold;
    color: $jst-text-primary;
  }

  &__desc {
    display: block;
    font-size: $jst-font-sm;
    color: $jst-text-secondary;
    margin-top: 4rpx;
    line-height: 1.4;
  }

  &__arrow {
    font-size: 28rpx;
    color: $jst-text-placeholder;
    flex-shrink: 0;
  }
}

/* ========== 4. Tab 详情 ========== */
.cd-tabs-card {
  background: $jst-bg-card;
  border-radius: $jst-radius-lg;
  box-shadow: $jst-shadow-sm;
  margin-bottom: $jst-space-lg;
  overflow: hidden;
}

.cd-tabs {
  display: flex;
  border-bottom: 2rpx solid $jst-border;

  &__item {
    flex: 1;
    text-align: center;
    padding: $jst-space-lg $jst-space-md;
    position: relative;
    transition: color $jst-duration-fast;

    &--active {
      .cd-tabs__label {
        color: $jst-brand;
        font-weight: $jst-weight-bold;
      }

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 20%;
        right: 20%;
        height: 4rpx;
        background: $jst-brand;
        border-radius: 2rpx;
      }
    }
  }

  &__label {
    font-size: $jst-font-base;
    font-weight: $jst-weight-medium;
    color: $jst-text-secondary;
  }

  &__count {
    font-size: $jst-font-xs;
    color: $jst-text-placeholder;
    margin-left: $jst-space-xs;
  }
}

.cd-panel {
  padding: $jst-space-lg;

  &__rich {
    font-size: $jst-font-base;
    line-height: 1.8;
    color: $jst-text-regular;
  }
}

/* ========== 课程目录折叠 ========== */
.cd-chapter {
  margin-bottom: $jst-space-md;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $jst-space-md 0;
    border-bottom: 2rpx solid $jst-border;
  }

  &__title {
    font-size: $jst-font-base;
    font-weight: $jst-weight-bold;
    color: $jst-text-primary;
  }

  &__toggle {
    font-size: $jst-font-sm;
    color: $jst-text-placeholder;
  }

  // 折叠动画
  &__body {
    max-height: 0;
    overflow: hidden;
    transition: max-height $jst-duration-normal $jst-easing;

    &--open {
      max-height: 2000rpx;
    }
  }
}

.cd-lesson {
  display: flex;
  align-items: center;
  gap: $jst-space-lg;
  padding: $jst-space-lg 0;
  border-bottom: 2rpx solid $jst-border;

  &:last-child {
    border-bottom: none;
  }

  &__num {
    width: 56rpx;
    height: 56rpx;
    border-radius: 50%;
    background: $jst-color-brand-soft;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  &__num-text {
    font-size: $jst-font-sm;
    font-weight: $jst-weight-bold;
    color: $jst-brand;
  }

  &__info {
    flex: 1;
  }

  &__title {
    display: block;
    font-size: $jst-font-base;
    font-weight: $jst-weight-medium;
    color: $jst-text-primary;
    margin-bottom: 4rpx;
  }

  &__meta {
    display: block;
    font-size: $jst-font-sm;
    color: $jst-text-secondary;
  }

  &__free-tag {
    flex-shrink: 0;
    height: 40rpx;
    padding: 0 $jst-space-md;
    border-radius: $jst-radius-round;
    background: $jst-success-light;
    display: flex;
    align-items: center;
  }

  &__free-text {
    font-size: $jst-font-xs;
    font-weight: $jst-weight-semibold;
    color: $jst-success;
  }
}

/* ========== 5. 底部操作栏 ========== */
.cd-bottom {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba($jst-bg-card, 0.97);
  backdrop-filter: blur(16rpx);
  border-top: 2rpx solid $jst-border;
  padding: $jst-space-lg $jst-page-padding;
  padding-bottom: calc(#{$jst-space-lg} + env(safe-area-inset-bottom));
  display: flex;
  align-items: center;
  z-index: 40;
  box-shadow: 0 -8rpx 40rpx rgba(12, 61, 107, 0.08);

  &__price-area {
    flex: 1;
  }

  &__label {
    display: block;
    font-size: $jst-font-xs;
    color: $jst-text-secondary;
  }

  &__price-row {
    display: flex;
    align-items: baseline;
    gap: $jst-space-xs;
  }

  &__price {
    font-size: 48rpx;
    font-weight: 900;
    color: $jst-danger;
    line-height: 1;

    &--free {
      color: $jst-success;
    }
  }

  &__actions {
    display: flex;
    align-items: center;
    gap: $jst-space-md;
  }

  &__btn-ghost {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 88rpx;
    height: 88rpx;
    font-size: 36rpx;
    color: $jst-text-secondary;
    transition: transform $jst-duration-fast;

    &:active {
      transform: scale(0.92);
    }
  }

  &__btn-text {
    font-size: $jst-font-xs;
    color: $jst-text-secondary;
    margin-top: 2rpx;
  }

  &__btn-buy {
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 200rpx;
    height: 88rpx;
    border-radius: $jst-radius-lg;
    background: $jst-gradient-primary;
    box-shadow: 0 8rpx 20rpx rgba(255, 107, 53, 0.2);
    transition: transform $jst-duration-fast ease-out;

    &:active {
      transform: scale(0.98);
    }
  }

  &__btn-buy-text {
    font-size: $jst-font-md;
    font-weight: $jst-weight-semibold;
    color: $jst-text-inverse;
  }
}
</style>
