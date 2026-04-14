<!-- 中文注释: 课程列表页；对照原型 小程序原型图/course-list.html；含多级筛选 -->
<template>
  <view class="course-list-page" :style="{ paddingTop: navPaddingTop }">
    <jst-loading :loading="pageLoading" text="课程加载中..." />

    <!-- 骨架屏 -->
    <view v-if="skeletonShow && !pageLoading" class="course-list-page__skeleton">
      <view class="course-list-page__skeleton-hero"></view>
      <view class="course-list-page__skeleton-segment">
        <u-skeleton rows="1" rows-width="100%" rows-height="76rpx" :loading="true" :animate="true"></u-skeleton>
      </view>
      <view v-for="i in 3" :key="i" class="course-list-page__skeleton-card">
        <u-skeleton rows="3" :loading="true" :animate="true"></u-skeleton>
      </view>
    </view>

    <template v-if="!skeletonShow">
      <view class="course-list-page__hero">
        <view class="course-list-page__hero-content">
          <text class="course-list-page__title">竞赛辅导课程</text>
          <text class="course-list-page__subtitle">名师讲解 · 精准备赛</text>
        </view>
        <!-- 搜索栏 -->
        <view class="course-list-page__search-wrap">
          <view class="course-list-page__search-box">
            <text class="course-list-page__search-icon">🔍</text>
            <input
              class="course-list-page__search-input"
              v-model="keyword"
              placeholder="搜索课程、讲师..."
              :placeholder-style="'color:rgba(255,255,255,0.55)'"
              confirm-type="search"
              @confirm="onSearch"
            />
            <view v-if="keyword" class="course-list-page__search-clear" @tap="onClearSearch">
              <text class="course-list-page__search-clear-icon">✕</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 类型 Tab -->
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

      <!-- 分类筛选胶囊横滑 -->
      <scroll-view class="course-list-page__filter-pills" scroll-x>
        <view class="course-list-page__pills-inner">
          <view
            class="course-list-page__pill"
            :class="{ 'course-list-page__pill--active': !filterValue.category }"
            @tap="onCategoryTap('')"
          >
            <text>全部</text>
          </view>
          <view
            v-for="cat in courseCategories"
            :key="cat.value"
            class="course-list-page__pill"
            :class="{ 'course-list-page__pill--active': filterValue.category === cat.value }"
            @tap="onCategoryTap(cat.value)"
          >
            <text>{{ cat.label }}</text>
          </view>
        </view>
      </scroll-view>

      <!-- 排序筛选栏 -->
      <jst-filter-bar
        :filters="filterConfig"
        :value="filterValue"
        @change="onFilterChange"
      />

      <view v-if="courses.length" class="course-list-page__list">
        <view v-for="(item, cIdx) in courses" :key="item.courseId" class="jst-anim-scale-in" :style="{ animationDelay: getStaggerDelay(cIdx) }">
          <jst-course-card :course="item" class="course-list-page__card" @item-tap="navigateDetail" />
        </view>

        <u-loadmore
          :status="loadMoreState"
          :loading-text="'加载更多中...'"
          :loadmore-text="'上拉加载更多'"
          :nomore-text="'没有更多课程了'"
        />
      </view>

      <jst-empty v-else-if="!pageLoading" :text="getEmptyText()" icon="📚" />
    </template>
  </view>
</template>

<script>
import { getCourseList } from '@/api/course'
import { getCourseCategories } from '@/api/dict'
import JstCourseCard from '@/components/jst-course-card/jst-course-card.vue'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import JstFilterBar from '@/components/jst-filter-bar/jst-filter-bar.vue'
import { staggerDelay } from '@/utils/visual-effects'
export default {
  components: {
    JstCourseCard,
    JstEmpty,
    JstLoading,
    JstFilterBar
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
      initialized: false,
      skeletonShow: true,
      keyword: '',
      courseCategories: [],
      // 筛选状态
      filterValue: {
        category: '',
        sortBy: 'default'
      },
      // 筛选配置（仅排序，分类由胶囊横滑承载）
      filterConfig: [
        {
          key: 'sortBy',
          label: '排序',
          type: 'radio',
          options: [
            { label: '最新上架', value: 'newest' },
            { label: '最热门', value: 'hot' }
          ]
        }
      ]
    }
  },
  computed: {
    loadMoreState() {
      if (this.loadingMore) return 'loading'
      if (!this.courses.length) return 'loadmore'
      if (this.total > 0 && this.courses.length >= this.total) return 'nomore'
      return 'loadmore'
    }
  },
  onLoad() {
    this.initPage()
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
    async initPage() {
      this.pageLoading = true
      await Promise.allSettled([this.fetchCategories(), this.loadCourses({ reset: true })])
      this.pageLoading = false
      this.skeletonShow = false
    },

    async fetchCategories() {
      try {
        const res = await getCourseCategories({ silent: true })
        const list = Array.isArray(res) ? res : (res && res.data ? res.data : [])
        const seen = new Set()
        this.courseCategories = list.filter(item => {
          if (!item || !item.value) return false
          if (seen.has(item.value)) return false
          seen.add(item.value)
          return true
        })
      } catch (error) {
        this.courseCategories = []
      }
    },

    // 中文注释: 分类胶囊点击
    onCategoryTap(val) {
      if (this.filterValue.category === val) return
      this.filterValue = { ...this.filterValue, category: val }
      this.loadCourses({ reset: true })
    },

    // 中文注释: 筛选栏变化
    onFilterChange(newValue) {
      this.filterValue = { ...this.filterValue, ...newValue }
      this.loadCourses({ reset: true })
    },

    async loadCourses({ reset }) {
      if (this.pageLoading && !reset) return
      if (this.loadingMore) return

      if (reset) {
        this.pageLoading = true
        this.pageNum = 1
      } else {
        this.loadingMore = true
      }

      try {
        const params = {
          courseType: this.currentType,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
        if (this.keyword) params.keyword = this.keyword
        if (this.filterValue.category) params.category = this.filterValue.category
        if (this.filterValue.sortBy && this.filterValue.sortBy !== 'default') params.sortBy = this.filterValue.sortBy

        const response = await getCourseList(params, { silent: true })
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
        this.skeletonShow = false
      }
    },

    handleTypeChange(type) {
      if (this.currentType === type) return
      this.currentType = type
      this.loadCourses({ reset: true })
    },

    loadMore() {
      if (this.pageLoading || this.loadingMore || this.loadError) return
      if (this.total > 0 && this.courses.length >= this.total) return
      this.pageNum += 1
      this.loadCourses({ reset: false })
    },

    navigateDetail(course) {
      if (!course || !course.courseId) return
      uni.navigateTo({ url: `/pages-sub/course/detail?id=${course.courseId}` })
    },

    onSearch() {
      this.loadCourses({ reset: true })
    },

    onClearSearch() {
      this.keyword = ''
      this.loadCourses({ reset: true })
    },

    getEmptyText() {
      if (this.loadError) return '课程接口暂未就绪'
      return this.currentType === 'ai_maic' ? '暂无 AI 课程' : '暂无普通课程'
    },

    getStaggerDelay(index) {
      return 'animation-delay:' + staggerDelay(index)
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.course-list-page {
  min-height: 100vh;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
  background: $jst-bg-subtle;
}

// 骨架屏
.course-list-page__skeleton-hero {
  height: 280rpx;
  background: $jst-brand-gradient;
  border-bottom-left-radius: $jst-radius-xl;
  border-bottom-right-radius: $jst-radius-xl;
}
.course-list-page__skeleton-segment {
  margin: -72rpx $jst-space-xl 0;
  padding: 10rpx;
  border-radius: $jst-radius-round;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}
.course-list-page__skeleton-card {
  margin: $jst-space-lg $jst-space-xl 0;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
}

.course-list-page__hero {
  padding: 88rpx $jst-space-xl 60rpx;
  background: $jst-hero-gradient;
  border-bottom-left-radius: $jst-radius-xl;
  border-bottom-right-radius: $jst-radius-xl;
  position: relative;
}

.course-list-page__hero-content {
  position: relative;
  z-index: 1;
}

.course-list-page__title {
  display: block;
  font-size: 52rpx;
  font-weight: $jst-weight-semibold;
  color: $jst-text-inverse;
}

.course-list-page__subtitle {
  display: block;
  margin-top: 18rpx;
  font-size: $jst-font-sm;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.76);
}

/* 搜索栏（在 hero 内） */
.course-list-page__search-wrap {
  margin-top: $jst-space-lg;
  position: relative;
  z-index: 1;
}

.course-list-page__search-box {
  display: flex;
  align-items: center;
  height: 72rpx;
  padding: 0 $jst-space-lg;
  border-radius: $jst-radius-round;
  background: rgba(255, 255, 255, 0.15);
  border: 2rpx solid rgba(255, 255, 255, 0.25);
  gap: $jst-space-sm;
}

.course-list-page__search-icon {
  font-size: $jst-font-base;
  flex-shrink: 0;
  opacity: 0.6;
}

.course-list-page__search-input {
  flex: 1;
  font-size: 32rpx;
  color: $jst-text-inverse;
  background: transparent;
}

.course-list-page__search-clear {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  flex-shrink: 0;
}

.course-list-page__search-clear-icon {
  font-size: $jst-font-xs;
  color: $jst-text-inverse;
}

.course-list-page__segment {
  display: flex;
  margin: -72rpx $jst-space-xl 0;
  padding: 10rpx;
  border-radius: $jst-radius-round;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.course-list-page__segment-item {
  flex: 1;
  height: 76rpx;
  border-radius: $jst-radius-round;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  line-height: 76rpx;
  text-align: center;
  color: $jst-text-secondary;
  transition: background $jst-duration-normal $jst-easing, color $jst-duration-normal $jst-easing;
}

.course-list-page__segment-item--active {
  background: $jst-brand-dark;
  color: $jst-text-inverse;
}

/* 分类筛选胶囊横滑 */
.course-list-page__filter-pills {
  white-space: nowrap;
  margin-top: $jst-space-lg;
  border-bottom: 2rpx solid $jst-border;
  background: $jst-bg-card;
}

.course-list-page__pills-inner {
  display: inline-flex;
  gap: $jst-space-sm;
  padding: $jst-space-md $jst-space-lg;
}

.course-list-page__pill {
  display: inline-flex;
  align-items: center;
  padding: 10rpx 24rpx;
  border-radius: $jst-radius-round;
  border: 2rpx solid $jst-border;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-medium;
  color: $jst-text-regular;
  flex-shrink: 0;
  transition: all $jst-duration-fast;

  &:active { transform: scale(0.95); }

  &--active {
    background: $jst-brand-light;
    border-color: $jst-brand;
    color: $jst-brand;
    font-weight: $jst-weight-semibold;
  }
}

.course-list-page__list {
  padding: $jst-space-lg $jst-space-xl 0;
}

.course-list-page__card + .course-list-page__card {
  margin-top: $jst-space-lg;
}
</style>
