<template>
  <el-empty :description="description" :image-size="imageSize">
    <slot />
  </el-empty>
</template>

<script>
export default {
  name: 'JstEmptyState',
  props: {
    description: { type: String, default: '暂无数据' },
    imageSize: { type: Number, default: 120 }
  }
}
</script>
