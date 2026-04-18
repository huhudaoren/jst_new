<template>
  <div class="app-container">
    <el-page-header @back="goBack" content="渠道画像" style="margin-bottom:20px" />

    <el-row :gutter="16" v-loading="loading">
      <el-col :xs="8" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">跟进次数</div>
          <div class="stat-card__value">{{ profile.followupCount != null ? profile.followupCount : '--' }}</div>
        </div>
      </el-col>
      <el-col :xs="8" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">成交笔数</div>
          <div class="stat-card__value">{{ profile.orderCount != null ? profile.orderCount : '--' }}</div>
        </div>
      </el-col>
      <el-col :xs="8" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">覆盖业务类型</div>
          <div class="stat-card__value">{{ profile.bizTypeCount != null ? profile.bizTypeCount : '--' }}</div>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top:16px" v-loading="loading">
      <div slot="header">活跃度趋势（近 6 个月跟进次数）</div>
      <div v-if="!loading && hasTrend">
        <div ref="barChart" style="height:260px;" />
      </div>
      <el-empty v-if="!loading && !hasTrend" description="暂无跟进数据" :image-size="80" />
    </el-card>

    <el-card shadow="never" style="margin-top:16px" v-if="!loading && profile.businessTypes && profile.businessTypes.length">
      <div slot="header">业务类型覆盖</div>
      <el-tag
        v-for="bt in profile.businessTypes"
        :key="bt"
        size="medium"
        style="margin:4px"
      >{{ bt }}</el-tag>
    </el-card>
  </div>
</template>

<script>
import { getChannelProfile } from '@/api/sales/me/channel'
import * as echarts from 'echarts'

export default {
  name: 'SalesChannelProfile',
  data() {
    return {
      loading: false,
      channelId: null,
      profile: {},
      chart: null
    }
  },
  computed: {
    hasTrend() {
      return this.profile.activityTrend && this.profile.activityTrend.some(t => t.count > 0)
    }
  },
  created() {
    this.channelId = parseInt(this.$route.query.channelId || this.$route.params.id)
    if (this.channelId) {
      this.loadProfile()
    }
  },
  beforeDestroy() {
    if (this.chart) this.chart.dispose()
  },
  methods: {
    loadProfile() {
      this.loading = true
      getChannelProfile(this.channelId).then(res => {
        this.profile = res.data || {}
        this.$nextTick(() => this.renderChart())
      }).catch(() => {
        this.$message.error('加载渠道画像失败')
      }).finally(() => {
        this.loading = false
      })
    },
    renderChart() {
      if (!this.$refs.barChart || !this.profile.activityTrend) return
      if (!this.chart) {
        this.chart = echarts.init(this.$refs.barChart)
      }
      const months = this.profile.activityTrend.map(t => t.month)
      const counts = this.profile.activityTrend.map(t => t.count || 0)
      this.chart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '8%', containLabel: true },
        xAxis: { type: 'category', data: months },
        yAxis: { type: 'value', name: '跟进次数', minInterval: 1 },
        series: [{
          name: '跟进次数',
          type: 'bar',
          data: counts,
          itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] },
          label: { show: true, position: 'top', formatter: '{c}' }
        }]
      })
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.stat-card {
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  margin-bottom: 12px;
}
.stat-card__label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  color: #172033;
}
</style>
