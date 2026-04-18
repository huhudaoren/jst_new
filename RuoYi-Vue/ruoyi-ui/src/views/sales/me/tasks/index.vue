<template>
  <div class="app-container">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">销售工作台</p>
        <h2>跟进任务</h2>
        <p class="hero-desc">查看主管派发的跟进任务，及时完成并关联跟进记录。</p>
      </div>
      <el-button icon="el-icon-refresh" :loading="loading" @click="loadTasks">刷新</el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="待办" name="pending" />
      <el-tab-pane label="进行中" name="in_progress" />
      <el-tab-pane label="已完成" name="completed" />
      <el-tab-pane label="已超时" name="overdue" />
    </el-tabs>

    <div v-loading="loading" style="min-height:200px">
      <empty-state-cta v-if="!loading && list.length === 0" :title="`暂无${tabLabel}任务`" description="主管派发的跟进任务会在这里显示" :image-size="80" />
      <followup-task-card
        v-for="task in list"
        :key="task.taskId"
        :task="task"
        @complete="handleComplete"
      />
    </div>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="loadTasks"
    />
  </div>
</template>

<script>
import { listMyTasks } from '@/api/sales/me/task'
import FollowupTaskCard from '@/components/sales/FollowupTaskCard.vue'

export default {
  name: 'SalesMyTasks',
  components: { FollowupTaskCard },
  data() {
    return {
      loading: false,
      activeTab: 'pending',
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 20 }
    }
  },
  computed: {
    tabLabel() {
      const map = { pending: '待办', in_progress: '进行中', completed: '已完成', overdue: '已超时' }
      return map[this.activeTab] || ''
    }
  },
  created() {
    this.loadTasks()
  },
  methods: {
    loadTasks() {
      this.loading = true
      listMyTasks({ ...this.queryParams, status: this.activeTab }).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    handleTabChange() {
      this.queryParams.pageNum = 1
      this.loadTasks()
    },
    handleComplete(task) {
      this.$message.success(`任务"${task.title}"已完成`)
      this.loadTasks()
    }
  }
}
</script>

<style scoped>
.page-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 24px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
  margin-bottom: 16px;
}
.hero-eyebrow { margin: 0; font-size: 12px; color: #409EFF; text-transform: uppercase; }
.page-hero h2 { margin: 4px 0; font-size: 22px; font-weight: 700; color: #172033; }
.hero-desc { margin: 4px 0 0; color: #6f7b8f; }
</style>
