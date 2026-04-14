<template>
  <div class="cert-element-panel">
    <div class="panel-title">元素面板</div>

    <div class="panel-section">
      <div class="section-label">文本</div>
      <div
        class="element-item"
        draggable="true"
        @dragstart="onDrag($event, 'staticText')"
      >
        <i class="el-icon-edit" />
        <span>静态文本</span>
      </div>
    </div>

    <div class="panel-section">
      <div class="section-label">动态变量</div>
      <div
        v-for="v in variables"
        :key="v.key"
        class="element-item element-var"
        draggable="true"
        @dragstart="onDrag($event, 'variable', v)"
      >
        <i class="el-icon-price-tag" />
        <span>{{ v.label }}</span>
        <code v-text="varTag(v.key)"></code>
      </div>
    </div>

    <div class="panel-section">
      <div class="section-label">图片 / 形状</div>
      <div class="element-item" draggable="true" @dragstart="onDrag($event, 'image')">
        <i class="el-icon-picture-outline" />
        <span>图片</span>
      </div>
      <div class="element-item" draggable="true" @dragstart="onDrag($event, 'rect')">
        <i class="el-icon-crop" />
        <span>矩形</span>
      </div>
      <div class="element-item" draggable="true" @dragstart="onDrag($event, 'line')">
        <i class="el-icon-minus" />
        <span>线条</span>
      </div>
    </div>
  </div>
</template>

<script>
const CONTEST_VARS = [
  { key: 'name', label: '获奖者姓名' },
  { key: 'contestName', label: '赛事名称' },
  { key: 'awardName', label: '奖项名称' },
  { key: 'score', label: '总成绩' },
  { key: 'certNo', label: '证书编号' },
  { key: 'date', label: '颁发日期' },
  { key: 'qrcode', label: '验证二维码' },
  { key: 'school', label: '学校名称' },
  { key: 'groupLevel', label: '参赛组别' }
]

const CHANNEL_VARS = [
  { key: 'channelName', label: '渠道名称' },
  { key: 'authNo', label: '授权编号' },
  { key: 'authDate', label: '授权日期' },
  { key: 'scope', label: '授权范围' }
]

export default {
  name: 'ElementPanel',
  props: {
    ownerType: { type: String, default: 'contest' }
  },
  computed: {
    variables() {
      return this.ownerType === 'channel' ? CHANNEL_VARS : CONTEST_VARS
    }
  },
  methods: {
    varTag(key) {
      return '{{' + key + '}}'
    },
    onDrag(event, type, varDef) {
      const payload = { type }
      if (varDef) {
        payload.varKey = varDef.key
        payload.varLabel = varDef.label
      }
      event.dataTransfer.setData('application/cert-element', JSON.stringify(payload))
      event.dataTransfer.effectAllowed = 'copy'
    }
  }
}
</script>

<style scoped>
.cert-element-panel {
  width: 200px;
  min-width: 200px;
  border-right: 1px solid #e4e7ed;
  padding: 12px;
  overflow-y: auto;
  background: #fafbfc;
}
.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}
.panel-section {
  margin-bottom: 16px;
}
.section-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 1px;
}
.element-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 7px 10px;
  margin-bottom: 4px;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
  cursor: grab;
  border: 1px solid transparent;
  transition: all 0.15s;
}
.element-item:hover {
  background: #ecf5ff;
  border-color: #b3d8ff;
  color: #409eff;
}
.element-item:active {
  cursor: grabbing;
}
.element-var code {
  margin-left: auto;
  font-size: 11px;
  color: #c0c4cc;
}
@media (max-width: 768px) {
  .cert-element-panel {
    width: 100%;
    min-width: 0;
    border-right: none;
    border-bottom: 1px solid #e4e7ed;
    max-height: 180px;
  }
  .panel-section {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
  }
  .section-label {
    width: 100%;
  }
  .element-item {
    flex: 0 0 auto;
  }
}
</style>
