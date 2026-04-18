<template>
  <div class="app-container">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">销售工作台</p>
        <h2>我的业绩</h2>
        <p class="hero-desc">查看本月或指定月份的销售业绩汇总。</p>
      </div>
    </div>

    <!-- 月份选择 -->
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" size="small">
        <el-form-item label="月份">
          <el-date-picker
            v-model="month"
            type="month"
            placeholder="不选则当月"
            value-format="yyyy-MM"
            style="width:160px"
            @change="loadPerformance"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" :loading="loading" @click="loadPerformance">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" v-loading="loading" class="stat-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">订单笔数</div>
          <div class="stat-card__value">{{ perf.orderCount != null ? perf.orderCount : '--' }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">覆盖渠道</div>
          <div class="stat-card__value">{{ perf.channelCount != null ? perf.channelCount : '--' }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">GMV</div>
          <div class="stat-card__value"><masked-amount :value="perf.totalGmv" /></div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">提成</div>
          <div class="stat-card__value"><masked-amount :value="perf.totalCommission" /></div>
        </div>
      </el-col>
    </el-row>

    <!-- 提成说明 banner -->
    <el-alert
      type="info"
      title="提成金额由公司 HR 与工资合并发放，页面展示金额仅供参考，以 HR 实发为准。"
      :closable="false"
      style="margin:16px 0"
    />

    <!-- 业务类型分布 -->
    <el-card shadow="never">
      <div slot="header">业务类型分布</div>
      <div v-loading="loading">
        <el-empty v-if="!loading && (!perf.byType || perf.byType.length === 0)" description="暂无分布数据" :image-size="80" />
        <el-table v-else :data="perf.byType || []" border stripe size="small">
          <el-table-column label="业务类型" prop="businessType" min-width="120" />
          <el-table-column label="订单数" prop="orderCount" width="90" align="center" />
          <el-table-column label="GMV" min-width="140" align="right">
            <template slot-scope="scope">
              <masked-amount :value="scope.row.gmv" />
            </template>
          </el-table-column>
          <el-table-column label="提成" min-width="140" align="right">
            <template slot-scope="scope">
              <masked-amount :value="scope.row.commission" />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getMyPerformance } from '@/api/sales/me/performance'
import MaskedAmount from '@/components/sales/MaskedAmount.vue'

export default {
  name: 'SalesMyPerformance',
  components: { MaskedAmount },
  data() {
    return {
      loading: false,
      month: '',
      perf: {}
    }
  },
  created() {
    this.loadPerformance()
  },
  methods: {
    loadPerformance() {
      this.loading = true
      getMyPerformance(this.month || undefined).then(res => {
        this.perf = res.data || {}
      }).catch(() => {}).finally(() => { this.loading = false })
    }
  }
}
</script>

<style scoped>
.page-hero {
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
</style>
