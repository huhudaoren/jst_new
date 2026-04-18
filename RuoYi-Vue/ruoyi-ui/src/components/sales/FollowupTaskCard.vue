<template>
  <el-card shadow="never" class="followup-task-card" :class="{ 'is-overdue': isOverdue }">
    <div class="task-card__header">
      <span class="task-card__title">{{ task.title }}</span>
      <el-tag size="mini" :type="statusType">{{ statusLabel }}</el-tag>
    </div>

    <div v-if="task.description" class="task-card__desc">{{ task.description }}</div>

    <div class="task-card__footer">
      <span class="task-card__due" :class="{ 'is-overdue': isOverdue }">
        <i class="el-icon-time" /> 截止：{{ formatDate(task.dueDate) }}
      </span>
      <el-button
        v-if="canComplete"
        type="primary"
        size="mini"
        :loading="completing"
        @click="handleComplete"
      >完成</el-button>
    </div>
  </el-card>
</template>

<script>
import { completeTask } from '@/api/sales/me/task'

export default {
  name: 'FollowupTaskCard',
  props: {
    task: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      completing: false
    }
  },
  computed: {
    canComplete() {
      return ['pending', 'in_progress'].includes(this.task.status)
    },
    isOverdue() {
      return this.task.status === 'overdue'
    },
    statusType() {
      const map = {
        pending: 'warning',
        in_progress: 'primary',
        completed: 'success',
        overdue: 'danger'
      }
      return map[this.task.status] || 'info'
    },
    statusLabel() {
      const map = {
        pending: '待办',
        in_progress: '进行中',
        completed: '已完成',
        overdue: '已超时'
      }
      return map[this.task.status] || this.task.status
    }
  },
  methods: {
    formatDate(val) {
      if (!val) return '--'
      return String(val).substring(0, 10)
    },
    handleComplete() {
      this.$confirm('确认完成此任务？', '提示', { type: 'info' }).then(() => {
        this.completing = true
        completeTask(this.task.taskId, null).then(() => {
          this.$message.success('任务已完成')
          this.$emit('complete', this.task)
        }).catch(e => this.$message.error(e.msg || '操作失败')).finally(() => {
          this.completing = false
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.followup-task-card {
  margin-bottom: 12px;
  border: 1px solid #e5eaf2;
  border-radius: 6px;
}
.followup-task-card.is-overdue {
  border-color: #fde2e2;
  background: #fff8f8;
}
.task-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.task-card__title {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}
.task-card__desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 10px;
  line-height: 1.5;
}
.task-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.task-card__due {
  font-size: 12px;
  color: #909399;
}
.task-card__due.is-overdue {
  color: #F56C6C;
  font-weight: 600;
}
</style>
