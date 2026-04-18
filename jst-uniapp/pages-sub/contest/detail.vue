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

      <!-- 2. 常驻吸顶锚点导航（5 个固定 tab，无数据时置灰） -->
      <view class="contest-detail-page__sticky-nav">
        <scroll-view scroll-x class="contest-detail-page__sticky-scroll" :show-scrollbar="false" :scroll-into-view="scrollIntoTabId" scroll-with-animation>
          <view class="contest-detail-page__sticky-inner">
            <view
              v-for="sec in navSections"
              :key="sec.id"
              :id="'tab-' + sec.id"
              class="contest-detail-page__sticky-item"
              :class="{
                'contest-detail-page__sticky-item--active': activeSection === sec.id,
                'contest-detail-page__sticky-item--disabled': sec.disabled
              }"
              @tap="sec.disabled ? null : scrollToSection(sec.id)"
            >
              <text>{{ sec.label }}</text>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 3. 内容区 -->
      <view class="contest-detail-page__body">
        <!-- 倒计时条（报名进行中时显示） -->
        <view v-if="countdown.show" class="contest-detail-page__countdown jst-anim-fade-up">
          <text class="contest-detail-page__countdown-label">距报名截止</text>
          <view class="contest-detail-page__countdown-timer">
            <view class="contest-detail-page__countdown-item">
              <text class="contest-detail-page__countdown-num">{{ countdown.days }}</text>
              <text class="contest-detail-page__countdown-unit">天</text>
            </view>
            <text class="contest-detail-page__countdown-sep">:</text>
            <view class="contest-detail-page__countdown-item">
              <text class="contest-detail-page__countdown-num">{{ countdown.hours }}</text>
              <text class="contest-detail-page__countdown-unit">时</text>
            </view>
            <text class="contest-detail-page__countdown-sep">:</text>
            <view class="contest-detail-page__countdown-item">
              <text class="contest-detail-page__countdown-num">{{ countdown.minutes }}</text>
              <text class="contest-detail-page__countdown-unit">分</text>
            </view>
            <text class="contest-detail-page__countdown-sep">:</text>
            <view class="contest-detail-page__countdown-item">
              <text class="contest-detail-page__countdown-num">{{ countdown.seconds }}</text>
              <text class="contest-detail-page__countdown-unit">秒</text>
            </view>
          </view>
        </view>

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

        <!-- 主办方信息卡（B4.3） -->
        <view v-if="hasOrganizerCard" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">主办方</text>
          <view class="contest-detail-page__organizer">
            <image
              v-if="detail.organizerLogo"
              class="contest-detail-page__organizer-logo"
              :src="detail.organizerLogo"
              mode="aspectFill"
            />
            <view v-else class="contest-detail-page__organizer-logo contest-detail-page__organizer-logo--placeholder">
              <text>🏛</text>
            </view>
            <view class="contest-detail-page__organizer-body">
              <text v-if="detail.organizer" class="contest-detail-page__organizer-name">{{ detail.organizer }}</text>
              <text v-if="detail.coOrganizer" class="contest-detail-page__organizer-co">协办：{{ detail.coOrganizer }}</text>
              <text v-if="detail.organizerDesc" class="contest-detail-page__organizer-desc">{{ detail.organizerDesc }}</text>
            </view>
          </view>
          <view
            v-if="detail.eventAddress"
            class="contest-detail-page__organizer-address"
            @tap="copyAddress"
          >
            <text class="contest-detail-page__organizer-address-icon">📍</text>
            <text class="contest-detail-page__organizer-address-text">{{ detail.eventAddress }}</text>
            <text class="contest-detail-page__organizer-address-copy">复制</text>
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

        <!-- 奖项设置（B4.5：表格式布局） -->
        <view v-if="awardList.length" id="section-awards" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">奖项设置</text>
          <view class="contest-detail-page__award-table">
            <view class="contest-detail-page__award-row contest-detail-page__award-row--head">
              <text class="contest-detail-page__award-cell contest-detail-page__award-cell--lv">奖项</text>
              <text class="contest-detail-page__award-cell contest-detail-page__award-cell--qty">名额</text>
              <text class="contest-detail-page__award-cell contest-detail-page__award-cell--desc">奖励内容</text>
            </view>
            <view v-for="(award, ai) in awardList" :key="ai" class="contest-detail-page__award-row">
              <view class="contest-detail-page__award-cell contest-detail-page__award-cell--lv">
                <text class="contest-detail-page__award-icon">{{ getAwardIcon(award.level || award.name) }}</text>
                <text class="contest-detail-page__award-name">{{ award.name || award.level || '奖项' }}</text>
              </view>
              <text class="contest-detail-page__award-cell contest-detail-page__award-cell--qty">{{ award.quota || award.num || '--' }}</text>
              <text class="contest-detail-page__award-cell contest-detail-page__award-cell--desc">{{ award.description || award.desc || award.reward || '--' }}</text>
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

        <!-- 联系咨询卡（B4.4） -->
        <view v-if="hasContactCard" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">联系咨询</text>
          <view
            v-if="detail.contactPhone"
            class="contest-detail-page__contact-row"
            @tap="handleContactPhone"
          >
            <text class="contest-detail-page__contact-icon">📞</text>
            <view class="contest-detail-page__contact-body">
              <text class="contest-detail-page__contact-label">咨询电话</text>
              <text class="contest-detail-page__contact-value">{{ detail.contactPhone }}</text>
            </view>
            <text class="contest-detail-page__contact-action">拨打</text>
          </view>
          <view
            v-if="detail.contactWechat"
            class="contest-detail-page__contact-row"
            @tap="handleCopy(detail.contactWechat, '微信号已复制')"
          >
            <text class="contest-detail-page__contact-icon">💬</text>
            <view class="contest-detail-page__contact-body">
              <text class="contest-detail-page__contact-label">咨询微信</text>
              <text class="contest-detail-page__contact-value">{{ detail.contactWechat }}</text>
            </view>
            <text class="contest-detail-page__contact-action">复制</text>
          </view>
          <view
            v-if="detail.contactEmail"
            class="contest-detail-page__contact-row"
            @tap="handleCopy(detail.contactEmail, '邮箱已复制')"
          >
            <text class="contest-detail-page__contact-icon">✉️</text>
            <view class="contest-detail-page__contact-body">
              <text class="contest-detail-page__contact-label">咨询邮箱</text>
              <text class="contest-detail-page__contact-value">{{ detail.contactEmail }}</text>
            </view>
            <text class="contest-detail-page__contact-action">复制</text>
          </view>
        </view>

        <!-- 相似赛事（B4.6） -->
        <view v-if="relatedContests.length" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">相似赛事</text>
          <scroll-view scroll-x class="contest-detail-page__recommend-scroll" :show-scrollbar="false">
            <view class="contest-detail-page__recommend-inner">
              <view
                v-for="rc in relatedContests"
                :key="rc.contestId"
                class="contest-detail-page__recommend-card"
                @tap="openRelatedContest(rc.contestId)"
              >
                <image
                  class="contest-detail-page__recommend-cover"
                  :src="rc.coverImage || rc.bannerImage || ''"
                  mode="aspectFill"
                />
                <text class="contest-detail-page__recommend-name">{{ rc.contestName || '赛事' }}</text>
                <text class="contest-detail-page__recommend-meta">{{ rc.category || '' }}</text>
                <text class="contest-detail-page__recommend-price">{{ formatPrice(rc.price) }}</text>
              </view>
            </view>
          </scroll-view>
        </view>

        <!-- 推荐课程 -->
        <view v-if="relatedCourses.length" class="contest-detail-page__card jst-anim-fade-up">
          <text class="contest-detail-page__card-title">推荐课程</text>
          <scroll-view scroll-x class="contest-detail-page__recommend-scroll" :show-scrollbar="false">
            <view class="contest-detail-page__recommend-inner">
              <view
                v-for="cc in relatedCourses"
                :key="cc.courseId"
                class="contest-detail-page__recommend-card"
                @tap="openRelatedCourse(cc.courseId)"
              >
                <image
                  class="contest-detail-page__recommend-cover"
                  :src="cc.coverImage || ''"
                  mode="aspectFill"
                />
                <text class="contest-detail-page__recommend-name">{{ cc.courseName || '课程' }}</text>
                <text class="contest-detail-page__recommend-meta">{{ cc.teacher || cc.level || '' }}</text>
                <text class="contest-detail-page__recommend-price">{{ formatPrice(cc.price) }}</text>
              </view>
            </view>
          </scroll-view>
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
import { getContestDetail, getContestRecommend } from '@/api/contest'
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
import { throttle } from '@/utils/visual-effects'

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
      activeSection: 'section-info',
      // 倒计时
      countdown: { show: false, days: '0', hours: '00', minutes: '00', seconds: '00' },
      countdownTimer: null,
      // 推荐赛事/课程（B4.6）
      relatedContests: [],
      relatedCourses: [],
      // section 偏移缓存（onPageScroll 自动高亮用，避免频繁 query）
      sectionOffsets: {},
      scrollIntoTabId: ''
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
    // [visual-effect] 粘性导航：固定 5 tab，无数据置灰
    navSections() {
      return [
        { id: 'section-info', label: '基本信息', disabled: false },
        { id: 'section-intro', label: '赛事介绍', disabled: !this.detailDescription },
        { id: 'section-schedule', label: '赛程', disabled: !this.scheduleList.length },
        { id: 'section-awards', label: '奖项', disabled: !this.awardList.length },
        { id: 'section-faq', label: '常见问题', disabled: !this.faqList.length }
      ]
    },
    // B4.3 主办方卡显示条件：至少有 organizer / logo / desc / eventAddress 之一
    hasOrganizerCard() {
      if (!this.detail) return false
      return !!(this.detail.organizer || this.detail.organizerLogo || this.detail.organizerDesc || this.detail.coOrganizer || this.detail.eventAddress)
    },
    // B4.4 联系咨询卡显示条件
    hasContactCard() {
      if (!this.detail) return false
      return !!(this.detail.contactPhone || this.detail.contactWechat || this.detail.contactEmail)
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
  // [visual-effect] 滚动视差 + 自动高亮当前 section
  onPageScroll: throttle(function(e) {
    var scrollTop = e.scrollTop || 0
    this.heroScrollTop = scrollTop
    this.updateActiveSectionByScroll(scrollTop)
  }, 80),
  onUnload() {
    this.stopCountdown()
  },
  methods: {
    async fetchDetail() {
      if (!this.contestId) {
        this.detail = null
        return
      }

      this.pageLoading = true

      try {
        const response = await getContestDetail(this.contestId, { silent: true })
        // 中文注释: 保留原始后端字段（organizerLogo/contactPhone 等），再合并 card 规范化结果
        const card = normalizeContestCard(response || {})
        this.detail = Object.assign({}, response || {}, card)
        // 启动倒计时（报名进行中）
        this.startCountdown()
        // 异步并发拉推荐
        this.fetchRecommend()
        // 首屏后测量 section 偏移（等 DOM 渲染完成）
        this.$nextTick(() => {
          setTimeout(() => this.measureSectionOffsets(), 300)
        })
      } catch (error) {
        uni.showToast({ title: '加载失败，请重试', icon: 'none' })
        this.detail = null
      } finally {
        this.pageLoading = false
      }
    },

    // 中文注释: 拉推荐赛事和相关课程（B4.6），失败不影响主流程
    async fetchRecommend() {
      try {
        const res = await getContestRecommend(this.contestId, { silent: true })
        const data = res || {}
        this.relatedContests = Array.isArray(data.relatedContests) ? data.relatedContests : []
        this.relatedCourses = Array.isArray(data.relatedCourses) ? data.relatedCourses : []
      } catch (e) {
        this.relatedContests = []
        this.relatedCourses = []
      }
    },

    // 中文注释: 启动报名倒计时（B4.2）
    startCountdown() {
      this.stopCountdown()
      if (!this.detail || !this.detail.enrollEndTime) return
      const end = new Date(this.detail.enrollEndTime).getTime()
      if (!end || isNaN(end)) return
      const update = () => {
        const now = Date.now()
        const diff = end - now
        if (diff <= 0) {
          this.countdown = { show: false, days: '0', hours: '00', minutes: '00', seconds: '00' }
          this.stopCountdown()
          return
        }
        const d = Math.floor(diff / 86400000)
        const h = Math.floor((diff % 86400000) / 3600000)
        const m = Math.floor((diff % 3600000) / 60000)
        const s = Math.floor((diff % 60000) / 1000)
        const pad = (n) => (n < 10 ? '0' + n : '' + n)
        this.countdown = {
          show: true,
          days: String(d),
          hours: pad(h),
          minutes: pad(m),
          seconds: pad(s)
        }
      }
      update()
      this.countdownTimer = setInterval(update, 1000)
    },
    stopCountdown() {
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
        this.countdownTimer = null
      }
    },

    // 中文注释: 测量 5 个 section 的绝对位置，供滚动高亮用
    measureSectionOffsets() {
      const ids = ['section-info', 'section-intro', 'section-schedule', 'section-awards', 'section-faq']
      const query = uni.createSelectorQuery().in(this)
      ids.forEach((id) => {
        query.select('#' + id).boundingClientRect()
      })
      query.selectViewport().scrollOffset()
      query.exec((results) => {
        if (!results) return
        const viewport = results[results.length - 1] || { scrollTop: 0 }
        const baseScroll = viewport.scrollTop || 0
        const offsets = {}
        ids.forEach((id, idx) => {
          const rect = results[idx]
          if (rect && typeof rect.top === 'number') {
            // 相对于文档顶部的位置
            offsets[id] = rect.top + baseScroll
          }
        })
        this.sectionOffsets = offsets
      })
    },

    // 中文注释: 滚动时自动切换 activeSection
    updateActiveSectionByScroll(scrollTop) {
      const offsets = this.sectionOffsets
      const ids = ['section-info', 'section-intro', 'section-schedule', 'section-awards', 'section-faq']
      const anchor = scrollTop + 120 // sticky nav 高度偏移
      let current = this.activeSection
      for (let i = ids.length - 1; i >= 0; i--) {
        const id = ids[i]
        if (offsets[id] != null && anchor >= offsets[id]) {
          current = id
          break
        }
      }
      if (current !== this.activeSection) {
        this.activeSection = current
        // 让 tab bar 把当前项滚到可视范围
        this.scrollIntoTabId = 'tab-' + current
      }
    },

    // 中文注释: 跳转到推荐赛事/课程
    openRelatedContest(contestId) {
      if (!contestId) return
      uni.redirectTo({ url: '/pages-sub/contest/detail?id=' + contestId })
    },
    openRelatedCourse(courseId) {
      if (!courseId) return
      uni.navigateTo({ url: '/pages-sub/course/detail?id=' + courseId })
    },
    formatPrice(price) {
      const n = Number(price)
      if (!n && n !== 0) return '免费'
      if (n === 0) return '免费'
      return '¥' + n.toFixed(2)
    },

    // 中文注释: 联系咨询交互
    handleContactPhone() {
      if (!this.detail || !this.detail.contactPhone) return
      uni.makePhoneCall({
        phoneNumber: String(this.detail.contactPhone),
        fail: () => {}
      })
    },
    handleCopy(text, toastText) {
      if (!text) return
      uni.setClipboardData({
        data: String(text),
        success: () => {
          uni.showToast({ title: toastText || '已复制', icon: 'success' })
        }
      })
    },
    copyAddress() {
      if (!this.detail || !this.detail.eventAddress) return
      this.handleCopy(this.detail.eventAddress, '地址已复制')
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

    // [visual-effect] 锚点跳转（优先用缓存的 offset，失败回退实时测量）
    scrollToSection(sectionId) {
      this.activeSection = sectionId
      const cached = this.sectionOffsets[sectionId]
      if (cached != null) {
        uni.pageScrollTo({ scrollTop: Math.max(0, cached - 100), duration: 300 })
        return
      }
      var query = uni.createSelectorQuery().in(this)
      query.select('#' + sectionId).boundingClientRect((rect) => {
        if (rect) {
          uni.pageScrollTo({ scrollTop: rect.top - 100, duration: 300 })
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

.contest-detail-page__sticky-item--disabled {
  color: $jst-text-placeholder;
  opacity: 0.5;
}

// 倒计时条
.contest-detail-page__countdown {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $jst-space-lg;
  padding: $jst-space-md $jst-space-lg;
  border-radius: $jst-radius-card;
  background: $jst-brand-gradient;
  box-shadow: $jst-shadow-lift;
}

.contest-detail-page__countdown-label {
  font-size: $jst-font-sm;
  color: rgba(255, 255, 255, 0.9);
}

.contest-detail-page__countdown-timer {
  display: flex;
  align-items: baseline;
  gap: $jst-space-xs;
}

.contest-detail-page__countdown-item {
  display: inline-flex;
  align-items: baseline;
  gap: 2rpx;
}

.contest-detail-page__countdown-num {
  font-size: 36rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
  min-width: 40rpx;
  text-align: center;
}

.contest-detail-page__countdown-unit {
  font-size: $jst-font-xs;
  color: rgba(255, 255, 255, 0.85);
}

.contest-detail-page__countdown-sep {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.6);
}

// 主办方卡
.contest-detail-page__organizer {
  display: flex;
  align-items: flex-start;
  gap: $jst-space-md;
}

.contest-detail-page__organizer-logo {
  width: 96rpx;
  height: 96rpx;
  border-radius: $jst-radius-md;
  background: $jst-bg-grey;
  flex-shrink: 0;
}

.contest-detail-page__organizer-logo--placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  color: $jst-brand;
  background: $jst-brand-light;
}

.contest-detail-page__organizer-body {
  flex: 1;
  min-width: 0;
}

.contest-detail-page__organizer-name {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.contest-detail-page__organizer-co {
  display: block;
  margin-top: 6rpx;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.contest-detail-page__organizer-desc {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-text-regular;
}

.contest-detail-page__organizer-address {
  display: flex;
  align-items: center;
  gap: $jst-space-sm;
  margin-top: $jst-space-md;
  padding: $jst-space-sm $jst-space-md;
  border-radius: $jst-radius-sm;
  background: $jst-bg-grey;
}

.contest-detail-page__organizer-address-icon {
  font-size: $jst-font-base;
  flex-shrink: 0;
}

.contest-detail-page__organizer-address-text {
  flex: 1;
  font-size: $jst-font-sm;
  color: $jst-text-regular;
  line-height: 1.5;
}

.contest-detail-page__organizer-address-copy {
  font-size: $jst-font-xs;
  color: $jst-brand;
  flex-shrink: 0;
}

// 奖项表格
.contest-detail-page__award-table {
  border: 2rpx solid $jst-border;
  border-radius: $jst-radius-md;
  overflow: hidden;
}

.contest-detail-page__award-row {
  display: flex;
  align-items: center;
  min-height: 80rpx;
  border-top: 2rpx solid $jst-border;
}

.contest-detail-page__award-row:first-child {
  border-top: none;
}

.contest-detail-page__award-row--head {
  background: $jst-bg-grey;
  font-weight: $jst-weight-semibold;
}

.contest-detail-page__award-cell {
  padding: $jst-space-sm $jst-space-md;
  font-size: $jst-font-sm;
  color: $jst-text-primary;
  box-sizing: border-box;
}

.contest-detail-page__award-cell--lv {
  flex: 2;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.contest-detail-page__award-cell--qty {
  flex: 1;
  text-align: center;
  color: $jst-text-regular;
}

.contest-detail-page__award-cell--desc {
  flex: 3;
  color: $jst-text-regular;
  line-height: 1.5;
}

.contest-detail-page__award-row--head .contest-detail-page__award-cell {
  color: $jst-text-secondary;
  font-size: $jst-font-xs;
}

// 联系咨询
.contest-detail-page__contact-row {
  display: flex;
  align-items: center;
  gap: $jst-space-md;
  padding: $jst-space-md 0;
  border-top: 2rpx solid $jst-border;
}

.contest-detail-page__contact-row:first-of-type {
  border-top: none;
}

.contest-detail-page__contact-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: $jst-brand-light;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  flex-shrink: 0;
}

.contest-detail-page__contact-body {
  flex: 1;
  min-width: 0;
}

.contest-detail-page__contact-label {
  display: block;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.contest-detail-page__contact-value {
  display: block;
  margin-top: 4rpx;
  font-size: $jst-font-sm;
  color: $jst-text-primary;
  font-weight: $jst-weight-medium;
}

.contest-detail-page__contact-action {
  font-size: $jst-font-xs;
  color: $jst-brand;
  padding: 8rpx 18rpx;
  border-radius: $jst-radius-round;
  background: $jst-brand-light;
  flex-shrink: 0;
}

// 推荐赛事/课程
.contest-detail-page__recommend-scroll {
  white-space: nowrap;
  margin: 0 -#{$jst-space-md};
  padding: 0 $jst-space-md;
}

.contest-detail-page__recommend-inner {
  display: inline-flex;
  gap: $jst-space-md;
}

.contest-detail-page__recommend-card {
  display: inline-flex;
  flex-direction: column;
  width: 260rpx;
  padding: $jst-space-sm;
  border-radius: $jst-radius-md;
  background: $jst-bg-grey;
  flex-shrink: 0;
  transition: transform $jst-duration-fast $jst-easing;
  &:active { transform: scale(0.97); }
}

.contest-detail-page__recommend-cover {
  width: 100%;
  height: 160rpx;
  border-radius: $jst-radius-sm;
  background: $jst-border;
}

.contest-detail-page__recommend-name {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.contest-detail-page__recommend-meta {
  display: block;
  margin-top: 4rpx;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.contest-detail-page__recommend-price {
  display: block;
  margin-top: 6rpx;
  font-size: $jst-font-sm;
  color: $jst-brand;
  font-weight: $jst-weight-bold;
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
