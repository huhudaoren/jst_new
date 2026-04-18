<template>
  <view class="my-course-page">
    <jst-loading :loading="pageLoading" text="课程列表加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view class="my-course-page__nav">
      <view class="my-course-page__back" @tap="handleBack">返回</view>
      <text class="my-course-page__nav-title">我的课程</text>
      <u-button class="my-course-page__nav-action" size="mini" plain @click="goCourseTab">去选课</u-button>
    </view>

    <view class="my-course-page__tabs">
      <view
        v-for="item in tabs"
        :key="item.value"
        class="my-course-page__tab"
        :class="{ 'my-course-page__tab--active': currentTab === item.value }"
        @tap="switchTab(item.value)"
      >
        {{ item.label }}
      </view>
    </view>

    <view class="my-course-page__summary">
      <view class="my-course-page__summary-item">
        <text class="my-course-page__summary-num">{{ courses.length }}</text>
        <text class="my-course-page__summary-label">已购课程</text>
      </view>
      <view class="my-course-page__summary-item">
        <text class="my-course-page__summary-num">{{ learningCount }}</text>
        <text class="my-course-page__summary-label">学习中</text>
      </view>
      <view class="my-course-page__summary-item">
        <text class="my-course-page__summary-num">{{ completedCount }}</text>
        <text class="my-course-page__summary-label">已完成</text>
      </view>
      <view class="my-course-page__summary-item">
        <text class="my-course-page__summary-num">{{ totalMinutes }}</text>
        <text class="my-course-page__summary-label">学习分钟</text>
      </view>
    </view>

    <view v-if="filteredCourses.length" class="my-course-page__list">
      <view
        v-for="item in filteredCourses"
        :key="item.courseId"
        class="my-course-page__card"
      >
        <view class="my-course-page__card-header">
          <view class="my-course-page__cover">
            <image v-if="item.coverImage" class="my-course-page__cover-image" :src="item.coverImage" mode="aspectFill" />
            <view v-else class="my-course-page__cover-fallback" :class="[`my-course-page__cover-fallback--${item.completeStatus}`]">
              <text class="my-course-page__cover-emoji">{{ item.courseType === 'ai_maic' ? 'AI' : '课' }}</text>
            </view>
          </view>

          <view class="my-course-page__card-main">
            <text class="my-course-page__card-title">{{ item.courseName || '课程名称待补齐' }}</text>
            <text class="my-course-page__card-meta">{{ getCourseTypeText(item.courseType) }} · {{ getCompleteText(item.completeStatus) }}</text>
            <text class="my-course-page__card-meta">学习时长：{{ getDurationText(item.durationSeconds) }}</text>
          </view>
        </view>

        <view class="my-course-page__progress">
          <view class="my-course-page__progress-header">
            <text class="my-course-page__progress-label">学习进度</text>
            <text class="my-course-page__progress-num">{{ item.progressText }}</text>
          </view>
          <view class="my-course-page__progress-bar">
            <view
              class="my-course-page__progress-fill"
              :class="{ 'my-course-page__progress-fill--done': item.completeStatus === 'completed' }"
              :style="{ width: item.progressText }"
            ></view>
          </view>
          <text class="my-course-page__progress-foot">
            {{ item.completeTime ? `完课时间：${formatDateTime(item.completeTime)}` : '最近学习记录待同步' }}
          </text>
        </view>

        <view class="my-course-page__actions">
          <u-button class="my-course-page__action my-course-page__action--ghost" @click="openDetail(item)">
            查看详情
          </u-button>
          <u-button class="my-course-page__action my-course-page__action--primary" @click="openDetail(item)">
            {{ item.completeStatus === 'completed' ? '重新学习' : '继续学习' }}
          </u-button>
        </view>
      </view>
    </view>

    <jst-empty
      v-else-if="!pageLoading"
      icon="📚"
      text="当前筛选下暂无课程记录。"
    />
  </view>
</template>

<script>
import { getMyCourses } from '@/api/course'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: {
    JstEmpty,
    JstLoading
  },
  data() {
    return {
      pageLoading: false,
      currentTab: 'all',
      courses: [],
      tabs: [
        { label: '全部', value: 'all' },
        { label: '学习中', value: 'learning' },
        { label: '已完成', value: 'completed' },
        { label: 'AI课堂', value: 'ai' }
      ]
    }
  },
  computed: {
    filteredCourses() {
      if (this.currentTab === 'all') {
        return this.courses
      }
      if (this.currentTab === 'learning') {
        return this.courses.filter((item) => item.completeStatus !== 'completed')
      }
      if (this.currentTab === 'completed') {
        return this.courses.filter((item) => item.completeStatus === 'completed')
      }
      return []
    },
    learningCount() {
      return this.courses.filter((item) => item.completeStatus !== 'completed').length
    },
    completedCount() {
      return this.courses.filter((item) => item.completeStatus === 'completed').length
    },
    totalMinutes() {
      const total = this.courses.reduce((sum, item) => sum + Number(item.durationSeconds || 0), 0)
      return Math.round(total / 60)
    }
  },
  onShow() {
    this.fetchCourses()
  },
  methods: {
    async fetchCourses() {
      this.pageLoading = true
      try {
        const list = await getMyCourses({ silent: true })
        this.courses = Array.isArray(list) ? list.map((item) => this.normalizeCourse(item)) : []
      } catch (error) {
        this.courses = []
      } finally {
        this.pageLoading = false
      }
    },

    normalizeCourse(item) {
      const progress = Math.max(0, Math.min(100, Number(item.progress || 0)))
      return Object.assign({}, item, {
        progressText: `${progress}%`
      })
    },

    switchTab(value) {
      if (value === 'ai') {
        uni.showToast({
          title: 'AI 课堂功能后期开启',
          icon: 'none'
        })
        return
      }
      this.currentTab = value
    },

    openDetail(item) {
      uni.navigateTo({
        url: `/pages-sub/course/detail?id=${item.courseId}`
      })
    },

    goCourseTab() {
      uni.switchTab({ url: '/pages/course/list' })
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.switchTab({ url: '/pages/my/index' })
    },

    getCourseTypeText(type) {
      return type === 'ai_maic' ? 'AI 课程' : '普通课程'
    },

    getCompleteText(status) {
      return status === 'completed' ? '已完成' : '学习中'
    },

    getDurationText(seconds) {
      const totalSeconds = Number(seconds || 0)
      if (!totalSeconds) {
        return '0 分钟'
      }
      const minutes = Math.max(1, Math.round(totalSeconds / 60))
      return `${minutes} 分钟`
    },

    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return `${value}`.slice(0, 16).replace('T', ' ')
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.my-course-page {
  min-height: 100vh;
  background: $jst-bg-page;
}

.jst-page-skeleton {
  margin: $jst-space-lg $jst-page-padding 0;
}

.my-course-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.my-course-page__back {
  width: 72rpx;
  flex-shrink: 0;
  font-size: 34rpx;
  color: $jst-text-secondary;
}

.my-course-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.my-course-page__tabs {
  display: flex;
  padding: $jst-space-sm $jst-space-sm 10rpx;
  background: $jst-bg-card;
}

.my-course-page__tab {
  flex: 1;
  height: 76rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 6rpx;
  border-radius: $jst-radius-round;
  color: $jst-text-secondary;
  font-size: $jst-font-sm;
}

.my-course-page__tab--active {
  color: $jst-brand;
  background: $jst-brand-light;
  font-weight: $jst-weight-semibold;
}

.my-course-page__summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $jst-space-sm;
  margin: $jst-space-lg $jst-page-padding 0;
  padding: $jst-space-lg 20rpx;
  border-radius: $jst-radius-lg;
  background: linear-gradient(135deg, $jst-brand-dark 0%, $jst-brand 100%);
}

.my-course-page__summary-item {
  text-align: center;
}

.my-course-page__summary-num {
  display: block;
  font-size: 34rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
}

.my-course-page__summary-label {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  color: rgba($jst-text-inverse, 0.78);
}

.my-course-page__list {
  padding: $jst-space-lg $jst-page-padding 28rpx;
}

.my-course-page__card {
  margin-bottom: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
  overflow: hidden;
}

.my-course-page__card-header {
  display: flex;
  gap: $jst-space-md;
  padding: $jst-space-lg;
}

.my-course-page__cover {
  width: 148rpx;
  height: 148rpx;
  border-radius: 22rpx;
  overflow: hidden;
  flex-shrink: 0;
  background: linear-gradient(135deg, $jst-brand 0%, $jst-brand-dark 100%);
}

.my-course-page__cover-image,
.my-course-page__cover-fallback {
  width: 100%;
  height: 100%;
}

.my-course-page__cover-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
}

.my-course-page__cover-fallback--completed {
  background: linear-gradient(135deg, $jst-success 0%, $jst-success-light 100%);
}

.my-course-page__cover-emoji {
  font-size: 46rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
}

.my-course-page__card-main {
  flex: 1;
  min-width: 0;
}

.my-course-page__card-title {
  display: block;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  line-height: 1.5;
  color: $jst-text-primary;
}

.my-course-page__card-meta {
  display: block;
  margin-top: 10rpx;
  font-size: $jst-font-xs;
  line-height: 1.6;
  color: $jst-text-secondary;
}

.my-course-page__progress {
  padding: 0 $jst-page-padding $jst-space-lg;
}

.my-course-page__progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.my-course-page__progress-label,
.my-course-page__progress-num,
.my-course-page__progress-foot {
  font-size: $jst-font-xs;
}

.my-course-page__progress-label,
.my-course-page__progress-foot {
  color: $jst-text-secondary;
}

.my-course-page__progress-num {
  font-weight: $jst-weight-semibold;
  color: $jst-brand;
}

.my-course-page__progress-bar {
  height: 12rpx;
  margin-top: 14rpx;
  border-radius: $jst-radius-round;
  background: $jst-border;
  overflow: hidden;
}

.my-course-page__progress-fill {
  height: 100%;
  border-radius: $jst-radius-round;
  background: linear-gradient(90deg, $jst-brand 0%, $jst-brand-dark 100%);
}

.my-course-page__progress-fill--done {
  background: linear-gradient(90deg, $jst-success 0%, $jst-success-light 100%);
}

.my-course-page__progress-foot {
  display: block;
  margin-top: $jst-space-xs;
  line-height: 1.6;
}

.my-course-page__actions {
  display: flex;
  gap: $jst-space-sm;
  padding: 0 $jst-page-padding $jst-space-lg;
}

.my-course-page__action {
  flex: 1;
  height: 76rpx;
  border-radius: $jst-radius-round;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
}

::v-deep .my-course-page__nav-action.u-button {
  min-width: 118rpx;
  height: 56rpx;
  border-color: $jst-brand;
  border-radius: $jst-radius-round;
  color: $jst-brand;
  font-size: $jst-font-xs;
}

::v-deep .my-course-page__action.u-button {
  min-height: 76rpx;
}

::v-deep .my-course-page__action--ghost.u-button {
  border-color: $jst-border;
  background: $jst-bg-card;
  color: $jst-brand;
}

::v-deep .my-course-page__action--primary.u-button {
  border: none;
  background: $jst-brand;
  color: $jst-text-inverse;
}
</style>
