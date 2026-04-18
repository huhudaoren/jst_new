<!-- 中文注释: 渠道方 · 我的提现单列表
     调用接口: GET /jst/wx/channel/withdraw/list?status=&pageNum=&pageSize= -->
<template>
  <view class="wl-page">
    <scroll-view class="wl-tabs" scroll-x :style="{ paddingTop: navPaddingTop }">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['wl-tabs__item', activeStatus === tab.value && 'wl-tabs__item--active']"
        @tap="onChange(tab.value)"
      >{{ tab.label }}</view>
    </scroll-view>

    <view class="wl-list">
      <view v-for="item in list" :key="item.settlementId" class="wl-card" @tap="goDetail(item)">
        <view v-if="item.status === 'paid'" class="wl-card__corner">已到账</view>
        <view class="wl-card__head">
          <text class="wl-card__no">{{ item.settlementNo }}</text>
          <u-tag
            :text="statusLabel(item.status)"
            :type="statusTagType(item.status)"
            size="mini"
            shape="circle"
          ></u-tag>
        </view>
        <view class="wl-card__body">
          <view class="wl-card__money">
            <text class="wl-card__amount">¥{{ formatAmount(item.applyAmount) }}</text>
            <text class="wl-card__sub">实付 ¥{{ formatAmount(item.actualPayAmount) }}</text>
          </view>
          <text class="wl-card__time">{{ formatDateTime(item.applyTime) }}</text>
        </view>
        <!-- 关联信息 chip -->
        <view class="wl-card__meta">
          <view v-if="item.ledgerCount != null" class="wl-card__meta-chip">
            <text>关联 {{ item.ledgerCount }} 笔返点</text>
          </view>
          <view :class="['wl-card__meta-chip', 'wl-card__meta-chip--' + invoiceStatusTheme(item.invoiceStatus)]">
            <text>{{ invoiceStatusLabel(item.invoiceStatus) }}</text>
          </view>
        </view>
      </view>
      <u-empty v-if="!list.length && !loading" mode="data" text="暂无提现记录"></u-empty>
      <u-loadmore v-if="list.length || loading" :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')"></u-loadmore>
    </view>
  </view>
</template>

<script>
import { getWithdrawList } from '@/api/channel'

// Tab 顺序按业务流程：待提交 / 审核中 / 待打款 / 已打款 / 已驳回 / 全部
const TABS = [
  { value: 'pending', label: '待提交' },
  { value: 'in_review', label: '审核中' },
  { value: 'approved', label: '待打款' },
  { value: 'paid', label: '已打款' },
  { value: 'rejected', label: '已驳回' },
  { value: '', label: '全部' }
]
const STATUS_LABEL = { pending: '待审核', in_review: '审核中', approved: '待打款', paid: '已打款', rejected: '已驳回', cancelled: '已取消' }
const INVOICE_STATUS_LABEL = { none: '未开票', pending_apply: '待开票', reviewing: '审核中', issuing: '开票中', issued: '已开票', voided: '已作废', red_offset: '红冲' }

export default {
  data() {
    return { skeletonShow: true, /* [visual-effect] */ tabs: TABS, activeStatus: 'pending', list: [], pageNum: 1, pageSize: 10, total: 0, loading: false, hasMore: true }
  },
  onLoad() { this.load(true) },
  onShow() {
    // 从详情页返回时刷新
    if (this.list.length) this.load(true)
  },
  onPullDownRefresh() { this.load(true).finally(() => uni.stopPullDownRefresh()) },
  onReachBottom() { if (this.hasMore && !this.loading) this.load(false) },
  methods: {
    async load(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await getWithdrawList({
          status: this.activeStatus || undefined,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        })
        const rows = (res && res.rows) || []
        this.total = (res && res.total) || 0
        this.list = reset ? rows : this.list.concat(rows)
        this.hasMore = this.list.length < this.total
        if (this.hasMore) this.pageNum += 1
      } finally { this.loading = false }
    },
    onChange(v) { if (this.activeStatus === v) return; this.activeStatus = v; this.load(true) },
    goDetail(item) { uni.navigateTo({ url: '/pages-sub/channel/withdraw-detail?id=' + item.settlementId }) },
    formatAmount(v) { if (v == null || v === '') return '0.00'; return String(v) },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) },
    formatDateTime(v) { if (!v) return '--'; const s = String(v).replace('T', ' '); return s.length >= 16 ? s.slice(0, 16) : s },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' },
    statusTagType(s) {
      if (s === 'paid') return 'success'
      if (s === 'rejected') return 'error'
      if (['pending', 'in_review', 'approved'].includes(s)) return 'warning'
      return 'info'
    },
    invoiceStatusLabel(s) { return INVOICE_STATUS_LABEL[s] || '未开票' },
    invoiceStatusTheme(s) {
      if (s === 'issued') return 'success'
      if (['reviewing', 'issuing', 'pending_apply'].includes(s)) return 'warning'
      if (['voided', 'red_offset'].includes(s)) return 'danger'
      return 'muted'
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.wl-page { min-height: 100vh; background: $jst-bg-page; }
.wl-tabs { white-space: nowrap; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.wl-tabs__item { display: inline-block; padding: 0 32rpx; height: 88rpx; line-height: 88rpx; font-size: 26rpx; color: $jst-text-secondary; position: relative; }
.wl-tabs__item--active { color: $jst-brand; font-weight: 600; }
.wl-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 24rpx; right: 24rpx; height: 4rpx; background: $jst-brand; border-radius: 2rpx; }

.wl-list { padding: 8rpx 0 32rpx; }
.wl-card { position: relative; margin: 20rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; overflow: hidden; }
.wl-card__corner { position: absolute; top: 0; right: 0; padding: 8rpx 20rpx; background: $jst-success; color: $jst-text-inverse; font-size: 20rpx; font-weight: 600; border-bottom-left-radius: $jst-radius-md; }
.wl-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.wl-card__no { font-size: 24rpx; color: $jst-text-secondary; }
.wl-card__body { display: flex; justify-content: space-between; align-items: flex-end; }
.wl-card__money { display: flex; flex-direction: column; }
.wl-card__amount { font-size: 40rpx; font-weight: 600; color: $jst-warning; }
.wl-card__sub { margin-top: 4rpx; font-size: 22rpx; color: $jst-text-secondary; }
.wl-card__time { font-size: 22rpx; color: $jst-text-secondary; }

/* 关联信息 chip 行 */
.wl-card__meta { display: flex; flex-wrap: wrap; gap: 12rpx; margin-top: 16rpx; padding-top: 16rpx; border-top: 2rpx dashed $jst-border; }
.wl-card__meta-chip { padding: 4rpx 14rpx; border-radius: $jst-radius-sm; font-size: 22rpx; background: $jst-bg-page; color: $jst-text-regular; }
.wl-card__meta-chip--success { background: $jst-success-light; color: $jst-success; }
.wl-card__meta-chip--warning { background: $jst-warning-light; color: $jst-warning; }
.wl-card__meta-chip--danger { background: $jst-danger-light; color: $jst-danger; }
</style>
