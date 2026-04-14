<template>
  <view class="my-page">
    <jst-loading :loading="pageLoading" text="资料加载中..." />

    <!-- 骨架屏 -->
    <view v-if="skeletonShow && !pageLoading" class="my-page__skeleton">
      <view class="my-page__skeleton-hero"></view>
      <view class="my-page__skeleton-summary">
        <u-skeleton rows="1" rows-width="100%" rows-height="80rpx" :loading="true" :animate="true"></u-skeleton>
      </view>
      <view v-for="i in 3" :key="i" class="my-page__skeleton-section">
        <u-skeleton rows="3" :loading="true" :animate="true"></u-skeleton>
      </view>
    </view>

    <template v-if="!skeletonShow">
      <!-- ===== Hero ===== -->
      <view class="my-page__hero" :style="{ paddingTop: navPaddingTop }">
        <view class="my-page__hero-orb"></view>
        <view class="my-page__hero-inner">
          <view class="my-page__profile-row">
            <view class="my-page__avatar">
              <image v-if="profile.avatar" class="my-page__avatar-image" :src="profile.avatar" mode="aspectFill" />
              <text v-else class="my-page__avatar-text">{{ getAvatarText() }}</text>
            </view>
            <view class="my-page__profile-main">
              <text class="my-page__name">{{ profile.nickname || '未设置昵称' }}</text>
              <text class="my-page__mobile">{{ profile.mobileMasked || '未绑定手机号' }}</text>
            </view>
            <view class="my-page__edit-btn" @tap="navigateProfileEdit">
              <u-icon name="edit-pen" color="#fff" size="32rpx" />
            </view>
          </view>
          <!-- 已认证渠道用户: 保留顶部切换器 -->
          <view v-if="isChannelUser" class="my-page__switcher">
            <view :class="['my-page__switcher-item', currentView === 'student' ? 'my-page__switcher-item--active' : '']" @tap="switchView('student')">学生</view>
            <view :class="['my-page__switcher-item', currentView === 'channel' ? 'my-page__switcher-item--active' : '']" @tap="switchView('channel')">渠道方</view>
          </view>
        </view>
      </view>

      <!-- ===== Summary 4格 ===== -->
      <view class="my-page__summary">
        <view class="my-page__summary-item" @tap="navigateMyEnroll"><text class="my-page__summary-num">{{ animatedSummary.enroll }}</text><text class="my-page__summary-label">报名记录</text></view>
        <view class="my-page__summary-item" @tap="navigateMyScore"><text class="my-page__summary-num">{{ animatedSummary.score }}</text><text class="my-page__summary-label">我的成绩</text></view>
        <view class="my-page__summary-item" @tap="navigateMyCert"><text class="my-page__summary-num">{{ animatedSummary.cert }}</text><text class="my-page__summary-label">我的证书</text></view>
        <view class="my-page__summary-item" @tap="navigateMyCourse"><text class="my-page__summary-num">{{ animatedSummary.course }}</text><text class="my-page__summary-label">我的课程</text></view>
      </view>

      <!-- ===== 等级卡 ===== -->
      <view class="my-page__level-card" @tap="navigatePointsCenter">
        <view class="my-page__level-header">
          <text class="my-page__level-badge">{{ profile.levelName || ('Lv.' + (profile.currentLevelId || 0)) }}</text>
          <view class="my-page__level-progress">
            <text class="my-page__level-progress-text">成长值进度 · 距下一级还差 {{ nextLevelGap }}</text>
            <view class="my-page__level-bar"><view class="my-page__level-bar-fill" :style="{ width: levelProgress + '%' }"></view></view>
          </view>
        </view>
        <view class="my-page__level-stats">
          <view class="my-page__level-stat"><text class="my-page__level-stat-num">{{ profile.availablePoints || 0 }}</text><text class="my-page__level-stat-label">可用积分</text></view>
          <view class="my-page__level-stat"><text class="my-page__level-stat-num">{{ profile.frozenPoints || 0 }}</text><text class="my-page__level-stat-label">冻结积分</text></view>
          <view class="my-page__level-stat"><text class="my-page__level-stat-num">{{ profile.totalPoints || 0 }}</text><text class="my-page__level-stat-label">累计获取</text></view>
        </view>
      </view>

      <!-- ===== 快捷标签 ===== -->
      <view class="my-page__quick-tags">
        <view class="my-page__quick-tag" @tap="navigateMall">🎁 积分商城</view>
        <view class="my-page__quick-tag" @tap="navigatePointsCenter">📊 积分明细</view>
        <view class="my-page__quick-tag" @tap="navigateCouponCenter">🎫 我的优惠券</view>
        <view class="my-page__quick-tag" @tap="navigateRightsCenter">🎖 我的权益</view>
      </view>

      <!-- ===== 渠道绑定卡 (已绑定时显示) ===== -->
      <view v-if="profile.channelBinding && profile.channelBinding.name" class="my-page__binding-card" @tap="navigateBinding">
        <view class="my-page__binding-dot"></view>
        <view class="my-page__binding-body">
          <text class="my-page__binding-title">已绑定：{{ profile.channelBinding.name }}（渠道方）</text>
          <text class="my-page__binding-desc">{{ profile.channelBinding.school || '' }}{{ profile.channelBinding.remark ? (' · ' + profile.channelBinding.remark) : '' }}</text>
        </view>
        <u-tag text="已绑定" type="primary" plain :plain-fill="true" size="mini" shape="circle" />
      </view>

      <!-- ===== 渠道方视角 ===== -->
      <template v-if="currentView === 'channel' && isChannelUser">
        <view class="my-page__section">
          <view class="my-page__cell" @tap="navigateChannelApplyStatus">
            <view class="my-page__cell-icon my-page__cell-icon--green">{{ channelAuthIcon }}</view>
            <view class="my-page__cell-body">
              <text class="my-page__cell-title">渠道认证</text>
              <text class="my-page__cell-desc">{{ channelAuthText }}</text>
            </view>
            <text class="my-page__cell-arrow">›</text>
          </view>
        </view>

        <view class="my-page__section">
          <view class="my-page__workbench-btn" @tap="navigateChannelHome">
            <text class="my-page__workbench-icon">🏢</text>
            <text class="my-page__workbench-text">进入我的工作台</text>
            <text class="my-page__workbench-arrow">›</text>
          </view>
        </view>

        <view class="my-page__section">
          <text class="my-page__section-title">快捷入口</text>
          <view class="my-page__grid">
            <view class="my-page__grid-item" @tap="navigateChannelStudents"><view class="my-page__grid-icon my-page__grid-icon--teal">👥</view><text class="my-page__grid-text">学生管理</text></view>
            <view class="my-page__grid-item" @tap="navigateChannelOrders"><view class="my-page__grid-icon my-page__grid-icon--orange">🧾</view><text class="my-page__grid-text">渠道订单</text></view>
            <view class="my-page__grid-item" @tap="navigateChannelRebate"><view class="my-page__grid-icon my-page__grid-icon--gold">💰</view><text class="my-page__grid-text">返点中心</text></view>
            <view class="my-page__grid-item" @tap="navigateChannelWithdrawList"><view class="my-page__grid-icon my-page__grid-icon--purple">📄</view><text class="my-page__grid-text">提现结算</text></view>
          </view>
        </view>

        <!-- 基础管理 -->
        <view class="my-page__section">
          <text class="my-page__section-title">基础管理</text>
          <view class="my-page__card">
            <view class="my-page__cell" @tap="navigateChannelStudents"><view class="my-page__cell-icon my-page__cell-icon--teal">👥</view><view class="my-page__cell-body"><text class="my-page__cell-title">学生管理</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateChannelOrders"><view class="my-page__cell-icon my-page__cell-icon--orange">📋</view><view class="my-page__cell-body"><text class="my-page__cell-title">订单管理</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateChannelRebate"><view class="my-page__cell-icon my-page__cell-icon--gold">💰</view><view class="my-page__cell-body"><text class="my-page__cell-title">返点中心</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateChannelWithdrawList"><view class="my-page__cell-icon my-page__cell-icon--blue">🏦</view><view class="my-page__cell-body"><text class="my-page__cell-title">提现结算</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateChannelData"><view class="my-page__cell-icon my-page__cell-icon--purple">📊</view><view class="my-page__cell-body"><text class="my-page__cell-title">经营分析</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateContractList"><view class="my-page__cell-icon my-page__cell-icon--blue">📄</view><view class="my-page__cell-body"><text class="my-page__cell-title">合同中心</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateInvoiceList"><view class="my-page__cell-icon my-page__cell-icon--orange">🧾</view><view class="my-page__cell-body"><text class="my-page__cell-title">开票中心</text></view><text class="my-page__cell-arrow">›</text></view>
          </view>
        </view>

        <!-- 其他 -->
        <view class="my-page__section">
          <view class="my-page__card">
            <view class="my-page__cell" @tap="navigateChannelApplyStatus"><view class="my-page__cell-icon my-page__cell-icon--teal">✅</view><view class="my-page__cell-body"><text class="my-page__cell-title">认证信息</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateHelp"><view class="my-page__cell-icon my-page__cell-icon--blue">💬</view><view class="my-page__cell-body"><text class="my-page__cell-title">客服与帮助</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateSettings"><view class="my-page__cell-icon my-page__cell-icon--gray">⚙️</view><view class="my-page__cell-body"><text class="my-page__cell-title">设置</text></view><text class="my-page__cell-arrow">›</text></view>
          </view>
        </view>
      </template>

      <!-- ===== 学生视角 ===== -->
      <template v-if="currentView === 'student'">
        <!-- 扫码核销 (partner/op only) -->
        <view v-if="isPartnerOrOp" class="my-page__section">
          <text class="my-page__section-title">核销工作台</text>
          <view class="my-page__card">
            <view class="my-page__cell" @tap="navigateScan"><view class="my-page__cell-icon my-page__cell-icon--purple">📷</view><view class="my-page__cell-body"><text class="my-page__cell-title">扫码核销</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateWriteoffRecord"><view class="my-page__cell-icon my-page__cell-icon--blue">📜</view><view class="my-page__cell-body"><text class="my-page__cell-title">核销记录</text><text class="my-page__cell-desc">查看历史权益核销记录</text></view><text class="my-page__cell-arrow">›</text></view>
          </view>
        </view>

        <!-- 我的服务 -->
        <view class="my-page__section">
          <text class="my-page__section-title">我的服务</text>
          <view class="my-page__card">
            <view class="my-page__cell" @tap="navigateMyEnroll"><view class="my-page__cell-icon my-page__cell-icon--orange">📝</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的报名</text><text class="my-page__cell-desc">查看报名进度与审核状态</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateOrderList"><view class="my-page__cell-icon my-page__cell-icon--blue">📦</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的订单</text><text class="my-page__cell-desc">赛事订单 & 课程订单</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateRefundList"><view class="my-page__cell-icon my-page__cell-icon--red">↩️</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的退款</text><text class="my-page__cell-desc">退款进度查询</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateMyScore"><view class="my-page__cell-icon my-page__cell-icon--gold">🏆</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的成绩</text><text class="my-page__cell-desc">查看参赛成绩与排名</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateMyCert"><view class="my-page__cell-icon my-page__cell-icon--green">🏅</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的证书</text><text class="my-page__cell-desc">查看与下载获奖证书</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateMyCourse"><view class="my-page__cell-icon my-page__cell-icon--teal">📚</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的课程</text><text class="my-page__cell-desc">课程学习中</text></view><text class="my-page__cell-arrow">›</text></view>
          </view>
        </view>

        <!-- 我的权益 -->
        <view class="my-page__section">
          <text class="my-page__section-title">我的权益</text>
          <view class="my-page__card">
            <view class="my-page__cell" @tap="navigatePointsCenter"><view class="my-page__cell-icon my-page__cell-icon--brand">💎</view><view class="my-page__cell-body"><text class="my-page__cell-title">积分中心</text><text class="my-page__cell-desc">等级权益，可用积分 {{ profile.availablePoints || 0 }}</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateMall"><view class="my-page__cell-icon my-page__cell-icon--gold">🎁</view><view class="my-page__cell-body"><text class="my-page__cell-title">积分商城</text><text class="my-page__cell-desc">兑换实物与虚拟权益</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateCouponCenter"><view class="my-page__cell-icon my-page__cell-icon--red">🎫</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的优惠券</text><text v-if="profile.couponCount" class="my-page__cell-desc">{{ profile.couponCount }} 张可用</text></view><view v-if="profile.couponCount" class="my-page__cell-badge">{{ profile.couponCount > 99 ? '99+' : profile.couponCount }}</view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateRightsCenter"><view class="my-page__cell-icon my-page__cell-icon--purple">🏅</view><view class="my-page__cell-body"><text class="my-page__cell-title">专属权益</text><text class="my-page__cell-desc">课程减免 · 客服特权</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateWriteoffRecord"><view class="my-page__cell-icon my-page__cell-icon--teal">📋</view><view class="my-page__cell-body"><text class="my-page__cell-title">核销记录</text><text class="my-page__cell-desc">查看历史权益核销记录</text></view><text class="my-page__cell-arrow">›</text></view>
          </view>
        </view>

        <!-- 消息与互动 -->
        <view class="my-page__section">
          <text class="my-page__section-title">消息与互动</text>
          <view class="my-page__card">
            <view class="my-page__cell" @tap="navigateMessage"><view class="my-page__cell-icon my-page__cell-icon--orange">🔔</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的消息</text><text class="my-page__cell-desc">订单/积分/权益/物流消息</text></view><view v-if="profile.unreadMsgCount" class="my-page__cell-badge">{{ profile.unreadMsgCount > 99 ? '99+' : profile.unreadMsgCount }}</view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateBinding"><view class="my-page__cell-icon my-page__cell-icon--teal">🔗</view><view class="my-page__cell-body"><text class="my-page__cell-title">绑定关系</text><text class="my-page__cell-desc">管理与渠道方的绑定</text></view><text class="my-page__cell-arrow">›</text></view>
          </view>
        </view>

        <!-- 非渠道用户: 申请渠道方独立入口 -->
        <view v-if="!isChannelUser" class="my-page__section">
          <view class="my-page__card">
            <view class="my-page__cell" @tap="navigateChannelApply">
              <view class="my-page__cell-icon my-page__cell-icon--brand">🏢</view>
              <view class="my-page__cell-body">
                <text class="my-page__cell-title">申请成为渠道方</text>
                <text class="my-page__cell-desc">成为渠道方，获取返点收益</text>
              </view>
              <text class="my-page__cell-arrow">›</text>
            </view>
          </view>
        </view>

        <!-- 帮助与设置 -->
        <view class="my-page__section">
          <text class="my-page__section-title">帮助与设置</text>
          <view class="my-page__card">
            <view class="my-page__cell" @tap="navigateProfileEdit"><view class="my-page__cell-icon my-page__cell-icon--blue">👤</view><view class="my-page__cell-body"><text class="my-page__cell-title">个人资料</text><text class="my-page__cell-desc">头像、昵称、学校、年级</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateParticipant"><view class="my-page__cell-icon my-page__cell-icon--orange">📁</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的档案</text><text class="my-page__cell-desc">参赛人信息管理</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateAddressList"><view class="my-page__cell-icon my-page__cell-icon--gray">📍</view><view class="my-page__cell-body"><text class="my-page__cell-title">收货地址</text><text class="my-page__cell-desc">地址管理</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateAppointmentList"><view class="my-page__cell-icon my-page__cell-icon--purple">📅</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的预约</text><text class="my-page__cell-desc">个人预约记录</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateHelp"><view class="my-page__cell-icon my-page__cell-icon--blue">💬</view><view class="my-page__cell-body"><text class="my-page__cell-title">客服与帮助</text><text class="my-page__cell-desc">常见问题 & 在线咨询</text></view><text class="my-page__cell-arrow">›</text></view>
            <view class="my-page__cell" @tap="navigateSettings"><view class="my-page__cell-icon my-page__cell-icon--gray">⚙️</view><view class="my-page__cell-body"><text class="my-page__cell-title">设置</text><text class="my-page__cell-desc">消息提醒 & 账号安全</text></view><text class="my-page__cell-arrow">›</text></view>
          </view>
        </view>
      </template>

      <u-button
        type="error"
        text="退出登录"
        plain
        :custom-style="logoutStyle"
        @click="handleLogout"
      />
    </template>
  </view>
</template>

<script>
import { useUserStore } from '@/store/user'
import { pinia } from '@/store/index'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
// [visual-effect]
import { countUp } from '@/utils/visual-effects'

export default {
  components: { JstLoading },
  data() {
    return {
      pageLoading: false, moreExpanded: false, currentView: 'student',
      profile: { nickname: '', avatar: '', mobileMasked: '', currentLevelId: 0, availablePoints: 0, totalPoints: 0, growthValue: 0, userType: '' },
      skeletonShow: true, // [visual-effect]
      animatedSummary: { enroll: 0, score: 0, cert: 0, course: 0 }, // [visual-effect]
      summaryAnimCancels: [], // [visual-effect]
      logoutStyle: { margin: '48rpx 32rpx 0' } // [visual-effect] u-button custom style
    }
  },
  computed: {
    // 渠道方视角: userInfo.userType === 'channel' 或 roles 包含 jst_channel
    isChannelUser() {
      const store = useUserStore(pinia)
      const info = store.userInfo || {}
      if (info.userType === 'channel') return true
      const roles = store.roles || []
      return Array.isArray(roles) && roles.includes('jst_channel')
    },
    // POLISH-D: 赛事方/平台运营权限, 用于扫码核销入口
    isPartnerOrOp() {
      const store = useUserStore(pinia)
      const roles = store.roles || []
      return Array.isArray(roles) && (roles.includes('jst_partner') || roles.includes('jst_platform_op'))
    },
    // 渠道认证状态图标
    channelAuthIcon() {
      const store = useUserStore(pinia)
      const info = store.userInfo || {}
      if (info.channelAuthStatus === 'approved') return '✅'
      if (info.channelAuthStatus === 'pending') return '⏳'
      if (info.channelAuthStatus === 'rejected') return '❌'
      return '✅'
    },
    // 渠道认证状态文字
    channelAuthText() {
      const store = useUserStore(pinia)
      const info = store.userInfo || {}
      if (info.channelAuthStatus === 'approved') return '已通过认证'
      if (info.channelAuthStatus === 'pending') return '审核中'
      if (info.channelAuthStatus === 'rejected') return '审核驳回，请重新提交'
      return '已通过认证'
    },
    // 等级进度百分比
    levelProgress() {
      const g = this.profile.growthValue || 0
      const next = this.profile.nextLevelGrowth || 100
      return Math.min(100, Math.round(g / next * 100))
    },
    // 距下一级差值
    nextLevelGap() {
      const g = this.profile.growthValue || 0
      const next = this.profile.nextLevelGrowth || 100
      return Math.max(0, next - g)
    }
  },
  onShow() { this.ensureLogin(); this.loadProfile(); this.initView() },
  // [visual-effect] start
  beforeDestroy() {
    this.summaryAnimCancels.forEach(fn => fn && fn())
  },
  // [visual-effect] end
  methods: {
    ensureLogin() { const userStore = useUserStore(pinia); if (!userStore.token) uni.reLaunch({ url: '/pages/login/login' }) },
    // 初始化默认视角: 渠道用户默认渠道方视角
    initView() { if (this.isChannelUser) { this.currentView = 'channel' } },
    // 切换视角
    switchView(v) { this.currentView = v },
    navigateChannelApplyStatus() { uni.navigateTo({ url: '/pages-sub/channel/apply-status' }) },
    async loadProfile() {
      const userStore = useUserStore(pinia); if (!userStore.token) return; this.pageLoading = true
      try {
        const profile = await userStore.fetchProfile(); this.profile = this.normalizeProfile(profile || {})
        // [visual-effect] start
        this.skeletonShow = false
        this.animateSummary()
        // [visual-effect] end
      } finally { this.pageLoading = false }
    },
    // [visual-effect]
    animateSummary() {
      this.summaryAnimCancels.forEach(fn => fn && fn())
      this.summaryAnimCancels = [
        countUp(0, this.profile.enrollCount || 0, 600, v => { this.animatedSummary.enroll = v }),
        countUp(0, this.profile.scoreCount || 0, 600, v => { this.animatedSummary.score = v }),
        countUp(0, this.profile.certCount || 0, 600, v => { this.animatedSummary.cert = v }),
        countUp(0, this.profile.courseCount || 0, 600, v => { this.animatedSummary.course = v })
      ]
    },
    normalizeProfile(profile) { return { nickname: profile.nickname || '', avatar: profile.avatar || '', mobileMasked: profile.mobileMasked || '', currentLevelId: profile.currentLevelId || 0, availablePoints: profile.availablePoints || 0, totalPoints: profile.totalPoints || 0, growthValue: profile.growthValue || 0, enrollCount: profile.enrollCount || 0, scoreCount: profile.scoreCount || 0, certCount: profile.certCount || 0, courseCount: profile.courseCount || 0, frozenPoints: profile.frozenPoints || 0, levelName: profile.levelName || '', couponCount: profile.couponCount || 0, unreadMsgCount: profile.unreadMsgCount || 0, channelBinding: profile.channelBinding || null, nextLevelGrowth: profile.nextLevelGrowth || 100 } },
    getAvatarText() { return (this.profile.nickname || '竞').slice(0, 1) },
    navigateProfileEdit() { uni.navigateTo({ url: '/pages-sub/my/profile-edit' }) },
    navigateParticipant() { uni.navigateTo({ url: '/pages-sub/my/participant' }) },
    navigateMyEnroll() { uni.navigateTo({ url: '/pages-sub/my/enroll' }) },
    navigateOrderList() { uni.navigateTo({ url: '/pages-sub/my/order-list' }) },
    navigateRefundList() { uni.navigateTo({ url: '/pages-sub/my/refund-list' }) },
    navigateBinding() { uni.navigateTo({ url: '/pages-sub/my/binding' }) },
    navigateAddressList() { uni.navigateTo({ url: '/pages-sub/my/address-list' }) },
    navigateMyCourse() { uni.navigateTo({ url: '/pages-sub/my/course' }) },
    navigateChannelHome() { uni.navigateTo({ url: '/pages-sub/channel/home' }) },
    navigateChannelRebate() { uni.navigateTo({ url: '/pages-sub/channel/rebate' }) },
    navigateChannelWithdrawList() { uni.navigateTo({ url: '/pages-sub/channel/withdraw-list' }) },
    navigateChannelStudents() { uni.navigateTo({ url: '/pages-sub/channel/students' }) },
    navigateChannelOrders() { uni.navigateTo({ url: '/pages-sub/channel/orders' }) },
    navigateChannelData() { uni.navigateTo({ url: '/pages-sub/channel/data' }) },
    navigateContractList() { uni.navigateTo({ url: '/pages-sub/channel/contract-list' }) },
    navigateInvoiceList() { uni.navigateTo({ url: '/pages-sub/channel/invoice-list' }) },
    navigateAppointmentList() { uni.navigateTo({ url: '/pages-sub/appointment/my-list' }) },
    navigateMall() { uni.navigateTo({ url: '/pages-sub/mall/list' }) },
    navigatePointsCenter() { uni.navigateTo({ url: '/pages-sub/points/center' }) },
    navigateCouponCenter() { uni.navigateTo({ url: '/pages-sub/coupon/center' }) },
    navigateRightsCenter() { uni.navigateTo({ url: '/pages-sub/rights/center' }) },
    navigateChannelApply() { uni.navigateTo({ url: '/pages-sub/channel/apply-entry' }) },
    navigateScan() { uni.navigateTo({ url: '/pages-sub/appointment/scan' }) },
    navigateWriteoffRecord() { uni.navigateTo({ url: '/pages-sub/appointment/writeoff-record' }) },
    navigateCourseTab() { uni.switchTab({ url: '/pages/course/list' }) },
    navigateMyScore() { uni.navigateTo({ url: '/pages-sub/my/score' }) },
    navigateMyCert() { uni.navigateTo({ url: '/pages-sub/my/cert' }) },
    navigateMessage() { uni.navigateTo({ url: '/pages-sub/my/message' }) },
    navigateHelp() { uni.navigateTo({ url: '/pages-sub/public/help' }) },
    navigateSettings() { uni.navigateTo({ url: '/pages-sub/my/settings' }) },
    handleLogout() { uni.showModal({ title: '退出登录', content: '确认退出当前账号？', success: async (res) => { if (!res.confirm) return; const userStore = useUserStore(pinia); await userStore.doLogout(); uni.reLaunch({ url: '/pages/login/login' }) } }) }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

/* === Page === */
.my-page { min-height: 100vh; padding-bottom: calc(60rpx + env(safe-area-inset-bottom)); background: $jst-bg-subtle; }

/* === 骨架屏 === */
.my-page__skeleton-hero { height: 280rpx; background: $jst-hero-gradient; border-bottom-left-radius: $jst-radius-xl; border-bottom-right-radius: $jst-radius-xl; }
.my-page__skeleton-summary { margin: -64rpx $jst-space-xl 0; padding: $jst-space-lg; border-radius: $jst-radius-lg; background: $jst-bg-card; box-shadow: $jst-shadow-sm; }
.my-page__skeleton-section { margin: $jst-space-lg $jst-space-xl 0; padding: $jst-space-lg; border-radius: $jst-radius-lg; background: $jst-bg-card; }

/* === Hero === */
.my-page__hero { position: relative; padding: 80rpx $jst-space-xl 120rpx; background: $jst-hero-gradient; border-bottom-left-radius: $jst-radius-xl; border-bottom-right-radius: $jst-radius-xl; overflow: hidden; }
.my-page__hero-orb { position: absolute; top: -100rpx; right: -60rpx; width: 300rpx; height: 300rpx; border-radius: 50%; background: rgba(255, 255, 255, 0.06); filter: blur(30px); }
.my-page__hero-inner { position: relative; z-index: 1; }
.my-page__profile-row { display: flex; align-items: center; }
.my-page__avatar { display: flex; align-items: center; justify-content: center; width: 112rpx; height: 112rpx; border-radius: 50%; background: $jst-brand-gradient; border: 4rpx solid rgba(255, 255, 255, 0.3); overflow: hidden; }
.my-page__avatar-image { width: 100%; height: 100%; }
.my-page__avatar-text { font-size: $jst-font-xxl; font-weight: $jst-weight-semibold; color: $jst-text-inverse; }
.my-page__profile-main { flex: 1; margin-left: $jst-space-lg; }
.my-page__name { display: block; max-width: 320rpx; font-size: $jst-font-xl; font-weight: $jst-weight-semibold; color: $jst-text-inverse; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.my-page__mobile { display: block; margin-top: $jst-space-xs; font-size: $jst-font-sm; color: rgba(255, 255, 255, 0.7); }
.my-page__edit-btn { display: flex; align-items: center; justify-content: center; width: 68rpx; height: 68rpx; border-radius: 50%; background: rgba(255, 255, 255, 0.18); flex-shrink: 0; transition: transform $jst-duration-fast $jst-easing; }
.my-page__edit-btn:active { transform: scale(0.9); background: rgba(255, 255, 255, 0.28); }
.my-page__switcher { display: inline-flex; margin-top: $jst-space-lg; padding: 6rpx; border-radius: $jst-radius-round; background: rgba(255, 255, 255, 0.15); }
.my-page__switcher-item { min-width: 120rpx; height: 56rpx; border-radius: $jst-radius-round; font-size: $jst-font-sm; line-height: 56rpx; text-align: center; color: rgba(255, 255, 255, 0.7); transition: all $jst-duration-fast $jst-easing; }
.my-page__switcher-item--active { background: $jst-bg-card; color: $jst-brand-dark; font-weight: $jst-weight-semibold; }

/* === Summary 4格 === */
.my-page__summary { display: flex; margin: -64rpx $jst-space-xl 0; padding: $jst-space-lg 0; border-radius: $jst-radius-card; background: $jst-bg-card; box-shadow: $jst-shadow-ring, $jst-shadow-ambient, $jst-shadow-lift; position: relative; z-index: 2; }
.my-page__summary-item { flex: 1; text-align: center; transition: transform $jst-duration-fast $jst-easing; }
.my-page__summary-item:active { transform: scale(0.95); }
.my-page__summary-num { display: block; font-size: $jst-font-xxl; font-weight: $jst-weight-bold; color: $jst-text-primary; font-feature-settings: "tnum"; font-variant-numeric: tabular-nums; }
.my-page__summary-label { display: block; margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }

/* === 等级卡 === */
.my-page__level-card { margin: $jst-space-lg $jst-space-xl 0; padding: $jst-space-lg; border-radius: $jst-radius-card; background: $jst-hero-gradient-dark; border: 1rpx solid rgba(255, 255, 255, 0.08); box-shadow: $jst-shadow-ring, $jst-shadow-lift; transition: transform $jst-duration-fast $jst-easing; }
.my-page__level-card:active { transform: scale(0.99); }
.my-page__level-header { display: flex; align-items: flex-start; gap: $jst-space-lg; }
.my-page__level-badge { flex-shrink: 0; padding: $jst-space-xs $jst-space-lg; border-radius: $jst-radius-round; background: rgba(255, 200, 100, 0.25); font-size: $jst-font-sm; font-weight: $jst-weight-semibold; color: $jst-gold; }
.my-page__level-progress { flex: 1; }
.my-page__level-progress-text { display: block; font-size: $jst-font-xs; color: rgba(255, 255, 255, 0.7); margin-bottom: $jst-space-sm; }
.my-page__level-bar { height: 10rpx; border-radius: 5rpx; background: rgba(255, 255, 255, 0.18); overflow: hidden; }
.my-page__level-bar-fill { height: 100%; border-radius: 5rpx; background: linear-gradient(90deg, $jst-gold, darken($jst-gold, 10%)); transition: width 0.8s $jst-easing; }
.my-page__level-stats { display: flex; margin-top: $jst-space-lg; padding-top: $jst-space-lg; border-top: 2rpx solid rgba(255, 255, 255, 0.1); }
.my-page__level-stat { flex: 1; text-align: center; }
.my-page__level-stat-num { display: block; font-size: $jst-font-xl; font-weight: $jst-weight-semibold; color: $jst-text-inverse; font-feature-settings: "tnum"; font-variant-numeric: tabular-nums; }
.my-page__level-stat-label { display: block; margin-top: 6rpx; font-size: $jst-font-xs; color: rgba(255, 255, 255, 0.6); }

/* === 快捷标签 === */
.my-page__quick-tags { display: flex; gap: $jst-space-sm; margin: $jst-space-lg $jst-space-xl 0; flex-wrap: wrap; }
.my-page__quick-tag { padding: $jst-space-xs 22rpx; border-radius: $jst-radius-round; background: $jst-brand-light; font-size: $jst-font-xs; font-weight: $jst-weight-medium; color: $jst-brand; transition: transform $jst-duration-fast $jst-easing; }
.my-page__quick-tag:active { transform: scale(0.95); }

/* === Section === */
.my-page__section { margin: $jst-space-xl $jst-space-xl 0; }
.my-page__section-title { display: block; margin-bottom: $jst-space-md; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }

/* === Card container === */
.my-page__card { border-radius: $jst-radius-card; background: $jst-bg-card; box-shadow: $jst-shadow-ring, $jst-shadow-ambient; overflow: hidden; }

/* === Cell (list item) === */
.my-page__cell { display: flex; align-items: center; gap: $jst-space-lg; padding: $jst-space-lg; border-bottom: 1rpx solid $jst-border; transition: background $jst-duration-fast $jst-easing; }
.my-page__cell:last-child { border-bottom: none; }
.my-page__cell:active { background: $jst-bg-grey; }
.my-page__cell-icon { display: flex; align-items: center; justify-content: center; width: 64rpx; height: 64rpx; border-radius: $jst-radius-lg; font-size: $jst-font-base; flex-shrink: 0; }
.my-page__cell-icon--blue { background: $jst-brand-light; }
.my-page__cell-icon--orange { background: $jst-warning-light; }
.my-page__cell-icon--teal { background: $jst-success-light; }
.my-page__cell-icon--gold { background: $jst-warning-light; }
.my-page__cell-icon--purple { background: $jst-info-light; }
.my-page__cell-icon--green { background: $jst-success-light; }
.my-page__cell-icon--gray { background: $jst-bg-grey; }
.my-page__cell-icon--brand { background: $jst-brand-light; }
.my-page__cell-icon--red { background: $jst-danger-light; }
.my-page__cell-body { flex: 1; min-width: 0; }
.my-page__cell-title { display: block; font-size: $jst-font-base; font-weight: $jst-weight-medium; color: $jst-text-primary; }
.my-page__cell-desc { display: block; margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }
.my-page__cell-arrow { font-size: $jst-font-base; color: $jst-text-placeholder; flex-shrink: 0; }
.my-page__cell-soon { flex-shrink: 0; padding: $jst-space-xs 14rpx; border-radius: $jst-radius-round; background: $jst-bg-grey; font-size: $jst-font-xs; color: $jst-text-secondary; }
.my-page__cell-badge { flex-shrink: 0; min-width: $jst-space-xl; height: $jst-space-xl; padding: 0 $jst-space-xs; border-radius: $jst-radius-round; background: $jst-danger; color: $jst-text-inverse; font-size: $jst-font-xs; font-weight: $jst-weight-semibold; text-align: center; line-height: $jst-space-xl; }
.my-page__cell--disabled { opacity: 0.55; }

/* === 绑定卡 === */
.my-page__binding-card { display: flex; align-items: center; gap: $jst-space-md; margin: $jst-space-lg $jst-space-xl 0; padding: $jst-space-lg; border-radius: $jst-radius-card; background: $jst-bg-card; box-shadow: $jst-shadow-ring, $jst-shadow-ambient; border-left: 6rpx solid $jst-brand; transition: transform $jst-duration-fast $jst-easing; }
.my-page__binding-card:active { transform: scale(0.99); }
.my-page__binding-dot { width: 12rpx; height: 12rpx; border-radius: 50%; background: $jst-success; flex-shrink: 0; }
.my-page__binding-body { flex: 1; min-width: 0; }
.my-page__binding-title { display: block; font-size: $jst-font-sm; font-weight: $jst-weight-semibold; color: $jst-text-primary; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.my-page__binding-desc { display: block; margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

/* === Grid (渠道视角) === */
.my-page__grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: $jst-space-md; }
.my-page__grid-item { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: $jst-space-lg $jst-space-sm; border-radius: $jst-radius-card; background: $jst-bg-card; box-shadow: $jst-card-glow; transition: transform $jst-duration-fast $jst-easing; }
.my-page__grid-item:active { transform: scale(0.95); }
.my-page__grid-icon { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: $jst-radius-lg; font-size: $jst-font-md; }
.my-page__grid-icon--blue { background: $jst-brand-light; }
.my-page__grid-icon--orange { background: $jst-warning-light; }
.my-page__grid-icon--teal { background: $jst-success-light; }
.my-page__grid-icon--gold { background: $jst-warning-light; }
.my-page__grid-icon--purple { background: $jst-info-light; }
.my-page__grid-icon--green { background: $jst-success-light; }
.my-page__grid-icon--gray { background: $jst-bg-grey; }
.my-page__grid-text { margin-top: $jst-space-md; font-size: $jst-font-xs; font-weight: $jst-weight-regular; color: $jst-text-regular; }
.my-page__grid-item--disabled { opacity: 0.45; }
.my-page__grid-coming { display: block; margin-top: $jst-space-xs; font-size: 18rpx; color: $jst-text-secondary; }

/* === Workbench === */
.my-page__workbench-btn { display: flex; align-items: center; gap: $jst-space-md; padding: $jst-space-xl $jst-space-lg; border-radius: $jst-radius-card; background: $jst-bg-card; box-shadow: $jst-shadow-ring, $jst-shadow-ambient, $jst-shadow-lift; transition: transform $jst-duration-fast $jst-easing; }
.my-page__workbench-btn:active { transform: scale(0.98); }
.my-page__workbench-icon { font-size: 40rpx; }
.my-page__workbench-text { flex: 1; font-size: $jst-font-md; font-weight: $jst-weight-semibold; color: $jst-brand-dark; }
.my-page__workbench-arrow { font-size: $jst-font-lg; color: $jst-text-placeholder; }
</style>
