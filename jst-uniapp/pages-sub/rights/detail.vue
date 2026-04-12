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
          <text class="rd-hero__num">{{ detail.quotaMode === 'amount' ? ('¥' + (detail.remainQuota != null ? detail.remainQuota : '--')) : (detail.remainQuota != null ? detail.remainQuota : '--') }}</text>
          <text class="rd-hero__lbl">{{ detail.quotaMode === 'amount' ? '剩余额度' : '剩余次数' }}</text>
        </view>
        <view class="rd-hero__cell">
          <text class="rd-hero__num">{{ detail.quotaMode === 'amount' ? ('¥' + (detail.quotaValue != null ? detail.quotaValue : '--')) : (detail.quotaValue != null ? detail.quotaValue : '--') }}</text>
          <text class="rd-hero__lbl">总额度</text>
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
      <text class="rd-section__title">核销历史</text>
      <u-empty v-if="!(detail.writeoffHistory || []).length" mode="list" text="暂无核销记录" />
      <view v-else class="rd-timeline">
        <view v-for="h in detail.writeoffHistory" :key="h.recordId" class="rd-timeline__item rd-timeline__item--done">
          <view class="rd-timeline__dot"></view>
          <view class="rd-timeline__body">
            <text class="rd-timeline__title">{{ h.applyRemark || ('核销单 ' + (h.writeoffNo || '')) }}</text>
            <text class="rd-timeline__sub">¥{{ h.useAmount != null ? h.useAmount : '0' }} · {{ h.status || '' }} · {{ formatTime(h.writeoffTime || h.createTime) }}</text>
          </view>
        </view>
      </view>
    </view>

    <view v-if="detail.status === 'available'" class="rd-footer">
      <button class="rd-footer__btn" @tap="goApply">申请核销</button>
    </view>
  </view>
</template>

<script>
import { getRightsDetail } from '@/api/rights'

const STATUS_LABEL = { available: '可用', applying: '申请中', used: '已使用', expired: '已过期' }

export default {
  data() { return { detail: {}, userRightsId: null } },
  onLoad(query) { this.userRightsId = query && query.id; if (this.userRightsId) this.load() },
  methods: {
    async load() { try { this.detail = (await getRightsDetail(this.userRightsId)) || {} } catch (e) {} },
    goApply() { uni.navigateTo({ url: '/pages-sub/rights/writeoff-apply?id=' + this.userRightsId }) },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' },
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
.rd-section__title { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; margin-bottom: $jst-space-md; }
.rd-section__desc { font-size: $jst-font-sm; line-height: 1.7; color: $jst-text-regular; white-space: pre-wrap; }
.rd-kv { display: flex; padding: 10rpx 0; font-size: $jst-font-sm; }
.rd-kv__k { width: 120rpx; color: $jst-text-secondary; }
.rd-kv__v { flex: 1; color: $jst-text-primary; }

.rd-timeline__item { display: flex; gap: 20rpx; padding: $jst-space-md 0; position: relative; }
.rd-timeline__item + .rd-timeline__item::before { content: ''; position: absolute; left: 14rpx; top: -$jst-space-md; width: 2rpx; height: $jst-space-xl; background: $jst-border; }
.rd-timeline__dot { width: 28rpx; height: 28rpx; border-radius: 50%; background: $jst-success; margin-top: $jst-space-xs; }
.rd-timeline__body { flex: 1; }
.rd-timeline__title { display: block; font-size: $jst-font-sm; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.rd-timeline__sub { display: block; margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }

.rd-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: $jst-space-lg $jst-space-xl calc(#{$jst-space-lg} + env(safe-area-inset-bottom)); background: $jst-bg-card; box-shadow: 0 -2rpx 8rpx rgba(20, 30, 60, 0.04); }
.rd-footer__btn { height: 88rpx; line-height: 88rpx; border-radius: $jst-radius-xl; background: linear-gradient(135deg, darken($jst-success, 15%), $jst-success); color: $jst-text-inverse; font-size: $jst-font-md; font-weight: $jst-weight-semibold; border: none; transition: opacity $jst-duration-fast $jst-easing; &:active { opacity: 0.85; } }
</style>
