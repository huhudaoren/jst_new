<!-- 中文注释: 首页；对应原型 小程序原型图/index.html；对应接口 GET /jst/wx/index/banner、GET /jst/wx/notice/list、GET /jst/wx/contest/hot -->
<template>
  <view class="home-page">
    <view class="home-page__hero">
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
      <jst-banner-swiper
        v-if="bannerList.length"
        class="home-page__banner"
        :list="bannerList"
        @tap="handleBannerTap"
      />

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

        <view class="home-page__notice-more" @tap="goNoticeTab">更多</view>
      </view>

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
          <text class="home-page__entry-desc">{{ entry.desc }}</text>
        </view>
      </view>

      <view class="home-page__section">
        <view class="home-page__section-head">
          <view>
            <text class="home-page__section-title">热门赛事</text>
            <text class="home-page__section-desc">优先展示后台已发布的热门赛事</text>
          </view>
          <view class="home-page__section-action" @tap="goContestList">
            查看更多
          </view>
        </view>

        <view
          v-if="hotContestList.length"
          class="home-page__contest-list"
        >
          <jst-contest-card
            v-for="contest in hotContestList"
            :key="contest.contestId"
            :contest="contest"
            @tap="openContestDetail"
          />
        </view>

        <jst-empty
          v-else
          text="热门赛事待后端 F7 联调完成后展示"
          icon="🎯"
        />
      </view>

      <view class="home-page__partner-entry" @tap="openPartnerApply">
        <view class="home-page__partner-entry-icon">🏢</view>
        <view class="home-page__partner-entry-body">
          <text class="home-page__partner-entry-title">赛事方入驻合作</text>
          <text class="home-page__partner-entry-desc">发布赛事 · 管理报名 · 结算收款</text>
        </view>
        <view class="home-page__partner-entry-tag">申请入驻</view>
      </view>
    </view>

    <view class="home-page__float-service" @tap="openHelpPage">💬</view>
  </view>
</template>

<script>
import { getBanner, getNoticeList } from '@/api/notice'
import { getContestHot } from '@/api/contest'
import JstBannerSwiper from '@/components/jst-banner-swiper/jst-banner-swiper.vue'
import JstContestCard from '@/components/jst-contest-card/jst-contest-card.vue'
import { normalizeContestCard } from '@/utils/contest'

export default {
  components: {
    JstBannerSwiper,
    JstContestCard
  },
  data() {
    return {
      bannerList: [],
      noticeTicker: [],
      hotContestList: [],
      entryList: [
        { key: 'contest', icon: '🏆', label: '赛事入口', desc: 'P3 开放', theme: 'brand' },
        { key: 'course', icon: '📚', label: '课程入口', desc: '查看课程内容', theme: 'primary' },
        { key: 'notice', icon: '📢', label: '公告资讯', desc: '查看最新公告', theme: 'gold' },
        { key: 'points', icon: '🎁', label: '积分商城', desc: '后续开放', theme: 'success' }
      ]
    }
  },
  onShow() {
    this.loadHomeData()
  },
  methods: {
    async loadHomeData() {
      await Promise.allSettled([
        this.loadBannerList(),
        this.loadNoticeTicker(),
        this.loadHotContestList()
      ])
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

    normalizeContest(item = {}) {
      return normalizeContestCard(item)
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

      const date = new Date(value)
      if (Number.isNaN(date.getTime())) {
        return value
      }

      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${month}-${day}`
    },

    handleSearch() {
      uni.showToast({
        title: '搜索功能后续开放',
        icon: 'none'
      })
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

      this.showComingSoon('积分商城后续开放')
    },

    goNoticeTab() {
      uni.switchTab({ url: '/pages/notice/list' })
    },

    goContestList() {
      uni.switchTab({ url: '/pages/contest/list' })
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

    openPartnerApply() {
      uni.navigateTo({ url: '/pages-sub/public/partner-apply' })
    },

    openHelpPage() {
      uni.navigateTo({ url: '/pages-sub/public/help' })
    },

    showComingSoon(title) {
      uni.showToast({
        title,
        icon: 'none'
      })
    }
  }
}
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
  background: var(--jst-color-page-bg);
}

.home-page__hero {
  position: relative;
  overflow: hidden;
  padding: 28rpx 32rpx 160rpx;
  background: linear-gradient(145deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%);
  border-bottom-left-radius: 48rpx;
  border-bottom-right-radius: 48rpx;
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
  background: var(--jst-color-white-08);
}

.home-page__hero-bg--bottom {
  left: -120rpx;
  bottom: -160rpx;
  width: 360rpx;
  height: 360rpx;
  background: var(--jst-color-primary-18);
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
  border-radius: 24rpx;
  background: var(--jst-color-white-18);
  border: 2rpx solid var(--jst-color-white-24);
  font-size: 40rpx;
}

.home-page__brand-copy {
  margin-left: 20rpx;
}

.home-page__brand-title {
  display: block;
  font-size: 42rpx;
  font-weight: 800;
  color: var(--jst-color-card-bg);
  line-height: 1.2;
}

.home-page__brand-subtitle {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: var(--jst-color-white-78);
}

.home-page__brand-action {
  padding: 14rpx 24rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-white-18);
  color: var(--jst-color-card-bg);
  font-size: 24rpx;
  font-weight: 600;
}

.home-page__search {
  display: flex;
  align-items: center;
  margin-top: 28rpx;
  padding: 0 24rpx;
  height: 84rpx;
  border-radius: 24rpx;
  background: var(--jst-color-white-18);
  border: 2rpx solid var(--jst-color-white-24);
}

.home-page__search-icon {
  font-size: 28rpx;
}

.home-page__search-text {
  margin-left: 16rpx;
  font-size: 26rpx;
  color: var(--jst-color-white-78);
}

.home-page__body {
  margin-top: -112rpx;
  padding: 0 32rpx 40rpx;
}

.home-page__banner {
  display: block;
}

.home-page__notice-strip {
  display: flex;
  align-items: center;
  margin-top: 24rpx;
  padding: 18rpx 20rpx;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.home-page__notice-strip-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
  border-radius: 18rpx;
  background: var(--jst-color-primary-soft);
  font-size: 28rpx;
}

.home-page__notice-strip-body {
  flex: 1;
  min-width: 0;
  margin-left: 16rpx;
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
  font-size: 24rpx;
  color: var(--jst-color-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.home-page__notice-time {
  margin-left: 16rpx;
  font-size: 22rpx;
  color: var(--jst-color-text-tertiary);
}

.home-page__notice-empty {
  display: flex;
  align-items: center;
  height: 44rpx;
}

.home-page__notice-empty-text {
  font-size: 24rpx;
  color: var(--jst-color-text-tertiary);
}

.home-page__notice-more {
  margin-left: 16rpx;
  font-size: 22rpx;
  font-weight: 600;
  color: var(--jst-color-brand);
}

.home-page__entry-grid {
  display: flex;
  flex-wrap: wrap;
  margin-top: 24rpx;
  padding: 16rpx 0;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.home-page__entry-item {
  width: 50%;
  padding: 24rpx 20rpx;
}

.home-page__entry-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 84rpx;
  height: 84rpx;
  border-radius: 26rpx;
  font-size: 38rpx;
}

.home-page__entry-icon--brand {
  background: var(--jst-color-brand-soft);
}

.home-page__entry-icon--primary {
  background: var(--jst-color-primary-soft);
}

.home-page__entry-icon--gold {
  background: var(--jst-color-warning-soft);
}

.home-page__entry-icon--success {
  background: var(--jst-color-success-soft);
}

.home-page__entry-label {
  display: block;
  margin-top: 18rpx;
  font-size: 28rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.home-page__entry-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: var(--jst-color-text-tertiary);
}

.home-page__section {
  margin-top: 24rpx;
  padding: 28rpx 0 24rpx;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.home-page__section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 0 24rpx;
}

.home-page__section-title {
  display: block;
  font-size: 30rpx;
  font-weight: 800;
  color: var(--jst-color-text);
}

.home-page__section-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: var(--jst-color-text-tertiary);
}

.home-page__section-action {
  padding: 10rpx 20rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-brand-soft);
  font-size: 22rpx;
  font-weight: 600;
  color: var(--jst-color-brand);
}

.home-page__contest-list {
  margin-top: 24rpx;
}

.home-page__partner-entry {
  display: flex;
  align-items: center;
  gap: 18rpx;
  margin-top: 24rpx;
  padding: 24rpx 24rpx;
  border-radius: var(--jst-radius-lg);
  background: linear-gradient(90deg, #e8f5e9 0%, #ffffff 100%);
  border: 2rpx solid rgba(27, 94, 32, 0.12);
  box-shadow: 0 8rpx 20rpx rgba(27, 94, 32, 0.06);
}

.home-page__partner-entry-icon {
  flex-shrink: 0;
  font-size: 42rpx;
}

.home-page__partner-entry-body {
  flex: 1;
  min-width: 0;
}

.home-page__partner-entry-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: #1b5e20;
}

.home-page__partner-entry-desc {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  color: #2e7d32;
}

.home-page__partner-entry-tag {
  flex-shrink: 0;
  padding: 10rpx 18rpx;
  border-radius: var(--jst-radius-full);
  background: rgba(27, 94, 32, 0.1);
  font-size: 22rpx;
  font-weight: 700;
  color: #1b5e20;
}

.home-page__float-service {
  position: fixed;
  right: 24rpx;
  bottom: 160rpx;
  z-index: 30;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 92rpx;
  height: 92rpx;
  border-radius: 50%;
  background: var(--jst-color-brand);
  box-shadow: 0 14rpx 30rpx rgba(16, 88, 160, 0.2);
  font-size: 40rpx;
}
</style>
