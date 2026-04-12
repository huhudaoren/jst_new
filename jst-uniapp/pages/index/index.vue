<!-- 中文注释: 首页；对应原型 小程序原型图/index.html；对应接口 GET /jst/wx/index/banner、GET /jst/wx/notice/list、GET /jst/wx/contest/hot、GET /jst/wx/home/* -->
<template>

  <view class="home-page">
    <!-- 骨架屏 -->
    <view v-if="skeletonShow" class="home-page__skeleton">
      <view class="home-page__skeleton-hero"></view>
      <view class="home-page__skeleton-body">
        <view class="home-page__skeleton-banner">
          <u-skeleton rows="1" rows-width="100%" rows-height="280rpx" :loading="true" :animate="true"></u-skeleton>
        </view>
        <view class="home-page__skeleton-strip">
          <u-skeleton rows="1" rows-width="100%" rows-height="56rpx" :loading="true" :animate="true"></u-skeleton>
        </view>
        <view class="home-page__skeleton-grid">
          <u-skeleton rows="2" rows-width="100%" rows-height="72rpx" :loading="true" :animate="true"></u-skeleton>
        </view>
        <view v-for="i in 3" :key="i" class="home-page__skeleton-card">
          <u-skeleton rows="3" :loading="true" :animate="true"></u-skeleton>
        </view>
      </view>
    </view>

    <template v-else>
      <!-- 1. 顶部品牌 + 搜索 -->
      <view class="home-page__hero" :style="{ paddingTop: navPaddingTop }">
        <view class="home-page__hero-bg home-page__hero-bg--top"></view>
        <view class="home-page__hero-bg home-page__hero-bg--bottom"></view>
        <view class="home-page__hero-main">
          <view class="home-page__brand-row">
            <view class="home-page__brand">
              <view class="home-page__brand-icon">🏆</view>
              <view class="home-page__brand-copy">
                <text class="home-page__brand-title">竞赛通</text>
                <text class="home-page__brand-subtitle">发现好赛事 · 连接好内容</text>
              </view>
            </view>

            <view class="home-page__brand-action" @tap="goNoticeTab">公告</view>
          </view>

          <view class="home-page__search" @tap="handleSearch">
            <text class="home-page__search-icon">🔍</text>
            <text class="home-page__search-text">搜索赛事、课程、公告</text>
          </view>
        </view>
      </view>

      <view class="home-page__body">
        <!-- 2. Banner 轮播 -->
        <jst-banner-swiper
          v-if="bannerList.length"
          class="home-page__banner"
          :list="bannerList"
          @item-tap="handleBannerTap"
        />

        <!-- 公告条 -->
        <view class="home-page__notice-strip">
          <view class="home-page__notice-strip-icon">📢</view>
          <view class="home-page__notice-strip-body">
            <swiper
              v-if="noticeTicker.length"
              class="home-page__notice-swiper"
              autoplay
              circular
              vertical
              interval="3200"
              duration="400"
            >
              <swiper-item
                v-for="item in noticeTicker"
                :key="item.noticeId"
              >
                <view class="home-page__notice-item" @tap="openNotice(item)">
                  <text class="home-page__notice-title">{{ item.title }}</text>
                  <text class="home-page__notice-time">{{ formatShortDate(item.publishTime) }}</text>
                </view>
              </swiper-item>
            </swiper>

            <view v-else class="home-page__notice-empty">
              <text class="home-page__notice-empty-text">公告加载中或暂未发布</text>
            </view>
          </view>

          <view class="home-page__notice-more jst-more-link" @tap="goNoticeTab">
            <text>查看更多</text>
            <u-icon name="arrow-right" size="24rpx" color="#8A8A8A" />
          </view>
        </view>

        <!-- 3. 金刚区 -->
        <view class="home-page__entry-grid">
          <view
            v-for="entry in entryList"
            :key="entry.key"
            class="home-page__entry-item"
            @tap="handleEntryTap(entry)"
          >
            <view class="home-page__entry-icon" :class="'home-page__entry-icon--' + entry.theme">
              {{ entry.icon }}
            </view>
            <text class="home-page__entry-label">{{ entry.label }}</text>
          </view>
          <view class="home-page__entry-item" @tap="openPartnerApply">
            <view class="home-page__entry-icon home-page__entry-icon--partner">🏢</view>
            <text class="home-page__entry-label">赛事入驻</text>
          </view>
        </view>

        <!-- 4. 热门标签行 -->
        <view v-if="hotTags.length" class="home-page__hot-tags">
          <scroll-view scroll-x class="home-page__hot-tags-scroll" :show-scrollbar="false">
            <view class="home-page__hot-tags-inner">
              <view
                v-for="(tag, idx) in hotTags"
                :key="tag.tag"
                class="home-page__hot-tag-item"
                @tap="goContestListWithTag(tag.tag)"
              >
                <u-tag
                  :text="tag.tag"
                  :type="tagColors[idx % tagColors.length]"
                  plain
                  :plain-fill="true"
                  size="mini"
                  shape="circle"
                />
                <text v-if="tag.count" class="home-page__hot-tag-count">{{ tag.count }}场</text>
              </view>
            </view>
          </scroll-view>
        </view>

        <!-- 5. 专题活动 -->
        <view v-if="topics.length" class="home-page__topics">
          <view
            v-for="topic in topics"
            :key="topic.noticeId"
            class="home-page__topic-card"
            @tap="openTopicLink(topic)"
          >
            <view class="home-page__topic-bg">
              <image v-if="topic.coverImage" class="home-page__topic-cover" :src="topic.coverImage" mode="aspectFill" />
              <view v-else class="home-page__topic-cover-fallback">
                <text class="home-page__topic-cover-emoji">🎟️</text>
              </view>
            </view>
            <view class="home-page__topic-content">
              <text class="home-page__topic-title">{{ topic.title }}</text>
              <text v-if="topic.description" class="home-page__topic-desc">{{ topic.description }}</text>
            </view>
            <view class="home-page__topic-arrow">
              <u-icon name="arrow-right" size="28rpx" color="rgba(255,255,255,0.6)" />
            </view>
          </view>
        </view>

        <!-- 6. 推荐赛事 — 横向滚动 -->
        <view class="home-page__section">
          <view class="home-page__section-head">
            <view>
              <text class="home-page__section-title">热门赛事</text>
            </view>
            <view class="home-page__section-action jst-more-link" @tap="goContestList">
              <text>查看更多</text>
              <u-icon name="arrow-right" size="24rpx" color="#8A8A8A" />
            </view>
          </view>

          <scroll-view
            v-if="recommendContests.length"
            scroll-x
            class="home-page__horizontal-scroll"
            :show-scrollbar="false"
          >
            <view class="home-page__horizontal-inner">
              <view
                v-for="(contest, cIdx) in recommendContests"
                :key="contest.contestId"
                class="jst-anim-slide-up"
                :style="{ animationDelay: getStaggerDelay(cIdx) }"
              >
                <jst-contest-card
                  :contest="contest"
                  :compact="true"
                  @item-tap="openContestDetail"
                />
              </view>
            </view>
          </scroll-view>

          <jst-empty
            v-else
            text="暂无推荐赛事"
            icon="🎯"
          />
        </view>

        <!-- 7. 精选赛事 — 竖排列表 -->
        <view class="home-page__section">
          <view class="home-page__section-head">
            <view>
              <text class="home-page__section-title">精选赛事</text>
            </view>
            <view class="home-page__section-action jst-more-link" @tap="goContestList">
              <text>查看更多</text>
              <u-icon name="arrow-right" size="24rpx" color="#8A8A8A" />
            </view>
          </view>

          <view
            v-if="hotContestList.length"
            class="home-page__contest-list"
          >
            <view v-for="(contest, hIdx) in hotContestList" :key="contest.contestId" class="jst-anim-slide-up" :style="{ animationDelay: getStaggerDelay(hIdx) }">
              <jst-contest-card
                :contest="contest"
                @item-tap="openContestDetail"
              />
            </view>
          </view>

          <jst-empty
            v-else
            text="暂无精选赛事"
            icon="🏆"
          />
        </view>

        <!-- 8. 推荐课程 — 横向滚动 -->
        <view class="home-page__section">
          <view class="home-page__section-head">
            <view>
              <text class="home-page__section-title">精选课程</text>
            </view>
            <view class="home-page__section-action jst-more-link" @tap="goCourseList">
              <text>查看更多</text>
              <u-icon name="arrow-right" size="24rpx" color="#8A8A8A" />
            </view>
          </view>

          <scroll-view
            v-if="recommendCourses.length"
            scroll-x
            class="home-page__horizontal-scroll"
            :show-scrollbar="false"
          >
            <view class="home-page__horizontal-inner">
              <view
                v-for="(course, kIdx) in recommendCourses"
                :key="course.courseId"
                class="jst-anim-slide-up"
                :style="{ animationDelay: getStaggerDelay(kIdx) }"
              >
                <jst-course-card
                  :course="course"
                  :compact="true"
                  @item-tap="openCourseDetail"
                />
              </view>
            </view>
          </scroll-view>

          <jst-empty
            v-else
            text="暂无推荐课程"
            icon="📚"
          />
        </view>

        <!-- 9. 平台数据 -->
        <view class="home-page__platform-intro jst-anim-fade-in">
          <text class="home-page__platform-intro-title">竞赛通 · 一站式竞赛服务平台</text>
          <view class="home-page__platform-stats">
            <view class="home-page__platform-stat">
              <text class="home-page__platform-stat-num">{{ animatedStats.contests }}<text class="home-page__platform-stat-unit">+</text></text>
              <text class="home-page__platform-stat-label">赛事资源</text>
            </view>
            <view class="home-page__platform-stat">
              <text class="home-page__platform-stat-num">{{ animatedStats.students }}<text class="home-page__platform-stat-unit">万+</text></text>
              <text class="home-page__platform-stat-label">注册学生</text>
            </view>
            <view class="home-page__platform-stat">
              <text class="home-page__platform-stat-num">{{ animatedStats.rate }}<text class="home-page__platform-stat-unit">%</text></text>
              <text class="home-page__platform-stat-label">好评率</text>
            </view>
          </view>
          <view class="home-page__platform-tags">
            <u-tag text="✅ 官方授权赛事" type="primary" plain :plain-fill="true" size="mini" shape="circle" />
            <u-tag text="🔒 信息安全保障" type="primary" plain :plain-fill="true" size="mini" shape="circle" />
            <u-tag text="📱 微信一键报名" type="primary" plain :plain-fill="true" size="mini" shape="circle" />
            <u-tag text="🏆 证书在线下载" type="primary" plain :plain-fill="true" size="mini" shape="circle" />
          </view>
        </view>

      </view>

      <view class="home-page__float-service" @tap="openHelpPage">
        <text class="home-page__float-service-icon">💬</text>
        <text class="home-page__float-service-text">在线客服</text>
      </view>
    </template>
  </view>
</template>

<script>
import { getBanner, getNoticeList } from '@/api/notice'
import { getContestHot } from '@/api/contest'
import { getRecommendContests, getRecommendCourses, getHotTags, getTopics } from '@/api/home'
import JstBannerSwiper from '@/components/jst-banner-swiper/jst-banner-swiper.vue'
import JstContestCard from '@/components/jst-contest-card/jst-contest-card.vue'
import JstCourseCard from '@/components/jst-course-card/jst-course-card.vue'
import { normalizeContestCard } from '@/utils/contest'
// [visual-effect]
import { countUp, staggerDelay } from '@/utils/visual-effects'

export default {
  components: {
    JstBannerSwiper,
    JstContestCard,
    JstCourseCard
  },
  data() {
    return {
      bannerList: [],
      noticeTicker: [],
      hotContestList: [],
      recommendContests: [],
      recommendCourses: [],
      hotTags: [],
      topics: [],
      // 标签颜色循环
      tagColors: ['primary', 'success', 'warning', 'error', 'info'],
      entryList: [
        { key: 'contest', icon: '🏆', label: '赛事入口', theme: 'brand' },
        { key: 'course', icon: '📚', label: '课程入口', theme: 'primary' },
        { key: 'notice', icon: '📢', label: '公告资讯', theme: 'gold' },
        { key: 'points', icon: '🎁', label: '积分商城', theme: 'success' },
        { key: 'score-query', icon: '📊', label: '成绩查询', theme: 'primary' },
        { key: 'cert-verify', icon: '🏅', label: '证书验真', theme: 'gold' }
      ],
      skeletonShow: true, // [visual-effect]
      animatedStats: { contests: 0, students: 0, rate: 0 }, // [visual-effect]
      statAnimCancels: [] // [visual-effect]
    }
  },
  onShow() {
    this.loadHomeData()
  },
  // [visual-effect] start
  beforeDestroy() {
    this.statAnimCancels.forEach(fn => fn && fn())
  },
  // [visual-effect] end
  methods: {
    async loadHomeData() {
      // 并行请求所有接口，单个失败不影响其他区块
      await Promise.allSettled([
        this.loadBannerList(),
        this.loadNoticeTicker(),
        this.loadHotContestList(),
        this.loadRecommendContests(),
        this.loadRecommendCourses(),
        this.loadHotTags(),
        this.loadTopics()
      ])
      this.skeletonShow = false // [visual-effect]
      this.animateStats() // [visual-effect]
    },

    // [visual-effect]
    animateStats() {
      this.statAnimCancels.forEach(fn => fn && fn())
      this.statAnimCancels = [
        countUp(0, 500, 800, v => { this.animatedStats.contests = v }),
        countUp(0, 10, 800, v => { this.animatedStats.students = v }),
        countUp(0, 98, 800, v => { this.animatedStats.rate = v })
      ]
    },

    // [visual-effect]
    getStaggerDelay(index) {
      return 'animation-delay:' + staggerDelay(index)
    },

    async loadBannerList() {
      try {
        const list = await getBanner()
        this.bannerList = Array.isArray(list) ? list.map(this.normalizeBanner).filter(item => item.id) : []
      } catch (error) {
        this.bannerList = []
      }
    },

    async loadNoticeTicker() {
      try {
        const response = await getNoticeList({ pageNum: 1, pageSize: 3 })
        this.noticeTicker = this.extractRows(response)
          .slice(0, 3)
          .map(this.normalizeNotice)
          .filter(item => item.noticeId)
      } catch (error) {
        this.noticeTicker = []
      }
    },

    async loadHotContestList() {
      try {
        const list = await getContestHot(6, { silent: true })
        this.hotContestList = Array.isArray(list)
          ? list.map((item) => normalizeContestCard(item)).filter(item => item.contestId)
          : []
      } catch (error) {
        this.hotContestList = []
      }
    },

    async loadRecommendContests() {
      try {
        const list = await getRecommendContests()
        this.recommendContests = Array.isArray(list)
          ? list.map((item) => normalizeContestCard(item)).filter(item => item.contestId)
          : []
      } catch (error) {
        this.recommendContests = []
      }
    },

    async loadRecommendCourses() {
      try {
        const list = await getRecommendCourses()
        this.recommendCourses = Array.isArray(list)
          ? list.map(this.normalizeCourse).filter(item => item.courseId)
          : []
      } catch (error) {
        this.recommendCourses = []
      }
    },

    async loadHotTags() {
      try {
        const list = await getHotTags()
        this.hotTags = Array.isArray(list) ? list : []
      } catch (error) {
        this.hotTags = []
      }
    },

    async loadTopics() {
      try {
        const list = await getTopics()
        this.topics = Array.isArray(list) ? list : []
      } catch (error) {
        this.topics = []
      }
    },

    extractRows(response) {
      if (Array.isArray(response)) {
        return response
      }
      if (response && Array.isArray(response.rows)) {
        return response.rows
      }
      return []
    },

    normalizeBanner(item = {}) {
      return {
        id: item.id || item.noticeId || '',
        title: item.title || '',
        image: item.image || item.coverImage || '',
        type: item.type || 'notice',
        linkUrl: item.linkUrl || '',
        subtitle: item.subtitle || ''
      }
    },

    normalizeNotice(item = {}) {
      return {
        noticeId: item.noticeId || item.id || '',
        title: item.title || '',
        publishTime: item.publishTime || item.createTime || '',
        category: item.category || '',
        categoryLabel: item.categoryLabel || this.getNoticeCategoryLabel(item.category)
      }
    },

    normalizeCourse(item = {}) {
      return {
        courseId: item.courseId || '',
        courseName: item.courseName || '',
        courseType: item.courseType || 'normal',
        coverImage: item.coverImage || '',
        price: item.price || 0,
        pointsPrice: item.pointsPrice || 0,
        lessonCount: item.lessonCount || 0,
        learnerCount: item.learnerCount || 0
      }
    },

    getNoticeCategoryLabel(category) {
      const labelMap = {
        platform: '平台公告',
        contest: '赛事通知',
        score: '成绩发布',
        certificate: '证书通知'
      }
      return labelMap[category] || '公告资讯'
    },

    formatShortDate(value) {
      if (!value) {
        return '--'
      }

      const date = new Date(String(value).replace(/ /g, 'T'))
      if (Number.isNaN(date.getTime())) {
        return value
      }

      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${month}-${day}`
    },

    handleSearch() {
      uni.navigateTo({ url: '/pages/contest/list?keyword=' })
    },

    handleBannerTap(item) {
      if (!item) {
        return
      }

      if (item.linkUrl && item.linkUrl.indexOf('/pages-sub/notice/detail') === 0) {
        uni.navigateTo({ url: item.linkUrl })
        return
      }

      if (item.type === 'notice') {
        this.openNotice({ noticeId: item.id })
      }
    },

    handleEntryTap(entry) {
      if (!entry) {
        return
      }

      if (entry.key === 'notice') {
        this.goNoticeTab()
        return
      }

      if (entry.key === 'contest') {
        this.goContestList()
        return
      }

      if (entry.key === 'course') {
        uni.switchTab({ url: '/pages/course/list' })
        return
      }

      if (entry.key === 'points') {
        uni.navigateTo({ url: '/pages-sub/mall/list' })
        return
      }

      if (entry.key === 'score-query') {
        uni.navigateTo({ url: '/pages-sub/public/score-query' })
        return
      }

      if (entry.key === 'cert-verify') {
        uni.navigateTo({ url: '/pages-sub/public/cert-verify' })
        return
      }
    },

    goNoticeTab() {
      uni.switchTab({ url: '/pages/notice/list' })
    },

    goContestList() {
      uni.switchTab({ url: '/pages/contest/list' })
    },

    // 带标签筛选跳转赛事列表
    goContestListWithTag(tag) {
      uni.navigateTo({ url: `/pages/contest/list?tag=${encodeURIComponent(tag)}` })
    },

    goCourseList() {
      uni.switchTab({ url: '/pages/course/list' })
    },

    openNotice(notice) {
      if (!notice || !notice.noticeId) {
        return
      }
      uni.navigateTo({ url: `/pages-sub/notice/detail?id=${notice.noticeId}` })
    },

    openContestDetail(contest) {
      if (!contest || !contest.contestId) {
        return
      }

      uni.navigateTo({ url: `/pages-sub/contest/detail?id=${contest.contestId}` })
    },

    openCourseDetail(course) {
      if (!course || !course.courseId) {
        return
      }

      uni.navigateTo({ url: `/pages-sub/course/detail?id=${course.courseId}` })
    },

    // 专题跳转：优先用 linkUrl，否则跳公告详情
    openTopicLink(topic) {
      if (!topic) {
        return
      }

      if (topic.linkUrl) {
        uni.navigateTo({ url: topic.linkUrl })
        return
      }

      if (topic.noticeId) {
        uni.navigateTo({ url: `/pages-sub/notice/detail?id=${topic.noticeId}` })
      }
    },

    openPartnerApply() {
      uni.navigateTo({ url: '/pages-sub/public/partner-apply' })
    },

    openHelpPage() {
      uni.navigateTo({ url: '/pages-sub/public/help' })
    },

  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.home-page {
  min-height: 100vh;
  background: $jst-bg-page;
}

// 骨架屏
.home-page__skeleton-hero {
  height: 320rpx;
  background: linear-gradient(150deg, $jst-purple-dark 0%, $jst-purple 55%, $jst-brand 100%);
  border-bottom-left-radius: $jst-radius-xl;
  border-bottom-right-radius: $jst-radius-xl;
}
.home-page__skeleton-body {
  margin-top: -112rpx;
  padding: 0 $jst-space-xl;
}
.home-page__skeleton-banner,
.home-page__skeleton-strip,
.home-page__skeleton-grid {
  margin-top: $jst-space-lg;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
}
.home-page__skeleton-card {
  margin-top: $jst-space-md;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
}

.home-page__hero {
  position: relative;
  overflow: hidden;
  padding: $jst-space-lg $jst-space-xl 160rpx;
  background: linear-gradient(150deg, $jst-purple-dark 0%, $jst-purple 55%, $jst-brand 100%);
  border-bottom-left-radius: $jst-radius-xl;
  border-bottom-right-radius: $jst-radius-xl;
}

.home-page__hero-bg {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.home-page__hero-bg--top {
  top: -180rpx;
  right: -120rpx;
  width: 420rpx;
  height: 420rpx;
  background: rgba(255, 255, 255, 0.08);
}

.home-page__hero-bg--bottom {
  left: -120rpx;
  bottom: -160rpx;
  width: 360rpx;
  height: 360rpx;
  background: rgba(255, 107, 53, 0.18);
}

.home-page__hero-main {
  position: relative;
  z-index: 1;
}

.home-page__brand-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.home-page__brand {
  display: flex;
  align-items: center;
}

.home-page__brand-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 84rpx;
  height: 84rpx;
  border-radius: $jst-radius-xl;
  background: rgba(255, 255, 255, 0.18);
  border: 2rpx solid rgba(255, 255, 255, 0.24);
  font-size: 40rpx;
}

.home-page__brand-copy {
  margin-left: $jst-space-lg;
}

.home-page__brand-title {
  display: block;
  font-size: $jst-font-xxl;
  font-weight: $jst-weight-semibold;
  color: $jst-text-inverse;
  line-height: 1.2;
}

.home-page__brand-subtitle {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  color: rgba(255, 255, 255, 0.78);
}

.home-page__brand-action {
  padding: 14rpx $jst-space-lg;
  border-radius: $jst-radius-round;
  background: rgba(255, 255, 255, 0.18);
  color: $jst-text-inverse;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  transition: transform $jst-duration-fast $jst-easing;
}
.home-page__brand-action:active { transform: scale(0.95); }

.home-page__search {
  display: flex;
  align-items: center;
  margin-top: $jst-space-lg;
  padding: 0 $jst-space-lg;
  height: 84rpx;
  border-radius: $jst-radius-xl;
  background: rgba(255, 255, 255, 0.18);
  border: 2rpx solid rgba(255, 255, 255, 0.24);
  transition: transform $jst-duration-fast $jst-easing;
}
.home-page__search:active { transform: scale(0.99); }

.home-page__search-icon {
  font-size: $jst-font-base;
}

.home-page__search-text {
  margin-left: $jst-space-md;
  font-size: $jst-font-sm;
  color: rgba(255, 255, 255, 0.78);
}

.home-page__body {
  margin-top: -112rpx;
  padding: 0 $jst-space-xl 240rpx;
}

.home-page__banner {
  display: block;
}

.home-page__notice-strip {
  display: flex;
  align-items: center;
  margin-top: $jst-space-lg;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.home-page__notice-strip-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
  border-radius: $jst-radius-lg;
  background: $jst-warning-light;
  font-size: $jst-font-base;
}

.home-page__notice-strip-body {
  flex: 1;
  min-width: 0;
  margin-left: $jst-space-md;
}

.home-page__notice-swiper {
  height: 44rpx;
}

.home-page__notice-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44rpx;
}

.home-page__notice-title {
  flex: 1;
  min-width: 0;
  font-size: $jst-font-sm;
  color: $jst-text-primary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.home-page__notice-time {
  margin-left: $jst-space-md;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.home-page__notice-empty {
  display: flex;
  align-items: center;
  height: 44rpx;
}

.home-page__notice-empty-text {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.home-page__notice-more {
  margin-left: $jst-space-md;
  font-size: $jst-font-xs;
  font-weight: $jst-weight-semibold;
  color: $jst-brand;
}

.home-page__entry-grid {
  display: flex;
  flex-wrap: wrap;
  margin-top: $jst-space-lg;
  padding: $jst-space-sm $jst-space-xs;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.home-page__entry-item {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $jst-space-md $jst-space-xs;
  transition: transform $jst-duration-fast $jst-easing;
}
.home-page__entry-item:active { transform: scale(0.93); }

.home-page__entry-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: $jst-radius-lg;
  font-size: $jst-font-lg;
}

.home-page__entry-icon--brand {
  background: $jst-brand-light;
}

.home-page__entry-icon--primary {
  background: $jst-danger-light;
}

.home-page__entry-icon--gold {
  background: $jst-warning-light;
}

.home-page__entry-icon--success {
  background: $jst-success-light;
}

.home-page__entry-icon--partner {
  background: $jst-info-light;
}

.home-page__entry-label {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-xs;
  font-weight: $jst-weight-medium;
  color: $jst-text-regular;
  text-align: center;
}

// 热门标签行
.home-page__hot-tags {
  margin-top: $jst-space-lg;
}

.home-page__hot-tags-scroll {
  white-space: nowrap;
}

.home-page__hot-tags-inner {
  display: inline-flex;
  gap: $jst-space-md;
  padding: $jst-space-xs 0;
}

.home-page__hot-tag-item {
  display: inline-flex;
  align-items: center;
  gap: $jst-space-xs;
  transition: transform $jst-duration-fast $jst-easing;
}
.home-page__hot-tag-item:active { transform: scale(0.95); }

.home-page__hot-tag-count {
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

// 专题活动卡片
.home-page__topics {
  margin-top: $jst-space-lg;
}

.home-page__topic-card {
  display: flex;
  align-items: center;
  margin-bottom: $jst-space-md;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: linear-gradient(90deg, $jst-purple-dark, $jst-purple);
  overflow: hidden;
  transition: transform $jst-duration-fast $jst-easing;
}
.home-page__topic-card:active { transform: scale(0.98); }

.home-page__topic-bg {
  flex-shrink: 0;
  width: 72rpx;
  height: 72rpx;
  border-radius: $jst-radius-md;
  overflow: hidden;
  margin-right: $jst-space-lg;
}

.home-page__topic-cover {
  width: 100%;
  height: 100%;
}

.home-page__topic-cover-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.15);
}

.home-page__topic-cover-emoji {
  font-size: $jst-font-xl;
}

.home-page__topic-content {
  flex: 1;
  min-width: 0;
}

.home-page__topic-title {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.home-page__topic-desc {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  color: rgba(255, 255, 255, 0.7);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.home-page__topic-arrow {
  flex-shrink: 0;
  margin-left: $jst-space-md;
}

// Section 通用
.home-page__section {
  margin-top: $jst-space-xl;
}

.home-page__section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $jst-space-xs $jst-space-lg;
}

.home-page__section-title {
  display: block;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
  text-align: left;
}

.home-page__section-action {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-medium;
  color: $jst-brand;
}

// 横向滚动容器
.home-page__horizontal-scroll {
  white-space: nowrap;
}

.home-page__horizontal-inner {
  display: inline-flex;
  gap: $jst-space-lg;
  padding: $jst-space-xs 0;
}

// 竖排赛事列表
.home-page__contest-list {
  padding: $jst-space-xs;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}
.home-page__contest-list > * {
  padding: $jst-space-xs;
}

// 平台介绍
.home-page__platform-intro {
  margin-top: $jst-space-xl;
  padding: $jst-space-xl;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}
.home-page__platform-intro-title {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
  margin-bottom: $jst-space-lg;
}
.home-page__platform-stats {
  display: flex;
  margin-bottom: $jst-space-lg;
}
.home-page__platform-stat {
  flex: 1;
  text-align: center;
}
.home-page__platform-stat-num {
  display: block;
  font-size: $jst-font-xxl;
  font-weight: $jst-weight-semibold;
  color: $jst-brand;
  font-feature-settings: "tnum";
  font-variant-numeric: tabular-nums;
  line-height: 1.1;
}
.home-page__platform-stat-unit {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  margin-left: 2rpx;
}
.home-page__platform-stat-label {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}
.home-page__platform-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $jst-space-sm;
}

// 悬浮客服
.home-page__float-service {
  position: fixed;
  right: $jst-space-lg;
  bottom: 180rpx;
  z-index: 30;
  display: flex;
  align-items: center;
  gap: $jst-space-xs;
  padding: $jst-space-md $jst-space-lg;
  border-radius: $jst-radius-round;
  background: $jst-brand-gradient;
  box-shadow: 0 10rpx $jst-space-lg rgba(43, 108, 255, 0.28);
  transition: transform $jst-duration-fast $jst-easing;
}
.home-page__float-service:active { transform: scale(0.93); }
.home-page__float-service::after {
  content: '';
  position: absolute;
  left: -10rpx;
  top: 50%;
  transform: translateY(-50%);
  border: 10rpx solid transparent;
  border-right-color: $jst-brand;
}
.home-page__float-service-icon {
  font-size: $jst-font-base;
  color: $jst-text-inverse;
}
.home-page__float-service-text {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-medium;
  color: $jst-text-inverse;
  letter-spacing: 1rpx;
}
</style>
