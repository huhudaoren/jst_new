<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">风控中心</p>
        <h2>风控告警</h2>
        <p class="hero-desc">查看风控预警记录</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="告警级别" prop="riskLevel">
        <el-select v-model="queryParams.riskLevel" placeholder="全部" clearable>
          <el-option v-for="item in dict.type.jst_risk_level" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="规则ID" prop="riskRuleId">
        <el-input v-model="queryParams.riskRuleId" placeholder="请输入规则ID" clearable @keyup.enter.native="handleQuery" />
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
        <div v-for="row in list" :key="row.alertId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">告警 #{{ row.alertId }}</div>
              <div class="mobile-sub">规则 #{{ row.riskRuleId || '--' }} / {{ row.targetType }} #{{ row.targetId }}</div>
            </div>
            <dict-tag :options="dict.type.jst_risk_level" :value="row.riskLevel" />
          </div>
          <div class="mobile-info-row">
            <el-tag size="mini" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
            <span>{{ parseTime(row.alertTime) || '--' }}</span>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无告警记录" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="alertId" width="70" />
      <el-table-column label="规则ID" prop="riskRuleId" width="80" />
      <el-table-column label="告警级别" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.jst_risk_level" :value="scope.row.riskLevel" />
        </template>
      </el-table-column>
      <el-table-column label="目标类型" prop="targetType" min-width="90" />
      <el-table-column label="目标ID" prop="targetId" width="80" />
      <el-table-column label="处理状态" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="关联工单" prop="caseId" width="90">
        <template slot-scope="scope">{{ scope.row.caseId || '--' }}</template>
      </el-table-column>
      <el-table-column label="告警时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.alertTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '600px'" title="告警详情" append-to-body>
      <div v-if="detail" class="drawer-body">
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="告警ID">{{ detail.alertId }}</el-descriptions-item>
          <el-descriptions-item label="规则ID">{{ detail.riskRuleId }}</el-descriptions-item>
          <el-descriptions-item label="告警级别">
            <dict-tag :options="dict.type.jst_risk_level" :value="detail.riskLevel" />
          </el-descriptions-item>
          <el-descriptions-item label="处理状态">
            <el-tag size="small" :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="目标类型">{{ detail.targetType }}</el-descriptions-item>
          <el-descriptions-item label="目标ID">{{ detail.targetId }}</el-descriptions-item>
          <el-descriptions-item label="关联工单">{{ detail.caseId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="告警时间">{{ parseTime(detail.alertTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="detail.hitDetailJson" style="margin-top: 16px">
          <h4 style="margin-bottom: 8px">命中详情</h4>
          <pre class="json-block">{{ formatJson(detail.hitDetailJson) }}</pre>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_risk_alert, getJst_risk_alert } from '@/api/jst/risk/jst_risk_alert'

const STATUS_META = {
  open: { label: '待处理', type: 'danger' },
  processing: { label: '处理中', type: 'warning' },
  closed: { label: '已关闭', type: 'success' },
  false_alarm: { label: '误报', type: 'info' }
}

export default {
  name: 'RiskAlertManage',
  dicts: ['jst_risk_level'],
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, riskLevel: undefined, riskRuleId: undefined, status: undefined },
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
        const res = await listJst_risk_alert(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, riskLevel: undefined, riskRuleId: undefined, status: undefined }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detail = null
      try {
        const res = await getJst_risk_alert(row.alertId)
        this.detail = res.data
      } catch (e) {
        this.$modal.msgError('加载详情失败')
        this.detail = row
      }
    },
    statusLabel(s) { return (STATUS_META[s] && STATUS_META[s].label) || s || '--' },
    statusType(s) { return (STATUS_META[s] && STATUS_META[s].type) || 'info' },
    formatJson(str) {
      try { return JSON.stringify(JSON.parse(str), null, 2) } catch (_) { return str }
    }
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
.json-block { background: #f6f8fb; border: 1px solid #e5eaf2; border-radius: 6px; padding: 12px; font-size: 13px; overflow-x: auto; white-space: pre-wrap; word-break: break-all; }
.mobile-list { min-height: 180px; }
.mobile-card { padding: 16px; margin-bottom: 12px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.mobile-card-top { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.mobile-title { font-weight: 700; color: #172033; }
.mobile-sub { margin-top: 4px; font-size: 12px; color: #7a8495; }
.mobile-info-row { margin-top: 8px; font-size: 13px; color: #7a8495; display: flex; align-items: center; gap: 8px; }
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
