<!-- 通用空状态组件; 支持 inline SVG 插画(data URI 作 background-image 跨端兼容) + CTA 按钮;
     新增 prop: illustration (orders|records|search|network|rights|default), size (sm|md|lg);
     SVG 源码全部内嵌在本组件 svgMap 中, 不依赖任何外部资源; url 支持 switchTab/reLaunch 前缀 -->
<template>
  <view class="jst-empty" :class="['jst-empty--' + size]">
    <view
      v-if="illustrationStyle"
      class="jst-empty__illustration"
      :style="illustrationStyle"
    />
    <u-empty
      v-else
      :mode="uEmptyMode"
      :text="text"
      :icon-size="120"
      :text-size="14"
      :margin-top="0"
    />

    <text v-if="illustrationStyle && text" class="jst-empty__text">{{ text }}</text>

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
// SVG 插画仓库; viewBox 200x200; 主色 #1A4FCC (brand-dark) + #E8F0FF (brand-light) + #F5A623 (amber);
// 风格: 扁平化, 描边 2.5-3px, 圆角, 低保真亲切不刺眼
const SVG_MAP = {
  // orders: 购物车轨迹 + 虚线包装盒 + 确认标记; 空订单场景
  orders: `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 200'>
<rect x='55' y='80' width='70' height='60' rx='6' fill='%23E8F0FF' stroke='%232B6CFF' stroke-width='2.5' stroke-dasharray='5 4'/>
<path d='M45 70 L60 70 L72 135 L135 135 L148 90 L70 90' fill='none' stroke='%231A4FCC' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'/>
<circle cx='82' cy='155' r='7' fill='%23FFFFFF' stroke='%231A4FCC' stroke-width='2.5'/>
<circle cx='128' cy='155' r='7' fill='%23FFFFFF' stroke='%231A4FCC' stroke-width='2.5'/>
<circle cx='140' cy='60' r='9' fill='%23F5A623' opacity='0.85'/>
<path d='M136 60 L139 63 L144 57' fill='none' stroke='%23FFFFFF' stroke-width='2' stroke-linecap='round'/>
</svg>`,
  // records: 卷轴/文档 + 问号 + 微笑圆 表示空记录
  records: `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 200'>
<rect x='55' y='55' width='90' height='100' rx='10' fill='%23FFFFFF' stroke='%231A4FCC' stroke-width='2.5'/>
<rect x='55' y='55' width='90' height='18' rx='10' fill='%23E8F0FF' stroke='%231A4FCC' stroke-width='2.5'/>
<line x1='70' y1='95' x2='130' y2='95' stroke='%23C0C4CC' stroke-width='2' stroke-linecap='round'/>
<line x1='70' y1='110' x2='115' y2='110' stroke='%23C0C4CC' stroke-width='2' stroke-linecap='round'/>
<line x1='70' y1='125' x2='125' y2='125' stroke='%23C0C4CC' stroke-width='2' stroke-linecap='round'/>
<circle cx='145' cy='145' r='22' fill='%23F5A623'/>
<path d='M138 142 Q145 136 152 142' fill='none' stroke='%23FFFFFF' stroke-width='2.5' stroke-linecap='round'/>
<path d='M138 152 Q145 158 152 152' fill='none' stroke='%23FFFFFF' stroke-width='2.5' stroke-linecap='round'/>
</svg>`,
  // search: 放大镜 + 斜线 + 镜内小问号; 搜索无结果
  search: `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 200'>
<circle cx='90' cy='90' r='42' fill='%23E8F0FF' stroke='%231A4FCC' stroke-width='3'/>
<line x1='122' y1='122' x2='152' y2='152' stroke='%231A4FCC' stroke-width='6' stroke-linecap='round'/>
<path d='M75 85 Q75 75 90 75 Q105 75 100 90 Q95 98 90 100' fill='none' stroke='%23F5A623' stroke-width='3' stroke-linecap='round'/>
<circle cx='90' cy='108' r='2.5' fill='%23F5A623'/>
<line x1='60' y1='118' x2='118' y2='60' stroke='%23FF4D4F' stroke-width='2.5' stroke-linecap='round' opacity='0.5'/>
</svg>`,
  // network: 云朵 + 断裂闪电 + 感叹号; 网络错误
  network: `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 200'>
<path d='M60 110 Q40 110 40 92 Q40 75 58 75 Q62 55 85 55 Q110 55 115 78 Q140 78 140 100 Q140 115 122 115 L62 115 Z' fill='%23E8F0FF' stroke='%231A4FCC' stroke-width='2.5' stroke-linejoin='round'/>
<path d='M95 125 L82 145 L95 145 L88 165' fill='none' stroke='%23F5A623' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'/>
<path d='M115 130 L108 142' stroke='%23FF4D4F' stroke-width='2.5' stroke-linecap='round'/>
<path d='M128 138 L122 148' stroke='%23FF4D4F' stroke-width='2.5' stroke-linecap='round'/>
<circle cx='138' cy='158' r='10' fill='%23FF4D4F'/>
<line x1='138' y1='153' x2='138' y2='159' stroke='%23FFFFFF' stroke-width='2.5' stroke-linecap='round'/>
<circle cx='138' cy='163' r='1.5' fill='%23FFFFFF'/>
</svg>`,
  // rights: 礼物盒 + 蝴蝶结 + 空星; 空权益/礼物
  rights: `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 200'>
<rect x='55' y='85' width='90' height='65' rx='4' fill='%23E8F0FF' stroke='%231A4FCC' stroke-width='2.5'/>
<line x1='100' y1='85' x2='100' y2='150' stroke='%231A4FCC' stroke-width='2.5'/>
<rect x='50' y='78' width='100' height='14' rx='3' fill='%231A4FCC'/>
<path d='M100 78 Q80 55 70 68 Q65 78 100 82 Q135 78 130 68 Q120 55 100 78 Z' fill='%23F5A623' stroke='%231A4FCC' stroke-width='2'/>
<path d='M75 55 L78 62 L85 63 L80 68 L81 75 L75 71 L69 75 L70 68 L65 63 L72 62 Z' fill='none' stroke='%23F5A623' stroke-width='2' stroke-linejoin='round'/>
<path d='M140 130 L143 137 L150 138 L145 143 L146 150 L140 146 L134 150 L135 143 L130 138 L137 137 Z' fill='none' stroke='%231A4FCC' stroke-width='1.8' stroke-linejoin='round'/>
</svg>`,
  // default: 抽象几何 + 微笑圆 + 漂浮粒子; 通用空态
  default: `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 200'>
<circle cx='100' cy='100' r='55' fill='%23E8F0FF' stroke='%231A4FCC' stroke-width='2.5'/>
<path d='M60 115 Q85 85 115 105 Q135 118 140 95' fill='none' stroke='%231A4FCC' stroke-width='2.5' stroke-linecap='round'/>
<circle cx='82' cy='88' r='5' fill='%231A4FCC'/>
<circle cx='122' cy='88' r='5' fill='%231A4FCC'/>
<circle cx='150' cy='55' r='8' fill='%23F5A623' opacity='0.6'/>
<circle cx='45' cy='150' r='6' fill='%23F5A623' opacity='0.4'/>
<circle cx='160' cy='140' r='4' fill='%232B6CFF' opacity='0.5'/>
</svg>`
}

export default {
  name: 'JstEmpty',
  props: {
    text: { type: String, default: '暂无数据' },
    icon: { type: String, default: '📭' },
    illustration: { type: String, default: '' }, // orders|records|search|network|rights|default
    size: { type: String, default: 'md' },       // sm|md|lg
    buttonText: { type: String, default: '' },
    buttonUrl: { type: String, default: '' },   // 'switchTab:...' / 'reLaunch:...' / default navigateTo
    buttonType: { type: String, default: 'primary' }
  },
  computed: {
    illustrationStyle() {
      if (!this.illustration) return ''
      const svg = SVG_MAP[this.illustration] || SVG_MAP.default
      // 压缩: 去换行避免 CSS url() 解析; % 已 encoded
      const compact = svg.replace(/\s*\n\s*/g, ' ').replace(/>\s+</g, '><')
      return `background-image:url("data:image/svg+xml;charset=utf-8,${compact}");`
    },
    uEmptyMode() {
      const modeMap = {
        '📭': 'message', '📋': 'list', '🔍': 'search', '🛒': 'order',
        '💳': 'order', '📰': 'data', '🏆': 'data', '📚': 'data',
        '🎓': 'data', '📄': 'list', '📁': 'list'
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

.jst-empty__illustration {
  width: 280rpx;
  height: 280rpx;
  margin-bottom: $jst-space-lg;
  background-repeat: no-repeat;
  background-position: center;
  background-size: contain;
}

.jst-empty--sm .jst-empty__illustration { width: 200rpx; height: 200rpx; }
.jst-empty--lg .jst-empty__illustration { width: 360rpx; height: 360rpx; }

.jst-empty__text {
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
  line-height: 1.6;
  max-width: 480rpx;
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
