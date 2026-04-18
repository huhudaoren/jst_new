<!-- 中文注释: 渠道方 · 渠道订单 (E1-CH-4 完整重构)
     对应原型: 小程序原型图/channel-orders.png + channel-orders.html
     功能: 6状态Tab + 搜索/筛选 + 订单卡片 + 触底加载
     数据来源: GET /jst/wx/channel/orders -->
<template>
  <view class="ord-page">
    <!-- 导航 -->
    <view class="ord-header" :style="{ paddingTop: navPaddingTop }">
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
        <text class="ord-stats__num ord-stats__num--warning">{{ stats.pendingPay || 0 }}</text>
        <text class="ord-stats__label">待支付</text>
      </view>
      <view class="ord-stats__cell" @tap="switchTab('paid')">
        <text class="ord-stats__num ord-stats__num--success">{{ stats.paid || 0 }}</text>
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
      <u-search
        v-model="keyword"
        placeholder="搜索学生姓名/订单号"
        :showAction="false"
        bgColor="transparent"
        @search="search"
      ></u-search>
    </view>

    <!-- 订单列表 -->
    <view class="ord-list">
      <view v-for="item in list" :key="item.orderId" class="ord-card" :class="{ 'ord-card--checked': isOrderChecked(item) }" @tap="goDetail(item.orderId)">
        <view class="ord-card__header">
          <!-- 中文注释: 批量选择 checkbox — 仅待支付订单显示 -->
          <view v-if="canBatchPay(item)" class="ord-card__checkbox" @tap.stop="toggleOrderCheck(item)">
            <view class="ord-card__checkbox-inner" :class="{ 'ord-card__checkbox-inner--on': isOrderChecked(item) }">
              <text v-if="isOrderChecked(item)" class="ord-card__checkbox-tick">✓</text>
            </view>
          </view>
          <text class="ord-card__contest">{{ item.contestName || '--' }}</text>
          <u-tag
            :text="item.statusText || getStatusText(item.orderStatus)"
            :type="getStatusType(item.orderStatus) === 'gray' ? 'info' : getStatusType(item.orderStatus)"
            size="mini"
            shape="circle"
          ></u-tag>
        </view>

        <!-- 学生行 -->
        <view class="ord-card__student">
          <view class="ord-card__avatar" :style="{ background: getAvatarColor(item.studentName) }">
            <text>{{ (item.studentName || '').slice(0, 1) }}</text>
          </view>
          <text class="ord-card__sname">{{ item.studentName || '--' }}</text>
          <text class="ord-card__school">{{ item.schoolName || '' }}</text>
          <text :class="['ord-card__amount', item.payAmount > 0 ? 'ord-card__amount--pay' : 'ord-card__amount--free']">
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

      <u-loadmore v-if="list.length" :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')" @loadmore="loadMore" />
      <u-empty v-if="!list.length && !loading" mode="order" text="暂无订单"></u-empty>
    </view>

    <!-- 中文注释: 批量支付 footer — translateY 滑入 -->
    <view
      class="ord-batch-footer"
      :class="{ 'ord-batch-footer--visible': checkedOrderCount > 0 }"
    >
      <view class="ord-batch-footer__left">
        <text class="ord-batch-footer__count">已选 <text class="ord-batch-footer__count-num">{{ checkedOrderCount }}</text> 单</text>
        <text class="ord-batch-footer__sep">·</text>
        <text class="ord-batch-footer__link" @tap="selectAllOrders">全选</text>
        <text class="ord-batch-footer__sep">/</text>
        <text class="ord-batch-footer__link" @tap="clearCheckedOrders">清空</text>
      </view>
      <view class="ord-batch-footer__right" @tap="goBatchPay">
        <text class="ord-batch-footer__btn-text">💳 批量支付</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getChannelOrders, getChannelDashboardStats, batchPayChannelOrders } from '@/api/channel'

const AVATAR_COLORS = [
  'linear-gradient(135deg, #283593, #3949AB)',
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
      skeletonShow: true, // [visual-effect]
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
      hasMore: false,
      // 中文注释: 批量支付选择 — 存 orderId
      checkedOrderIds: []
    }
  },
  computed: {
    checkedOrderCount() { return this.checkedOrderIds.length }
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

    // 中文注释: 批量支付 helpers — 仅 pending_pay 订单可勾选
    canBatchPay(item) { return item && item.orderStatus === 'pending_pay' },
    isOrderChecked(item) {
      return item && this.checkedOrderIds.includes(item.orderId)
    },
    toggleOrderCheck(item) {
      if (!this.canBatchPay(item)) return
      const id = item.orderId
      const idx = this.checkedOrderIds.indexOf(id)
      if (idx >= 0) this.checkedOrderIds.splice(idx, 1)
      else this.checkedOrderIds.push(id)
    },
    selectAllOrders() {
      const payable = this.list.filter(o => this.canBatchPay(o)).map(o => o.orderId)
      const set = new Set(this.checkedOrderIds.concat(payable))
      this.checkedOrderIds = Array.from(set)
    },
    clearCheckedOrders() { this.checkedOrderIds = [] },
    // 中文注释: 批量支付 — Round 2B · B1
    // 后端: POST /jst/wx/channel/orders/batch-pay, 返回 { batchPayNo, totalAmount, count, items }
    // items 每条含 merchantPayParams 可唤起 wx.requestPayment（接入商户号后启用）
    // 当前简化: 接口返回即 toast + 跳订单列表以 batchPayNo 过滤
    goBatchPay() {
      if (!this.checkedOrderCount) return
      uni.showModal({
        title: '批量支付',
        content: `即将对 ${this.checkedOrderCount} 笔订单发起支付，是否继续？`,
        confirmText: '确认',
        success: async (res) => {
          if (!res.confirm) return
          const orderIds = this.checkedOrderIds.slice()
          uni.showLoading({ title: '创建批量支付单...' })
          try {
            const resp = await batchPayChannelOrders(orderIds)
            uni.hideLoading()
            const batchPayNo = (resp && resp.batchPayNo) || ''
            const total = (resp && resp.totalAmount) || 0
            const count = (resp && resp.count) || orderIds.length
            // TODO(wxpay): 接入商户号后改为循环 resp.items 每项调 wx.requestPayment(merchantPayParams)
            uni.showToast({
              title: `批量支付单创建成功，共 ${count} 单 ¥${total}`,
              icon: 'none',
              duration: 2500
            })
            // 清空选择，刷新列表
            this.checkedOrderIds = []
            this.pageNum = 1
            this.loadList()
            this.loadStats()
            // 2.5s 后跳到我的订单列表（带 batchPayNo 过滤参数）
            setTimeout(() => {
              const qs = batchPayNo ? `?batchPayNo=${encodeURIComponent(batchPayNo)}` : ''
              uni.navigateTo({ url: `/pages-sub/my/order-list${qs}` })
            }, 1800)
          } catch (err) {
            uni.hideLoading()
            // 友好降级: 后端未合入 / 接口报错时给提示, 不破坏勾选状态
            const msg = (err && (err.msg || err.message)) || '批量支付接口暂未就绪，请稍后再试'
            uni.showToast({ title: msg, icon: 'none', duration: 2500 })
          }
        }
      })
    },

    goDetail(id) { uni.navigateTo({ url: '/pages-sub/channel/order-detail?id=' + id }) },
    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.ord-page { min-height: 100vh; padding-bottom: calc(160rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }

.ord-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; position: sticky; top: 0; z-index: 40; }
.ord-header__back { width: 72rpx; height: 72rpx; border-radius: $jst-radius-sm; background: $jst-bg-page; display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.ord-header__title { flex: 1; font-size: 34rpx; font-weight: 600; color: $jst-text-primary; }

/* 统计横条 */
.ord-stats { display: grid; grid-template-columns: repeat(4, 1fr); margin: 24rpx 32rpx 0; background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; overflow: hidden; }
.ord-stats__cell { padding: 28rpx 12rpx; text-align: center; border-right: 2rpx solid $jst-border; }
.ord-stats__cell:last-child { border-right: none; }
.ord-stats__num { display: block; font-size: 40rpx; font-weight: 600; color: $jst-text-primary; line-height: 1; margin-bottom: 8rpx; }
.ord-stats__num--warning { color: $jst-warning; }
.ord-stats__num--success { color: $jst-success; }
.ord-stats__label { display: block; font-size: 20rpx; color: $jst-text-secondary; }

/* Tab */
.ord-tabs { display: flex; overflow-x: auto; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; position: sticky; top: 200rpx; z-index: 30; }
.ord-tabs::-webkit-scrollbar { display: none; }
.ord-tab { flex-shrink: 0; padding: 0 36rpx; height: 88rpx; display: flex; align-items: center; gap: 10rpx; font-size: 28rpx; font-weight: 500; color: $jst-text-secondary; white-space: nowrap; position: relative; }
.ord-tab--active { color: $jst-brand; font-weight: 600; }
.ord-tab--active::after { content: ''; position: absolute; bottom: 0; left: 28rpx; right: 28rpx; height: 4rpx; background: $jst-brand; border-radius: 2rpx; }
.ord-tab__badge { font-size: 20rpx; font-weight: 600; background: $jst-warning; color: $jst-text-inverse; padding: 2rpx 10rpx; border-radius: $jst-radius-round; line-height: 1.4; }

/* 搜索 */
.ord-filter { padding: 20rpx 32rpx; background: $jst-bg-card; }
::v-deep .ord-filter .u-search__content { background-color: $jst-bg-page !important; }

/* 订单列表 */
.ord-list { padding: 24rpx 32rpx 0; }
.ord-card { background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; margin-bottom: 20rpx; overflow: hidden; }
.ord-card__header { display: flex; align-items: center; gap: 16rpx; padding: 24rpx 28rpx 20rpx; border-bottom: 2rpx solid $jst-border; }
.ord-card__contest { flex: 1; font-size: 26rpx; font-weight: 600; color: $jst-text-primary; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.ord-card__student { display: flex; align-items: center; gap: 20rpx; padding: 22rpx 28rpx; border-bottom: 2rpx solid $jst-border; }
.ord-card__student:last-of-type { border-bottom: none; }
.ord-card__avatar { width: 64rpx; height: 64rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 600; color: $jst-text-inverse; flex-shrink: 0; }
.ord-card__sname { font-size: 26rpx; font-weight: 600; color: $jst-text-primary; min-width: 96rpx; }
.ord-card__school { flex: 1; font-size: 22rpx; color: $jst-text-secondary; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-left: 8rpx; }
.ord-card__amount { font-size: 28rpx; font-weight: 600; flex-shrink: 0; }
.ord-card__amount--pay { color: $jst-warning; }
.ord-card__amount--free { color: $jst-success; }
.ord-card__footer { display: flex; align-items: center; justify-content: space-between; padding: 20rpx 28rpx; background: $jst-bg-page; }
.ord-card__total { font-size: 26rpx; color: $jst-text-regular; }
.ord-card__total-val { font-weight: 600; color: $jst-warning; font-size: 32rpx; }
.ord-card__actions { display: flex; gap: 16rpx; }
.ord-card__btn { height: 64rpx; padding: 0 24rpx; border-radius: $jst-radius-sm; font-size: 24rpx; font-weight: 600; display: flex; align-items: center; gap: 6rpx; }
.ord-card__btn--outline { background: transparent; color: $jst-brand; border: 3rpx solid rgba(63,81,181,0.3); }

/* 批量支付 checkbox */
.ord-card { transition: background 0.2s ease; }
.ord-card--checked { background: $jst-brand-light; }
.ord-card__checkbox {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-right: $jst-space-xs;
}
.ord-card__checkbox-inner {
  width: 36rpx;
  height: 36rpx;
  border-radius: $jst-radius-xs;
  border: 3rpx solid $jst-border;
  background: $jst-bg-card;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}
.ord-card__checkbox-inner--on {
  background: $jst-brand;
  border-color: $jst-brand;
}
.ord-card__checkbox-tick {
  color: $jst-text-inverse;
  font-size: 24rpx;
  font-weight: $jst-weight-bold;
  line-height: 1;
}

/* 批量支付 footer — translateY 滑入 */
.ord-batch-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 60;
  display: flex;
  align-items: center;
  gap: $jst-space-md;
  padding: $jst-space-md $jst-space-xl;
  padding-bottom: calc(#{$jst-space-md} + env(safe-area-inset-bottom));
  background: $jst-bg-card;
  border-top: 2rpx solid $jst-border;
  box-shadow: 0 -6rpx 24rpx rgba(0, 0, 0, 0.08);
  transform: translateY(120%);
  transition: transform 0.25s ease;
}
.ord-batch-footer--visible { transform: translateY(0); }
.ord-batch-footer__left {
  flex: 1;
  display: flex;
  align-items: center;
  gap: $jst-space-xs;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}
.ord-batch-footer__count-num {
  color: $jst-warning;
  font-weight: $jst-weight-bold;
}
.ord-batch-footer__sep {
  color: $jst-text-placeholder;
  margin: 0 4rpx;
}
.ord-batch-footer__link {
  color: $jst-brand;
  font-weight: $jst-weight-semibold;
  padding: 6rpx 10rpx;
}
.ord-batch-footer__right {
  display: flex;
  align-items: center;
  padding: 0 $jst-space-lg;
  height: 80rpx;
  background: linear-gradient(135deg, $jst-warning, darken($jst-warning, 10%));
  border-radius: $jst-radius-round;
  color: $jst-text-inverse;
  box-shadow: $jst-shadow-md;
}
.ord-batch-footer__btn-text {
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

</style>
