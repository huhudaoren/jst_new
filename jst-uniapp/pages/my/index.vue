<template>
  <view class="my-page">
    <jst-loading :loading="pageLoading" text="资料加载中..." />

    <!-- ===== Hero ===== -->
    <view class="my-page__hero">
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
            <view class="my-page__meta">
              <text class="my-page__meta-tag">Lv.{{ profile.currentLevelId || 0 }}</text>
              <text class="my-page__meta-tag my-page__meta-tag--accent">积分 {{ profile.availablePoints || 0 }}</text>
            </view>
          </view>
          <view class="my-page__edit" @tap="navigateProfileEdit">编辑</view>
        </view>
        <view class="my-page__switcher">
          <view :class="['my-page__switcher-item', currentView === 'student' ? 'my-page__switcher-item--active' : '']" @tap="switchView('student')">学生</view>
          <view v-if="isChannelUser" :class="['my-page__switcher-item', currentView === 'channel' ? 'my-page__switcher-item--active' : '']" @tap="switchView('channel')">渠道方</view>
          <view v-else class="my-page__switcher-item" @tap="navigateChannelApply">申请渠道方</view>
        </view>
      </view>
    </view>

    <!-- ===== Summary 4格 ===== -->
    <view class="my-page__summary">
      <view class="my-page__summary-item" @tap="navigateMyEnroll"><text class="my-page__summary-num">{{ profile.enrollCount || 0 }}</text><text class="my-page__summary-label">报名记录</text></view>
      <view class="my-page__summary-item" @tap="navigateMyScore"><text class="my-page__summary-num">{{ profile.scoreCount || 0 }}</text><text class="my-page__summary-label">我的成绩</text></view>
      <view class="my-page__summary-item" @tap="navigateMyCert"><text class="my-page__summary-num">{{ profile.certCount || 0 }}</text><text class="my-page__summary-label">我的证书</text></view>
      <view class="my-page__summary-item" @tap="navigateMyCourse"><text class="my-page__summary-num">{{ profile.courseCount || 0 }}</text><text class="my-page__summary-label">我的课程</text></view>
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
      <text class="my-page__binding-tag">已绑定</text>
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
        </view>
      </view>

      <!-- 财务与结算 -->
      <view class="my-page__section">
        <text class="my-page__section-title">财务与结算</text>
        <view class="my-page__card">
          <view class="my-page__cell my-page__cell--disabled"><view class="my-page__cell-icon my-page__cell-icon--gray">📋</view><view class="my-page__cell-body"><text class="my-page__cell-title">合同中心</text></view><text class="my-page__cell-soon">即将上线</text></view>
          <view class="my-page__cell my-page__cell--disabled"><view class="my-page__cell-icon my-page__cell-icon--gray">🧾</view><view class="my-page__cell-body"><text class="my-page__cell-title">开票中心</text></view><text class="my-page__cell-soon">即将上线</text></view>
        </view>
      </view>

      <!-- 其他 -->
      <view class="my-page__section">
        <view class="my-page__card">
          <view class="my-page__cell" @tap="navigateChannelApplyStatus"><view class="my-page__cell-icon my-page__cell-icon--teal">✅</view><view class="my-page__cell-body"><text class="my-page__cell-title">认证信息</text></view><text class="my-page__cell-arrow">›</text></view>
          <view class="my-page__cell my-page__cell--disabled"><view class="my-page__cell-icon my-page__cell-icon--blue">💬</view><view class="my-page__cell-body"><text class="my-page__cell-title">客服与帮助</text></view><text class="my-page__cell-soon">即将上线</text></view>
          <view class="my-page__cell my-page__cell--disabled"><view class="my-page__cell-icon my-page__cell-icon--gray">⚙️</view><view class="my-page__cell-body"><text class="my-page__cell-title">设置</text></view><text class="my-page__cell-soon">即将上线</text></view>
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

      <!-- 帮助与设置 -->
      <view class="my-page__section">
        <text class="my-page__section-title">帮助与设置</text>
        <view class="my-page__card">
          <view class="my-page__cell" @tap="navigateProfileEdit"><view class="my-page__cell-icon my-page__cell-icon--blue">👤</view><view class="my-page__cell-body"><text class="my-page__cell-title">个人资料</text><text class="my-page__cell-desc">头像、昵称、学校、年级</text></view><text class="my-page__cell-arrow">›</text></view>
          <view class="my-page__cell" @tap="navigateParticipant"><view class="my-page__cell-icon my-page__cell-icon--orange">📁</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的档案</text><text class="my-page__cell-desc">参赛人信息管理</text></view><text class="my-page__cell-arrow">›</text></view>
          <view class="my-page__cell" @tap="navigateAddressList"><view class="my-page__cell-icon my-page__cell-icon--gray">📍</view><view class="my-page__cell-body"><text class="my-page__cell-title">收货地址</text><text class="my-page__cell-desc">地址管理</text></view><text class="my-page__cell-arrow">›</text></view>
          <view class="my-page__cell" @tap="navigateAppointmentList"><view class="my-page__cell-icon my-page__cell-icon--purple">📅</view><view class="my-page__cell-body"><text class="my-page__cell-title">我的预约</text><text class="my-page__cell-desc">个人预约记录</text></view><text class="my-page__cell-arrow">›</text></view>
          <view class="my-page__cell my-page__cell--disabled"><view class="my-page__cell-icon my-page__cell-icon--blue">💬</view><view class="my-page__cell-body"><text class="my-page__cell-title">客服与帮助</text><text class="my-page__cell-desc">常见问题 & 在线咨询</text></view><text class="my-page__cell-soon">即将上线</text></view>
          <view class="my-page__cell" @tap="navigateSettings"><view class="my-page__cell-icon my-page__cell-icon--gray">⚙️</view><view class="my-page__cell-body"><text class="my-page__cell-title">设置</text><text class="my-page__cell-desc">消息提醒 & 账号安全</text></view><text class="my-page__cell-arrow">›</text></view>
          <view class="my-page__cell my-page__cell--disabled"><view class="my-page__cell-icon my-page__cell-icon--brand">ℹ️</view><view class="my-page__cell-body"><text class="my-page__cell-title">关于竞赛通</text><text class="my-page__cell-desc">版本 v4.1</text></view><text class="my-page__cell-soon">即将上线</text></view>
        </view>
      </view>
    </template>

    <button class="my-page__logout" @tap="handleLogout">退出登录</button>
  </view>
</template>

<script>
import { useUserStore } from '@/store/user'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: { JstLoading },
  data() {
    return { pageLoading: false, moreExpanded: false, currentView: 'student', profile: { nickname: '', avatar: '', mobileMasked: '', currentLevelId: 0, availablePoints: 0, totalPoints: 0, growthValue: 0, userType: '' } }
  },
  computed: {
    // 渠道方视角: userInfo.userType === 'channel' 或 roles 包含 jst_channel
    isChannelUser() {
      const store = useUserStore()
      const info = store.userInfo || {}
      if (info.userType === 'channel') return true
      const roles = info.roles || []
      return Array.isArray(roles) && roles.includes('jst_channel')
    },
    // POLISH-D: 赛事方/平台运营权限, 用于扫码核销入口
    isPartnerOrOp() {
      const store = useUserStore()
      const roles = (store.userInfo && store.userInfo.roles) || []
      return Array.isArray(roles) && (roles.includes('jst_partner') || roles.includes('jst_platform_op'))
    },
    // 渠道认证状态图标
    channelAuthIcon() {
      const store = useUserStore()
      const info = store.userInfo || {}
      if (info.channelAuthStatus === 'approved') return '✅'
      if (info.channelAuthStatus === 'pending') return '⏳'
      if (info.channelAuthStatus === 'rejected') return '❌'
      return '✅'
    },
    // 渠道认证状态文字
    channelAuthText() {
      const store = useUserStore()
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
  methods: {
    ensureLogin() { const userStore = useUserStore(); if (!userStore.token) uni.reLaunch({ url: '/pages/login/login' }) },
    // 初始化默认视角: 渠道用户默认渠道方视角
    initView() { if (this.isChannelUser) { this.currentView = 'channel' } },
    // 切换视角
    switchView(v) { this.currentView = v },
    navigateChannelApplyStatus() { uni.navigateTo({ url: '/pages-sub/channel/apply-status' }) },
    async loadProfile() { const userStore = useUserStore(); if (!userStore.token) return; this.pageLoading = true; try { const profile = await userStore.fetchProfile(); this.profile = this.normalizeProfile(profile || {}) } finally { this.pageLoading = false } },
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
    navigateSettings() { uni.navigateTo({ url: '/pages-sub/my/settings' }) },
    handleLogout() { uni.showModal({ title: '退出登录', content: '确认退出当前账号？', success: async (res) => { if (!res.confirm) return; const userStore = useUserStore(); await userStore.doLogout(); uni.reLaunch({ url: '/pages/login/login' }) } }) }
  }
}
</script>

<style scoped lang="scss">
$xs-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04);

/* === Page === */
.my-page { min-height: 100vh; padding-bottom: calc(60rpx + env(safe-area-inset-bottom)); background: #F7F8FA; }

/* === Hero === */
.my-page__hero { position: relative; padding: 80rpx 32rpx 120rpx; background: linear-gradient(150deg, #1A237E 0%, #283593 40%, #3949AB 100%); border-bottom-left-radius: 32rpx; border-bottom-right-radius: 32rpx; overflow: hidden; }
.my-page__hero-orb { position: absolute; top: -100rpx; right: -60rpx; width: 300rpx; height: 300rpx; border-radius: 50%; background: rgba(255,255,255,0.06); }
.my-page__hero-inner { position: relative; z-index: 1; }
.my-page__profile-row { display: flex; align-items: center; }
.my-page__avatar { display: flex; align-items: center; justify-content: center; width: 112rpx; height: 112rpx; border-radius: 50%; background: var(--jst-gradient-primary); border: 4rpx solid rgba(255,255,255,0.3); overflow: hidden; }
.my-page__avatar-image { width: 100%; height: 100%; }
.my-page__avatar-text { font-size: 44rpx; font-weight: 600; color: #fff; }
.my-page__profile-main { flex: 1; margin-left: 24rpx; }
.my-page__name { display: block; font-size: 36rpx; font-weight: 600; color: #fff; }
.my-page__mobile { display: block; margin-top: 8rpx; font-size: 24rpx; color: rgba(255,255,255,0.7); }
.my-page__meta { display: flex; gap: 12rpx; margin-top: 14rpx; }
.my-page__meta-tag { padding: 6rpx 18rpx; border-radius: 999rpx; background: rgba(255,255,255,0.15); font-size: 22rpx; font-weight: 500; color: #fff; }
.my-page__meta-tag--accent { background: rgba(255,107,53,0.25); }
.my-page__edit { padding: 12rpx 24rpx; border-radius: 999rpx; background: rgba(255,255,255,0.15); font-size: 24rpx; font-weight: 500; color: #fff; }
.my-page__switcher { display: inline-flex; margin-top: 24rpx; padding: 6rpx; border-radius: 999rpx; background: rgba(255,255,255,0.15); }
.my-page__switcher-item { min-width: 120rpx; height: 56rpx; border-radius: 999rpx; font-size: 24rpx; line-height: 56rpx; text-align: center; color: rgba(255,255,255,0.7); transition: all 180ms ease; }
.my-page__switcher-item--active { background: #fff; color: #283593; font-weight: 600; }

/* === Summary 4格 === */
.my-page__summary { display: flex; margin: -64rpx 32rpx 0; padding: 28rpx 0; border-radius: 20rpx; background: #fff; box-shadow: $xs-shadow; position: relative; z-index: 2; }
.my-page__summary-item { flex: 1; text-align: center; }
.my-page__summary-num { display: block; font-size: 36rpx; font-weight: 600; color: #1A1A1A; font-feature-settings: "tnum"; font-variant-numeric: tabular-nums; }
.my-page__summary-label { display: block; margin-top: 8rpx; font-size: 22rpx; color: #8A8A8A; }

/* === 等级卡 === */
.my-page__level-card { margin: 24rpx 32rpx 0; padding: 28rpx; border-radius: 20rpx; background: linear-gradient(135deg, #1A237E 0%, #303F9F 50%, #5C6BC0 100%); }
.my-page__level-header { display: flex; align-items: flex-start; gap: 20rpx; }
.my-page__level-badge { flex-shrink: 0; padding: 8rpx 20rpx; border-radius: 999rpx; background: rgba(255,200,100,0.25); font-size: 24rpx; font-weight: 600; color: #FFD54F; }
.my-page__level-progress { flex: 1; }
.my-page__level-progress-text { display: block; font-size: 22rpx; color: rgba(255,255,255,0.7); margin-bottom: 12rpx; }
.my-page__level-bar { height: 10rpx; border-radius: 5rpx; background: rgba(255,255,255,0.18); overflow: hidden; }
.my-page__level-bar-fill { height: 100%; border-radius: 5rpx; background: linear-gradient(90deg, #FFD54F, #FFB300); transition: width 0.6s ease; }
.my-page__level-stats { display: flex; margin-top: 28rpx; padding-top: 24rpx; border-top: 2rpx solid rgba(255,255,255,0.1); }
.my-page__level-stat { flex: 1; text-align: center; }
.my-page__level-stat-num { display: block; font-size: 34rpx; font-weight: 600; color: #fff; font-feature-settings: "tnum"; font-variant-numeric: tabular-nums; }
.my-page__level-stat-label { display: block; margin-top: 6rpx; font-size: 20rpx; color: rgba(255,255,255,0.6); }

/* === 快捷标签 === */
.my-page__quick-tags { display: flex; gap: 12rpx; margin: 20rpx 32rpx 0; flex-wrap: wrap; }
.my-page__quick-tag { padding: 10rpx 22rpx; border-radius: 999rpx; background: #EAF4FC; font-size: 22rpx; font-weight: 500; color: #3F51B5; }

/* === Section === */
.my-page__section { margin: 32rpx 32rpx 0; }
.my-page__section-title { display: block; margin-bottom: 16rpx; font-size: 28rpx; font-weight: 600; color: #1A1A1A; }

/* === Card container === */
.my-page__card { border-radius: 20rpx; background: #fff; box-shadow: $xs-shadow; overflow: hidden; }

/* === Cell (list item) === */
.my-page__cell { display: flex; align-items: center; gap: 20rpx; padding: 28rpx 24rpx; border-bottom: 2rpx solid #F2F3F5; transition: background 150ms; }
.my-page__cell:last-child { border-bottom: none; }
.my-page__cell:active { background: #F7F8FA; }
.my-page__cell-icon { display: flex; align-items: center; justify-content: center; width: 64rpx; height: 64rpx; border-radius: 18rpx; font-size: 28rpx; flex-shrink: 0; }
.my-page__cell-icon--blue { background: #EAF4FC; }
.my-page__cell-icon--orange { background: #FFF3EE; }
.my-page__cell-icon--teal { background: #E9FBF3; }
.my-page__cell-icon--gold { background: #FFF7E8; }
.my-page__cell-icon--purple { background: #F3EBFF; }
.my-page__cell-icon--green { background: #EEFBF1; }
.my-page__cell-icon--gray { background: #F3F5FA; }
.my-page__cell-icon--brand { background: #EAF4FC; }
.my-page__cell-icon--red { background: #FDECEC; }
.my-page__cell-body { flex: 1; min-width: 0; }
.my-page__cell-title { display: block; font-size: 28rpx; font-weight: 500; color: #1A1A1A; }
.my-page__cell-desc { display: block; margin-top: 4rpx; font-size: 22rpx; color: #8A8A8A; }
.my-page__cell-arrow { font-size: 28rpx; color: #C0C4CC; flex-shrink: 0; }
.my-page__cell-soon { flex-shrink: 0; padding: 4rpx 14rpx; border-radius: 999rpx; background: #F2F3F5; font-size: 20rpx; color: #8A8A8A; }
.my-page__cell-badge { flex-shrink: 0; min-width: 32rpx; height: 32rpx; padding: 0 8rpx; border-radius: 999rpx; background: #E74C3C; color: #fff; font-size: 20rpx; font-weight: 600; text-align: center; line-height: 32rpx; }
.my-page__cell--disabled { opacity: 0.55; }

/* === 绑定卡 === */
.my-page__binding-card { display: flex; align-items: center; gap: 16rpx; margin: 20rpx 32rpx 0; padding: 24rpx; border-radius: 20rpx; background: #fff; box-shadow: $xs-shadow; border-left: 6rpx solid #3949AB; }
.my-page__binding-dot { width: 12rpx; height: 12rpx; border-radius: 50%; background: #27AE60; flex-shrink: 0; }
.my-page__binding-body { flex: 1; min-width: 0; }
.my-page__binding-title { display: block; font-size: 26rpx; font-weight: 600; color: #1A1A1A; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.my-page__binding-desc { display: block; margin-top: 4rpx; font-size: 22rpx; color: #8A8A8A; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.my-page__binding-tag { flex-shrink: 0; padding: 6rpx 16rpx; border-radius: 999rpx; background: #EAF4FC; font-size: 20rpx; font-weight: 500; color: #3949AB; }

/* === Grid (渠道视角) === */
.my-page__grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16rpx; }
.my-page__grid-item { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 28rpx 12rpx; border-radius: 20rpx; background: #fff; box-shadow: $xs-shadow; transition: transform 180ms ease-out; }
.my-page__grid-item:active { transform: scale(0.98); }
.my-page__grid-icon { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 20rpx; font-size: 30rpx; }
.my-page__grid-icon--blue { background: #EAF4FC; }
.my-page__grid-icon--orange { background: #FFF3EE; }
.my-page__grid-icon--teal { background: #E9FBF3; }
.my-page__grid-icon--gold { background: #FFF7E8; }
.my-page__grid-icon--purple { background: #F3EBFF; }
.my-page__grid-icon--green { background: #EEFBF1; }
.my-page__grid-icon--gray { background: #F3F5FA; }
.my-page__grid-text { margin-top: 16rpx; font-size: 22rpx; font-weight: 400; color: #4A4A4A; }
.my-page__grid-item--disabled { opacity: 0.45; }
.my-page__grid-coming { display: block; margin-top: 4rpx; font-size: 18rpx; color: #8A8A8A; }

/* === Workbench === */
.my-page__workbench-btn { display: flex; align-items: center; gap: 16rpx; padding: 32rpx 24rpx; border-radius: 20rpx; background: #fff; box-shadow: $xs-shadow; }
.my-page__workbench-icon { font-size: 40rpx; }
.my-page__workbench-text { flex: 1; font-size: 30rpx; font-weight: 600; color: #283593; }
.my-page__workbench-arrow { font-size: 32rpx; color: #C0C4CC; }

/* === Logout === */
.my-page__logout { margin: 48rpx 32rpx 0; height: 88rpx; border-radius: 16rpx; background: transparent; border: 2rpx solid #FDECEC; color: #E74C3C; font-size: 28rpx; font-weight: 600; transition: transform 180ms ease-out; }
.my-page__logout:active { transform: scale(0.98); }
</style>
