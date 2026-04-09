<template>
  <view class="my-page">
    <jst-loading :loading="pageLoading" text="资料加载中..." />

    <view class="my-page__hero">
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
          <view class="my-page__switcher-item my-page__switcher-item--active">学生</view>
          <view class="my-page__switcher-item" @tap="showTeacherComingSoon">老师</view>
        </view>
      </view>
    </view>

    <view class="my-page__summary">
      <view class="my-page__summary-item"><text class="my-page__summary-num">{{ profile.availablePoints || 0 }}</text><text class="my-page__summary-label">可用积分</text></view>
      <view class="my-page__summary-item"><text class="my-page__summary-num">{{ profile.totalPoints || 0 }}</text><text class="my-page__summary-label">累计积分</text></view>
      <view class="my-page__summary-item"><text class="my-page__summary-num">{{ profile.growthValue || 0 }}</text><text class="my-page__summary-label">成长值</text></view>
    </view>

    <view v-if="isChannelUser" class="my-page__section">
      <text class="my-page__section-title">渠道方工作台</text>
      <view class="my-page__grid">
        <view class="my-page__grid-item" @tap="navigateChannelHome"><view class="my-page__grid-icon my-page__grid-icon--blue">🏢</view><text class="my-page__grid-text">渠道首页</text></view>
        <view class="my-page__grid-item" @tap="navigateChannelRebate"><view class="my-page__grid-icon my-page__grid-icon--gold">💰</view><text class="my-page__grid-text">返点中心</text></view>
        <view class="my-page__grid-item" @tap="navigateChannelWithdrawList"><view class="my-page__grid-icon my-page__grid-icon--purple">📄</view><text class="my-page__grid-text">我的提现</text></view>
        <view class="my-page__grid-item" @tap="navigateChannelStudents"><view class="my-page__grid-icon my-page__grid-icon--teal">👥</view><text class="my-page__grid-text">我的学生</text></view>
        <view class="my-page__grid-item" @tap="navigateChannelOrders"><view class="my-page__grid-icon my-page__grid-icon--orange">🧾</view><text class="my-page__grid-text">渠道订单</text></view>
        <view class="my-page__grid-item" @tap="navigateChannelData"><view class="my-page__grid-icon my-page__grid-icon--green">📊</view><text class="my-page__grid-text">渠道数据</text></view>
      </view>
    </view>

    <!-- POLISH-D: 扫码核销入口独立区块, 条件为 jst_partner / jst_platform_op -->
    <view v-if="isPartnerOrOp" class="my-page__section">
      <text class="my-page__section-title">核销工作台</text>
      <view class="my-page__grid">
        <view class="my-page__grid-item" @tap="navigateScan"><view class="my-page__grid-icon my-page__grid-icon--purple">📷</view><text class="my-page__grid-text">扫码核销</text></view>
        <view class="my-page__grid-item" @tap="navigateWriteoffRecord"><view class="my-page__grid-icon my-page__grid-icon--blue">📜</view><text class="my-page__grid-text">核销记录</text></view>
      </view>
    </view>

    <view class="my-page__section">
      <text class="my-page__section-title">我的服务</text>
      <view class="my-page__grid">
        <view class="my-page__grid-item" @tap="navigateOrderList"><view class="my-page__grid-icon my-page__grid-icon--gold">单</view><text class="my-page__grid-text">我的订单</text></view>
        <view class="my-page__grid-item" @tap="navigateRefundList"><view class="my-page__grid-icon my-page__grid-icon--purple">退</view><text class="my-page__grid-text">我的退款</text></view>
        <view class="my-page__grid-item" @tap="navigateMyEnroll"><view class="my-page__grid-icon my-page__grid-icon--teal">报</view><text class="my-page__grid-text">我的报名</text></view>
        <view class="my-page__grid-item" @tap="navigateBinding"><view class="my-page__grid-icon my-page__grid-icon--blue">绑</view><text class="my-page__grid-text">我的绑定</text></view>
        <view class="my-page__grid-item" @tap="navigateMyCourse"><view class="my-page__grid-icon my-page__grid-icon--green">课</view><text class="my-page__grid-text">我的课程</text></view>
        <view class="my-page__grid-item" @tap="navigateProfileEdit"><view class="my-page__grid-icon my-page__grid-icon--blue">👤</view><text class="my-page__grid-text">我的资料</text></view>
        <view class="my-page__grid-item" @tap="navigateParticipant"><view class="my-page__grid-icon my-page__grid-icon--orange">📁</view><text class="my-page__grid-text">我的档案</text></view>
        <view class="my-page__grid-item" @tap="navigateAppointmentList"><view class="my-page__grid-icon my-page__grid-icon--teal">📅</view><text class="my-page__grid-text">我的预约</text></view>
        <view class="my-page__grid-item" @tap="navigateMall"><view class="my-page__grid-icon my-page__grid-icon--gold">🎁</view><text class="my-page__grid-text">积分商城</text></view>
        <view class="my-page__grid-item" @tap="navigatePointsCenter"><view class="my-page__grid-icon my-page__grid-icon--purple">⭐</view><text class="my-page__grid-text">积分中心</text></view>
        <view class="my-page__grid-item" @tap="navigateCouponCenter"><view class="my-page__grid-icon my-page__grid-icon--orange">🎫</view><text class="my-page__grid-text">我的优惠券</text></view>
        <view class="my-page__grid-item" @tap="navigateRightsCenter"><view class="my-page__grid-icon my-page__grid-icon--green">🎖</view><text class="my-page__grid-text">我的权益</text></view>
      </view>
    </view>

    <view class="my-page__hint"><text class="my-page__hint-text">当前仅开放学生视角；老师工作台依赖后续迭代开放。</text></view>
    <button class="my-page__logout" @tap="handleLogout">退出登录</button>
  </view>
</template>

<script>
import { useUserStore } from '@/store/user'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: { JstLoading },
  data() {
    return { pageLoading: false, profile: { nickname: '', avatar: '', mobileMasked: '', currentLevelId: 0, availablePoints: 0, totalPoints: 0, growthValue: 0, userType: '' } }
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
    }
  },
  onShow() { this.ensureLogin(); this.loadProfile() },
  methods: {
    ensureLogin() { const userStore = useUserStore(); if (!userStore.token) uni.reLaunch({ url: '/pages/login/login' }) },
    async loadProfile() { const userStore = useUserStore(); if (!userStore.token) return; this.pageLoading = true; try { const profile = await userStore.fetchProfile(); this.profile = this.normalizeProfile(profile || {}) } finally { this.pageLoading = false } },
    normalizeProfile(profile) { return { nickname: profile.nickname || '', avatar: profile.avatar || '', mobileMasked: profile.mobileMasked || '', currentLevelId: profile.currentLevelId || 0, availablePoints: profile.availablePoints || 0, totalPoints: profile.totalPoints || 0, growthValue: profile.growthValue || 0 } },
    getAvatarText() { return (this.profile.nickname || '竞').slice(0, 1) },
    navigateProfileEdit() { uni.navigateTo({ url: '/pages-sub/my/profile-edit' }) },
    navigateParticipant() { uni.navigateTo({ url: '/pages-sub/my/participant' }) },
    navigateMyEnroll() { uni.navigateTo({ url: '/pages-sub/my/enroll' }) },
    navigateOrderList() { uni.navigateTo({ url: '/pages-sub/my/order-list' }) },
    navigateRefundList() { uni.navigateTo({ url: '/pages-sub/my/refund-list' }) },
    navigateBinding() { uni.navigateTo({ url: '/pages-sub/my/binding' }) },
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
    navigateScan() { uni.navigateTo({ url: '/pages-sub/appointment/scan' }) },
    navigateWriteoffRecord() { uni.navigateTo({ url: '/pages-sub/appointment/writeoff-record' }) },
    navigateCourseTab() { uni.switchTab({ url: '/pages/course/list' }) },
    showTeacherComingSoon() { uni.showToast({ title: '后续开放', icon: 'none' }) },
    handleLogout() { uni.showModal({ title: '退出登录', content: '确认退出当前账号？', success: async (res) => { if (!res.confirm) return; const userStore = useUserStore(); await userStore.doLogout(); uni.reLaunch({ url: '/pages/login/login' }) } }) }
  }
}
</script>

<style scoped lang="scss">
.my-page { min-height: 100vh; padding-bottom: calc(40rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }
.my-page__hero { position: relative; padding: 96rpx 32rpx 120rpx; background: linear-gradient(135deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%); border-bottom-left-radius: 48rpx; border-bottom-right-radius: 48rpx; overflow: hidden; }
.my-page__hero::after { content: ''; position: absolute; top: -120rpx; right: -80rpx; width: 320rpx; height: 320rpx; border-radius: 50%; background: var(--jst-color-white-08); }
.my-page__hero-inner { position: relative; z-index: 1; }
.my-page__profile-row { display: flex; align-items: center; }
.my-page__avatar { display: flex; align-items: center; justify-content: center; width: 120rpx; height: 120rpx; border-radius: 50%; background: linear-gradient(135deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%); border: 6rpx solid var(--jst-color-white-24); overflow: hidden; }
.my-page__avatar-image { width: 100%; height: 100%; }
.my-page__avatar-text { font-size: 48rpx; font-weight: 700; color: var(--jst-color-card-bg); }
.my-page__profile-main { flex: 1; margin-left: 24rpx; }
.my-page__name { display: block; font-size: 40rpx; font-weight: 700; color: var(--jst-color-card-bg); }
.my-page__mobile { display: block; margin-top: 10rpx; font-size: 24rpx; color: var(--jst-color-white-76); }
.my-page__meta { display: flex; flex-wrap: wrap; margin-top: 16rpx; }
.my-page__meta-tag { padding: 8rpx 20rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-white-18); font-size: 22rpx; font-weight: 600; color: var(--jst-color-card-bg); }
.my-page__meta-tag--accent { margin-left: 12rpx; background: var(--jst-color-primary-18); }
.my-page__edit { padding: 14rpx 28rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-white-18); font-size: 24rpx; color: var(--jst-color-card-bg); }
.my-page__switcher { display: inline-flex; margin-top: 28rpx; padding: 8rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-white-18); }
.my-page__switcher-item { min-width: 112rpx; height: 56rpx; border-radius: var(--jst-radius-full); font-size: 24rpx; line-height: 56rpx; text-align: center; color: var(--jst-color-white-76); }
.my-page__switcher-item--active { background: var(--jst-color-card-bg); color: var(--jst-color-brand); font-weight: 700; }
.my-page__summary { display: flex; margin: -72rpx 32rpx 0; padding: 24rpx 0; border-radius: var(--jst-radius-lg); background: var(--jst-color-card-bg); box-shadow: var(--jst-shadow-card); }
.my-page__summary-item { flex: 1; text-align: center; }
.my-page__summary-num { display: block; font-size: 34rpx; font-weight: 700; color: var(--jst-color-brand); }
.my-page__summary-label { display: block; margin-top: 10rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.my-page__section { margin: 24rpx 32rpx 0; }
.my-page__section-title { display: block; margin-bottom: 16rpx; font-size: 26rpx; font-weight: 700; color: var(--jst-color-text); }
.my-page__grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16rpx; }
.my-page__grid-item { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 28rpx 12rpx; border-radius: var(--jst-radius-lg); background: var(--jst-color-card-bg); box-shadow: var(--jst-shadow-card); }
.my-page__grid-icon { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 20rpx; font-size: 30rpx; }
.my-page__grid-icon--blue { background: var(--jst-color-brand-soft); }
.my-page__grid-icon--orange { background: var(--jst-color-primary-soft); }
.my-page__grid-icon--teal { background: #e9fbf3; }
.my-page__grid-icon--gold { background: #fff7e8; }
.my-page__grid-icon--purple { background: #f3ebff; }
.my-page__grid-icon--green { background: #eefbf1; }
.my-page__grid-icon--gray { background: #f3f5fa; }
.my-page__grid-text { margin-top: 16rpx; font-size: 22rpx; color: var(--jst-color-text-secondary); }
.my-page__hint { margin: 24rpx 32rpx 0; padding: 20rpx 24rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-primary-soft); }
.my-page__hint-text { font-size: 22rpx; line-height: 1.7; color: var(--jst-color-text-secondary); }
.my-page__logout { margin: 28rpx 32rpx 0; height: 88rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-card-bg); color: var(--jst-color-danger); font-size: 28rpx; font-weight: 700; }
</style>
