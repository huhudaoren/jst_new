<template>
  <div class="app-container">
    <!-- 月份选择 -->
    <div class="dashboard-header">
      <span class="dashboard-header__title">销售业绩看板</span>
      <el-date-picker
        v-model="selectedMonth"
        type="month"
        value-format="yyyy-MM"
        placeholder="选择月份"
        style="width: 160px;"
        @change="loadAll"
      />
    </div>

    <!-- 扩展筛选器 -->
    <el-card class="filter-card" shadow="never" style="margin-bottom: 12px">
      <el-form :inline="true" size="small">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRangeArr"
            type="daterange"
            range-separator="至"
            start-placeholder="开始"
            end-placeholder="结束"
            value-format="yyyy-MM-dd"
            style="width: 240px"
            @change="loadAll"
          />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="businessType" clearable placeholder="全部" @change="loadAll" style="width: 140px">
            <el-option v-for="d in businessTypeDict" :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="region" placeholder="省/市关键字" clearable @change="loadAll" style="width: 160px" />
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 4 stat-card -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__num">{{ overview.activeSalesCount || 0 }}</div>
          <div class="stat-card__label">销售在职数</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__num">{{ overview.monthOrderCount || 0 }}</div>
          <div class="stat-card__label">本月订单数</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-card--green">
          <div class="stat-card__num">¥{{ formatAmount(overview.monthGmv) }}</div>
          <div class="stat-card__label">本月 GMV</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-card--orange">
          <div class="stat-card__num">¥{{ formatAmount(overview.monthCommissionCost) }}</div>
          <div class="stat-card__label">本月提成成本</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <!-- 销售业绩排行 -->
      <el-col :xs="24" :sm="14">
        <el-card class="chart-card" v-loading="loadingOverview">
          <div slot="header" class="chart-card__header">
            <span>销售业绩排行</span>
          </div>
          <el-table :data="salesRankList" border stripe size="small">
            <el-table-column label="销售姓名" prop="salesName" min-width="90" show-overflow-tooltip />
            <el-table-column label="成交笔数" prop="orderCount" width="90" align="center" />
            <el-table-column label="GMV(元)" prop="gmv" width="110" align="right">
              <template slot-scope="scope">{{ formatAmount(scope.row.gmv) }}</template>
            </el-table-column>
            <el-table-column label="提成(元)" prop="commissionAmount" width="110" align="right">
              <template slot-scope="scope">{{ formatAmount(scope.row.commissionAmount) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="viewSalesDetail(scope.row)">明细</el-button>
              </template>
            </el-table-column>
          </el-table>
          <empty-state-cta v-if="!loadingOverview && !salesRankList.length" title="暂无业绩数据" description="本月尚无成交记录" :image-size="60" />
        </el-card>
      </el-col>

      <!-- 跟进活跃度 echarts bar -->
      <el-col :xs="24" :sm="10">
        <el-card class="chart-card" v-loading="loadingActivity">
          <div slot="header" class="chart-card__header">
            <span>跟进活跃度</span>
          </div>
          <div ref="barChart" style="height: 300px;" />
          <empty-state-cta v-if="!loadingActivity && !activityData.length" title="暂无跟进活跃度" description="本月尚无跟进记录" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 销售明细抽屉 -->
    <el-drawer title="销售月度明细" :visible.sync="detailOpen" :size="drawerSize" direction="rtl">
      <div style="padding: 20px;" v-loading="loadingDetail">
        <el-descriptions :column="1" border size="medium" v-if="salesDetail">
          <el-descriptions-item label="销售姓名">{{ salesDetail.salesName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="成交笔数">{{ salesDetail.orderCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="GMV">¥{{ formatAmount(salesDetail.gmv) }}</el-descriptions-item>
          <el-descriptions-item label="提成金额">¥{{ formatAmount(salesDetail.commissionAmount) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { getDashboardOverview, getSalesDetail, getFollowupActivity } from '@/api/admin/sales/dashboard'
import * as echarts from 'echarts'

export default {
  name: 'SalesDashboard',
  data() {
    return {
      selectedMonth: this.currentMonth(),
      dateRangeArr: [],
      businessType: '',
      region: '',
      businessTypeDict: [],
      overview: {},
      salesRankList: [],
      activityData: [],
      loadingOverview: false,
      loadingActivity: false,
      loadingDetail: false,
      detailOpen: false,
      salesDetail: null,
      chart: null
    }
  },
  computed: {
    drawerSize() {
      return window.innerWidth <= 768 ? '100%' : '480px'
    }
  },
  created() {
    this.loadAll()
    this.getDicts('jst_sales_business_type').then(r => { this.businessTypeDict = r.data || [] }).catch(() => {})
  },
  beforeDestroy() {
    if (this.chart) this.chart.dispose()
  },
  methods: {
    currentMonth() {
      const d = new Date()
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
    },
    loadAll() {
      this.loadOverview()
      this.loadActivity()
    },
    loadOverview() {
      this.loadingOverview = true
      getDashboardOverview(this.selectedMonth).then(res => {
        const data = (res && res.data) || {}
        this.overview = data
        this.salesRankList = data.salesRank || data.salesList || []
      }).catch(() => {}).finally(() => { this.loadingOverview = false })
    },
    loadActivity() {
      this.loadingActivity = true
      getFollowupActivity(this.selectedMonth).then(res => {
        this.activityData = (res && res.data) || []
        this.$nextTick(() => this.renderChart())
      }).catch(() => {}).finally(() => { this.loadingActivity = false })
    },
    renderChart() {
      if (!this.$refs.barChart) return
      if (!this.chart) {
        this.chart = echarts.init(this.$refs.barChart)
      }
      const names = this.activityData.map(d => d.salesName || d.name || '')
      const counts = this.activityData.map(d => d.followupCount || d.count || 0)
      this.chart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '8%', containLabel: true },
        xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: counts.length > 5 ? 30 : 0 } },
        yAxis: { type: 'value', name: '跟进次数' },
        series: [{
          name: '跟进次数',
          type: 'bar',
          data: counts,
          itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] },
          label: { show: true, position: 'top', formatter: '{c}' }
        }]
      })
    },
    viewSalesDetail(row) {
      this.detailOpen = true
      this.loadingDetail = true
      getSalesDetail(row.salesId, this.selectedMonth).then(res => {
        this.salesDetail = (res && res.data) || {}
      }).catch(() => { this.salesDetail = null }).finally(() => { this.loadingDetail = false })
    },
    formatAmount(v) {
      if (v == null) return '0.00'
      return Number(v).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }
  }
}
</script>

<style scoped>
.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.dashboard-header__title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.stat-row { margin-bottom: 16px; }
.stat-card {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: center;
  margin-bottom: 12px;
}
.stat-card__num {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}
.stat-card--green .stat-card__num { color: #67C23A; }
.stat-card--orange .stat-card__num { color: #E6A23C; }
.stat-card__label { margin-top: 6px; font-size: 13px; color: #909399; }
.chart-row .chart-card { margin-bottom: 16px; }
.chart-card__header { font-size: 14px; font-weight: 600; color: #303133; }
@media (max-width: 768px) {
  .stat-card__num { font-size: 22px; }
}
</style>
