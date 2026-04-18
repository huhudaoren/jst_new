<!-- 中文注释: 统一庆祝动画组件 jst-celebrate
     纯 CSS 动画，不依赖任何外部库/素材。
     4 preset: level-up | money-rain | ribbon | scroll
     props: visible(.sync) preset title subtitle duration showClose
     events: @close
     同时刻只一个显示（内部 queue，延迟显示后一个）
-->
<template>
  <view v-if="shown" class="jst-celebrate" @tap.stop="onMaskTap">
    <view class="jst-celebrate__mask"></view>

    <!-- ============ level-up ============ -->
    <view v-if="preset === 'level-up'" class="jst-celebrate__stage jst-celebrate__stage--levelup" @tap.stop>
      <view class="jst-celebrate__disc">
        <text class="jst-celebrate__disc-emoji">🏆</text>
      </view>
      <view
        v-for="(style, idx) in sparkleStyles"
        :key="idx"
        class="jst-celebrate__sparkle"
        :style="style"
      ></view>
      <text v-if="title" class="jst-celebrate__title jst-celebrate__title--light">{{ title }}</text>
      <text v-if="subtitle" class="jst-celebrate__subtitle jst-celebrate__subtitle--light">{{ subtitle }}</text>
    </view>

    <!-- ============ money-rain ============ -->
    <view v-else-if="preset === 'money-rain'" class="jst-celebrate__stage jst-celebrate__stage--money" @tap.stop>
      <view
        v-for="(style, idx) in coinStyles"
        :key="idx"
        class="jst-celebrate__coin"
        :style="style"
      >
        <text class="jst-celebrate__coin-emoji">💰</text>
      </view>
      <view class="jst-celebrate__money-bubble">
        <text v-if="title" class="jst-celebrate__money-amount">{{ title }}</text>
        <text v-if="subtitle" class="jst-celebrate__money-sub">{{ subtitle }}</text>
      </view>
    </view>

    <!-- ============ ribbon ============ -->
    <view v-else-if="preset === 'ribbon'" class="jst-celebrate__stage jst-celebrate__stage--ribbon" @tap.stop>
      <view
        v-for="(style, idx) in ribbonStyles"
        :key="idx"
        class="jst-celebrate__ribbon"
        :style="style"
      ></view>
      <view class="jst-celebrate__check">
        <text class="jst-celebrate__check-emoji">✅</text>
      </view>
      <text v-if="title" class="jst-celebrate__title">{{ title }}</text>
      <text v-if="subtitle" class="jst-celebrate__subtitle">{{ subtitle }}</text>
      <view class="jst-celebrate__slot"><slot></slot></view>
    </view>

    <!-- ============ scroll ============ -->
    <view v-else-if="preset === 'scroll'" class="jst-celebrate__stage jst-celebrate__stage--scroll" @tap.stop>
      <view class="jst-celebrate__scroll">
        <view class="jst-celebrate__scroll-inner">
          <text class="jst-celebrate__scroll-star">✦</text>
          <text v-if="title" class="jst-celebrate__scroll-title">{{ title }}</text>
          <text v-if="subtitle" class="jst-celebrate__scroll-sub">{{ subtitle }}</text>
          <text class="jst-celebrate__scroll-star jst-celebrate__scroll-star--r">✦</text>
        </view>
      </view>
      <view class="jst-celebrate__slot jst-celebrate__slot--bottom">
        <slot>
          <view class="jst-celebrate__cta" @tap.stop="close">查看证书</view>
        </slot>
      </view>
    </view>

    <view v-if="showClose" class="jst-celebrate__close" @tap.stop="close">×</view>
  </view>
</template>

<script>
// 全局 queue：同时刻只一个 celebrate 显示
const _queue = []
let _current = null

export default {
  name: 'jst-celebrate',
  model: { prop: 'visible', event: 'update:visible' },
  props: {
    visible: { type: Boolean, default: false },
    preset: { type: String, default: 'level-up' },
    title: { type: String, default: '' },
    subtitle: { type: String, default: '' },
    duration: { type: Number, default: 2500 },
    showClose: { type: Boolean, default: false }
  },
  data() { return { shown: false, _timer: null } },
  watch: {
    visible(val) { val ? this.enqueue() : this.close() }
  },
  computed: {
    // 稀疏光点预计算数组：绕圆盘发散（uniapp 不支持 :style 函数调用，改为 computed 数组）
    sparkleStyles() {
      return Array.from({ length: 14 }, (_, i) => {
        const n = i + 1
        const angle = (n * 360) / 14
        const delay = (n % 5) * 60
        return `--angle: ${angle}deg; animation-delay: ${delay}ms;`
      })
    },
    // 金币预计算数组：x 随机、delay 随机、duration 随机
    coinStyles() {
      return Array.from({ length: 24 }, (_, i) => {
        const n = i + 1
        const left = (n * 37) % 90 + 2          // 2%~92%
        const delay = (n * 71) % 900             // 0~900ms
        const dur = 2000 + ((n * 53) % 1200)     // 2000~3200ms
        const rot = (n * 47) % 360
        return `left: ${left}%; animation-delay: ${delay}ms; animation-duration: ${dur}ms; --start-rot: ${rot}deg;`
      })
    },
    // 彩带预计算数组：多色斜飘
    ribbonStyles() {
      const colors = ['#FF6B6B', '#FFD54F', '#4FC3F7', '#81C784', '#BA68C8', '#FFB74D', '#F06292']
      return Array.from({ length: 7 }, (_, i) => {
        const n = i + 1
        const left = (n - 1) * 14 + 2
        const delay = ((n - 1) % 4) * 120
        const dur = 1800 + ((n * 83) % 600)
        const skew = (n % 2 === 0 ? 1 : -1) * (15 + (n * 3) % 10)
        return `left: ${left}%; background: ${colors[n - 1] || '#FFD54F'}; animation-delay: ${delay}ms; animation-duration: ${dur}ms; --skew: ${skew}deg;`
      })
    }
  },
  methods: {
    enqueue() {
      if (_current && _current !== this) { _queue.push(this); return }
      _current = this
      this.shown = true
      if (this.duration > 0) {
        this._timer && clearTimeout(this._timer)
        this._timer = setTimeout(() => this.close(), this.duration)
      }
    },
    close() {
      if (this._timer) { clearTimeout(this._timer); this._timer = null }
      this.shown = false
      this.$emit('update:visible', false)
      this.$emit('close')
      if (_current === this) {
        _current = null
        const next = _queue.shift()
        next && setTimeout(() => next.enqueue(), 300)
      }
    },
    onMaskTap() { if (this.showClose) this.close() }
  },
  mounted() { if (this.visible) this.enqueue() },
  beforeDestroy() {
    if (this._timer) clearTimeout(this._timer)
    const i = _queue.indexOf(this); if (i >= 0) _queue.splice(i, 1)
    if (_current === this) _current = null
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/design-tokens.scss';

// ── 通用遮罩 & 舞台 ──
.jst-celebrate {
  position: fixed; inset: 0; z-index: 9999;
  display: flex; align-items: center; justify-content: center;
  pointer-events: auto;
}
.jst-celebrate__mask {
  position: absolute; inset: 0;
  background: rgba(0, 0, 0, 0.55);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  animation: jst-fade-in 280ms cubic-bezier(0.22, 1, 0.36, 1) both;
}
.jst-celebrate__stage {
  position: relative; z-index: 1;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: $jst-space-xl;
}
.jst-celebrate__title {
  display: block; margin-top: $jst-space-lg;
  font-size: $jst-font-xxl; font-weight: $jst-weight-bold;
  color: $jst-text-primary;
  animation: jst-pop 420ms cubic-bezier(0.22, 1, 0.36, 1) both;
  animation-delay: 150ms;
}
.jst-celebrate__title--light { color: #FFFFFF; text-shadow: 0 4rpx 20rpx rgba(255, 213, 79, 0.6); }
.jst-celebrate__subtitle {
  display: block; margin-top: $jst-space-sm;
  font-size: $jst-font-base; color: $jst-text-regular;
  animation: jst-fade-up 420ms cubic-bezier(0.22, 1, 0.36, 1) both;
  animation-delay: 280ms;
}
.jst-celebrate__subtitle--light { color: rgba(255, 255, 255, 0.9); }

// 关闭 X
.jst-celebrate__close {
  position: absolute; top: 80rpx; right: 48rpx; z-index: 2;
  width: 64rpx; height: 64rpx; border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.18); color: #FFFFFF;
  font-size: 40rpx; line-height: 64rpx; text-align: center;
  backdrop-filter: blur(6px);
}

// ═══ preset: level-up ═══
.jst-celebrate__disc {
  position: relative;
  width: 320rpx; height: 320rpx; border-radius: 999rpx;
  background: linear-gradient(135deg, #FFD54F 0%, #FFA000 100%);
  box-shadow: 0 12rpx 48rpx rgba(255, 160, 0, 0.5), inset 0 -8rpx 20rpx rgba(255, 255, 255, 0.25);
  display: flex; align-items: center; justify-content: center;
  animation: jst-disc-in 600ms cubic-bezier(0.22, 1, 0.36, 1) both;
}
.jst-celebrate__disc-emoji { font-size: 180rpx; line-height: 1; filter: drop-shadow(0 4rpx 8rpx rgba(0, 0, 0, 0.2)); }
.jst-celebrate__sparkle {
  position: absolute; left: 50%; top: 50%;
  width: 16rpx; height: 16rpx; border-radius: 999rpx;
  background: $jst-gold; box-shadow: 0 0 16rpx $jst-gold;
  transform: translate(-50%, -50%) rotate(var(--angle)) translateY(-240rpx) scale(0);
  animation: jst-sparkle 1100ms cubic-bezier(0.22, 1, 0.36, 1) both;
}

// ═══ preset: money-rain ═══
.jst-celebrate__stage--money { width: 100%; height: 100%; }
.jst-celebrate__coin {
  position: absolute; top: -80rpx;
  width: 64rpx; height: 64rpx; border-radius: 999rpx;
  background: radial-gradient(circle at 30% 30%, #FFE082 0%, #FFB300 70%, #F57C00 100%);
  box-shadow: 0 4rpx 12rpx rgba(245, 124, 0, 0.5);
  display: flex; align-items: center; justify-content: center;
  animation: jst-coin-fall linear both;
  transform: rotate(var(--start-rot));
}
.jst-celebrate__coin-emoji { font-size: 40rpx; line-height: 1; }
.jst-celebrate__money-bubble {
  position: relative; z-index: 2;
  padding: $jst-space-xl $jst-space-xxl;
  background: rgba(255, 255, 255, 0.95);
  border-radius: $jst-radius-xl;
  box-shadow: 0 16rpx 48rpx rgba(245, 166, 35, 0.4);
  text-align: center;
  animation: jst-pop 500ms cubic-bezier(0.22, 1, 0.36, 1) both;
  animation-delay: 200ms;
}
.jst-celebrate__money-amount {
  display: block;
  font-size: 72rpx; font-weight: $jst-weight-bold;
  color: $jst-amber;
}
.jst-celebrate__money-sub {
  display: block; margin-top: $jst-space-sm;
  font-size: $jst-font-base; color: $jst-text-regular;
}

// ═══ preset: ribbon ═══
.jst-celebrate__stage--ribbon { width: 100%; }
.jst-celebrate__ribbon {
  position: absolute; top: -40rpx;
  width: 16rpx; height: 120rpx;
  animation: jst-ribbon-fall linear both;
  transform: skewX(var(--skew));
  border-radius: 4rpx;
}
.jst-celebrate__check {
  width: 200rpx; height: 200rpx; border-radius: 999rpx;
  background: #FFFFFF;
  box-shadow: 0 12rpx 32rpx rgba(24, 181, 102, 0.35);
  display: flex; align-items: center; justify-content: center;
  animation: jst-pop 500ms cubic-bezier(0.22, 1, 0.36, 1) both;
}
.jst-celebrate__check-emoji { font-size: 140rpx; line-height: 1; }
.jst-celebrate__slot { margin-top: $jst-space-xl; animation: jst-fade-up 420ms ease-out 400ms both; }

// ═══ preset: scroll ═══
.jst-celebrate__stage--scroll { width: 100%; }
.jst-celebrate__scroll {
  width: 560rpx; height: 40rpx;
  background: linear-gradient(180deg, #FFF8E1 0%, #FFECB3 100%);
  border: 4rpx solid #B8860B;
  border-radius: $jst-radius-md;
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.4), inset 0 0 40rpx rgba(184, 134, 11, 0.15);
  transform-origin: top center; transform: scaleY(0.1);
  overflow: hidden;
  animation: jst-scroll-expand 1200ms cubic-bezier(0.4, 0, 0.2, 1) both;
  animation-delay: 100ms;
}
.jst-celebrate__scroll-inner {
  padding: $jst-space-xxl $jst-space-xl; text-align: center;
  opacity: 0;
  animation: jst-fade-up 500ms ease-out 1100ms both;
}
.jst-celebrate__scroll-star {
  display: inline-block; font-size: 40rpx; color: #B8860B;
  margin: 0 $jst-space-md;
}
.jst-celebrate__scroll-star--r { margin-left: $jst-space-md; }
.jst-celebrate__scroll-title {
  display: block; margin-top: $jst-space-md;
  font-size: 44rpx; font-weight: $jst-weight-bold; color: #5D4037;
  letter-spacing: 4rpx;
}
.jst-celebrate__scroll-sub {
  display: block; margin-top: $jst-space-sm;
  font-size: $jst-font-base; color: #8D6E63;
}
.jst-celebrate__slot--bottom { margin-top: $jst-space-xl; animation-delay: 1400ms; }
.jst-celebrate__cta {
  padding: $jst-space-md $jst-space-xxl;
  background: #FFFFFF; color: #B8860B;
  border-radius: 999rpx; font-size: $jst-font-base; font-weight: $jst-weight-semibold;
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.2);
}

// ═══ keyframes ═══
@keyframes jst-fade-in { from { opacity: 0; } to { opacity: 1; } }
@keyframes jst-fade-up {
  from { opacity: 0; transform: translateY(20rpx); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes jst-pop {
  0% { opacity: 0; transform: scale(0.3); }
  60% { opacity: 1; transform: scale(1.08); }
  100% { opacity: 1; transform: scale(1); }
}
@keyframes jst-disc-in {
  0% { opacity: 0; transform: scale(0.2) rotate(-60deg); }
  60% { opacity: 1; transform: scale(1.1) rotate(8deg); }
  100% { opacity: 1; transform: scale(1) rotate(0); }
}
@keyframes jst-sparkle {
  0% { transform: translate(-50%, -50%) rotate(var(--angle)) translateY(-100rpx) scale(0); opacity: 0; }
  50% { opacity: 1; }
  100% { transform: translate(-50%, -50%) rotate(var(--angle)) translateY(-360rpx) scale(1.2); opacity: 0; }
}
@keyframes jst-coin-fall {
  0% { transform: translateY(0) rotate(var(--start-rot)); opacity: 0; }
  10% { opacity: 1; }
  100% { transform: translateY(120vh) rotate(calc(var(--start-rot) + 720deg)); opacity: 0.8; }
}
@keyframes jst-ribbon-fall {
  0% { transform: translateY(0) skewX(var(--skew)); opacity: 0; }
  10% { opacity: 1; }
  100% { transform: translateY(120vh) skewX(calc(var(--skew) * -1)); opacity: 0.7; }
}
@keyframes jst-scroll-expand {
  0% { height: 40rpx; transform: scaleY(0.1); }
  100% { height: 600rpx; transform: scaleY(1); }
}
</style>
