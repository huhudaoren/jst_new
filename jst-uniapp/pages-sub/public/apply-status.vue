<!-- 中文注释: 赛事方入驻申请状态查询页；对应任务卡公开查询要求；对应接口 GET /jst/public/organizer/partner/apply/{applyNo}/status -->
<template>
  <view class="apply-status-page">
    <jst-loading :loading="loading" text="查询中..." />

    <view class="apply-status-page__nav" :style="{ paddingTop: navPaddingTop }">
      <view class="apply-status-page__back" @tap="handleBack">←</view>
      <text class="apply-status-page__nav-title">申请状态查询</text>
      <view class="apply-status-page__nav-placeholder"></view>
    </view>

    <view class="apply-status-page__hero">
      <text class="apply-status-page__hero-title">查询赛事方入驻审核进度</text>
      <text class="apply-status-page__hero-subtitle">输入申请单号，可反复查询不同申请记录</text>
    </view>

    <view class="apply-status-page__body">
      <view class="apply-status-page__panel">
        <text class="apply-status-page__label">申请单号</text>
        <input
          v-model.trim="applyNo"
          class="apply-status-page__input"
          placeholder="请输入申请单号，例如 PAxxxxxxxx"
          placeholder-class="apply-status-page__placeholder"
        />
        <button class="apply-status-page__query" :disabled="loading" @tap="handleQuery">
          立即查询
        </button>
      </view>

      <view v-if="currentResult" class="apply-status-page__result">
        <view class="apply-status-page__result-head">
          <text class="apply-status-page__result-title">当前结果</text>
          <text class="apply-status-page__result-no">{{ currentResult.applyNo }}</text>
        </view>

        <view class="apply-status-page__status-chip" :class="'apply-status-page__status-chip--' + currentResult.statusTheme">
          {{ currentResult.statusLabel }}
        </view>

        <view class="apply-status-page__info-row">
          <text class="apply-status-page__info-key">提交时间</text>
          <text class="apply-status-page__info-value">{{ formatDateTime(currentResult.submitTime) }}</text>
        </view>
        <view class="apply-status-page__info-row">
          <text class="apply-status-page__info-key">审核时间</text>
          <text class="apply-status-page__info-value">{{ formatDateTime(currentResult.auditTime) }}</text>
        </view>
        <view class="apply-status-page__remark">
          <text class="apply-status-page__remark-label">审核备注</text>
          <text class="apply-status-page__remark-text">{{ currentResult.auditRemark || '暂无备注' }}</text>
        </view>
      </view>

      <view class="apply-status-page__history">
        <view class="apply-status-page__history-head">
          <text class="apply-status-page__history-title">最近查询</text>
          <text v-if="historyList.length" class="apply-status-page__history-clear" @tap="clearHistory">清空</text>
        </view>

        <view v-if="historyList.length" class="apply-status-page__history-list">
          <view
            v-for="item in historyList"
            :key="item.applyNo"
            class="apply-status-page__history-item"
            @tap="reuseQuery(item)"
          >
            <view>
              <text class="apply-status-page__history-no">{{ item.applyNo }}</text>
              <text class="apply-status-page__history-time">{{ formatDateTime(item.submitTime) }}</text>
            </view>
            <text class="apply-status-page__history-status">{{ item.statusLabel }}</text>
          </view>
        </view>

        <jst-empty
          v-else
          icon="🔎"
          text="尚未查询过申请单号"
        />
      </view>
    </view>
  </view>
</template>

<script>
import { queryApplyStatus } from '@/api/public'

const STATUS_MAP = {
  draft: { label: '草稿', theme: 'draft' },
  pending: { label: '待审核', theme: 'pending' },
  approved: { label: '已通过', theme: 'approved' },
  rejected: { label: '已驳回', theme: 'rejected' },
  supplement: { label: '待补件', theme: 'supplement' }
}

export default {
  data() {
    return {
      applyNo: '',
      loading: false,
      currentResult: null,
      historyList: []
    }
  },
  onLoad(query) {
    this.applyNo = query.applyNo || ''

    if (this.applyNo) {
      this.handleQuery()
    }
  },
  methods: {
    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }

      uni.switchTab({ url: '/pages/index/index' })
    },

    normalizeResult(applyNo, data = {}) {
      const status = data.applyStatus || ''
      const statusMeta = STATUS_MAP[status] || { label: status || '未知状态', theme: 'draft' }

      return {
        applyNo,
        applyStatus: status,
        statusLabel: statusMeta.label,
        statusTheme: statusMeta.theme,
        auditRemark: data.auditRemark || '',
        submitTime: data.submitTime || '',
        auditTime: data.auditTime || ''
      }
    },

    formatDateTime(value) {
      if (!value) {
        return '--'
      }

      const date = new Date(String(value).replace(/ /g, 'T'))
      if (Number.isNaN(date.getTime())) {
        return value
      }

      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')

      return `${year}-${month}-${day} ${hour}:${minute}`
    },

    async handleQuery() {
      if (!this.applyNo) {
        uni.showToast({
          title: '请输入申请单号',
          icon: 'none'
        })
        return
      }

      this.loading = true

      try {
        const response = await queryApplyStatus(this.applyNo)
        const result = this.normalizeResult(this.applyNo, response || {})

        this.currentResult = result
        this.historyList = [result].concat(this.historyList.filter(item => item.applyNo !== result.applyNo))
      } catch (error) {
      } finally {
        this.loading = false
      }
    },

    reuseQuery(item) {
      this.applyNo = item.applyNo
      this.currentResult = item
    },

    clearHistory() {
      this.historyList = []
      this.currentResult = null
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.apply-status-page {
  min-height: 100vh;
  background: #f4f6fb;
}

.apply-status-page__nav {
  display: flex;
  align-items: center;
  height: 100rpx;
  padding: 0 24rpx;
  background: linear-gradient(180deg, #25328f 0%, #29379b 100%);
}

.apply-status-page__back,
.apply-status-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.apply-status-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
  border-radius: 16rpx;
  background: rgba(255, 255, 255, 0.12);
  font-size: 30rpx;
  color: $jst-bg-card;
}

.apply-status-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 32rpx;
  font-weight: 600;
  color: $jst-bg-card;
}

.apply-status-page__hero {
  padding: 24rpx;
}

.apply-status-page__hero-title {
  display: block;
  font-size: 34rpx;
  font-weight: 600;
  color: #2c3448;
}

.apply-status-page__hero-subtitle {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #7c879c;
}

.apply-status-page__body {
  padding: 0 24rpx 40rpx;
}

.apply-status-page__panel,
.apply-status-page__result,
.apply-status-page__history {
  margin-bottom: 20rpx;
  padding: 28rpx;
  border-radius: 20rpx;
  background: $jst-bg-card;
  box-shadow: 0 10rpx 24rpx rgba(35, 52, 116, 0.06);
}

.apply-status-page__label,
.apply-status-page__result-title,
.apply-status-page__history-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #2c3448;
}

.apply-status-page__input {
  width: 100%;
  margin-top: 18rpx;
  padding: 22rpx 24rpx;
  border: 2rpx solid #e7ebf3;
  border-radius: 20rpx;
  background: #fbfcff;
  font-size: 26rpx;
  color: #2c3448;
}

.apply-status-page__placeholder {
  color: #a3acbc;
}

.apply-status-page__query {
  width: 100%;
  height: 88rpx;
  margin-top: 20rpx;
  border-radius: 20rpx;
  background: linear-gradient(135deg, #25328f 0%, #3553c4 100%);
  font-size: 28rpx;
  font-weight: 600;
  color: $jst-bg-card;
}

.apply-status-page__query[disabled] {
  background: $jst-border;
  color: $jst-text-secondary;
}

.apply-status-page__result-head,
.apply-status-page__history-head,
.apply-status-page__history-item,
.apply-status-page__info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.apply-status-page__result-no {
  font-size: 22rpx;
  color: #7c879c;
}

.apply-status-page__status-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-top: 18rpx;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 600;
}

.apply-status-page__status-chip--draft,
.apply-status-page__status-chip--pending {
  background: #fff4d6;
  color: #d5961f;
}

.apply-status-page__status-chip--approved {
  background: #e9f9ef;
  color: #2bbb63;
}

.apply-status-page__status-chip--rejected {
  background: #fff0ee;
  color: #ed6d59;
}

.apply-status-page__status-chip--supplement {
  background: #eff3ff;
  color: #3553c4;
}

.apply-status-page__info-row {
  padding: 22rpx 0;
  border-bottom: 2rpx solid #eef1f6;
}

.apply-status-page__info-key {
  font-size: 22rpx;
  color: #8d97aa;
}

.apply-status-page__info-value {
  flex: 1;
  text-align: right;
  font-size: 24rpx;
  color: #2c3448;
}

.apply-status-page__remark {
  margin-top: 22rpx;
  padding: 22rpx;
  border-radius: 20rpx;
  background: #f7f9fd;
}

.apply-status-page__remark-label {
  display: block;
  font-size: 22rpx;
  font-weight: 600;
  color: #4f5d78;
}

.apply-status-page__remark-text {
  display: block;
  margin-top: 12rpx;
  font-size: 22rpx;
  line-height: 1.7;
  color: #677489;
}

.apply-status-page__history-clear {
  font-size: 22rpx;
  color: #3553c4;
}

.apply-status-page__history-list {
  margin-top: 18rpx;
}

.apply-status-page__history-item {
  padding: 20rpx 0;
  border-bottom: 2rpx solid #eef1f6;
}

.apply-status-page__history-item:last-child {
  border-bottom: 0;
}

.apply-status-page__history-no {
  display: block;
  font-size: 24rpx;
  font-weight: 600;
  color: #2c3448;
}

.apply-status-page__history-time {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  color: #8d97aa;
}

.apply-status-page__history-status {
  font-size: 22rpx;
  font-weight: 600;
  color: #3553c4;
}
</style>
