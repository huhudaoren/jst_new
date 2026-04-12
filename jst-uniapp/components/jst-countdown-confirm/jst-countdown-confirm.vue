<!-- 中文注释: 倒计时确认弹窗组件 (E1-CH-3, Q-01 决策)
     用途: 渠道方解绑学生时的5秒倒计时确认，防止误操作
     接口: props.visible / props.studentName / emit('confirm') / emit('cancel') -->
<template>
  <u-popup :show="visible" mode="bottom" round="32" @close="handleCancel">
    <view class="cdm-sheet">
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
        <u-button
          type="info"
          text="取消"
          :custom-style="{ flex: 1 }"
          @click="handleCancel"
        />
        <u-button
          type="error"
          :text="countdown > 0 ? '确认解绑（' + countdown + '）' : '确认解绑'"
          :disabled="countdown > 0"
          :custom-style="{ flex: 1 }"
          @click="handleConfirm"
        />
      </view>
    </view>
  </u-popup>
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
@import '@/styles/design-tokens.scss';

.cdm-sheet {
  width: 100%;
  background: $jst-bg-card;
  overflow: hidden;
}

.cdm-handle {
  width: 72rpx;
  height: 8rpx;
  background: $jst-border;
  border-radius: $jst-radius-xs;
  margin: $jst-space-lg auto 0;
}

.cdm-header {
  padding: $jst-space-xl 40rpx $jst-space-md;
}

.cdm-title {
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
  line-height: 1.5;
}

.cdm-body {
  padding: 0 40rpx $jst-space-lg;
}

.cdm-warn-item {
  display: flex;
  align-items: flex-start;
  gap: $jst-space-sm;
  padding: $jst-space-sm 0;
}

.cdm-warn-icon {
  font-size: $jst-font-base;
  flex-shrink: 0;
  line-height: 1.6;
}

.cdm-warn-text {
  font-size: $jst-font-sm;
  color: $jst-text-regular;
  line-height: 1.6;
}

.cdm-footer {
  display: flex;
  gap: 20rpx;
  padding: $jst-space-lg 40rpx;
  padding-bottom: calc(#{$jst-space-lg} + env(safe-area-inset-bottom));
}
</style>
