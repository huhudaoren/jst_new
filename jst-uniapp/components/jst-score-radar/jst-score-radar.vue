<!-- jst-score-radar: 成绩雷达图（Canvas 实现），用于成绩查询页 -->
<template>
  <view class="jst-score-radar">
    <canvas
      v-if="dimensions.length >= 3"
      :canvas-id="canvasId"
      class="jst-score-radar__canvas"
      :style="{ width: size + 'rpx', height: size + 'rpx' }"
    />
    <!-- 降级: 维度不足3个时显示数字列表 -->
    <view v-else class="jst-score-radar__fallback">
      <view v-for="(d, i) in dimensions" :key="i" class="jst-score-radar__fallback-row">
        <text class="jst-score-radar__fallback-label">{{ d.label }}</text>
        <text class="jst-score-radar__fallback-value">{{ d.value }}</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'JstScoreRadar',
  props: {
    // [{ label: '逻辑', value: 85, max: 100 }]
    dimensions: {
      type: Array,
      default: function() { return [] }
    },
    size: { type: Number, default: 400 },
    canvasId: { type: String, default: 'scoreRadar' }
  },
  watch: {
    dimensions: {
      handler: function() { this.draw() },
      deep: true
    }
  },
  mounted: function() {
    if (this.dimensions.length >= 3) {
      this.$nextTick(function() { this.draw() })
    }
  },
  methods: {
    // [visual-effect]
    draw: function() {
      var dims = this.dimensions
      if (dims.length < 3) return
      var ctx = uni.createCanvasContext(this.canvasId, this)
      var n = dims.length
      var ratio = uni.getSystemInfoSync().pixelRatio || 2
      var s = this.size / 2
      var cx = s, cy = s
      var r = s * 0.75
      var angleStep = (2 * Math.PI) / n
      var startAngle = -Math.PI / 2
      var levels = 4

      // 绘制网格
      for (var lv = 1; lv <= levels; lv++) {
        var lr = r * lv / levels
        ctx.beginPath()
        for (var i = 0; i <= n; i++) {
          var angle = startAngle + i * angleStep
          var x = cx + lr * Math.cos(angle)
          var y = cy + lr * Math.sin(angle)
          if (i === 0) ctx.moveTo(x, y)
          else ctx.lineTo(x, y)
        }
        ctx.closePath()
        ctx.setStrokeStyle(lv === levels ? '#DDDFE4' : '#EBEDF0')
        ctx.setLineWidth(1)
        ctx.stroke()
      }

      // 绘制轴线
      for (var j = 0; j < n; j++) {
        var axAngle = startAngle + j * angleStep
        ctx.beginPath()
        ctx.moveTo(cx, cy)
        ctx.lineTo(cx + r * Math.cos(axAngle), cy + r * Math.sin(axAngle))
        ctx.setStrokeStyle('#EBEDF0')
        ctx.setLineWidth(1)
        ctx.stroke()
      }

      // 绘制数据区域
      ctx.beginPath()
      for (var k = 0; k < n; k++) {
        var d = dims[k]
        var val = Math.min(d.value / (d.max || 100), 1)
        var da = startAngle + k * angleStep
        var dx = cx + r * val * Math.cos(da)
        var dy = cy + r * val * Math.sin(da)
        if (k === 0) ctx.moveTo(dx, dy)
        else ctx.lineTo(dx, dy)
      }
      ctx.closePath()
      ctx.setFillStyle('rgba(43, 108, 255, 0.15)')
      ctx.fill()
      ctx.setStrokeStyle('#2B6CFF')
      ctx.setLineWidth(2)
      ctx.stroke()

      // 绘制数据点 + 标签
      for (var m = 0; m < n; m++) {
        var dd = dims[m]
        var vv = Math.min(dd.value / (dd.max || 100), 1)
        var ma = startAngle + m * angleStep
        var mx = cx + r * vv * Math.cos(ma)
        var my = cy + r * vv * Math.sin(ma)
        ctx.beginPath()
        ctx.arc(mx, my, 4, 0, 2 * Math.PI)
        ctx.setFillStyle('#2B6CFF')
        ctx.fill()

        // 标签
        var lx = cx + (r + 20) * Math.cos(ma)
        var ly = cy + (r + 20) * Math.sin(ma)
        ctx.setFontSize(11)
        ctx.setFillStyle('#4A4A4A')
        ctx.setTextAlign('center')
        ctx.fillText(dd.label, lx, ly + 4)
      }

      ctx.draw()
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-score-radar {
  display: flex;
  justify-content: center;
  padding: $jst-space-lg 0;
}

.jst-score-radar__canvas {
  display: block;
}

.jst-score-radar__fallback {
  width: 100%;
  padding: $jst-space-md;
}

.jst-score-radar__fallback-row {
  display: flex;
  justify-content: space-between;
  padding: $jst-space-sm 0;
  border-bottom: 1rpx solid $jst-border;
}

.jst-score-radar__fallback-row:last-child {
  border-bottom: none;
}

.jst-score-radar__fallback-label {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.jst-score-radar__fallback-value {
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-brand;
}
</style>
