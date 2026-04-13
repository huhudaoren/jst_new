<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">入驻管理</p>
        <h2>渠道认证申请</h2>
        <p class="hero-desc">查看渠道方认证申请记录，按申请编号、用户、审核状态筛选。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="申请编号" prop="applyNo">
        <el-input v-model="queryParams.applyNo" placeholder="请输入申请编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="审核状态" prop="applyStatus">
        <el-select v-model="queryParams.applyStatus" placeholder="全部" clearable>
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
        <div v-for="row in list" :key="row.applyId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.applyNo || '--' }}</div>
              <div class="mobile-sub">{{ row.applyName || '--' }} (用户 #{{ row.userId }})</div>
            </div>
            <el-tag size="small" :type="statusType(row.applyStatus)">{{ statusLabel(row.applyStatus) }}</el-tag>
          </div>
          <div class="mobile-info-row">
            <el-tag size="mini" type="info">{{ channelTypeLabel(row.channelType) }}</el-tag>
            <span>{{ parseTime(row.submitTime, '{y}-{m}-{d}') || '--' }}</span>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无认证申请" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="applyId" width="70" />
      <el-table-column label="申请编号" prop="applyNo" min-width="160" show-overflow-tooltip />
      <el-table-column label="用户ID" prop="userId" width="90" />
      <el-table-column label="申请人" prop="applyName" min-width="120" show-overflow-tooltip />
      <el-table-column label="渠道类型" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" type="info">{{ channelTypeLabel(scope.row.channelType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.applyStatus)">{{ statusLabel(scope.row.applyStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.submitTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="审核时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.auditTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '600px'" title="认证申请详情" append-to-body>
      <div v-if="detail" class="drawer-body">
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="申请ID">{{ detail.applyId }}</el-descriptions-item>
          <el-descriptions-item label="申请编号">{{ detail.applyNo }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
          <el-descriptions-item label="渠道ID">{{ detail.channelId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ detail.applyName }}</el-descriptions-item>
          <el-descriptions-item label="渠道类型"><el-tag size="small" type="info">{{ channelTypeLabel(detail.channelType) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="审核状态"><el-tag size="small" :type="statusType(detail.applyStatus)">{{ statusLabel(detail.applyStatus) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="审核备注">{{ detail.auditRemark || '--' }}</el-descriptions-item>
          <el-descriptions-item label="审核人ID">{{ detail.auditUserId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ parseTime(detail.submitTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">{{ parseTime(detail.auditTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detail.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="materials.length" style="margin-top: 16px">
          <h4 style="margin-bottom: 12px">认证材料</h4>
          <div v-for="(m, i) in materials" :key="i" class="material-item">
            <span>{{ m.name || m.type || '附件' + (i + 1) }}</span>
            <el-image v-if="isImage(m.url)" :src="m.url" style="max-width: 160px; margin-top: 4px" :preview-src-list="[m.url]" />
            <a v-else :href="m.url" target="_blank" class="material-link">查看文件</a>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_channel_auth_apply, getJst_channel_auth_apply } from '@/api/jst/organizer/jst_channel_auth_apply'

const STATUS_META = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已驳回', type: 'danger' }
}

export default {
  name: 'ChannelAuthApplyManage',
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, applyNo: undefined, userId: undefined, applyStatus: undefined },
      statusOptions: Object.entries(STATUS_META).map(([value, { label }]) => ({ value, label })),
      detailVisible: false,
      detail: null
    }
  },
  computed: {
    materials() {
      if (!this.detail || !this.detail.materialsJson) return []
      try { return JSON.parse(this.detail.materialsJson) } catch (_) { return [] }
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
        const res = await listJst_channel_auth_apply(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, applyNo: undefined, userId: undefined, applyStatus: undefined }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detail = null
      try {
        const res = await getJst_channel_auth_apply(row.applyId)
        this.detail = res.data
      } catch (e) {
        this.$modal.msgError('加载详情失败')
        this.detail = row
      }
    },
    statusLabel(s) { return (STATUS_META[s] && STATUS_META[s].label) || s || '--' },
    statusType(s) { return (STATUS_META[s] && STATUS_META[s].type) || 'info' },
    channelTypeLabel(t) { return { teacher: '教师', organization: '机构', individual: '个人' }[t] || t || '--' },
    isImage(url) { return url && /\.(jpg|jpeg|png|gif|webp|bmp)(\?|$)/i.test(url) }
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
.material-item { margin-bottom: 12px; padding: 8px; background: #f6f8fb; border-radius: 6px; }
.material-link { color: #2f6fec; margin-left: 8px; }
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
