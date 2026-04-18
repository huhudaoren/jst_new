<!-- 中文注释: 渠道方 · 合同列表
     调用接口: GET /jst/wx/contract/list?status=&pageNum=&pageSize= -->
<template>
  <view class="cl-page">
    <!-- 顶部导航 + 筛选 Tab -->
    <view class="cl-header" :style="{ paddingTop: navPaddingTop }">
      <view class="cl-header__back" @tap="goBack">←</view>
      <text class="cl-header__title">合同中心</text>
    </view>

    <scroll-view class="cl-tabs" scroll-x>
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['cl-tabs__item', activeStatus === tab.value && 'cl-tabs__item--active']"
        @tap="onChange(tab.value)"
      >{{ tab.label }}</view>
    </scroll-view>

    <!-- 列表 -->
    <view class="cl-list">
      <view v-for="item in list" :key="item.contractId" class="cl-card" @tap="goDetail(item)">
        <view class="cl-card__head">
          <text class="cl-card__no">{{ item.contractNo || '--' }}</text>
          <u-tag
            :text="statusLabel(item.status)"
            :type="statusType(item.status)"
            size="mini"
            shape="circle"
          />
        </view>
        <view class="cl-card__body">
          <view class="cl-card__row">
            <u-tag
              :text="typeLabel(item.contractType)"
              type="primary"
              plain
              :plain-fill="true"
              size="mini"
              shape="circle"
            />
            <!-- 关联结算单 chip（可点击跳转） -->
            <view
              v-if="item.settlementId || item.settlementNo"
              class="cl-card__chip"
              @tap.stop="goSettlement(item)"
            >
              <text class="cl-card__chip-icon">$</text>
              <text class="cl-card__chip-text">{{ item.settlementNo || '关联结算单' }}</text>
              <text class="cl-card__chip-arrow">›</text>
            </view>
          </view>
          <view class="cl-card__meta">
            <text v-if="item.signTime" class="cl-card__time">签署 {{ formatDateTime(item.signTime) }}</text>
            <text v-if="item.effectiveDate" class="cl-card__time">生效 {{ formatTime(item.effectiveDate) }}</text>
            <text v-if="item.expireDate" class="cl-card__time">到期 {{ formatTime(item.expireDate) }}</text>
          </view>
        </view>
      </view>

      <u-empty v-if="!list.length && !loading" mode="data" text="暂无合同记录" />
      <u-loadmore v-if="list.length || loading" :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')" />
    </view>
  </view>
</template>

<script>
import { getContractList } from '@/api/finance'

const TABS = [
  { value: '', label: '全部' },
  { value: 'pending_sign', label: '待签署' },
  { value: 'signed', label: '已签署' },
  { value: 'expired', label: '已到期' }
]

const STATUS_MAP = {
  draft: { label: '草稿', type: 'info' },
  pending_sign: { label: '待签署', type: 'warning' },
  signed: { label: '已签署', type: 'success' },
  expired: { label: '已到期', type: 'info' },
  archived: { label: '已归档', type: 'info' }
}

const TYPE_MAP = {
  partner_coop: '合作协议',
  channel_settle: '结算合同',
  supplement: '补充协议'
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
        const res = await getContractList({
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
    goDetail(item) {
      uni.navigateTo({ url: '/pages-sub/channel/contract-detail?id=' + item.contractId })
    },
    goBack() { uni.navigateBack() },
    statusLabel(s) { return (STATUS_MAP[s] && STATUS_MAP[s].label) || s || '--' },
    statusType(s) { return (STATUS_MAP[s] && STATUS_MAP[s].type) || 'info' },
    typeLabel(t) { return TYPE_MAP[t] || t || '--' },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 10) },
    // 金融统一时间格式 YYYY-MM-DD HH:mm
    formatDateTime(v) { if (!v) return '--'; const s = String(v).replace('T', ' '); return s.length >= 16 ? s.slice(0, 16) : s },
    goSettlement(item) {
      if (item.settlementId) uni.navigateTo({ url: '/pages-sub/channel/withdraw-detail?id=' + item.settlementId })
      else uni.showToast({ title: '暂未关联结算单', icon: 'none' })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.cl-page { min-height: 100vh; background: $jst-bg-page; }

.cl-header { display: flex; align-items: center; padding: $jst-space-lg $jst-page-padding $jst-space-md; background: $jst-bg-card; }
.cl-header__back { width: 60rpx; font-size: $jst-font-xl; color: $jst-text-primary; }
.cl-header__title { font-size: $jst-font-lg; font-weight: $jst-weight-semibold; color: $jst-text-primary; }

.cl-tabs { white-space: nowrap; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.cl-tabs__item { display: inline-block; padding: 0 $jst-space-xl; height: 88rpx; line-height: 88rpx; font-size: 26rpx; color: $jst-text-secondary; position: relative; }
.cl-tabs__item--active { color: $jst-brand; font-weight: $jst-weight-semibold; }
.cl-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: $jst-space-lg; right: $jst-space-lg; height: 4rpx; background: $jst-brand; border-radius: 2rpx; }

.cl-list { padding: $jst-space-xs 0 $jst-space-xl; }

.cl-card { margin: 20rpx $jst-space-xl 0; padding: 28rpx $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.cl-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: $jst-space-md; }
.cl-card__no { font-size: $jst-font-sm; color: $jst-text-secondary; }
.cl-card__body { display: flex; flex-direction: column; gap: $jst-space-sm; }
.cl-card__row { display: flex; align-items: center; gap: $jst-space-sm; flex-wrap: wrap; }
.cl-card__meta { display: flex; flex-wrap: wrap; gap: $jst-space-lg; }
.cl-card__time { font-size: $jst-font-xs; color: $jst-text-secondary; }

/* 关联结算单 chip */
.cl-card__chip { display: flex; align-items: center; gap: 4rpx; padding: 4rpx 14rpx; background: $jst-gold-light; border-radius: $jst-radius-sm; }
.cl-card__chip-icon { font-size: 22rpx; color: $jst-indigo; font-weight: 700; }
.cl-card__chip-text { font-size: 22rpx; color: $jst-indigo; }
.cl-card__chip-arrow { font-size: 22rpx; color: $jst-indigo; }
</style>
