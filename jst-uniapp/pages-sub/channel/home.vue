<!-- 中文注释: 渠道方 · 工作台首页 (E1-CH-2 完整重构)
     对应原型: 小程序原型图/channel-home.png + channel-home.html
     5区块: Hero KPI + 返点横幅 + 功能宫格 + 权益区 + 最近订单
     数据来源: dashboard/monthly + dashboard/stats + rebate/summary + rights/my + orders -->
<template>
  <view class="ch-home">
    <!-- A. Hero 区域 -->
    <view class="ch-hero">
      <view class="ch-hero__nav">
        <view class="ch-hero__back" @tap="goBack">←</view>
        <text class="ch-hero__title">渠道方工作台</text>
        <view class="ch-hero__msg" @tap="goNotice">🔔</view>
      </view>

      <!-- 身份卡 -->
      <view class="ch-hero__identity">
        <view class="ch-hero__avatar">👨‍🏫</view>
        <view class="ch-hero__info">
          <text class="ch-hero__name">{{ channelName }}</text>
          <view class="ch-hero__badges">
            <text v-if="authStatus === 'approved'" class="ch-hero__badge ch-hero__badge--verified">✅ 已认证</text>
            <text v-else-if="authStatus === 'pending'" class="ch-hero__badge ch-hero__badge--pending">⏳ 审核中</text>
            <text v-if="channelType" class="ch-hero__badge">{{ channelType }}</text>
          </view>
          <text class="ch-hero__sub">{{ schoolInfo }}</text>
        </view>
      </view>

      <!-- 视角切换 -->
      <view class="ch-hero__switch">
        <view class="ch-hero__switch-btn ch-hero__switch-btn--active">渠道方视角</view>
        <view class="ch-hero__switch-btn" @tap="goMyPage">返回个人中心</view>
      </view>
    </view>

    <!-- 未认证拦截 -->
    <view v-if="authStatus !== 'approved' && !pageLoading" class="ch-home__block">
      <view class="ch-home__block-card">
        <text class="ch-home__block-icon">🔒</text>
        <text class="ch-home__block-title">{{ authStatus === 'pending' ? '认证审核中' : '请先申请成为渠道方' }}</text>
        <text class="ch-home__block-desc">{{ authStatus === 'pending' ? '您的认证申请正在审核中，通过后即可使用工作台全部功能' : '完成渠道方认证后，即可使用绑定学生、代报名等功能' }}</text>
        <view v-if="authStatus !== 'pending'" class="ch-home__block-btn" @tap="goApply">立即申请</view>
        <view v-else class="ch-home__block-btn ch-home__block-btn--outline" @tap="goApplyStatus">查看进度</view>
      </view>
    </view>

    <!-- 已认证内容 -->
    <template v-if="authStatus === 'approved'">
      <!-- A. 4格 Hero KPI -->
      <view class="ch-stats">
        <view class="ch-stats__cell" @tap="goStudents">
          <text class="ch-stats__num">{{ monthly.bindStudents || 0 }}</text>
          <text class="ch-stats__label">绑定学生</text>
        </view>
        <view class="ch-stats__cell" @tap="goOrders">
          <text class="ch-stats__num">{{ monthly.enrollTotal || 0 }}</text>
          <text class="ch-stats__label">代报名总数</text>
        </view>
        <view class="ch-stats__cell" @tap="goOrders">
          <text class="ch-stats__num" style="color: #FF5722;">{{ monthly.pendingPay || 0 }}</text>
          <text class="ch-stats__label">待付款</text>
        </view>
        <view class="ch-stats__cell" @tap="goOrders">
          <text class="ch-stats__num">¥{{ formatK(monthly.totalPaid) }}</text>
          <text class="ch-stats__label">累计支付</text>
        </view>
      </view>

      <!-- B. 返点横幅 -->
      <view class="ch-rebate-banner" @tap="goRebate">
        <view class="ch-rebate-banner__item">
          <text class="ch-rebate-banner__num ch-rebate-banner__num--gold">¥{{ rebateSummary.withdrawableAmount || '0.00' }}</text>
          <text class="ch-rebate-banner__label">可提现余额</text>
        </view>
        <view class="ch-rebate-banner__divider"></view>
        <view class="ch-rebate-banner__item">
          <text class="ch-rebate-banner__num">¥{{ rebateSummary.reviewingAmount || '0.00' }}</text>
          <text class="ch-rebate-banner__label">审核中</text>
        </view>
        <view class="ch-rebate-banner__divider"></view>
        <view class="ch-rebate-banner__item">
          <text class="ch-rebate-banner__num">¥{{ rebateSummary.paidAmount || '0.00' }}</text>
          <text class="ch-rebate-banner__label">累计已打款</text>
        </view>
        <view class="ch-rebate-banner__arrow">›</view>
      </view>

      <!-- 绑定二维码入口 -->
      <view class="ch-qr-entry" @tap="goStudentsQr">
        <view class="ch-qr-entry__icon">🔲</view>
        <view class="ch-qr-entry__info">
          <text class="ch-qr-entry__title">我的绑定二维码</text>
          <text class="ch-qr-entry__desc">学生扫码完成一对一绑定，绑定后可代报名 & 统一支付</text>
        </view>
        <text class="ch-qr-entry__arrow">›</text>
      </view>

      <!-- C. 功能宫格 -->
      <view class="ch-func-grid">
        <view class="ch-func-item" @tap="goStudents">
          <view class="ch-func-icon" style="background: #EEF0FF;">👨‍🎓</view>
          <text class="ch-func-label">学生管理</text>
        </view>
        <view class="ch-func-item" @tap="goBatchEnroll">
          <view class="ch-func-icon" style="background: #EBF4FC;">📋</view>
          <text class="ch-func-label">代报名</text>
        </view>
        <view class="ch-func-item" @tap="goOrders">
          <view class="ch-func-icon" style="background: #FFF3EE;">🧾</view>
          <text class="ch-func-label">订单管理</text>
        </view>
        <view class="ch-func-item" @tap="goRebate">
          <view class="ch-func-icon" style="background: #FEF9EC;">💰</view>
          <text class="ch-func-label">返点中心</text>
        </view>
        <view class="ch-func-item" @tap="goSettlement">
          <view class="ch-func-icon" style="background: #F5EEF8;">📄</view>
          <text class="ch-func-label">提现结算</text>
        </view>
        <view class="ch-func-item" @tap="goData">
          <view class="ch-func-icon" style="background: #EAFAF1;">📊</view>
          <text class="ch-func-label">报名数据</text>
        </view>
        <view class="ch-func-item" @tap="goData">
          <view class="ch-func-icon" style="background: #E8EAF6;">📈</view>
          <text class="ch-func-label">经营分析</text>
        </view>
        <view class="ch-func-item" @tap="goAppointment">
          <view class="ch-func-icon" style="background: #E0F7FA;">📅</view>
          <text class="ch-func-label">团队预约</text>
        </view>
        <view class="ch-func-item" @tap="showComingSoon">
          <view class="ch-func-icon" style="background: #F3E8FF;">🤖</view>
          <text class="ch-func-label">AI 课程</text>
        </view>
      </view>

      <!-- D. 权益区 -->
      <view v-if="rightsList.length" class="ch-section">
        <view class="ch-section__header">
          <text class="ch-section__title">我的权益</text>
          <text class="ch-section__more" @tap="goRightsCenter">查看全部 ›</text>
        </view>
        <view class="ch-rights-grid">
          <view v-for="item in rightsList.slice(0, 4)" :key="item.rightsId" class="ch-rights-item" @tap="goRightsDetail(item.rightsId)">
            <text class="ch-rights-item__icon">{{ getRightsIcon(item.rightsType) }}</text>
            <text class="ch-rights-item__name">{{ item.rightsName }}</text>
            <text class="ch-rights-item__quota">剩余 {{ item.remainQuota || 0 }}</text>
          </view>
        </view>
      </view>

      <!-- E. 最近订单 -->
      <view class="ch-section">
        <view class="ch-section__header">
          <text class="ch-section__title">待支付订单</text>
          <text class="ch-section__more" @tap="goOrders">全部 ›</text>
        </view>
        <view v-for="item in recentOrders" :key="item.orderId" class="ch-order-item" @tap="goOrderDetail(item.orderId)">
          <view class="ch-order-item__avatar" :style="{ background: getAvatarColor(item.studentName) }">
            <text class="ch-order-item__avatar-text">{{ (item.studentName || '').slice(0, 1) }}</text>
          </view>
          <view class="ch-order-item__info">
            <text class="ch-order-item__name">{{ item.studentName || '--' }}</text>
            <text class="ch-order-item__contest">{{ item.contestName || '--' }}</text>
          </view>
          <view class="ch-order-item__right">
            <text class="ch-order-item__amount">{{ item.payAmount > 0 ? '¥' + item.payAmount : '免费' }}</text>
            <text class="ch-order-item__status">{{ item.statusText || '' }}</text>
          </view>
        </view>
        <view v-if="!recentOrders.length" class="ch-empty">暂无待支付订单</view>
      </view>

      <!-- 学生概览 -->
      <view class="ch-section">
        <view class="ch-section__header">
          <text class="ch-section__title">最近绑定学生</text>
          <text class="ch-section__more" @tap="goStudents">全部 ›</text>
        </view>
        <view v-for="item in recentStudents" :key="item.bindingId" class="ch-student-item" @tap="goStudents">
          <view class="ch-student-item__avatar" :style="{ background: getAvatarColor(item.studentName) }">
            <text>{{ (item.studentName || '').slice(0, 1) }}</text>
          </view>
          <view class="ch-student-item__info">
            <text class="ch-student-item__name">{{ item.studentName }}</text>
            <text class="ch-student-item__meta">{{ item.schoolName || '' }} · {{ item.gradeName || '' }}</text>
          </view>
        </view>
        <view v-if="!recentStudents.length" class="ch-empty">暂无绑定学生</view>
      </view>

      <view style="height: 48rpx;"></view>
    </template>
  </view>
</template>

<script>
import { getRebateSummary, getChannelMonthly, getChannelDashboardStats, getChannelRightsMy, getChannelOrders, getChannelStudents, getMyChannelApply } from '@/api/channel'
import { useUserStore } from '@/store/user'

// 头像渐变色池
const AVATAR_COLORS = [
  'linear-gradient(135deg, #3F51B5, #5C6BC0)',
  'linear-gradient(135deg, #E65100, #FF7043)',
  'linear-gradient(135deg, #1B5E20, #66BB6A)',
  'linear-gradient(135deg, #880E4F, #F06292)',
  'linear-gradient(135deg, #4527A0, #7E57C2)'
]

export default {
  data() {
    return {
      pageLoading: true,
      authStatus: '', // '' | 'pending' | 'approved' | 'rejected'
      channelName: '渠道方',
      channelType: '',
      schoolInfo: '',
      monthly: {},
      rebateSummary: {},
      rightsList: [],
      recentOrders: [],
      recentStudents: []
    }
  },
  onShow() { this.init() },
  methods: {
    async init() {
      this.pageLoading = true
      const store = useUserStore()
      if (!store.token) {
        uni.reLaunch({ url: '/pages/login/login' })
        return
      }
      // 先检查认证状态
      try {
        const apply = await getMyChannelApply()
        if (apply) {
          this.authStatus = apply.status || ''
          this.channelName = apply.applyName || (store.userInfo && store.userInfo.nickname) || '渠道方'
          this.channelType = apply.channelType || ''
          this.schoolInfo = apply.school || ''
        }
      } catch (e) {
        // 未申请过
        const info = store.userInfo || {}
        const roles = info.roles || []
        if (roles.includes('jst_channel')) {
          this.authStatus = 'approved'
          this.channelName = info.nickname || info.channelName || '渠道方'
        }
      }

      if (this.authStatus === 'approved') {
        await this.loadData()
      }
      this.pageLoading = false
    },

    async loadData() {
      // 并行加载各区块数据
      const results = await Promise.allSettled([
        getChannelMonthly(),
        getRebateSummary(),
        getChannelRightsMy(),
        getChannelOrders({ pageNum: 1, pageSize: 3, status: 'pending_pay' }),
        getChannelStudents({ pageNum: 1, pageSize: 3 })
      ])

      if (results[0].status === 'fulfilled') this.monthly = results[0].value || {}
      if (results[1].status === 'fulfilled') this.rebateSummary = results[1].value || {}
      if (results[2].status === 'fulfilled') this.rightsList = Array.isArray(results[2].value) ? results[2].value : []
      if (results[3].status === 'fulfilled') {
        const ordData = results[3].value
        this.recentOrders = (ordData && ordData.rows) || (Array.isArray(ordData) ? ordData : [])
      }
      if (results[4].status === 'fulfilled') {
        const stuData = results[4].value
        this.recentStudents = (stuData && stuData.rows) || (Array.isArray(stuData) ? stuData : [])
      }
    },

    formatK(val) {
      if (!val) return '0'
      const n = Number(val)
      if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
      return n.toFixed(2)
    },

    getAvatarColor(name) {
      if (!name) return AVATAR_COLORS[0]
      const code = name.charCodeAt(0) || 0
      return AVATAR_COLORS[code % AVATAR_COLORS.length]
    },

    getRightsIcon(type) {
      const map = { venue: '🏟', course: '📚', service: '💬', writeoff: '✅' }
      return map[type] || '🎖'
    },

    goBack() { uni.navigateBack() },
    goMyPage() { uni.switchTab({ url: '/pages/my/index' }) },
    goNotice() { uni.switchTab({ url: '/pages/notice/list' }) },
    goStudents() { uni.navigateTo({ url: '/pages-sub/channel/students' }) },
    goStudentsQr() { uni.navigateTo({ url: '/pages-sub/channel/students' }) },
    goOrders() { uni.navigateTo({ url: '/pages-sub/channel/orders' }) },
    goRebate() { uni.navigateTo({ url: '/pages-sub/channel/rebate' }) },
    goSettlement() { uni.navigateTo({ url: '/pages-sub/channel/settlement' }) },
    goData() { uni.navigateTo({ url: '/pages-sub/channel/data' }) },
    goBatchEnroll() { uni.showToast({ title: '批量报名 CH-7 后续开放', icon: 'none' }) },
    goAppointment() { uni.showToast({ title: '团队预约 CH-7 后续开放', icon: 'none' }) },
    goApply() { uni.navigateTo({ url: '/pages-sub/channel/apply-entry' }) },
    goApplyStatus() { uni.navigateTo({ url: '/pages-sub/channel/apply-status' }) },
    goRightsCenter() { uni.navigateTo({ url: '/pages-sub/rights/center' }) },
    goRightsDetail(id) { uni.navigateTo({ url: '/pages-sub/rights/detail?id=' + id }) },
    goOrderDetail(id) { uni.navigateTo({ url: '/pages-sub/channel/order-detail?id=' + id }) },
    showComingSoon() { uni.showToast({ title: '即将上线', icon: 'none' }) }
  }
}
</script>

<style scoped lang="scss">
.ch-home { min-height: 100vh; padding-bottom: calc(48rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }

/* Hero */
.ch-hero { background: linear-gradient(150deg, #1A237E 0%, #283593 45%, #3949AB 100%); padding: 88rpx 32rpx 48rpx; position: relative; overflow: hidden; border-bottom-left-radius: 48rpx; border-bottom-right-radius: 48rpx; }
.ch-hero::before { content: ''; position: absolute; top: -60rpx; right: -40rpx; width: 280rpx; height: 280rpx; border-radius: 50%; background: rgba(255,255,255,0.06); }
.ch-hero__nav { display: flex; align-items: center; margin-bottom: 32rpx; position: relative; z-index: 1; }
.ch-hero__back { width: 72rpx; height: 72rpx; border-radius: var(--jst-radius-sm); background: rgba(255,255,255,0.15); display: flex; align-items: center; justify-content: center; font-size: 36rpx; color: #fff; margin-right: 24rpx; }
.ch-hero__title { flex: 1; font-size: 34rpx; font-weight: 700; color: #fff; }
.ch-hero__msg { width: 68rpx; height: 68rpx; border-radius: 50%; background: rgba(255,255,255,0.12); display: flex; align-items: center; justify-content: center; font-size: 32rpx; }
.ch-hero__identity { display: flex; align-items: center; gap: 28rpx; margin-bottom: 32rpx; position: relative; z-index: 1; }
.ch-hero__avatar { width: 112rpx; height: 112rpx; border-radius: 50%; background: linear-gradient(135deg, #FFD54F, #FF8A65); display: flex; align-items: center; justify-content: center; font-size: 52rpx; border: 6rpx solid rgba(255,255,255,0.3); }
.ch-hero__info { flex: 1; }
.ch-hero__name { display: block; font-size: 36rpx; font-weight: 800; color: #fff; }
.ch-hero__badges { display: flex; flex-wrap: wrap; gap: 12rpx; margin-top: 12rpx; }
.ch-hero__badge { display: inline-flex; padding: 6rpx 20rpx; border-radius: var(--jst-radius-full); background: rgba(255,255,255,0.18); border: 2rpx solid rgba(255,255,255,0.25); font-size: 22rpx; font-weight: 700; color: rgba(255,255,255,0.95); }
.ch-hero__badge--verified { background: rgba(39,174,96,0.3); border-color: rgba(39,174,96,0.5); }
.ch-hero__badge--pending { background: rgba(255,193,7,0.3); border-color: rgba(255,193,7,0.5); }
.ch-hero__sub { display: block; margin-top: 8rpx; font-size: 24rpx; color: rgba(255,255,255,0.65); }
.ch-hero__switch { display: inline-flex; padding: 6rpx; border-radius: var(--jst-radius-full); background: rgba(255,255,255,0.12); position: relative; z-index: 1; }
.ch-hero__switch-btn { padding: 10rpx 32rpx; border-radius: var(--jst-radius-full); font-size: 24rpx; font-weight: 600; color: rgba(255,255,255,0.65); }
.ch-hero__switch-btn--active { background: #fff; color: #3F51B5; }

/* 未认证拦截 */
.ch-home__block { padding: 80rpx 32rpx; }
.ch-home__block-card { padding: 56rpx 40rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); text-align: center; }
.ch-home__block-icon { display: block; font-size: 72rpx; margin-bottom: 20rpx; }
.ch-home__block-title { display: block; font-size: 32rpx; font-weight: 700; color: var(--jst-color-text); }
.ch-home__block-desc { display: block; margin-top: 16rpx; font-size: 24rpx; color: var(--jst-color-text-tertiary); line-height: 1.6; }
.ch-home__block-btn { margin-top: 32rpx; height: 80rpx; line-height: 80rpx; border-radius: var(--jst-radius-md); background: #3F51B5; color: #fff; font-size: 28rpx; font-weight: 700; text-align: center; }
.ch-home__block-btn--outline { background: transparent; border: 2rpx solid #3F51B5; color: #3F51B5; }

/* 4格 KPI */
.ch-stats { display: grid; grid-template-columns: repeat(4, 1fr); margin: 24rpx 32rpx 0; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); overflow: hidden; }
.ch-stats__cell { padding: 28rpx 16rpx; text-align: center; border-right: 2rpx solid var(--jst-color-border); }
.ch-stats__cell:last-child { border-right: none; }
.ch-stats__num { display: block; font-size: 44rpx; font-weight: 900; color: #3F51B5; line-height: 1; margin-bottom: 8rpx; }
.ch-stats__label { display: block; font-size: 20rpx; color: var(--jst-color-text-tertiary); }

/* 返点横幅 */
.ch-rebate-banner { display: flex; align-items: center; margin: 24rpx 32rpx 0; padding: 28rpx 24rpx; background: linear-gradient(135deg, #3F51B5, #5C6BC0); border-radius: var(--jst-radius-lg); box-shadow: 0 12rpx 32rpx rgba(63,81,181,0.3); }
.ch-rebate-banner__item { flex: 1; text-align: center; }
.ch-rebate-banner__num { display: block; font-size: 28rpx; font-weight: 800; color: #fff; }
.ch-rebate-banner__num--gold { color: #FFD54F; font-size: 32rpx; }
.ch-rebate-banner__label { display: block; margin-top: 6rpx; font-size: 20rpx; color: rgba(255,255,255,0.7); }
.ch-rebate-banner__divider { width: 2rpx; height: 48rpx; background: rgba(255,255,255,0.2); }
.ch-rebate-banner__arrow { font-size: 36rpx; color: rgba(255,255,255,0.6); margin-left: 12rpx; }

/* 二维码入口 */
.ch-qr-entry { display: flex; align-items: center; gap: 28rpx; margin: 24rpx 32rpx 0; padding: 32rpx; background: linear-gradient(135deg, #3F51B5 0%, #5C6BC0 100%); border-radius: var(--jst-radius-lg); box-shadow: 0 12rpx 40rpx rgba(63,81,181,0.35); position: relative; overflow: hidden; }
.ch-qr-entry::before { content: ''; position: absolute; top: -40rpx; right: -40rpx; width: 200rpx; height: 200rpx; border-radius: 50%; background: rgba(255,255,255,0.06); }
.ch-qr-entry__icon { width: 104rpx; height: 104rpx; border-radius: var(--jst-radius-md); background: rgba(255,255,255,0.18); display: flex; align-items: center; justify-content: center; font-size: 56rpx; }
.ch-qr-entry__info { flex: 1; }
.ch-qr-entry__title { display: block; font-size: 32rpx; font-weight: 800; color: #fff; }
.ch-qr-entry__desc { display: block; margin-top: 8rpx; font-size: 24rpx; color: rgba(255,255,255,0.75); line-height: 1.5; }
.ch-qr-entry__arrow { font-size: 44rpx; color: rgba(255,255,255,0.6); }

/* 功能宫格 */
.ch-func-grid { display: grid; grid-template-columns: repeat(3, 1fr); margin: 24rpx 32rpx 0; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); overflow: hidden; }
.ch-func-item { padding: 32rpx 16rpx; text-align: center; border-right: 2rpx solid var(--jst-color-border); border-bottom: 2rpx solid var(--jst-color-border); }
.ch-func-item:nth-child(3n) { border-right: none; }
.ch-func-item:nth-last-child(-n+3) { border-bottom: none; }
.ch-func-icon { width: 88rpx; height: 88rpx; border-radius: var(--jst-radius-md); display: flex; align-items: center; justify-content: center; font-size: 44rpx; margin: 0 auto 16rpx; }
.ch-func-label { font-size: 24rpx; font-weight: 500; color: var(--jst-color-text-secondary); }

/* 通用 section */
.ch-section { margin: 24rpx 32rpx 0; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); overflow: hidden; }
.ch-section__header { display: flex; align-items: center; justify-content: space-between; padding: 28rpx 32rpx 20rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.ch-section__title { font-size: 30rpx; font-weight: 700; color: var(--jst-color-text); display: flex; align-items: center; gap: 12rpx; }
.ch-section__title::before { content: ''; width: 6rpx; height: 30rpx; background: #3F51B5; border-radius: 4rpx; }
.ch-section__more { font-size: 26rpx; color: #3F51B5; font-weight: 500; }

/* 权益区 */
.ch-rights-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 0; }
.ch-rights-item { display: flex; flex-direction: column; align-items: center; padding: 28rpx 16rpx; border-right: 2rpx solid var(--jst-color-border); border-bottom: 2rpx solid var(--jst-color-border); }
.ch-rights-item:nth-child(2n) { border-right: none; }
.ch-rights-item:nth-last-child(-n+2) { border-bottom: none; }
.ch-rights-item__icon { font-size: 40rpx; margin-bottom: 8rpx; }
.ch-rights-item__name { font-size: 24rpx; font-weight: 600; color: var(--jst-color-text); }
.ch-rights-item__quota { font-size: 22rpx; color: var(--jst-color-text-tertiary); margin-top: 4rpx; }

/* 最近订单 */
.ch-order-item { display: flex; align-items: center; gap: 24rpx; padding: 24rpx 32rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.ch-order-item:last-child { border-bottom: none; }
.ch-order-item__avatar { width: 76rpx; height: 76rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.ch-order-item__avatar-text { font-size: 32rpx; font-weight: 700; color: #fff; }
.ch-order-item__info { flex: 1; min-width: 0; }
.ch-order-item__name { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); }
.ch-order-item__contest { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.ch-order-item__right { text-align: right; flex-shrink: 0; }
.ch-order-item__amount { display: block; font-size: 30rpx; font-weight: 800; color: #FF5722; }
.ch-order-item__status { display: block; margin-top: 4rpx; font-size: 20rpx; color: var(--jst-color-text-tertiary); }

/* 学生概览 */
.ch-student-item { display: flex; align-items: center; gap: 24rpx; padding: 24rpx 32rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.ch-student-item:last-child { border-bottom: none; }
.ch-student-item__avatar { width: 76rpx; height: 76rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 32rpx; font-weight: 700; color: #fff; }
.ch-student-item__info { flex: 1; }
.ch-student-item__name { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); }
.ch-student-item__meta { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }

.ch-empty { padding: 40rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
</style>
