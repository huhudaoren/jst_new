<template>
  <view class="course-list-page">
    <jst-loading :loading="pageLoading" text="课程加载中..." />

    <view class="course-list-page__hero">
      <view class="course-list-page__hero-content">
        <text class="course-list-page__title">竞赛课程</text>
        <text class="course-list-page__subtitle">按课程类型浏览，后端 F-COURSE 完成后自动展示真实数据</text>
      </view>
    </view>

    <view class="course-list-page__segment">
      <view
        v-for="item in typeTabs"
        :key="item.value"
        class="course-list-page__segment-item"
        :class="{ 'course-list-page__segment-item--active': currentType === item.value }"
        @tap="handleTypeChange(item.value)"
      >
        {{ item.label }}
      </view>
    </view>

    <view v-if="courses.length" class="course-list-page__list">
      <jst-course-card v-for="item in courses" :key="item.courseId" :course="item" class="course-list-page__card" @tap="navigateDetail" />

      <view class="course-list-page__more">
        <text class="course-list-page__more-text">{{ getLoadMoreText() }}</text>
      </view>
    </view>

    <jst-empty v-else :text="getEmptyText()" icon="📚" />
  </view>
</template>

<script>
import { getCourseList } from '@/api/course'
import JstCourseCard from '@/components/jst-course-card/jst-course-card.vue'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: {
    JstCourseCard,
    JstEmpty,
    JstLoading
  },
  data() {
    return {
      typeTabs: [
        { label: '普通课程', value: 'normal' },
        { label: 'AI 课程', value: 'ai_maic' }
      ],
      currentType: 'normal',
      courses: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      pageLoading: false,
      loadingMore: false,
      loadError: false,
      initialized: false
    }
  },
  onLoad() {
    this.loadCourses({ reset: true })
  },
  onShow() {
    if (this.initialized && !this.pageLoading) {
      this.loadCourses({ reset: true })
    }
  },
  onReachBottom() {
    this.loadMore()
  },
  methods: {
    async loadCourses({ reset }) {
      if (this.pageLoading || this.loadingMore) {
        return
      }

      if (reset) {
        this.pageLoading = true
        this.pageNum = 1
      } else {
        this.loadingMore = true
      }

      try {
        const response = await getCourseList(
          { courseType: this.currentType, pageNum: this.pageNum, pageSize: this.pageSize },
          { silent: true }
        )
        const rows = Array.isArray(response.rows) ? response.rows : []
        this.total = Number(response.total || 0)
        this.courses = reset ? rows : this.courses.concat(rows)
        this.loadError = false
      } catch (error) {
        if (reset) {
          this.courses = []
          this.total = 0
        }
        this.loadError = true
      } finally {
        this.initialized = true
        this.pageLoading = false
        this.loadingMore = false
      }
    },

    handleTypeChange(type) {
      if (this.currentType === type) {
        return
      }
      this.currentType = type
      this.loadCourses({ reset: true })
    },

    loadMore() {
      if (this.pageLoading || this.loadingMore || this.loadError) {
        return
      }
      if (this.total > 0 && this.courses.length >= this.total) {
        return
      }
      this.pageNum += 1
      this.loadCourses({ reset: false })
    },

    navigateDetail(course) {
      if (!course || !course.courseId) {
        return
      }
      uni.navigateTo({ url: `/pages-sub/course/detail?id=${course.courseId}` })
    },

    getEmptyText() {
      if (this.loadError) {
        return '课程接口暂未就绪，待后端联调完成后展示'
      }
      return this.currentType === 'ai_maic' ? '暂无 AI 课程' : '暂无普通课程'
    },

    getLoadMoreText() {
      if (this.loadingMore) {
        return '加载更多中...'
      }
      if (!this.courses.length) {
        return ''
      }
      if (this.total > 0 && this.courses.length >= this.total) {
        return '没有更多课程了'
      }
      return '上拉加载更多'
    }
  }
}
</script>

<style scoped lang="scss">
.course-list-page {
  min-height: 100vh;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
  background: var(--jst-color-page-bg);
}

.course-list-page__hero {
  padding: 88rpx 32rpx 140rpx;
  background: linear-gradient(135deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%);
  border-bottom-left-radius: 48rpx;
  border-bottom-right-radius: 48rpx;
}

.course-list-page__hero-content {
  position: relative;
  z-index: 1;
}

.course-list-page__title {
  display: block;
  font-size: 52rpx;
  font-weight: 700;
  color: var(--jst-color-card-bg);
}

.course-list-page__subtitle {
  display: block;
  margin-top: 18rpx;
  font-size: 24rpx;
  line-height: 1.8;
  color: var(--jst-color-white-76);
}

.course-list-page__segment {
  display: flex;
  margin: -72rpx 32rpx 0;
  padding: 10rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.course-list-page__segment-item {
  flex: 1;
  height: 76rpx;
  border-radius: var(--jst-radius-full);
  font-size: 26rpx;
  font-weight: 600;
  line-height: 76rpx;
  text-align: center;
  color: var(--jst-color-text-secondary);
}

.course-list-page__segment-item--active {
  background: var(--jst-color-brand-soft);
  color: var(--jst-color-brand);
}

.course-list-page__list {
  padding: 24rpx 32rpx 0;
}

.course-list-page__card + .course-list-page__card {
  margin-top: 24rpx;
}

.course-list-page__more {
  padding: 32rpx 0 24rpx;
  text-align: center;
}

.course-list-page__more-text {
  font-size: 24rpx;
  color: var(--jst-color-text-tertiary);
}
</style>