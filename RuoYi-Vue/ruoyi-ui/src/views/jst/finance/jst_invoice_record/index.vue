<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">财务中心</p>
        <h2>发票记录</h2>
        <p class="hero-desc">管理平台发票，按发票号、开票抬头、状态筛选，查看开票与物流信息。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="发票号" prop="invoiceNo">
        <el-input v-model="queryParams.invoiceNo" placeholder="请输入发票号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="开票抬头" prop="invoiceTitle">
        <el-input v-model="queryParams.invoiceTitle" placeholder="请输入开票抬头" clearable @keyup.enter.native="handleQuery" />
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
        <div v-for="row in list" :key="row.invoiceId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.invoiceNo || '--' }}</div>
              <div class="mobile-sub">{{ row.invoiceTitle || '--' }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div style="margin-top:12px;font-size:20px;font-weight:700;color:#172033">{{ formatMoney(row.amount) }}</div>
          <div class="mobile-info-row">开票：{{ parseTime(row.issueTime, '{y}-{m}-{d}') || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无发票记录" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="invoiceId" width="70" />
      <el-table-column label="发票号" prop="invoiceNo" min-width="160" show-overflow-tooltip />
      <el-table-column label="开票抬头" prop="invoiceTitle" min-width="180" show-overflow-tooltip />
      <el-table-column label="税号" prop="taxNo" min-width="160" show-overflow-tooltip />
      <el-table-column label="金额" min-width="120" align="right">
        <template slot-scope="scope"><strong>{{ formatMoney(scope.row.amount) }}</strong></template>
      </el-table-column>
      <el-table-column label="状态" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="物流状态" min-width="100">
        <template slot-scope="scope">{{ scope.row.expressStatus || '--' }}</template>
      </el-table-column>
      <el-table-column label="开票时间" min-width="120">
        <template slot-scope="scope">{{ parseTime(scope.row.issueTime, '{y}-{m}-{d}') || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '560px'" title="发票详情" append-to-body>
      <div v-if="detail" class="drawer-body">
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="发票ID">{{ detail.invoiceId }}</el-descriptions-item>
          <el-descriptions-item label="发票号">{{ detail.invoiceNo }}</el-descriptions-item>
          <el-descriptions-item label="对象类型">{{ targetTypeLabel(detail.targetType) }}</el-descriptions-item>
          <el-descriptions-item label="对象ID">{{ detail.targetId }}</el-descriptions-item>
          <el-descriptions-item label="关联结算号">{{ detail.refSettlementNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="开票抬头">{{ detail.invoiceTitle }}</el-descriptions-item>
          <el-descriptions-item label="税号">{{ detail.taxNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="金额"><strong style="font-size:18px">{{ formatMoney(detail.amount) }}</strong></el-descriptions-item>
          <el-descriptions-item label="状态"><el-tag size="small" :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="物流状态">{{ detail.expressStatus || '--' }}</el-descriptions-item>
          <el-descriptions-item label="开票时间">{{ parseTime(detail.issueTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detail.remark || '--' }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.fileUrl" label="发票文件" :span="2">
            <a :href="detail.fileUrl" target="_blank" class="text-brand">查看发票文件</a>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_invoice_record, getJst_invoice_record } from '@/api/jst/finance/jst_invoice_record'
import { formatMoney as formatMoneyUtil } from '@/utils/format'

const STATUS_META = {
  pending_apply: { label: '待申请', type: 'info' },
  reviewing: { label: '审核中', type: 'warning' },
  issuing: { label: '开票中', type: 'warning' },
  issued: { label: '已开票', type: 'success' },
  voided: { label: '已作废', type: 'danger' },
  red_offset: { label: '红冲', type: 'danger' }
}

export default {
  name: 'InvoiceRecordManage',
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, invoiceNo: undefined, invoiceTitle: undefined, status: undefined },
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
        const res = await listJst_invoice_record(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, invoiceNo: undefined, invoiceTitle: undefined, status: undefined }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detail = null
      try {
        const res = await getJst_invoice_record(row.invoiceId)
        this.detail = res.data
      } catch (e) {
        this.$modal.msgError('加载详情失败')
        this.detail = row
      }
    },
    statusLabel(s) { return (STATUS_META[s] && STATUS_META[s].label) || s || '--' },
    statusType(s) { return (STATUS_META[s] && STATUS_META[s].type) || 'info' },
    targetTypeLabel(t) { return { channel: '渠道方', partner: '赛事方' }[t] || t || '--' },
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
.drawer-body { padding: 20px; }
.mobile-list { min-height: 180px; }
.mobile-card { padding: 16px; margin-bottom: 12px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.mobile-card-top { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.mobile-title { font-weight: 700; color: #172033; }
.mobile-sub { margin-top: 4px; font-size: 12px; color: #7a8495; }
.mobile-info-row { margin-top: 8px; font-size: 13px; color: #7a8495; }
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
