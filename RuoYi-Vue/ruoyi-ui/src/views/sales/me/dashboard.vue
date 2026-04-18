<template>
  <div class="app-container sales-dashboard">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">销售工作台</p>
        <h2>工作台首页</h2>
        <p class="hero-desc">你的业绩一目了然，今日待跟进都在下面。</p>
      </div>
    </div>

    <!-- 日期范围筛选 -->
    <el-card class="filter-card" shadow="never" style="margin-bottom: 12px">
      <el-radio-group v-model="dateRange" @change="load">
        <el-radio-button label="7">近 7 天</el-radio-button>
        <el-radio-button label="30">近 30 天</el-radio-button>
        <el-radio-button label="thisMonth">本月</el-radio-button>
        <el-radio-button label="lastMonth">上月</el-radio-button>
      </el-radio-group>
    </el-card>

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
        <div class="stat-card stat-card--orange">
          <div class="stat-card__label">今日待跟进</div>
          <div class="stat-card__value">{{ todayTasks.length }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-card--red">
          <div class="stat-card__label">超期未跟进</div>
          <div class="stat-card__value">{{ inactiveChannels.length }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:16px">
      <!-- 今日待跟进 -->
      <el-col :xs="24" :sm="14">
        <el-card shadow="never" class="section-card">
          <div slot="header" class="section-card__header">
            <span>今日待跟进任务</span>
            <el-button type="text" @click="$router.push('/sales-workbench/tasks')">查看全部</el-button>
          </div>
          <div v-loading="tasksLoading">
            <empty-state-cta v-if="!tasksLoading && todayTasks.length === 0" title="今日无待跟进任务" description="放松心情，业绩稳如泰山" :image-size="60" />
            <followup-task-card
              v-for="task in todayTasks"
              :key="task.taskId"
              :task="task"
              @complete="load"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 业绩分布饼图 -->
      <el-col :xs="24" :sm="10">
        <el-card shadow="never" class="section-card">
          <div slot="header">本月业务类型分布</div>
          <div v-loading="statsLoading">
            <div ref="pieChart" style="height: 260px" />
            <empty-state-cta v-if="!statsLoading && (!stats.byType || stats.byType.length === 0)" title="暂无业绩数据" description="本月尚无成交记录" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 超期未跟进渠道 -->
    <el-card v-if="inactiveChannels.length" style="margin-top: 16px">
      <div slot="header">
        <i class="el-icon-warning" style="color:#e6a23c"></i>
        超过 14 天未跟进的渠道（{{ inactiveChannels.length }} 个）
      </div>
      <el-table :data="inactiveChannels" size="small">
        <el-table-column label="渠道名称" prop="channelName" show-overflow-tooltip />
        <el-table-column label="最后联系时间" prop="lastFollowupAt" />
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="goFollowup(scope.row)">去跟进</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 7 天内到期预录入 -->
    <el-card v-if="expiringPreReg.length" style="margin-top: 16px">
      <div slot="header">
        <i class="el-icon-alarm-clock" style="color:#f56c6c"></i>
        7 天内到期的预录入（{{ expiringPreReg.length }} 个）
      </div>
      <el-table :data="expiringPreReg" size="small">
        <el-table-column label="手机号" prop="phone" width="140" />
        <el-table-column label="联系人" prop="targetName" />
        <el-table-column label="过期时间" prop="expireAt" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getMyPerformance } from '@/api/sales/me/performance'
import { getReminders } from '@/api/sales/me/reminder'
import { listMyTasks } from '@/api/sales/me/task'
import MaskedAmount from '@/components/sales/MaskedAmount.vue'
import FollowupTaskCard from '@/components/sales/FollowupTaskCard.vue'
import * as echarts from 'echarts'

export default {
  name: 'SalesDashboard',
  components: { MaskedAmount, FollowupTaskCard },
  data() {
    return {
      dateRange: 'thisMonth',
      statsLoading: false,
      tasksLoading: false,
      stats: {},
      todayTasks: [],
      inactiveChannels: [],
      expiringPreReg: [],
      pieChart: null
    }
  },
  created() {
    this.load()
  },
  beforeDestroy() {
    if (this.pieChart) this.pieChart.dispose()
  },
  methods: {
    load() {
      this.loadStats()
      this.loadTasks()
      this.loadReminders()
    },
    loadStats() {
      this.statsLoading = true
      const { month } = this.computeRange()
      getMyPerformance(month).then(res => {
        this.stats = res.data || {}
        this.$nextTick(() => this.renderPie())
      }).catch(() => {}).finally(() => { this.statsLoading = false })
    },
    loadTasks() {
      this.tasksLoading = true
      const today = new Date().toISOString().substring(0, 10)
      listMyTasks({ status: 'pending', pageNum: 1, pageSize: 20 }).then(res => {
        const rows = res.rows || []
        this.todayTasks = rows.filter(t => t.dueDate && String(t.dueDate).substring(0, 10) === today)
      }).catch(() => {}).finally(() => { this.tasksLoading = false })
    },
    loadReminders() {
      getReminders().then(res => {
        const data = res.data || {}
        this.inactiveChannels = data.inactiveChannels || []
        this.expiringPreReg = data.expiringPreReg || []
      }).catch(() => {})
    },
    computeRange() {
      const now = new Date()
      if (this.dateRange === 'thisMonth') {
        return { month: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}` }
      }
      if (this.dateRange === 'lastMonth') {
        const lm = new Date(now.getFullYear(), now.getMonth() - 1, 1)
        return { month: `${lm.getFullYear()}-${String(lm.getMonth() + 1).padStart(2, '0')}` }
      }
      return { days: Number(this.dateRange) }
    },
    renderPie() {
      if (!this.$refs.pieChart) return
      const byType = this.stats.byType || []
      if (!byType.length) return
      if (!this.pieChart) this.pieChart = echarts.init(this.$refs.pieChart)
      const data = byType.map(x => ({ name: x.businessType, value: x.orderCount || 0 }))
      this.pieChart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{ type: 'pie', data, radius: ['40%', '65%'] }]
      })
    },
    goFollowup(row) {
      this.$router.push(`/sales-workbench/channels/${row.channelId}`)
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
.stat-row { margin-bottom: 0; }
.stat-card {
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  margin-bottom: 12px;
}
.stat-card__label { font-size: 13px; color: #909399; margin-bottom: 8px; }
.stat-card__value { font-size: 24px; font-weight: 700; color: #172033; }
.stat-card--orange .stat-card__value { color: #E6A23C; }
.stat-card--red .stat-card__value { color: #F56C6C; }
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
  .stat-card__value { font-size: 20px; }
}
</style>
