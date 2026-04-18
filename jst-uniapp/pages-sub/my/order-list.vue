<template>
  <view class="order-list-page">
    <jst-loading :loading="pageLoading" text="订单加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view class="order-list-page__nav">
      <view class="order-list-page__back" @tap="handleBack">返回</view>
      <text class="order-list-page__nav-title">我的订单</text>
      <view class="order-list-page__nav-placeholder"></view>
    </view>

    <scroll-view class="order-list-page__tabs" scroll-x>
      <view class="order-list-page__tabs-inner">
        <view
          v-for="item in tabs"
          :key="item.value"
          class="order-list-page__tab"
          :class="{ 'order-list-page__tab--active': currentTab === item.value }"
          @tap="switchTab(item.value)"
        >
          {{ item.label }}
        </view>
      </view>
    </scroll-view>

    <view v-if="list.length" class="order-list-page__list">
      <view
        v-for="item in list"
        :key="item.orderId"
        class="order-list-page__card"
      >
        <view class="order-list-page__card-header">
          <view class="order-list-page__card-main">
            <text class="order-list-page__card-title">{{ item.itemName || item.contestName || '订单' }}</text>
            <text class="order-list-page__card-subtitle">订单号：{{ item.orderNo || '--' }}</text>
          </view>
          <jst-status-badge :text="getStatusText(item)" :tone="getStatusTone(item)" />
        </view>

        <view class="order-list-page__meta">
          <view class="order-list-page__meta-row">
            <text class="order-list-page__meta-key">参赛人</text>
            <text class="order-list-page__meta-value">{{ item.participantName || '--' }}</text>
          </view>
          <view class="order-list-page__meta-row">
            <text class="order-list-page__meta-key">创建时间</text>
            <text class="order-list-page__meta-value">{{ formatDateTime(item.createTime) }}</text>
          </view>
          <view class="order-list-page__meta-row">
            <text class="order-list-page__meta-key">支付方式</text>
            <text class="order-list-page__meta-value">{{ getPayMethodText(item.payMethod) }}</text>
          </view>
        </view>

        <view class="order-list-page__footer">
          <view class="order-list-page__amount">
            <text class="order-list-page__amount-label">实付</text>
            <text class="order-list-page__amount-value">¥{{ formatAmount(item.netPayAmount) }}</text>
          </view>

          <view class="order-list-page__actions">
            <u-button
              v-if="canCancel(item)"
              class="order-list-page__action order-list-page__action--ghost"
              @click="handleCancel(item)"
            >
              取消订单
            </u-button>
            <u-button
              v-if="showPrimaryAction(item)"
              class="order-list-page__action order-list-page__action--primary"
              @click="handlePrimary(item)"
            >
              {{ getPrimaryActionText(item) }}
            </u-button>
          </view>
        </view>
      </view>

      <u-loadmore :status="loadMoreText === '加载中...' ? 'loading' : loadMoreText === '上拉加载更多' ? 'loadmore' : 'nomore'" />
    </view>

    <jst-empty
      v-else-if="!pageLoading"
      :illustration="EMPTY_ORDERS.illustration"
      :button-text="EMPTY_ORDERS.buttonText"
      :button-url="EMPTY_ORDERS.buttonUrl"
      :text="emptyText"
    />
  </view>
</template>

<script>
import { cancelOrder, getMyOrders } from '@/api/order'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'
import { EMPTY_ORDERS } from '@/utils/empty-state-preset'

const DEFAULT_PAGE_SIZE = 10

export default {
  components: {
    JstEmpty,
    JstLoading,
    JstStatusBadge
  },
  data() {
    return {
      EMPTY_ORDERS,
      pageLoading: false,
      currentTab: '',
      list: [],
      pageNum: 1,
      hasMore: true,
      loadingMore: false,
      tabs: [
        { label: '全部', value: '' },
        { label: '待支付', value: 'pending_pay' },
        { label: '已支付', value: 'paid' },
        { label: '进行中', value: 'in_service' },
        { label: '售后中', value: 'aftersale' },
        { label: '已完成', value: 'completed' },
        { label: '已取消', value: 'cancelled' },
        { label: '退款中', value: 'refunding' }
      ]
    }
  },
  computed: {
    loadMoreText() {
      if (this.pageLoading || this.loadingMore) {
        return '加载中...'
      }
      return this.hasMore ? '上拉加载更多' : '没有更多了'
    },
    // 根据当前 tab 动态显示空态文案，给用户更清晰的上下文
    emptyText() {
      const mapper = {
        '': '还没有订单记录，去浏览赛事报名试试',
        'pending_pay': '暂无待支付订单',
        'paid': '暂无已支付订单',
        'in_service': '暂无进行中的订单',
        'aftersale': '暂无售后订单',
        'completed': '暂无已完成订单',
        'cancelled': '暂无已取消订单',
        'refunding': '暂无退款中的订单'
      }
      return mapper[this.currentTab] || '当前筛选下还没有订单记录'
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
        const params = {
          pageNum: this.pageNum,
          pageSize: DEFAULT_PAGE_SIZE
        }
        if (this.currentTab && this.currentTab !== 'refunding') {
          params.status = this.currentTab
        }

        const response = await getMyOrders(params, { silent: true })
        let rows = Array.isArray(response && response.rows) ? response.rows : []
        if (this.currentTab === 'refunding') {
          rows = rows.filter((item) => this.isRefunding(item))
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
        url: `/pages-sub/my/order-detail?orderId=${item.orderId}`
      })
    },

    handlePrimary(item) {
      if (item.orderStatus === 'pending_pay') {
        this.openDetail(item)
        return
      }
      if (this.canApplyRefund(item)) {
        this.openDetail(item)
        return
      }
      if (this.isRefunding(item)) {
        uni.navigateTo({ url: '/pages-sub/my/refund-list' })
        return
      }
      this.openDetail(item)
    },

    handleCancel(item) {
      uni.showModal({
        title: '取消订单',
        content: '确认取消这笔待支付订单吗？',
        success: async (res) => {
          if (!res.confirm) {
            return
          }
          try {
            await cancelOrder(item.orderId)
            uni.showToast({ title: '已取消', icon: 'success' })
            this.fetchList(true)
          } catch (error) {}
        }
      })
    },

    canCancel(item) {
      return item.orderStatus === 'pending_pay'
    },

    canApplyRefund(item) {
      return item.orderStatus === 'paid' && item.refundStatus === 'none'
    },

    isRefunding(item) {
      return item.orderStatus === 'aftersale' || ['partial', 'full'].includes(item.refundStatus)
    },

    showPrimaryAction() {
      return true
    },

    getPrimaryActionText(item) {
      if (item.orderStatus === 'pending_pay') {
        return '去支付'
      }
      if (this.canApplyRefund(item)) {
        return '申请退款'
      }
      if (this.isRefunding(item)) {
        return '查看退款'
      }
      return '查看详情'
    },

    getStatusText(item) {
      if (this.isRefunding(item) && item.orderStatus !== 'completed') {
        return '退款中'
      }
      const mapper = {
        created: '已创建',
        pending_pay: '待支付',
        paid: '已支付',
        reviewing: '审核中',
        in_service: '进行中',
        aftersale: '售后中',
        completed: '已完成',
        cancelled: '已取消',
        closed: '已关闭'
      }
      return mapper[item.orderStatus] || '处理中'
    },

    getStatusTone(item) {
      if (this.isRefunding(item) && item.orderStatus !== 'completed') {
        return 'refund'
      }
      const mapper = {
        created: 'gray',
        pending_pay: 'pending',
        paid: 'active',
        reviewing: 'active',
        in_service: 'active',
        aftersale: 'refund',
        completed: 'success',
        cancelled: 'gray',
        closed: 'gray'
      }
      return mapper[item.orderStatus] || 'active'
    },

    getPayMethodText(value) {
      const mapper = {
        wechat: '微信支付',
        bank_transfer: '线下转账',
        points: '积分支付'
      }
      return mapper[value] || '待确认'
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

.order-list-page {
  min-height: 100vh;
  background: $jst-bg-page;
}

.jst-page-skeleton {
  margin: $jst-space-lg;
}

.order-list-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: linear-gradient(135deg, $jst-brand-dark 0%, $jst-brand 100%);
}

.order-list-page__back,
.order-list-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.order-list-page__back {
  font-size: 34rpx;
  color: $jst-text-inverse;
}

.order-list-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: $jst-text-inverse;
}

.order-list-page__tabs {
  white-space: nowrap;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.order-list-page__tabs-inner {
  display: inline-flex;
  padding: 16rpx 16rpx 18rpx;
}

.order-list-page__tab {
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

.order-list-page__tab--active {
  background: $jst-brand-light;
  color: $jst-brand;
  font-weight: 700;
}

.order-list-page__list {
  padding: 20rpx 24rpx 28rpx;
}

.order-list-page__card {
  margin-bottom: 20rpx;
  border-radius: 28rpx;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
  overflow: hidden;
}

.order-list-page__card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 28rpx 24rpx 20rpx;
}

.order-list-page__card-main {
  flex: 1;
  min-width: 0;
}

.order-list-page__card-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.5;
  color: $jst-text-primary;
}

.order-list-page__card-subtitle {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: $jst-text-secondary;
}

.order-list-page__meta {
  padding: 0 24rpx 12rpx;
}

.order-list-page__meta-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  padding-bottom: 16rpx;
}

.order-list-page__meta-key,
.order-list-page__meta-value {
  font-size: 24rpx;
  line-height: 1.6;
}

.order-list-page__meta-key {
  color: $jst-text-secondary;
}

.order-list-page__meta-value {
  color: $jst-text-primary;
  text-align: right;
}

.order-list-page__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 20rpx 24rpx 24rpx;
  border-top: 2rpx solid $jst-border;
}

.order-list-page__amount {
  display: flex;
  flex-direction: column;
}

.order-list-page__amount-label {
  font-size: 22rpx;
  color: $jst-text-secondary;
}

.order-list-page__amount-value {
  margin-top: 8rpx;
  font-size: 34rpx;
  font-weight: 800;
  color: $jst-warning;
}

.order-list-page__actions {
  display: flex;
  justify-content: flex-end;
  gap: 12rpx;
}

.order-list-page__action {
  min-width: 172rpx;
  height: 76rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 700;
  line-height: 76rpx;
}

.order-list-page__action--ghost {
  border: 2rpx solid $jst-border;
  background: $jst-bg-card;
  color: $jst-brand;
}

.order-list-page__action--primary {
  background: $jst-brand;
  color: $jst-text-inverse;
}

.order-list-page__loadmore {
  padding: 12rpx 0 8rpx;
  text-align: center;
}

.order-list-page__loadmore-text {
  font-size: 22rpx;
  color: $jst-text-secondary;
}

::v-deep .order-list-page__action.u-button {
  min-height: 76rpx;
  border: none;
}
</style>
