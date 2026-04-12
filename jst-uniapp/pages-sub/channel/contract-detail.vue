<!-- 中文注释: 渠道方 · 合同详情
     调用接口: GET /jst/wx/contract/{contractId} -->
<template>
  <view class="cd-page">
    <!-- 导航 -->
    <view class="cd-header" :style="{ paddingTop: navPaddingTop }">
      <view class="cd-header__back" @tap="goBack">←</view>
      <text class="cd-header__title">合同详情</text>
    </view>

    <!-- 加载态 -->
    <view v-if="loading" class="cd-loading">
      <u-loading-icon />
    </view>

    <template v-if="!loading && detail">
      <!-- 状态卡 -->
      <view class="cd-status-card">
        <u-tag
          :text="statusLabel(detail.status)"
          :type="statusType(detail.status)"
          size="large"
          shape="circle"
        />
        <text class="cd-status-card__no">{{ detail.contractNo || '--' }}</text>
      </view>

      <!-- 信息区块 -->
      <view class="cd-info">
        <view class="cd-info__row">
          <text class="cd-info__label">合同类型</text>
          <text class="cd-info__value">{{ typeLabel(detail.contractType) }}</text>
        </view>
        <view class="cd-info__row">
          <text class="cd-info__label">生效时间</text>
          <text class="cd-info__value">{{ formatTime(detail.effectiveDate) }}</text>
        </view>
        <view class="cd-info__row">
          <text class="cd-info__label">签署时间</text>
          <text class="cd-info__value">{{ formatTime(detail.signTime) }}</text>
        </view>
        <view v-if="detail.expireDate" class="cd-info__row">
          <text class="cd-info__label">到期时间</text>
          <text class="cd-info__value">{{ formatTime(detail.expireDate) }}</text>
        </view>
        <view v-if="detail.remark" class="cd-info__row">
          <text class="cd-info__label">备注</text>
          <text class="cd-info__value">{{ detail.remark }}</text>
        </view>
      </view>

      <!-- 文件预览 -->
      <view v-if="detail.fileUrl" class="cd-file">
        <u-button type="primary" text="查看合同文件" @click="openFile" />
      </view>

      <!-- 待签署提示 -->
      <view v-if="detail.status === 'pending_sign'" class="cd-tip">
        <text class="cd-tip__text">如需签署，请联系管理员处理</text>
      </view>
    </template>

    <!-- 空态 -->
    <u-empty v-if="!loading && !detail" mode="data" text="合同不存在" />
  </view>
</template>

<script>
import { getContractDetail } from '@/api/finance'

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
    return { loading: false, detail: null }
  },
  onLoad(options) {
    if (options.id) this.loadDetail(options.id)
  },
  methods: {
    async loadDetail(id) {
      this.loading = true
      try {
        this.detail = await getContractDetail(id)
      } finally { this.loading = false }
    },
    goBack() { uni.navigateBack() },
    statusLabel(s) { return (STATUS_MAP[s] && STATUS_MAP[s].label) || s || '--' },
    statusType(s) { return (STATUS_MAP[s] && STATUS_MAP[s].type) || 'info' },
    typeLabel(t) { return TYPE_MAP[t] || t || '--' },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 10) },
    // 打开合同文件（PDF 或其他）
    openFile() {
      const url = this.detail.fileUrl
      if (!url) return
      uni.showLoading({ title: '加载中...' })
      uni.downloadFile({
        url,
        success: (res) => {
          if (res.statusCode === 200) {
            uni.openDocument({
              filePath: res.tempFilePath,
              showMenu: true,
              fail: () => { uni.showToast({ title: '无法打开文件', icon: 'none' }) }
            })
          }
        },
        fail: () => { uni.showToast({ title: '下载失败', icon: 'none' }) },
        complete: () => { uni.hideLoading() }
      })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.cd-page { min-height: 100vh; background: $jst-bg-page; padding-bottom: calc(60rpx + env(safe-area-inset-bottom)); }

.cd-header { display: flex; align-items: center; padding: $jst-space-lg $jst-page-padding $jst-space-md; background: $jst-bg-card; }
.cd-header__back { width: 60rpx; font-size: $jst-font-xl; color: $jst-text-primary; }
.cd-header__title { font-size: $jst-font-lg; font-weight: $jst-weight-semibold; color: $jst-text-primary; }

.cd-loading { display: flex; justify-content: center; padding: 100rpx 0; }

.cd-status-card { margin: $jst-space-lg $jst-page-padding; padding: $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; display: flex; flex-direction: column; align-items: center; gap: $jst-space-md; }
.cd-status-card__no { font-size: $jst-font-sm; color: $jst-text-secondary; }

.cd-info { margin: 0 $jst-page-padding; padding: $jst-space-lg $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.cd-info__row { display: flex; justify-content: space-between; align-items: flex-start; padding: $jst-space-md 0; border-bottom: 2rpx solid $jst-border; }
.cd-info__row:last-child { border-bottom: none; }
.cd-info__label { font-size: $jst-font-base; color: $jst-text-secondary; flex-shrink: 0; width: 160rpx; }
.cd-info__value { font-size: $jst-font-base; color: $jst-text-primary; text-align: right; flex: 1; }

.cd-file { margin: $jst-space-lg $jst-page-padding; }

.cd-tip { margin: $jst-space-lg $jst-page-padding; padding: $jst-space-lg; background: $jst-warning-light; border-radius: $jst-radius-md; text-align: center; }
.cd-tip__text { font-size: $jst-font-sm; color: $jst-warning; }
</style>
