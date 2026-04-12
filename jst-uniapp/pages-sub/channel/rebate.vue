<!-- 中文注释: 渠道方 · 返点中心
     对应原型: 小程序原型图/channel-rebate.png / channel-rebate.html
     调用接口: GET /jst/wx/channel/rebate/summary + /jst/wx/channel/rebate/ledger/list -->
<template>
  <view class="rebate-page">
    <!-- 顶部 hero: 4 KPI -->
    <view class="rebate-hero">
      <view class="rebate-hero__nav" :style="{ paddingTop: navPaddingTop }">
        <view class="rebate-hero__back" @tap="goBack">←</view>
        <text class="rebate-hero__title">返点中心</text>
        <view class="rebate-hero__action" @tap="navigateWithdrawList">提现单</view>
      </view>
      <view class="rebate-hero__kpi">
        <view class="rebate-hero__kpi-cell">
          <text class="rebate-hero__kpi-num rebate-hero__kpi-num--gold">¥{{ formatAmount(summary.withdrawableAmount) }}</text>
          <text class="rebate-hero__kpi-label">可提现余额</text>
        </view>
        <view class="rebate-hero__kpi-cell">
          <text class="rebate-hero__kpi-num">¥{{ formatAmount(summary.reviewingAmount) }}</text>
          <text class="rebate-hero__kpi-label">审核中金额</text>
        </view>
        <view class="rebate-hero__kpi-cell">
          <text class="rebate-hero__kpi-num">¥{{ formatAmount(summary.paidAmount) }}</text>
          <text class="rebate-hero__kpi-label">累计已打款</text>
        </view>
        <view class="rebate-hero__kpi-cell">
          <text class="rebate-hero__kpi-num rebate-hero__kpi-num--danger">¥{{ formatAmount(summary.rolledBackAmount) }}</text>
          <text class="rebate-hero__kpi-label">历史退款回退</text>
        </view>
      </view>
    </view>

    <!-- 状态筛选 tabs -->
    <scroll-view class="rebate-tabs" scroll-x>
      <view
        v-for="tab in statusTabs"
        :key="tab.value"
        :class="['rebate-tabs__item', activeStatus === tab.value && 'rebate-tabs__item--active']"
        @tap="onChangeStatus(tab.value)"
      >{{ tab.label }}</view>
    </scroll-view>

    <!-- 明细列表 -->
    <view class="rebate-list">
      <view v-for="item in list" :key="item.ledgerId" class="rebate-card">
        <view class="rebate-card__top">
          <view class="rebate-card__info">
            <text class="rebate-card__contest">{{ item.contestName || '——' }}</text>
            <text class="rebate-card__order">订单 {{ item.orderNo || '--' }}</text>
            <view class="rebate-card__tags">
              <u-tag :text="statusLabel(item.status)" :type="item.status === 'paid' ? 'success' : ((item.status === 'rejected' || item.status === 'rolled_back') ? 'info' : 'warning')" size="mini" shape="circle"></u-tag>
              <u-tag v-if="item.direction === 'negative'" text="回退" type="error" size="mini" shape="circle"></u-tag>
            </view>
          </view>
          <text :class="['rebate-card__amount', item.direction === 'negative' && 'rebate-card__amount--neg']">
            {{ item.direction === 'negative' ? '-' : '' }}¥{{ formatAmount(item.rebateAmount) }}
          </text>
        </view>
        <view class="rebate-card__meta">
          <text class="rebate-card__meta-item">计提 {{ formatTime(item.accrualTime || item.createTime) }}</text>
        </view>
      </view>

      <u-empty v-if="!list.length && !loading" mode="data" text="暂无返点记录"></u-empty>
      <u-loadmore v-if="list.length || loading" :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')"></u-loadmore>
    </view>

    <!-- 悬浮申请按钮 -->
    <view v-if="canWithdraw" class="withdraw-bar">
      <view class="withdraw-bar__info">
        <text class="withdraw-bar__label">可提现金额</text>
        <text class="withdraw-bar__amount">¥{{ formatAmount(summary.withdrawableAmount) }}</text>
      </view>
      <u-button class="withdraw-bar__btn" type="primary" shape="circle" @click="goWithdrawApply">申请提现</u-button>
    </view>
  </view>
</template>

<script>
import { getRebateSummary, getRebateLedgerList } from '@/api/channel'

// E1-CH-5: PRD V4.0 要求全部 + 6 状态共 7 个 tab
const STATUS_TABS = [
  { value: '', label: '全部' },
  { value: 'pending', label: '待结算' },
  { value: 'withdrawable', label: '可提现' },
  { value: 'in_review', label: '审核中' },
  { value: 'paid', label: '已打款' },
  { value: 'rejected', label: '已驳回' },
  { value: 'rolled_back', label: '已回退' }
]

const STATUS_LABEL = {
  pending: '待结算',
  withdrawable: '可提现',
  in_review: '审核中',
  paid: '已打款',
  rejected: '已驳回',
  rolled_back: '已回退'
}

export default {
  data() {
    return {
      skeletonShow: true, // [visual-effect]
      summary: { withdrawableAmount: '0.00', reviewingAmount: '0.00', paidAmount: '0.00', rolledBackAmount: '0.00' },
      statusTabs: STATUS_TABS,
      activeStatus: '',
      list: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      hasMore: true
    }
  },
  computed: {
    canWithdraw() {
      const v = Number(this.summary.withdrawableAmount || 0)
      return v > 0
    }
  },
  onLoad() {
    this.loadAll()
  },
  onPullDownRefresh() {
    this.loadAll().finally(() => uni.stopPullDownRefresh())
  },
  onReachBottom() {
    if (this.hasMore && !this.loading) this.loadList(false)
  },
  methods: {
    goBack() {
      if (getCurrentPages().length > 1) uni.navigateBack()
      else uni.switchTab({ url: '/pages/my/index' })
    },
    async loadAll() {
      await Promise.all([this.loadSummary(), this.loadList(true)])
    },
    async loadSummary() {
      try {
        const data = await getRebateSummary()
        this.summary = {
          withdrawableAmount: (data && data.withdrawableAmount) || '0.00',
          reviewingAmount: (data && data.reviewingAmount) || '0.00',
          paidAmount: (data && data.paidAmount) || '0.00',
          rolledBackAmount: (data && data.rolledBackAmount) || '0.00'
        }
      } catch (e) {}
    },
    async loadList(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await getRebateLedgerList({
          status: this.activeStatus || undefined,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        })
        const rows = (res && res.rows) || []
        this.total = (res && res.total) || 0
        this.list = reset ? rows : this.list.concat(rows)
        this.hasMore = this.list.length < this.total
        if (this.hasMore) this.pageNum += 1
      } finally {
        this.loading = false
      }
    },
    onChangeStatus(value) {
      if (this.activeStatus === value) return
      this.activeStatus = value
      this.loadList(true)
    },
    goWithdrawApply() {
      // E1-CH-5: 跳 settlement 统一结算页
      uni.navigateTo({ url: '/pages-sub/channel/settlement' })
    },
    navigateWithdrawList() {
      // E1-CH-5: 跳 settlement 统一结算页
      uni.navigateTo({ url: '/pages-sub/channel/settlement' })
    },
    formatAmount(v) {
      // money 直接展示后端返回的字符串, 仅在缺省时回落到 '0.00'
      if (v == null || v === '') return '0.00'
      return String(v)
    },
    formatTime(v) {
      if (!v) return '--'
      return String(v).replace('T', ' ').slice(0, 16)
    },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.rebate-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }
.rebate-hero { padding: 88rpx 32rpx 80rpx; background: linear-gradient(150deg, $jst-indigo 0%, $jst-indigo-light 55%, $jst-indigo 120%); position: relative; border-bottom-left-radius: 32rpx; border-bottom-right-radius: 32rpx; }
.rebate-hero__nav { display: flex; align-items: center; margin-bottom: 32rpx; }
.rebate-hero__back { width: 64rpx; height: 64rpx; border-radius: $jst-radius-sm; background: rgba(255, 255, 255, 0.18); color: $jst-text-inverse; text-align: center; line-height: 64rpx; font-size: 36rpx; }
.rebate-hero__title { flex: 1; margin-left: 24rpx; font-size: 34rpx; font-weight: 600; color: $jst-text-inverse; }
.rebate-hero__action { padding: 10rpx 24rpx; border-radius: $jst-radius-round; background: rgba($jst-gold, 0.22); border: 2rpx solid rgba($jst-gold, 0.5); color: $jst-gold; font-size: 24rpx; font-weight: 600; }
.rebate-hero__kpi { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16rpx; }
.rebate-hero__kpi-cell { padding: 24rpx; border-radius: $jst-radius-md; background: rgba(255, 255, 255, 0.18); border: 2rpx solid rgba(255, 255, 255, 0.16); }
.rebate-hero__kpi-num { display: block; font-size: 40rpx; font-weight: 600; color: $jst-text-inverse; }
.rebate-hero__kpi-num--gold { color: $jst-gold; }
.rebate-hero__kpi-num--danger { color: $jst-danger; }
.rebate-hero__kpi-label { display: block; margin-top: 8rpx; font-size: 22rpx; color: rgba(255, 255, 255, 0.76); }

.rebate-tabs { white-space: nowrap; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.rebate-tabs__item { display: inline-block; padding: 0 32rpx; height: 88rpx; line-height: 88rpx; font-size: 26rpx; color: $jst-text-secondary; position: relative; }
.rebate-tabs__item--active { color: $jst-brand; font-weight: 600; }
.rebate-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 20%; right: 20%; height: 4rpx; background: $jst-brand; border-radius: 2rpx; }

.rebate-list { padding: 8rpx 0 32rpx; }
.rebate-card { margin: 20rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.rebate-card__top { display: flex; align-items: flex-start; gap: 20rpx; }
.rebate-card__info { flex: 1; min-width: 0; }
.rebate-card__contest { display: block; font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.rebate-card__order { display: block; margin-top: 6rpx; font-size: 22rpx; color: $jst-text-secondary; }
.rebate-card__tags { display: flex; flex-wrap: wrap; gap: 10rpx; margin-top: 12rpx; }
.rebate-card__amount { flex-shrink: 0; font-size: 36rpx; font-weight: 600; color: $jst-warning; }
.rebate-card__amount--neg { color: $jst-danger; }
.rebate-card__meta { margin-top: 16rpx; padding-top: 16rpx; border-top: 2rpx dashed $jst-border; }
.rebate-card__meta-item { font-size: 22rpx; color: $jst-text-secondary; }

.withdraw-bar { position: fixed; left: 0; right: 0; bottom: 0; display: flex; align-items: center; gap: 20rpx; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: $jst-bg-card; box-shadow: $jst-shadow-sm; }
.withdraw-bar__info { flex: 1; }
.withdraw-bar__label { display: block; font-size: 22rpx; color: $jst-text-secondary; }
.withdraw-bar__amount { display: block; font-size: 40rpx; font-weight: 600; color: $jst-warning; }
::v-deep .withdraw-bar__btn.u-button { flex: 1; height: 88rpx; font-size: $jst-font-md; font-weight: $jst-weight-semibold; background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light); border: none; }
</style>
