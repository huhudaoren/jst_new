<!-- 中文注释: 赛事列表页；对照原型 小程序原型图/contest-list.html；含多级筛选 -->
<template>
  <view class="contest-list-page" :style="{ paddingTop: navPaddingTop }">
    <!-- 骨架屏 -->
    <view v-if="skeletonShow" class="contest-list-page__skeleton">
      <view class="contest-list-page__skeleton-search">
        <u-skeleton rows="1" rows-width="100%" rows-height="88rpx" :loading="true" :animate="true"></u-skeleton>
      </view>
      <view class="contest-list-page__skeleton-tabs">
        <u-skeleton rows="1" rows-width="100%" rows-height="40rpx" :loading="true" :animate="true"></u-skeleton>
      </view>
      <view v-for="i in 5" :key="i" class="contest-list-page__skeleton-card">
        <u-skeleton rows="3" :loading="true" :animate="true"></u-skeleton>
      </view>
    </view>

    <template v-else>
      <view class="contest-list-page__sticky">
        <!-- 搜索栏 -->
        <view class="contest-list-page__search-wrap">
          <view class="contest-list-page__search-box">
            <text class="contest-list-page__search-icon">🔍</text>
            <input
              class="contest-list-page__search-input"
              v-model="keyword"
              placeholder="搜索赛事名称、学科..."
              :placeholder-style="'color:' + '#B0B8CC'"
              confirm-type="search"
              @confirm="onSearch"
            />
            <view v-if="keyword" class="contest-list-page__search-clear" @tap="onClearSearch">
              <text class="contest-list-page__search-clear-icon">✕</text>
            </view>
          </view>
        </view>

        <!-- 分类 Tab 横滑 -->
        <scroll-view class="contest-list-page__cat-tabs" scroll-x>
          <view class="contest-list-page__cat-tabs-inner">
            <view
              class="contest-list-page__cat-tab"
              :class="{ 'contest-list-page__cat-tab--active': !filterValue.category }"
              @tap="onCategoryTap('')"
            >
              <text>全部</text>
            </view>
            <view
              v-for="cat in categories"
              :key="cat.value"
              class="contest-list-page__cat-tab"
              :class="{ 'contest-list-page__cat-tab--active': filterValue.category === cat.value }"
              @tap="onCategoryTap(cat.value)"
            >
              <text>{{ cat.label }}</text>
            </view>
          </view>
        </scroll-view>

        <!-- 筛选按钮行 -->
        <jst-filter-bar
          :filters="filterConfig"
          :value="filterValue"
          @change="onFilterChange"
        />
      </view>

      <!-- 结果栏 -->
      <view class="contest-list-page__result-bar">
        <text class="contest-list-page__result-text">共找到 <text class="contest-list-page__result-count">{{ total }}</text> 个赛事</text>
      </view>

      <jst-loading :loading="pageLoading && !contestList.length" text="赛事加载中..." />

      <view v-if="contestList.length" class="contest-list-page__list">
        <view v-for="(item, cIdx) in contestList" :key="item.contestId" class="jst-anim-slide-up" :style="{ animationDelay: getStaggerDelay(cIdx) }" @tap="openContestDetail(item)">
          <jst-contest-card :contest="item" />
        </view>
      </view>

      <jst-empty v-else-if="!pageLoading && !listLoading" icon="🏆" text="暂无符合条件的赛事" />

      <u-loadmore
        v-if="contestList.length"
        :status="loadMoreStatus"
        :loading-text="'赛事加载中...'"
        :loadmore-text="'上拉加载更多'"
        :nomore-text="'没有更多赛事了'"
      />
    </template>
  </view>
</template>

<script>
import { getContestList } from '@/api/contest'
import { getContestCategories } from '@/api/dict'
import JstContestCard from '@/components/jst-contest-card/jst-contest-card.vue'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import JstFilterBar from '@/components/jst-filter-bar/jst-filter-bar.vue'
import { normalizeContestCard } from '@/utils/contest'
import { staggerDelay } from '@/utils/visual-effects'
export default {
  components: {
    JstContestCard,
    JstEmpty,
    JstLoading,
    JstFilterBar
  },
  data() {
    return {
      pageLoading: false,
      listLoading: false,
      categories: [],
      contestList: [],
      total: 0,
      hasMore: true,
      keyword: '',
      query: {
        pageNum: 1,
        pageSize: 10
      },
      loadMoreStatus: 'more',
      skeletonShow: true,
      // 筛选状态
      filterValue: {
        category: '',
        sortBy: 'default',
        eventType: ''
      },
      // 筛选按钮配置（分类由 Tab 横滑承载，这里配排序 + 筛选）
      filterConfig: [
        {
          key: 'sortBy',
          label: '排序',
          type: 'radio',
          options: [
            { label: '最新上架', value: 'newest' },
            { label: '热门推荐', value: 'hot' },
            { label: '报名截止临近', value: 'deadline' }
          ]
        },
        {
          key: 'more',
          label: '筛选',
          type: 'multi',
          groups: [
            {
              title: '赛事形式',
              key: 'eventType',
              options: [
                { label: '线上', value: 'online' },
                { label: '线下', value: 'offline' },
                { label: '混合', value: 'mixed' }
              ]
            }
          ]
        }
      ]
    }
  },
  onLoad(query) {
    if (query && query.keyword) {
      this.keyword = decodeURIComponent(query.keyword)
    }
    this.initializePage()
  },
  onPullDownRefresh() {
    this.initializePage()
  },
  onReachBottom() {
    this.loadNextPage()
  },
  methods: {
    async initializePage() {
      this.pageLoading = true
      await Promise.allSettled([this.fetchCategories(), this.resetAndLoadList()])
      this.pageLoading = false
      this.skeletonShow = false
      uni.stopPullDownRefresh()
    },

    async fetchCategories() {
      try {
        const res = await getContestCategories({ silent: true })
        const list = Array.isArray(res) ? res : (res && res.data ? res.data : [])
        // 中文注释: 去重保留首条
        const seen = new Set()
        this.categories = list.filter(item => {
          if (!item || !item.value) return false
          if (seen.has(item.value)) return false
          seen.add(item.value)
          return true
        })
      } catch (error) {
        this.categories = []
      }
    },

    // 中文注释: 分类 Tab 点击
    onCategoryTap(val) {
      if (this.filterValue.category === val) return
      this.filterValue = { ...this.filterValue, category: val }
      this.resetAndLoadList()
    },

    // 中文注释: 筛选栏变化回调
    onFilterChange(newValue) {
      this.filterValue = { ...this.filterValue, ...newValue }
      this.resetAndLoadList()
    },

    async resetAndLoadList() {
      this.query.pageNum = 1
      this.total = 0
      this.hasMore = true
      this.contestList = []
      this.loadMoreStatus = 'loading'
      await this.fetchContestList(true)
    },

    async loadNextPage() {
      if (!this.hasMore || this.listLoading) return
      this.query.pageNum += 1
      this.loadMoreStatus = 'loading'
      await this.fetchContestList(false)
    },

    async fetchContestList(reset) {
      this.listLoading = true
      try {
        const params = {
          pageNum: this.query.pageNum,
          pageSize: this.query.pageSize
        }
        if (this.keyword) params.keyword = this.keyword
        if (this.filterValue.category) params.category = this.filterValue.category
        if (this.filterValue.sortBy && this.filterValue.sortBy !== 'default') params.sortBy = this.filterValue.sortBy
        if (this.filterValue.eventType) params.eventType = this.filterValue.eventType

        const response = await getContestList(params, { silent: true })
        const rows = Array.isArray(response.rows) ? response.rows : []
        const normalizedRows = rows.map(item => normalizeContestCard(item))
        this.contestList = reset ? normalizedRows : this.contestList.concat(normalizedRows)
        this.total = Number(response.total || this.contestList.length || 0)
        this.hasMore = this.contestList.length < this.total && rows.length >= this.query.pageSize
        this.loadMoreStatus = this.hasMore ? 'more' : 'noMore'
      } catch (error) {
        if (!reset) {
          this.query.pageNum = Math.max(1, this.query.pageNum - 1)
        }
        this.contestList = reset ? [] : this.contestList
        this.total = reset ? 0 : this.total
        this.hasMore = false
        this.loadMoreStatus = 'noMore'
      } finally {
        this.listLoading = false
      }
    },

    openContestDetail(contest) {
      if (!contest || !contest.contestId) return
      uni.navigateTo({ url: `/pages-sub/contest/detail?id=${contest.contestId}` })
    },

    onSearch() {
      this.resetAndLoadList()
    },

    onClearSearch() {
      this.keyword = ''
      this.resetAndLoadList()
    },

    getStaggerDelay(index) {
      return 'animation-delay:' + staggerDelay(index)
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.contest-list-page {
  min-height: 100vh;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  background: $jst-bg-page;
}

// 骨架屏
.contest-list-page__skeleton {
  padding: $jst-space-lg;
}
.contest-list-page__skeleton-search {
  margin-bottom: $jst-space-md;
}
.contest-list-page__skeleton-tabs {
  margin-bottom: $jst-space-lg;
}
.contest-list-page__skeleton-card {
  margin-bottom: $jst-space-md;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
}

.contest-list-page__sticky {
  position: sticky;
  top: 0;
  z-index: 20;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

/* 搜索栏 */
.contest-list-page__search-wrap {
  padding: $jst-space-lg $jst-space-lg $jst-space-sm;
}

.contest-list-page__search-box {
  display: flex;
  align-items: center;
  height: 72rpx;
  padding: 0 $jst-space-lg;
  border-radius: $jst-radius-round;
  background: $jst-bg-grey;
  gap: $jst-space-sm;
}

.contest-list-page__search-icon {
  font-size: $jst-font-base;
  flex-shrink: 0;
}

.contest-list-page__search-input {
  flex: 1;
  font-size: 32rpx;
  color: $jst-text-primary;
  background: transparent;
}

.contest-list-page__search-clear {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: $jst-text-placeholder;
  flex-shrink: 0;
}

.contest-list-page__search-clear-icon {
  font-size: $jst-font-xs;
  color: $jst-text-inverse;
}

/* 分类 Tab 横滑 */
.contest-list-page__cat-tabs {
  white-space: nowrap;
  border-top: 2rpx solid $jst-border;
}

.contest-list-page__cat-tabs-inner {
  display: inline-flex;
  padding: 0 $jst-space-lg;
}

.contest-list-page__cat-tab {
  display: inline-flex;
  align-items: center;
  padding: $jst-space-lg 28rpx;
  font-size: $jst-font-base;
  font-weight: $jst-weight-medium;
  color: $jst-text-secondary;
  position: relative;
  flex-shrink: 0;
  transition: color $jst-duration-fast;
}

.contest-list-page__cat-tab--active {
  color: $jst-brand;
  font-weight: $jst-weight-bold;

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 28rpx;
    right: 28rpx;
    height: 4rpx;
    background: $jst-brand;
    border-radius: 2rpx;
  }
}

/* 结果栏 */
.contest-list-page__result-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $jst-space-lg $jst-space-lg $jst-space-xs;
}

.contest-list-page__result-text {
  color: $jst-text-secondary;
  font-size: $jst-font-sm;
}

.contest-list-page__result-count {
  color: $jst-brand;
  font-weight: $jst-weight-semibold;
}

/* 列表 */
.contest-list-page__list {
  padding: $jst-space-xs $jst-space-lg 0;
}

.contest-list-page__list > * + * {
  margin-top: $jst-space-lg;
}
</style>
