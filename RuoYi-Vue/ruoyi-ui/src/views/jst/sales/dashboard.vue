<template>
  <div class="app-container sales-dashboard">
    <div class="dashboard-header">
      <span class="dashboard-header__title">销售业绩看板</span>
      <el-date-picker
        v-model="selectedMonth"
        class="dashboard-header__picker"
        type="month"
        value-format="yyyy-MM"
        placeholder="选择月份"
        @change="loadAll"
      />
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" size="small" class="dashboard-filter">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRangeArr"
            type="daterange"
            range-separator="至"
            start-placeholder="开始"
            end-placeholder="结束"
            value-format="yyyy-MM-dd"
            class="dashboard-filter__range"
            @change="loadAll"
          />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="businessType" clearable placeholder="全部" class="dashboard-filter__select" @change="loadAll">
            <el-option v-for="d in businessTypeDict" :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="region" clearable placeholder="省/市关键字" class="dashboard-filter__region" @change="loadAll" />
        </el-form-item>
      </el-form>
    </el-card>

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
      <el-col :xs="24" :lg="14">
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
          <empty-state-cta
            v-if="!loadingOverview && !salesRankList.length"
            title="暂无业绩数据"
            description="本月尚无成交记录"
            :image-size="60"
          />
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="10">
        <el-card class="chart-card" v-loading="loadingActivity">
          <div slot="header" class="chart-card__header">
            <span>跟进活跃度</span>
          </div>
          <div ref="activityChart" class="chart-canvas" v-show="activityData.length" />
          <empty-state-cta
            v-if="!loadingActivity && !activityData.length"
            title="暂无跟进活跃度"
            description="本月尚无跟进记录"
            :image-size="60"
          />
        </el-card>
      </el-col>
    </el-row>

    <el-card class="metric-card" v-loading="trendLoading">
      <div slot="header" class="metric-card__header">
        <span>提成成本趋势</span>
        <el-radio-group v-model="trendBucket" size="small" @change="loadTrend">
          <el-radio-button label="day">按日</el-radio-button>
          <el-radio-button label="week">按周</el-radio-button>
          <el-radio-button label="month">按月</el-radio-button>
        </el-radio-group>
      </div>

      <div ref="trendChart" class="chart-canvas chart-canvas--wide" v-show="trendHasData" />
      <empty-state-cta
        v-if="!trendLoading && !trendHasData"
        title="暂无成本趋势数据"
        description="当前筛选范围内没有提成或成交记录"
        :image-size="72"
      />

      <div class="trend-summary" v-if="!trendLoading">
        <div class="trend-summary__item">
          <div class="trend-summary__label">区间平均成本率</div>
          <div class="trend-summary__value">{{ formatPercent(trendSummary.averageCostRate) }}</div>
        </div>
        <div class="trend-summary__item">
          <div class="trend-summary__label">环比上周期</div>
          <div class="trend-summary__value" :class="trendChangeClass">
            {{ formatPp(trendSummary.changePp) }}
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="metric-card" v-loading="compressionLoading">
      <div slot="header" class="metric-card__header">
        <span>J 上限压缩触发率</span>
      </div>

      <div class="compression-grid">
        <div class="compression-item">
          <div class="compression-item__label">触发次数 / 总次数</div>
          <div class="compression-item__value">
            {{ compressionStats.triggeredCount || 0 }} / {{ compressionStats.totalCount || 0 }}
          </div>
        </div>
        <div class="compression-item" :class="compressionRateClass">
          <div class="compression-item__label">触发率</div>
          <div class="compression-item__value">{{ formatPercent(compressionStats.triggerRate) }}</div>
        </div>
        <div class="compression-item">
          <div class="compression-item__label">压缩金额</div>
          <div class="compression-item__value">¥{{ formatAmount(compressionStats.compressedAmount) }}</div>
        </div>
        <div class="compression-item">
          <div class="compression-item__label">压缩率</div>
          <div class="compression-item__value">{{ formatPercent(compressionStats.compressedRate) }}</div>
        </div>
      </div>

      <div class="compression-hint">
        压缩率持续大于 10% 说明费率设计偏激进，建议检查 `jst_sales_commission_rate` 的自定义费率配置。
      </div>
    </el-card>

    <el-card class="metric-card" v-loading="heatmapLoading">
      <div slot="header" class="metric-card__header">
        <span>渠道业绩热力图</span>
      </div>

      <el-row :gutter="16">
        <el-col :xs="24" :lg="16">
          <div ref="heatmapChart" class="chart-canvas chart-canvas--heatmap" v-show="heatmapHasData" />
          <empty-state-cta
            v-if="!heatmapLoading && !heatmapHasData"
            title="暂无热力图数据"
            description="当前筛选范围内暂无已支付订单"
            :image-size="72"
          />
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-table :data="heatmapTopList" border stripe size="small" class="heatmap-table">
            <el-table-column label="地区" prop="region" min-width="100" show-overflow-tooltip>
              <template slot-scope="scope">{{ regionLabel(scope.row.region) }}</template>
            </el-table-column>
            <el-table-column label="业务类型" min-width="100" show-overflow-tooltip>
              <template slot-scope="scope">{{ businessTypeLabel(scope.row.businessType) }}</template>
            </el-table-column>
            <el-table-column label="GMV(元)" min-width="100" align="right">
              <template slot-scope="scope">{{ formatAmount(scope.row.gmv) }}</template>
            </el-table-column>
            <el-table-column label="渠道数" prop="channelCount" width="80" align="center" />
          </el-table>
        </el-col>
      </el-row>
    </el-card>

    <el-drawer title="销售月度明细" :visible.sync="detailOpen" :size="drawerSize" direction="rtl">
      <div class="detail-drawer" v-loading="loadingDetail">
        <el-descriptions :column="1" border size="medium" v-if="salesDetail">
          <el-descriptions-item label="销售姓名">{{ salesDetail.salesName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="成交笔数">{{ salesDetail.orderCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="GMV">¥{{ formatAmount(salesDetail.gmv || salesDetail.totalGmv) }}</el-descriptions-item>
          <el-descriptions-item label="提成金额">¥{{ formatAmount(salesDetail.commissionAmount || salesDetail.totalCommission) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import {
  getDashboardOverview,
  getSalesDetail,
  getFollowupActivity,
  getCommissionTrend,
  getCompressionStats,
  getChannelHeatmap
} from '@/api/admin/sales/dashboard'
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
      trendBucket: 'day',
      trendData: [],
      trendSummary: {
        averageCostRate: 0,
        previousAverageCostRate: null,
        changePp: null
      },
      compressionStats: {
        totalCount: 0,
        triggeredCount: 0,
        triggerRate: 0,
        compressedAmount: 0,
        originalTotal: 0,
        compressedRate: 0
      },
      heatmapData: [],
      // PATCH-5: 省级行政区字典，用于热力图 region 拼音 → 中文翻译
      regionDict: [],
      loadingOverview: false,
      loadingActivity: false,
      trendLoading: false,
      compressionLoading: false,
      heatmapLoading: false,
      loadingDetail: false,
      detailOpen: false,
      salesDetail: null,
      activityChart: null,
      trendChart: null,
      heatmapChart: null
    }
  },
  computed: {
    drawerSize() {
      return window.innerWidth <= 768 ? '100%' : '480px'
    },
    trendHasData() {
      return Array.isArray(this.trendData) && this.trendData.length > 0
    },
    heatmapHasData() {
      return Array.isArray(this.heatmapData) && this.heatmapData.length > 0
    },
    compressionRateClass() {
      const rate = Number(this.compressionStats.triggerRate || 0)
      if (rate > 0.1) return 'compression-item--danger'
      if (rate >= 0.05) return 'compression-item--warning'
      return 'compression-item--safe'
    },
    trendChangeClass() {
      if (this.trendSummary.changePp == null) return ''
      if (this.trendSummary.changePp > 0) return 'trend-summary__value--danger'
      if (this.trendSummary.changePp < 0) return 'trend-summary__value--safe'
      return ''
    },
    heatmapTopList() {
      return (this.heatmapData || [])
        .slice()
        .sort((a, b) => Number(b.gmv || 0) - Number(a.gmv || 0))
        .slice(0, 10)
    }
  },
  created() {
    this.loadAll()
    this.getDicts('jst_sales_business_type').then(res => {
      this.businessTypeDict = (res && res.data) || []
    }).catch(() => {
      this.businessTypeDict = []
    })
    // PATCH-5: 拉取省级行政区字典
    this.getDicts('jst_region_province').then(res => {
      this.regionDict = (res && res.data) || []
    }).catch(() => {
      this.regionDict = []
    })
  },
  mounted() {
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.disposeChart('activityChart')
    this.disposeChart('trendChart')
    this.disposeChart('heatmapChart')
  },
  methods: {
    currentMonth() {
      const d = new Date()
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
    },
    loadAll() {
      this.loadOverview()
      this.loadActivity()
      this.loadTrend()
      this.loadCompression()
      this.loadHeatmap()
    },
    loadOverview() {
      this.loadingOverview = true
      getDashboardOverview(this.selectedMonth).then(res => {
        const data = (res && res.data) || {}
        this.overview = data
        this.salesRankList = data.salesRank || data.salesList || []
      }).catch(() => {
        this.overview = {}
        this.salesRankList = []
      }).finally(() => {
        this.loadingOverview = false
      })
    },
    loadActivity() {
      this.loadingActivity = true
      getFollowupActivity(this.selectedMonth).then(res => {
        this.activityData = (res && res.data) || []
        this.$nextTick(() => this.renderActivityChart())
      }).catch(() => {
        this.activityData = []
        this.renderActivityChart()
      }).finally(() => {
        this.loadingActivity = false
      })
    },
    loadTrend() {
      const currentParams = this.buildMetricParams({ bucket: this.trendBucket })
      const previousRange = this.shiftRange(currentParams.startDate, currentParams.endDate)
      const previousParams = Object.assign({}, currentParams, previousRange)
      this.trendLoading = true
      Promise.all([
        getCommissionTrend(currentParams),
        getCommissionTrend(previousParams)
      ]).then(([currentRes, previousRes]) => {
        this.trendData = (currentRes && currentRes.data) || []
        const previousData = (previousRes && previousRes.data) || []
        const currentAverage = this.calculateAverageCostRate(this.trendData)
        const previousAverage = previousData.length ? this.calculateAverageCostRate(previousData) : null
        this.trendSummary = {
          averageCostRate: currentAverage,
          previousAverageCostRate: previousAverage,
          changePp: previousAverage == null ? null : Number(((currentAverage - previousAverage) * 100).toFixed(2))
        }
        this.$nextTick(() => this.renderTrendChart())
      }).catch(() => {
        this.trendData = []
        this.trendSummary = {
          averageCostRate: 0,
          previousAverageCostRate: null,
          changePp: null
        }
        this.renderTrendChart()
      }).finally(() => {
        this.trendLoading = false
      })
    },
    loadCompression() {
      this.compressionLoading = true
      getCompressionStats(this.buildMetricParams()).then(res => {
        const data = (res && res.data) || {}
        this.compressionStats = Object.assign({
          totalCount: 0,
          triggeredCount: 0,
          triggerRate: 0,
          compressedAmount: 0,
          originalTotal: 0,
          compressedRate: 0
        }, data)
      }).catch(() => {
        this.compressionStats = {
          totalCount: 0,
          triggeredCount: 0,
          triggerRate: 0,
          compressedAmount: 0,
          originalTotal: 0,
          compressedRate: 0
        }
      }).finally(() => {
        this.compressionLoading = false
      })
    },
    loadHeatmap() {
      this.heatmapLoading = true
      getChannelHeatmap(this.buildMetricParams()).then(res => {
        this.heatmapData = (res && res.data) || []
        this.$nextTick(() => this.renderHeatmapChart())
      }).catch(() => {
        this.heatmapData = []
        this.renderHeatmapChart()
      }).finally(() => {
        this.heatmapLoading = false
      })
    },
    renderActivityChart() {
      if (!this.$refs.activityChart) return
      if (!this.activityChart) {
        this.activityChart = echarts.init(this.$refs.activityChart)
      }
      if (!this.activityData.length) {
        this.activityChart.clear()
        return
      }
      const names = this.activityData.map(item => item.salesName || item.name || '')
      const counts = this.activityData.map(item => Number(item.followupCount || item.count || 0))
      this.activityChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '8%', containLabel: true },
        xAxis: {
          type: 'category',
          data: names,
          axisLabel: {
            interval: 0,
            rotate: counts.length > 5 ? 30 : 0
          }
        },
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
    renderTrendChart() {
      if (!this.$refs.trendChart) return
      if (!this.trendChart) {
        this.trendChart = echarts.init(this.$refs.trendChart)
      }
      if (!this.trendData.length) {
        this.trendChart.clear()
        return
      }
      const buckets = this.trendData.map(item => item.bucket)
      const commissionData = this.trendData.map(item => Number(item.commissionTotal || 0))
      const costRateData = this.trendData.map(item => Number(item.costRate || 0) * 100)
      this.trendChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: params => {
            const list = Array.isArray(params) ? params : [params]
            const title = list.length ? list[0].axisValueLabel : ''
            const lines = [title]
            list.forEach(item => {
              const suffix = item.seriesName === '成本率' ? '%' : '元'
              lines.push(`${item.marker}${item.seriesName}：${this.formatNumber(item.value)}${suffix}`)
            })
            return lines.join('<br/>')
          }
        },
        legend: { data: ['提成总额', '成本率'] },
        grid: { left: '4%', right: '5%', bottom: '8%', containLabel: true },
        xAxis: {
          type: 'category',
          data: buckets,
          axisLabel: {
            interval: 0,
            rotate: buckets.length > 8 ? 30 : 0
          }
        },
        yAxis: [
          {
            type: 'value',
            name: '提成总额(元)'
          },
          {
            type: 'value',
            name: '成本率(%)',
            axisLabel: {
              formatter: '{value}%'
            }
          }
        ],
        series: [
          {
            name: '提成总额',
            type: 'bar',
            barMaxWidth: 28,
            data: commissionData,
            itemStyle: {
              color: '#67C23A',
              borderRadius: [4, 4, 0, 0]
            }
          },
          {
            name: '成本率',
            type: 'line',
            yAxisIndex: 1,
            smooth: true,
            data: costRateData,
            symbolSize: 8,
            itemStyle: { color: '#E6A23C' },
            lineStyle: { width: 2 }
          }
        ]
      })
    },
    renderHeatmapChart() {
      if (!this.$refs.heatmapChart) return
      if (!this.heatmapChart) {
        this.heatmapChart = echarts.init(this.$refs.heatmapChart)
      }
      if (!this.heatmapData.length) {
        this.heatmapChart.clear()
        return
      }
      const businessAxis = this.getHeatmapBusinessAxis()
      const regionAxis = this.getHeatmapRegionAxis()
      const dataIndexMap = {}
      this.heatmapData.forEach(item => {
        dataIndexMap[`${item.region}__${item.businessType}`] = item
      })
      const seriesData = this.heatmapData.map(item => [
        businessAxis.indexOf(item.businessType),
        regionAxis.indexOf(item.region),
        Number(item.gmv || 0)
      ]).filter(item => item[0] >= 0 && item[1] >= 0)
      const maxValue = seriesData.reduce((max, item) => Math.max(max, item[2]), 0)
      this.heatmapChart.setOption({
        tooltip: {
          position: 'top',
          formatter: params => {
            const business = businessAxis[params.data[0]]
            const region = regionAxis[params.data[1]]
            const raw = dataIndexMap[`${region}__${business}`] || {}
            // PATCH-5: 翻译 region 拼音 → 中文
            return `${this.regionLabel(region)} × ${this.businessTypeLabel(business)}<br/>GMV：¥${this.formatAmount(raw.gmv)}<br/>渠道数：${raw.channelCount || 0}`
          }
        },
        grid: { left: '5%', right: '8%', top: '12%', bottom: '10%', containLabel: true },
        xAxis: {
          type: 'category',
          data: businessAxis.map(item => this.businessTypeLabel(item)),
          splitArea: { show: true },
          axisLabel: { interval: 0 }
        },
        yAxis: {
          type: 'category',
          // PATCH-5: y 轴展示中文省份名（内部仍以 regionAxis 拼音索引）
          data: regionAxis.map(r => this.regionLabel(r)),
          splitArea: { show: true }
        },
        visualMap: {
          min: 0,
          max: maxValue || 1,
          calculable: true,
          orient: 'horizontal',
          left: 'center',
          bottom: 0,
          inRange: {
            color: ['#f7fbff', '#9ecae1', '#3182bd']
          }
        },
        series: [{
          type: 'heatmap',
          data: seriesData,
          label: {
            show: true,
            formatter: params => {
              return params.data[2] ? this.formatCompactAmount(params.data[2]) : '-'
            }
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.3)'
            }
          }
        }]
      })
    },
    buildMetricParams(extra) {
      const range = this.getMetricRange()
      return Object.assign({
        startDate: range.startDate,
        endDate: range.endDate,
        businessType: this.businessType || undefined,
        region: this.region || undefined
      }, extra || {})
    },
    getMetricRange() {
      if (Array.isArray(this.dateRangeArr) && this.dateRangeArr.length === 2) {
        return {
          startDate: this.dateRangeArr[0],
          endDate: this.dateRangeArr[1]
        }
      }
      const month = this.selectedMonth || this.currentMonth()
      const parts = month.split('-')
      const year = Number(parts[0])
      const monthIndex = Number(parts[1])
      const lastDay = new Date(year, monthIndex, 0).getDate()
      return {
        startDate: `${month}-01`,
        endDate: `${month}-${String(lastDay).padStart(2, '0')}`
      }
    },
    shiftRange(startDate, endDate) {
      const start = new Date(`${startDate}T00:00:00`)
      const end = new Date(`${endDate}T00:00:00`)
      const days = Math.round((end.getTime() - start.getTime()) / 86400000) + 1
      const previousEnd = new Date(start)
      previousEnd.setDate(previousEnd.getDate() - 1)
      const previousStart = new Date(previousEnd)
      previousStart.setDate(previousStart.getDate() - days + 1)
      return {
        startDate: this.formatDate(previousStart),
        endDate: this.formatDate(previousEnd)
      }
    },
    calculateAverageCostRate(rows) {
      const commissionTotal = rows.reduce((sum, item) => sum + Number(item.commissionTotal || 0), 0)
      const orderNetTotal = rows.reduce((sum, item) => sum + Number(item.orderNetTotal || 0), 0)
      if (!orderNetTotal) return 0
      return commissionTotal / orderNetTotal
    },
    getHeatmapBusinessAxis() {
      if (this.businessType) return [this.businessType]
      const dictValues = (this.businessTypeDict || []).map(item => item.value)
      if (dictValues.length) return dictValues
      return Array.from(new Set((this.heatmapData || []).map(item => item.businessType))).filter(Boolean)
    },
    getHeatmapRegionAxis() {
      const regionTotals = {}
      ;(this.heatmapData || []).forEach(item => {
        const key = item.region || '未设置'
        regionTotals[key] = (regionTotals[key] || 0) + Number(item.gmv || 0)
      })
      return Object.keys(regionTotals).sort((a, b) => regionTotals[b] - regionTotals[a])
    },
    businessTypeLabel(value) {
      const hit = (this.businessTypeDict || []).find(item => item.value === value)
      return hit ? hit.label : (value || '--')
    },
    // PATCH-5: region 拼音 value → 中文 label（字典外或空值兜底 "未设置"/"--"）
    regionLabel(value) {
      if (!value) return '未设置'
      const hit = (this.regionDict || []).find(item => item.value === value)
      return hit ? hit.label : value
    },
    handleResize() {
      ;['activityChart', 'trendChart', 'heatmapChart'].forEach(key => {
        if (this[key]) this[key].resize()
      })
    },
    disposeChart(key) {
      if (this[key]) {
        this[key].dispose()
        this[key] = null
      }
    },
    viewSalesDetail(row) {
      this.detailOpen = true
      this.loadingDetail = true
      getSalesDetail(row.salesId, this.selectedMonth).then(res => {
        this.salesDetail = (res && res.data) || {}
      }).catch(() => {
        this.salesDetail = null
      }).finally(() => {
        this.loadingDetail = false
      })
    },
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    formatAmount(value) {
      if (value == null || value === '') return '0.00'
      return Number(value).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },
    formatCompactAmount(value) {
      const amount = Number(value || 0)
      if (amount >= 10000) {
        return `${(amount / 10000).toFixed(1)}w`
      }
      return this.formatNumber(amount)
    },
    formatNumber(value) {
      return Number(value || 0).toFixed(2).replace(/\.00$/, '')
    },
    formatPercent(value) {
      if (value == null || value === '') return '--'
      return `${(Number(value) * 100).toFixed(2)}%`
    },
    formatPp(value) {
      if (value == null || Number.isNaN(Number(value))) return '--'
      const num = Number(value)
      const prefix = num > 0 ? '+' : ''
      return `${prefix}${num.toFixed(2)} pp`
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
  gap: 12px;
}

.dashboard-header__title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.dashboard-header__picker {
  width: 160px;
}

.filter-card {
  margin-bottom: 12px;
}

.dashboard-filter__range {
  width: 240px;
}

.dashboard-filter__select,
.dashboard-filter__region {
  width: 160px;
}

.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  text-align: center;
  margin-bottom: 12px;
}

.stat-card__num {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-card--green .stat-card__num {
  color: #67c23a;
}

.stat-card--orange .stat-card__num {
  color: #e6a23c;
}

.stat-card__label {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}

.chart-row .chart-card,
.metric-card {
  margin-bottom: 16px;
}

.chart-card__header,
.metric-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.chart-canvas {
  height: 300px;
}

.chart-canvas--wide {
  height: 360px;
}

.chart-canvas--heatmap {
  height: 420px;
}

.trend-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.trend-summary__item {
  padding: 14px 16px;
  border-radius: 8px;
  background: #f8fafc;
  border: 1px solid #ebeef5;
}

.trend-summary__label {
  font-size: 12px;
  color: #909399;
}

.trend-summary__value {
  margin-top: 8px;
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.trend-summary__value--danger {
  color: #f56c6c;
}

.trend-summary__value--safe {
  color: #67c23a;
}

.compression-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.compression-item {
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background: #fff;
}

.compression-item__label {
  font-size: 12px;
  color: #909399;
}

.compression-item__value {
  margin-top: 10px;
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.compression-item--danger {
  border-color: rgba(245, 108, 108, 0.3);
  background: rgba(254, 240, 240, 0.7);
}

.compression-item--danger .compression-item__value {
  color: #f56c6c;
}

.compression-item--warning {
  border-color: rgba(230, 162, 60, 0.3);
  background: rgba(253, 246, 236, 0.8);
}

.compression-item--warning .compression-item__value {
  color: #e6a23c;
}

.compression-item--safe {
  border-color: rgba(103, 194, 58, 0.3);
  background: rgba(240, 249, 235, 0.8);
}

.compression-item--safe .compression-item__value {
  color: #67c23a;
}

.compression-hint {
  margin-top: 16px;
  padding: 12px 14px;
  border-radius: 8px;
  background: #fff7e6;
  color: #8c6d1f;
  font-size: 13px;
  line-height: 1.6;
}

.heatmap-table {
  margin-top: 8px;
}

.detail-drawer {
  padding: 20px;
}

@media (max-width: 992px) {
  .compression-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .dashboard-header__picker,
  .dashboard-filter__range,
  .dashboard-filter__select,
  .dashboard-filter__region {
    width: 100%;
  }

  .chart-canvas--wide,
  .chart-canvas--heatmap {
    height: 320px;
  }

  .trend-summary {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .stat-card__num,
  .compression-item__value,
  .trend-summary__value {
    font-size: 20px;
  }

  .compression-grid {
    grid-template-columns: 1fr;
  }
}
</style>
