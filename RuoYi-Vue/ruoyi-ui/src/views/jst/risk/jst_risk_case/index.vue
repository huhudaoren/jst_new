<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">风控中心</p>
        <h2>风控案例</h2>
        <p class="hero-desc">查看风控工单，按案例号、状态、处理人筛选，查看处理时间线。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="案例号" prop="caseNo">
        <el-input v-model="queryParams.caseNo" placeholder="请输入案例号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理人ID" prop="assigneeId">
        <el-input v-model="queryParams.assigneeId" placeholder="请输入处理人ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.caseId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.caseNo || '--' }}</div>
              <div class="mobile-sub">告警 #{{ row.alertId || '--' }} / 处理人 #{{ row.assigneeId || '--' }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div v-if="row.conclusion" class="mobile-info-row">{{ row.conclusion }}</div>
          <div class="mobile-info-row">{{ parseTime(row.createTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无风控案例" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="caseId" width="70" />
      <el-table-column label="案例号" prop="caseNo" min-width="160" show-overflow-tooltip />
      <el-table-column label="告警ID" prop="alertId" width="80" />
      <el-table-column label="处理人" prop="assigneeId" width="90" />
      <el-table-column label="状态" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="结论" prop="conclusion" min-width="200" show-overflow-tooltip />
      <el-table-column label="审核人" prop="reviewerId" width="80" />
      <el-table-column label="关闭时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.closeTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '600px'" title="案例详情" append-to-body>
      <div v-if="detail" class="drawer-body">
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="案例ID">{{ detail.caseId }}</el-descriptions-item>
          <el-descriptions-item label="案例号">{{ detail.caseNo }}</el-descriptions-item>
          <el-descriptions-item label="告警ID">{{ detail.alertId }}</el-descriptions-item>
          <el-descriptions-item label="处理人ID">{{ detail.assigneeId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="状态"><el-tag size="small" :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="审核人ID">{{ detail.reviewerId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="结论" :span="2">{{ detail.conclusion || '--' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(detail.createTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="关闭时间">{{ parseTime(detail.closeTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '--' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 时间线 -->
        <h4 style="margin: 20px 0 12px">处理时间线</h4>
        <el-timeline>
          <el-timeline-item v-for="(item, i) in timeline" :key="i" :timestamp="item.time" :type="item.type" placement="top">
            {{ item.label }}
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_risk_case, getJst_risk_case } from '@/api/jst/risk/jst_risk_case'

const STATUS_META = {
  open: { label: '待处理', type: 'danger' },
  assigned: { label: '已分配', type: 'warning' },
  processing: { label: '处理中', type: 'warning' },
  reviewing: { label: '审核中', type: '' },
  closed: { label: '已关闭', type: 'success' }
}

export default {
  name: 'RiskCaseManage',
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, caseNo: undefined, status: undefined, assigneeId: undefined },
      statusOptions: Object.entries(STATUS_META).map(([value, { label }]) => ({ value, label })),
      detailVisible: false,
      detail: null
    }
  },
  computed: {
    timeline() {
      if (!this.detail) return []
      const items = []
      if (this.detail.createTime) items.push({ label: '工单创建', time: this.parseTime(this.detail.createTime), type: 'primary' })
      if (this.detail.assigneeId) items.push({ label: '分配给处理人 #' + this.detail.assigneeId, time: '', type: 'warning' })
      if (this.detail.status === 'processing' || this.detail.status === 'reviewing' || this.detail.status === 'closed') {
        items.push({ label: '开始处理', time: '', type: 'warning' })
      }
      if (this.detail.status === 'reviewing' || this.detail.status === 'closed') {
        items.push({ label: '提交审核' + (this.detail.reviewerId ? '（审核人 #' + this.detail.reviewerId + '）' : ''), time: '', type: '' })
      }
      if (this.detail.status === 'closed') {
        items.push({ label: '工单关闭', time: this.parseTime(this.detail.closeTime) || '', type: 'success' })
      }
      return items
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
        const res = await listJst_risk_case(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, caseNo: undefined, status: undefined, assigneeId: undefined }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detail = null
      try {
        const res = await getJst_risk_case(row.caseId)
        this.detail = res.data
      } catch (e) {
        this.$modal.msgError('加载详情失败')
        this.detail = row
      }
    },
    statusLabel(s) { return (STATUS_META[s] && STATUS_META[s].label) || s || '--' },
    statusType(s) { return (STATUS_META[s] && STATUS_META[s].type) || 'info' }
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
