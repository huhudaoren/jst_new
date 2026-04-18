<!-- 中文注释: 权益详情
     对应原型: 小程序原型图/rights-detail.html
     调用接口: GET /jst/wx/rights/{id} -->
<template>
  <view class="rd-page">
    <view class="rd-hero" :style="{ paddingTop: navPaddingTop }">
      <text class="rd-hero__name">{{ detail.rightsName || '--' }}</text>
      <text :class="['rd-hero__status', 'rd-hero__status--' + detail.status]">{{ statusLabel(detail.status) }}</text>
      <view class="rd-hero__meta">
        <view class="rd-hero__cell">
          <text class="rd-hero__num">{{ kpiTotalLabel }}</text>
          <text class="rd-hero__lbl">{{ detail.quotaMode === 'amount' ? '总额度' : '总次数' }}</text>
        </view>
        <view class="rd-hero__cell">
          <text class="rd-hero__num">{{ kpiUsedLabel }}</text>
          <text class="rd-hero__lbl">已使用</text>
        </view>
        <view class="rd-hero__cell">
          <text class="rd-hero__num">{{ kpiRemainLabel }}</text>
          <text class="rd-hero__lbl">{{ detail.quotaMode === 'amount' ? '剩余额度' : '剩余次数' }}</text>
        </view>
      </view>
    </view>

    <view class="rd-section">
      <text class="rd-section__title">核销规则</text>
      <text class="rd-section__desc">{{ detail.remark || ('核销模式: ' + (detail.writeoffMode || '--')) }}</text>
    </view>

    <view class="rd-section">
      <text class="rd-section__title">有效期</text>
      <view class="rd-kv"><text class="rd-kv__k">开始</text><text class="rd-kv__v">{{ formatDate(detail.validStart) }}</text></view>
      <view class="rd-kv"><text class="rd-kv__k">结束</text><text class="rd-kv__v">{{ formatDate(detail.validEnd) }}</text></view>
    </view>

    <view class="rd-section">
      <view class="rd-section__head">
        <text class="rd-section__title">核销历史</text>
        <text v-if="(detail.writeoffHistory || []).length > 3" class="rd-section__more" @tap="goAllRecords">查看全部 ›</text>
      </view>
      <u-empty v-if="!(detail.writeoffHistory || []).length" mode="list" text="暂无核销记录" />
      <view v-else class="rd-timeline">
        <view v-for="h in displayHistory" :key="h.recordId" :class="['rd-timeline__item', 'rd-timeline__item--' + (h.status || 'approved')]">
          <view :class="['rd-timeline__dot', 'rd-timeline__dot--' + (h.status || 'approved')]"></view>
          <view class="rd-timeline__body">
            <text class="rd-timeline__title">{{ h.applyRemark || ('核销单 ' + (h.writeoffNo || '')) }}</text>
            <text class="rd-timeline__sub">¥{{ h.useAmount != null ? h.useAmount : '0' }} · {{ statusLabel2(h.status) }} · {{ formatTime(h.writeoffTime || h.createTime) }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="rd-footer">
      <button class="rd-footer__btn" :disabled="!canApply" @tap="goApply">{{ ctaText }}</button>
    </view>
  </view>
</template>

<script>
import { getRightsDetail } from '@/api/rights'

const STATUS_LABEL = { available: '可用', applying: '申请中', used: '已使用', expired: '已过期' }
const RECORD_STATUS_LABEL = { pending: '申请中', approved: '已核销', rejected: '已驳回', rolled_back: '已回退' }

export default {
  data() { return { detail: {}, userRightsId: null } },
  computed: {
    // 3 KPI 计算: 总 / 已用 / 剩余
    kpiTotalLabel() {
      const v = this.detail.quotaValue
      if (v == null) return '--'
      return this.detail.quotaMode === 'amount' ? ('¥' + v) : String(v)
    },
    kpiRemainLabel() {
      const v = this.detail.remainQuota
      if (v == null) return '--'
      return this.detail.quotaMode === 'amount' ? ('¥' + v) : String(v)
    },
    kpiUsedLabel() {
      const total = Number(this.detail.quotaValue || 0)
      const remain = Number(this.detail.remainQuota || 0)
      const used = Math.max(0, total - remain)
      if (this.detail.quotaValue == null) return '--'
      return this.detail.quotaMode === 'amount' ? ('¥' + used.toFixed(2)) : String(used)
    },
    // 只展示前 5 条
    displayHistory() {
      const all = this.detail.writeoffHistory || []
      return all.slice(0, 5)
    },
    canApply() {
      if (this.detail.status !== 'available') return false
      if (this.detail.expired) return false
      const remain = Number(this.detail.remainQuota || 0)
      return remain > 0
    },
    ctaText() {
      if (this.detail.expired || this.detail.status === 'expired') return '权益已过期'
      if (this.detail.status === 'used') return '权益已用完'
      if (this.detail.status === 'applying') return '审核中，暂不可再申请'
      if (Number(this.detail.remainQuota || 0) <= 0) return '已用完，无可用额度'
      return '发起核销申请'
    }
  },
  onLoad(query) { this.userRightsId = query && query.id; if (this.userRightsId) this.load() },
  onShow() { if (this.userRightsId) this.load() },
  methods: {
    async load() { try { this.detail = (await getRightsDetail(this.userRightsId)) || {} } catch (e) {} },
    goApply() {
      if (!this.canApply) {
        uni.showToast({ title: this.ctaText, icon: 'none' })
        return
      }
      uni.navigateTo({ url: '/pages-sub/rights/writeoff-apply?id=' + this.userRightsId })
    },
    goAllRecords() {
      uni.navigateTo({ url: '/pages-sub/appointment/writeoff-record?rightsId=' + this.userRightsId })
    },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' },
    statusLabel2(s) { return RECORD_STATUS_LABEL[s] || s || '--' },
    formatDate(v) { if (!v) return '--'; return String(v).slice(0, 10) },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.rd-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }
.rd-hero { padding: 72rpx $jst-space-xl 56rpx; background: linear-gradient(135deg, darken($jst-success, 15%), $jst-success); color: $jst-text-inverse; }
.rd-hero__name { display: block; font-size: 40rpx; font-weight: $jst-weight-semibold; }
.rd-hero__status { display: inline-block; margin-top: $jst-space-sm; padding: 6rpx 20rpx; border-radius: $jst-radius-round; background: rgba(255,255,255,0.18); font-size: $jst-font-xs; font-weight: $jst-weight-semibold; }
.rd-hero__meta { display: flex; margin-top: $jst-space-xl; padding: $jst-space-lg; background: rgba(255,255,255,0.18); border-radius: $jst-radius-xl; }
.rd-hero__cell { flex: 1; text-align: center; }
.rd-hero__num { display: block; font-size: 40rpx; font-weight: $jst-weight-semibold; color: $jst-gold; }
.rd-hero__lbl { display: block; margin-top: 6rpx; font-size: $jst-font-xs; color: rgba(255,255,255,0.76); }

.rd-section { margin: $jst-space-lg $jst-space-xl 0; padding: $jst-space-lg $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; }
.rd-section__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: $jst-space-md; }
.rd-section__title { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; margin-bottom: $jst-space-md; }
.rd-section__head .rd-section__title { margin-bottom: 0; }
.rd-section__more { font-size: $jst-font-xs; color: $jst-success; padding: 8rpx 0; }
.rd-section__desc { font-size: $jst-font-sm; line-height: 1.7; color: $jst-text-regular; white-space: pre-wrap; }
.rd-kv { display: flex; padding: 10rpx 0; font-size: $jst-font-sm; }
.rd-kv__k { width: 120rpx; color: $jst-text-secondary; }
.rd-kv__v { flex: 1; color: $jst-text-primary; }

.rd-timeline__item { display: flex; gap: 20rpx; padding: $jst-space-md 0; position: relative; }
.rd-timeline__item + .rd-timeline__item::before { content: ''; position: absolute; left: 14rpx; top: -$jst-space-md; width: 2rpx; height: $jst-space-xl; background: $jst-border; }
.rd-timeline__dot { width: 28rpx; height: 28rpx; border-radius: 50%; background: $jst-success; margin-top: $jst-space-xs; }
.rd-timeline__dot--pending { background: $jst-warning; }
.rd-timeline__dot--rejected { background: #EB5252; }
.rd-timeline__dot--rolled_back { background: $jst-text-secondary; }
.rd-timeline__body { flex: 1; }
.rd-timeline__title { display: block; font-size: $jst-font-sm; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.rd-timeline__sub { display: block; margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }

.rd-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: $jst-space-lg $jst-space-xl calc(#{$jst-space-lg} + env(safe-area-inset-bottom)); background: $jst-bg-card; box-shadow: 0 -2rpx 8rpx rgba(20, 30, 60, 0.04); }
.rd-footer__btn { height: 88rpx; line-height: 88rpx; border-radius: $jst-radius-xl; background: linear-gradient(135deg, darken($jst-success, 15%), $jst-success); color: $jst-text-inverse; font-size: $jst-font-md; font-weight: $jst-weight-semibold; border: none; transition: opacity $jst-duration-fast $jst-easing; &:active { opacity: 0.85; } }
.rd-footer__btn[disabled] { opacity: 0.5; background: $jst-bg-grey; color: $jst-text-secondary; }
</style>
