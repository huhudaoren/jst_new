<!-- 中文注释: 赛事详情页；对应原型 contest-detail.html；对应接口 GET /jst/wx/contest/{id} -->
<template>
  <view class="contest-detail-page">
    <!-- 骨架屏 -->
    <jst-skeleton-plus v-if="pageLoading" :loading="true" mode="hero" />

    <template v-if="detail && !pageLoading">
      <!-- 1. 沉浸式 Hero -->
      <jst-hero-banner
        :src="detail.bannerImage || detail.coverImage"
        :fallback-icon="categoryIcon"
        :scroll-top="heroScrollTop"
        :show-nav="true"
        :nav-title="detail.contestName"
        height="520rpx"
        @back="handleBack"
      >
        <view class="contest-detail-page__hero-info">
          <view class="contest-detail-page__hero-tags">
            <u-tag v-if="detail.category" :text="detail.category" type="primary" plain shape="circle" size="mini" />
            <jst-status-badge :status="detail.status" :enroll-open="detail.enrollOpen" />
          </view>
          <text class="contest-detail-page__hero-title">{{ detail.contestName }}</text>
          <text v-if="detail.partnerName" class="contest-detail-page__hero-partner">{{ detail.partnerName }}</text>
        </view>
      </jst-hero-banner>

      <!-- 2. 粘性锚点导航 -->
      <view v-if="showStickyNav" class="contest-detail-page__sticky-nav jst-anim-sticky-enter">
        <scroll-view scroll-x class="contest-detail-page__sticky-scroll" :show-scrollbar="false">
          <view class="contest-detail-page__sticky-inner">
            <view
              v-for="(sec, si) in navSections"
              :key="sec.id"
              class="contest-detail-page__sticky-item"
              :class="{ 'contest-detail-page__sticky-item--active': activeSection === sec.id }"
              @tap="scrollToSection(sec.id)"
            >
              <text>{{ sec.label }}</text>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 3. 内容区 -->
      <view class="contest-detail-page__body">
        <!-- 价格卡 -->
        <view class="contest-detail-page__price-card jst-anim-fade-up">
          <text class="contest-detail-page__price-label">报名费用</text>
          <text class="contest-detail-page__price-value">{{ priceText }}</text>
        </view>

        <!-- 基本信息 -->
        <view id="section-info" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">基本信息</text>
          <view class="contest-detail-page__info-grid">
            <view class="contest-detail-page__info-item">
              <text class="contest-detail-page__info-icon">&#128197;</text>
              <view class="contest-detail-page__info-text">
                <text class="contest-detail-page__info-key">报名时间</text>
                <text class="contest-detail-page__info-val">{{ enrollPeriodText }}</text>
              </view>
            </view>
            <view class="contest-detail-page__info-item">
              <text class="contest-detail-page__info-icon">&#127942;</text>
              <view class="contest-detail-page__info-text">
                <text class="contest-detail-page__info-key">比赛时间</text>
                <text class="contest-detail-page__info-val">{{ eventPeriodText }}</text>
              </view>
            </view>
            <view class="contest-detail-page__info-item">
              <text class="contest-detail-page__info-icon">&#128101;</text>
              <view class="contest-detail-page__info-text">
                <text class="contest-detail-page__info-key">参赛组别</text>
                <text class="contest-detail-page__info-val">{{ detail.groupLevels || '待公布' }}</text>
              </view>
            </view>
            <view class="contest-detail-page__info-item">
              <text class="contest-detail-page__info-icon">&#128179;</text>
              <view class="contest-detail-page__info-text">
                <text class="contest-detail-page__info-key">积分抵扣</text>
                <text class="contest-detail-page__info-val">{{ detail.supportPointsDeduct === 1 ? '支持' : '不支持' }}</text>
              </view>
            </view>
          </view>
          <view v-if="detail.supportAppointment === 1" class="contest-detail-page__feature-tag">
            <text class="contest-detail-page__feature-tag-icon">&#128205;</text>
            <text class="contest-detail-page__feature-tag-text">支持线下预约</text>
          </view>
          <view v-if="detail.supportChannelEnroll === 1" class="contest-detail-page__feature-tag">
            <text class="contest-detail-page__feature-tag-icon">&#128279;</text>
            <text class="contest-detail-page__feature-tag-text">支持渠道报名</text>
          </view>
        </view>

        <!-- 赛事介绍 -->
        <view id="section-intro" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">赛事介绍</text>
          <view v-if="detailDescription" class="contest-detail-page__rich-text">
            <rich-text :nodes="detailDescription"></rich-text>
          </view>
          <jst-empty-state v-else icon="&#128214;" title="赛事介绍待补充" />
        </view>

        <!-- 赛程安排 -->
        <view v-if="scheduleList.length" id="section-schedule" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">赛程安排</text>
          <jst-timeline :items="scheduleTimelineItems" :active-index="currentScheduleIndex" />
        </view>

        <!-- 奖项设置 -->
        <view v-if="awardList.length" id="section-awards" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">奖项设置</text>
          <view class="contest-detail-page__award-grid">
            <view v-for="(award, ai) in awardList" :key="ai" class="contest-detail-page__award-item">
              <text class="contest-detail-page__award-icon">{{ getAwardIcon(award.level || award.name) }}</text>
              <text class="contest-detail-page__award-name">{{ award.name || award.level || '奖项' }}</text>
              <text v-if="award.description || award.desc" class="contest-detail-page__award-desc">{{ award.description || award.desc }}</text>
            </view>
          </view>
        </view>

        <!-- 评分维度 -->
        <view v-if="scoreItemList.length" id="section-score" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">评分维度</text>
          <view class="contest-detail-page__score-tags">
            <u-tag v-for="(si, idx) in scoreItemList" :key="idx" :text="si.name || si.label || si" type="primary" plain shape="circle" size="mini" />
          </view>
        </view>

        <!-- 常见问题 -->
        <view v-if="faqList.length" id="section-faq" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">常见问题</text>
          <u-collapse>
            <u-collapse-item v-for="(faq, fi) in faqList" :key="fi" :title="faq.question || faq.q || '问题'" :name="fi">
              <text class="contest-detail-page__faq-answer">{{ faq.answer || faq.a || '' }}</text>
            </u-collapse-item>
          </u-collapse>
        </view>
        <!-- 成绩查询入口（赛事已结束或已出成绩时显示） -->
        <view v-if="detail.scorePublished" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">成绩查询</text>
          <view class="contest-detail-page__score-entry" @tap="goScoreQuery">
            <view class="contest-detail-page__score-entry-icon">🏆</view>
            <view class="contest-detail-page__score-entry-body">
              <text class="contest-detail-page__score-entry-title">本赛事成绩已发布</text>
              <text class="contest-detail-page__score-entry-desc">点击查看成绩与证书</text>
            </view>
            <text class="contest-detail-page__score-entry-arrow">›</text>
          </view>
        </view>
      </view>

      <!-- 4. CTA 底栏 -->
      <view class="contest-detail-page__cta">
        <view class="contest-detail-page__cta-main">
          <text class="contest-detail-page__cta-label">报名费用</text>
          <text class="contest-detail-page__cta-price">{{ priceText }}</text>
        </view>
        <view class="contest-detail-page__cta-actions">
          <u-button
            v-if="showAppointmentEntry"
            class="contest-detail-page__cta-secondary"
            @click="handleAppointmentTap"
          >我要预约</u-button>
          <u-button
            v-if="showTeamEnrollEntry"
            class="contest-detail-page__cta-secondary"
            :disabled="enrollAction.disabled"
            @click="handleTeamEnrollTap"
          >团队报名</u-button>
          <u-button
            class="contest-detail-page__cta-button"
            :class="{ 'contest-detail-page__cta-button--disabled': enrollAction.disabled }"
            :disabled="enrollAction.disabled"
            @click="handleEnrollTap"
          >
            {{ enrollAction.text }}
          </u-button>
        </view>
      </view>
    </template>

    <jst-empty-state
      v-else-if="!pageLoading"
      icon="&#128237;"
      title="暂无赛事详情"
    />
  </view>
</template>

<script>
import { getContestDetail } from '@/api/contest'
import JstEmptyState from '@/components/jst-empty-state/jst-empty-state.vue'
import JstHeroBanner from '@/components/jst-hero-banner/jst-hero-banner.vue'
import JstSkeletonPlus from '@/components/jst-skeleton-plus/jst-skeleton-plus.vue'
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'
import JstTimeline from '@/components/jst-timeline/jst-timeline.vue'
import {
  normalizeContestCard,
  formatContestPeriod,
  formatContestPrice,
  sanitizeRichText,
  getContestEnrollAction,
  getContestCategoryIcon
} from '@/utils/contest'
import { throttle, stickyNavState } from '@/utils/visual-effects'

export default {
  components: {
    JstEmptyState,
    JstHeroBanner,
    JstSkeletonPlus,
    JstStatusBadge,
    JstTimeline
  },
  data() {
    return {
      pageLoading: false,
      contestId: '',
      detail: null,
      // [visual-effect] 视觉状态
      heroScrollTop: 0,
      showStickyNav: false,
      activeSection: 'section-info'
    }
  },
  computed: {
    enrollPeriodText() {
      return this.detail
        ? formatContestPeriod(this.detail.enrollStartTime, this.detail.enrollEndTime)
        : '--'
    },
    eventPeriodText() {
      return this.detail
        ? formatContestPeriod(this.detail.eventStartTime, this.detail.eventEndTime)
        : '--'
    },
    priceText() {
      return this.detail ? formatContestPrice(this.detail.price) : '--'
    },
    detailDescription() {
      return this.detail ? sanitizeRichText(this.detail.description) : ''
    },
    enrollAction() {
      return getContestEnrollAction(this.detail || {})
    },
    showAppointmentEntry() {
      const v = this.detail && this.detail.supportAppointment
      return v === 1 || v === true || v === '1'
    },
    showTeamEnrollEntry() {
      return this.detail && this.detail.teamMinSize > 0
    },
    categoryIcon() {
      return getContestCategoryIcon(this.detail && this.detail.category)
    },
    // 结构化数据
    scheduleList() {
      return (this.detail && Array.isArray(this.detail.scheduleList) && this.detail.scheduleList) || []
    },
    awardList() {
      return (this.detail && Array.isArray(this.detail.awardList) && this.detail.awardList) || []
    },
    faqList() {
      return (this.detail && Array.isArray(this.detail.faqList) && this.detail.faqList) || []
    },
    scoreItemList() {
      return (this.detail && Array.isArray(this.detail.scoreItemList) && this.detail.scoreItemList) || []
    },
    // [visual-effect] 赛程时间轴数据
    scheduleTimelineItems() {
      return this.scheduleList.map(function(s) {
        return { label: s.name || s.stageName || '阶段', time: s.time || s.startTime || '', desc: s.description || s.desc || '' }
      })
    },
    currentScheduleIndex() {
      return -1
    },
    // [visual-effect] 粘性导航 sections
    navSections() {
      var secs = [{ id: 'section-info', label: '基本信息' }, { id: 'section-intro', label: '赛事介绍' }]
      if (this.scheduleList.length) secs.push({ id: 'section-schedule', label: '赛程安排' })
      if (this.awardList.length) secs.push({ id: 'section-awards', label: '奖项设置' })
      if (this.scoreItemList.length) secs.push({ id: 'section-score', label: '评分维度' })
      if (this.faqList.length) secs.push({ id: 'section-faq', label: '常见问题' })
      return secs
    }
  },
  onLoad(query) {
    this.contestId = query.contestId || query.id || ''
    // 微信团队邀请落地：显示邀请提示并高亮团队报名入口
    if (query.inviteTeam === '1') {
      uni.showToast({ title: '好友邀请你参加团队报名', icon: 'none', duration: 3000 })
    }
    this.fetchDetail()
  },
  // [visual-effect] 滚动视差 + 粘性导航
  onPageScroll: throttle(function(e) {
    this.heroScrollTop = e.scrollTop || 0
    var state = stickyNavState(e.scrollTop, 460)
    this.showStickyNav = state.show
  }, 16),
  methods: {
    async fetchDetail() {
      if (!this.contestId) {
        this.detail = null
        return
      }

      this.pageLoading = true

      try {
        const response = await getContestDetail(this.contestId, { silent: true })
        this.detail = normalizeContestCard(response || {})
      } catch (error) {
        uni.showToast({ title: '加载失败，请重试', icon: 'none' })
        this.detail = null
      } finally {
        this.pageLoading = false
      }
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }

      uni.switchTab({ url: '/pages/contest/list' })
    },

    // 跳转成绩查询（我的成绩页）
    goScoreQuery() {
      uni.navigateTo({ url: '/pages-sub/my/score' })
    },

    handleAppointmentTap() {
      uni.navigateTo({
        url: '/pages-sub/appointment/apply?contestId=' + this.contestId + '&contestName=' + encodeURIComponent((this.detail && this.detail.contestName) || '')
      })
    },

    handleTeamEnrollTap() {
      if (this.enrollAction.disabled) {
        return
      }
      uni.navigateTo({
        url: '/pages-sub/contest/team-enroll?contestId=' + this.contestId
      })
    },

    handleEnrollTap() {
      if (this.enrollAction.disabled) {
        return
      }

      uni.navigateTo({
        url: '/pages-sub/contest/enroll?contestId=' + this.contestId
      })
    },

    // [visual-effect] 锚点跳转
    scrollToSection(sectionId) {
      this.activeSection = sectionId
      var query = uni.createSelectorQuery().in(this)
      query.select('#' + sectionId).boundingClientRect(function(rect) {
        if (rect) {
          uni.pageScrollTo({ scrollTop: rect.top + (uni.getSystemInfoSync().windowHeight ? 0 : 0) - 100, duration: 300 })
        }
      }).exec()
    },

    getAwardIcon(level) {
      if (!level) return '🏅'
      var l = (level + '').toLowerCase()
      if (l.indexOf('一等') !== -1 || l.indexOf('金') !== -1) return '🥇'
      if (l.indexOf('二等') !== -1 || l.indexOf('银') !== -1) return '🥈'
      if (l.indexOf('三等') !== -1 || l.indexOf('铜') !== -1) return '🥉'
      return '🏅'
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.contest-detail-page {
  min-height: 100vh;
  padding-bottom: calc(160rpx + env(safe-area-inset-bottom));
  background: $jst-bg-subtle;
}

// Hero 叠加内容
.contest-detail-page__hero-info {
  padding-bottom: $jst-space-sm;
}

.contest-detail-page__hero-tags {
  display: flex;
  align-items: center;
  gap: $jst-space-sm;
  margin-bottom: $jst-space-md;
}

.contest-detail-page__hero-title {
  display: block;
  font-size: 44rpx;
  font-weight: $jst-weight-bold;
  line-height: 1.3;
  color: $jst-text-inverse;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.3);
}

.contest-detail-page__hero-partner {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  color: rgba(255, 255, 255, 0.8);
}

// 粘性导航
.contest-detail-page__sticky-nav {
  position: sticky;
  top: 0;
  z-index: 20;
  background: $jst-glass-bg;
  backdrop-filter: blur($jst-glass-blur);
  -webkit-backdrop-filter: blur($jst-glass-blur);
  border-bottom: 1rpx solid $jst-glass-border;
  box-shadow: $jst-shadow-ambient;
}

.contest-detail-page__sticky-scroll {
  white-space: nowrap;
}

.contest-detail-page__sticky-inner {
  display: inline-flex;
  gap: $jst-space-xl;
  padding: $jst-space-md $jst-page-padding;
}

.contest-detail-page__sticky-item {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
  padding-bottom: $jst-space-xs;
  border-bottom: 3rpx solid transparent;
  transition: all $jst-duration-fast $jst-easing;
  flex-shrink: 0;
}

.contest-detail-page__sticky-item--active {
  color: $jst-brand;
  font-weight: $jst-weight-semibold;
  border-bottom-color: $jst-brand;
}

// 内容区
.contest-detail-page__body {
  padding: $jst-space-lg $jst-page-padding;
}

// 价格卡
.contest-detail-page__price-card {
  display: flex;
  align-items: baseline;
  gap: $jst-space-sm;
  padding: $jst-space-lg;
  margin-bottom: $jst-space-lg;
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-ring, $jst-shadow-ambient, $jst-shadow-lift;
}

.contest-detail-page__price-label {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.contest-detail-page__price-value {
  font-size: $jst-font-xxl;
  font-weight: $jst-weight-bold;
  color: $jst-brand;
}

// 通用卡片
.contest-detail-page__card {
  margin-bottom: $jst-space-lg;
  padding: $jst-space-xl $jst-space-lg;
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-ring, $jst-shadow-ambient;
}

.contest-detail-page__card-title {
  display: block;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
  margin-bottom: $jst-space-lg;
  padding-bottom: $jst-space-md;
  border-bottom: 1rpx solid $jst-border;
}

// 信息网格
.contest-detail-page__info-grid {
  display: flex;
  flex-wrap: wrap;
  gap: $jst-space-lg;
}

.contest-detail-page__info-item {
  display: flex;
  align-items: flex-start;
  gap: $jst-space-sm;
  width: calc(50% - #{$jst-space-lg} / 2);
}

.contest-detail-page__info-icon {
  font-size: 36rpx;
  flex-shrink: 0;
  margin-top: 2rpx;
}

.contest-detail-page__info-text {
  flex: 1;
  min-width: 0;
}

.contest-detail-page__info-key {
  display: block;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
  margin-bottom: 4rpx;
}

.contest-detail-page__info-val {
  display: block;
  font-size: $jst-font-sm;
  color: $jst-text-primary;
  font-weight: $jst-weight-medium;
  line-height: 1.5;
}

.contest-detail-page__feature-tag {
  display: inline-flex;
  align-items: center;
  gap: 6rpx;
  margin-top: $jst-space-md;
  margin-right: $jst-space-md;
  padding: 6rpx $jst-space-md;
  border-radius: $jst-radius-round;
  background: $jst-brand-light;
}

.contest-detail-page__feature-tag-icon {
  font-size: $jst-font-sm;
}

.contest-detail-page__feature-tag-text {
  font-size: $jst-font-xs;
  color: $jst-brand;
  font-weight: $jst-weight-medium;
}

// 富文本
.contest-detail-page__rich-text {
  font-size: $jst-font-sm;
  line-height: 1.8;
  color: $jst-text-regular;
}

// 奖项网格
.contest-detail-page__award-grid {
  display: flex;
  flex-wrap: wrap;
  gap: $jst-space-md;
}

.contest-detail-page__award-item {
  flex: 1;
  min-width: calc(33% - #{$jst-space-md});
  text-align: center;
  padding: $jst-space-lg $jst-space-sm;
  border-radius: $jst-radius-md;
  background: $jst-bg-grey;
  transition: transform $jst-duration-fast $jst-easing;
  &:active { transform: scale(0.97); }
}

.contest-detail-page__award-icon {
  display: block;
  font-size: 48rpx;
  margin-bottom: $jst-space-xs;
}

.contest-detail-page__award-name {
  display: block;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
  margin-bottom: 4rpx;
}

.contest-detail-page__award-desc {
  display: block;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
  line-height: 1.5;
}

// 评分维度标签
.contest-detail-page__score-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $jst-space-sm;
}

// FAQ
.contest-detail-page__faq-answer {
  font-size: $jst-font-sm;
  color: $jst-text-regular;
  line-height: 1.7;
}

// CTA 底栏
.contest-detail-page__cta {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 30;
  display: flex;
  align-items: center;
  gap: $jst-space-lg;
  padding: $jst-space-lg $jst-page-padding calc(#{$jst-space-lg} + env(safe-area-inset-bottom));
  background: $jst-glass-bg;
  backdrop-filter: blur($jst-glass-blur);
  -webkit-backdrop-filter: blur($jst-glass-blur);
  border-top: 1rpx solid $jst-glass-border;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.contest-detail-page__cta-main {
  flex: 1;
}

.contest-detail-page__cta-label {
  display: block;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.contest-detail-page__cta-price {
  display: block;
  margin-top: 4rpx;
  font-size: 40rpx;
  font-weight: $jst-weight-bold;
  color: $jst-brand;
}

.contest-detail-page__cta-actions {
  display: flex;
  align-items: center;
  gap: $jst-space-md;
}

.contest-detail-page__cta-button {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 220rpx;
  height: 88rpx;
  padding: 0 $jst-space-xl;
  border-radius: $jst-radius-round;
  background: $jst-brand;
  font-size: $jst-font-base;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
  transition: transform $jst-duration-fast $jst-easing;
  &:active { transform: scale(0.96); }
}

.contest-detail-page__cta-button--disabled {
  background: $jst-border;
  color: $jst-text-secondary;
}

.contest-detail-page__cta-secondary {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 180rpx;
  height: 88rpx;
  padding: 0 $jst-space-lg;
  border-radius: $jst-radius-round;
  background: $jst-brand-light;
  color: $jst-brand;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-bold;
  border: none;
  transition: transform $jst-duration-fast $jst-easing;
  &:active { transform: scale(0.96); }
}

::v-deep .contest-detail-page__cta-secondary.u-button,
::v-deep .contest-detail-page__cta-button.u-button {
  min-height: 88rpx;
  line-height: 88rpx;
  border: none;
}

// 成绩查询入口
.contest-detail-page__score-entry {
  display: flex;
  align-items: center;
  padding: $jst-space-lg;
  border-radius: $jst-radius-md;
  background: $jst-gold-light;
  transition: transform $jst-duration-fast $jst-easing;
  &:active { transform: scale(0.98); }
}

.contest-detail-page__score-entry-icon {
  font-size: 48rpx;
  flex-shrink: 0;
}

.contest-detail-page__score-entry-body {
  flex: 1;
  margin-left: $jst-space-md;
}

.contest-detail-page__score-entry-title {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.contest-detail-page__score-entry-desc {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.contest-detail-page__score-entry-arrow {
  font-size: $jst-font-xl;
  color: $jst-text-placeholder;
}
</style>
