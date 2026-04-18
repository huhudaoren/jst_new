<template>
  <span class="entity-link-wrap">
    <a v-if="canClick && name" class="entity-link" :class="[`entity-link--${mode}`]" @click.stop.prevent="onClick">{{ name }}</a>
    <span v-else-if="name" class="entity-link--disabled">{{ name }}</span>
    <span v-else class="entity-link--loading">{{ id ? '#' + id : '-' }}</span>
  </span>
</template>

<script>
import { resolveEntityRoute } from '@/utils/entityRouteMap'
import { checkPermi } from '@/utils/permission'

export default {
  name: 'EntityLink',
  props: {
    entity: { type: String, required: true },
    id: { type: [Number, String], default: null },
    name: { type: String, default: '' },
    mode: { type: String, default: 'text' },
    newTab: { type: Boolean, default: false }
  },
  computed: {
    routeInfo() { return this.id ? resolveEntityRoute(this.entity, this.id) : null },
    canClick() {
      if (!this.routeInfo) return false
      try { return checkPermi([this.routeInfo.perm]) } catch (e) { return true }
    }
  },
  methods: {
    onClick() {
      if (!this.routeInfo) return
      if (this.newTab) {
        const href = this.$router.resolve(this.routeInfo.path).href
        window.open(href, '_blank')
      } else {
        this.$router.push(this.routeInfo.path)
      }
    }
  }
}
</script>

<style scoped>
.entity-link-wrap { display: inline-block; }
.entity-link { color: #409eff; cursor: pointer; text-decoration: none; }
.entity-link:hover { text-decoration: underline; }
.entity-link--tag { padding: 2px 8px; background: #ecf5ff; border-radius: 3px; font-size: 12px; }
.entity-link--disabled { color: #606266; }
.entity-link--loading { color: #c0c4cc; font-size: 12px; }
</style>
