<!-- 中文注释: 渠道方 · 工作台首页 (E1-CH-2 完整重构)
     对应原型: 小程序原型图/channel-home.png + channel-home.html
     5区块: Hero KPI + 返点横幅 + 功能宫格 + 权益区 + 最近订单
     数据来源: dashboard/monthly + dashboard/stats + rebate/summary + rights/my + orders -->
<template>
  <view class="ch-home">
    <!-- A. Hero 区域 -->
    <view class="ch-hero">
      <view class="ch-hero__nav" :style="{ paddingTop: navPaddingTop }">
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
            <u-tag v-if="authStatus === 'approved'" text="已认证" type="success" size="mini" plain shape="circle"></u-tag>
            <u-tag v-else-if="authStatus === 'pending'" text="审核中" type="warning" size="mini" plain shape="circle"></u-tag>
            <u-tag v-if="channelType" :text="channelType" type="info" size="mini" plain shape="circle"></u-tag>
          </view>
          <text class="ch-hero__sub">{{ schoolInfo }}</text>
        </view>
      </view>

      <!-- 视角切换 -->
      <view class="ch-hero__switch">
        <u-tag v-if="authStatus === 'approved'" text="渠道方视角" type="primary" size="mini" shape="circle"></u-tag>
        <u-tag v-else-if="authStatus === 'pending'" text="审核中" type="warning" size="mini" shape="circle"></u-tag>
        <u-tag v-else text="未开通" type="info" size="mini" plain shape="circle"></u-tag>
        <u-button text="返回个人中心" size="mini" shape="circle" @click="goMyPage"></u-button>
      </view>
    </view>

    <!-- 未认证拦截 -->
    <view v-if="authStatus !== 'approved' && !pageLoading" class="ch-home__block">
      <view class="ch-home__block-card">
        <view class="ch-home__block-icon-wrap">
          <text class="ch-home__block-icon">{{ authStatus === 'pending' ? '⏳' : '🚀' }}</text>
        </view>
        <text class="ch-home__block-title">{{ authStatus === 'pending' ? '认证审核中' : '成为渠道方 · 开启新收益' }}</text>
        <text class="ch-home__block-desc">{{ authStatus === 'pending' ? '您的认证申请正在审核中，预计 1-3 个工作日完成，通过后通知您' : '认证通过后，即可享有以下特权与收益' }}</text>

        <!-- 3 特权预览（仅未认证时展示） -->
        <view v-if="authStatus !== 'pending'" class="ch-home__perks">
          <view class="ch-home__perk">
            <text class="ch-home__perk-emoji">💰</text>
            <text class="ch-home__perk-label">返点收益</text>
            <text class="ch-home__perk-desc">代报名享返点</text>
          </view>
          <view class="ch-home__perk">
            <text class="ch-home__perk-emoji">🎓</text>
            <text class="ch-home__perk-label">学生管理</text>
            <text class="ch-home__perk-desc">绑定/批量操作</text>
          </view>
          <view class="ch-home__perk">
            <text class="ch-home__perk-emoji">📊</text>
            <text class="ch-home__perk-label">数据分析</text>
            <text class="ch-home__perk-desc">报名/成绩看板</text>
          </view>
        </view>

        <!-- 3 步流程 -->
        <view v-if="authStatus !== 'pending'" class="ch-home__flow">
          <view class="ch-home__flow-step">
            <view class="ch-home__flow-num">1</view>
            <text class="ch-home__flow-label">提交资料</text>
          </view>
          <view class="ch-home__flow-line"></view>
          <view class="ch-home__flow-step">
            <view class="ch-home__flow-num">2</view>
            <text class="ch-home__flow-label">平台审核</text>
          </view>
          <view class="ch-home__flow-line"></view>
          <view class="ch-home__flow-step">
            <view class="ch-home__flow-num">3</view>
            <text class="ch-home__flow-label">认证完成</text>
          </view>
        </view>

        <view class="ch-home__block-cta">
          <u-button v-if="authStatus !== 'pending'" text="立即申请成为渠道方" type="primary" shape="circle" @click="goApply"></u-button>
          <u-button v-else text="查看审核进度" shape="circle" plain @click="goApplyStatus"></u-button>
        </view>
      </view>
    </view>

    <!-- 已认证内容 -->
    <template v-if="authStatus === 'approved'">
      <!-- A. 4格 Hero KPI -->
      <view class="ch-stats">
        <view class="ch-stats__cell" @tap="goStudents">
          <text class="ch-stats__num">{{ monthly.newStudentCount || 0 }}</text>
          <text class="ch-stats__label">新增学生</text>
        </view>
        <view class="ch-stats__cell" @tap="goOrders">
          <text class="ch-stats__num">{{ monthly.orderCount || 0 }}</text>
          <text class="ch-stats__label">订单数</text>
        </view>
        <view class="ch-stats__cell" @tap="goRebate">
          <text class="ch-stats__num">¥{{ formatK(monthly.rebateEstimatedAmount) }}</text>
          <text class="ch-stats__label">预估返点</text>
        </view>
        <view class="ch-stats__cell" @tap="goOrders">
          <text class="ch-stats__num">¥{{ formatK(monthly.orderPaidAmount) }}</text>
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
          <view class="ch-func-icon ch-func-icon--students">👨‍🎓</view>
          <text class="ch-func-label">学生管理</text>
        </view>
        <view class="ch-func-item" @tap="goBatchEnroll">
          <view class="ch-func-icon ch-func-icon--enroll">📋</view>
          <text class="ch-func-label">代报名</text>
        </view>
        <view class="ch-func-item" @tap="goOrders">
          <view class="ch-func-icon ch-func-icon--orders">🧾</view>
          <text class="ch-func-label">订单管理</text>
        </view>
        <view class="ch-func-item" @tap="goRebate">
          <view class="ch-func-icon ch-func-icon--rebate">💰</view>
          <text class="ch-func-label">返点中心</text>
        </view>
        <view class="ch-func-item" @tap="goSettlement">
          <view class="ch-func-icon ch-func-icon--settlement">📄</view>
          <text class="ch-func-label">提现结算</text>
        </view>
        <view class="ch-func-item" @tap="goData">
          <view class="ch-func-icon ch-func-icon--data">📊</view>
          <text class="ch-func-label">报名数据</text>
        </view>
        <view class="ch-func-item" @tap="goData">
          <view class="ch-func-icon ch-func-icon--analysis">📈</view>
          <text class="ch-func-label">经营分析</text>
        </view>
        <view class="ch-func-item" @tap="goAppointment">
          <view class="ch-func-icon ch-func-icon--appointment">📅</view>
          <text class="ch-func-label">团队预约</text>
        </view>
        <view class="ch-func-item" @tap="showComingSoon">
          <view class="ch-func-icon ch-func-icon--ai">🤖</view>
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
            <u-tag :text="item.statusText || ''" type="warning" size="mini" plain shape="circle"></u-tag>
          </view>
        </view>
        <u-empty v-if="!recentOrders.length" mode="data" text="暂无待支付订单"></u-empty>
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
        <u-empty v-if="!recentStudents.length" mode="data" text="暂无绑定学生"></u-empty>
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
  'linear-gradient(135deg, #283593, #3949AB)',
  'linear-gradient(135deg, #E65100, #FF7043)',
  'linear-gradient(135deg, #1B5E20, #66BB6A)',
  'linear-gradient(135deg, #880E4F, #F06292)',
  'linear-gradient(135deg, #4527A0, #7E57C2)'
]

export default {
  data() {
    return {
      skeletonShow: true, // [visual-effect]
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
      const info = store.userInfo || {}
      const roles = store.roles || []
      // 已被授予 jst_channel 角色 = 认证已通过（兜底依据，防止 apply 接口返空 applyStatus 误拦截）
      const hasChannelRole = roles.includes('jst_channel') || info.userType === 'channel'

      try {
        const apply = await getMyChannelApply()
        if (apply && apply.applyStatus) {
          this.authStatus = apply.applyStatus
          this.channelName = apply.applyName || info.nickname || '渠道方'
          this.channelType = apply.channelType || ''
          this.schoolInfo = apply.school || ''
        } else if (hasChannelRole) {
          // apply 返回空但用户已有 channel role → 视为已认证
          this.authStatus = 'approved'
          this.channelName = info.nickname || info.channelName || '渠道方'
        }
      } catch (e) {
        // apply 接口异常时，用 role 兜底
        if (hasChannelRole) {
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
    goBatchEnroll() { uni.navigateTo({ url: '/pages-sub/channel/batch-enroll' }) },
    goAppointment() { uni.navigateTo({ url: '/pages-sub/channel/appointment' }) },
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
@import '@/styles/design-tokens.scss';

.ch-home { min-height: 100vh; padding-bottom: calc(#{$jst-space-xxl} + env(safe-area-inset-bottom)); background: $jst-bg-subtle; }

/* Hero */
.ch-hero { background: $jst-hero-gradient; padding: 88rpx $jst-space-xl $jst-space-xxl; position: relative; overflow: hidden; border-bottom-left-radius: $jst-radius-xl; border-bottom-right-radius: $jst-radius-xl; }
.ch-hero::before { content: ''; position: absolute; top: -60rpx; right: -40rpx; width: 280rpx; height: 280rpx; border-radius: 50%; background: rgba($jst-text-inverse, 0.06); }
.ch-hero__nav { display: flex; align-items: center; margin-bottom: $jst-space-xl; position: relative; z-index: 1; }
.ch-hero__back { width: 72rpx; height: 72rpx; border-radius: $jst-radius-sm; background: rgba($jst-text-inverse, 0.15); display: flex; align-items: center; justify-content: center; font-size: $jst-font-xl; color: $jst-text-inverse; margin-right: $jst-space-lg; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.ch-hero__title { flex: 1; font-size: 34rpx; font-weight: $jst-weight-semibold; color: $jst-text-inverse; }
.ch-hero__msg { width: 68rpx; height: 68rpx; border-radius: 50%; background: rgba($jst-text-inverse, 0.12); display: flex; align-items: center; justify-content: center; font-size: $jst-font-lg; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.ch-hero__identity { display: flex; align-items: center; gap: 28rpx; margin-bottom: $jst-space-xl; position: relative; z-index: 1; }
.ch-hero__avatar { width: 112rpx; height: 112rpx; border-radius: 50%; background: linear-gradient(135deg, $jst-gold, $jst-warning); display: flex; align-items: center; justify-content: center; font-size: 52rpx; border: 6rpx solid rgba($jst-text-inverse, 0.3); }
.ch-hero__info { flex: 1; }
.ch-hero__name { display: block; font-size: $jst-font-xl; font-weight: $jst-weight-semibold; color: $jst-text-inverse; }
.ch-hero__badges { display: flex; flex-wrap: wrap; gap: $jst-space-sm; margin-top: $jst-space-sm; }
.ch-hero__sub { display: block; margin-top: $jst-space-xs; font-size: $jst-font-sm; color: rgba($jst-text-inverse, 0.65); }
.ch-hero__switch { display: flex; align-items: center; gap: $jst-space-sm; position: relative; z-index: 1; }

/* 未认证拦截 */
.ch-home__block { padding: 48rpx $jst-space-xl; }
.ch-home__block-card { display: flex; flex-direction: column; gap: $jst-space-md; padding: 56rpx 40rpx; background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; text-align: center; }
.ch-home__block-icon-wrap { display: flex; align-items: center; justify-content: center; width: 128rpx; height: 128rpx; border-radius: 50%; background: linear-gradient(135deg, $jst-brand-light, rgba($jst-gold, 0.15)); margin: 0 auto $jst-space-md; box-shadow: 0 8rpx 24rpx rgba($jst-brand, 0.12); }
.ch-home__block-icon { display: block; font-size: 64rpx; }
.ch-home__block-title { display: block; font-size: $jst-font-lg; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.ch-home__block-desc { display: block; font-size: $jst-font-sm; color: $jst-text-secondary; line-height: 1.6; }

/* 3 特权预览 */
.ch-home__perks { display: grid; grid-template-columns: repeat(3, 1fr); gap: $jst-space-sm; margin: $jst-space-lg 0 $jst-space-md; padding: $jst-space-lg 0; border-top: 2rpx dashed $jst-border; border-bottom: 2rpx dashed $jst-border; }
.ch-home__perk { display: flex; flex-direction: column; align-items: center; gap: 6rpx; padding: 0 $jst-space-xs; }
.ch-home__perk-emoji { font-size: 44rpx; line-height: 1; }
.ch-home__perk-label { font-size: $jst-font-sm; font-weight: $jst-weight-semibold; color: $jst-text-primary; margin-top: 6rpx; }
.ch-home__perk-desc { font-size: 22rpx; color: $jst-text-secondary; text-align: center; line-height: 1.4; }

/* 3 步流程 */
.ch-home__flow { display: flex; align-items: center; justify-content: center; gap: $jst-space-xs; margin: $jst-space-md 0 $jst-space-lg; }
.ch-home__flow-step { display: flex; flex-direction: column; align-items: center; gap: 6rpx; }
.ch-home__flow-num { width: 48rpx; height: 48rpx; border-radius: 50%; background: $jst-brand-light; color: $jst-brand; font-size: $jst-font-sm; font-weight: $jst-weight-semibold; display: flex; align-items: center; justify-content: center; }
.ch-home__flow-label { font-size: 22rpx; color: $jst-text-secondary; }
.ch-home__flow-line { width: 48rpx; height: 2rpx; background: $jst-border; margin-bottom: 24rpx; }

.ch-home__block-cta { margin-top: $jst-space-md; }

/* 4格 KPI */
.ch-stats { display: grid; grid-template-columns: repeat(4, 1fr); margin: $jst-space-lg $jst-space-xl 0; background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; overflow: hidden; }
.ch-stats__cell { padding: 28rpx $jst-space-md; text-align: center; border-right: 2rpx solid $jst-border; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.ch-stats__cell:last-child { border-right: none; }
.ch-stats__num { display: block; font-size: $jst-font-xxl; font-weight: $jst-weight-semibold; color: $jst-brand; line-height: 1; margin-bottom: $jst-space-xs; }
.ch-stats__label { display: block; font-size: $jst-font-xs; color: $jst-text-secondary; }

/* 返点横幅 */
.ch-rebate-banner { display: flex; align-items: center; margin: $jst-space-lg $jst-space-xl 0; padding: 28rpx $jst-space-lg; background: linear-gradient(135deg, $jst-brand, $jst-brand-dark); border-radius: $jst-radius-lg; box-shadow: $jst-shadow-md; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.ch-rebate-banner__item { flex: 1; text-align: center; }
.ch-rebate-banner__num { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-inverse; }
.ch-rebate-banner__num--gold { color: $jst-gold; font-size: $jst-font-lg; }
.ch-rebate-banner__label { display: block; margin-top: 6rpx; font-size: $jst-font-xs; color: rgba($jst-text-inverse, 0.7); }
.ch-rebate-banner__divider { width: 2rpx; height: $jst-space-xxl; background: rgba($jst-text-inverse, 0.2); }
.ch-rebate-banner__arrow { font-size: $jst-font-xl; color: rgba($jst-text-inverse, 0.6); margin-left: $jst-space-sm; }

/* 二维码入口 */
.ch-qr-entry { display: flex; align-items: center; gap: 28rpx; margin: $jst-space-lg $jst-space-xl 0; padding: $jst-space-xl; background: linear-gradient(135deg, $jst-brand 0%, $jst-brand-dark 100%); border-radius: $jst-radius-lg; box-shadow: $jst-shadow-md; position: relative; overflow: hidden; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.ch-qr-entry::before { content: ''; position: absolute; top: -40rpx; right: -40rpx; width: 200rpx; height: 200rpx; border-radius: 50%; background: rgba($jst-text-inverse, 0.06); }
.ch-qr-entry__icon { width: 104rpx; height: 104rpx; border-radius: $jst-radius-md; background: rgba($jst-text-inverse, 0.18); display: flex; align-items: center; justify-content: center; font-size: 56rpx; }
.ch-qr-entry__info { flex: 1; }
.ch-qr-entry__title { display: block; font-size: $jst-font-lg; font-weight: $jst-weight-semibold; color: $jst-text-inverse; }
.ch-qr-entry__desc { display: block; margin-top: $jst-space-xs; font-size: $jst-font-sm; color: rgba($jst-text-inverse, 0.75); line-height: 1.5; }
.ch-qr-entry__arrow { font-size: $jst-font-xxl; color: rgba($jst-text-inverse, 0.6); }

/* 功能宫格 */
.ch-func-grid { display: grid; grid-template-columns: repeat(3, 1fr); margin: $jst-space-lg $jst-space-xl 0; background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; overflow: hidden; }
.ch-func-item { padding: $jst-space-xl $jst-space-md; text-align: center; border-right: 2rpx solid $jst-border; border-bottom: 2rpx solid $jst-border; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.ch-func-item:nth-child(3n) { border-right: none; }
.ch-func-item:nth-last-child(-n+3) { border-bottom: none; }
.ch-func-icon { width: 88rpx; height: 88rpx; border-radius: $jst-radius-md; display: flex; align-items: center; justify-content: center; font-size: $jst-font-xxl; margin: 0 auto $jst-space-md; }
.ch-func-icon--students { background: $jst-brand-light; }
.ch-func-icon--enroll { background: rgba($jst-brand, 0.12); }
.ch-func-icon--orders { background: $jst-warning-light; }
.ch-func-icon--rebate { background: $jst-gold-light; }
.ch-func-icon--settlement { background: rgba($jst-indigo-light, 0.12); }
.ch-func-icon--data { background: rgba($jst-success, 0.12); }
.ch-func-icon--analysis { background: rgba($jst-indigo, 0.12); }
.ch-func-icon--appointment { background: rgba($jst-info, 0.15); }
.ch-func-icon--ai { background: rgba($jst-brand-dark, 0.12); }
.ch-func-label { font-size: $jst-font-sm; font-weight: $jst-weight-medium; color: $jst-text-regular; }

/* 通用 section */
.ch-section { margin: $jst-space-lg $jst-space-xl 0; background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; overflow: hidden; }
.ch-section__header { display: flex; align-items: center; justify-content: space-between; padding: 28rpx $jst-space-xl 20rpx; border-bottom: 2rpx solid $jst-border; }
.ch-section__title { font-size: $jst-font-md; font-weight: $jst-weight-semibold; color: $jst-text-primary; display: flex; align-items: center; gap: $jst-space-sm; }
.ch-section__title::before { content: ''; width: 6rpx; height: 30rpx; background: $jst-brand; border-radius: $jst-radius-xs; }
.ch-section__more { font-size: 26rpx; color: $jst-brand; font-weight: $jst-weight-medium; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }

/* 权益区 */
.ch-rights-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 0; }
.ch-rights-item { display: flex; flex-direction: column; align-items: center; padding: 28rpx $jst-space-md; border-right: 2rpx solid $jst-border; border-bottom: 2rpx solid $jst-border; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.ch-rights-item:nth-child(2n) { border-right: none; }
.ch-rights-item:nth-last-child(-n+2) { border-bottom: none; }
.ch-rights-item__icon { font-size: 40rpx; margin-bottom: $jst-space-xs; }
.ch-rights-item__name { font-size: $jst-font-sm; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.ch-rights-item__quota { font-size: 22rpx; color: $jst-text-secondary; margin-top: 4rpx; }

/* 最近订单 */
.ch-order-item { display: flex; align-items: center; gap: $jst-space-lg; padding: $jst-space-lg $jst-space-xl; border-bottom: 2rpx solid $jst-border; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.ch-order-item:last-child { border-bottom: none; }
.ch-order-item__avatar { width: 76rpx; height: 76rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.ch-order-item__avatar-text { font-size: $jst-font-lg; font-weight: $jst-weight-semibold; color: $jst-text-inverse; }
.ch-order-item__info { flex: 1; min-width: 0; }
.ch-order-item__name { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.ch-order-item__contest { display: block; margin-top: 4rpx; font-size: 22rpx; color: $jst-text-secondary; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.ch-order-item__right { text-align: right; flex-shrink: 0; }
.ch-order-item__amount { display: block; font-size: $jst-font-md; font-weight: $jst-weight-semibold; color: $jst-warning; }
.ch-order-item__status { display: block; margin-top: 4rpx; font-size: $jst-font-xs; color: $jst-text-secondary; }

/* 学生概览 */
.ch-student-item { display: flex; align-items: center; gap: $jst-space-lg; padding: $jst-space-lg $jst-space-xl; border-bottom: 2rpx solid $jst-border; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.ch-student-item:last-child { border-bottom: none; }
.ch-student-item__avatar { width: 76rpx; height: 76rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: $jst-font-lg; font-weight: $jst-weight-semibold; color: $jst-text-inverse; }
.ch-student-item__info { flex: 1; }
.ch-student-item__name { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.ch-student-item__meta { display: block; margin-top: 4rpx; font-size: 22rpx; color: $jst-text-secondary; }

</style>
