<template>
  <view class="my-enroll-page">
    <jst-loading :loading="pageLoading" text="报名记录加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view class="my-enroll-page__nav">
      <view class="my-enroll-page__back" @tap="handleBack"><</view>
      <text class="my-enroll-page__nav-title">我的报名</text>
      <view class="my-enroll-page__nav-placeholder"></view>
    </view>

    <view class="my-enroll-page__tabs">
      <view
        v-for="item in tabs"
        :key="item.value"
        class="my-enroll-page__tab"
        :class="{ 'my-enroll-page__tab--active': currentTab === item.value }"
        @tap="switchTab(item.value)"
      >
        {{ item.label }}
      </view>
    </view>

    <view v-if="list.length" class="my-enroll-page__list">
      <view
        v-for="item in list"
        :key="item.enrollId"
        class="my-enroll-page__card"
      >
        <view class="my-enroll-page__card-header">
          <view class="my-enroll-page__card-main">
            <text class="my-enroll-page__card-title">{{ item.contestName || '赛事名称待补齐' }}</text>
            <text class="my-enroll-page__card-subtitle">
              参赛人：{{ item.participantName || '未关联' }} · 报名号：{{ item.enrollNo || '--' }}
            </text>
          </view>
          <jst-status-badge :text="getStatusText(item.auditStatus)" :tone="getStatusTone(item.auditStatus)" />
        </view>

        <view v-if="item.auditStatus === 'rejected'" class="my-enroll-page__banner my-enroll-page__banner--danger">
          当前报名已驳回，可进入详情查看原因。
        </view>
        <view v-else-if="item.auditStatus === 'supplement'" class="my-enroll-page__banner my-enroll-page__banner--warning">
          审核要求补充材料，请尽快处理。
        </view>

        <view class="my-enroll-page__meta">
          <view class="my-enroll-page__meta-row">
            <text class="my-enroll-page__meta-key">监护人手机</text>
            <text class="my-enroll-page__meta-value">{{ item.guardianMobileMasked || '--' }}</text>
          </view>
          <view class="my-enroll-page__meta-row">
            <text class="my-enroll-page__meta-key">材料状态</text>
            <text class="my-enroll-page__meta-value">{{ getMaterialStatusText(item.materialStatus) }}</text>
          </view>
          <view class="my-enroll-page__meta-row">
            <text class="my-enroll-page__meta-key">提交时间</text>
            <text class="my-enroll-page__meta-value">{{ formatDateTime(item.submitTime || item.updateTime) }}</text>
          </view>
        </view>

        <view class="my-enroll-page__actions">
          <u-button
            v-if="item.auditStatus === 'supplement'"
            class="my-enroll-page__action my-enroll-page__action--ghost"
            @click="goSupplement(item)"
          >
            去补充
          </u-button>
          <u-button class="my-enroll-page__action my-enroll-page__action--primary" @click="openDetail(item)">
            查看详情
          </u-button>
        </view>
      </view>
    </view>

    <jst-empty
      v-else-if="!pageLoading"
      icon="📄"
      text="当前筛选下还没有报名记录。"
      button-text="去浏览赛事"
      button-url="switchTab:/pages/contest/list"
    />
  </view>
</template>

<script>
import { getMyEnrolls } from '@/api/enroll'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'

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
      tabs: [
        { label: '全部', value: '' },
        { label: '待审核', value: 'pending' },
        { label: '已通过', value: 'approved' },
        { label: '待补充', value: 'supplement' },
        { label: '已驳回', value: 'rejected' }
      ]
    }
  },
  onLoad(query) {
    this.currentTab = query.auditStatus || ''
  },
  onShow() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.pageLoading = true
      try {
        const list = await getMyEnrolls(
          this.currentTab ? { auditStatus: this.currentTab } : {},
          { silent: true }
        )
        this.list = Array.isArray(list) ? list : []
      } catch (error) {
        this.list = []
      } finally {
        this.pageLoading = false
      }
    },

    switchTab(value) {
      if (this.currentTab === value) {
        return
      }
      this.currentTab = value
      this.fetchList()
    },

    openDetail(item) {
      uni.navigateTo({
        url: `/pages-sub/my/enroll-detail?id=${item.enrollId}`
      })
    },

    goSupplement(item) {
      uni.navigateTo({
        url: `/pages-sub/contest/enroll?contestId=${item.contestId}&enrollId=${item.enrollId}&mode=supplement`
      })
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.switchTab({ url: '/pages/my/index' })
    },

    getStatusText(status) {
      const mapper = {
        pending: '待审核',
        approved: '已通过',
        supplement: '待补充',
        rejected: '已驳回',
        draft: '草稿'
      }
      return mapper[status] || '处理中'
    },

    getStatusTone(status) {
      const mapper = {
        pending: 'accent',
        approved: 'success',
        supplement: 'warning',
        rejected: 'danger',
        draft: 'gray'
      }
      return mapper[status] || 'primary'
    },

    getMaterialStatusText(status) {
      const mapper = {
        complete: '材料齐全',
        supplement: '待补件',
        missing: '材料缺失'
      }
      return mapper[status] || '待确认'
    },

    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return `${value}`.slice(0, 16).replace('T', ' ')
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.my-enroll-page {
  min-height: 100vh;
  background: $jst-bg-page;
}

.jst-page-skeleton {
  margin: $jst-space-lg;
}

.my-enroll-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: linear-gradient(135deg, $jst-brand-dark 0%, $jst-brand 100%);
}

.my-enroll-page__back,
.my-enroll-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.my-enroll-page__back {
  font-size: 34rpx;
  color: $jst-text-inverse;
}

.my-enroll-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: $jst-text-inverse;
}

.my-enroll-page__tabs {
  display: flex;
  padding: 16rpx 12rpx 12rpx;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.my-enroll-page__tab {
  flex: 1;
  height: 76rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 6rpx;
  border-radius: 999rpx;
  color: $jst-text-secondary;
  font-size: 24rpx;
}

.my-enroll-page__tab--active {
  background: $jst-brand-light;
  color: $jst-brand;
  font-weight: 700;
}

.my-enroll-page__list {
  padding: 20rpx 24rpx 28rpx;
}

.my-enroll-page__card {
  margin-bottom: 20rpx;
  border-radius: 28rpx;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
  overflow: hidden;
}

.my-enroll-page__card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 28rpx 24rpx 20rpx;
}

.my-enroll-page__card-main {
  flex: 1;
  min-width: 0;
}

.my-enroll-page__card-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.5;
  color: $jst-text-primary;
}

.my-enroll-page__card-subtitle {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: $jst-text-secondary;
}

.my-enroll-page__banner {
  margin: 0 24rpx;
  padding: 16rpx 20rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  line-height: 1.6;
}

.my-enroll-page__banner--danger {
  background: $jst-danger-light;
  color: $jst-danger;
}

.my-enroll-page__banner--warning {
  background: $jst-warning-light;
  color: $jst-warning;
}

.my-enroll-page__meta {
  padding: 18rpx 24rpx 0;
}

.my-enroll-page__meta-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  padding-bottom: 18rpx;
}

.my-enroll-page__meta-key,
.my-enroll-page__meta-value {
  font-size: 24rpx;
  line-height: 1.6;
}

.my-enroll-page__meta-key {
  color: $jst-text-secondary;
}

.my-enroll-page__meta-value {
  color: $jst-text-primary;
  text-align: right;
}

.my-enroll-page__actions {
  display: flex;
  justify-content: flex-end;
  gap: 12rpx;
  padding: 20rpx 24rpx 24rpx;
}

.my-enroll-page__action {
  min-width: 180rpx;
  height: 76rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 700;
}

.my-enroll-page__action--ghost {
  border: 2rpx solid $jst-border;
  background: $jst-bg-card;
  color: $jst-brand;
}

.my-enroll-page__action--primary {
  background: $jst-brand;
  color: $jst-text-inverse;
}

::v-deep .my-enroll-page__action.u-button {
  border: none;
  min-height: 76rpx;
}
</style>
