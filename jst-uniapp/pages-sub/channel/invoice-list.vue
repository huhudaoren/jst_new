<!-- 中文注释: 渠道方 · 发票列表
     调用接口: GET /jst/wx/invoice/list?status=&pageNum=&pageSize= -->
<template>
  <view class="il-page">
    <!-- 导航 -->
    <view class="il-header" :style="{ paddingTop: navPaddingTop }">
      <view class="il-header__back" @tap="goBack">←</view>
      <text class="il-header__title">开票中心</text>
      <view class="il-header__action" @tap="goApply">申请开票</view>
    </view>

    <!-- 筛选 Tab -->
    <scroll-view class="il-tabs" scroll-x>
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['il-tabs__item', activeStatus === tab.value && 'il-tabs__item--active']"
        @tap="onChange(tab.value)"
      >{{ tab.label }}</view>
    </scroll-view>

    <!-- 列表 -->
    <view class="il-list">
      <view v-for="item in list" :key="item.invoiceId" class="il-card">
        <view class="il-card__head">
          <text class="il-card__no">{{ item.invoiceNo || '--' }}</text>
          <u-tag
            :text="statusLabel(item.status)"
            :type="statusType(item.status)"
            size="mini"
            shape="circle"
          />
        </view>
        <view class="il-card__body">
          <view class="il-card__main">
            <text class="il-card__title">{{ item.invoiceTitle || '--' }}</text>
            <text class="il-card__amount">¥{{ formatAmount(item.amount) }}</text>
          </view>
          <text class="il-card__time">{{ formatTime(item.applyTime || item.createTime) }}</text>
        </view>
      </view>

      <u-empty v-if="!list.length && !loading" mode="data" text="暂无发票记录" />
      <u-loadmore v-if="list.length || loading" :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')" />
    </view>
  </view>
</template>

<script>
import { getInvoiceList } from '@/api/finance'

const TABS = [
  { value: '', label: '全部' },
  { value: 'pending_apply', label: '待审核' },
  { value: 'issuing', label: '开票中' },
  { value: 'issued', label: '已开票' }
]

const STATUS_MAP = {
  pending_apply: { label: '待审核', type: 'warning' },
  reviewing: { label: '审核中', type: 'warning' },
  issuing: { label: '开票中', type: 'primary' },
  issued: { label: '已开票', type: 'success' },
  voided: { label: '已作废', type: 'error' },
  red_offset: { label: '红冲', type: 'error' }
}

export default {
  data() {
    return {
      tabs: TABS,
      activeStatus: '',
      list: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      hasMore: true
    }
  },
  onLoad() { this.load(true) },
  onShow() { if (this.list.length) this.load(true) },
  onReachBottom() { if (this.hasMore && !this.loading) this.load(false) },
  methods: {
    async load(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await getInvoiceList({
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
    onChange(v) {
      if (this.activeStatus === v) return
      this.activeStatus = v
      this.load(true)
    },
    goApply() { uni.navigateTo({ url: '/pages-sub/channel/invoice-apply' }) },
    goBack() { uni.navigateBack() },
    statusLabel(s) { return (STATUS_MAP[s] && STATUS_MAP[s].label) || s || '--' },
    statusType(s) { return (STATUS_MAP[s] && STATUS_MAP[s].type) || 'info' },
    formatAmount(v) { if (v == null || v === '') return '0.00'; return Number(v).toFixed(2) },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.il-page { min-height: 100vh; background: $jst-bg-page; }

.il-header { display: flex; align-items: center; padding: $jst-space-lg $jst-page-padding $jst-space-md; background: $jst-bg-card; }
.il-header__back { width: 60rpx; font-size: $jst-font-xl; color: $jst-text-primary; }
.il-header__title { flex: 1; font-size: $jst-font-lg; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.il-header__action { font-size: $jst-font-sm; color: $jst-brand; font-weight: $jst-weight-medium; }

.il-tabs { white-space: nowrap; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.il-tabs__item { display: inline-block; padding: 0 $jst-space-xl; height: 88rpx; line-height: 88rpx; font-size: 26rpx; color: $jst-text-secondary; position: relative; }
.il-tabs__item--active { color: $jst-brand; font-weight: $jst-weight-semibold; }
.il-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: $jst-space-lg; right: $jst-space-lg; height: 4rpx; background: $jst-brand; border-radius: 2rpx; }

.il-list { padding: $jst-space-xs 0 $jst-space-xl; }

.il-card { margin: 20rpx $jst-space-xl 0; padding: 28rpx $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.il-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: $jst-space-md; }
.il-card__no { font-size: $jst-font-sm; color: $jst-text-secondary; }
.il-card__body { display: flex; flex-direction: column; gap: $jst-space-xs; }
.il-card__main { display: flex; justify-content: space-between; align-items: center; }
.il-card__title { font-size: $jst-font-base; color: $jst-text-primary; font-weight: $jst-weight-medium; flex: 1; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.il-card__amount { font-size: $jst-font-xl; font-weight: $jst-weight-bold; color: $jst-warning; margin-left: $jst-space-md; flex-shrink: 0; }
.il-card__time { font-size: $jst-font-xs; color: $jst-text-secondary; }
</style>
