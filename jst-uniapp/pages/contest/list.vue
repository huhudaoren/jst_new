<template>
  <view class="contest-list-page">
    <view class="contest-list-page__sticky">
      <view class="contest-list-page__search">
        <text class="contest-list-page__search-icon">🔍</text>
        <input
          class="contest-list-page__search-input"
          v-model="keyword"
          placeholder="搜索赛事名称、学科..."
          placeholder-class="contest-list-page__search-placeholder"
          confirm-type="search"
          @confirm="onSearch"
          @input="onSearchInput"
        />
        <text v-if="keyword" class="contest-list-page__search-clear" @tap="onClearSearch">✕</text>
      </view>

      <scroll-view class="contest-list-page__tabs" scroll-x>
        <view
          class="contest-list-page__tab"
          :class="{ 'contest-list-page__tab--active': currentCategory === '' }"
          @tap="handleCategoryChange('')"
        >
          全部
        </view>
        <view
          v-for="(item, idx) in categories"
          :key="(item.code || 'cat') + '-' + idx"
          class="contest-list-page__tab"
          :class="{ 'contest-list-page__tab--active': currentCategory === item.code }"
          @tap="handleCategoryChange(item.code)"
        >
          {{ item.name }}
          <text v-if="item.count || item.count === 0" class="contest-list-page__tab-count">{{ item.count }}</text>
        </view>
      </scroll-view>

      <view v-if="categoriesLoading && !categories.length" class="contest-list-page__tabs-tip">分类加载中...</view>
    </view>

    <view class="contest-list-page__result-bar">
      <text class="contest-list-page__result-text">共找到 {{ total }} 个赛事</text>
      <text class="contest-list-page__result-sort">综合排序</text>
    </view>

    <jst-loading :loading="pageLoading && !contestList.length" text="赛事加载中..." />

    <view v-if="contestList.length" class="contest-list-page__list">
      <!-- 外层 view 捕获 tap, 避免 uni-app 下自定义组件 @tap 事件透传不一致 -->
      <view v-for="item in contestList" :key="item.contestId" @tap="openContestDetail(item)">
        <jst-contest-card :contest="item" />
      </view>
    </view>

    <jst-empty v-else-if="!pageLoading && !listLoading" icon="🏆" text="赛事数据准备中，待后端联调完成后自动展示。" />

    <view v-if="contestList.length" class="contest-list-page__load-more">
      <uni-load-more :status="loadMoreStatus" :content-text="loadMoreText" />
    </view>
  </view>
</template>

<script>
import { getContestCategories, getContestList } from '@/api/contest'
import JstContestCard from '@/components/jst-contest-card/jst-contest-card.vue'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import { normalizeContestCard } from '@/utils/contest'

export default {
  components: {
    JstContestCard,
    JstEmpty,
    JstLoading
  },
  data() {
    return {
      pageLoading: false,
      listLoading: false,
      categoriesLoading: false,
      categories: [],
      currentCategory: '',
      contestList: [],
      total: 0,
      hasMore: true,
      keyword: '',
      query: {
        pageNum: 1,
        pageSize: 10
      },
      loadMoreStatus: 'more',
      loadMoreText: {
        contentdown: '上拉加载更多',
        contentrefresh: '赛事加载中...',
        contentnomore: '没有更多赛事了'
      }
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
      uni.stopPullDownRefresh()
    },

    async fetchCategories() {
      this.categoriesLoading = true
      try {
        const list = await getContestCategories({ silent: true })
        // 后端可能返回重复 code, 按 code 去重保留首条
        const seen = new Set()
        const deduped = []
        ;(Array.isArray(list) ? list : []).forEach((item) => {
          const code = item && item.code
          if (code != null && seen.has(code)) return
          if (code != null) seen.add(code)
          deduped.push(item)
        })
        this.categories = deduped
      } catch (error) {
        this.categories = []
      } finally {
        this.categoriesLoading = false
      }
    },

    async handleCategoryChange(code) {
      if (this.currentCategory === code || this.listLoading) {
        return
      }
      this.currentCategory = code
      await this.resetAndLoadList()
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
      if (!this.hasMore || this.listLoading) {
        return
      }
      this.query.pageNum += 1
      this.loadMoreStatus = 'loading'
      await this.fetchContestList(false)
    },

    async fetchContestList(reset) {
      this.listLoading = true
      try {
        const response = await getContestList(
          {
            pageNum: this.query.pageNum,
            pageSize: this.query.pageSize,
            category: this.currentCategory || undefined,
            keyword: this.keyword || undefined
          },
          { silent: true }
        )
        const rows = Array.isArray(response.rows) ? response.rows : []
        const normalizedRows = rows.map((item) => normalizeContestCard(item))
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
      if (!contest || !contest.contestId) {
        return
      }
      uni.navigateTo({ url: `/pages-sub/contest/detail?id=${contest.contestId}` })
    },

    onSearch(e) {
      this.keyword = (e && e.detail && e.detail.value) || this.keyword
      this.resetAndLoadList()
    },
    onSearchInput(e) {
      this.keyword = (e && e.detail && e.detail.value) || ''
    },
    onClearSearch() {
      this.keyword = ''
      this.resetAndLoadList()
    }
  }
}
</script>

<style scoped lang="scss">
.contest-list-page {
  min-height: 100vh;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  background: #F7F8FA;
}

.contest-list-page__sticky {
  position: sticky;
  top: 0;
  z-index: 20;
  padding: 24rpx 0 0;
  background: var(--jst-color-card-bg);
  box-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04);
}

.contest-list-page__search {
  display: flex;
  align-items: center;
  height: 88rpx;
  margin: 0 24rpx;
  padding: 0 28rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-page-bg);
}

.contest-list-page__search-icon {
  font-size: 28rpx;
}

.contest-list-page__search-input {
  flex: 1;
  margin-left: 16rpx;
  font-size: 26rpx;
  color: var(--jst-color-text);
}
.contest-list-page__search-placeholder {
  color: var(--jst-color-text-tertiary);
}
.contest-list-page__search-clear {
  margin-left: 12rpx;
  font-size: 24rpx;
  color: var(--jst-color-text-tertiary);
  padding: 8rpx;
}

.contest-list-page__tabs {
  margin-top: 18rpx;
  padding: 0 24rpx 12rpx;
  white-space: nowrap;
}

.contest-list-page__tab {
  display: inline-flex;
  align-items: center;
  margin-right: 16rpx;
  padding: 12rpx 24rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-page-bg);
  color: var(--jst-color-text-secondary);
  font-size: 24rpx;
}

.contest-list-page__tab--active {
  background: var(--jst-color-brand-soft);
  color: var(--jst-color-brand);
  font-weight: 600;
}

.contest-list-page__tab-count {
  margin-left: 10rpx;
  font-size: 20rpx;
}

.contest-list-page__tabs-tip {
  padding: 0 24rpx 16rpx;
  color: var(--jst-color-text-tertiary);
  font-size: 22rpx;
}

.contest-list-page__result-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18rpx 24rpx;
}

.contest-list-page__result-text,
.contest-list-page__result-sort {
  color: var(--jst-color-text-tertiary);
  font-size: 22rpx;
}

.contest-list-page__list {
  padding: 8rpx 24rpx 0;
}
.contest-list-page__list > * + * {
  margin-top: 16rpx;
}

.contest-list-page__load-more {
  padding: 24rpx 0 32rpx;
}
</style>