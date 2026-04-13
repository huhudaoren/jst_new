<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">风控中心</p>
        <h2>审计日志</h2>
        <p class="hero-desc">查看平台操作审计日志，按操作人、类型、模块筛选。只读，不可编辑或删除。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="操作人" prop="operatorName">
        <el-input v-model="queryParams.operatorName" placeholder="请输入操作人姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="操作类型" prop="action">
        <el-select v-model="queryParams.action" placeholder="全部" clearable>
          <el-option v-for="a in actionOptions" :key="a.value" :label="a.label" :value="a.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="模块" prop="module">
        <el-input v-model="queryParams.module" placeholder="请输入模块名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.auditId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.operatorName || '--' }}</div>
              <div class="mobile-sub">{{ operatorTypeLabel(row.operatorType) }} / {{ row.module || '--' }}</div>
            </div>
            <el-tag size="mini" :type="row.result === 'success' ? 'success' : 'danger'">{{ row.result === 'success' ? '成功' : '失败' }}</el-tag>
          </div>
          <div class="mobile-info-row">
            <el-tag size="mini" type="info">{{ actionLabel(row.action) }}</el-tag>
            <span>{{ row.targetType }} #{{ row.targetId }}</span>
          </div>
          <div class="mobile-info-row">{{ parseTime(row.opTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无审计日志" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="auditId" width="70" />
      <el-table-column label="操作人" prop="operatorName" min-width="100" show-overflow-tooltip />
      <el-table-column label="角色" min-width="80">
        <template slot-scope="scope">
          <el-tag size="mini" type="info">{{ operatorTypeLabel(scope.row.operatorType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="模块" prop="module" min-width="100" show-overflow-tooltip />
      <el-table-column label="操作" min-width="90">
        <template slot-scope="scope">
          <el-tag size="mini">{{ actionLabel(scope.row.action) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="目标" min-width="130">
        <template slot-scope="scope">{{ scope.row.targetType }} #{{ scope.row.targetId }}</template>
      </el-table-column>
      <el-table-column label="结果" width="80" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.result === 'success' ? 'success' : 'danger'">{{ scope.row.result === 'success' ? '成功' : '失败' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="IP" prop="ip" width="130" show-overflow-tooltip />
      <el-table-column label="操作时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.opTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '680px'" title="审计日志详情" append-to-body>
      <div v-if="detail" class="drawer-body">
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="日志ID">{{ detail.auditId }}</el-descriptions-item>
          <el-descriptions-item label="操作人">{{ detail.operatorName }} (#{{ detail.operatorId }})</el-descriptions-item>
          <el-descriptions-item label="角色"><el-tag size="mini" type="info">{{ operatorTypeLabel(detail.operatorType) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="模块">{{ detail.module }}</el-descriptions-item>
          <el-descriptions-item label="操作"><el-tag size="mini">{{ actionLabel(detail.action) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="目标">{{ detail.targetType }} #{{ detail.targetId }}</el-descriptions-item>
          <el-descriptions-item label="结果"><el-tag size="mini" :type="detail.result === 'success' ? 'success' : 'danger'">{{ detail.result === 'success' ? '成功' : '失败' }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="终端">{{ detail.terminal || '--' }}</el-descriptions-item>
          <el-descriptions-item label="IP">{{ detail.ip || '--' }}</el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ parseTime(detail.opTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '--' }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="detail.beforeJson || detail.afterJson" style="margin-top: 16px">
          <h4 style="margin-bottom: 8px">变更对比</h4>
          <el-row :gutter="16">
            <el-col :span="12" :xs="24">
              <div class="diff-label">变更前</div>
              <pre class="json-block">{{ formatJson(detail.beforeJson) || '(无)' }}</pre>
            </el-col>
            <el-col :span="12" :xs="24">
              <div class="diff-label">变更后</div>
              <pre class="json-block">{{ formatJson(detail.afterJson) || '(无)' }}</pre>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_audit_log, getJst_audit_log } from '@/api/jst/risk/jst_audit_log'

const ACTION_OPTIONS = [
  { value: 'review', label: '审核' },
  { value: 'pay', label: '打款' },
  { value: 'adjust', label: '调整' },
  { value: 'refund', label: '退款' },
  { value: 'grant', label: '授权' },
  { value: 'revoke', label: '撤销' }
]

export default {
  name: 'AuditLogManage',
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, operatorName: undefined, action: undefined, module: undefined },
      actionOptions: ACTION_OPTIONS,
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
        const res = await listJst_audit_log(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, operatorName: undefined, action: undefined, module: undefined }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detail = null
      try {
        const res = await getJst_audit_log(row.auditId)
        this.detail = res.data
      } catch (e) {
        this.$modal.msgError('加载详情失败')
        this.detail = row
      }
    },
    operatorTypeLabel(t) { return { admin: '管理员', partner: '赛事方', channel: '渠道方', system: '系统' }[t] || t || '--' },
    actionLabel(a) { const found = ACTION_OPTIONS.find(o => o.value === a); return found ? found.label : a || '--' },
    formatJson(str) {
      if (!str) return null
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
.json-block { background: #f6f8fb; border: 1px solid #e5eaf2; border-radius: 6px; padding: 12px; font-size: 13px; overflow-x: auto; white-space: pre-wrap; word-break: break-all; max-height: 300px; }
.diff-label { font-weight: 600; font-size: 13px; margin-bottom: 6px; color: #172033; }
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
