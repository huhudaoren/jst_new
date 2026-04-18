<!-- 中文注释: 渠道方 · 分销收益
     调用接口: GET /jst/wx/channel/distribution/summary
               GET /jst/wx/channel/distribution/ledger -->
<template>
  <view class="dist-page">
    <!-- 顶部 hero -->
    <view class="dist-hero" :style="{ paddingTop: navPaddingTop }">
      <view class="dist-hero__nav">
        <view class="dist-hero__back" @tap="goBack">←</view>
        <text class="dist-hero__title">分销收益</text>
        <view style="width: 64rpx;" />
      </view>
      <!-- 汇总 3 项 -->
      <view class="dist-hero__summary">
        <view class="dist-hero__kpi">
          <text class="dist-hero__kpi-num">¥{{ formatAmount(summary.totalAmount) }}</text>
          <text class="dist-hero__kpi-label">累计收益</text>
        </view>
        <view class="dist-hero__kpi">
          <text class="dist-hero__kpi-num dist-hero__kpi-num--warning">¥{{ formatAmount(summary.pendingAmount) }}</text>
          <text class="dist-hero__kpi-label">待入账</text>
        </view>
        <view class="dist-hero__kpi">
          <text class="dist-hero__kpi-num dist-hero__kpi-num--gold">¥{{ formatAmount(summary.accruedAmount) }}</text>
          <text class="dist-hero__kpi-label">可提现</text>
        </view>
      </view>
      <!-- 提现按钮 -->
      <view class="dist-hero__withdraw" @tap="goWithdraw">
        <text class="dist-hero__withdraw-text">前往提现</text>
        <text class="dist-hero__withdraw-arrow">→</text>
      </view>
    </view>

    <!-- 台账列表 -->
    <view class="dist-list">
      <view v-for="item in list" :key="item.ledgerId" class="dist-card">
        <view class="dist-card__top">
          <view class="dist-card__info">
            <text class="dist-card__channel">{{ item.inviteeChannelName || '下级渠道 ' + item.inviteeChannelId }}</text>
            <text class="dist-card__order" v-if="item.orderNo">订单 {{ item.orderNo }}</text>
            <u-tag :text="distStatusLabel(item.status)" :type="distStatusType(item.status)" size="mini" shape="circle" />
          </view>
          <text class="dist-card__amount">+¥{{ formatAmount(item.amount) }}</text>
        </view>
        <view class="dist-card__meta">
          <text class="dist-card__meta-item">{{ formatTime(item.accrueAt || item.createTime) }}</text>
        </view>
      </view>

      <u-empty v-if="!loading && !list.length" mode="data" text="暂无分销收益记录"></u-empty>
      <u-loadmore v-if="list.length || loading" :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')" @loadmore="loadMore" />
    </view>
  </view>
</template>

<script>
import { getDistributionSummary, listDistributionLedger } from '@/api/invite'

const STATUS_LABEL = { pending: '待入账', accrued: '已到账', cancelled: '已取消' }
const STATUS_TYPE = { pending: 'warning', accrued: 'success', cancelled: 'info' }

export default {
  data() {
    return {
      summary: { totalAmount: '0.00', pendingAmount: '0.00', accruedAmount: '0.00' },
      list: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      hasMore: true
    }
  },
  computed: {
    navPaddingTop() {
      const si = uni.getSystemInfoSync()
      return (si.statusBarHeight || 20) + 'px'
    }
  },
  onLoad() {
    this.loadAll()
  },
  onPullDownRefresh() {
    this.loadAll().finally(() => uni.stopPullDownRefresh())
  },
  onReachBottom() {
    if (this.hasMore && !this.loading) this.loadMore()
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
        const data = await getDistributionSummary()
        this.summary = {
          totalAmount: (data && data.totalAmount) || '0.00',
          pendingAmount: (data && data.pendingAmount) || '0.00',
          accruedAmount: (data && data.accruedAmount) || '0.00'
        }
      } catch (e) {}
    },
    async loadList(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await listDistributionLedger({ pageNum: this.pageNum, pageSize: this.pageSize })
        const rows = (res && res.rows) || []
        this.total = (res && res.total) || 0
        this.list = reset ? rows : this.list.concat(rows)
        this.hasMore = this.list.length < this.total
        if (this.hasMore) this.pageNum += 1
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      this.loadList(false)
    },
    goWithdraw() {
      uni.navigateTo({ url: '/pages-sub/channel/withdraw-apply' })
    },
    distStatusLabel(s) { return STATUS_LABEL[s] || s || '--' },
    distStatusType(s) { return STATUS_TYPE[s] || 'info' },
    formatAmount(v) {
      if (v == null || v === '') return '0.00'
      return Number(v).toFixed(2)
    },
    formatTime(v) {
      if (!v) return '--'
      return String(v).replace('T', ' ').slice(0, 16)
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.dist-page { min-height: 100vh; padding-bottom: 60rpx; background: $jst-bg-page; }

.dist-hero {
  padding: 0 $jst-space-xl 48rpx;
  background: linear-gradient(150deg, $jst-indigo 0%, $jst-indigo-light 55%, $jst-indigo 120%);
  border-bottom-left-radius: 32rpx;
  border-bottom-right-radius: 32rpx;
}
.dist-hero__nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 24rpx;
  margin-bottom: $jst-space-xl;
}
.dist-hero__back {
  width: 64rpx;
  height: 64rpx;
  border-radius: $jst-radius-sm;
  background: rgba(255, 255, 255, 0.18);
  color: $jst-text-inverse;
  text-align: center;
  line-height: 64rpx;
  font-size: 36rpx;
}
.dist-hero__title { font-size: 34rpx; font-weight: 600; color: $jst-text-inverse; }

.dist-hero__summary {
  display: flex;
  justify-content: space-around;
  margin-bottom: $jst-space-xl;
}
.dist-hero__kpi { text-align: center; }
.dist-hero__kpi-num { display: block; font-size: 44rpx; font-weight: 700; color: $jst-text-inverse; }
.dist-hero__kpi-num--warning { color: $jst-warning; }
.dist-hero__kpi-num--gold { color: $jst-gold; }
.dist-hero__kpi-label { display: block; font-size: $jst-font-sm; color: rgba(255, 255, 255, 0.75); margin-top: 6rpx; }

.dist-hero__withdraw {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 16rpx 48rpx;
  background: rgba($jst-gold, 0.18);
  border: 2rpx solid rgba($jst-gold, 0.5);
  border-radius: $jst-radius-round;
  width: max-content;
  margin: 0 auto;
}
.dist-hero__withdraw-text { font-size: $jst-font-sm; color: $jst-gold; font-weight: 600; }
.dist-hero__withdraw-arrow { font-size: $jst-font-base; color: $jst-gold; }

.dist-list { padding: $jst-space-md 0; }

.dist-card {
  margin: $jst-space-md $jst-space-xl 0;
  padding: $jst-space-lg $jst-space-xl;
  background: $jst-bg-card;
  border-radius: $jst-radius-md;
  box-shadow: $jst-shadow-sm;
}
.dist-card__top { display: flex; align-items: flex-start; gap: $jst-space-md; }
.dist-card__info { flex: 1; min-width: 0; }
.dist-card__channel { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; margin-bottom: 6rpx; }
.dist-card__order { display: block; font-size: $jst-font-xs; color: $jst-text-secondary; margin-bottom: 8rpx; }
.dist-card__amount { font-size: 36rpx; font-weight: 600; color: $jst-success; flex-shrink: 0; }
.dist-card__meta { margin-top: $jst-space-md; padding-top: $jst-space-md; border-top: 2rpx dashed $jst-border; }
.dist-card__meta-item { font-size: $jst-font-xs; color: $jst-text-secondary; }
</style>
