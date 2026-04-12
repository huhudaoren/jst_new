<!-- 中文注释: 个人预约 · 申请页
     对应原型: 小程序原型图/reserve.png
     调用接口: GET /jst/wx/appointment/contest/{id}/remaining
               POST /jst/wx/appointment/apply -->
<template>
  <view class="ap-page">
    <view class="ap-hero" :style="{ paddingTop: navPaddingTop }">
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
      <u-button class="ap-footer__btn" type="primary" :loading="submitting" :disabled="submitting || !canSubmit" shape="circle" @click="onSubmit">
        提交预约
      </u-button>
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
      skeletonShow: true, // [visual-effect]
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
@import '@/styles/design-tokens.scss';

.ap-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }
.ap-hero { padding: 72rpx 32rpx 56rpx; background: linear-gradient(135deg, $jst-brand, $jst-brand-light); color: $jst-text-inverse; }
.ap-hero__label { display: block; font-size: 24rpx; color: rgba(255, 255, 255, 0.76); }
.ap-hero__title { display: block; margin-top: 12rpx; font-size: 40rpx; font-weight: 600; }
.ap-section { margin: 24rpx 32rpx 0; padding: 16rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.ap-field { display: flex; align-items: center; padding: 28rpx 0; border-bottom: 2rpx solid $jst-border; }
.ap-field:last-child { border-bottom: none; }
.ap-field__label { width: 180rpx; font-size: 26rpx; color: $jst-text-regular; }
.ap-field__value { flex: 1; font-size: 28rpx; color: $jst-text-primary; }
.ap-remaining { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; display: flex; align-items: baseline; gap: 12rpx; }
.ap-remaining__label { font-size: 24rpx; color: $jst-text-secondary; }
.ap-remaining__num { font-size: 48rpx; font-weight: 600; color: $jst-success; }
.ap-remaining__num--danger { color: $jst-danger; }
.ap-remaining__total { font-size: 24rpx; color: $jst-text-secondary; }
.ap-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: $jst-bg-card; box-shadow: $jst-shadow-sm; }
::v-deep .ap-footer__btn.u-button {
  height: 96rpx;
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  background: linear-gradient(135deg, $jst-brand, $jst-brand-dark);
  border: none;
}
</style>
