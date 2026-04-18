<template>
  <div class="page-header">
    <el-breadcrumb v-if="breadcrumb && breadcrumb.length" separator="/" class="page-header__bc">
      <el-breadcrumb-item v-for="(item, i) in breadcrumb" :key="i" :to="item.to || null">
        {{ item.text }}
      </el-breadcrumb-item>
    </el-breadcrumb>

    <div class="page-header__row">
      <div class="page-header__title-wrap">
        <h2 class="page-header__title">{{ title }}</h2>
        <p v-if="helpText" class="page-header__help">
          <i class="el-icon-info"></i> {{ helpText }}
        </p>
      </div>

      <div class="page-header__actions">
        <el-button
          v-if="primaryAction"
          type="primary"
          :icon="primaryAction.icon || 'el-icon-plus'"
          @click="primaryAction.handler"
        >{{ primaryAction.text }}</el-button>
        <el-button
          v-if="secondaryAction"
          :icon="secondaryAction.icon"
          @click="secondaryAction.handler"
        >{{ secondaryAction.text }}</el-button>
        <el-dropdown v-if="moreActions && moreActions.length" @command="onMoreCommand">
          <el-button>更多<i class="el-icon-arrow-down el-icon--right"></i></el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="a in moreActions" :key="a.text"
              :command="a.text" :divided="a.divided" :icon="a.icon"
            >{{ a.text }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PageHeader',
  props: {
    title: { type: String, required: true },
    breadcrumb: { type: Array, default: () => [] },
    helpText: { type: String, default: '' },
    primaryAction: { type: Object, default: null },
    secondaryAction: { type: Object, default: null },
    moreActions: { type: Array, default: () => [] }
  },
  methods: {
    onMoreCommand(text) {
      const item = this.moreActions.find(a => a.text === text)
      if (item && item.handler) item.handler()
    }
  }
}
</script>

<style scoped>
.page-header { padding: 16px 20px; background: #fff; margin-bottom: 16px; border-radius: 4px; }
.page-header__bc { margin-bottom: 8px; font-size: 12px; }
.page-header__row { display: flex; justify-content: space-between; align-items: flex-start; }
.page-header__title { margin: 0; font-size: 20px; font-weight: 600; color: #303133; }
.page-header__help { margin: 6px 0 0; color: #909399; font-size: 13px; }
.page-header__help .el-icon-info { color: #409eff; margin-right: 4px; }
.page-header__actions { display: flex; gap: 8px; }
@media (max-width: 768px) { .page-header__row { flex-direction: column; gap: 12px; } }
</style>
