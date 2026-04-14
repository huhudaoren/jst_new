<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">财务中心</p>
        <h2>合同管理</h2>
        <p class="hero-desc">管理赛事方与渠道合同</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="合同号" prop="contractNo">
        <el-input v-model="queryParams.contractNo" placeholder="请输入合同号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="对象类型" prop="targetType">
        <el-select v-model="queryParams.targetType" placeholder="全部" clearable>
          <el-option label="赛事方" value="partner" />
          <el-option label="渠道方" value="channel" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="item in dict.type.jst_contract_status" :key="item.value" :label="item.label" :value="item.value" />
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
        <div v-for="row in list" :key="row.contractId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.contractNo || '--' }}</div>
              <div class="mobile-sub">{{ contractTypeLabel(row.contractType) }} / {{ targetTypeLabel(row.targetType) }} #{{ row.targetId }}</div>
            </div>
            <dict-tag :options="dict.type.jst_contract_status" :value="row.status" />
          </div>
          <div class="mobile-info-row">有效期：{{ parseTime(row.effectiveTime, '{y}-{m}-{d}') || '--' }} 起</div>
          <div class="mobile-info-row">签署：{{ parseTime(row.signTime, '{y}-{m}-{d}') || '未签署' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无合同记录" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="contractId" width="70" />
      <el-table-column label="合同号" prop="contractNo" min-width="160" show-overflow-tooltip />
      <el-table-column label="合同类型" min-width="110">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.jst_contract_type" :value="scope.row.contractType" />
        </template>
      </el-table-column>
      <el-table-column label="对象类型" min-width="90">
        <template slot-scope="scope">{{ targetTypeLabel(scope.row.targetType) }}</template>
      </el-table-column>
      <el-table-column label="对象ID" prop="targetId" width="80" />
      <el-table-column label="状态" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.jst_contract_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="生效日期" min-width="120">
        <template slot-scope="scope">{{ parseTime(scope.row.effectiveTime, '{y}-{m}-{d}') || '--' }}</template>
      </el-table-column>
      <el-table-column label="签署日期" min-width="120">
        <template slot-scope="scope">{{ parseTime(scope.row.signTime, '{y}-{m}-{d}') || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '560px'" title="合同详情" append-to-body>
      <div v-if="detail" class="drawer-body">
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="合同ID">{{ detail.contractId }}</el-descriptions-item>
          <el-descriptions-item label="合同号">{{ detail.contractNo }}</el-descriptions-item>
          <el-descriptions-item label="合同类型"><dict-tag :options="dict.type.jst_contract_type" :value="detail.contractType" /></el-descriptions-item>
          <el-descriptions-item label="对象类型">{{ targetTypeLabel(detail.targetType) }}</el-descriptions-item>
          <el-descriptions-item label="对象ID">{{ detail.targetId }}</el-descriptions-item>
          <el-descriptions-item label="模板ID">{{ detail.templateId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="状态"><dict-tag :options="dict.type.jst_contract_status" :value="detail.status" /></el-descriptions-item>
          <el-descriptions-item label="关联结算ID">{{ detail.refSettlementId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="生效日期">{{ parseTime(detail.effectiveTime, '{y}-{m}-{d}') || '--' }}</el-descriptions-item>
          <el-descriptions-item label="签署日期">{{ parseTime(detail.signTime, '{y}-{m}-{d}') || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '--' }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.fileUrl" label="合同文件" :span="2">
            <a :href="detail.fileUrl" target="_blank" class="text-brand">查看合同文件</a>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_contract_record, getJst_contract_record } from '@/api/jst/finance/jst_contract_record'

export default {
  name: 'ContractRecordManage',
  dicts: ['jst_contract_type', 'jst_contract_status'],
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, contractNo: undefined, targetType: undefined, status: undefined },
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
        const res = await listJst_contract_record(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, contractNo: undefined, targetType: undefined, status: undefined }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detail = null
      try {
        const res = await getJst_contract_record(row.contractId)
        this.detail = res.data
      } catch (e) {
        this.$modal.msgError('加载详情失败')
        this.detail = row
      }
    },
    targetTypeLabel(t) { return { partner: '赛事方', channel: '渠道方' }[t] || t || '--' }
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
