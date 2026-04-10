<!-- 中文注释: 渠道方 · 报名数据（轻量看板）(E1-CH-6)
     对应原型: 小程序原型图/channel-data.png + channel-data.html
     功能: 时间周期选择 + 4KPI + 趋势表格(替代图表) + 排行榜 + 深度分析引导
     数据来源: GET /jst/wx/channel/dashboard/stats + top-contests + top-students -->
<template>
  <view class="data-page">
    <!-- 导航 -->
    <view class="data-header">
      <view class="data-header__back" @tap="goBack">←</view>
      <text class="data-header__title">报名数据</text>
      <text class="data-header__sub">{{ channelName }}</text>
    </view>

    <!-- A. 时间周期选择器 -->
    <view class="data-time">
      <view class="data-time__chips">
        <view v-for="p in periods" :key="p.key" :class="['data-time__chip', activePeriod === p.key ? 'data-time__chip--active' : '']" @tap="switchPeriod(p.key)">
          {{ p.label }}
        </view>
      </view>
    </view>

    <!-- B. 4格 KPI -->
    <view class="data-kpi">
      <view class="data-kpi__card data-kpi__card--accent">
        <text class="data-kpi__label">代报名总数</text>
        <text class="data-kpi__num">{{ stats.enrollTotal || 0 }}</text>
        <text class="data-kpi__sub">绑定学生 {{ stats.bindStudents || 0 }} 人</text>
        <text v-if="stats.enrollTrend" class="data-kpi__trend data-kpi__trend--up">↑ 较上月 +{{ stats.enrollTrend }}</text>
      </view>
      <view class="data-kpi__card">
        <text class="data-kpi__label">累计支付金额</text>
        <text class="data-kpi__num" style="color: #3F51B5;">¥{{ formatK(stats.totalPaidAmount) }}</text>
        <text class="data-kpi__sub">本月 ¥{{ stats.monthlyPaid || 0 }}</text>
      </view>
      <view class="data-kpi__card">
        <text class="data-kpi__label">退款金额</text>
        <text class="data-kpi__num" style="color: #E74C3C;">¥{{ stats.refundAmount || 0 }}</text>
        <text class="data-kpi__sub">退款 {{ stats.refundCount || 0 }} 笔</text>
      </view>
      <view class="data-kpi__card">
        <text class="data-kpi__label">返点金额</text>
        <text class="data-kpi__num" style="color: #F5A623;">¥{{ stats.rebateTotal || 0 }}</text>
        <text class="data-kpi__sub">待结算 ¥{{ stats.rebatePending || 0 }}</text>
      </view>
    </view>

    <!-- 学生报名状态分布 -->
    <view class="data-section">
      <view class="data-section__header">
        <text class="data-section__title">学生报名状态</text>
        <text class="data-section__more" @tap="goStudents">管理学生 ›</text>
      </view>
      <view class="data-status-grid">
        <view class="data-status-item">
          <text class="data-status-num" style="color: #27AE60;">{{ stats.enrolledCount || 0 }}</text>
          <text class="data-status-label">已报名</text>
        </view>
        <view class="data-status-item">
          <text class="data-status-num" style="color: #F39C12;">{{ stats.pendingPayCount || 0 }}</text>
          <text class="data-status-label">待支付</text>
        </view>
        <view class="data-status-item">
          <text class="data-status-num" style="color: #3498DB;">{{ stats.reviewingCount || 0 }}</text>
          <text class="data-status-label">审核中</text>
        </view>
      </view>
    </view>

    <!-- C. 趋势表格（替代图表，不引入新依赖） -->
    <view class="data-section">
      <view class="data-section__header">
        <text class="data-section__title">学科报名分布</text>
      </view>
      <view v-for="(bar, idx) in subjectBars" :key="idx" class="data-bar-row">
        <text class="data-bar-label">{{ bar.name }}</text>
        <view class="data-bar-track">
          <view class="data-bar-fill" :style="{ width: bar.percent + '%', background: barColors[idx % barColors.length] }"></view>
        </view>
        <text class="data-bar-val">{{ bar.count }} 人</text>
      </view>
      <view v-if="!subjectBars.length" class="data-empty-hint">暂无数据</view>
    </view>

    <!-- D. 排行榜 -->
    <view class="data-section">
      <view class="data-section__header">
        <text class="data-section__title">报名热门赛事 TOP5</text>
        <text class="data-section__more" @tap="goContestList">浏览全部 ›</text>
      </view>
      <view v-for="(item, idx) in topContests" :key="item.contestId || idx" class="data-rank-item" @tap="goContestDetail(item.contestId)">
        <view class="data-rank-num" :style="{ background: rankColors[idx] || '#C5CAE9' }">{{ idx + 1 }}</view>
        <view class="data-rank-info">
          <text class="data-rank-name">{{ item.contestName || '--' }}</text>
          <text class="data-rank-sub">{{ item.category || '' }}</text>
        </view>
        <text class="data-rank-cnt">{{ item.enrollCount || 0 }}人</text>
      </view>
      <view v-if="!topContests.length" class="data-empty-hint">暂无数据</view>
    </view>

    <!-- 近期报名记录 -->
    <view class="data-section">
      <view class="data-section__header">
        <text class="data-section__title">近期报名记录</text>
        <text class="data-section__more" @tap="goOrders">全部订单 ›</text>
      </view>
      <view v-for="item in recentEnrolls" :key="item.enrollId || item.orderId" class="data-enroll-item" @tap="goOrderDetail(item.orderId)">
        <view class="data-enroll-avatar" :style="{ background: getAvatarColor(item.studentName) }">
          <text>{{ (item.studentName || '').slice(0, 1) }}</text>
        </view>
        <view class="data-enroll-info">
          <text class="data-enroll-name">{{ item.studentName || '--' }}</text>
          <text class="data-enroll-contest">{{ item.contestName || '--' }} · {{ item.payAmount > 0 ? '¥' + item.payAmount : '免费' }}</text>
        </view>
        <text class="data-enroll-time">{{ formatRelative(item.createTime) }}</text>
      </view>
      <view v-if="!recentEnrolls.length" class="data-empty-hint">暂无记录</view>
    </view>

    <!-- E. 底部深度分析引导 -->
    <view class="data-analysis-entry">
      <text class="data-analysis-icon">📈</text>
      <view class="data-analysis-info">
        <text class="data-analysis-title">深度经营分析</text>
        <text class="data-analysis-desc">收入·用券·返点·结算·风险指标多维分析</text>
      </view>
      <view class="data-analysis-badge">即将上线</view>
    </view>

    <view style="height: 48rpx;"></view>
  </view>
</template>

<script>
import { getChannelStats, getTopContests, getTopStudents, getChannelOrders } from '@/api/channel'
import { useUserStore } from '@/store/user'

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
      channelName: '',
      activePeriod: 'all',
      periods: [
        { key: 'all', label: '全部' },
        { key: 'month', label: '本月' },
        { key: '3month', label: '近3月' },
        { key: '6month', label: '近6月' }
      ],
      stats: {},
      subjectBars: [],
      topContests: [],
      recentEnrolls: [],
      barColors: [
        'linear-gradient(90deg, #3F51B5, #5C6BC0)',
        'linear-gradient(90deg, #5C6BC0, #7986CB)',
        'linear-gradient(90deg, #7986CB, #9FA8DA)',
        'linear-gradient(90deg, #9FA8DA, #C5CAE9)'
      ],
      rankColors: ['#3F51B5', '#5C6BC0', '#7986CB', '#9FA8DA', '#C5CAE9']
    }
  },
  onShow() { this.initChannel(); this.loadAll() },
  methods: {
    initChannel() {
      const store = useUserStore()
      const info = store.userInfo || {}
      this.channelName = info.nickname || info.channelName || '渠道方'
    },

    async loadAll() {
      await Promise.allSettled([
        this.loadStats(),
        this.loadTopContests(),
        this.loadRecentEnrolls()
      ])
    },

    async loadStats() {
      try {
        const res = await getChannelStats({ period: this.activePeriod })
        this.stats = res || {}
        // 学科分布
        if (res && Array.isArray(res.subjectDistribution)) {
          const maxCount = Math.max(...res.subjectDistribution.map(s => s.count || 0), 1)
          this.subjectBars = res.subjectDistribution.map(s => ({
            name: s.name || s.subject || '--',
            count: s.count || 0,
            percent: Math.round((s.count || 0) / maxCount * 100)
          }))
        }
      } catch (e) {
        this.stats = {}
      }
    },

    async loadTopContests() {
      try {
        const res = await getTopContests({ period: this.activePeriod, limit: 5 })
        this.topContests = Array.isArray(res) ? res : (res && res.rows) || []
      } catch (e) {
        this.topContests = []
      }
    },

    async loadRecentEnrolls() {
      try {
        const res = await getChannelOrders({ pageNum: 1, pageSize: 4 })
        this.recentEnrolls = (res && res.rows) || (Array.isArray(res) ? res : [])
      } catch (e) {
        this.recentEnrolls = []
      }
    },

    switchPeriod(key) {
      this.activePeriod = key
      this.loadAll()
    },

    formatK(val) {
      if (!val) return '0'
      const n = Number(val)
      if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
      return String(n)
    },

    formatRelative(v) {
      if (!v) return '--'
      const now = Date.now()
      const d = new Date(String(v).replace('T', ' ')).getTime()
      const diff = now - d
      if (diff < 86400000) return '今天'
      if (diff < 172800000) return '昨天'
      if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
      return String(v).slice(0, 10)
    },

    getAvatarColor(name) {
      if (!name) return AVATAR_COLORS[0]
      return AVATAR_COLORS[name.charCodeAt(0) % AVATAR_COLORS.length]
    },

    goStudents() { uni.navigateTo({ url: '/pages-sub/channel/students' }) },
    goOrders() { uni.navigateTo({ url: '/pages-sub/channel/orders' }) },
    goContestList() { uni.switchTab({ url: '/pages/contest/list' }) },
    goContestDetail(id) { if (id) uni.navigateTo({ url: '/pages-sub/contest/detail?id=' + id }) },
    goOrderDetail(id) { if (id) uni.navigateTo({ url: '/pages-sub/channel/order-detail?id=' + id }) },
    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
.data-page { min-height: 100vh; padding-bottom: 48rpx; background: var(--jst-color-page-bg); }

.data-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: #fff; border-bottom: 2rpx solid var(--jst-color-border); position: sticky; top: 0; z-index: 40; }
.data-header__back { width: 72rpx; height: 72rpx; border-radius: var(--jst-radius-sm); background: var(--jst-color-page-bg); display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.data-header__title { flex: 1; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }
.data-header__sub { font-size: 26rpx; color: var(--jst-color-text-tertiary); }

/* 时间选择器 */
.data-time { padding: 20rpx 32rpx; background: #fff; border-bottom: 2rpx solid var(--jst-color-border); position: sticky; top: 200rpx; z-index: 39; }
.data-time__chips { display: flex; gap: 12rpx; overflow-x: auto; }
.data-time__chips::-webkit-scrollbar { display: none; }
.data-time__chip { flex-shrink: 0; height: 60rpx; padding: 0 28rpx; border-radius: var(--jst-radius-full); border: 3rpx solid var(--jst-color-border); background: var(--jst-color-page-bg); font-size: 24rpx; font-weight: 500; color: var(--jst-color-text-secondary); display: flex; align-items: center; white-space: nowrap; }
.data-time__chip--active { border-color: #3F51B5; background: #EEF0FF; color: #3F51B5; font-weight: 700; }

/* 4 KPI */
.data-kpi { display: grid; grid-template-columns: 1fr 1fr; gap: 20rpx; padding: 28rpx 32rpx 0; }
.data-kpi__card { background: #fff; border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); padding: 32rpx; }
.data-kpi__card--accent { background: linear-gradient(145deg, #3F51B5, #5C6BC0); box-shadow: 0 8rpx 32rpx rgba(63,81,181,0.3); }
.data-kpi__label { display: block; font-size: 24rpx; color: var(--jst-color-text-tertiary); margin-bottom: 16rpx; }
.data-kpi__card--accent .data-kpi__label { color: rgba(255,255,255,0.7); }
.data-kpi__num { display: block; font-size: 60rpx; font-weight: 900; color: var(--jst-color-text); line-height: 1; letter-spacing: -2rpx; }
.data-kpi__card--accent .data-kpi__num { color: #fff; }
.data-kpi__sub { display: block; margin-top: 12rpx; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.data-kpi__card--accent .data-kpi__sub { color: rgba(255,255,255,0.6); }
.data-kpi__trend { display: block; margin-top: 12rpx; font-size: 22rpx; font-weight: 600; }
.data-kpi__trend--up { color: #27AE60; }
.data-kpi__card--accent .data-kpi__trend--up { color: #A5D6A7; }

/* 通用 section */
.data-section { margin: 24rpx 32rpx 0; background: #fff; border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); overflow: hidden; }
.data-section__header { display: flex; align-items: center; justify-content: space-between; padding: 28rpx 32rpx 20rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.data-section__title { font-size: 30rpx; font-weight: 700; color: var(--jst-color-text); display: flex; align-items: center; gap: 12rpx; }
.data-section__title::before { content: ''; width: 6rpx; height: 30rpx; background: #3F51B5; border-radius: 4rpx; }
.data-section__more { font-size: 26rpx; color: #3F51B5; font-weight: 500; }

/* 状态分布 */
.data-status-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 0; padding: 8rpx 0; }
.data-status-item { padding: 28rpx 16rpx; text-align: center; border-right: 2rpx solid var(--jst-color-border); }
.data-status-item:last-child { border-right: none; }
.data-status-num { display: block; font-size: 44rpx; font-weight: 900; line-height: 1; margin-bottom: 8rpx; }
.data-status-label { display: block; font-size: 22rpx; color: var(--jst-color-text-tertiary); }

/* 横向进度条 */
.data-bar-row { display: flex; align-items: center; gap: 20rpx; padding: 20rpx 32rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.data-bar-row:last-child { border-bottom: none; }
.data-bar-label { font-size: 26rpx; color: var(--jst-color-text-secondary); width: 128rpx; flex-shrink: 0; }
.data-bar-track { flex: 1; height: 16rpx; background: var(--jst-color-border); border-radius: 8rpx; overflow: hidden; }
.data-bar-fill { height: 100%; border-radius: 8rpx; transition: width 0.8s ease; }
.data-bar-val { font-size: 24rpx; font-weight: 700; color: var(--jst-color-text-secondary); width: 80rpx; text-align: right; flex-shrink: 0; }

/* 排行榜 */
.data-rank-item { display: flex; align-items: center; gap: 24rpx; padding: 24rpx 32rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.data-rank-item:last-child { border-bottom: none; }
.data-rank-num { width: 48rpx; height: 48rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24rpx; font-weight: 700; color: #fff; flex-shrink: 0; }
.data-rank-info { flex: 1; min-width: 0; }
.data-rank-name { display: block; font-size: 26rpx; font-weight: 600; color: var(--jst-color-text); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.data-rank-sub { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.data-rank-cnt { font-size: 32rpx; font-weight: 800; color: #3F51B5; flex-shrink: 0; }

/* 近期报名 */
.data-enroll-item { display: flex; align-items: center; gap: 20rpx; padding: 22rpx 32rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.data-enroll-item:last-child { border-bottom: none; }
.data-enroll-avatar { width: 72rpx; height: 72rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 32rpx; font-weight: 700; color: #fff; flex-shrink: 0; }
.data-enroll-info { flex: 1; min-width: 0; }
.data-enroll-name { display: block; font-size: 26rpx; font-weight: 700; color: var(--jst-color-text); }
.data-enroll-contest { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.data-enroll-time { font-size: 22rpx; color: #ccc; flex-shrink: 0; }

.data-empty-hint { padding: 40rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }

/* 深度分析引导 */
.data-analysis-entry { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: linear-gradient(90deg, #EBF4FC, #fff); border: 2rpx solid rgba(63,81,181,0.15); border-radius: var(--jst-radius-lg); display: flex; align-items: center; gap: 24rpx; opacity: 0.7; }
.data-analysis-icon { font-size: 56rpx; flex-shrink: 0; }
.data-analysis-info { flex: 1; }
.data-analysis-title { display: block; font-size: 28rpx; font-weight: 700; color: #3F51B5; }
.data-analysis-desc { display: block; margin-top: 4rpx; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.data-analysis-badge { padding: 8rpx 20rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-border); font-size: 22rpx; color: var(--jst-color-text-tertiary); font-weight: 600; flex-shrink: 0; }
</style>
