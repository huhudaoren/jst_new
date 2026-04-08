<template>
  <view class="my-course-page">
    <jst-loading :loading="pageLoading" text="课程列表加载中..." />

    <view class="my-course-page__nav">
      <view class="my-course-page__back" @tap="handleBack"><</view>
      <text class="my-course-page__nav-title">我的课程</text>
      <view class="my-course-page__nav-action" @tap="goCourseTab">去选课</view>
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
          <button class="my-course-page__action my-course-page__action--ghost" @tap="openDetail(item)">
            查看详情
          </button>
          <button class="my-course-page__action my-course-page__action--primary" @tap="openDetail(item)">
            {{ item.completeStatus === 'completed' ? '重新学习' : '继续学习' }}
          </button>
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
.my-course-page {
  min-height: 100vh;
  background: #f4f7fc;
}

.my-course-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 22rpx rgba(14, 58, 113, 0.05);
}

.my-course-page__back {
  width: 72rpx;
  flex-shrink: 0;
  font-size: 34rpx;
  color: #66768f;
}

.my-course-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: #1f2937;
}

.my-course-page__nav-action {
  width: 100rpx;
  flex-shrink: 0;
  font-size: 22rpx;
  text-align: right;
  color: #2f7de1;
}

.my-course-page__tabs {
  display: flex;
  padding: 12rpx 12rpx 10rpx;
  background: #ffffff;
}

.my-course-page__tab {
  flex: 1;
  height: 76rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 6rpx;
  border-radius: 999rpx;
  color: #73829b;
  font-size: 24rpx;
}

.my-course-page__tab--active {
  color: #2f7de1;
  background: #eef4ff;
  font-weight: 700;
}

.my-course-page__summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12rpx;
  margin: 20rpx 24rpx 0;
  padding: 24rpx 20rpx;
  border-radius: 28rpx;
  background: linear-gradient(135deg, #0c3d6b 0%, #2f7de1 100%);
}

.my-course-page__summary-item {
  text-align: center;
}

.my-course-page__summary-num {
  display: block;
  font-size: 34rpx;
  font-weight: 800;
  color: #ffffff;
}

.my-course-page__summary-label {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.74);
}

.my-course-page__list {
  padding: 20rpx 24rpx 28rpx;
}

.my-course-page__card {
  margin-bottom: 20rpx;
  border-radius: 28rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 28rpx rgba(14, 58, 113, 0.06);
  overflow: hidden;
}

.my-course-page__card-header {
  display: flex;
  gap: 16rpx;
  padding: 24rpx;
}

.my-course-page__cover {
  width: 148rpx;
  height: 148rpx;
  border-radius: 22rpx;
  overflow: hidden;
  flex-shrink: 0;
  background: linear-gradient(135deg, #1565c0 0%, #42a5f5 100%);
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
  background: linear-gradient(135deg, #1ea96b 0%, #52d68a 100%);
}

.my-course-page__cover-emoji {
  font-size: 46rpx;
  font-weight: 800;
  color: #ffffff;
}

.my-course-page__card-main {
  flex: 1;
  min-width: 0;
}

.my-course-page__card-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.5;
  color: #1f2937;
}

.my-course-page__card-meta {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #7a869d;
}

.my-course-page__progress {
  padding: 0 24rpx 24rpx;
}

.my-course-page__progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.my-course-page__progress-label,
.my-course-page__progress-num,
.my-course-page__progress-foot {
  font-size: 22rpx;
}

.my-course-page__progress-label,
.my-course-page__progress-foot {
  color: #7a869d;
}

.my-course-page__progress-num {
  font-weight: 700;
  color: #2f7de1;
}

.my-course-page__progress-bar {
  height: 12rpx;
  margin-top: 14rpx;
  border-radius: 999rpx;
  background: #edf2fb;
  overflow: hidden;
}

.my-course-page__progress-fill {
  height: 100%;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #2f7de1 0%, #4a98ff 100%);
}

.my-course-page__progress-fill--done {
  background: linear-gradient(90deg, #1ea96b 0%, #52d68a 100%);
}

.my-course-page__progress-foot {
  display: block;
  margin-top: 10rpx;
  line-height: 1.6;
}

.my-course-page__actions {
  display: flex;
  gap: 12rpx;
  padding: 0 24rpx 24rpx;
}

.my-course-page__action {
  flex: 1;
  height: 76rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 700;
}

.my-course-page__action--ghost {
  border: 2rpx solid #d6e4fb;
  background: #ffffff;
  color: #2f7de1;
}

.my-course-page__action--primary {
  background: #2f7de1;
  color: #ffffff;
}
</style>
