<template>
  <view class="refund-list-page">
    <jst-loading :loading="pageLoading" text="退款记录加载中..." />

    <view class="refund-list-page__nav">
      <view class="refund-list-page__back" @tap="handleBack"><</view>
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
          <view class="refund-list-page__meta-row">
            <text class="refund-list-page__meta-key">申请金额</text>
            <text class="refund-list-page__meta-value refund-list-page__meta-value--price">
              ¥{{ formatAmount(item.applyCash) }}
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
          <button
            v-if="item.status === 'pending'"
            class="refund-list-page__action refund-list-page__action--ghost"
            @tap="handleCancel(item)"
          >
            撤销申请
          </button>
          <button class="refund-list-page__action refund-list-page__action--primary" @tap="openDetail(item)">
            查看详情
          </button>
        </view>
      </view>

      <view class="refund-list-page__loadmore">
        <text class="refund-list-page__loadmore-text">{{ loadMoreText }}</text>
      </view>
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
        { label: '待审核', value: 'pending' },
        { label: '已通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' },
        { label: '退款中', value: 'refunding' },
        { label: '已完成', value: 'completed' },
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
      const mapper = {
        pending: '待审核',
        approved: '已通过',
        rejected: '已驳回',
        refunding: '退款中',
        completed: '已完成',
        closed: '已关闭'
      }
      return mapper[status] || '处理中'
    },

    getStatusTone(status) {
      const mapper = {
        pending: 'pending',
        approved: 'active',
        rejected: 'danger',
        refunding: 'refund',
        completed: 'success',
        closed: 'gray'
      }
      return mapper[status] || 'active'
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
.refund-list-page {
  min-height: 100vh;
  background: #f4f7fc;
}

.refund-list-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: linear-gradient(135deg, #0c3d6b 0%, #2f7de1 100%);
}

.refund-list-page__back,
.refund-list-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.refund-list-page__back {
  font-size: 34rpx;
  color: #ffffff;
}

.refund-list-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: #ffffff;
}

.refund-list-page__tabs {
  white-space: nowrap;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(14, 58, 113, 0.05);
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
  background: #f4f7fc;
  font-size: 24rpx;
  color: #72809a;
}

.refund-list-page__tab--active {
  background: #eef4ff;
  color: #2f7de1;
  font-weight: 700;
}

.refund-list-page__list {
  padding: 20rpx 24rpx 28rpx;
}

.refund-list-page__card {
  margin-bottom: 20rpx;
  border-radius: 28rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 28rpx rgba(14, 58, 113, 0.06);
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
  color: #1f2937;
}

.refund-list-page__card-subtitle {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #7a869d;
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
  color: #7a869d;
}

.refund-list-page__meta-value {
  color: #1f2937;
  text-align: right;
}

.refund-list-page__meta-value--price {
  font-size: 30rpx;
  font-weight: 800;
  color: #ff6a3d;
}

.refund-list-page__remark {
  margin: 0 24rpx;
  padding: 16rpx 20rpx;
  border-radius: 20rpx;
  background: #fff1f1;
  font-size: 22rpx;
  line-height: 1.6;
  color: #eb5757;
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
  border: 2rpx solid #d6e4fb;
  background: #ffffff;
  color: #2f7de1;
}

.refund-list-page__action--primary {
  background: #2f7de1;
  color: #ffffff;
}

.refund-list-page__loadmore {
  padding: 12rpx 0 8rpx;
  text-align: center;
}

.refund-list-page__loadmore-text {
  font-size: 22rpx;
  color: #7a869d;
}

:deep(.jst-status-badge--pending) {
  background: rgba(255, 138, 0, 0.14);
}

:deep(.jst-status-badge--pending .jst-status-badge__text) {
  color: #ff8a00;
}

:deep(.jst-status-badge--active) {
  background: rgba(46, 125, 255, 0.14);
}

:deep(.jst-status-badge--active .jst-status-badge__text) {
  color: #2e7dff;
}

:deep(.jst-status-badge--refund) {
  background: rgba(139, 92, 246, 0.14);
}

:deep(.jst-status-badge--refund .jst-status-badge__text) {
  color: #8b5cf6;
}

:deep(.jst-status-badge--success) {
  background: rgba(82, 196, 26, 0.14);
}

:deep(.jst-status-badge--success .jst-status-badge__text) {
  color: #52c41a;
}
</style>
