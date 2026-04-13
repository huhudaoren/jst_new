<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">渠道财务</p>
        <h2>返点结算</h2>
        <p class="hero-desc">管理渠道返点提现结算单，查看申请金额、负向抵扣与实付金额。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="结算单号" prop="settlementNo">
        <el-input v-model="queryParams.settlementNo" placeholder="请输入结算单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="渠道ID" prop="channelId">
        <el-input v-model="queryParams.channelId" placeholder="请输入渠道ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.settlementId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.settlementNo || '--' }}</div>
              <div class="mobile-sub">渠道 {{ row.channelName || row.channelId || '--' }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-amounts">
            <div class="mobile-amount-item"><span class="mobile-amount-label">申请</span><span>{{ formatMoney(row.applyAmount) }}</span></div>
            <div class="mobile-amount-item"><span class="mobile-amount-label">抵扣</span><span class="amount-negative">{{ formatMoney(row.negativeOffsetAmount) }}</span></div>
            <div class="mobile-amount-item"><span class="mobile-amount-label">实付</span><span class="amount-brand">{{ formatMoney(row.actualPayAmount) }}</span></div>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无结算记录" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list" show-summary :summary-method="getSummary">
      <el-table-column label="ID" prop="settlementId" width="70" />
      <el-table-column label="结算单号" prop="settlementNo" min-width="160" show-overflow-tooltip />
      <el-table-column label="渠道" min-width="140" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.channelName || scope.row.channelId || '--' }}</template>
      </el-table-column>
      <el-table-column label="申请金额" prop="applyAmount" min-width="120" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.applyAmount) }}</template>
      </el-table-column>
      <el-table-column label="负向抵扣" prop="negativeOffsetAmount" min-width="120" align="right">
        <template slot-scope="scope"><span class="amount-negative">{{ formatMoney(scope.row.negativeOffsetAmount) }}</span></template>
      </el-table-column>
      <el-table-column label="实付金额" prop="actualPayAmount" min-width="120" align="right">
        <template slot-scope="scope"><strong class="amount-brand">{{ formatMoney(scope.row.actualPayAmount) }}</strong></template>
      </el-table-column>
      <el-table-column label="状态" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.applyTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '560px'" title="结算详情" append-to-body>
      <div v-if="detail" class="drawer-body">
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="结算ID">{{ detail.settlementId }}</el-descriptions-item>
          <el-descriptions-item label="结算单号">{{ detail.settlementNo }}</el-descriptions-item>
          <el-descriptions-item label="渠道">{{ detail.channelName || detail.channelId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="状态"><el-tag size="small" :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="申请金额">{{ formatMoney(detail.applyAmount) }}</el-descriptions-item>
          <el-descriptions-item label="负向抵扣"><span class="amount-negative">{{ formatMoney(detail.negativeOffsetAmount) }}</span></el-descriptions-item>
          <el-descriptions-item label="实付金额"><strong class="amount-brand">{{ formatMoney(detail.actualPayAmount) }}</strong></el-descriptions-item>
          <el-descriptions-item label="发票状态">{{ invoiceStatusLabel(detail.invoiceStatus) }}</el-descriptions-item>
          <el-descriptions-item label="银行账户快照">{{ detail.bankAccountSnapshot || '--' }}</el-descriptions-item>
          <el-descriptions-item label="审核备注">{{ detail.auditRemark || '--' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ parseTime(detail.applyTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="打款时间">{{ parseTime(detail.payTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.payVoucherUrl" label="打款凭证" :span="2">
            <el-image :src="detail.payVoucherUrl" style="max-width: 200px" :preview-src-list="[detail.payVoucherUrl]" />
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_rebate_settlement, getJst_rebate_settlement } from '@/api/jst/channel/jst_rebate_settlement'
import { formatMoney as formatMoneyUtil } from '@/utils/format'

const STATUS_META = {
  pending: { label: '待审核', type: 'info' },
  reviewing: { label: '审核中', type: 'warning' },
  rejected: { label: '已驳回', type: 'danger' },
  approved: { label: '已通过', type: 'success' },
  paid: { label: '已打款', type: 'success' }
}

export default {
  name: 'RebateSettlementManage',
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, settlementNo: undefined, channelId: undefined, status: undefined },
      statusOptions: Object.entries(STATUS_META).map(([value, { label }]) => ({ value, label })),
      detailVisible: false,
      detail: null
    }
  },
  created() {
    this.updateViewport()
    window.addEventListener('resize', this.updateViewport)
    this.getList()
  },
  beforeDestroy() { window.removeEventListener('resize', this.updateViewport) },
  methods: {
    updateViewport() { this.isMobile = window.innerWidth <= 768 },
    async getList() {
      this.loading = true
      try {
        const res = await listJst_rebate_settlement(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, settlementNo: undefined, channelId: undefined, status: undefined }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detail = null
      try {
        const res = await getJst_rebate_settlement(row.settlementId)
        this.detail = res.data
      } catch (e) {
        this.$modal.msgError('加载详情失败')
        this.detail = row
      }
    },
    getSummary({ columns, data }) {
      const sums = []
      columns.forEach((col, i) => {
        if (i === 0) { sums[i] = '合计'; return }
        if (['applyAmount', 'negativeOffsetAmount', 'actualPayAmount'].includes(col.property)) {
          const total = data.reduce((s, r) => s + Number(r[col.property] || 0), 0)
          sums[i] = '\u00a5' + total.toFixed(2)
        } else { sums[i] = '' }
      })
      return sums
    },
    statusLabel(status) { return (STATUS_META[status] && STATUS_META[status].label) || status || '--' },
    statusType(status) { return (STATUS_META[status] && STATUS_META[status].type) || 'info' },
    invoiceStatusLabel(s) { return { none: '无', pending: '待开', issued: '已开', voided: '已作废' }[s] || s || '--' },
    formatMoney(v) { return formatMoneyUtil(v) }
  }
}
</script>

<style scoped>
.enhanced-page { background: #f6f8fb; min-height: calc(100vh - 84px); }
.page-hero { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 24px; margin-bottom: 18px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.hero-eyebrow { margin: 0 0 8px; color: #2f6fec; font-size: 13px; font-weight: 600; }
.page-hero h2 { margin: 0; font-size: 24px; font-weight: 700; color: #172033; }
.hero-desc { margin: 8px 0 0; color: #6f7b8f; }
.query-panel { padding: 16px 16px 0; margin-bottom: 16px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.amount-negative { color: #f56c6c; font-weight: 600; }
.amount-brand { color: #2f6fec; }
.drawer-body { padding: 20px; }
.mobile-list { min-height: 180px; }
.mobile-card { padding: 16px; margin-bottom: 12px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.mobile-card-top { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.mobile-title { font-weight: 700; color: #172033; }
.mobile-sub { margin-top: 4px; font-size: 12px; color: #7a8495; }
.mobile-amounts { display: flex; gap: 16px; margin-top: 12px; padding: 10px 0; border-top: 1px dashed #e5eaf2; }
.mobile-amount-item { display: flex; flex-direction: column; gap: 4px; font-size: 15px; font-weight: 600; }
.mobile-amount-label { font-size: 12px; font-weight: 400; color: #7a8495; }
.mobile-actions { margin-top: 12px; border-top: 1px solid #f0f2f5; padding-top: 12px; }
@media (max-width: 768px) {
  .enhanced-page { padding: 12px; }
  .page-hero { display: block; padding: 18px; }
  .page-hero .el-button { width: 100%; min-height: 44px; margin-top: 16px; }
  .page-hero h2 { font-size: 20px; }
  .query-panel { padding-bottom: 8px; }
  .query-panel ::v-deep .el-form-item { display: block; margin-right: 0; }
  .query-panel ::v-deep .el-form-item__content, .query-panel ::v-deep .el-select, .query-panel ::v-deep .el-input { width: 100%; }
}
</style>
