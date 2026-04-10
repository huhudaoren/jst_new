<!-- 中文注释: 倒计时确认弹窗组件 (E1-CH-3, Q-01 决策)
     用途: 渠道方解绑学生时的5秒倒计时确认，防止误操作
     接口: props.visible / props.studentName / emit('confirm') / emit('cancel') -->
<template>
  <view v-if="visible" class="cdm-mask" @tap.stop="handleCancel">
    <view class="cdm-sheet" @tap.stop>
      <view class="cdm-handle"></view>

      <view class="cdm-header">
        <text class="cdm-title">确认解除与 {{ studentName }} 的绑定关系？</text>
      </view>

      <view class="cdm-body">
        <view class="cdm-warn-item">
          <text class="cdm-warn-icon">⚠️</text>
          <text class="cdm-warn-text">解绑后该学生可被其他渠道方绑定</text>
        </view>
        <view class="cdm-warn-item">
          <text class="cdm-warn-icon">⚠️</text>
          <text class="cdm-warn-text">已计提的历史返点不受影响</text>
        </view>
        <view class="cdm-warn-item">
          <text class="cdm-warn-icon">⚠️</text>
          <text class="cdm-warn-text">未计提的后续订单将不再归属您</text>
        </view>
      </view>

      <view class="cdm-footer">
        <view class="cdm-btn cdm-btn--cancel" @tap="handleCancel">取消</view>
        <view :class="['cdm-btn', countdown > 0 ? 'cdm-btn--disabled' : 'cdm-btn--danger']" @tap="handleConfirm">
          {{ countdown > 0 ? '确认解绑（' + countdown + '）' : '确认解绑' }}
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'jst-countdown-confirm',
  props: {
    visible: { type: Boolean, default: false },
    studentName: { type: String, default: '' }
  },
  emits: ['confirm', 'cancel'],
  data() {
    return { countdown: 5, timer: null }
  },
  watch: {
    // 弹窗打开时启动倒计时
    visible(val) {
      if (val) {
        this.startCountdown()
      } else {
        this.clearTimer()
      }
    }
  },
  beforeDestroy() { this.clearTimer() },
  methods: {
    startCountdown() {
      this.countdown = 5
      this.clearTimer()
      this.timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          this.clearTimer()
        }
      }, 1000)
    },
    clearTimer() {
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
    },
    handleConfirm() {
      // 倒计时未结束不允许确认 (Q-01 强制要求)
      if (this.countdown > 0) return
      this.$emit('confirm')
    },
    handleCancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped lang="scss">
.cdm-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: var(--jst-color-mask-dark); z-index: 200; display: flex; align-items: flex-end; justify-content: center; }
.cdm-sheet { width: 100%; background: var(--jst-color-card-bg); border-radius: 32rpx 32rpx 0 0; overflow: hidden; animation: cdm-slide-up 0.3s ease; }
@keyframes cdm-slide-up { from { transform: translateY(100%); } to { transform: translateY(0); } }
.cdm-handle { width: 72rpx; height: 8rpx; background: var(--jst-color-border); border-radius: 4rpx; margin: 24rpx auto 0; }
.cdm-header { padding: 32rpx 40rpx 16rpx; }
.cdm-title { font-size: 32rpx; font-weight: 700; color: var(--jst-color-text); line-height: 1.5; }
.cdm-body { padding: 0 40rpx 24rpx; }
.cdm-warn-item { display: flex; align-items: flex-start; gap: 12rpx; padding: 12rpx 0; }
.cdm-warn-icon { font-size: 28rpx; flex-shrink: 0; line-height: 1.6; }
.cdm-warn-text { font-size: 26rpx; color: var(--jst-color-text-secondary); line-height: 1.6; }
.cdm-footer { display: flex; gap: 20rpx; padding: 24rpx 40rpx calc(24rpx + env(safe-area-inset-bottom)); }
.cdm-btn { flex: 1; height: 88rpx; border-radius: var(--jst-radius-md); display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 700; }
.cdm-btn--cancel { background: var(--jst-color-page-bg); color: var(--jst-color-text-secondary); }
.cdm-btn--disabled { background: var(--jst-color-border); color: var(--jst-color-text-tertiary); }
.cdm-btn--danger { background: var(--jst-color-danger); color: #fff; }
</style>
