<template>
  <div class="app-container">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">销售主管</p>
        <h2>业绩明细</h2>
        <p class="hero-desc">查看团队销售提成台账与月结审核。</p>
      </div>
      <el-button type="primary" @click="goToAdminSettlement">前往月结管理</el-button>
    </div>

    <el-card shadow="never">
      <div slot="header">月结单列表</div>
      <div v-loading="loading">
        <el-table :data="settlementList" border stripe size="small">
          <el-table-column label="结算单号" prop="settlementNo" min-width="130" show-overflow-tooltip />
          <el-table-column label="销售姓名" prop="salesName" min-width="90" show-overflow-tooltip />
          <el-table-column label="结算周期" min-width="160" align="center">
            <template slot-scope="scope">
              {{ formatDate(scope.row.periodStart) }} ~ {{ formatDate(scope.row.periodEnd) }}
            </template>
          </el-table-column>
          <el-table-column label="总提成(元)" min-width="110" align="right">
            <template slot-scope="scope">¥{{ formatAmount(scope.row.totalAmount) }}</template>
          </el-table-column>
          <el-table-column label="状态" prop="status" width="90" align="center">
            <template slot-scope="scope">
              <el-tag size="mini" :type="settlementStatusType(scope.row.status)">
                {{ settlementStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loading && settlementList.length === 0" description="暂无月结数据" :image-size="80" />
      </div>
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="loadList"
      />
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'SalesManagerSettlement',
  data() {
    return {
      loading: false,
      settlementList: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 20 }
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    loadList() {
      this.loading = true
      request({
        url: '/admin/sales/settlement/list',
        method: 'get',
        params: this.queryParams
      }).then(res => {
        this.settlementList = res.rows || []
        this.total = res.total || 0
      }).catch(() => {
        this.$message.error('加载月结数据失败')
      }).finally(() => {
        this.loading = false
      })
    },
    goToAdminSettlement() {
      this.$router.push('/jst/sales/settlement').catch(() => {})
    },
    settlementStatusType(s) {
      const map = { pending: 'warning', approved: 'primary', paid: 'success', rejected: 'danger' }
      return map[s] || 'info'
    },
    settlementStatusLabel(s) {
      const map = { pending: '待审核', approved: '已批准', paid: '已打款', rejected: '已驳回' }
      return map[s] || s
    },
    formatDate(val) {
      if (!val) return '--'
      return String(val).substring(0, 10)
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
.hero-eyebrow { margin: 0; font-size: 12px; color: #409EFF; text-transform: uppercase; }
.page-hero h2 { margin: 4px 0; font-size: 22px; font-weight: 700; color: #172033; }
.hero-desc { margin: 4px 0 0; color: #6f7b8f; }
</style>
