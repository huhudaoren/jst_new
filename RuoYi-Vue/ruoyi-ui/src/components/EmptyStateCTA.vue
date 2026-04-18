<template>
  <div class="empty-state">
    <el-empty :image-size="imageSize" :description="null">
      <template #description>
        <div class="empty-state__title">{{ title }}</div>
        <div v-if="description" class="empty-state__desc">{{ description }}</div>
      </template>
      <div class="empty-state__actions" v-if="primaryText || secondaryText">
        <el-button v-if="primaryText" type="primary" :icon="primaryIcon" @click="$emit('primary-click')">{{ primaryText }}</el-button>
        <el-button v-if="secondaryText" :icon="secondaryIcon" @click="$emit('secondary-click')">{{ secondaryText }}</el-button>
      </div>
    </el-empty>
  </div>
</template>

<script>
export default {
  name: 'EmptyStateCTA',
  props: {
    title: { type: String, default: '暂无数据' },
    description: { type: String, default: '' },
    primaryText: { type: String, default: '' },
    primaryIcon: { type: String, default: 'el-icon-plus' },
    secondaryText: { type: String, default: '' },
    secondaryIcon: { type: String, default: '' },
    imageSize: { type: Number, default: 100 }
  }
}
</script>

<style scoped>
.empty-state { padding: 40px 20px; text-align: center; }
.empty-state__title { font-size: 16px; color: #606266; margin-bottom: 4px; }
.empty-state__desc { font-size: 13px; color: #909399; }
.empty-state__actions { margin-top: 20px; display: flex; gap: 12px; justify-content: center; }
</style>
