<!-- jst-glass-card: 毛玻璃卡片容器，用于搜索栏/底栏/登录卡等场景 -->
<template>
  <view class="jst-glass-card" :class="[radiusClass, noBorder && 'jst-glass-card--no-border']" :style="customStyle">
    <slot />
  </view>
</template>

<script>
export default {
  name: 'JstGlassCard',
  props: {
    radius: {
      type: String,
      default: 'lg',
      validator: function(v) { return ['sm', 'md', 'lg', 'xl', 'round'].indexOf(v) !== -1 }
    },
    noBorder: {
      type: Boolean,
      default: false
    },
    blur: {
      type: [String, Number],
      default: 20
    },
    bg: {
      type: String,
      default: ''
    }
  },
  computed: {
    radiusClass: function() {
      return 'jst-glass-card--r-' + this.radius
    },
    customStyle: function() {
      var style = {}
      if (this.blur) {
        var val = typeof this.blur === 'number' ? this.blur + 'px' : this.blur
        style['backdrop-filter'] = 'blur(' + val + ')'
        style['-webkit-backdrop-filter'] = 'blur(' + val + ')'
      }
      if (this.bg) {
        style['background'] = this.bg
      }
      return style
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-glass-card {
  background: $jst-glass-bg;
  border: 1rpx solid $jst-glass-border;
  box-shadow: $jst-card-glow;
  overflow: hidden;
}

.jst-glass-card--no-border {
  border: none;
}

.jst-glass-card--r-sm { border-radius: $jst-radius-sm; }
.jst-glass-card--r-md { border-radius: $jst-radius-md; }
.jst-glass-card--r-lg { border-radius: $jst-radius-card; }
.jst-glass-card--r-xl { border-radius: $jst-radius-xl; }
.jst-glass-card--r-round { border-radius: $jst-radius-round; }
</style>
