<template>
  <div class="app-container manager-dashboard">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">销售主管</p>
        <h2>团队看板</h2>
        <p class="hero-desc">你的下属业绩状况、团队健康度都在这里。</p>
      </div>
    </div>

    <!-- 日期范围筛选 -->
    <el-card class="filter-card" shadow="never" style="margin-bottom: 12px">
      <el-radio-group v-model="dateRange" @change="load">
        <el-radio-button label="7">近 7 天</el-radio-button>
        <el-radio-button label="30">近 30 天</el-radio-button>
        <el-radio-button label="thisMonth">本月</el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row" v-loading="loading">
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">团队人数</div>
          <div class="stat-card__value">{{ overview.teamCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-card--green">
          <div class="stat-card__label">本月团队订单</div>
          <div class="stat-card__value">{{ overview.orderCount || 0 }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-card--orange">
          <div class="stat-card__label">本月团队 GMV</div>
          <div class="stat-card__value">¥{{ formatAmount(overview.gmv) }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">本月跟进活跃度</div>
          <div class="stat-card__value">{{ overview.followupCount || 0 }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <!-- 销售排行 -->
      <el-col :xs="24" :sm="14">
        <el-card shadow="never" v-loading="loading">
          <div slot="header">下属销售业绩排行</div>
          <el-table :data="ranking" size="small" border stripe>
            <el-table-column label="销售姓名" prop="salesName" show-overflow-tooltip />
            <el-table-column label="订单" prop="orderCount" width="80" align="center" />
            <el-table-column label="GMV" align="right" min-width="110">
              <template slot-scope="scope">¥{{ formatAmount(scope.row.gmv) }}</template>
            </el-table-column>
            <el-table-column label="提成" align="right" min-width="110">
              <template slot-scope="scope">¥{{ formatAmount(scope.row.commissionAmount) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click="viewSales(scope.row)">明细</el-button>
              </template>
            </el-table-column>
          </el-table>
          <empty-state-cta v-if="!loading && !ranking.length" title="还没有团队成员业绩" description="本月尚无成交记录" :image-size="60" />
        </el-card>
      </el-col>

      <!-- 客户标签分布 -->
      <el-col :xs="24" :sm="10">
        <el-card shadow="never">
          <div slot="header">团队客户类型分布</div>
          <div ref="barChart" style="height: 260px" />
          <empty-state-cta v-if="!loading && !(overview.tagDistribution || []).length" title="暂无标签分布" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 健康度告警 -->
    <el-card v-if="healthAlerts.length" style="margin-top: 16px">
      <div slot="header">
        <i class="el-icon-warning" style="color:#f56c6c"></i>
        团队健康度告警（{{ healthAlerts.length }}）
      </div>
      <div v-for="a in healthAlerts" :key="a.salesId" class="alert-row">
        <span class="alert-msg">
          <i class="el-icon-warning-outline" style="color:#e6a23c; margin-right:4px"></i>
          {{ a.message }}
        </span>
        <el-button size="mini" type="text" @click="viewSalesById(a.salesId)">查看销售</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getManagerDashboard } from '@/api/sales/manager/dashboard'
import * as echarts from 'echarts'

export default {
  name: 'ManagerDashboard',
  data() {
    return {
      dateRange: 'thisMonth',
      loading: false,
      overview: {},
      ranking: [],
      healthAlerts: [],
      barChart: null
    }
  },
  created() {
    this.load()
  },
  beforeDestroy() {
    if (this.barChart) this.barChart.dispose()
  },
  methods: {
    load() {
      this.loading = true
      const month = this.computeMonth()
      getManagerDashboard(month).then(res => {
        const data = res.data || {}
        this.overview = data.overview || {}
        this.ranking = data.ranking || []
        this.healthAlerts = data.healthAlerts || []
        this.$nextTick(() => this.renderBar())
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    computeMonth() {
      if (this.dateRange === 'thisMonth') {
        const now = new Date()
        return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
      }
      return null
    },
    renderBar() {
      if (!this.$refs.barChart) return
      const data = this.overview.tagDistribution || []
      if (!data.length) return
      if (!this.barChart) this.barChart = echarts.init(this.$refs.barChart)
      this.barChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '8%', containLabel: true },
        xAxis: {
          type: 'category',
          data: data.map(x => x.name),
          axisLabel: { interval: 0, rotate: data.length > 5 ? 30 : 0 }
        },
        yAxis: { type: 'value' },
        series: [{
          type: 'bar',
          data: data.map(x => x.count),
          itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] }
        }]
      })
    },
    viewSales(row) {
      this.$router.push(`/jst/sales/${row.salesId}`)
    },
    viewSalesById(id) {
      this.$router.push(`/jst/sales/${id}`)
    },
    formatAmount(v) {
      if (v == null) return '0.00'
      return Number(v).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
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
.stat-card--green .stat-card__value { color: #67C23A; }
.stat-card--orange .stat-card__value { color: #E6A23C; }
.alert-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-bottom: 1px solid #f0f2f5;
  background: #fffbe6;
  border-radius: 4px;
  margin-bottom: 6px;
}
.alert-row:last-child { border-bottom: none; }
.alert-msg { font-size: 13px; color: #606266; }
@media (max-width: 576px) {
  .stat-card__value { font-size: 20px; }
}
</style>
