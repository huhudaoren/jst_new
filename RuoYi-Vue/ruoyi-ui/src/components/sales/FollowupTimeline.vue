<template>
  <div class="followup-timeline">
    <div class="followup-timeline__header">
      <span class="followup-timeline__title">跟进记录</span>
      <el-button type="primary" size="mini" icon="el-icon-plus" @click="openCreate">新建跟进</el-button>
    </div>

    <div v-loading="loading" class="followup-timeline__body">
      <el-empty v-if="!loading && records.length === 0" description="暂无跟进记录" :image-size="80" />

      <el-timeline v-else>
        <el-timeline-item
          v-for="r in records"
          :key="r.recordId"
          :timestamp="formatTime(r.followupAt)"
          placement="top"
        >
          <el-card shadow="never" class="followup-card">
            <div class="followup-card__top">
              <div class="followup-card__tags">
                <el-tag size="mini" type="primary" effect="plain">{{ r.followupType }}</el-tag>
                <el-tag v-if="r.mood" size="mini" :type="moodType(r.mood)" effect="plain" style="margin-left:4px">{{ r.mood }}</el-tag>
              </div>
              <div v-if="canEdit(r)" class="followup-card__actions">
                <el-button size="mini" type="text" icon="el-icon-edit" @click="openEdit(r)">编辑</el-button>
                <el-button size="mini" type="text" icon="el-icon-delete" style="color:#F56C6C" @click="handleDelete(r)">删除</el-button>
              </div>
            </div>

            <div class="followup-card__content">{{ r.content }}</div>

            <div v-if="r.nextFollowupAt" class="followup-card__hint">
              <i class="el-icon-time" /> 下次跟进：{{ formatDate(r.nextFollowupAt) }}
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>

    <!-- 新建/编辑弹窗 -->
    <followup-form
      :visible.sync="formVisible"
      :channel-id="channelId"
      :record="editingRecord"
      @saved="refresh"
    />
  </div>
</template>

<script>
import { listByChannel, remove } from '@/api/sales/me/followup'
import FollowupForm from './FollowupForm.vue'

export default {
  name: 'FollowupTimeline',
  components: { FollowupForm },
  props: {
    channelId: {
      type: [Number, String],
      required: true
    }
  },
  data() {
    return {
      loading: false,
      records: [],
      formVisible: false,
      editingRecord: null
    }
  },
  created() {
    this.refresh()
  },
  watch: {
    channelId() {
      this.refresh()
    }
  },
  methods: {
    refresh() {
      if (!this.channelId) return
      this.loading = true
      listByChannel(this.channelId).then(res => {
        this.records = (res.data || []).slice().reverse()
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    canEdit(r) {
      if (!r.canEditUntil) return false
      return new Date(r.canEditUntil) > new Date()
    },
    openCreate() {
      this.editingRecord = null
      this.formVisible = true
    },
    openEdit(r) {
      this.editingRecord = r
      this.formVisible = true
    },
    handleDelete(r) {
      this.$confirm('确定删除这条跟进记录？', '提示', { type: 'warning' }).then(() => {
        remove(r.recordId).then(() => {
          this.$message.success('已删除')
          this.refresh()
        }).catch(e => this.$message.error(e.msg || '删除失败'))
      }).catch(() => {})
    },
    moodType(mood) {
      const map = { happy: 'success', neutral: 'info', frustrated: 'warning', cold: 'danger' }
      return map[mood] || 'info'
    },
    formatTime(val) {
      if (!val) return ''
      return val.replace('T', ' ').substring(0, 16)
    },
    formatDate(val) {
      if (!val) return ''
      return String(val).substring(0, 10)
    }
  }
}
</script>

<style scoped>
.followup-timeline__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.followup-timeline__title {
  font-size: 15px;
  font-weight: 600;
  color: #172033;
}
.followup-card {
  border: 1px solid #e5eaf2;
}
.followup-card__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.followup-card__tags {
  display: flex;
  align-items: center;
  gap: 4px;
}
.followup-card__content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
  white-space: pre-wrap;
}
.followup-card__hint {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>
