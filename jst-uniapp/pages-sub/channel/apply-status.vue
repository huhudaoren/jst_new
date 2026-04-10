<!-- 中文注释: 渠道认证 · 状态查询
     调用接口: GET /jst/wx/channel/auth/my
               POST /jst/wx/channel/auth/cancel/{id} -->
<template>
  <view class="as-page">
    <view v-if="apply" class="as-hero" :class="'as-hero--' + apply.applyStatus">
      <text class="as-hero__icon">{{ statusIcon }}</text>
      <text class="as-hero__title">{{ statusTitle }}</text>
      <text v-if="apply.applyStatus === 'rejected'" class="as-hero__reason">驳回原因：{{ apply.auditRemark || '未说明' }}</text>
      <text v-if="isLocked" class="as-hero__reason">该申请已被锁定，请联系客服处理</text>
    </view>

    <view v-if="apply" class="as-section">
      <text class="as-section__title">申请信息</text>
      <view class="as-kv"><text class="as-kv__k">申请编号</text><text class="as-kv__v">{{ apply.applyNo || '--' }}</text></view>
      <view class="as-kv"><text class="as-kv__k">认证类型</text><text class="as-kv__v">{{ typeLabel }}</text></view>
      <view class="as-kv"><text class="as-kv__k">申请名称</text><text class="as-kv__v">{{ apply.applyName || '--' }}</text></view>
      <view class="as-kv"><text class="as-kv__k">提交时间</text><text class="as-kv__v">{{ formatTime(apply.submitTime) }}</text></view>
      <view class="as-kv"><text class="as-kv__k">审核时间</text><text class="as-kv__v">{{ formatTime(apply.auditTime) }}</text></view>
      <view class="as-kv"><text class="as-kv__k">驳回次数</text><text class="as-kv__v">{{ apply.rejectCount || 0 }} / 3</text></view>
    </view>

    <view v-if="apply" class="as-actions">
      <button v-if="apply.applyStatus === 'pending'" class="as-actions__btn as-actions__btn--outline" @tap="onCancel">撤回申请</button>
      <button v-if="apply.applyStatus === 'rejected' && !isLocked" class="as-actions__btn" @tap="onResubmit">修改重新提交</button>
      <button v-if="isLocked" class="as-actions__btn as-actions__btn--disabled" disabled>请联系客服</button>
      <button v-if="apply.applyStatus === 'approved'" class="as-actions__btn" @tap="goChannelHome">进入渠道工作台</button>
    </view>

    <view v-if="!apply && !loading" class="as-empty">暂无认证申请记录</view>
  </view>
</template>

<script>
import { getMyChannelApply, cancelChannelApply } from '@/api/channel'

const TYPE_LABEL = { teacher: '老师', organization: '机构', individual: '个人' }
const STATUS_ICON = { pending: '⏳', approved: '✅', rejected: '❌', locked_for_manual: '🔒', cancelled: '🚫' }
const STATUS_TITLE = { pending: '审核中', approved: '认证通过', rejected: '认证被驳回', locked_for_manual: '已锁定', cancelled: '已撤回' }

export default {
  data() { return { apply: null, loading: false } },
  computed: {
    typeLabel() { return (this.apply && TYPE_LABEL[this.apply.channelType]) || '--' },
    statusIcon() { return (this.apply && STATUS_ICON[this.apply.applyStatus]) || '❓' },
    statusTitle() { return (this.apply && STATUS_TITLE[this.apply.applyStatus]) || '未知状态' },
    isLocked() {
      if (!this.apply) return false
      return this.apply.lockedForManual === 1 || this.apply.applyStatus === 'locked_for_manual' || (this.apply.rejectCount >= 3 && this.apply.applyStatus === 'rejected')
    }
  },
  onShow() { this.load() },
  onPullDownRefresh() { this.load().finally(() => uni.stopPullDownRefresh()) },
  methods: {
    async load() {
      this.loading = true
      try { this.apply = (await getMyChannelApply()) || null } catch (e) { this.apply = null }
      finally { this.loading = false }
    },
    onCancel() {
      uni.showModal({
        title: '撤回申请', content: '确认撤回当前认证申请？',
        success: async (res) => {
          if (!res.confirm || !this.apply) return
          try { await cancelChannelApply(this.apply.applyId); uni.showToast({ title: '已撤回', icon: 'success' }); this.load() } catch (e) {}
        }
      })
    },
    onResubmit() {
      if (!this.apply) return
      uni.navigateTo({ url: `/pages-sub/channel/apply-form?channelType=${this.apply.channelType}&resubmitId=${this.apply.applyId}` })
    },
    goChannelHome() { uni.navigateTo({ url: '/pages-sub/channel/home' }) },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) }
  }
}
</script>

<style scoped lang="scss">
.as-page { min-height: 100vh; background: #F7F8FA; padding-bottom: 48rpx; }
.as-hero { padding: 96rpx 32rpx 64rpx; text-align: center; color: #fff; border-bottom-left-radius: 40rpx; border-bottom-right-radius: 40rpx; }
.as-hero--pending { background: linear-gradient(135deg, #F39C12, #F5A623); }
.as-hero--approved { background: linear-gradient(135deg, #1B5E20, #27AE60); }
.as-hero--rejected, .as-hero--locked_for_manual { background: linear-gradient(135deg, #B71C1C, #E53935); }
.as-hero--cancelled { background: linear-gradient(135deg, #37474F, #607D8B); }
.as-hero__icon { display: block; font-size: 96rpx; }
.as-hero__title { display: block; margin-top: 16rpx; font-size: 36rpx; font-weight: 800; }
.as-hero__reason { display: block; margin-top: 16rpx; font-size: 24rpx; color: var(--jst-color-white-76); padding: 16rpx 24rpx; background: var(--jst-color-white-18); border-radius: var(--jst-radius-md); }

.as-section { margin: 32rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: 0 2rpx 8rpx rgba(20,30,60,0.04); }
.as-section__title { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 16rpx; }
.as-kv { display: flex; justify-content: space-between; padding: 14rpx 0; font-size: 26rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.as-kv:last-child { border-bottom: none; }
.as-kv__k { color: var(--jst-color-text-tertiary); }
.as-kv__v { color: var(--jst-color-text); }

.as-actions { margin: 32rpx 32rpx 0; display: flex; flex-direction: column; gap: 20rpx; }
.as-actions__btn { height: 96rpx; line-height: 96rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, var(--jst-color-brand), var(--jst-color-brand-light)); color: #fff; font-size: 30rpx; font-weight: 800; border: none; }
.as-actions__btn--outline { background: #fff; color: var(--jst-color-brand); border: 2rpx solid var(--jst-color-brand); }
.as-actions__btn--disabled { background: var(--jst-color-border); color: var(--jst-color-text-tertiary); }
.as-empty { padding: 120rpx; text-align: center; font-size: 28rpx; color: var(--jst-color-text-tertiary); }
</style>
