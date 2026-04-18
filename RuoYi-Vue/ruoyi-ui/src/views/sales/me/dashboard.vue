<template>
  <div class="app-container sales-dashboard">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">销售工作台</p>
        <h2>工作台首页</h2>
        <p class="hero-desc">查看本月业绩摘要、待跟进任务与工作提醒。</p>
      </div>
    </div>

    <!-- 统计卡片行 -->
    <el-row :gutter="16" class="stat-row" v-loading="statsLoading">
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">本月订单</div>
          <div class="stat-card__value">{{ stats.orderCount != null ? stats.orderCount : '--' }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">覆盖渠道</div>
          <div class="stat-card__value">{{ stats.channelCount != null ? stats.channelCount : '--' }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">本月 GMV</div>
          <div class="stat-card__value">
            <masked-amount :value="stats.totalGmv" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">本月提成</div>
          <div class="stat-card__value">
            <masked-amount :value="stats.totalCommission" />
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:16px">
      <!-- 今日待跟进 -->
      <el-col :xs="24" :sm="12">
        <el-card shadow="never" class="section-card">
          <div slot="header" class="section-card__header">
            <span>今日待跟进任务</span>
            <el-button type="text" @click="$router.push('/sales-workbench/tasks')">查看全部</el-button>
          </div>
          <div v-loading="tasksLoading">
            <el-empty v-if="!tasksLoading && todayTasks.length === 0" description="今日暂无待跟进任务" :image-size="60" />
            <followup-task-card
              v-for="task in todayTasks"
              :key="task.taskId"
              :task="task"
              @complete="loadTasks"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 业绩分布 -->
      <el-col :xs="24" :sm="12">
        <el-card shadow="never" class="section-card">
          <div slot="header">业务类型分布</div>
          <div v-loading="statsLoading">
            <el-empty v-if="!statsLoading && (!stats.byType || stats.byType.length === 0)" description="暂无业绩数据" :image-size="60" />
            <el-table v-else :data="stats.byType || []" size="small" border stripe>
              <el-table-column label="业务类型" prop="businessType" min-width="100" />
              <el-table-column label="订单数" prop="orderCount" width="80" align="center" />
              <el-table-column label="GMV" min-width="120" align="right">
                <template slot-scope="scope">
                  <masked-amount :value="scope.row.gmv" />
                </template>
              </el-table-column>
              <el-table-column label="提成" min-width="120" align="right">
                <template slot-scope="scope">
                  <masked-amount :value="scope.row.commission" />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getDashboard } from '@/api/sales/me/dashboard'
import { listMyTasks } from '@/api/sales/me/task'
import MaskedAmount from '@/components/sales/MaskedAmount.vue'
import FollowupTaskCard from '@/components/sales/FollowupTaskCard.vue'

export default {
  name: 'SalesDashboard',
  components: { MaskedAmount, FollowupTaskCard },
  data() {
    return {
      statsLoading: false,
      tasksLoading: false,
      stats: {},
      todayTasks: []
    }
  },
  created() {
    this.loadStats()
    this.loadTasks()
  },
  methods: {
    loadStats() {
      this.statsLoading = true
      getDashboard().then(res => {
        this.stats = res.data || {}
      }).catch(() => {}).finally(() => { this.statsLoading = false })
    },
    loadTasks() {
      this.tasksLoading = true
      const today = new Date().toISOString().substring(0, 10)
      listMyTasks({ status: 'pending', pageNum: 1, pageSize: 20 }).then(res => {
        const rows = res.rows || []
        this.todayTasks = rows.filter(t => t.dueDate && String(t.dueDate).substring(0, 10) === today)
      }).catch(() => {}).finally(() => { this.tasksLoading = false })
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
.hero-eyebrow {
  margin: 0;
  font-size: 12px;
  color: #409EFF;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.page-hero h2 {
  margin: 4px 0;
  font-size: 22px;
  font-weight: 700;
  color: #172033;
}
.hero-desc {
  margin: 4px 0 0;
  color: #6f7b8f;
}
.stat-row {
  margin-bottom: 0;
}
.stat-card {
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}
.stat-card__label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-card__value {
  font-size: 24px;
  font-weight: 700;
  color: #172033;
}
.section-card {
  height: 100%;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}
.section-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
@media (max-width: 576px) {
  .stat-card__value {
    font-size: 20px;
  }
}
</style>
