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

      <!-- 签署流程时间轴 -->
      <view class="cd-section">
        <view class="cd-section__title"><text>签署流程</text></view>
        <view class="cd-timeline">
          <view
            v-for="(node, idx) in timelineNodes"
            :key="idx"
            :class="['cd-timeline__item', `cd-timeline__item--${node.state}`]"
          >
            <view class="cd-timeline__rail">
              <view :class="['cd-timeline__dot', `cd-timeline__dot--${node.state}`]">
                <text v-if="node.state === 'done'" class="cd-timeline__check">✓</text>
                <text v-else-if="node.state === 'reject'" class="cd-timeline__check">✕</text>
              </view>
              <view v-if="idx < timelineNodes.length - 1" :class="['cd-timeline__line', `cd-timeline__line--${node.state}`]"></view>
            </view>
            <view class="cd-timeline__content">
              <text :class="['cd-timeline__label', `cd-timeline__label--${node.state}`]">{{ node.label }}</text>
              <text v-if="node.time" class="cd-timeline__time">{{ formatDateTime(node.time) }}</text>
              <text v-if="node.desc" class="cd-timeline__desc">{{ node.desc }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 信息区块 -->
      <view class="cd-info">
        <view class="cd-info__row">
          <text class="cd-info__label">合同类型</text>
          <text class="cd-info__value">{{ typeLabel(detail.contractType) }}</text>
        </view>
        <!-- 签约方（多方展示） -->
        <view v-if="detail.partyA || detail.partyAName" class="cd-info__row">
          <text class="cd-info__label">甲方</text>
          <text class="cd-info__value">{{ detail.partyAName || detail.partyA || '--' }}</text>
        </view>
        <view v-if="detail.partyB || detail.partyBName" class="cd-info__row">
          <text class="cd-info__label">乙方</text>
          <text class="cd-info__value">{{ detail.partyBName || detail.partyB || '--' }}</text>
        </view>
        <view class="cd-info__row">
          <text class="cd-info__label">生效时间</text>
          <text class="cd-info__value">{{ formatTime(detail.effectiveDate) }}</text>
        </view>
        <view class="cd-info__row">
          <text class="cd-info__label">签署时间</text>
          <text class="cd-info__value">{{ formatDateTime(detail.signTime) }}</text>
        </view>
        <view v-if="detail.expireDate" class="cd-info__row">
          <text class="cd-info__label">到期时间</text>
          <text class="cd-info__value">{{ formatTime(detail.expireDate) }}</text>
        </view>
        <view v-if="detail.settlementId || detail.settlementNo" class="cd-info__row cd-info__row--link" @tap="goSettlement">
          <text class="cd-info__label">关联结算单</text>
          <view class="cd-info__link">
            <text class="cd-info__value cd-info__value--link">{{ detail.settlementNo || '查看详情' }}</text>
            <text class="cd-info__arrow">›</text>
          </view>
        </view>
        <view v-if="detail.remark" class="cd-info__row">
          <text class="cd-info__label">备注</text>
          <text class="cd-info__value">{{ detail.remark }}</text>
        </view>
      </view>

      <!-- 文件预览 -->
      <view v-if="detail.fileUrl" class="cd-file">
        <u-button type="primary" text="查看/下载合同 PDF" @click="openFile" />
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
  computed: {
    // 签署流程时间轴节点：拟定 → 已签署 → 已归档（或 已到期/已失效）
    timelineNodes() {
      const d = this.detail || {}
      const s = d.status
      const n1 = { label: '合同拟定', state: 'done', time: d.createTime, desc: '合同已生成' }
      let n2State = 'pending'
      let n2Label = '等待签署'
      let n2Desc = ''
      let n2Time = null
      if (s === 'pending_sign') { n2State = 'current'; n2Desc = '请尽快完成签署' }
      else if (['signed', 'archived', 'expired'].includes(s)) {
        n2State = 'done'
        n2Label = '已签署'
        n2Time = d.signTime
        n2Desc = d.effectiveDate ? ('生效日 ' + String(d.effectiveDate).slice(0, 10)) : ''
      } else if (s === 'draft') {
        n2State = 'pending'
        n2Label = '待签署'
      }
      let n3State = 'pending'
      let n3Label = '归档'
      let n3Desc = ''
      let n3Time = null
      if (s === 'archived') { n3State = 'done'; n3Label = '已归档'; n3Time = d.updateTime }
      else if (s === 'expired') { n3State = 'reject'; n3Label = '已到期'; n3Time = d.expireDate; n3Desc = '合同已失效' }
      else if (s === 'signed') { n3State = 'current'; n3Label = '履约中'; n3Desc = '合同在有效期内' }
      return [n1, { ...{ label: n2Label, state: n2State, time: n2Time, desc: n2Desc } }, { label: n3Label, state: n3State, time: n3Time, desc: n3Desc }]
    }
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
    goSettlement() {
      if (this.detail && this.detail.settlementId) {
        uni.navigateTo({ url: '/pages-sub/channel/withdraw-detail?id=' + this.detail.settlementId })
      }
    },
    statusLabel(s) { return (STATUS_MAP[s] && STATUS_MAP[s].label) || s || '--' },
    statusType(s) { return (STATUS_MAP[s] && STATUS_MAP[s].type) || 'info' },
    typeLabel(t) { return TYPE_MAP[t] || t || '--' },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 10) },
    formatDateTime(v) { if (!v) return '--'; const s = String(v).replace('T', ' '); return s.length >= 16 ? s.slice(0, 16) : s },
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

.cd-status-card { margin: $jst-space-lg $jst-page-padding; padding: $jst-space-xl; background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light); border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; display: flex; flex-direction: column; align-items: center; gap: $jst-space-md; }
.cd-status-card__no { font-size: $jst-font-sm; color: rgba(255, 255, 255, 0.85); }

.cd-section { margin: $jst-space-lg $jst-page-padding 0; padding: $jst-space-lg $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.cd-section__title { font-size: $jst-font-md; font-weight: 600; color: $jst-text-primary; margin-bottom: $jst-space-md; }

/* 签署流程时间轴 */
.cd-timeline { padding: 8rpx 0; }
.cd-timeline__item { display: flex; gap: 20rpx; min-height: 108rpx; }
.cd-timeline__rail { display: flex; flex-direction: column; align-items: center; width: 40rpx; flex-shrink: 0; }
.cd-timeline__dot { width: 28rpx; height: 28rpx; border-radius: 50%; background: $jst-text-placeholder; display: flex; align-items: center; justify-content: center; flex-shrink: 0; margin-top: 4rpx; }
.cd-timeline__dot--done { background: $jst-success; }
.cd-timeline__dot--current { background: $jst-warning; box-shadow: 0 0 0 6rpx rgba(255, 149, 0, 0.18); }
.cd-timeline__dot--reject { background: $jst-danger; }
.cd-timeline__check { font-size: 18rpx; color: $jst-text-inverse; line-height: 1; font-weight: 700; }
.cd-timeline__line { flex: 1; width: 3rpx; min-height: 56rpx; background: $jst-border; margin: 4rpx 0; }
.cd-timeline__line--done { background: $jst-success; }
.cd-timeline__content { flex: 1; padding-bottom: 20rpx; }
.cd-timeline__label { display: block; font-size: 26rpx; font-weight: 600; color: $jst-text-primary; line-height: 1.4; }
.cd-timeline__label--current { color: $jst-warning; }
.cd-timeline__label--reject { color: $jst-danger; }
.cd-timeline__label--pending { color: $jst-text-placeholder; font-weight: 400; }
.cd-timeline__time { display: block; margin-top: 4rpx; font-size: 22rpx; color: $jst-text-secondary; }
.cd-timeline__desc { display: block; margin-top: 6rpx; font-size: 22rpx; color: $jst-text-secondary; line-height: 1.5; }

.cd-info { margin: $jst-space-lg $jst-page-padding 0; padding: $jst-space-lg $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.cd-info__row { display: flex; justify-content: space-between; align-items: flex-start; padding: $jst-space-md 0; border-bottom: 2rpx solid $jst-border; }
.cd-info__row:last-child { border-bottom: none; }
.cd-info__row--link { cursor: pointer; }
.cd-info__label { font-size: $jst-font-base; color: $jst-text-secondary; flex-shrink: 0; width: 160rpx; }
.cd-info__value { font-size: $jst-font-base; color: $jst-text-primary; text-align: right; flex: 1; }
.cd-info__value--link { color: $jst-indigo; font-weight: 600; }
.cd-info__link { display: flex; align-items: center; gap: 4rpx; }
.cd-info__arrow { font-size: $jst-font-md; color: $jst-indigo; }

.cd-file { margin: $jst-space-lg $jst-page-padding; }

.cd-tip { margin: $jst-space-lg $jst-page-padding; padding: $jst-space-lg; background: $jst-warning-light; border-radius: $jst-radius-md; text-align: center; }
.cd-tip__text { font-size: $jst-font-sm; color: $jst-warning; }
</style>
