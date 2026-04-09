<!-- 中文注释: 个人预约 · 申请页
     对应原型: 小程序原型图/reserve.png
     调用接口: GET /jst/wx/appointment/contest/{id}/remaining
               POST /jst/wx/appointment/apply -->
<template>
  <view class="ap-page">
    <view class="ap-hero">
      <text class="ap-hero__label">我要预约</text>
      <text class="ap-hero__title">{{ contestName || '赛事预约' }}</text>
    </view>

    <view class="ap-section">
      <view class="ap-field">
        <text class="ap-field__label">预约日期</text>
        <picker mode="date" :value="form.appointmentDate" @change="onDateChange">
          <view class="ap-field__value">{{ form.appointmentDate || '请选择日期' }}</view>
        </picker>
      </view>
      <view class="ap-field">
        <text class="ap-field__label">场次</text>
        <picker mode="selector" :range="sessionList" range-key="label" @change="onSessionChange">
          <view class="ap-field__value">{{ currentSessionLabel || '请选择场次' }}</view>
        </picker>
      </view>
      <view class="ap-field">
        <text class="ap-field__label">参赛档案</text>
        <view class="ap-field__value" @tap="navigateParticipant">{{ participantName || '请选择参赛档案 >' }}</view>
      </view>
    </view>

    <view v-if="remaining" class="ap-remaining">
      <text class="ap-remaining__label">当前剩余名额</text>
      <text :class="['ap-remaining__num', remaining.remainingCount <= 0 && 'ap-remaining__num--danger']">
        {{ remaining.remainingCount != null ? remaining.remainingCount : '--' }}
      </text>
      <text class="ap-remaining__total">/ {{ remaining.totalCapacity || '--' }}</text>
    </view>

    <view class="ap-footer">
      <button class="ap-footer__btn" :disabled="submitting || !canSubmit" @tap="onSubmit">
        {{ submitting ? '提交中...' : '提交预约' }}
      </button>
    </view>
  </view>
</template>

<script>
import { applyAppointment, getRemaining } from '@/api/appointment'

const SESSIONS = [
  { value: 'AM', label: '上午场 (AM)' },
  { value: 'PM', label: '下午场 (PM)' },
  { value: 'EVE', label: '晚场 (EVE)' }
]

export default {
  data() {
    return {
      contestId: null,
      contestName: '',
      sessionList: SESSIONS,
      form: { appointmentDate: '', sessionCode: '', participantId: null },
      participantName: '',
      remaining: null,
      submitting: false
    }
  },
  computed: {
    currentSessionLabel() {
      const s = SESSIONS.find((i) => i.value === this.form.sessionCode)
      return s ? s.label : ''
    },
    canSubmit() {
      return !!(this.form.appointmentDate && this.form.sessionCode && this.contestId)
    }
  },
  onLoad(query) {
    this.contestId = query && query.contestId ? Number(query.contestId) : null
    this.contestName = (query && query.contestName) || ''
    // 从 participant 选择页返回可通过 eventChannel 或 store 回填, 此处简化: 监听 onShow 读取全局
  },
  onShow() {
    const picked = uni.getStorageSync('ap_picked_participant')
    if (picked && picked.id) {
      this.form.participantId = picked.id
      this.participantName = picked.name || ''
      uni.removeStorageSync('ap_picked_participant')
    }
  },
  methods: {
    onDateChange(e) { this.form.appointmentDate = e.detail.value; this.tryLoadRemaining() },
    onSessionChange(e) { this.form.sessionCode = SESSIONS[e.detail.value].value; this.tryLoadRemaining() },
    navigateParticipant() { uni.navigateTo({ url: '/pages-sub/my/participant?mode=pick&back=/pages-sub/appointment/apply' }) },
    async tryLoadRemaining() {
      if (!this.form.appointmentDate || !this.form.sessionCode || !this.contestId) return
      try {
        this.remaining = await getRemaining(this.contestId, {
          appointmentDate: this.form.appointmentDate,
          sessionCode: this.form.sessionCode
        })
      } catch (e) { this.remaining = null }
    },
    async onSubmit() {
      if (!this.canSubmit) return
      this.submitting = true
      try {
        const body = {
          contestId: this.contestId,
          sessionCode: this.form.sessionCode,
          appointmentDate: this.form.appointmentDate
        }
        if (this.form.participantId) body.participantId = this.form.participantId
        const res = await applyAppointment(body)
        if (res && res.orderId) {
          // 付费单: 跳订单详情走 C2 支付
          uni.showToast({ title: '预约创建，去支付', icon: 'none' })
          setTimeout(() => {
            uni.redirectTo({ url: '/pages-sub/my/order-detail?id=' + res.orderId })
          }, 600)
        } else {
          uni.showToast({ title: '预约成功', icon: 'success' })
          setTimeout(() => {
            uni.redirectTo({ url: '/pages-sub/appointment/detail?id=' + res.appointmentId })
          }, 600)
        }
      } catch (e) {
        if (e && e.code === 30092) uni.showToast({ title: '名额已满', icon: 'none' })
        else if (e && e.code === 30093) uni.showToast({ title: '重复预约', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
.ap-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }
.ap-hero { padding: 72rpx 32rpx 56rpx; background: linear-gradient(135deg, var(--jst-color-brand), var(--jst-color-brand-light)); color: #fff; }
.ap-hero__label { display: block; font-size: 24rpx; color: var(--jst-color-white-76); }
.ap-hero__title { display: block; margin-top: 12rpx; font-size: 40rpx; font-weight: 800; }
.ap-section { margin: 24rpx 32rpx 0; padding: 16rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.ap-field { display: flex; align-items: center; padding: 28rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.ap-field:last-child { border-bottom: none; }
.ap-field__label { width: 180rpx; font-size: 26rpx; color: var(--jst-color-text-secondary); }
.ap-field__value { flex: 1; font-size: 28rpx; color: var(--jst-color-text); }
.ap-remaining { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); display: flex; align-items: baseline; gap: 12rpx; }
.ap-remaining__label { font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.ap-remaining__num { font-size: 48rpx; font-weight: 800; color: var(--jst-color-success); }
.ap-remaining__num--danger { color: var(--jst-color-danger); }
.ap-remaining__total { font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.ap-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: var(--jst-color-card-bg); box-shadow: 0 -8rpx 24rpx rgba(16,88,160,0.08); }
.ap-footer__btn { height: 96rpx; line-height: 96rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, var(--jst-color-brand), var(--jst-color-brand-light)); color: #fff; font-size: 30rpx; font-weight: 800; border: none; }
.ap-footer__btn[disabled] { opacity: 0.5; }
</style>
