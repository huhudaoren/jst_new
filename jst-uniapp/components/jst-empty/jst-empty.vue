<!-- 中文注释: 通用空状态组件; 支持 CTA 按钮, url 支持 switchTab: 前缀跳转 tab，否则 navigateTo; 无 url 时 emit action -->
<template>
  <view class="jst-empty">
    <u-empty
      :mode="uEmptyMode"
      :text="text"
      :icon-size="120"
      :text-size="14"
      :margin-top="0"
    />
    <view v-if="buttonText" class="jst-empty__cta-wrap">
      <u-button
        class="jst-empty__cta"
        :class="{ 'jst-empty__cta--ghost': buttonType === 'ghost' }"
        shape="circle"
        @click="handleAction"
      >{{ buttonText }}</u-button>
    </view>
  </view>
</template>

<script>
export default {
  name: 'JstEmpty',
  props: {
    text: {
      type: String,
      default: '暂无数据'
    },
    icon: {
      type: String,
      default: '📭'
    },
    buttonText: {
      type: String,
      default: ''
    },
    /**
     * 跳转 URL, 支持前缀:
     *   'switchTab:/pages/xxx'  → uni.switchTab
     *   'reLaunch:/pages/xxx'   → uni.reLaunch
     *   '/pages-sub/xxx'        → uni.navigateTo (默认)
     * 为空时 emit('action')
     */
    buttonUrl: {
      type: String,
      default: ''
    },
    buttonType: {
      type: String,
      default: 'primary' // primary | ghost
    }
  },
  computed: {
    uEmptyMode() {
      const modeMap = {
        '📭': 'message',
        '📋': 'list',
        '🔍': 'search',
        '🛒': 'order',
        '💳': 'order',
        '📰': 'data',
        '🏆': 'data',
        '📚': 'data',
        '🎓': 'data',
        '📄': 'list',
        '📁': 'list'
      }
      return modeMap[this.icon] || 'data'
    }
  },
  methods: {
    handleAction() {
      if (!this.buttonUrl) {
        this.$emit('action')
        return
      }
      const raw = this.buttonUrl
      if (raw.startsWith('switchTab:')) {
        uni.switchTab({ url: raw.slice('switchTab:'.length) })
      } else if (raw.startsWith('reLaunch:')) {
        uni.reLaunch({ url: raw.slice('reLaunch:'.length) })
      } else {
        uni.navigateTo({ url: raw })
      }
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $jst-space-xxl $jst-space-xl;
  text-align: center;
}

.jst-empty__cta-wrap {
  margin-top: $jst-space-lg;
}

::v-deep .jst-empty__cta.u-button {
  min-width: 240rpx;
  height: 72rpx;
  padding: 0 $jst-space-xl;
  border: none;
  border-radius: $jst-radius-md;
  background: $jst-brand-gradient;
  color: $jst-text-inverse;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

::v-deep .jst-empty__cta--ghost.u-button {
  background: $jst-brand-light;
  color: $jst-brand;
}
</style>
