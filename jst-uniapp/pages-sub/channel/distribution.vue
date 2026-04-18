<!-- 中文注释: 渠道方 · 分销收益（SALES-DISTRIBUTION plan-02 闭环）
     调用接口:
       GET /jst/wx/channel/distribution/summary    汇总 {totalAmount, pendingAmount, accruedAmount}
       GET /jst/wx/channel/distribution/ledger     明细（分页，可按 status 过滤）
     分销规则: 实付为基数 · 单笔 15% 上限 · T+7 到账 · 退款撤销
     底部 CTA: 可提现金额 → 结算中心申请提现 -->
<template>
  <view class="dist-page">
    <!-- Hero -->
    <view class="dist-hero" :style="{ paddingTop: navPaddingTop }">
      <view class="dist-hero__nav">
        <view class="dist-hero__back" @tap="goBack">←</view>
        <text class="dist-hero__title">分销收益</text>
        <view class="dist-hero__action" @tap="goInviteCode">邀请下级</view>
      </view>
      <!-- KPI 4 格 -->
      <view class="dist-hero__kpi">
        <view class="dist-hero__kpi-cell dist-hero__kpi-cell--main">
          <text class="dist-hero__kpi-num dist-hero__kpi-num--gold">¥{{ formatAmount(summary.monthAmount) }}</text>
          <text class="dist-hero__kpi-label">本月分销收益</text>
        </view>
        <view class="dist-hero__kpi-cell">
          <text class="dist-hero__kpi-num">¥{{ formatAmount(summary.totalAmount) }}</text>
          <text class="dist-hero__kpi-label">累计分销收益</text>
        </view>
        <view class="dist-hero__kpi-cell">
          <text class="dist-hero__kpi-num dist-hero__kpi-num--warning">¥{{ formatAmount(summary.pendingAmount) }}</text>
          <text class="dist-hero__kpi-label">待结算</text>
        </view>
        <view class="dist-hero__kpi-cell">
          <text class="dist-hero__kpi-num dist-hero__kpi-num--success">¥{{ formatAmount(summary.accruedAmount) }}</text>
          <text class="dist-hero__kpi-label">已到账</text>
        </view>
      </view>
    </view>

    <!-- 月份切换器 + 状态筛选 -->
    <view class="dist-filter">
      <scroll-view class="dist-filter__scroll" scroll-x>
        <view
          v-for="m in monthOptions" :key="m.value"
          :class="['dist-filter__chip', activeMonth === m.value && 'dist-filter__chip--active']"
          @tap="onChangeMonth(m.value)"
        >{{ m.label }}</view>
      </scroll-view>
      <scroll-view class="dist-filter__scroll" scroll-x>
        <view
          v-for="s in statusTabs" :key="s.value"
          :class="['dist-filter__tab', activeStatus === s.value && 'dist-filter__tab--active']"
          @tap="onChangeStatus(s.value)"
        >{{ s.label }}</view>
      </scroll-view>
    </view>

    <!-- 骨架 -->
    <view v-if="loading && !list.length" class="dist-list">
      <view v-for="i in 3" :key="i" class="dist-card">
        <u-skeleton rows="3" :loading="true" animation="wave" />
      </view>
    </view>

    <!-- 明细列表 -->
    <view v-else class="dist-list">
      <view
        v-for="item in list"
        :key="item.ledgerId"
        :class="['dist-card', expandedId === item.ledgerId && 'dist-card--expanded']"
        @tap="toggleExpand(item.ledgerId)"
      >
        <view class="dist-card__top">
          <view class="dist-card__info">
            <view class="dist-card__tags">
              <u-tag :text="statusLabel(item.status)" :type="statusType(item.status)" size="mini" shape="circle" />
              <u-tag v-if="isMyMonth(item)" text="本月" type="warning" size="mini" shape="circle" plain />
            </view>
            <!-- TODO(backend): ledger 返回需补 inviteeChannelName（当前仅 inviteeChannelId） -->
            <text class="dist-card__channel">下级 · {{ item.inviteeChannelName || '渠道 #' + item.inviteeChannelId }}</text>
            <text class="dist-card__order" v-if="item.orderNo">订单号 {{ item.orderNo }}</text>
          </view>
          <view class="dist-card__amount-col">
            <text class="dist-card__amount">+¥{{ formatAmount(item.amount) }}</text>
            <text v-if="item.appliedRate != null" class="dist-card__rate">费率 {{ (Number(item.appliedRate) * 100).toFixed(1) }}%</text>
          </view>
        </view>

        <!-- 展开：结算路径说明 -->
        <view v-if="expandedId === item.ledgerId" class="dist-card__detail">
          <view class="dist-card__row">
            <text class="dist-card__row-label">订单实付金额</text>
            <text class="dist-card__row-value">¥{{ formatAmount(item.baseAmount) }}</text>
          </view>
          <view class="dist-card__row">
            <text class="dist-card__row-label">应用费率</text>
            <text class="dist-card__row-value">{{ item.appliedRate != null ? (Number(item.appliedRate) * 100).toFixed(2) + '%' : '--' }}</text>
          </view>
          <view class="dist-card__row" v-if="item.compressRatio != null && Number(item.compressRatio) < 1">
            <text class="dist-card__row-label">压缩系数（防套利）</text>
            <text class="dist-card__row-value dist-card__row-value--warn">×{{ Number(item.compressRatio).toFixed(2) }}</text>
          </view>
          <view class="dist-card__row">
            <text class="dist-card__row-label">分润金额</text>
            <text class="dist-card__row-value dist-card__row-value--accent">¥{{ formatAmount(item.amount) }}</text>
          </view>
          <view class="dist-card__divider" />
          <view class="dist-card__row">
            <text class="dist-card__row-label">到账时间</text>
            <text class="dist-card__row-value">{{ accrueHint(item) }}</text>
          </view>
          <view class="dist-card__note">
            <u-icon name="info-circle" size="24" color="#9CA3AF" />
            <text class="dist-card__note-text">T+7 售后期结束自动到账；若订单退款将被撤销</text>
          </view>
        </view>

        <view v-else class="dist-card__meta">
          <text class="dist-card__meta-item">{{ formatTime(item.createTime) }}</text>
          <text class="dist-card__meta-item dist-card__meta-item--action">查看详情 ›</text>
        </view>
      </view>

      <jst-empty
        v-if="!loading && !list.length"
        text="暂无分销记录"
        icon="📭"
        button-text="去分享邀请码"
        button-url="/pages-sub/channel/invite-code"
      />
      <u-loadmore
        v-if="list.length || (loading && list.length)"
        :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')"
        @loadmore="loadMore"
        margin-top="20"
      />
    </view>

    <!-- 底部固定栏 -->
    <view class="dist-bar" v-if="canWithdraw">
      <view class="dist-bar__info">
        <text class="dist-bar__label">可提现分销收益</text>
        <text class="dist-bar__amount">¥{{ formatAmount(summary.accruedAmount) }}</text>
      </view>
      <u-button class="dist-bar__btn" type="primary" shape="circle" @click="goWithdraw">申请提现</u-button>
    </view>
  </view>
</template>

<script>
import { getDistributionSummary, listDistributionLedger } from '@/api/invite'

const STATUS_LABEL = { pending: '待结算', accrued: '已到账', cancelled: '已取消' }
const STATUS_TYPE = { pending: 'warning', accrued: 'success', cancelled: 'info' }

const STATUS_TABS = [
  { value: '', label: '全部' },
  { value: 'pending', label: '待结算' },
  { value: 'accrued', label: '已到账' },
  { value: 'cancelled', label: '已取消' }
]

function ymStr(d) {
  return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0')
}

export default {
  data() {
    return {
      summary: { totalAmount: '0.00', pendingAmount: '0.00', accruedAmount: '0.00', monthAmount: '0.00' },
      list: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      hasMore: true,
      activeMonth: 'current', // current / last / recent3 / all
      activeStatus: '',
      statusTabs: STATUS_TABS,
      expandedId: null
    }
  },
  computed: {
    navPaddingTop() {
      const si = uni.getSystemInfoSync()
      return (si.statusBarHeight || 20) + 'px'
    },
    monthOptions() {
      const now = new Date()
      const last = new Date(now.getFullYear(), now.getMonth() - 1, 1)
      return [
        { value: 'current', label: '本月 ' + ymStr(now).slice(5) },
        { value: 'last', label: '上月 ' + ymStr(last).slice(5) },
        { value: 'recent3', label: '近 3 月' },
        { value: 'all', label: '全部' }
      ]
    },
    canWithdraw() {
      return Number(this.summary.accruedAmount || 0) > 0
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
    goInviteCode() {
      uni.navigateTo({ url: '/pages-sub/channel/invite-code' })
    },
    goWithdraw() {
      // 复用渠道返点提现流程；后端若后续分销独立提现入口，仅改此路径
      uni.navigateTo({ url: '/pages-sub/channel/withdraw-apply' })
    },
    async loadAll() {
      await Promise.all([this.loadSummary(), this.loadList(true)])
    },
    async loadSummary() {
      try {
        const data = await getDistributionSummary()
        this.summary.totalAmount = (data && data.totalAmount) || '0.00'
        this.summary.pendingAmount = (data && data.pendingAmount) || '0.00'
        this.summary.accruedAmount = (data && data.accruedAmount) || '0.00'
        // 本月金额：前端聚合（后端 summary 暂不分月，拉第一页 ledger 聚合）
        try {
          const res = await listDistributionLedger({ pageNum: 1, pageSize: 100 })
          const rows = (res && res.rows) || []
          const ym = ymStr(new Date())
          const sum = rows.reduce((acc, r) => {
            if (r.status === 'cancelled') return acc
            if (!String(r.createTime || '').startsWith(ym)) return acc
            return acc + Number(r.amount || 0)
          }, 0)
          this.summary.monthAmount = sum.toFixed(2)
        } catch (e) {}
      } catch (e) {}
    },
    async loadList(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await listDistributionLedger({
          status: this.activeStatus || undefined,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        })
        const rows = (res && res.rows) || []
        const filtered = this.filterByMonth(rows)
        this.total = (res && res.total) || 0
        this.list = reset ? filtered : this.list.concat(filtered)
        // 月份筛选在前端做，分页仍以后端 total 判断；如前端过滤后数量不足，继续拉下一页
        this.hasMore = this.pageNum * this.pageSize < this.total
        if (this.hasMore) this.pageNum += 1
      } finally {
        this.loading = false
      }
    },
    filterByMonth(rows) {
      if (this.activeMonth === 'all') return rows
      const now = new Date()
      const curYm = ymStr(now)
      const lastYm = ymStr(new Date(now.getFullYear(), now.getMonth() - 1, 1))
      const threeMonthAgo = new Date(now.getTime() - 90 * 24 * 3600 * 1000)
      return rows.filter(r => {
        const t = String(r.createTime || '')
        if (this.activeMonth === 'current') return t.startsWith(curYm)
        if (this.activeMonth === 'last') return t.startsWith(lastYm)
        if (this.activeMonth === 'recent3') {
          const d = new Date(t.replace('T', ' '))
          return d >= threeMonthAgo
        }
        return true
      })
    },
    loadMore() { this.loadList(false) },
    onChangeMonth(v) {
      if (this.activeMonth === v) return
      this.activeMonth = v
      this.loadList(true)
    },
    onChangeStatus(v) {
      if (this.activeStatus === v) return
      this.activeStatus = v
      this.loadList(true)
    },
    toggleExpand(id) {
      this.expandedId = this.expandedId === id ? null : id
    },
    isMyMonth(item) {
      return String(item.createTime || '').startsWith(ymStr(new Date()))
    },
    accrueHint(item) {
      if (item.status === 'accrued') return '已于 ' + this.formatTime(item.accruedAt) + ' 到账'
      if (item.status === 'cancelled') return '已取消（订单退款）'
      if (item.accrueAt) return '预计 ' + this.formatTime(item.accrueAt) + ' 到账'
      return '待售后期结束'
    },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' },
    statusType(s) { return STATUS_TYPE[s] || 'info' },
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

.dist-page {
  min-height: 100vh;
  padding-bottom: calc(220rpx + env(safe-area-inset-bottom));
  background: $jst-bg-page;
}

/* Hero */
.dist-hero {
  padding: 0 $jst-space-xl 48rpx;
  background: linear-gradient(150deg, $jst-indigo 0%, $jst-indigo-light 55%, $jst-indigo 120%);
  border-bottom-left-radius: 32rpx;
  border-bottom-right-radius: 32rpx;
}
.dist-hero__nav {
  display: flex; align-items: center; justify-content: space-between;
  padding-top: 24rpx; margin-bottom: $jst-space-xl;
}
.dist-hero__back {
  width: 64rpx; height: 64rpx; border-radius: $jst-radius-sm;
  background: rgba(255, 255, 255, 0.18);
  color: $jst-text-inverse; text-align: center;
  line-height: 64rpx; font-size: 36rpx;
}
.dist-hero__title {
  flex: 1; text-align: center;
  font-size: 34rpx; font-weight: 600;
  color: $jst-text-inverse;
}
.dist-hero__action {
  padding: 10rpx 24rpx; border-radius: $jst-radius-round;
  background: rgba(255, 213, 79, 0.22);
  border: 2rpx solid rgba(255, 213, 79, 0.5);
  color: $jst-gold; font-size: 24rpx; font-weight: 600;
}

.dist-hero__kpi {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
}
.dist-hero__kpi-cell {
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.12);
  border: 2rpx solid rgba(255, 255, 255, 0.18);
  border-radius: $jst-radius-md;
}
.dist-hero__kpi-cell--main {
  grid-column: span 2;
  background: rgba(255, 213, 79, 0.18);
  border-color: rgba(255, 213, 79, 0.4);
}
.dist-hero__kpi-num {
  display: block; font-size: 40rpx; font-weight: 700;
  color: $jst-text-inverse;
}
.dist-hero__kpi-num--gold { color: $jst-gold; font-size: 54rpx; }
.dist-hero__kpi-num--warning { color: rgba($jst-warning, 0.85); }
.dist-hero__kpi-num--success { color: rgba($jst-success, 0.85); }
.dist-hero__kpi-label {
  display: block; margin-top: 8rpx;
  font-size: 22rpx; color: rgba(255, 255, 255, 0.78);
}

/* Filter */
.dist-filter {
  margin: $jst-space-md $jst-space-xl 0;
  padding: $jst-space-md;
  background: $jst-bg-card;
  border-radius: $jst-radius-md;
  box-shadow: $jst-shadow-sm;
}
.dist-filter__scroll {
  white-space: nowrap;
  & + & { margin-top: $jst-space-sm; padding-top: $jst-space-sm; border-top: 2rpx dashed $jst-border; }
}
.dist-filter__chip {
  display: inline-block;
  padding: 10rpx 28rpx; margin-right: 16rpx;
  border-radius: $jst-radius-round;
  background: $jst-bg-grey;
  color: $jst-text-secondary;
  font-size: 24rpx;
}
.dist-filter__chip--active {
  background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light);
  color: $jst-text-inverse;
  font-weight: $jst-weight-semibold;
}
.dist-filter__tab {
  display: inline-block;
  padding: 8rpx 24rpx; margin-right: 12rpx;
  border-radius: $jst-radius-sm;
  background: transparent;
  color: $jst-text-secondary;
  font-size: 24rpx;
  border: 2rpx solid $jst-border;
}
.dist-filter__tab--active {
  color: $jst-indigo;
  font-weight: $jst-weight-semibold;
  border-color: $jst-indigo;
  background: rgba(26, 35, 126, 0.06);
}

/* List */
.dist-list { padding: $jst-space-md 0; }
.dist-card {
  margin: $jst-space-md $jst-space-xl 0;
  padding: $jst-space-lg;
  background: $jst-bg-card;
  border-radius: $jst-radius-md;
  box-shadow: $jst-shadow-sm;
  transition: box-shadow 0.2s;
}
.dist-card--expanded { box-shadow: $jst-shadow-md; }
.dist-card__top { display: flex; align-items: flex-start; gap: $jst-space-md; }
.dist-card__info { flex: 1; min-width: 0; }
.dist-card__tags {
  display: flex; gap: 8rpx; flex-wrap: wrap; margin-bottom: 10rpx;
}
.dist-card__channel {
  display: block; font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary; margin-bottom: 6rpx;
}
.dist-card__order {
  display: block; font-size: $jst-font-xs;
  color: $jst-text-secondary;
}
.dist-card__amount-col {
  text-align: right; flex-shrink: 0;
}
.dist-card__amount {
  display: block; font-size: 40rpx;
  font-weight: 700; color: $jst-success;
}
.dist-card__rate {
  display: block; margin-top: 4rpx;
  font-size: $jst-font-xs; color: $jst-text-secondary;
}

.dist-card__detail {
  margin-top: $jst-space-md;
  padding-top: $jst-space-md;
  border-top: 2rpx dashed $jst-border;
}
.dist-card__row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 8rpx 0;
}
.dist-card__row-label {
  font-size: $jst-font-sm; color: $jst-text-secondary;
}
.dist-card__row-value {
  font-size: $jst-font-sm; color: $jst-text-primary;
  font-weight: $jst-weight-semibold;
}
.dist-card__row-value--accent { color: $jst-success; font-size: $jst-font-md; }
.dist-card__row-value--warn { color: $jst-warning; }
.dist-card__divider {
  height: 2rpx; background: $jst-border; margin: $jst-space-xs 0;
}
.dist-card__note {
  display: flex; align-items: center; gap: 8rpx;
  margin-top: $jst-space-md;
  padding: $jst-space-sm;
  background: $jst-bg-grey;
  border-radius: $jst-radius-sm;
}
.dist-card__note-text {
  flex: 1;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
  line-height: 1.5;
}

.dist-card__meta {
  display: flex; justify-content: space-between; align-items: center;
  margin-top: $jst-space-md;
  padding-top: $jst-space-md;
  border-top: 2rpx dashed $jst-border;
}
.dist-card__meta-item { font-size: $jst-font-xs; color: $jst-text-secondary; }
.dist-card__meta-item--action { color: $jst-indigo; font-weight: $jst-weight-semibold; }

/* 底部固定栏 */
.dist-bar {
  position: fixed; left: 0; right: 0; bottom: 0;
  display: flex; align-items: center; gap: 20rpx;
  padding: 20rpx 32rpx calc(20rpx + env(safe-area-inset-bottom));
  background: $jst-bg-card;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.dist-bar__info { flex: 1; }
.dist-bar__label { display: block; font-size: 22rpx; color: $jst-text-secondary; }
.dist-bar__amount { display: block; font-size: 40rpx; font-weight: 700; color: $jst-success; }
::v-deep .dist-bar__btn.u-button {
  width: 240rpx; height: 88rpx;
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light);
  border: none;
}
</style>
