<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">用户管理</p>
        <h2>渠道账户</h2>
        <p class="hero-desc">查看渠道方档案，按渠道名、手机号、认证状态筛选，查看银行与认证信息。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="渠道名" prop="channelName">
        <el-input v-model="queryParams.channelName" placeholder="请输入渠道名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="联系手机" prop="contactMobile">
        <el-input v-model="queryParams.contactMobile" placeholder="请输入手机号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="认证状态" prop="authStatus">
        <el-select v-model="queryParams.authStatus" placeholder="全部" clearable>
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
        <div v-for="row in list" :key="row.channelId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.channelName || '--' }}</div>
              <div class="mobile-sub">{{ channelTypeLabel(row.channelType) }} / {{ row.contactMobile || '--' }}</div>
            </div>
            <el-tag size="small" :type="authStatusType(row.authStatus)">{{ authStatusLabel(row.authStatus) }}</el-tag>
          </div>
          <div class="mobile-info-row">
            <span>
              用户：
              <el-link v-if="row.userId && row.userName" type="primary" :underline="false" @click="goUser(row)">
                {{ row.userName }}
              </el-link>
              <span v-else>{{ row.userName || row.userId || '--' }}</span>
            </span>
            <el-tag v-if="row.riskFlag === 1" size="mini" type="danger">风险</el-tag>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无渠道账户" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="channelId" width="70" />
      <el-table-column label="用户" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link v-if="scope.row.userId && scope.row.userName" type="primary" :underline="false" @click="goUser(scope.row)">
            {{ scope.row.userName }}
          </el-link>
          <span v-else>{{ scope.row.userName || scope.row.userId || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="渠道类型" min-width="90">
        <template slot-scope="scope">
          <el-tag size="small" type="info">{{ channelTypeLabel(scope.row.channelType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="渠道名" prop="channelName" min-width="140" show-overflow-tooltip />
      <el-table-column label="联系手机" prop="contactMobile" min-width="120" />
      <el-table-column label="认证状态" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="authStatusType(scope.row.authStatus)">{{ authStatusLabel(scope.row.authStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="认证时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.authTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="风险" width="70" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.riskFlag === 1" size="mini" type="danger">风险</el-tag>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '600px'" title="渠道账户详情" append-to-body>
      <div v-if="detail" class="drawer-body">
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="渠道ID">{{ detail.channelId }}</el-descriptions-item>
          <el-descriptions-item label="用户">
            <el-link v-if="detail.userId && detail.userName" type="primary" :underline="false" @click="goUser(detail)">
              {{ detail.userName }}
            </el-link>
            <span v-else>{{ detail.userName || detail.userId || '--' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="渠道类型"><el-tag size="small" type="info">{{ channelTypeLabel(detail.channelType) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="渠道名">{{ detail.channelName }}</el-descriptions-item>
          <el-descriptions-item label="联系手机">{{ detail.contactMobile || '--' }}</el-descriptions-item>
          <el-descriptions-item label="认证状态"><el-tag size="small" :type="authStatusType(detail.authStatus)">{{ authStatusLabel(detail.authStatus) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="认证时间">{{ parseTime(detail.authTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="认证备注">{{ detail.authRemark || '--' }}</el-descriptions-item>
          <el-descriptions-item label="风险标记">{{ detail.riskFlag === 1 ? '风险' : detail.riskFlag === 2 ? '待审核' : '正常' }}</el-descriptions-item>
          <el-descriptions-item label="标签">{{ detail.tags || '--' }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin: 20px 0 12px">银行信息</h4>
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="开户名">{{ detail.bankAccountName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="银行账号">{{ detail.bankAccountNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="开户行">{{ detail.bankName || '--' }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="certMaterials.length" style="margin-top: 16px">
          <h4 style="margin-bottom: 12px">认证材料</h4>
          <div v-for="(m, i) in certMaterials" :key="i" class="material-item">
            <span>{{ m.name || m.type || '附件' + (i + 1) }}</span>
            <el-image v-if="isImage(m.url)" :src="m.url" style="max-width: 160px; margin-top: 4px" :preview-src-list="[m.url]" />
            <a v-else :href="m.url" target="_blank" style="color:#2f6fec;margin-left:8px">查看文件</a>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_channel, getJst_channel } from '@/api/jst/user/jst_channel'

const AUTH_STATUS_META = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '已认证', type: 'success' },
  rejected: { label: '已驳回', type: 'danger' },
  suspended: { label: '已冻结', type: 'info' }
}

export default {
  name: 'ChannelAccountManage',
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, channelName: undefined, contactMobile: undefined, authStatus: undefined },
      statusOptions: Object.entries(AUTH_STATUS_META).map(([value, { label }]) => ({ value, label })),
      detailVisible: false,
      detail: null
    }
  },
  computed: {
    certMaterials() {
      if (!this.detail || !this.detail.certMaterialsJson) return []
      try { return JSON.parse(this.detail.certMaterialsJson) } catch (_) { return [] }
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
        const res = await listJst_channel(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, channelName: undefined, contactMobile: undefined, authStatus: undefined }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detail = null
      try {
        const res = await getJst_channel(row.channelId)
        this.detail = res.data
      } catch (e) {
        this.$modal.msgError('加载详情失败')
        this.detail = row
      }
    },
    goUser(row) {
      const userId = row && row.userId
      if (!userId) return
      this.$router.push({
        path: '/jst/user',
        query: { userId: String(userId), autoOpen: '1' }
      }).catch(() => {})
    },
    authStatusLabel(s) { return (AUTH_STATUS_META[s] && AUTH_STATUS_META[s].label) || s || '--' },
    authStatusType(s) { return (AUTH_STATUS_META[s] && AUTH_STATUS_META[s].type) || 'info' },
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
