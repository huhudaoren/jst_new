<!-- 中文注释: 权益详情
     对应原型: 小程序原型图/rights-detail.html
     调用接口: GET /jst/wx/rights/{id} -->
<template>
  <view class="rd-page">
    <view class="rd-hero">
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
      <view v-if="!(detail.writeoffHistory || []).length" class="rd-empty">暂无核销记录</view>
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
.rd-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }
.rd-hero { padding: 72rpx 32rpx 56rpx; background: linear-gradient(135deg, #1B5E20, #2E7D32); color: #fff; }
.rd-hero__name { display: block; font-size: 40rpx; font-weight: 800; }
.rd-hero__status { display: inline-block; margin-top: 12rpx; padding: 6rpx 20rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-white-18); font-size: 22rpx; font-weight: 700; }
.rd-hero__meta { display: flex; margin-top: 32rpx; padding: 24rpx; background: var(--jst-color-white-18); border-radius: var(--jst-radius-md); }
.rd-hero__cell { flex: 1; text-align: center; }
.rd-hero__num { display: block; font-size: 40rpx; font-weight: 800; color: #FFD54F; }
.rd-hero__lbl { display: block; margin-top: 6rpx; font-size: 22rpx; color: var(--jst-color-white-76); }

.rd-section { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.rd-section__title { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 16rpx; }
.rd-section__desc { font-size: 24rpx; line-height: 1.7; color: var(--jst-color-text-secondary); white-space: pre-wrap; }
.rd-kv { display: flex; padding: 10rpx 0; font-size: 26rpx; }
.rd-kv__k { width: 120rpx; color: var(--jst-color-text-tertiary); }
.rd-kv__v { flex: 1; color: var(--jst-color-text); }

.rd-timeline__item { display: flex; gap: 20rpx; padding: 16rpx 0; position: relative; }
.rd-timeline__item + .rd-timeline__item::before { content: ''; position: absolute; left: 14rpx; top: -16rpx; width: 2rpx; height: 32rpx; background: var(--jst-color-border); }
.rd-timeline__dot { width: 28rpx; height: 28rpx; border-radius: 50%; background: #1B5E20; margin-top: 8rpx; }
.rd-timeline__body { flex: 1; }
.rd-timeline__title { display: block; font-size: 26rpx; font-weight: 600; color: var(--jst-color-text); }
.rd-timeline__sub { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.rd-empty { padding: 40rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }

.rd-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: var(--jst-color-card-bg); box-shadow: 0 -8rpx 24rpx rgba(16,88,160,0.08); }
.rd-footer__btn { height: 88rpx; line-height: 88rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, #1B5E20, #2E7D32); color: #fff; font-size: 30rpx; font-weight: 800; border: none; }
</style>
