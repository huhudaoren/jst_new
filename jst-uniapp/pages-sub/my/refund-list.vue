<template>
  <view class="refund-list-page">
    <jst-loading :loading="pageLoading" text="退款记录加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view class="refund-list-page__nav">
      <view class="refund-list-page__back" @tap="handleBack">返回</view>
      <text class="refund-list-page__nav-title">我的退款</text>
      <view class="refund-list-page__nav-placeholder"></view>
    </view>

    <scroll-view class="refund-list-page__tabs" scroll-x>
      <view class="refund-list-page__tabs-inner">
        <view
          v-for="item in tabs"
          :key="item.value"
          class="refund-list-page__tab"
          :class="{ 'refund-list-page__tab--active': currentTab === item.value }"
          @tap="switchTab(item.value)"
        >
          {{ item.label }}
        </view>
      </view>
    </scroll-view>

    <view v-if="list.length" class="refund-list-page__list">
      <view
        v-for="item in list"
        :key="item.refundId"
        class="refund-list-page__card"
      >
        <view class="refund-list-page__card-header">
          <view class="refund-list-page__card-main">
            <text class="refund-list-page__card-title">{{ item.contestName || item.itemName || '退款单' }}</text>
            <text class="refund-list-page__card-subtitle">退款单号：{{ item.refundNo || '--' }}</text>
          </view>
          <jst-status-badge :text="getStatusText(item.status)" :tone="getStatusTone(item.status)" />
        </view>

        <view class="refund-list-page__meta">
          <view class="refund-list-page__meta-row">
            <text class="refund-list-page__meta-key">关联订单</text>
            <text class="refund-list-page__meta-value">{{ item.orderNo || '--' }}</text>
          </view>
          <!-- 中文注释: 现金退款行 -->
          <view class="refund-list-page__meta-row">
            <text class="refund-list-page__meta-key">💰 现金退款</text>
            <text class="refund-list-page__meta-value refund-list-page__meta-value--price">
              ¥{{ formatAmount(getCashAmount(item)) }}
            </text>
          </view>
          <!-- 中文注释: 券原路回行 — 仅当涉及券时显示 (后端返回 couponRefund 对象) -->
          <view v-if="hasCouponRefund(item)" class="refund-list-page__meta-row">
            <text class="refund-list-page__meta-key">🎟️ 券原路回</text>
            <text class="refund-list-page__meta-value">{{ formatCouponRefund(item) }}</text>
          </view>
          <!-- 中文注释: 积分回退行 — 仅当涉及积分时显示 (后端返回 pointsRefund, 缺失时兜底) -->
          <view v-if="hasPointsRefund(item)" class="refund-list-page__meta-row">
            <text class="refund-list-page__meta-key">⭐ 积分回退</text>
            <text class="refund-list-page__meta-value">+{{ getPointsRefund(item) }} 积分</text>
          </view>
          <!-- 中文注释: 合计退款 — 至少包含现金 (积分/券为非金额型回退) -->
          <view class="refund-list-page__meta-row refund-list-page__meta-row--total">
            <text class="refund-list-page__meta-key">合计退款</text>
            <text class="refund-list-page__meta-value refund-list-page__meta-value--total">
              ¥{{ formatAmount(getCashAmount(item)) }}
              <text v-if="hasPointsRefund(item) || hasCouponRefund(item)" class="refund-list-page__meta-extra">
                + {{ buildExtraLabel(item) }}
              </text>
            </text>
          </view>
          <view class="refund-list-page__meta-row">
            <text class="refund-list-page__meta-key">申请时间</text>
            <text class="refund-list-page__meta-value">{{ formatDateTime(item.createTime) }}</text>
          </view>
        </view>

        <view v-if="item.auditRemark" class="refund-list-page__remark">
          审核说明：{{ item.auditRemark }}
        </view>

        <view class="refund-list-page__actions">
          <u-button
            v-if="item.status === 'pending'"
            class="refund-list-page__action refund-list-page__action--ghost"
            @click="handleCancel(item)"
          >
            撤销申请
          </u-button>
          <u-button class="refund-list-page__action refund-list-page__action--primary" @click="openDetail(item)">
            查看详情
          </u-button>
        </view>
      </view>

      <u-loadmore :status="loadMoreText === '加载中...' ? 'loading' : loadMoreText === '上拉加载更多' ? 'loadmore' : 'nomore'" />
    </view>

    <jst-empty
      v-else-if="!pageLoading"
      icon="💸"
      text="当前还没有退款记录。"
    />
  </view>
</template>

<script>
import { cancelRefund, getMyRefunds } from '@/api/refund'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'

const DEFAULT_PAGE_SIZE = 10

export default {
  components: {
    JstEmpty,
    JstLoading,
    JstStatusBadge
  },
  data() {
    return {
      pageLoading: false,
      currentTab: '',
      list: [],
      pageNum: 1,
      hasMore: true,
      loadingMore: false,
      tabs: [
        { label: '全部', value: '' },
        { label: '申请中', value: 'pending' },
        { label: '审核中', value: 'reviewing' },
        { label: '已通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' },
        { label: '退款中', value: 'refunding' },
        { label: '已到账', value: 'completed' },
        { label: '已关闭', value: 'closed' }
      ]
    }
  },
  computed: {
    loadMoreText() {
      if (this.pageLoading || this.loadingMore) {
        return '加载中...'
      }
      return this.hasMore ? '上拉加载更多' : '没有更多了'
    }
  },
  onLoad(query) {
    this.currentTab = query.status || ''
  },
  onShow() {
    this.fetchList(true)
  },
  onPullDownRefresh() {
    this.fetchList(true)
  },
  onReachBottom() {
    if (!this.pageLoading && !this.loadingMore && this.hasMore) {
      this.fetchList(false)
    }
  },
  methods: {
    async fetchList(reset = false) {
      if (reset) {
        this.pageNum = 1
        this.hasMore = true
        this.pageLoading = true
      } else {
        this.loadingMore = true
      }

      try {
        const response = await getMyRefunds(
          {
            pageNum: this.pageNum,
            pageSize: DEFAULT_PAGE_SIZE
          },
          { silent: true }
        )
        let rows = Array.isArray(response && response.rows) ? response.rows : []
        if (this.currentTab) {
          rows = rows.filter((item) => item.status === this.currentTab)
        }
        this.list = reset ? rows : this.list.concat(rows)
        const total = Number(response && response.total) || 0
        this.hasMore = this.list.length < total && rows.length > 0
        if (rows.length > 0) {
          this.pageNum += 1
        } else if (reset) {
          this.hasMore = false
        }
      } catch (error) {
        if (reset) {
          this.list = []
        }
        this.hasMore = false
      } finally {
        this.pageLoading = false
        this.loadingMore = false
        uni.stopPullDownRefresh()
      }
    },

    switchTab(value) {
      if (this.currentTab === value) {
        return
      }
      this.currentTab = value
      this.fetchList(true)
    },

    openDetail(item) {
      uni.navigateTo({
        url: `/pages-sub/my/refund-detail?refundId=${item.refundId}`
      })
    },

    handleCancel(item) {
      uni.showModal({
        title: '撤销退款',
        content: '确认撤销这笔待审核退款申请吗？',
        success: async (res) => {
          if (!res.confirm) {
            return
          }
          try {
            await cancelRefund(item.refundId)
            uni.showToast({ title: '已撤销', icon: 'success' })
            this.fetchList(true)
          } catch (error) {}
        }
      })
    },

    getStatusText(status) {
      // 中文注释: 5 态 chip 命名对齐 UX 要求（申请中/审核中/已通过/已到账/已驳回）
      const mapper = {
        pending: '申请中',
        reviewing: '审核中',
        approved: '已通过',
        rejected: '已驳回',
        refunding: '退款中',
        completed: '已到账',
        closed: '已关闭'
      }
      return mapper[status] || '处理中'
    },

    getStatusTone(status) {
      const mapper = {
        pending: 'pending',
        reviewing: 'pending',
        approved: 'active',
        rejected: 'danger',
        refunding: 'refund',
        completed: 'success',
        closed: 'gray'
      }
      return mapper[status] || 'active'
    },

    // 中文注释: 现金退款取值优先级 actualCash > applyCash
    getCashAmount(item) {
      const actual = Number(item.actualCash)
      if (!Number.isNaN(actual) && actual > 0) {
        return actual
      }
      return item.applyCash
    },

    // 中文注释: 积分回退 — 优先后端 pointsRefund (整数), 缺失时 fallback actualPoints / applyPoints
    getPointsRefund(item) {
      if (item && item.pointsRefund != null) {
        const n = Number(item.pointsRefund)
        if (!Number.isNaN(n) && n > 0) return n
      }
      const actualPoints = Number(item && item.actualPoints)
      if (!Number.isNaN(actualPoints) && actualPoints > 0) {
        return actualPoints
      }
      return Number(item && item.applyPoints) || 0
    },

    hasPointsRefund(item) {
      return this.getPointsRefund(item) > 0
    },

    // 中文注释: 判断是否含券回退 — 优先后端 couponRefund 对象, 兼容旧 couponReturned 标志
    hasCouponRefund(item) {
      const cr = item && (item.couponRefund || (item.couponReturned ? { couponName: '优惠券已退回' } : null))
      if (cr && (cr.couponName || cr.faceAmount)) return true
      return !!(item && item.couponReturned)
    },

    // 中文注释: 后端返回 couponRefund 对象 → 显示 "券名 ¥面额"; 否则降级提示已返还
    formatCouponRefund(item) {
      const cr = (item && item.couponRefund) || (item && item.couponReturned ? { couponName: '优惠券已退回' } : null)
      if (cr && cr.couponName) {
        const face = Number(cr.faceAmount)
        if (!Number.isNaN(face) && face > 0) {
          return `${cr.couponName} ¥${face.toFixed(2)}`
        }
        return cr.couponName
      }
      if (cr && cr.faceAmount) {
        return `面额 ¥${Number(cr.faceAmount).toFixed(2)}`
      }
      return '已原路返回'
    },

    // 中文注释: 拼接合计行右侧的「+ 券/+ 积分」附加说明
    buildExtraLabel(item) {
      const parts = []
      if (this.hasCouponRefund(item)) {
        parts.push('券 1 张')
      }
      if (this.hasPointsRefund(item)) {
        parts.push(`${this.getPointsRefund(item)} 积分`)
      }
      return parts.join(' + ')
    },

    formatAmount(value) {
      const amount = Number(value)
      if (Number.isNaN(amount)) {
        return '0.00'
      }
      return amount.toFixed(2)
    },

    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return `${value}`.slice(0, 16).replace('T', ' ')
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.switchTab({ url: '/pages/my/index' })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.refund-list-page {
  min-height: 100vh;
  background: $jst-bg-page;
}

.jst-page-skeleton {
  margin: $jst-space-lg;
}

.refund-list-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: linear-gradient(135deg, $jst-brand-dark 0%, $jst-brand 100%);
}

.refund-list-page__back,
.refund-list-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.refund-list-page__back {
  font-size: 34rpx;
  color: $jst-text-inverse;
}

.refund-list-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: $jst-text-inverse;
}

.refund-list-page__tabs {
  white-space: nowrap;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.refund-list-page__tabs-inner {
  display: inline-flex;
  padding: 16rpx 16rpx 18rpx;
}

.refund-list-page__tab {
  min-width: 136rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  background: $jst-bg-page;
  font-size: 24rpx;
  color: $jst-text-secondary;
}

.refund-list-page__tab--active {
  background: $jst-brand-light;
  color: $jst-brand;
  font-weight: 700;
}

.refund-list-page__list {
  padding: 20rpx 24rpx 28rpx;
}

.refund-list-page__card {
  margin-bottom: 20rpx;
  border-radius: 28rpx;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
  overflow: hidden;
}

.refund-list-page__card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 28rpx 24rpx 20rpx;
}

.refund-list-page__card-main {
  flex: 1;
  min-width: 0;
}

.refund-list-page__card-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.5;
  color: $jst-text-primary;
}

.refund-list-page__card-subtitle {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: $jst-text-secondary;
}

.refund-list-page__meta {
  padding: 0 24rpx 8rpx;
}

.refund-list-page__meta-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  padding-bottom: 16rpx;
}

.refund-list-page__meta-key,
.refund-list-page__meta-value {
  font-size: 24rpx;
  line-height: 1.6;
}

.refund-list-page__meta-key {
  color: $jst-text-secondary;
}

.refund-list-page__meta-value {
  color: $jst-text-primary;
  text-align: right;
}

.refund-list-page__meta-value--price {
  font-size: 30rpx;
  font-weight: 800;
  color: $jst-warning;
}

.refund-list-page__meta-row--total {
  border-top: 2rpx dashed $jst-border;
  padding-top: 14rpx;
  margin-top: 4rpx;
}

.refund-list-page__meta-value--total {
  font-size: 30rpx;
  font-weight: $jst-weight-bold;
  color: $jst-warning;
}

.refund-list-page__meta-extra {
  display: inline-block;
  margin-left: $jst-space-xs;
  font-size: $jst-font-xs;
  font-weight: $jst-weight-regular;
  color: $jst-text-secondary;
}

.refund-list-page__remark {
  margin: 0 24rpx;
  padding: 16rpx 20rpx;
  border-radius: 20rpx;
  background: $jst-danger-light;
  font-size: 22rpx;
  line-height: 1.6;
  color: $jst-danger;
}

.refund-list-page__actions {
  display: flex;
  justify-content: flex-end;
  gap: 12rpx;
  padding: 20rpx 24rpx 24rpx;
}

.refund-list-page__action {
  min-width: 172rpx;
  height: 76rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 700;
  line-height: 76rpx;
}

.refund-list-page__action--ghost {
  border: 2rpx solid $jst-border;
  background: $jst-bg-card;
  color: $jst-brand;
}

.refund-list-page__action--primary {
  background: $jst-brand;
  color: $jst-text-inverse;
}

.refund-list-page__loadmore {
  padding: 12rpx 0 8rpx;
  text-align: center;
}

.refund-list-page__loadmore-text {
  font-size: 22rpx;
  color: $jst-text-secondary;
}

::v-deep .refund-list-page__action.u-button {
  min-height: 76rpx;
  border: none;
}
</style>
