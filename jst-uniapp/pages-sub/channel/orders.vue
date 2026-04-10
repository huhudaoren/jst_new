<!-- 中文注释: 渠道方 · 渠道订单 (E1-CH-4 完整重构)
     对应原型: 小程序原型图/channel-orders.png + channel-orders.html
     功能: 6状态Tab + 搜索/筛选 + 订单卡片 + 触底加载
     数据来源: GET /jst/wx/channel/orders -->
<template>
  <view class="ord-page">
    <!-- 导航 -->
    <view class="ord-header">
      <view class="ord-header__back" @tap="goBack">←</view>
      <text class="ord-header__title">代报名订单</text>
    </view>

    <!-- 统计横条 -->
    <view class="ord-stats">
      <view class="ord-stats__cell" @tap="switchTab('')">
        <text class="ord-stats__num">{{ stats.total || 0 }}</text>
        <text class="ord-stats__label">全部订单</text>
      </view>
      <view class="ord-stats__cell" @tap="switchTab('pending_pay')">
        <text class="ord-stats__num" style="color: #FF5722;">{{ stats.pendingPay || 0 }}</text>
        <text class="ord-stats__label">待支付</text>
      </view>
      <view class="ord-stats__cell" @tap="switchTab('paid')">
        <text class="ord-stats__num" style="color: #27AE60;">{{ stats.paid || 0 }}</text>
        <text class="ord-stats__label">已支付</text>
      </view>
      <view class="ord-stats__cell">
        <text class="ord-stats__num">¥{{ formatK(stats.totalAmount) }}</text>
        <text class="ord-stats__label">累计金额</text>
      </view>
    </view>

    <!-- 状态 Tab -->
    <view class="ord-tabs">
      <view v-for="tab in statusTabs" :key="tab.key" :class="['ord-tab', activeStatus === tab.key ? 'ord-tab--active' : '']" @tap="switchTab(tab.key)">
        <text>{{ tab.label }}</text>
        <text v-if="tab.badge" class="ord-tab__badge">{{ tab.badge }}</text>
      </view>
    </view>

    <!-- 搜索 + 筛选 -->
    <view class="ord-filter">
      <view class="ord-filter__search">
        <text class="ord-filter__icon">🔍</text>
        <input class="ord-filter__input" type="text" placeholder="搜索学生姓名/订单号" v-model="keyword" @confirm="search" />
      </view>
    </view>

    <!-- 订单列表 -->
    <view class="ord-list">
      <view v-for="item in list" :key="item.orderId" class="ord-card" @tap="goDetail(item.orderId)">
        <view class="ord-card__header">
          <text class="ord-card__contest">{{ item.contestName || '--' }}</text>
          <view class="ord-card__status" :class="'ord-card__status--' + getStatusType(item.orderStatus)">
            {{ item.statusText || getStatusText(item.orderStatus) }}
          </view>
        </view>

        <!-- 学生行 -->
        <view class="ord-card__student">
          <view class="ord-card__avatar" :style="{ background: getAvatarColor(item.studentName) }">
            <text>{{ (item.studentName || '').slice(0, 1) }}</text>
          </view>
          <text class="ord-card__sname">{{ item.studentName || '--' }}</text>
          <text class="ord-card__school">{{ item.schoolName || '' }}</text>
          <text class="ord-card__amount" :style="{ color: item.payAmount > 0 ? '#FF5722' : '#27AE60' }">
            {{ item.payAmount > 0 ? '¥' + item.payAmount : '免费' }}
          </text>
        </view>

        <!-- 底部 -->
        <view class="ord-card__footer">
          <text class="ord-card__total">
            合计 <text class="ord-card__total-val">{{ item.payAmount > 0 ? '¥' + item.payAmount : '免费' }}</text>
            <text v-if="item.createTime"> · {{ formatTime(item.createTime) }}</text>
          </text>
          <view class="ord-card__actions">
            <view class="ord-card__btn ord-card__btn--outline" @tap.stop="goDetail(item.orderId)">详情</view>
          </view>
        </view>
      </view>

      <view v-if="hasMore" class="ord-loadmore" @tap="loadMore">
        <text>加载更多订单</text>
      </view>

      <view v-if="!list.length && !loading" class="ord-empty">
        <text class="ord-empty__icon">🧾</text>
        <text class="ord-empty__text">暂无订单</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getChannelOrders, getChannelDashboardStats } from '@/api/channel'

const AVATAR_COLORS = [
  'linear-gradient(135deg, #3F51B5, #5C6BC0)',
  'linear-gradient(135deg, #E65100, #FF7043)',
  'linear-gradient(135deg, #1B5E20, #66BB6A)',
  'linear-gradient(135deg, #880E4F, #F06292)',
  'linear-gradient(135deg, #4527A0, #7E57C2)'
]

const STATUS_MAP = {
  pending_pay: '待支付',
  paid: '已支付',
  voucher_review: '对公转账',
  refunded: '已退款',
  cancelled: '已关闭'
}

export default {
  data() {
    return {
      loading: false,
      keyword: '',
      activeStatus: '',
      statusTabs: [
        { key: '', label: '全部', badge: '' },
        { key: 'pending_pay', label: '待支付', badge: '' },
        { key: 'paid', label: '已支付', badge: '' },
        { key: 'voucher_review', label: '对公转账', badge: '' },
        { key: 'refunded', label: '已退款', badge: '' },
        { key: 'cancelled', label: '已关闭', badge: '' }
      ],
      stats: {},
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 20,
      hasMore: false
    }
  },
  onShow() { this.loadStats(); this.pageNum = 1; this.loadList() },
  // 触底加载
  onReachBottom() { if (this.hasMore) this.loadMore() },
  methods: {
    async loadStats() {
      try {
        const res = await getChannelDashboardStats()
        this.stats = res || {}
      } catch (e) {}
    },

    async loadList() {
      this.loading = true
      try {
        const params = { pageNum: this.pageNum, pageSize: this.pageSize }
        if (this.activeStatus) params.status = this.activeStatus
        if (this.keyword) params.keyword = this.keyword
        const res = await getChannelOrders(params)
        const rows = (res && res.rows) || (Array.isArray(res) ? res : [])
        const total = (res && res.total) || rows.length
        if (this.pageNum === 1) {
          this.list = rows
        } else {
          this.list = this.list.concat(rows)
        }
        this.total = total
        this.hasMore = this.list.length < total
      } catch (e) {
        if (this.pageNum === 1) this.list = []
      }
      this.loading = false
    },

    search() { this.pageNum = 1; this.loadList() },
    switchTab(key) { this.activeStatus = key; this.pageNum = 1; this.loadList() },
    loadMore() { this.pageNum++; this.loadList() },

    getAvatarColor(name) {
      if (!name) return AVATAR_COLORS[0]
      return AVATAR_COLORS[name.charCodeAt(0) % AVATAR_COLORS.length]
    },

    getStatusText(s) { return STATUS_MAP[s] || s || '--' },
    getStatusType(s) {
      if (s === 'pending_pay') return 'warning'
      if (s === 'paid') return 'success'
      if (s === 'voucher_review') return 'info'
      if (s === 'refunded') return 'gray'
      if (s === 'cancelled') return 'gray'
      return ''
    },

    formatK(val) {
      if (!val) return '0'
      const n = Number(val)
      if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
      return n.toFixed(2)
    },

    formatTime(v) { return v ? String(v).replace('T', ' ').slice(0, 16) : '--' },

    goDetail(id) { uni.navigateTo({ url: '/pages-sub/channel/order-detail?id=' + id }) },
    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
.ord-page { min-height: 100vh; padding-bottom: 48rpx; background: var(--jst-color-page-bg); }

.ord-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: #fff; border-bottom: 2rpx solid var(--jst-color-border); position: sticky; top: 0; z-index: 40; }
.ord-header__back { width: 72rpx; height: 72rpx; border-radius: var(--jst-radius-sm); background: var(--jst-color-page-bg); display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.ord-header__title { flex: 1; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }

/* 统计横条 */
.ord-stats { display: grid; grid-template-columns: repeat(4, 1fr); margin: 24rpx 32rpx 0; background: #fff; border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); overflow: hidden; }
.ord-stats__cell { padding: 28rpx 12rpx; text-align: center; border-right: 2rpx solid var(--jst-color-border); }
.ord-stats__cell:last-child { border-right: none; }
.ord-stats__num { display: block; font-size: 40rpx; font-weight: 900; color: var(--jst-color-text); line-height: 1; margin-bottom: 8rpx; }
.ord-stats__label { display: block; font-size: 20rpx; color: var(--jst-color-text-tertiary); }

/* Tab */
.ord-tabs { display: flex; overflow-x: auto; background: #fff; border-bottom: 2rpx solid var(--jst-color-border); position: sticky; top: 200rpx; z-index: 30; }
.ord-tabs::-webkit-scrollbar { display: none; }
.ord-tab { flex-shrink: 0; padding: 0 36rpx; height: 88rpx; display: flex; align-items: center; gap: 10rpx; font-size: 28rpx; font-weight: 500; color: var(--jst-color-text-tertiary); white-space: nowrap; position: relative; }
.ord-tab--active { color: #3F51B5; font-weight: 700; }
.ord-tab--active::after { content: ''; position: absolute; bottom: 0; left: 28rpx; right: 28rpx; height: 4rpx; background: #3F51B5; border-radius: 2rpx; }
.ord-tab__badge { font-size: 20rpx; font-weight: 700; background: #FF5722; color: #fff; padding: 2rpx 10rpx; border-radius: var(--jst-radius-full); line-height: 1.4; }

/* 搜索 */
.ord-filter { padding: 20rpx 32rpx; background: #fff; }
.ord-filter__search { display: flex; align-items: center; padding: 0 24rpx; height: 72rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-page-bg); }
.ord-filter__icon { font-size: 28rpx; margin-right: 12rpx; }
.ord-filter__input { flex: 1; font-size: 26rpx; color: var(--jst-color-text); }

/* 订单列表 */
.ord-list { padding: 24rpx 32rpx 0; }
.ord-card { background: #fff; border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); margin-bottom: 20rpx; overflow: hidden; }
.ord-card__header { display: flex; align-items: center; gap: 16rpx; padding: 24rpx 28rpx 20rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.ord-card__contest { flex: 1; font-size: 26rpx; font-weight: 700; color: var(--jst-color-text); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.ord-card__status { flex-shrink: 0; padding: 6rpx 16rpx; border-radius: var(--jst-radius-full); font-size: 22rpx; font-weight: 600; }
.ord-card__status--warning { background: var(--jst-color-warning-soft); color: #B26A00; }
.ord-card__status--success { background: var(--jst-color-success-soft); color: #0F7B3F; }
.ord-card__status--info { background: var(--jst-color-brand-soft); color: var(--jst-color-brand); }
.ord-card__status--gray { background: #ECECEC; color: #6B6B6B; }
.ord-card__student { display: flex; align-items: center; gap: 20rpx; padding: 22rpx 28rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.ord-card__student:last-of-type { border-bottom: none; }
.ord-card__avatar { width: 64rpx; height: 64rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 700; color: #fff; flex-shrink: 0; }
.ord-card__sname { font-size: 26rpx; font-weight: 600; color: var(--jst-color-text); min-width: 96rpx; }
.ord-card__school { flex: 1; font-size: 22rpx; color: var(--jst-color-text-tertiary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-left: 8rpx; }
.ord-card__amount { font-size: 28rpx; font-weight: 700; flex-shrink: 0; }
.ord-card__footer { display: flex; align-items: center; justify-content: space-between; padding: 20rpx 28rpx; background: var(--jst-color-page-bg); }
.ord-card__total { font-size: 26rpx; color: var(--jst-color-text-secondary); }
.ord-card__total-val { font-weight: 900; color: #FF5722; font-size: 32rpx; }
.ord-card__actions { display: flex; gap: 16rpx; }
.ord-card__btn { height: 64rpx; padding: 0 24rpx; border-radius: var(--jst-radius-sm); font-size: 24rpx; font-weight: 600; display: flex; align-items: center; gap: 6rpx; }
.ord-card__btn--outline { background: transparent; color: #3F51B5; border: 3rpx solid rgba(63,81,181,0.3); }

.ord-loadmore { padding: 32rpx; text-align: center; font-size: 26rpx; color: #3F51B5; font-weight: 600; }
.ord-empty { text-align: center; padding: 96rpx 48rpx; }
.ord-empty__icon { display: block; font-size: 80rpx; margin-bottom: 24rpx; }
.ord-empty__text { font-size: 28rpx; color: var(--jst-color-text-tertiary); }
</style>
