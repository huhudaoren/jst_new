<template>
  <div class="app-container deprecated-page">
    <el-result icon="warning" title="该页面已废弃" sub-title="公告管理已迁移到新版页面。">
      <template slot="extra">
        <el-button type="primary" @click="goNewPage">前往新页面</el-button>
      </template>
    </el-result>
  </div>
</template>

<script>
export default {
  name: 'JstNoticeDeprecated',
  methods: {
    goNewPage() {
      this.$router.push('/jst/notice')
    }
  }
}
</script>

<style scoped>
.deprecated-page {
  min-height: 360px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
